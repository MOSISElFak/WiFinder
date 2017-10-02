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
    public String user;

    public WiFi()
    {

    }

    public WiFi(int id, String name, String password, double latitude, double longitude, int createdBy, String user)
    {
        this.id = id;
        this.name = name;
        this.password = password;
        this.latitude = latitude;
        this.longitude = longitude;
        this.createdBy = createdBy;
        this.user = user;
    }

    @Override
    public boolean equals (Object object) {
        boolean result = false;
        if (object == null || object.equals("")) {
            result = false;
        } else {
            String name = String.valueOf(object);
            if (this.name.equals(name)) {
                result = true;
            }
        }
        return result;
    }
}
