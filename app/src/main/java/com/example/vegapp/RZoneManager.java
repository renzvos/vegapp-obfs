package com.example.vegapp;

import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import java.util.ArrayList;

public class RZoneManager {
    Location current;
    ArrayList<Zone> zones;
    String TAG = "RZLOCATION";


    public void UpdateZones(ArrayList<Zone> zones)
    {
        this.zones = zones;
    }

    public void UpdateCurrentLocation(Location current)
    {
        this.current = current;
    }

    public boolean isInsideZones()
    {
        if(current == null)
        {
            Log.i(TAG, "isInsideZones: Location not set");
            return  false;
        }
        else {

            for (int i = 0; i < zones.size(); i++)
            {
                Log.i(TAG, "isInsideZones: Checking Zone "  + i);
                if(CheckInsideZone(zones.get(i)))
                {
                    Log.i(TAG, "is Inside");
                    return true;
                }
            }
            Log.i(TAG, "is outside");
            return false;
        }
    }



    public boolean CheckInsideZone(Zone zone)
    {
        Location zonepoint = new Location(LocationManager.GPS_PROVIDER);
        zonepoint.setLongitude(zone.log);
        zonepoint.setLatitude(zone.lat);
        float distance = current.distanceTo(zonepoint);
        if(distance <= zone.radius)
        {
            Log.i(TAG, "isInsideZones: Distance "  + distance);
            Log.i(TAG, "isInsideZones: Zone "  + zone);
            return true;
        }
        else
        {
            Log.i(TAG, "isInsideZones: Distance "  + distance);
            Log.i(TAG, "isInsideZones: Zone "  + zone);
            return false;
        }
    }

}
