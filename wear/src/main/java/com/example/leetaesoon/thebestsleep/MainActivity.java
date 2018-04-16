package com.example.leetaesoon.thebestsleep;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.wear.widget.WearableLinearLayoutManager;
import android.support.wear.widget.WearableRecyclerView;
import android.support.wear.widget.drawer.WearableNavigationDrawerView;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends WearableActivity implements WearableNavigationDrawerView.OnItemSelectedListener {

    private static final String TAG = "MainActivity";

    private WearableNavigationDrawerView mWearableNavigationDrawer;
    private WearableRecyclerView mWearableRecyclerView;
    private CustomRecyclerAdapter mCustomRecyclerAdapter;

    Device[] devices = {
            new Device("light", "조명", ""),
            new Device("speaker", "스피커", ""),
            new Device("plug", "플러그", "")
    };

    Device[] lights = {
            new Device("light", "조명1", ""),
            new Device("light", "조명2", ""),
            new Device("light", "조명3", "")
    };

    Device[] speakers = {
            new Device("speaker", "스피커1", ""),
            new Device("speaker", "스피커2", ""),
            new Device("speaker", "스피커3", "")
    };

    Device[] plugs = {
            new Device("plug", "플러그1", ""),
            new Device("plug", "플러그2", ""),
            new Device("plug", "플러그3", "")
    };

    int mSelectedDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWearableRecyclerView = (WearableRecyclerView) findViewById(R.id.deviceList);
        mWearableRecyclerView.setEdgeItemsCenteringEnabled(true);
        mWearableRecyclerView.setLayoutManager(new WearableLinearLayoutManager(this, new CustomScrollingLayoutCallback()));
        mWearableRecyclerView.setCircularScrollingGestureEnabled(true);
        mWearableRecyclerView.setBezelFraction(0.5f);
        mWearableRecyclerView.setScrollDegreesPerScreen(90);

        mSelectedDevice = 0;
        mCustomRecyclerAdapter = new CustomRecyclerAdapter(this, lights);

        mWearableRecyclerView.setAdapter(mCustomRecyclerAdapter);

        // Top Navigation Drawer
        mWearableNavigationDrawer = (WearableNavigationDrawerView) findViewById(R.id.top_navigation_drawer);
        mWearableNavigationDrawer.setAdapter(new NavigationAdapter(this));
        // Peeks navigation drawer on the top.
        mWearableNavigationDrawer.getController().peekDrawer();
        mWearableNavigationDrawer.addOnItemSelectedListener(this);

        // Enables Always-on
        setAmbientEnabled();
    }

    @Override
    public void onItemSelected(int pos) {
        Log.d(TAG, "WearableNavigationDrawerView triggered onItemSelected(): " + pos);
        mSelectedDevice = pos;

        Device[] selectedDevices;
        if (pos == 0) selectedDevices = lights;
        else if (pos == 1) selectedDevices = speakers;
        else selectedDevices = plugs;

        mCustomRecyclerAdapter = new CustomRecyclerAdapter(this, selectedDevices);
        mWearableRecyclerView.setAdapter(mCustomRecyclerAdapter);
    }

    private final class NavigationAdapter extends WearableNavigationDrawerView.WearableNavigationDrawerAdapter {

        private final Context mContext;

        public NavigationAdapter(Context context) {
            mContext = context;
        }

        @Override
        public int getCount() {
            return devices.length;
        }

        @Override
        public String getItemText(int pos) {
            return devices[pos].getName();
        }

        @Override
        public Drawable getItemDrawable(int pos) {
            String navigationIcon = devices[pos].getImg();

            int drawableNavigationIconId = getResources().getIdentifier(navigationIcon, "drawable", getPackageName());

            return mContext.getDrawable(drawableNavigationIconId);
        }
    }
}

class CustomScrollingLayoutCallback extends WearableLinearLayoutManager.LayoutCallback {
    /** How much should we scale the icon at most. */
    private static final float MAX_ICON_PROGRESS = 0.65f;

    private float mProgressToCenter;

    @Override
    public void onLayoutFinished(View child, RecyclerView parent) {

        // Figure out % progress from top to bottom
        float centerOffset = ((float) child.getHeight() / 2.0f) / (float) parent.getHeight();
        float yRelativeToCenterOffset = (child.getY() / parent.getHeight()) + centerOffset;

        // Normalize for center
        mProgressToCenter = Math.abs(0.5f - yRelativeToCenterOffset);
        // Adjust to the maximum scale
        mProgressToCenter = Math.min(mProgressToCenter, MAX_ICON_PROGRESS);

        child.setScaleX(1 - mProgressToCenter);
        child.setScaleY(1 - mProgressToCenter);
    }
}