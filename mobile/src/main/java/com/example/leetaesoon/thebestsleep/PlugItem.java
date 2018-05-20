package com.example.leetaesoon.thebestsleep;

import java.io.Serializable;

public class PlugItem implements Serializable {
    private String m_url;
    private String m_id;
    private String m_alias;
    private String m_userId;
    public PlugItem(){}
    public PlugItem(String id, String url, String alias, String userId)
    {
        this.m_id = id;
        this.m_url = url;
        this.m_alias = alias;
        this.m_userId = userId;
    }
    public String getdeviceId() {return this.m_id;}
    public String geturl()
    {
        return this.m_url;
    }
    public String getalias() {return this.m_alias;}
    public String getuserId() {return this.m_userId;}

    public void setdeviceId(String id){m_id = id;}
    public void seturl(String url){m_url = url;}
    public void setalias(String alias){m_alias = alias;}
    public void setuserId(String userId){m_userId = userId;}

}
