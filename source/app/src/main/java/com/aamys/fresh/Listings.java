package com.aamys.fresh;

import android.util.Log;

import java.util.ArrayList;

public class Listings
{
    public String title;
    public ArrayList<ProductCard> products;
    int layout;
    public static final int HORIZONTAL_LAYOUT = 1;
    public static final int VERTICAL_LAYOUT = 2;
    public static final int HORIZONTAL_FIT_LAYOUT = 3;

    public boolean extension = false;
    private String NoitemText = null;
    public int horizontalSpanCount = 0;


    public void HorizonralScrollableLayout(){layout = HORIZONTAL_LAYOUT;}
    public void VerticalScrollableayout(){layout = VERTICAL_LAYOUT;}
    public void HorizontalFitLayout(int Coulumns){layout = HORIZONTAL_FIT_LAYOUT; horizontalSpanCount = Coulumns;}

    public Listings(String title, ArrayList<ProductCard> products)
    {
        this.title = title;
        this.products = products;
        this.layout = layout;
    }

    public Listings(String title, ArrayList<ProductCard> products, String NoitemText)
    {
        this.title = title;
        this.products = products;
        this.NoitemText = NoitemText;
    }

    public String getNoItemText()
    {
        if(this.NoitemText == null)
        {
            return "No items in " + title;
        }
        else{return this.NoitemText;}

    }

    public void LogDataChange()
    {
        Log.i("RZ_EComListing", "--Listings --- ");
        Log.i("RZ_EComListing", "Title : " + title);
        for(ProductCard pcard : products)
        {
            pcard.LogDataChange();
        }
    }


}
