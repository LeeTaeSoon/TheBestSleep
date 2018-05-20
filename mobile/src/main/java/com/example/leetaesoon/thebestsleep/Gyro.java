package com.example.leetaesoon.thebestsleep;

/**
 * Created by hyun on 2018-05-17.
 */

public class Gyro {
    private int m_id;
    private String m_time;
    private double m_x;
    private double m_y;
    private double m_z;

    public Gyro(){

    }
    public Gyro(String time, double x, double y, double z)
    {// id는 autoincrement이므로 null로 넣어줘야함
        m_time = time;
        m_x = x;
        m_y = y;
        m_z = z;
    }
    public int getGyroId(){return m_id;}
    public String getGyroTime(){return m_time;}
    public double getGyroX(){return m_x;}
    public double getGyroY(){return m_y;}
    public double getGyroZ(){return m_z;}

    public void setGyroId(int id){m_id = id;}
    public void setGyroTime(String time){m_time = time;}
    public void setGyroX(double x){m_x = x;}
    public void setGyroY(double y){m_y = y;}
    public void setGyroZ(double z){m_z = z;}
}
