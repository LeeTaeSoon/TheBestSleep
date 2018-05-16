package com.example.leetaesoon.thebestsleep;

import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

public class MessageListener extends WearableListenerService {
    @Override
    public void onMessageReceived(final MessageEvent messageEvent) {
        String data = new String(messageEvent.getData());
        //showToast(data);
        ExternalStorageHandler externalStorageHandler = new ExternalStorageHandler();
        externalStorageHandler.writeFile(data + "\n");
        //Log.d("MessageListener", "message : " + data);
        super.onMessageReceived(messageEvent);

        //Looper.loop();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
