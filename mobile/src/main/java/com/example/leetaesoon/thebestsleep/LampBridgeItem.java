package com.example.leetaesoon.thebestsleep;

/**
 * Created by hyun on 2018-05-17.
 */

public class LampBridgeItem {
    private String m_username;
    private String m_ip;


    public LampBridgeItem(){

    }
    public LampBridgeItem(String username, String ip)
    {
        m_username = username;
        m_ip = ip;
    }

    public String getUserName(){return m_username;}
    public String getIp(){return m_ip;}

    public void setUserName(String username){m_username = username;}
    public void setIp(String ip){m_ip = ip;}
}
