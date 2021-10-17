package com.example.vegapp;

import android.util.Log;

import java.util.ArrayList;

public class Listings
{
    public String title;
    public ArrayList<ProductCard> products;
    int layout;
    public static final int HORIZONTAL_LAYOUT = 1;
    public static final int VERTICAL_LAYOUT = 2;
    public boolean extension = false;


    public Listings(String title, ArrayList<ProductCard> products, int layout)
    {
        this.title = title;
        this.products = products;
        this.layout = layout;
    }

    public void LogDataChange()
    {
        Log.i("RZEcomLisiting", "--Listings --- ");
        Log.i("RZEcomLisiting", "Title : " + title);
        for(ProductCard pcard : products)
        {
            pcard.LogDataChange();
        }
    }
}
