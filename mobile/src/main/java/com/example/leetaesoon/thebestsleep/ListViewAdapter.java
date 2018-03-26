package com.example.leetaesoon.thebestsleep;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by hyun on 2018-03-25.
 */

public class ListViewAdapter extends ArrayAdapter<PairedDevice> {

    Context m_context;
    List<PairedDevice> m_listData;

    public ListViewAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<PairedDevice> objects) {
        super(context, resource, objects);
        m_context = context;
        m_listData = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        if(v==null)
        {
            LayoutInflater inflater = (LayoutInflater)m_context.getSystemService(m_context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.row,null);
        }
        PairedDevice data = m_listData.get(position);
        if(v != null)
        {
            TextView deviceName = (TextView)v.findViewById(R.id.device_Name);
            TextView deviceAddress = (TextView)v.findViewById(R.id.device_address);
            deviceName.setText(data.getName());
            deviceAddress.setText(data.getAddress());
        }

        return v;
        //return super.getView(position, convertView, parent);
    }
}
