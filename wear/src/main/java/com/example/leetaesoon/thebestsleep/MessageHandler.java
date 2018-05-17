package com.example.leetaesoon.thebestsleep;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.CapabilityClient;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.util.Set;
import java.util.concurrent.ExecutionException;

public class MessageHandler implements CapabilityClient.OnCapabilityChangedListener {
    private static final String TAG = "MessageHandler";
    private static final String BODY_SENSOR_CAPABILITY_NAME = "body_sensor";
    public static final String BODY_SENSOR_MESSAGE_PATH = "/body_sensor";
    private String nodeId;
    private Context context;

    public MessageHandler(Context context) throws ExecutionException, InterruptedException {
        this.context = context;
        setupBodySensor();
    }

    private void setupBodySensor() throws ExecutionException, InterruptedException {
        Log.d(TAG, "enter setupBodySensor");
        CapabilityInfo capabilityInfo = Tasks.await(
                Wearable.getCapabilityClient(context).getCapability(
                        BODY_SENSOR_CAPABILITY_NAME, CapabilityClient.FILTER_REACHABLE));
        // capabilityInfo has the reachable nodes with the transcription capability
        updateBodySensorCapability(capabilityInfo);

        Wearable.getCapabilityClient(context).addListener(this, BODY_SENSOR_CAPABILITY_NAME);
        Log.d(TAG, "exit setupBodySensor");
    }

    @Override
    public void onCapabilityChanged(@NonNull CapabilityInfo capabilityInfo) {
        Log.d(TAG, "enter onCapabilityChanged");
        updateBodySensorCapability(capabilityInfo);
        Log.d(TAG, "exit onCapabilityChanged");
    }

    private void updateBodySensorCapability(CapabilityInfo capabilityInfo) {
        Log.d(TAG, "enter updateBodySensorCapability");
        Set<Node> connectedNodes = capabilityInfo.getNodes();

        nodeId = pickBestNodeId(connectedNodes);
        Log.d(TAG, "picked nodeid : " + nodeId);
        Log.d(TAG, "exit updateBodySensorCapability");
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

    public void requestSendData(byte[] sensorData) {
        //Log.d(TAG, "enter requestSendData");
        if (nodeId != null) {
            Task<Integer> sendTask =
                    Wearable.getMessageClient(context).sendMessage(nodeId, BODY_SENSOR_MESSAGE_PATH, sensorData);
            // You can add success and/or failure listeners,
            // Or you can call Tasks.await() and catch ExecutionException
            //sendTask.addOnSuccessListener(...);
            //sendTask.addOnFailureListener(...);

            //Log.d(TAG, "sendTask : " + sendTask.toString());
        } else {
            // Unable to retrieve node with transcription capability
            Log.d(TAG, "Unable to retrieve node with body sensor capability");
        }
        //Log.d(TAG, "exit requestSendData");
    }
}
