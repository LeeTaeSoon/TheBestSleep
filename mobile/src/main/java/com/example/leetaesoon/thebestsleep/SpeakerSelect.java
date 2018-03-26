package com.example.leetaesoon.thebestsleep;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.io.Serializable;
import java.util.ArrayList;

public class SpeakerSelect extends Activity implements Serializable{

    ListView listView;
    ListAdapter listAdapter;
    ArrayList<PairedDevice> listData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speaker_select);

        Init();
    }

    private void Init() {
        Intent intent = getIntent();
        listData = new ArrayList<PairedDevice>();
        listData = (ArrayList<PairedDevice>)intent.getSerializableExtra("list");


        listView = (ListView)findViewById(R.id.speakerSelect);
        listAdapter = new ListViewAdapter(getApplicationContext(),R.layout.row,listData);
        listView.setAdapter(listAdapter);
    }
}
