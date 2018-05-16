package com.example.leetaesoon.thebestsleep;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.Toast;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.SaturationBar;

public class LampColorSetting extends Activity implements SeekBar.OnSeekBarChangeListener {
    ColorPicker colorPicker;
    SeekBar seekRed;
    SeekBar seekGreen;
    SeekBar seekBlue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lamp_color_setting);
        init();
    }
    public void init(){
        final float[] hsv_value = new float[3];
        colorPicker = (ColorPicker)findViewById(R.id.picker);
        seekRed = (SeekBar)findViewById(R.id.red_bar);
        seekGreen = (SeekBar)findViewById(R.id.green_bar);
        seekBlue = (SeekBar)findViewById(R.id.blue_bar);

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

    }
}
