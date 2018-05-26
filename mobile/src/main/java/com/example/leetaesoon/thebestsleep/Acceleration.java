package com.example.leetaesoon.thebestsleep;

/**
 * Created by hyun on 2018-05-17.
 */

public class Acceleration {
    private int m_id;
    private long m_time;
    private double m_x;
    private double m_y;
    private double m_z;
    private double m_scalar;

    public Acceleration(){

    }
    public Acceleration(long time, double x, double y, double z, double scalar)
    {// id는 autoincrement이므로 null로 넣어줘야함
        m_time = time;
        m_x = x;
        m_y = y;
        m_z = z;
        m_scalar = scalar;
    }
    public int getAccelerationId(){return m_id;}
    public long getAccelerationTime(){return m_time;}
    public double getAccelerationX(){return m_x;}
    public double getAccelerationY(){return m_y;}
    public double getAccelerationZ(){return m_z;}
    public double getAccelerationSCALAR(){return m_scalar;}

    public void setAccelerationId(int id){m_id = id;}
    public void setAccelerationTime(long time){m_time = time;}
    public void setAccelerationX(double x){m_x = x;}
    public void setAccelerationY(double y){m_y = y;}
    public void setAccelerationZ(double z){m_z = z;}
    public void setAccelerationSCALAR(double scalar){ m_scalar=scalar;}
}
