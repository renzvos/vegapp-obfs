package com.aamys.fresh;

import android.util.Log;

import java.util.ArrayList;

public class RZEcomLayoutParams {
    public ArrayList<String> bannerUrls;
    public ArrayList<Listings> Content;

    public RZEcomLayoutParams(ArrayList<String> bannerUrls, ArrayList<Listings> content) {
        this.bannerUrls = bannerUrls;
        this.Content  = content;
    }

    public void LogDataChange()
    {
        Log.i("RZ_EComMain", "-LogDataChange: ");
        Log.i("RZ_EComMain", "-----Banner URLs -----: ");
        for(String banurl : bannerUrls)
        {
            Log.i("RZ_EComMain", banurl);
        }
        for(Listings listing : Content)
        {
            listing.LogDataChange();
        }

    }
}
