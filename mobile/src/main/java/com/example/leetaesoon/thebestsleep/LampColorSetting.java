package com.example.leetaesoon.thebestsleep;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.Toast;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.SaturationBar;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;

public class LampColorSetting extends Activity implements SeekBar.OnSeekBarChangeListener {
    ColorPicker colorPicker;
    SeekBar seekRed;
    SeekBar seekGreen;
    SeekBar seekBlue;
    ArrayList<LampItem> lampItems;
    int position;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lamp_color_setting);
        init();
    }
    public void init(){
        Intent intent = getIntent();
        position = intent.getIntExtra("pos",-1);
        lampItems= (ArrayList<LampItem>) intent.getSerializableExtra("list");

        final float[] hsv_value = new float[3];
        colorPicker = (ColorPicker)findViewById(R.id.picker);
        seekRed = (SeekBar)findViewById(R.id.red_bar);
        seekGreen = (SeekBar)findViewById(R.id.green_bar);
        seekBlue = (SeekBar)findViewById(R.id.blue_bar);
        seekRed.setProgress(lampItems.get(position).getLampR());
        seekGreen.setProgress(lampItems.get(position).getLampG());
        seekBlue.setProgress(lampItems.get(position).getLampB());

        colorPicker.setNewCenterColor(Color.argb(0xff,lampItems.get(position).getLampR(),lampItems.get(position).getLampG(),lampItems.get(position).getLampB()));
        colorPicker.setOldCenterColor(Color.argb(0xff,lampItems.get(position).getLampR(),lampItems.get(position).getLampG(),lampItems.get(position).getLampB()));
        colorPicker.setTouchAnywhereOnColorWheelEnabled(false);
        colorPicker.setClickable(false);

        seekRed.setOnSeekBarChangeListener(this);
        seekGreen.setOnSeekBarChangeListener(this);
        seekBlue.setOnSeekBarChangeListener(this);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        int A;
        int R=seekRed.getProgress();
        int G=seekGreen.getProgress();
        int B=seekBlue.getProgress();
        //Reference the value changing
        int id=seekBar.getId();
        //Get the chnaged value
        if(id == com.example.leetaesoon.thebestsleep.R.id.red_bar)
            R=progress;
        else if(id == com.example.leetaesoon.thebestsleep.R.id.green_bar)
            G=progress;
        else if(id == com.example.leetaesoon.thebestsleep.R.id.blue_bar)
            B=progress;
        //Build and show the new color
        //some math so text shows (needs improvement for greys)
        colorPicker.setNewCenterColor(Color.argb(0xff,R,G,B));
        colorPicker.setOldCenterColor(Color.argb(0xff,R,G,B));

        //R,G,B를 가지고 명령어 보내면 됨
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        int id=seekBar.getId();
        //Get the chnaged value
        if(id == com.example.leetaesoon.thebestsleep.R.id.red_bar)
        {
            lampItems.get(position).setLampR(seekBar.getProgress());
        }
        else if(id == com.example.leetaesoon.thebestsleep.R.id.green_bar){
            lampItems.get(position).setLampG(seekBar.getProgress());
        }
        else if(id == com.example.leetaesoon.thebestsleep.R.id.blue_bar){
            lampItems.get(position).setLampB(seekBar.getProgress());
        }


    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("list2",lampItems);
        setResult(RESULT_OK,returnIntent);
        finish();
//        super.onBackPressed();
    }
}
