package com.example.leetaesoon.thebestsleep;

import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class MainActivity extends WearableActivity {

    private static final String TAG = "MainActivity";

    ImageButton mImageButton;
    int using;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageButton = findViewById(R.id.button);

        String status = readFromFile();
        // TODO : 껏다가 켰을때 서비스를 하나 더 만들게 됨
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
        using = 1;

        Intent intent = new Intent(this, SensorService.class);
        startService(intent);
    }

    public void offService() {
        Log.d(TAG, "Service Off");
        mImageButton.setImageResource(R.drawable.using);
        using = 0;

        Intent intent = new Intent(this, SensorService.class);
        stopService(intent);
    }

    @Override
    protected void onPause() {
        writeToFile(String.valueOf(using));
        super.onPause();
    }

    private void writeToFile(String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput("service_status.txt", MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private String readFromFile() {
        Log.d(TAG, "Start read file");
        String ret = "";

        try {
            InputStream inputStream = openFileInput("service_status.txt");

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    Log.d(TAG, "receiveString: " + receiveString);
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            Log.e(TAG, "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e(TAG, "Can not read file: " + e.toString());
        }

        Log.d(TAG, "End read file");
        return ret;
    }
}