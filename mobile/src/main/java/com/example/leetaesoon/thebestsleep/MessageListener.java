package com.example.leetaesoon.thebestsleep;

import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MessageListener extends WearableListenerService {
    final static String TAG = "MessageListener";
    long pre = 0;

    @Override
    public void onMessageReceived(final MessageEvent messageEvent) {
        String data = new String(messageEvent.getData());
        //showToast(data);
        ExternalStorageHandler externalStorageHandler = new ExternalStorageHandler();
        externalStorageHandler.writeFile("sensor.txt", data + "\n");

        InnerStorageHandler innerStorageHandler = new InnerStorageHandler(this);
        String preString = innerStorageHandler.readFile("service_pre_time.txt");
        //Log.d(TAG, "preString : " + preString);
        if (preString.trim().length() > 0) pre = Long.parseLong(preString);
        //Log.d(TAG, "read pre : " + pre);

        DBHandler dbHandler = new DBHandler(getApplicationContext(), DBHandler.DATABASE_NAME, null, 1);
        ArrayList<HeartRate> heartRates = new ArrayList<>();
        ArrayList<Acceleration> accelerometers = new ArrayList<>();

        String[] datas = data.split("\n");

        for (int i = 0; i < datas.length; i++) {
            //Log.d("MessageListener", "datas[" + i + "] : " + datas[i]);
            String[] arr = datas[i].split(" ");

            if(arr.length > 1) {
                long time = Long.parseLong(arr[0]);
                long diff = (time - pre) / 60000L;
                //Log.d(TAG, "pre : " + pre + ", time : " + time);
                //Log.d(TAG, "diff : " + diff + " min");
                if (diff > 30) pre = time;           // first time
                else if (diff > 5) {
                    if (isSleep(time)) turnOffDevices(time);
                    pre = time;
                }

                switch (arr[1]) {
                    case "HeartRate":
                        int rate = Integer.parseInt(arr[3]);
                        //Log.d("MessageListener", "heart rate : " + rate);
                        HeartRate heartRate = new HeartRate(rate, time);
                        heartRates.add(heartRate);
                        break;

                    case "Accelerometer":
                        float x = Float.parseFloat(arr[3]);
                        float y = Float.parseFloat(arr[4]);
                        float z = Float.parseFloat(arr[5]);
                        //Log.d("MessageListener", "accelerometer : " + x + " " + y + " " + z);
                        double sum = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
                        Acceleration acc = new Acceleration(time, x, y, z, sum);
                        accelerometers.add(acc);
                        break;
                }
            }
        }

        //Log.d("MessageListener", "Start save datas in db");
        dbHandler.addAllHeartRate(heartRates);
        dbHandler.addAllAcceleration(accelerometers);
        dbHandler.close();
        //Log.d("MessageListener", "message : " + data);

        innerStorageHandler.writeFile("service_pre_time.txt", String.valueOf(pre), MODE_PRIVATE );

        super.onMessageReceived(messageEvent);

        //Looper.loop();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private boolean isSleep(long time) {
        Log.d(TAG, "called isSleep");
        long windowStart = time - 1800000;      // 30 min before
        if (getAvgAcc(windowStart, time) < 0.002 && getAvgHeartRate(windowStart, time) < 62) return true;
        else return false;
    }

    private double getAvgAcc(long start, long end) {
        DBHandler dbHandler = new DBHandler(getApplicationContext(), DBHandler.DATABASE_NAME, null, 1);
        double d = dbHandler.getAccelerationDBAvg(start, end);
        Log.d(TAG, "avg acc : " + d);
        return d;
    }

    private int getAvgHeartRate(long start, long end) {
        DBHandler dbHandler = new DBHandler(getApplicationContext(), DBHandler.DATABASE_NAME, null, 1);
        int r = dbHandler.getHeartRateDBAvg(start, end);
        Log.d(TAG, "avg heart : " + r);
        return r;
    }

    private void turnOffDevices(long time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        Log.d(TAG, "turn off at " + simpleDateFormat.format(time));
        ExternalStorageHandler externalStorageHandler = new ExternalStorageHandler();
        externalStorageHandler.writeFile("sleep time.txt", simpleDateFormat.format(time) + "\n");
    }
}
