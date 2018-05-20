package com.example.leetaesoon.thebestsleep;

/**
 * Created by hyun on 2018-05-17.
 */

public class HeartRate {
    private int m_id;
    private int m_rate;

    public HeartRate(){

    }
    public HeartRate(int rate)
    {// id는 autoincrement이므로 null로 넣어줘야함
        m_rate = rate;
    }
    public int getHeartRateId(){return m_id;}
    public int getHeartRateRate(){return m_rate;}


    public void setHeartRateId(int id){m_id = id;}
    public void setHeartRateRate(int rate){m_rate = rate;}

}
