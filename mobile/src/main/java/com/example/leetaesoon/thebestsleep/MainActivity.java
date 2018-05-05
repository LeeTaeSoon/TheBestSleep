package com.example.leetaesoon.thebestsleep;

import android.app.Activity;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends Activity{

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void logShow(View view) {
        intent = new Intent(MainActivity.this, sleepRecord.class);
        startActivity(intent);
    }

    public void plugSetting(View view) {
        intent = new Intent(MainActivity.this, plugSignIn.class);
        startActivity(intent);
    }

    public void speakerSetting(View view) {
        intent = new Intent(MainActivity.this, SpeakerSelect.class);
        startActivity(intent);
    }

    public void LampSetting(View view) {
        intent = new Intent(MainActivity.this, LampSelect.class);
        startActivity(intent);
    }
}
