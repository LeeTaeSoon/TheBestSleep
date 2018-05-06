package com.example.leetaesoon.thebestsleep;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class PlugAdapter extends ArrayAdapter<PlugItem> {
    List<PlugItem> deviceList;
    public PlugAdapter(@NonNull Context context, int resource, @NonNull List<PlugItem> objects) {
        super(context, resource, objects);
        deviceList = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        if(v == null){
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.pluglistlayout, null);
        }
        final PlugItem plugItems = deviceList.get(position);
        if(plugItems != null){
            TextView t1 = (TextView)v.findViewById(R.id.device_ID);

            t1.setText("Device ID : " + plugItems.getDeviceID());
        }
        return v;
    }
}
