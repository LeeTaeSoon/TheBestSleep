package com.example.leetaesoon.thebestsleep;

import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends WearableActivity {

    private static final String TAG = "MainActivity";

    InnerStorageHandler innerStorageHandler;
    ImageButton mImageButton;
    int using;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        innerStorageHandler = new InnerStorageHandler(this);
        mImageButton = findViewById(R.id.button);

        String status = innerStorageHandler.readFile("service_status.txt");
        if (status.trim().equals("1")) onService();
        else offService();
        Log.d(TAG, "using : " + using);

        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (using == 0) onService();
                else offService();
            }
        });

        // Enables Always-on
        setAmbientEnabled();
    }

    public void onService() {
        Log.d(TAG, "Service On");
        mImageButton.setImageResource(R.drawable.stop);

        Intent intent = new Intent(this, SensorService.class);
        startService(intent);

        using = 1;
        innerStorageHandler.writeFile("service_status.txt", String.valueOf(using), MODE_PRIVATE);
    }

    public void offService() {
        Log.d(TAG, "Service Off");
        mImageButton.setImageResource(R.drawable.using);

        Intent intent = new Intent(this, SensorService.class);
        stopService(intent);

        using = 0;
        innerStorageHandler.writeFile("service_status.txt", String.valueOf(using), MODE_PRIVATE);
    }

    @Override
    protected void onPause() {
        innerStorageHandler.writeFile("service_status.txt", String.valueOf(using), MODE_PRIVATE);
        super.onPause();
    }
}