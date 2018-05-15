package com.example.leetaesoon.thebestsleep;

import java.io.Serializable;

public class LampItem implements Serializable {
    String deviceID;
    String MACaddr;

    public LampItem(String deviceID, String MACaddr) {
        this.deviceID = deviceID;
        this.MACaddr = MACaddr;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public String getMACaddr() {
        return MACaddr;
    }

    public void setMACaddr(String MACaddr) {
        this.MACaddr = MACaddr;
    }
}
