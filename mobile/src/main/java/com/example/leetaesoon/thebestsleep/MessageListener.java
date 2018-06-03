package com.example.leetaesoon.thebestsleep;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;
import com.philips.lighting.hue.listener.PHLightListener;
import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHHueSDK;
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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class MessageListener extends WearableListenerService {
    final static String TAG = "MessageListener";
    long pre = 0;
    PHHueSDK phHueSDK;

    @Override
    public void onMessageReceived(final MessageEvent messageEvent) {
        String data = new String(messageEvent.getData());
        //showToast(data);
        ExternalStorageHandler externalStorageHandler = new ExternalStorageHandler();
        externalStorageHandler.writeFile("sensor.txt", data + "\n");

        InnerStorageHandler innerStorageHandler = new InnerStorageHandler(this);
        String preString = innerStorageHandler.readFile("service_pre_time.txt");
        //Log.d(TAG, "preString : " + preString);
        if (preString.trim().length() > 0) pre = Long.parseLong(preString);
        //Log.d(TAG, "read pre : " + pre);

        DBHandler dbHandler = new DBHandler(getApplicationContext(), DBHandler.DATABASE_NAME, null, 1);
        ArrayList<HeartRate> heartRates = new ArrayList<>();
        ArrayList<Acceleration> accelerometers = new ArrayList<>();

        String[] datas = data.split("\n");

        for (int i = 0; i < datas.length; i++) {
            //Log.d("MessageListener", "datas[" + i + "] : " + datas[i]);
            String[] arr = datas[i].split(" ");

            if(arr.length > 1) {
                long time = Long.parseLong(arr[0]);
                long diff = (time - pre) / 60000L;
                //Log.d(TAG, "pre : " + pre + ", time : " + time);
                //Log.d(TAG, "diff : " + diff + " min");
                if (diff > 30) pre = time;           // first time
                else if (diff > 5) {
                    if (isSleep(time)) turnOffDevices(time);
                    pre = time;
                }

                switch (arr[1]) {
                    case "HeartRate":
                        int rate = Integer.parseInt(arr[3]);
                        //Log.d("MessageListener", "heart rate : " + rate);
                        HeartRate heartRate = new HeartRate(rate, time);
                        heartRates.add(heartRate);
                        break;

                    case "Accelerometer":
                        float x = Float.parseFloat(arr[3]);
                        float y = Float.parseFloat(arr[4]);
                        float z = Float.parseFloat(arr[5]);
                        //Log.d("MessageListener", "accelerometer : " + x + " " + y + " " + z);
                        double sum = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
                        Acceleration acc = new Acceleration(time, x, y, z, sum);
                        accelerometers.add(acc);
                        break;
                }
            }
        }

        //Log.d("MessageListener", "Start save datas in db");
        dbHandler.addAllHeartRate(heartRates);
        dbHandler.addAllAcceleration(accelerometers);
        dbHandler.close();
        //Log.d("MessageListener", "message : " + data);

        innerStorageHandler.writeFile("service_pre_time.txt", String.valueOf(pre), MODE_PRIVATE );

        super.onMessageReceived(messageEvent);

        //Looper.loop();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private boolean isSleep(long time) {
        Log.d(TAG, "called isSleep");
        long windowStart = time - 1800000;      // 30 min before
        if (getAvgAcc(windowStart, time) < 0.002 && getAvgHeartRate(windowStart, time) < 62) return true;
        else return false;
    }

    private double getAvgAcc(long start, long end) {
        DBHandler dbHandler = new DBHandler(getApplicationContext(), DBHandler.DATABASE_NAME, null, 1);
        double d = dbHandler.getAccelerationDBAvg(start, end);
        Log.d(TAG, "avg acc : " + d);
        return d;
    }

    private int getAvgHeartRate(long start, long end) {
        DBHandler dbHandler = new DBHandler(getApplicationContext(), DBHandler.DATABASE_NAME, null, 1);
        int r = dbHandler.getHeartRateDBAvg(start, end);
        Log.d(TAG, "avg heart : " + r);
        return r;
    }

    private void turnOffDevices(long time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        Log.d(TAG, "turn off at " + simpleDateFormat.format(time));
        ExternalStorageHandler externalStorageHandler = new ExternalStorageHandler();
        externalStorageHandler.writeFile("sleep time.txt", simpleDateFormat.format(time) + "\n");

        //장치 제어.
        DBHandler dbHandler = new DBHandler(this,DBHandler.DATABASE_NAME,null,1);

        if(BluetoothAdapter.getDefaultAdapter().getProfileConnectionState(BluetoothProfile.A2DP) == BluetoothProfile.STATE_CONNECTED)//블루투스에 연결되어있는 상태이고
        {
            BluetoothAdapter.getDefaultAdapter().getProfileProxy(getApplicationContext(), serviceListener, BluetoothProfile.A2DP);
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
                                DBHandler dbHandler = new DBHandler(getApplicationContext(),DBHandler.DATABASE_NAME,null,1);
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
            for(int dbSize=0;dbSize <dbHandler.getLampBridgeDB().size();dbSize++)
            {
                Log.d("bridge1","IP"+"("+dbSize+") : " +dbHandler.getLampBridgeDB().get(dbSize).getIp());
                Log.d("bridge1","UserName"+"("+dbSize+") : " +dbHandler.getLampBridgeDB().get(dbSize).getUserName());
            }
            lastAccessPoint.setIpAddress(dbHandler.getLampBridgeDB().get(0).getIp());
            lastAccessPoint.setUsername(dbHandler.getLampBridgeDB().get(0).getUserName());
            PHBridge connectedBridge = phHueSDK.getSelectedBridge();
            if(connectedBridge != null)
            {
                phHueSDK.disableHeartbeat(connectedBridge);
                phHueSDK.disconnect(connectedBridge);
            }
            phHueSDK.connect(lastAccessPoint);//디비에 저장된 브릿지로 연결
        }
    }

    private BluetoothProfile.ServiceListener serviceListener = new BluetoothProfile.ServiceListener() {
        @Override
        public void onServiceConnected(int profile, BluetoothProfile proxy) {
            if(proxy.getConnectedDevices().isEmpty() == false)
            {
                DBHandler dbHandler = new DBHandler(getApplicationContext(),DBHandler.DATABASE_NAME,null,1);
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
            //조명 제어부분

            List<PHLight> allLights = bridge.getResourceCache().getAllLights();//브릿지에 연결된 조명들.
            DBHandler dbHandler = new DBHandler(getApplicationContext(),DBHandler.DATABASE_NAME,null,1);
            Log.d("bridge1","브릿지 연결됨");
            for (PHLight light : allLights) {
                Log.d("bridge1","조명 정보 가져옴.");
                LampItem lampItem = dbHandler.selectLamp(light.getUniqueId());
                if(lampItem != null)// 조명이 등록되어있다.
                {
                    Log.d("bridge1","조명이 존재함!");
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
                    Log.d("redlight","Message Listener");
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
            Log.d("bridge1","OnError");
        }

        @Override
        public void onConnectionResumed(PHBridge bridge) {
            Log.d("bridge1","Connection Resume");
        }

        @Override
        public void onConnectionLost(PHAccessPoint phAccessPoint) {
            Log.d("bridge1","Connection Lost");
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
    public void onDestroy() {
        super.onDestroy();
        if (SDKlistener !=null && phHueSDK != null) {
            phHueSDK.getNotificationManager().unregisterSDKListener(SDKlistener);
        }
    }
}
