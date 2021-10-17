package com.example.vegapp;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ListViewLayoutParams {
    public ArrayList<Listings> listings;


    public ListViewLayoutParams( ArrayList<Listings> listings)
    {
        this.listings = listings;
    }
}


