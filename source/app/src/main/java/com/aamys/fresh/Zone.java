package com.aamys.fresh;

import android.util.Log;

public class Zone {
    public String name;
    public String zoneid;
    public double lat;
    public double log;
    public double radius;

    public Zone()
    {}


    public Zone(String id, double lat,double log, double radius)
    {
        this.zoneid = id;
        this.lat = lat;
        this.log = log;
        this.radius = radius;

    }
    public void logall()
    {
        if (name!= null) Log.i("RZLOCATION", "logall: " + name);
        Log.i("RZLOCATION", "logall: " + log);
        Log.i("RZLOCATION", "logall: " + lat);
        Log.i("RZLOCATION", "logall: " + radius);
    }
}
