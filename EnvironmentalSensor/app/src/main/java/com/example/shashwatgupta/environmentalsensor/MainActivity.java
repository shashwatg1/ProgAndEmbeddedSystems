package com.example.shashwatgupta.environmentalsensor;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private int on = 0;
    Button switch1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        switch1 = (Button) findViewById(R.id.switch1);

        switch1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchActivity();
            }
        });
    }

    private void launchActivity() {
        Intent intent = new Intent(this, graphActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(on==1) return;
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if(sensorManager != null && sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
        }
        else {
            Toast.makeText(getApplicationContext(), "Accelerometer Error", Toast.LENGTH_LONG).show();
            System.exit(1);
        }
        if(sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null) {
            Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
        }
        else {
            Toast.makeText(getApplicationContext(), "Gyroscope Error", Toast.LENGTH_LONG).show();
            System.exit(1);
        }
        if(sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null) {
            Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
        }
        else {
            Toast.makeText(getApplicationContext(), "Magnetometer Error", Toast.LENGTH_LONG).show();
            System.exit(1);
        }
    }

    @Override
    protected void onPause() {
        if(on==1) {
            super.onPause();
            return;
        }
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if(sensorManager != null && sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.unregisterListener(this, sensor);
        }
        else {
            Toast.makeText(getApplicationContext(), "Accelerometer Error", Toast.LENGTH_LONG).show();
            System.exit(1);
        }
        if(sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null) {
            Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            sensorManager.unregisterListener(this, sensor);
        }
        else {
            Toast.makeText(getApplicationContext(), "Gyroscope Error", Toast.LENGTH_LONG).show();
            System.exit(1);
        }
        if(sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null) {
            Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            sensorManager.unregisterListener(this, sensor);
        }
        else {
            Toast.makeText(getApplicationContext(), "Magnetometer Error", Toast.LENGTH_LONG).show();
            System.exit(1);
        }
        super.onPause();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent==null) return;
        if(sensorEvent.sensor.getType()==Sensor.TYPE_ACCELEROMETER) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];
            TextView xtv = (TextView) findViewById(R.id.ax);
            TextView ytv = (TextView) findViewById(R.id.ay);
            TextView ztv = (TextView) findViewById(R.id.az);
            xtv.setText(String.format(Locale.getDefault(), "%.9f", x));
            ytv.setText(String.format(Locale.getDefault(), "%.9f", y));
            ztv.setText(String.format(Locale.getDefault(), "%.9f", z));
        }
        if(sensorEvent.sensor.getType()== Sensor.TYPE_GYROSCOPE) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];
            TextView xtv = (TextView) findViewById(R.id.gx);
            TextView ytv = (TextView) findViewById(R.id.gy);
            TextView ztv = (TextView) findViewById(R.id.gz);
            xtv.setText(String.format(Locale.getDefault(), "%.9f", x));
            ytv.setText(String.format(Locale.getDefault(), "%.9f", y));
            ztv.setText(String.format(Locale.getDefault(), "%.9f", z));
        }
        if(sensorEvent.sensor.getType()==Sensor.TYPE_MAGNETIC_FIELD) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];
            TextView xtv = (TextView) findViewById(R.id.mx);
            TextView ytv = (TextView) findViewById(R.id.my);
            TextView ztv = (TextView) findViewById(R.id.mz);
            xtv.setText(String.format(Locale.getDefault(), "%.9f", x));
            ytv.setText(String.format(Locale.getDefault(), "%.9f", y));
            ztv.setText(String.format(Locale.getDefault(), "%.9f", z));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }
}
