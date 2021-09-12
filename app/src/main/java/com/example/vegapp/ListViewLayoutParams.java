package com.example.vegapp;

import java.util.ArrayList;

public class ListViewLayoutParams {
    public ArrayList<Listings> listings;
    String cartPrice;

    public ListViewLayoutParams(String cartPrice, ArrayList<Listings> listings)
    {
        this.listings = listings;
    }
}


