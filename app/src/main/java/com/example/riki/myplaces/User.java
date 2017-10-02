package com.example.riki.myplaces;

import java.io.Serializable;


public class User implements Serializable{
    public int id;
    public String name;
    public String firstName;
    public String lastName;
    public String email;
    public String phoneNumber;
    public double latitude;
    public double longitude;
    public String avatar;
    public int points;

    public User()
    {

    }

    public User(int uid, String name, String firstName, String lastName, String email, String phoneNumber, double latitude, double longitude,  int points, String avatar)
    {
        this.id = uid;
        this.name = name;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.latitude = latitude;
        this.longitude = longitude;
        this.points = points;
        this.avatar = avatar;
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
