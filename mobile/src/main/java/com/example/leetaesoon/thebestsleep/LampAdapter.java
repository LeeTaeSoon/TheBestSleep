package com.example.leetaesoon.thebestsleep;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.List;

public class LampAdapter extends ArrayAdapter<LampItem> {
    List<LampItem> lamplist;
    public LampAdapter(@NonNull Context context, int resource, @NonNull List<LampItem> objects) {
        super(context, resource, objects);
        lamplist = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        if(v == null){
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.lamplistlayout, null);
        }
        final LampItem lampItems = lamplist.get(position);
        if(lampItems != null){
            TextView t1 = (TextView)v.findViewById(R.id.lamp_device_ID);
            TextView t2 = (TextView)v.findViewById(R.id.lamp_MAC_addr);
            /* 초기 밝기값 seek bar에 설정해야함
             * seekBar.setProgress(int value);
             * */
            SeekBar seekBar = (SeekBar)v.findViewById(R.id.seekbar);
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    //밝기값이 변경되면 이 함수에서 밝기값을 저장해주는 task 수행
                    seekBar.setProgress(progress);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

            t1.setText("Device ID : " + lampItems.getDeviceID());
            t2.setText("MAC addr : " + lampItems.getMACaddr());
        }
        return v;
    }
}
