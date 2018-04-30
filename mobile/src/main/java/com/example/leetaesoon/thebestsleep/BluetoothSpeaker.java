package com.example.leetaesoon.thebestsleep;

import android.app.Activity;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class BluetoothSpeaker extends AppCompatActivity {
    BluetoothAdapter m_BtAdapter;// onCreate에서 정의함.(bluetooth 송수신 장치)
    BluetoothA2dp m_A2dpService; // m_A2dpListener에서 정의함.(블루투스를 통한 오디오 스트리밍 방법 정의)
    AudioManager m_Audio;
    ImageButton speakerPlus;
    SeekBar volumeSeekBar;
    Intent bluetoothIntent;
    ArrayList<PairedDevice> listData;//현재 연결된 기기의 정보를 가져온다.(사용자가 등록한 주소와 비교해서 볼륨을 낮추거나 블루투스를 끄도록 하기위함)
    ArrayList<PairedDevice> selectedDevice;//선택된 기기의 정보를 저장한다.
    ListView listView_Speaker;
    final int request_code = 111;
    Intent intent;
    ListViewAdapter listViewAdapter_Speaker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_speaker);
    }

    private void Init() {
        listView_Speaker = (ListView)findViewById(R.id.listViewSpeaker);//장치정보 보여줄 listview
        listData = new ArrayList<PairedDevice>();//장치 정보
        selectedDevice = new ArrayList<PairedDevice>();//선택된 장치들
        listViewAdapter_Speaker = new ListViewAdapter(getApplicationContext(),R.layout.row,selectedDevice);
        listView_Speaker.setAdapter(listViewAdapter_Speaker);
        Switch bluetoothSwitch = (Switch)findViewById(R.id.bluetoothSwitch);//볼륨 스위치
        m_Audio = (AudioManager)getSystemService(Context.AUDIO_SERVICE);//볼륨 조절을 위함.
        volumeSeekBar = (SeekBar)findViewById(R.id.volumeSeekbar); //볼륨 조절 바
        speakerPlus = (ImageButton)findViewById(R.id.speakerPlus);
        getConnectedDevice();

        //블루투스
        if(m_BtAdapter.isEnabled()) {//블루투스가 켜져있으면 스위치 on상태로 초기화.
            bluetoothSwitch.setChecked(true);
        }
        else//켜져 있으면 off 상태로 초기화.
        {
            bluetoothSwitch.setChecked(false);
        }

        bluetoothSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {//블루투스 온오프 기능 리스너
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    m_BtAdapter.enable();
                    bluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(bluetoothIntent, 1);//뒤에 찾는 부분 더 해줘야 한다.
                }
                else
                {
                    m_BtAdapter.disable();
                }
            }
        });

        //볼륨
        volumeSeekBar.setMax(m_Audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        volumeSeekBar.setProgress(m_Audio.getStreamVolume(AudioManager.STREAM_MUSIC),true);//현재 볼륨으로 seekbar의 초기값을 초기화 해준다.
        Toast.makeText(this," "+m_Audio.getStreamVolume(AudioManager.STREAM_MUSIC),Toast.LENGTH_SHORT);
        volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {//seekbar를 움직일 때만 볼륨 변화.
                m_Audio.setStreamVolume(AudioManager.STREAM_MUSIC,progress,AudioManager.FLAG_PLAY_SOUND);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        speakerPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//현재 페어링 된 기기를 보여주고 선택할 수 있도록 넘겨준다.
                Set<BluetoothDevice> pairingDeivces = m_BtAdapter.getBondedDevices();
                if(pairingDeivces.size()>0)
                {
                    listData.clear();
//                    Toast.makeText(getApplicationContext()," "+ pairingDeivces.size(),Toast.LENGTH_SHORT).show();
                    for(BluetoothDevice device : pairingDeivces)
                    {

                        PairedDevice data = new PairedDevice(device.getName(),device.getAddress());//각 디바이스의 정보에 대한 데이터 생성
                        listData.add(data);//데이터를 리스트에 저장

                    }
                }
                intent = new Intent(MainActivity.this,SpeakerSelect.class);
                intent.putExtra("list",listData);

                startActivityForResult(intent,request_code);
            }
        });
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)
        {
            //volumeSeekBar.setProgress(m_Audio.getStreamVolume(AudioManager.STREAM_MUSIC),true);
            // Toast.makeText(this," " + m_Audio.getStreamVolume(AudioManager.STREAM_MUSIC),Toast.LENGTH_SHORT);
            int vol = volumeSeekBar.getProgress();
            if(vol>=0)
                volumeSeekBar.setProgress(vol-1);
            return true;
        }
        else if(keyCode==KeyEvent.KEYCODE_VOLUME_UP)
        {
            //volumeSeekBar.setProgress(m_Audio.getStreamVolume(AudioManager.STREAM_MUSIC),true);
            //Toast.makeText(this," " + m_Audio.getStreamVolume(AudioManager.STREAM_MUSIC),Toast.LENGTH_SHORT);
            int vol = volumeSeekBar.getProgress();
            if(vol<=m_Audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            volumeSeekBar.setProgress(vol+1);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void getConnectedDevice()
    {
        m_BtAdapter = BluetoothAdapter.getDefaultAdapter();
        int state = m_BtAdapter.getProfileConnectionState(BluetoothProfile.A2DP);

        if(state != BluetoothProfile.STATE_CONNECTED)//연결 기기가 없을 경우.
        {
            Toast.makeText(this,"No device connected",Toast.LENGTH_SHORT).show();
        }
        else
        {
            try {
                m_BtAdapter.getProfileProxy(this, serviceListener, BluetoothProfile.A2DP);
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    private BluetoothProfile.ServiceListener serviceListener = new BluetoothProfile.ServiceListener() {
        @Override
        public void onServiceConnected(int profile, BluetoothProfile proxy) {
            BluetoothDevice device = proxy.getConnectedDevices().get(0);
            Toast.makeText(getBaseContext(),"Connected Device : "+ device.getName().toString() ,Toast.LENGTH_SHORT).show();
//            Log.d("Connect",device.getName().toString()+"is Connected Svlistener");
        }
        @Override
        public void onServiceDisconnected(int profile) {
            Log.d("Connect","DisConnectSV");
        }
    };

    @Override
    protected void onDestroy() {
        m_BtAdapter.closeProfileProxy(BluetoothProfile.A2DP, m_A2dpService);
//        unregisterReceiver(m_Receiver);//이거 안해주면 백그라운드에서 계속 프로그램이 돈다.
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==request_code && resultCode== Activity.RESULT_OK)
        {
            PairedDevice device = (PairedDevice)data.getExtras().get("data");
            for(PairedDevice devices : selectedDevice)
            {
                if(devices.getAddress().equals(device.getAddress()))
                {
                    Toast.makeText(getBaseContext(),"이미 선택한 장치입니다.",Toast.LENGTH_SHORT).show();
                    return;
                }

            }
            selectedDevice.add(device);
            listViewAdapter_Speaker.notifyDataSetChanged();
        }
    }
}
