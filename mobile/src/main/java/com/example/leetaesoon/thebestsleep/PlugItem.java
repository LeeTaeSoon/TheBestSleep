package com.example.leetaesoon.thebestsleep;

import java.io.Serializable;

public class PlugItem implements Serializable {
    String deviceID;

    public PlugItem(String deviceID) {
        this.deviceID = deviceID;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }
}
