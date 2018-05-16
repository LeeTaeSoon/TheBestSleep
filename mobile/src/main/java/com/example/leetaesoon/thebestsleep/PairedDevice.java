package com.example.leetaesoon.thebestsleep;

import java.io.Serializable;

/**
 * Created by hyun on 2018-02-20.
 */

public class PairedDevice implements Serializable {
    private String m_name;
    private String m_address;

    public PairedDevice()
    {

    }

    public PairedDevice(String address, String name)
    {
        this.m_name = name;
        this.m_address = address;
    }
    public String getName()
    {
        return this.m_name;
    }
    public String getAddress()
    {
        return this.m_address;
    }
    public void setName(String name){m_name = name;}
    public void setAddress(String address){m_address = address;}
}