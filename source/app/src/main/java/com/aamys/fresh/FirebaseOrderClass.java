package com.aamys.fresh;

/*
The file is licenced under MIT and reserves to Arshad Nazir on 28th July 2022 at renzvos.com
 */

import com.aamys.fresh.Location;import java.io.Serializable;
import java.util.ArrayList;

public class FirebaseOrderClass implements Serializable {
    public String timestamp;
    public String uid;
    public boolean payment = false;
    public boolean approved = false;
    public boolean intransit = false;
    public boolean delivered = false;
    public boolean cancelled = false;
    public FirebaseAppUser appUser;
    public float Amount;
    public ArrayList<FirebaseOrderItem> Items;
    public Location delivery;
    public float deliverycharge;
    public String phone;


}
