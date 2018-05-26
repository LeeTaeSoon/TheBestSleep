package com.example.leetaesoon.thebestsleep;

import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.ArrayList;

public class MessageListener extends WearableListenerService {
    @Override
    public void onMessageReceived(final MessageEvent messageEvent) {
        String data = new String(messageEvent.getData());
        //showToast(data);
        ExternalStorageHandler externalStorageHandler = new ExternalStorageHandler();
        externalStorageHandler.writeFile(data + "\n");

        DBHandler dbHandler = new DBHandler(getApplicationContext(), DBHandler.DATABASE_NAME, null, 1);
        ArrayList<HeartRate> heartRates = new ArrayList<>();
        ArrayList<Acceleration> accelerometers = new ArrayList<>();

        String[] datas = data.split("\n");

        for (int i = 0; i < datas.length; i++) {
            //Log.d("MessageListener", "datas[" + i + "] : " + datas[i]);
            String[] arr = datas[i].split(" ");

            if(arr.length > 1) {
                long time = Long.parseLong(arr[0]);
                //Log.d("MessageListener", "time : " + time);
                //Log.d("MessageListener", "type : " + arr[1]);

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
        //Log.d("MessageListener", "message : " + data);
        super.onMessageReceived(messageEvent);

        //Looper.loop();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
