package com.aamys.fresh;

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

    public Zone getInsideZones()
    {
        if(current == null)
        {
            Log.i(TAG, "isInsideZones: Location not set");
            return  null;
        }
        else {

            for (int i = 0; i < zones.size(); i++)
            {
                if(zones.get(i).name == null)
                    {Log.i(TAG, "isInsideZones: Checking Zone "  + i);}
                else{
                    Log.i(TAG, "isInsideZones: Checking Zone "  + zones.get(i).name);
                }
                if(CheckInsideZone(zones.get(i)))
                {
                    Log.i(TAG, "is Inside");
                    return zones.get(i);
                }
            }
            Log.i(TAG, "is outside");
            return null;
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
            Log.i(TAG, "isInsideZones: Zone "  + zone.radius);
            return true;
        }
        else
        {
            Log.i(TAG, "isInsideZones: Distance from Phone "  + distance);
            Log.i(TAG, "isInsideZones: Zone Allotted Radius"  + zone.radius);
            return false;
        }
    }

}
