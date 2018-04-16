package com.example.leetaesoon.thebestsleep;

public class Device {
    private String img;
    private String name;
    private String mac;

    public Device() {
        img = "light";
        name = "device";
        mac = "00:00:00:00:00:00";
    }

    public Device(String img, String name, String mac) {
        this.img = img;
        this.name = name;
        this.mac = mac;
    }

    public String getImg() { return img; }
    public String getName() { return name; }
    public String getMac() { return mac; }

    public void setImg(String img) { this.img = img; }
    public void setName(String name) { this. name = name; }
    public void setMac(String mac) { this.mac = mac; }
}
