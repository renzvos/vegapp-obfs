package com.example.vegapp;

import java.util.ArrayList;

public class FirebaseOrderClass {
    public String timestamp;
    public boolean payment = false;
    public boolean approved = false;
    public boolean intransit = false;
    public String uid;
    public FirebaseAppUser appUser;
    public int Amount;
    public ArrayList<FirebaseCartItem> cartItems;
    public Location location;
    public String phone;


}
