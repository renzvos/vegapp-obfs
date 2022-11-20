package com.aamys.fresh;

import java.util.ArrayList;

public class ListViewLayoutParams {
    public ArrayList<Listings> listings;


    public ListViewLayoutParams( ArrayList<Listings> listings)
    {
        this.listings = listings;
    }

    public static ListViewLayoutParams EmptySet()
    {
        ListViewLayoutParams params = new ListViewLayoutParams(new ArrayList<>());
        return params;
    }
}


