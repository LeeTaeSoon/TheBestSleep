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
import android.provider.ContactsContract;
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
    public static DBHandler dbHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHandler = new DBHandler(this,DBHandler.DATABASE_NAME,null,1);//DBHander 생성


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
        intent = new Intent(MainActivity.this, BluetoothSpeaker.class);
//        intent.putExtra("DB", dbHandler);
        startActivity(intent);
    }

    public void LampSetting(View view) {
        intent = new Intent(MainActivity.this, LampSelect.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {//뒤로가기 눌렀을 때.
        //현재 블루투스 연결된 장치가 DB에 있는지 확인 후 종료
        if(BluetoothAdapter.getDefaultAdapter().getProfileConnectionState(BluetoothProfile.A2DP) == BluetoothProfile.STATE_CONNECTED)//블루투스에 연결되어있는 상태이고
        {
            BluetoothAdapter.getDefaultAdapter().getProfileProxy(this, serviceListener, BluetoothProfile.A2DP);
        }

        //현재 id 값에 해당하는 DB에 저장된 모든 장치에 off 메세지 전송

        super.onBackPressed();
    }

    private BluetoothProfile.ServiceListener serviceListener = new BluetoothProfile.ServiceListener() {
        @Override
        public void onServiceConnected(int profile, BluetoothProfile proxy) {
            if(proxy.getConnectedDevices().isEmpty() == false)
            {
                BluetoothDevice device = proxy.getConnectedDevices().get(0);
                if(dbHandler.selectSpeaker(device.getAddress()) != null)
                {
                    BluetoothAdapter.getDefaultAdapter().disable();
                }
            }
            BluetoothAdapter.getDefaultAdapter().closeProfileProxy(profile,proxy);
        }
        @Override
        public void onServiceDisconnected(int profile) {
            Log.d("Connect","DisConnectSV");
        }
    };
}
////
