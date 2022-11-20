package com.aamys.fresh;

/*
The file is licenced under MIT and reserves to Arshad Nazir on 28th July 2022 at renzvos.com
 */

import android.util.Log;

import java.util.ArrayList;

public class ProductDt {
    public boolean productEnable;
    public String productName;
    public int productPrice;
    public String productLink;
    public int productOfferPrice;
    public String productUnit;
    public int productAvailability;
    public String productDescription;

    public ProductDt()
    {}

    public void logall()
    {
        Log.i("RZ", "logall: " + productName);
        Log.i("RZ", "logall: " + productPrice);
        Log.i("RZ", "logall: " + productLink.toString());
        Log.i("RZ", "logall: " + productOfferPrice);
        Log.i("RZ", "logall: " + productUnit);
        Log.i("RZ", "logall: " + productAvailability);
        Log.i("RZ", "logall: " + productDescription);

    }

}
