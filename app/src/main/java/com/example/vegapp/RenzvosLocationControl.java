package com.example.vegapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class RenzvosLocationControl {
    FusedLocationProviderClient mFusedLocationClient;
    Activity activity;
    LocationCallbacks callbacks;
    int PERMISSION_ID = 44;
    String TAG = "RZLOCATION";


    public RenzvosLocationControl(Activity activity)
    {
        this.activity = activity;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
    }

    @SuppressLint("MissingPermission")
    public void getLastLocation(LocationCallbacks callbacks)
    {   this.callbacks = callbacks;
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                if (location == null) {
                                    requestNewLocationData();
                                } else {
                                    requestNewLocationData();
                                    Log.i(TAG, "Got Location : " + location.getLatitude() + "  " + location.getLongitude());
                                    callbacks.OnLocationResult(location);
                                }
                            }
                        }
                );
            } else {
                callbacks.ifLocationOff();
                Toast.makeText(activity, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                activity.startActivity(intent);
            }
        } else {
            Log.i(TAG, "getLastLocation: No permission requesting permission");
            callbacks.ifNoPermission();
            requestPermissions();
        }


    }

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestPermissions() {
        Log.i(TAG, "getLastLocation: No permission requesting permission");
        ActivityCompat.requestPermissions(
                activity,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
        Log.i(TAG, "getLastLocation: Permission requesting complete");
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }


    public void HandlePermissionResult(int requestCode, String[] permissions, int[] grantResults, LocationCallbacks callbacks ) {
        Log.i(TAG, "getLastLocation: Handeling Permission Result");
        if (requestCode == PERMISSION_ID) {
            Log.i(TAG, "getLastLocation: Permission result if for location");
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "getLastLocation: Permission has been graneted ");
                callbacks.ifPermitted();
            }else {Log.i(TAG, "getLastLocation:Permission not granted");
                callbacks.ifNotPermitted();}

        }else {callbacks.ifNotPermitted();}
    }







    @SuppressLint("MissingPermission")
    private void requestNewLocationData(){

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            Log.i(TAG, "Got Location "+ mLastLocation.getLatitude() + mLastLocation.getLongitude());
            callbacks.OnLocationResult(mLastLocation);
        }
    };


    public interface LocationCallbacks {
        public void ifNoPermission();
        public void ifLocationOff();
        public void ifPermitted();
        public void ifNotPermitted();
        public void OnLocationResult(Location location);
    }

}
