package com.example.leetaesoon.thebestsleep;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class PlugList extends Activity {
    ListView listView;
    List<PlugItem> plugItems;
    PlugAdapter plugAdapter;
    TextView user_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plug_list);
        init();
    }
    public void init(){
        Intent intent = getIntent();
        String user_email = intent.getStringExtra("email");
        user_id = (TextView)findViewById(R.id.user_id);
        user_id.setText(user_email +" 님");

        listView = (ListView)findViewById(R.id.listview);
        plugItems = new ArrayList<>();
        // 임시 데이터
        plugItems.add(new PlugItem("temp_ID_1"));
        plugItems.add(new PlugItem("temp_ID_2"));
        plugItems.add(new PlugItem("temp_ID_3"));
        getDeviceData();
        plugAdapter = new PlugAdapter(this, R.layout.pluglistlayout, plugItems);
        listView.setAdapter(plugAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PlugItem p = (PlugItem) parent.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(),"Click" + p.getDeviceID(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    // Device ID 가져오는 Task 수행
    public void getDeviceData(){

    }

    public void KasaLogout(View view) {
        finish();
    }
}
