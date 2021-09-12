package com.example.vegapp;

import java.io.Serializable;

public class Location implements Serializable {
    public String lat ;
    public String log;
    public String landmark;
    public String street;
    public String city;
    public String district;
    public String pincode;

    public Location()
    {}

    public Location(String lat, String log, String landmark, String street, String city, String district, String pincode)
    {
        this.lat = lat;
        this.log = log;
        this.landmark = landmark;
        this.street = street;
        this.city = city;
        this.district = district;
        this.pincode = pincode;
    }

}
