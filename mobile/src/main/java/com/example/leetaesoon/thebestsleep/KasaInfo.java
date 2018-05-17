package com.example.leetaesoon.thebestsleep;

/**
 * Created by hyun on 2018-05-17.
 */

public class KasaInfo {
    private String m_id;
    private String m_password;
    public KasaInfo(){

    }
    public KasaInfo(String id,String password)
    {
        m_id = id;
        m_password = password;
    }
    public String getUserId(){return m_id;}
    public String getUserPassword(){return m_password;}
    public void setUserId(String id){m_id = id;}
    public void setUserPassword(String password){m_password = password;}
}
