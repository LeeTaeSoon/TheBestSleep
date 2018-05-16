package com.example.leetaesoon.thebestsleep;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class LampSelect extends Activity {
    ListView listView;
    List<LampItem> lampItems;
    LampAdapter lampAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lamp_select);
        init();
    }
    public void init(){
        listView = (ListView)findViewById(R.id.listview);


        lampItems = new ArrayList<>();
        lampItems.add(new LampItem("temp1","temp1_addr"));
        lampItems.add(new LampItem("temp2","temp2_addr"));
        lampItems.add(new LampItem("temp3","temp3_addr"));
        lampItems.add(new LampItem("temp4","temp4_addr"));
        getDeviceData();
        lampAdapter = new LampAdapter(this, R.layout.lamplistlayout, lampItems);
        listView.setAdapter(lampAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LampItem lamp = (LampItem) parent.getItemAtPosition(position);
                Intent intent = new Intent(LampSelect.this,LampColorSetting.class);
                intent.putExtra("lampID", lamp.getDeviceID());
                startActivity(intent);
            }
        });
    }
    // Device ID 가져오는 Task 수행
    public void getDeviceData(){

    }
}
