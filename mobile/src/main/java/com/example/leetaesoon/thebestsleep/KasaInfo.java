package com.example.leetaesoon.thebestsleep;

/**
 * Created by hyun on 2018-05-17.
 */

public class KasaInfo {
    private String m_id;
    private String m_token;

    public KasaInfo(){

    }
    public KasaInfo(String id,String token)
    {
        m_id = id;
        m_token = token;
    }
    public String getUserId(){return m_id;}
    public String getUserToken(){return m_token;}
    public void setUserId(String id){m_id = id;}
    public void setUserToken(String token){m_token=token;}
}
