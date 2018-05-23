package com.example.leetaesoon.thebestsleep;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.utilities.PHUtilities;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class LampSelect extends Activity {
    ListView listView;
    ArrayList<LampItem> lampItems;
    LampAdapter lampAdapter;
    DBHandler dbHandler;
    PHHueSDK phHueSDK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lamp_select);
        init();
    }
    public void init(){
        dbHandler = MainActivity.dbHandler;
        phHueSDK = PHHueSDK.create();
        listView = (ListView)findViewById(R.id.listview);


        lampItems = new ArrayList<>();

        getDeviceData();

        lampAdapter = new LampAdapter(this, R.layout.lamplistlayout, lampItems);
        listView.setAdapter(lampAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LampItem lamp = (LampItem) parent.getItemAtPosition(position);
                Intent intent = new Intent(LampSelect.this,LampColorSetting.class);
                intent.putExtra("pos", position);
                Log.d("light",lampItems.get(position).getLampName());
                intent.putExtra("list",lampItems);
                startActivityForResult(intent,200);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode ==RESULT_OK)
        {
            if(requestCode==200)
            {
                lampItems = (ArrayList<LampItem>) data.getSerializableExtra("list2");
                lampAdapter = new LampAdapter(this, R.layout.lamplistlayout, lampItems);
                listView.setAdapter(lampAdapter);
                Log.d("back","result : R "+lampItems.get(0).getLampR() + "A : "+ lampItems.get(0).getLampA());
            }
        }
    }

    // Device ID 가져오는 Task 수행
    public void getDeviceData(){
        PHBridge bridge = phHueSDK.getSelectedBridge();

        List<PHLight> allLights = bridge.getResourceCache().getAllLights();

        for (PHLight light : allLights) {

            LampItem lampItem = dbHandler.selectLamp(light.getUniqueId());
            if(lampItem == null)
            {
                lampItem = new LampItem(light.getUniqueId(),light.getName(),128,128,128,50,false);
            }
            lampItems.add(lampItem);
//            Log.d("light","unique : "+light.getUniqueId() + "Name : "+light.getName());
        }
//            PHLightState lightState = new PHLightState();
//            lightState.setHue(rand.nextInt(MAX_HUE));
//            lightState.setHue(k);
//            float xy[] = PHUtilities.calculateXYFromRGB(255,0,0,light.getModelNumber());
//            lightState.setX(xy[0]);
//            lightState.setY(xy[1]);
//            lightState.setOn(false);

            //            lightState.setSaturation();
            // To validate your lightstate is valid (before sending to the bridge) you can use:
            // String validState = lightState.validateState();
//            bridge.updateLightState(light, lightState, listener);
            //  bridge.updateLightState(light, lightState);   // If no bridge response is required then use this simpler form.
//        }
    }

    @Override
    public void onBackPressed() {//
        dbHandler.deleteLampDB();
        for(LampItem item : lampItems)
        {
            if(item.getLampControl() == true)
            {
                dbHandler.addLamp(item);
            }
        }
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result",1);
        setResult(RESULT_OK,returnIntent);
        finish();
//        super.onBackPressed();
    }

    public void NewBridge(View view) {
        dbHandler.deleteLampDB();
        for(LampItem item : lampItems)
        {
            if(item.getLampControl() == true)
            {
                dbHandler.addLamp(item);
            }
        }

        dbHandler.deleteLampBridgeDB();
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result",0);
        setResult(RESULT_OK,returnIntent);
        finish();
    }
}
