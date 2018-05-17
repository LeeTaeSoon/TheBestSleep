package com.example.leetaesoon.thebestsleep;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.io.Serializable;
import java.util.ArrayList;

public class SpeakerSelect extends Activity implements Serializable{

    ListView listView;
    ListAdapter listAdapter;
    ArrayList<PairedDevice> listData;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speaker_select);

        Init();
    }

    private void Init() {
        intent = getIntent();
        listData = new ArrayList<PairedDevice>();
        listData = (ArrayList<PairedDevice>)intent.getSerializableExtra("list");


        listView = (ListView)findViewById(R.id.speakerSelect);
        listAdapter = new SpeakerSelectAdapter(getApplicationContext(),R.layout.row2,listData);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                intent.putExtra("data",listData.get(i));
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }
}
