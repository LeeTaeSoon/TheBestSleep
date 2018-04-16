package com.example.leetaesoon.thebestsleep;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.wear.widget.WearableRecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomRecyclerAdapter extends WearableRecyclerView.Adapter<CustomRecyclerAdapter.ViewHolder> {

    private static final String TAG = "CustomRecyclerAdapter";

    public static class ViewHolder extends WearableRecyclerView.ViewHolder {

         private final ImageView mImageView;
         private final TextView mTextView;

        public ViewHolder(View view) {
            super(view);
             mImageView = (ImageView) view.findViewById(R.id.imageView);
             mTextView = (TextView) view.findViewById(R.id.textView);
        }

         @Override
         public String toString() { return (String) mTextView.getText(); }
    }

    // TODO : 디바이스 정보를 받아 저장
    Context context;
    Device[] devices;
    public CustomRecyclerAdapter(Context context, Device[] devices) {
        this.context = context;
        this.devices = devices;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_row_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.d(TAG, "Element " + position + " set.");

        holder.mTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

            }
        });

        // Replaces content of view with correct element from data set
        String mDrawableName = devices[position].getImg();
        int resID = context.getResources().getIdentifier(mDrawableName , "drawable", context.getPackageName());
        holder.mImageView.setImageResource(resID);
        holder.mTextView.setText(devices[position].getName());
    }

    @Override
    public int getItemCount() {
        return devices.length;
    }

}
