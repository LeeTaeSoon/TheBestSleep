package com.example.leetaesoon.thebestsleep;

import java.io.Serializable;

public class PlugItem implements Serializable {
    String deviceID;
    private String m_url;
    private String m_id;
    private String m_alias;
    private String m_status;
    public PlugItem(){}
    public PlugItem(String url, String id, String alias, String status)
    {
        this.m_url = url;
        this.m_id = id;
        this.m_alias = alias;
        this.m_status = status;
    }
    public String geturl()
    {
        return this.m_url;
    }
    public String getid() {return this.m_id;}
    public String getalias() {return this.m_alias;}
    public String getstatus() {return this.m_status;}
    public void seturl(String url){m_url = url;}
    public void setid(String id){m_id = id;}
    public void setalias(String alias){m_alias = alias;}
    public void setstatus(String status){m_status = status;}



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
