package com.example.shashwatgupta.myapplication;

import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Vibrator;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.apache.commons.collections4.queue.CircularFifoQueue;

import mr.go.sgfilter.SGFilter;
import mr.go.sgfilter.ZeroEliminator;

public class MainActivity extends Activity implements SensorEventListener {

    private float lastX, lastY, lastZ;

    private SensorManager sensorManager;
    private Sensor accelerometer;

    private float deltaXMax = 0;
    private float deltaYMax = 0;
    private float deltaZMax = 0;

    private float deltaX = 0;
    private float deltaY = 0;
    private float deltaZ = 0;

    private float vibrateThreshold = 0;

    private TextView currentX, currentY, currentZ, maxX, maxY, maxZ;

    public Vibrator v;

    private LineGraphSeries<DataPoint> seriesX, seriesY, seriesZ, seriesAll, seriesFil;
    private CircularFifoQueue<Double> queue;
    private final int maxData = 200;
    private final int nl = 5;
    private final int nr = 5;
    private final int degree = 3;
    private int counter = 0;
    private SGFilter sgFilter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeViews();

        GraphView graph = (GraphView) findViewById(R.id.graph);
        GraphView graph2 = (GraphView) findViewById(R.id.graph2);
        queue = new CircularFifoQueue<>(nl+nr+1);

        seriesX = new LineGraphSeries<>();
        seriesY = new LineGraphSeries<>();
        seriesZ = new LineGraphSeries<>();
        seriesAll = new LineGraphSeries<>();
        seriesFil = new LineGraphSeries<>();

        seriesX.appendData(new DataPoint(0, 0), true, maxData);
        seriesY.appendData(new DataPoint(0, 0), true, maxData);
        seriesZ.appendData(new DataPoint(0, 0), true, maxData);
        seriesAll.appendData(new DataPoint(0, 0), true, maxData);
        seriesFil.appendData(new DataPoint(0, 0), true, maxData);

        seriesX.setColor(Color.RED);
        seriesY.setColor(Color.BLUE);
        seriesZ.setColor(Color.BLACK);
        seriesAll.setColor(Color.RED);
        seriesAll.setThickness(3);
        seriesFil.setColor(Color.BLUE);

        seriesX.setTitle("X-Axis");
        seriesY.setTitle("Y-Axis");
        seriesZ.setTitle("Z-Axis");
        seriesAll.setTitle("Combined");
        seriesFil.setTitle("Filtered");

        graph.setTitle("Accelerometer Data");
        graph.setTitleTextSize(50);
        graph.addSeries(seriesX);
        graph.addSeries(seriesY);
        graph.addSeries(seriesZ);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(maxData);
        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setBackgroundColor(Color.YELLOW);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
        graph.getGridLabelRenderer().setHorizontalAxisTitle("Samples");
        graph.getGridLabelRenderer().setHorizontalAxisTitleColor(Color.BLUE);
        graph.getGridLabelRenderer().setHorizontalAxisTitleTextSize(40);
        graph.getGridLabelRenderer().setVerticalAxisTitle("Acceleration");
        graph.getGridLabelRenderer().setVerticalAxisTitleColor(Color.BLUE);
        graph.getGridLabelRenderer().setVerticalAxisTitleTextSize(40);

        graph2.setTitle("Combined and Filtered");
        graph2.setTitleTextSize(50);
        graph2.addSeries(seriesAll);
        graph2.addSeries(seriesFil);
        graph2.getViewport().setXAxisBoundsManual(true);
        graph2.getViewport().setMinX(0);
        graph2.getViewport().setMaxX(maxData);
        graph2.getLegendRenderer().setVisible(true);
        graph2.getLegendRenderer().setBackgroundColor(Color.YELLOW);
        graph2.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
        graph2.getGridLabelRenderer().setHorizontalAxisTitle("Samples");
        graph2.getGridLabelRenderer().setHorizontalAxisTitleColor(Color.BLUE);
        graph2.getGridLabelRenderer().setHorizontalAxisTitleTextSize(40);
        graph2.getGridLabelRenderer().setVerticalAxisTitle("Acceleration");
        graph2.getGridLabelRenderer().setVerticalAxisTitleColor(Color.BLUE);
        graph2.getGridLabelRenderer().setVerticalAxisTitleTextSize(40);

        sgFilter = new SGFilter(nl, nr);
        System.out.println(degree);
        sgFilter.appendPreprocessor(new ZeroEliminator());

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            // success! we have an accelerometer

            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            vibrateThreshold = accelerometer.getMaximumRange() / 2;
        } else {
            // fai! we dont have an accelerometer!
        }

        //initialize vibration
        v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);

    }

    public void initializeViews() {
        currentX = (TextView) findViewById(R.id.currentX);
        currentY = (TextView) findViewById(R.id.currentY);
        currentZ = (TextView) findViewById(R.id.currentZ);

        maxX = (TextView) findViewById(R.id.maxX);
        maxY = (TextView) findViewById(R.id.maxY);
        maxZ = (TextView) findViewById(R.id.maxZ);
    }

    //onResume() register the accelerometer for listening the events
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    //onPause() unregister the accelerometer for stop listening the events
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        // clean current values
       // displayCleanValues();
        // display the current x,y,z accelerometer values
        displayCurrentValues();
        // display the max x,y,z accelerometer values
        displayMaxValues();

        // get the change of the x,y,z values of the accelerometer
        deltaX = Math.abs(lastX - event.values[0]);
        deltaY = Math.abs(lastY - event.values[1]);
        deltaZ = Math.abs(lastZ - event.values[2]);

        // if the change is below 2, it is just plain noise
        if (deltaX < 2)
            deltaX = 0;
        if (deltaY < 2)
            deltaY = 0;
        if (deltaZ < 2)
            deltaZ = 0;

        // set the last know values of x,y,z
        lastX = event.values[0];
        lastY = event.values[1];
        lastZ = event.values[2];
        counter++;

        seriesX.appendData(new DataPoint(counter, lastX), true, maxData);
        seriesY.appendData(new DataPoint(counter, lastY), true, maxData);
        seriesZ.appendData(new DataPoint(counter, lastZ), true, maxData);

        queue.add(Math.sqrt(Math.sqrt(lastX*lastX+lastY*lastY+lastZ*lastZ)));

        Double[] seriesData = queue.toArray(new Double[queue.size()]);
        double[] seriesDataDouble = new double[queue.size()];
        for (int i = 0; i < queue.size(); i++)
            seriesDataDouble[i] = seriesData[i];
        double[] smooth = sgFilter.smooth(seriesDataDouble, SGFilter.computeSGCoefficients(nl, nr, degree));
        if(queue.size()>nl+nr) {
            seriesFil.appendData(new DataPoint(counter, smooth[nl]), true, maxData);
            seriesAll.appendData(new DataPoint(counter, queue.get(nl)), true, maxData);
        }
        vibrate();

    }

    // if the change in the accelerometer value is big enough, then vibrate!
    // our threshold is MaxValue/2
    public void vibrate() {
        if ((deltaX > vibrateThreshold) || (deltaY > vibrateThreshold) || (deltaZ > vibrateThreshold)) {
            v.vibrate(50);
        }
    }

    public void displayCleanValues() {
        currentX.setText("0.0");
        currentY.setText("0.0");
        currentZ.setText("0.0");
    }

    // display the current x,y,z accelerometer values
    public void displayCurrentValues() {
        currentX.setText(Float.toString(lastX));
        currentY.setText(Float.toString(lastY));
        currentZ.setText(Float.toString(lastZ));
    }

    // display the max x,y,z accelerometer values
    public void displayMaxValues() {
        if (deltaX > deltaXMax) {
            deltaXMax = deltaX;
            maxX.setText(Float.toString(deltaXMax));
        }
        if (deltaY > deltaYMax) {
            deltaYMax = deltaY;
            maxY.setText(Float.toString(deltaYMax));
        }
        if (deltaZ > deltaZMax) {
            deltaZMax = deltaZ;
            maxZ.setText(Float.toString(deltaZMax));
        }
    }
}
