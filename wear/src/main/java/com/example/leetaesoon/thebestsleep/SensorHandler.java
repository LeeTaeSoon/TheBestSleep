package com.example.leetaesoon.thebestsleep;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutionException;

public class SensorHandler implements SensorEventListener {
    private static final String TAG = "SensorHandler";
    private static final int REQUEST_PERMISSION_CODE_BODY_SENSOR = 0;

    private Context context;
    MessageHandler messageHandler;
    InnerStorageHandler innerStorageHandler;

    private SensorManager mSensorManager;
    private Sensor mHeartRateSensor;
    private Sensor mAccelerometerSensor;
    private Sensor mGyroscopeSensor;

    Queue<String> queue = new LinkedList<String>();

    public SensorHandler(Context context) {
        this.context = context;

        innerStorageHandler = new InnerStorageHandler(context);

        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mHeartRateSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        mAccelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mGyroscopeSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        try {
            Log.d(TAG, "Create messageHandler object");
            messageHandler = new MessageHandler(context);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // 신뢰성없는 값은 무시
        if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) return;

        String msg = "";
        String separator = System.getProperty("line.separator");

        switch (event.sensor.getType()) {
            case Sensor.TYPE_HEART_RATE:
                msg += "HeartRate : " + (int) event.values[0];
                break;
            case Sensor.TYPE_ACCELEROMETER:
                float gX = event.values[0] / SensorManager.GRAVITY_EARTH;
                float gY = event.values[1] / SensorManager.GRAVITY_EARTH;
                float gZ = event.values[2] / SensorManager.GRAVITY_EARTH;

                msg += "Accelerometer : " + gX + " " + gY + " " + gZ;
                break;
            case Sensor.TYPE_GYROSCOPE:
                msg += "Gyroscope : " + (float) event.values[0] + " " + (float) event.values[1] + " " + (float) event.values[2];
                break;
        }

        if (msg.length() > 0) {
            msg = currentTimeStr() + " " + msg + separator;
            queue.offer(msg);

            //Log.d(TAG, "msg: " + msg);
            //Log.d(TAG, "Queue size is " + queue.size());
            //innerStorageHandler.writeFile("sensor.txt", currentTimeStr() + " " + msg + separator, context.MODE_APPEND);
            //Log.d(TAG, "Write sensor data to file");

            //messageHandler.requestSendData(msg.getBytes());
            //Log.d(TAG, "Request sending data");
        }

        if (queue.size() == 1000) {
            Log.d(TAG, "Transfer queue datas");
            transferData();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d(TAG, "onAccuracyChanged - accuracy: " + accuracy);
    }

    public void startMeasure() {
        boolean heartRateRegistered = mSensorManager.registerListener(this, mHeartRateSensor, SensorManager.SENSOR_DELAY_FASTEST);
        boolean accelerometerRegistered = mSensorManager.registerListener(this, mAccelerometerSensor, 1000000, 1000000);
        boolean gyroscopeRegistered = mSensorManager.registerListener(this, mGyroscopeSensor, 1000000, 1000000);
        Log.d("Sensor Status:", " Heart Rate registered: " + (heartRateRegistered ? "yes" : "no"));
        Log.d("Sensor Status:", " Accelerometer registered: " + (accelerometerRegistered ? "yes" : "no"));
        Log.d("Sensor Status:", " Gyroscope registered: " + (gyroscopeRegistered ? "yes" : "no"));
    }

    public void stopMeasure() {
        mSensorManager.unregisterListener(this);
    }

    private String currentTimeStr() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        return df.format(c.getTime());
    }

    private void transferData() {
        Log.d(TAG, currentTimeStr() + "Transfer " + queue.size() + " datas");

        String data = "";

        while (!queue.isEmpty()) data += queue.poll();

        messageHandler.requestSendData(data.getBytes());
    }
}