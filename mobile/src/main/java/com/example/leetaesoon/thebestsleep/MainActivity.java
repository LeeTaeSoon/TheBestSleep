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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

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
        ArrayList<PlugItem> plugItems = new ArrayList<>();
        if(dbHandler.getPlugUserDB() != null)//사용중인 유저 ID가 DB에 있을 때.
        {
            if(dbHandler.selectPlugs(dbHandler.getPlugUserDB().get(0).getUserId()) != null)//현재 사용중인 유저가 등록한 장치가 DB에 저장되어 있으면.
            {
                plugItems.addAll(dbHandler.selectPlugs(dbHandler.getPlugUserDB().get(0).getUserId()));
                for(final PlugItem pi : plugItems)
                {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                URL url = new URL(pi.geturl()+"/?"+"token="+dbHandler.getPlugUserDB().get(0).getUserToken());

                                JSONObject jsonObject1 = new JSONObject();
                                jsonObject1.accumulate("deviceId", pi.getdeviceId());
                                JSONObject content1 = new JSONObject();
                                JSONObject content2 = new JSONObject();
                                JSONObject content3 = new JSONObject();

                                content1.accumulate("state",0);// off

                                content2.accumulate("set_relay_state",content1);
                                content3.accumulate("system",content2);

                                jsonObject1.accumulate("requestData", ""+content3);



                                JSONObject jsonObject2 = new JSONObject();
                                jsonObject2.accumulate("method", "passthrough");
                                jsonObject2.accumulate("params", jsonObject1);

                                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                                connection.setRequestMethod("POST");//POST message
                                connection.setRequestProperty("Content-type", "application/json");
                                connection.setDoOutput(true);
                                connection.setDoInput(true);


                                OutputStream outputStream = connection.getOutputStream();
                                outputStream.write(jsonObject2.toString().getBytes());
                                outputStream.flush();

                                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK)//
                                {
                                    InputStream inputStream = connection.getInputStream();
                                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                    byte[] byteBuffer = new byte[1024];
                                    byte[] byteData = null;
                                    int nLength = 0;
                                    while ((nLength = inputStream.read(byteBuffer, 0, byteBuffer.length)) != -1) {
                                        byteArrayOutputStream.write(byteBuffer, 0, nLength);
                                    }
                                    byteData = byteArrayOutputStream.toByteArray();
                                    String response = new String(byteData);

                                    JSONObject responseJSON1 = new JSONObject(response);
                                    Log.d("off",responseJSON1.getString("msg"));
                                }


                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                    }).start();
                }

            }
        }

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
