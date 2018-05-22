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

import com.philips.lighting.hue.listener.PHLightListener;
import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHBridgeSearchManager;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.PHMessageType;
import com.philips.lighting.hue.sdk.PHSDKListener;
import com.philips.lighting.hue.sdk.utilities.PHUtilities;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHBridgeResource;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHHueParsingError;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;

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
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends Activity{

    Intent intent;
    public static DBHandler dbHandler;
    PHHueSDK phHueSDK;
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
        intent = new Intent(MainActivity.this, LampBridge.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {//뒤로가기 눌렀을 때.
        //현재 블루투스 연결된 장치가 DB에 있는지 확인 후 종료(블루투스 스피커)
        if(BluetoothAdapter.getDefaultAdapter().getProfileConnectionState(BluetoothProfile.A2DP) == BluetoothProfile.STATE_CONNECTED)//블루투스에 연결되어있는 상태이고
        {
            BluetoothAdapter.getDefaultAdapter().getProfileProxy(this, serviceListener, BluetoothProfile.A2DP);
        }

        //현재 id 값에 해당하는 DB에 저장된 모든 장치에 off 메세지 전송(스마트 플러그)
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

        //조명


        if(dbHandler.getLampBridgeDB() !=null)
        {
            phHueSDK = PHHueSDK.create();
            phHueSDK.getNotificationManager().registerSDKListener(SDKlistener);

            PHAccessPoint lastAccessPoint = new PHAccessPoint();
            lastAccessPoint.setIpAddress(dbHandler.getLampBridgeDB().get(0).getIp());
            lastAccessPoint.setUsername(dbHandler.getLampBridgeDB().get(0).getUserName());
            PHBridge connectedBridge = phHueSDK.getSelectedBridge();
            if(connectedBridge != null)
            {
                phHueSDK.disableHeartbeat(connectedBridge);
                phHueSDK.disconnect(connectedBridge);
            }
            phHueSDK.connect(lastAccessPoint);//디비에 저장된 브릿지로 연결

//            //조명 제어부분
//            PHBridge bridge = phHueSDK.getSelectedBridge();//브릿지 정보가져옴
//
//            List<PHLight> allLights = bridge.getResourceCache().getAllLights();//브릿지에 연결된 조명들.
//            for (PHLight light : allLights) {
//                LampItem lampItem = dbHandler.selectLamp(light.getUniqueId());
//                if(lampItem != null)// 조명이 등록되어있다.
//                {
//                    PHLightState lightState = new PHLightState();
//                    if(lampItem.getLampA() ==0)//램프 off.
//                    {
//                        lightState.setOn(false);
//                    }
//                    else{// 색 조절.
//                        float xy[] = PHUtilities.calculateXYFromRGB(lampItem.getLampR(),lampItem.getLampG(),lampItem.getLampB(),light.getModelNumber());
//                        lightState.setX(xy[0]);
//                        lightState.setY(xy[1]);
//                        lightState.setBrightness(lampItem.getLampA());
//                    }
//
//                    bridge.updateLightState(light, lightState, lightListener);
//                }
//            }
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


    //listener
    private PHSDKListener SDKlistener = new PHSDKListener() {
        @Override
        public void onCacheUpdated(List<Integer> list, PHBridge phBridge) {

        }

        @Override
        public void onBridgeConnected(PHBridge bridge, String username) {
//            phHueSDK.setSelectedBridge(bridge);
//            phHueSDK.enableHeartbeat(bridge, PHHueSDK.HB_INTERVAL);
//            phHueSDK.getLastHeartbeat().put(bridge.getResourceCache().getBridgeConfiguration() .getIpAddress(), System.currentTimeMillis());
//            phHueSDK.getDeviceName();

            //조명 제어부분

            List<PHLight> allLights = bridge.getResourceCache().getAllLights();//브릿지에 연결된 조명들.
            for (PHLight light : allLights) {
                LampItem lampItem = dbHandler.selectLamp(light.getUniqueId());
                if(lampItem != null)// 조명이 등록되어있다.
                {
                    PHLightState lightState = new PHLightState();
                    if(lampItem.getLampA() ==0)//램프 off.
                    {
                        lightState.setOn(false);
                    }
                    else{// 색 조절.
                        float xy[] = PHUtilities.calculateXYFromRGB(lampItem.getLampR(),lampItem.getLampG(),lampItem.getLampB(),light.getModelNumber());
                        lightState.setX(xy[0]);
                        lightState.setY(xy[1]);
                        lightState.setBrightness(lampItem.getLampA());
                    }

                    bridge.updateLightState(light, lightState, lightListener);
                }
            }
            phHueSDK.disconnect(bridge);
        }

        @Override
        public void onAuthenticationRequired(PHAccessPoint phAccessPoint) {
        }

        @Override
        public void onAccessPointsFound(List<PHAccessPoint> accessPoint) {
        }

        @Override
        public void onError(int code, final String message) {
        }

        @Override
        public void onConnectionResumed(PHBridge bridge) {
        }

        @Override
        public void onConnectionLost(PHAccessPoint phAccessPoint) {
        }

        @Override
        public void onParsingErrors(List<PHHueParsingError> list) {

        }
    };

    private PHLightListener lightListener = new PHLightListener() {

        @Override
        public void onSuccess() {
        }

        @Override
        public void onStateUpdate(Map<String, String> arg0, List<PHHueError> arg1) {
        }

        @Override
        public void onError(int arg0, String arg1) {}

        @Override
        public void onReceivingLightDetails(PHLight arg0) {}

        @Override
        public void onReceivingLights(List<PHBridgeResource> arg0) {}

        @Override
        public void onSearchComplete() {}
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (SDKlistener !=null && phHueSDK != null) {
            phHueSDK.getNotificationManager().unregisterSDKListener(SDKlistener);
        }
    }
}
////
