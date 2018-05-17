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
import java.util.concurrent.ExecutionException;

public class SensorHandler implements SensorEventListener {
    private static final String TAG = "SensorHandler";
    private static final int REQUEST_PERMISSION_CODE_BODY_SENSOR = 0;

    private Context context;
    MessageHandler messageHandler;

    private SensorManager mSensorManager;
    private Sensor mHeartRateSensor;
    private Sensor mAccelerometerSensor;
    private Sensor mGyroscopeSensor;

    public SensorHandler(Context context) {
        this.context = context;

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
        String msg = "";
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
                msg += "Gyroscope : " + (float) event.values[0] + " " + (float) event.values[1] + " " + (float) event.values[2];Log.d(TAG, "msg: " + msg);
                break;
        }

        //Log.d(TAG, "msg: " + msg);

        if (msg.length() > 0) {
            String separator = System.getProperty("line.separator");
            writeToFile(currentTimeStr() + " " + msg + separator);
            //Log.d(TAG, "Write sensor data to file");

            messageHandler.requestSendData((currentTimeStr() + " " + msg + separator).getBytes());
            //Log.d(TAG, "Request sending data");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d(TAG, "onAccuracyChanged - accuracy: " + accuracy);
    }

    public void startMeasure() {
        boolean heartRateRegistered = mSensorManager.registerListener(this, mHeartRateSensor, SensorManager.SENSOR_DELAY_FASTEST);
        boolean accelerometerRegistered = mSensorManager.registerListener(this, mAccelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        boolean gyroscopeRegistered = mSensorManager.registerListener(this, mGyroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL);
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

    private void writeToFile(String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("sensor.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private String readFromFile() {
        Log.d(TAG, "Start read file");
        String ret = "";

        try {
            InputStream inputStream = context.openFileInput("sensor.txt");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    Log.d(TAG, "receiveString : " + receiveString);
                    //stringBuilder.append(receiveString);
                    //stringBuilder.append("\n");
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e(TAG, "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e(TAG, "Can not read file: " + e.toString());
        }

        Log.d(TAG, "End read file");
        return ret;
    }
}