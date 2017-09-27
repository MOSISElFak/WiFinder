package com.example.riki.myplaces;

import java.io.Serializable;

public class WiFi  implements Serializable
{
    public int id;
    public String name;
    public String password;
    public double latitude;
    public double longitude;
    public int createdBy;

    public WiFi()
    {

    }

    public WiFi(int id, String name, String password, double latitude, double longitude, int createdBy)
    {
        this.id = id;
        this.name = name;
        this.password = password;
        this.latitude = latitude;
        this.longitude = longitude;
        this.createdBy = createdBy;
    }
}
