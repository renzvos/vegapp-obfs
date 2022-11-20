package com.aamys.fresh;

import android.util.Log;

import java.util.ArrayList;

public class ProductCard {

    public boolean feature;
    public String pid;
    public String productname;
    public String oldrate;
    public String newrate;
    public String message;
    public String unit;
    public ArrayList<String> imagelinks;


    public ProductCard(String id, String productname , String oldrate , String newrate, String message , String unit,ArrayList<String> imagelinks)
    {
        this.feature = true;
        this.pid = id;
        this.productname = productname;
        this.oldrate = oldrate;
        this.newrate = newrate;
        this.imagelinks = imagelinks;
        this.message = message;
        this.unit = unit;
    }

    public ProductCard(String id, String productname , String newrate,String unit, ArrayList<String> imagelinks)
    {
        this.pid = id;
        this.productname = productname;
        this.newrate = newrate;
        this.imagelinks = imagelinks;
        this.unit = unit;
    }





    public void LogDataChange()
    {
        Log.i("RZ_EComListing", "Product ID " + pid);
        Log.i("RZ_EComListing", "Product Name " + productname);
        Log.i("RZ_EComListing", "Product SubRate " + oldrate);
        Log.i("RZ_EComListing", "Product Price " + newrate);
        for (String imagelink : imagelinks)
        {
            Log.i("RZ_EComListing", "Product Image Link " + imagelink);
        }


    }

}
