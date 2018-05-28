package com.example.leetaesoon.thebestsleep;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by hyun on 2018-03-25.
 */
//블루투스 스피커용 리스트 뷰
public class ListViewAdapter extends ArrayAdapter<PairedDevice> {

    Context m_context;
    List<PairedDevice> m_listData;
    DBHandler dbHandler;

    public ListViewAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<PairedDevice> objects) {
        super(context, resource, objects);
        m_context = context;
        m_listData = objects;
        dbHandler = new DBHandler(m_context,DBHandler.DATABASE_NAME,null,1);
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
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
            final ImageButton imageButton = (ImageButton)v.findViewById(R.id.imgBtn);
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    Log.v("aaaaaaaa","bbbbbbbbbbb"+position);
                    dbHandler.deleteSpeaker(m_listData.get(position).getAddress());
                    m_listData.remove(position);
                    notifyDataSetChanged();
                }
            });
            deviceName.setText(data.getName());
            deviceAddress.setText(data.getAddress());
        }

        return v;
        //return super.getView(position, convertView, parent);
    }
}
