package com.example.shashwatgupta.environmentalsensor;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mSensor;
    private TextView LI, Accel, Gyro, Magno;

    private final String TAG = "GraphSensors";
    private GraphView mGraphGyro;
    private GraphView mGraphAccel;
    private GraphView mGraphLI;
    private GraphView mGraphMag;
    private LineGraphSeries<DataPoint> mSeriesGyro;
    private LineGraphSeries<DataPoint> mSeriesAccel;
    private LineGraphSeries<DataPoint> mSeriesMag;
    private LineGraphSeries<DataPoint> mSeriesLI;
    private double graphLastGyroXValue = 5d;
    private double graphLastAccelXValue = 5d;
    private double graphLastLIXValue = 5d;
    private double graphLastMagXValue = 5d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LI = (TextView)findViewById(R.id.light);
        Accel = (TextView)findViewById(R.id.accel);
        Gyro = (TextView)findViewById(R.id.gyro);
        Magno = (TextView)findViewById(R.id.magne);
        startLI();
        startAccel();
        startGyro();
        startMag();
        mGraphGyro = initGraph(R.id.graphGyro, "Sensor.TYPE_GYROSCOPE");
        mGraphAccel = initGraph(R.id.graphAccel, "Sensor.TYPE_ACCELEROMETER");
        mGraphLI = initGraph(R.id.graphLI, "Sensor.TYPE_LIGHT");
        mGraphMag = initGraph(R.id.graphMag, "Sensor.TYPE_MAGNETIC_FIELD");

        mSeriesGyro = initSeries(Color.DKGRAY, "Gyro");
        mGraphGyro.addSeries(mSeriesGyro);
        startGyro();

        // ACCEL
        mSeriesAccel = initSeries(Color.CYAN, "Accel");
        mGraphAccel.addSeries(mSeriesAccel);
        startAccel();

        // LI
        mSeriesLI = initSeries(Color.RED, "Light Int");
        mGraphLI.addSeries(mSeriesLI);
        startLI();

        // Magnatic
        mSeriesMag = initSeries(Color.BLUE, "Magne");
        mGraphMag.addSeries(mSeriesMag);
        startMag();
    }

    public GraphView initGraph(int id, String title) {
        GraphView graph = (GraphView) findViewById(id);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(5);
        graph.getGridLabelRenderer().setLabelVerticalWidth(100);
        graph.setTitle(title);
        graph.getGridLabelRenderer().setHorizontalLabelsVisible(false);
        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
        return graph;
    }

    public LineGraphSeries<DataPoint> initSeries(int color, String title) {
        LineGraphSeries<DataPoint> series;
        series = new LineGraphSeries<>();
        series.setDrawDataPoints(true);
        series.setDrawBackground(false);
        series.setColor(color);
        series.setTitle(title);
        return series;
    }

    public void startLI(){
        mSensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);}

    public void startGyro(){
        mSensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void startAccel(){
        mSensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void startMag(){
        mSensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //event.values[x,y,z]
        if(event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            graphLastGyroXValue += 0.15d;
            double gx,gy,gz, gyro;
            gx = event.values[0];
            gy = event.values[1];
            gz = event.values[2];
            gyro = Math.sqrt(gx * gx + gy * gy + gz * gz);
            Gyro.setText("Gyroscope: " + gyro);
            mSeriesGyro.appendData(new DataPoint(graphLastGyroXValue, gyro), true, 33);

        } else if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            graphLastAccelXValue += 0.15d;
            double ax,ay,az, accel;
            ax = event.values[0];
            ay = event.values[1];
            az = event.values[2];
            accel = Math.sqrt(ax * ax + ay * ay + az * az);
            Accel.setText("Acceleration: " + accel);
            mSeriesAccel.appendData(new DataPoint(graphLastAccelXValue, accel), true, 33);
        }
        else if(event.sensor.getType() == Sensor.TYPE_LIGHT) {
            LI.setText("Temperature: " + event.values[0]);
            graphLastLIXValue += 0.15d;
            mSeriesLI.appendData(new DataPoint(graphLastLIXValue, event.values[0]), true, 33);

        } else if(event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            graphLastMagXValue += 0.15d;
            double mx,my,mz, magne;
            mx = event.values[0];
            my = event.values[1];
            mz = event.values[2];

            magne = Math.sqrt(mx * mx + my * my + mz * mz);
            Magno.setText("Mangntometer: " + magne);
            mSeriesMag.appendData(new DataPoint(graphLastMagXValue,magne ), true, 33);
        }
        String dataString = String.valueOf(event.accuracy) + "," + String.valueOf(event.timestamp) + "," + String.valueOf(event.sensor.getType()) + "\n";
        Log.d(TAG, "Data received: " + dataString);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        Log.d(TAG, "onAccuracyChanged called for the gyro");
    }

}
