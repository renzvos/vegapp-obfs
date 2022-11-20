package com.aamys.fresh;

import android.util.Log;

import java.util.ArrayList;

public class PviewLayoutParams {
    public String HeadingThemeColor;
    public ArrayList<String> imageurls;
    public String pname;
    public String old_rate;
    public String new_rate;
    public String Description;
    public String punit;

    public PviewLayoutParams(ArrayList<String> imageurls, String pname, String old_rate, String new_rate,String Description,String unit)
    {
        this.imageurls = imageurls;
        this.pname = pname;
        this.old_rate = old_rate;
        this.new_rate = new_rate;
        this.Description = Description;
        this.punit = unit;
    }

    public PviewLayoutParams(int LoadingimagesSize, String pname, String old_rate, String new_rate,String Description,String unit)
    {
        this.imageurls = new ArrayList<>();
        for(int i = 0 ; i < LoadingimagesSize ; i++)
        {
            imageurls.add("asda");
            //TODO Loading
        }
        this.pname = pname;
        this.old_rate = old_rate;
        this.new_rate = new_rate;
        this.Description = Description;
        this.punit = unit;

    }

    public void Logall()
    {
        for(String url : imageurls)
        {
            Log.i("RZ_ProductViewer", "Logall: Images " + imageurls);
        }
        Log.i("RZ_ProductViewer", "Logall: " + pname);
        Log.i("RZ_ProductViewer", "Logall: " + old_rate);
        Log.i("RZ_ProductViewer", "Logall: " + new_rate);
        Log.i("RZ_ProductViewer", "Logall: " + Description);
        Log.i("RZ_ProductViewer", "Logall: " + punit);


    }




}
