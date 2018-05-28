package com.example.leetaesoon.thebestsleep;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.List;

public class PlugAdapter extends ArrayAdapter<PlugItem> {
    List<PlugItem> deviceList;
    DBHandler dbHandler;
    public PlugAdapter(@NonNull Context context, int resource, @NonNull List<PlugItem> objects) {
        super(context, resource, objects);
        deviceList = objects;
        dbHandler = new DBHandler(context,DBHandler.DATABASE_NAME,null,1);
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        if(v == null){
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.pluglistlayout, null);
        }
        final PlugItem plugItems = deviceList.get(position);
        if(plugItems != null){
            TextView t1 = (TextView)v.findViewById(R.id.device_Alias);
            TextView t2 = (TextView)v.findViewById(R.id.device_ID);
            final ToggleButton button = (ToggleButton)v.findViewById(R.id.plugToggle);

            t1.setText("Device Name : " + plugItems.getalias());
            t2.setText("Device ID : " + plugItems.getdeviceId());
            t2.setSelected(true);

            if(dbHandler.existPlug(plugItems.getdeviceId()))
            {
                Log.d("db","exist");
                button.setChecked(true);
            }
            else{
                Log.d("db","doesn't exist");
                button.setChecked(false);
            }
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(button.isChecked()==false)
                    {
                        dbHandler.deletePlug(deviceList.get(position).getdeviceId());
                        Log.d("db","deleted");
//                        notifyDataSetChanged();
                    }
                    else{
                        PlugItem pi = new PlugItem(deviceList.get(position).getdeviceId(),deviceList.get(position).geturl(),deviceList.get(position).getalias(),deviceList.get(position).getuserId());
                        dbHandler.addPlug(pi);
                        Log.d("db","added");
//                        notifyDataSetChanged();
                    }

                }
            });

        }
        return v;
    }
}
