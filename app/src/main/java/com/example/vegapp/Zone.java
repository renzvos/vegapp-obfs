package com.example.vegapp;

import android.util.Log;

public class Zone {
    public double lat;
    public double log;
    public double radius;

    public Zone()
    {}


    public Zone(double lat,double log, double radius)
    {
        this.lat = lat;
        this.log = log;
        this.radius = radius;

    }
    public void logall()
    {
        Log.i("RZLOCATION", "logall: " + log);
        Log.i("RZLOCATION", "logall: " + lat);
        Log.i("RZLOCATION", "logall: " + radius);
    }
}
