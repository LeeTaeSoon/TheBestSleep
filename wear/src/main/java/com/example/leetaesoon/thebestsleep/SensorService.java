package com.example.leetaesoon.thebestsleep;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class SensorService extends Service {

    private static final String TAG = "SensorService";
    SensorHandler sensorHandler;
    Context context = this;
    boolean running = false;

    public SensorService() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");
        running = false;

        if (sensorHandler != null) {
            sensorHandler.stopMeasure();
            Log.d(TAG, "Called sensorHandler.stopMeasure()");
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Enter onStartCommand");
        // TODO : 현재 여러 번 실행이 될 경우 계속해서 새로운 쓰레드를 만들어 서비스를 돌려서 여러 개가 생김
        if (running) {
            Log.d(TAG, "Service is already running");
            return  START_NOT_STICKY;
        } else {
            running = true;

            startForeground(1, new Notification());
            NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            Notification notification;
            notification = new Notification.Builder(getApplicationContext())
                    .setContentTitle("")
                    .setContentText("")
                    .setSmallIcon(getApplicationInfo().icon)
                    .build();

            nm.notify(startId, notification);
            nm.cancel(startId);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "Create sensorHandler object");
                    sensorHandler = new SensorHandler(context);

                    sensorHandler.startMeasure();
                    Log.d(TAG, "Called sensorHandler.startMeasure()");
                }
            }).start();
        }

        Log.d(TAG, "Exit onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }
}