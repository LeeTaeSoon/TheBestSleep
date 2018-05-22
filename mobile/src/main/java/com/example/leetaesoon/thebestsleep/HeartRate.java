package com.example.leetaesoon.thebestsleep;

/**
 * Created by hyun on 2018-05-17.
 */

public class HeartRate {
    private int m_id;
    private int m_rate;
    private long m_time;
    public HeartRate(){

    }
    public HeartRate(int rate, long time)
    {// id는 autoincrement이므로 null로 넣어줘야함
        m_rate = rate;
        m_time = time;
    }
    public int getHeartRateId(){return m_id;}
    public int getHeartRateRate(){return m_rate;}


    public void setHeartRateId(int id){m_id = id;}
    public void setHeartRateRate(int rate){m_rate = rate;}

    public long getHeartRatetime() {
        return m_time;
    }

    public void setHeartRatetime(long m_time) {
        this.m_time = m_time;
    }
}
