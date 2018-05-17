package com.example.leetaesoon.thebestsleep;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class SensorService extends Service {

    private static final String TAG = "SensorService";
    SensorHandler sensorHandler;
    Context context = this;

    public SensorService() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sensorHandler.stopMeasure();
        Log.d(TAG, "Called sensorHandler.stopMeasure()");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO : 현재 여러 번 실행이 될 경우 계속해서 새로운 쓰레드를 만들어 서비스를 돌려서 여러 개가 생김
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Enter onStartCommand");
                Log.d(TAG, "Create sensorHandler object");
                sensorHandler = new SensorHandler(context);

                sensorHandler.startMeasure();
                Log.d(TAG, "Called sensorHandler.startMeasure()");

                Log.d(TAG, "Exit onStartCommand");
            }
        }).start();

        return super.onStartCommand(intent, flags, startId);
    }
}