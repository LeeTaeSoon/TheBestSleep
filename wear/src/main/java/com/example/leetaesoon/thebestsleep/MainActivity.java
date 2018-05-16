package com.example.leetaesoon.thebestsleep;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.CapabilityClient;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class MainActivity extends WearableActivity implements CapabilityClient.OnCapabilityChangedListener {

    private static final String TAG = "MainActivity";
    private static final String BODY_SENSOR_CAPABILITY_NAME = "body_sensor";
    public static final String BODY_SENSOR_MESSAGE_PATH = "/body_sensor";
    private int CONNECTION_TIME_OUT_MS = 2000;
    private String nodeId;

    TextView mTextView;

    private void setupVoiceTranscription() throws ExecutionException, InterruptedException {
        Log.d(TAG, "enter setupVoiceTranscription");
        CapabilityInfo capabilityInfo = Tasks.await(
                Wearable.getCapabilityClient(this).getCapability(
                        BODY_SENSOR_CAPABILITY_NAME, CapabilityClient.FILTER_REACHABLE));
        // capabilityInfo has the reachable nodes with the transcription capability
        updateTranscriptionCapability(capabilityInfo);

        Wearable.getCapabilityClient(this).addListener(this, BODY_SENSOR_CAPABILITY_NAME);
        Log.d(TAG, "exit setupVoiceTranscription");
    }

    @Override
    public void onCapabilityChanged(@NonNull CapabilityInfo capabilityInfo) {
        Log.d(TAG, "enter onCapabilityChanged");
        updateTranscriptionCapability(capabilityInfo);
        Log.d(TAG, "exit onCapabilityChanged");
    }

    private void updateTranscriptionCapability(CapabilityInfo capabilityInfo) {
        Log.d(TAG, "enter updateTranscriptionCapability");
        Set<Node> connectedNodes = capabilityInfo.getNodes();

        nodeId = pickBestNodeId(connectedNodes);
        Log.d(TAG, "picked nodeid : " + nodeId);
        Log.d(TAG, "exit updateTranscriptionCapability");
    }

    private String pickBestNodeId(Set<Node> nodes) {
        String bestNodeId = null;
        // Find a nearby node or pick one arbitrarily
        for (Node node : nodes) {
            if (node.isNearby()) {
                return node.getId();
            }
            bestNodeId = node.getId();
        }
        return bestNodeId;
    }

    private void requestTranscription(byte[] sensorData) {
        Log.d(TAG, "enter requestTranscription");
        if (nodeId != null) {
            Task<Integer> sendTask =
                    Wearable.getMessageClient(this).sendMessage(nodeId, BODY_SENSOR_MESSAGE_PATH, sensorData);
            // You can add success and/or failure listeners,
            // Or you can call Tasks.await() and catch ExecutionException
            //sendTask.addOnSuccessListener(...);
            //sendTask.addOnFailureListener(...);

            Log.d(TAG, "sendTask : " + sendTask.toString());
        } else {
            // Unable to retrieve node with transcription capability
            Log.d(TAG, "Unable to retrieve node with transcription capability");
        }
        Log.d(TAG, "exit requestTranscription");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = findViewById(R.id.text);
        mTextView.setText("hello");

        final String data = "string data";

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    setupVoiceTranscription();
                    requestTranscription(data.getBytes());
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        // Enables Always-on
        setAmbientEnabled();
    }
}