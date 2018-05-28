package com.example.leetaesoon.thebestsleep;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHBridgeSearchManager;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.PHMessageType;
import com.philips.lighting.hue.sdk.PHSDKListener;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHHueParsingError;

import java.util.List;

public class LampBridge extends Activity {
    private PHHueSDK phHueSDK;
    private HueSharedPreferences prefs;
    private AccessPointListAdapter adapter;
    private ListView accessPointList;
    private boolean lastSearchWasIPScan = false;
    private DBHandler dbHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lamp_bridge);

        Init();
    }

    private void Init() {
        phHueSDK = PHHueSDK.create();
        phHueSDK.getNotificationManager().registerSDKListener(listener);

        dbHandler = new DBHandler(this,DBHandler.DATABASE_NAME,null,1);
        //Bridge 를 보여줄 리스트, 아답터.
        adapter = new AccessPointListAdapter(getApplicationContext(), phHueSDK.getAccessPointsFound());
        accessPointList = (ListView) findViewById(R.id.bridge_list);
        accessPointList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                PHAccessPoint lastAccessPoint = null;

                prefs = HueSharedPreferences.getInstance(getApplicationContext());
                String lastIpAddress   = prefs.getLastConnectedIPAddress();
                String lastUsername    = prefs.getUsername();
                if(lastIpAddress !=null && !lastIpAddress.equals(""))//이전에 사용한 적이 있고
                {
                    if(lastIpAddress.equals(((PHAccessPoint) adapter.getItem(position)).getIpAddress()))//현재 클릭한 것이 이전에 사용한 것일 때.
                    {
                        lastAccessPoint = new PHAccessPoint();
                        lastAccessPoint.setIpAddress(lastIpAddress);
                        lastAccessPoint.setUsername(lastUsername);
                    }
                }
                else{//처음 등록하는 브릿지 일 때.
                    lastAccessPoint = (PHAccessPoint) adapter.getItem(position);
                }

                PHBridge connectedBridge = phHueSDK.getSelectedBridge();

                if (connectedBridge != null) {
                    String connectedIP = connectedBridge.getResourceCache().getBridgeConfiguration().getIpAddress();
                    if (connectedIP != null) {   // We are already connected here:-
                        phHueSDK.disableHeartbeat(connectedBridge);
                        phHueSDK.disconnect(connectedBridge);
                    }
                }
                PHWizardAlertDialog.getInstance().showProgressDialog(R.string.connecting, LampBridge.this);
                phHueSDK.connect(lastAccessPoint);
            }
        });

        accessPointList.setAdapter(adapter);

        prefs = HueSharedPreferences.getInstance(getApplicationContext());
        String lastIpAddress   = prefs.getLastConnectedIPAddress();
        String lastUsername    = prefs.getUsername();

        if (lastIpAddress !=null && !lastIpAddress.equals("")) {//이미 이전에 연결 했던 브릿지가 있을 때 바로 연결.
            PHAccessPoint lastAccessPoint = new PHAccessPoint();
            lastAccessPoint.setIpAddress(lastIpAddress);
            lastAccessPoint.setUsername(lastUsername);
//            if (!phHueSDK.isAccessPointConnected(lastAccessPoint)) {
            PHBridge connectedBridge = phHueSDK.getSelectedBridge();
            if(connectedBridge != null)
            {
                phHueSDK.disableHeartbeat(connectedBridge);
                phHueSDK.disconnect(connectedBridge);
            }
//            }
            PHWizardAlertDialog.getInstance().showProgressDialog(R.string.connecting, LampBridge.this);
            phHueSDK.connect(lastAccessPoint);
        }
        else{//이전에 브릿지 연결을 안했을 경우(맨 처음.)
            doBridgeSearch();
        }
    }

    //리스너 생성
    private PHSDKListener listener = new PHSDKListener() {
        @Override
        public void onCacheUpdated(List<Integer> list, PHBridge phBridge) {

        }

        @Override
        public void onBridgeConnected(PHBridge bridge, String username) {
            phHueSDK.setSelectedBridge(bridge);
            phHueSDK.enableHeartbeat(bridge, PHHueSDK.HB_INTERVAL);
            phHueSDK.getLastHeartbeat().put(bridge.getResourceCache().getBridgeConfiguration() .getIpAddress(), System.currentTimeMillis());
            prefs.setLastConnectedIPAddress(bridge.getResourceCache().getBridgeConfiguration().getIpAddress());
            phHueSDK.getDeviceName();
            prefs.setUsername(username);
            PHWizardAlertDialog.getInstance().closeProgressDialog();
            //DB에 Username, ip 추가.
            LampBridgeItem lampBridgeItem = new LampBridgeItem(username,bridge.getResourceCache().getBridgeConfiguration().getIpAddress());
            if(dbHandler.selectLampBridge(username) ==null)
            {
                dbHandler.addLampBridge(lampBridgeItem);
            }
            startMainActivity();
        }

        @Override
        public void onAuthenticationRequired(PHAccessPoint phAccessPoint) {
            phHueSDK.startPushlinkAuthentication(phAccessPoint);
            startActivity(new Intent(LampBridge.this, PHPushlinkActivity.class));
        }

        @Override
        public void onAccessPointsFound(List<PHAccessPoint> accessPoint) {
            PHWizardAlertDialog.getInstance().closeProgressDialog();// 로딩 창 닫기.
            if (accessPoint != null && accessPoint.size() > 0) {
                phHueSDK.getAccessPointsFound().clear();
                phHueSDK.getAccessPointsFound().addAll(accessPoint);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.updateData(phHueSDK.getAccessPointsFound());
                    }
                });
            }
        }

        @Override
        public void onError(int code, final String message) {
            Log.e("Lamp", "on Error Called : " + code + ":" + message);

            if (code == PHHueError.NO_CONNECTION) {
                Log.w("Lamp", "On No Connection");
            }
            else if (code == PHHueError.AUTHENTICATION_FAILED || code== PHMessageType.PUSHLINK_AUTHENTICATION_FAILED) {
                PHWizardAlertDialog.getInstance().closeProgressDialog();
            }
            else if (code == PHHueError.BRIDGE_NOT_RESPONDING) {
                Log.w("Lamp", "Bridge Not Responding . . . ");
                PHWizardAlertDialog.getInstance().closeProgressDialog();
                LampBridge.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        PHWizardAlertDialog.showErrorDialog(LampBridge.this, message, R.string.btn_ok);
                    }
                });

            }
            else if (code == PHMessageType.BRIDGE_NOT_FOUND) {

                if (!lastSearchWasIPScan) {  // Perform an IP Scan (backup mechanism) if UPNP and Portal Search fails.
                    phHueSDK = PHHueSDK.getInstance();
                    PHBridgeSearchManager sm = (PHBridgeSearchManager) phHueSDK.getSDKService(PHHueSDK.SEARCH_BRIDGE);
                    sm.search(false, false, true);
                    lastSearchWasIPScan=true;
                }
                else {
                    PHWizardAlertDialog.getInstance().closeProgressDialog();
                    LampBridge.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            PHWizardAlertDialog.showErrorDialog(LampBridge.this, message, R.string.btn_ok);
                        }
                    });
                }


            }
        }

        @Override
        public void onConnectionResumed(PHBridge bridge) {
            if (LampBridge.this.isFinishing()) return;

            phHueSDK.getLastHeartbeat().put(bridge.getResourceCache().getBridgeConfiguration().getIpAddress(),  System.currentTimeMillis());
            for (int i = 0; i < phHueSDK.getDisconnectedAccessPoint().size(); i++) {

                if (phHueSDK.getDisconnectedAccessPoint().get(i).getIpAddress().equals(bridge.getResourceCache().getBridgeConfiguration().getIpAddress())) {
                    phHueSDK.getDisconnectedAccessPoint().remove(i);
                }
            }
        }

        @Override
        public void onConnectionLost(PHAccessPoint phAccessPoint) {
            if (!phHueSDK.getDisconnectedAccessPoint().contains(phAccessPoint)) {
                phHueSDK.getDisconnectedAccessPoint().add(phAccessPoint);
            }
        }

        @Override
        public void onParsingErrors(List<PHHueParsingError> list) {

        }
    };


    //종료 시
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v("Lamp","onDestroy");
        if (listener !=null) {
            phHueSDK.getNotificationManager().unregisterSDKListener(listener);
        }
        phHueSDK.disableAllHeartbeat();
    }

    public void doBridgeSearch() {
        PHWizardAlertDialog.getInstance().showProgressDialog(R.string.search_progress, LampBridge.this);
        PHBridgeSearchManager sm = (PHBridgeSearchManager) phHueSDK.getSDKService(PHHueSDK.SEARCH_BRIDGE);
        sm.search(true, true);
    }

    public void startMainActivity() {
        Intent intent = new Intent(getApplicationContext(), LampSelect.class);
        startActivityForResult(intent,1010);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode ==RESULT_OK)
        {
            if(requestCode==1010)
            {
                int check = data.getIntExtra("result",-1);
                if(check ==1)//뒤로가기를 했다면 메인화면으로 가도록.
                {
                    finish();
                }
                else if(check==0)
                {
                    doBridgeSearch();
                }
            }
        }
    }
}
