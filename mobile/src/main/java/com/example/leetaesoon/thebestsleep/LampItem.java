package com.example.leetaesoon.thebestsleep;

import java.io.Serializable;

public class LampItem implements Serializable {
    String m_deviceID;
    String m_name;
    int m_R;
    int m_G;
    int m_B;
    int m_A;
    boolean m_control;
    public LampItem(){}

    public LampItem(String deviceID, String name,int R,int G, int B,int A, boolean control) {
        m_deviceID = deviceID;
        m_name = name;
        m_R = R;
        m_G = G;
        m_B = B;
        m_A = A;
        m_control = control;
    }


    public String getDeviceID() {
        return m_deviceID;
    }
    public String getLampName(){return m_name;}
    public int getLampR(){return m_R;}
    public int getLampG(){return m_G;}
    public int getLampB(){return m_B;}
    public int getLampA(){return m_A;}
    public boolean getLampControl(){return m_control;}

    public void setDeviceID(String deviceID) {
        m_deviceID = deviceID;
    }
    public void setLampName(String name){m_name = name;}
    public void setLampR(int R){m_R = R;}
    public void setLampG(int G){m_G = G;}
    public void setLampB(int B){m_B = B;}
    public void setLampA(int A){m_A = A;}
    public void setLampControl(boolean control){m_control = control;}
}
