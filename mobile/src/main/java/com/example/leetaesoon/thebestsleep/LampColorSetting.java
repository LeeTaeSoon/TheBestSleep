package com.example.leetaesoon.thebestsleep;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.SaturationBar;

public class LampColorSetting extends Activity {
    ColorPicker colorPicker;
    SaturationBar saturationBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lamp_color_setting);
        init();
    }
    public void init(){
        final float[] hsv_value = new float[3];
        colorPicker = (ColorPicker)findViewById(R.id.picker);
        saturationBar = (SaturationBar)findViewById(R.id.saturationbar);
        colorPicker.addSaturationBar(saturationBar);

        colorPicker.setOnColorChangedListener(new ColorPicker.OnColorChangedListener() {
            @Override
            public void onColorChanged(int color) {
                colorPicker.setOldCenterColor(color);
                String c = Integer.toHexString(color);
                Log.v("Toast_sat",""+c);
            }
        });

        colorPicker.setOnColorSelectedListener(new ColorPicker.OnColorSelectedListener() {
            @Override
            public void onColorSelected(int color) {
                colorPicker.setOldCenterColor(color);
                String c = Integer.toHexString(color);
                Log.v("Toast_sat",""+c);
            }
        });
        saturationBar.setOnSaturationChangedListener(new SaturationBar.OnSaturationChangedListener() {
            @Override
            public void onSaturationChanged(int saturation) {
                //colorPicker.setOldCenterColor(saturation);
               colorPicker.setNewCenterColor(saturation);
               String c = Integer.toHexString(saturation);
               Log.v("Toast_sat",""+c);
            }
        });
    }
}
