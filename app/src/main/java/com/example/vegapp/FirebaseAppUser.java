package com.example.vegapp;

import android.util.Log;

import java.io.Serializable;

public class FirebaseAppUser implements Serializable {
    public String name = null;
    public String phone = null;
    public String email;
    public Location Location;
    public String uid = null;
    public FirebaseCart Cart;

    public  FirebaseAppUser(){}

    public  FirebaseAppUser(String uid,String name, String email ,String phone , Location location,FirebaseCart firebaseCart)
    {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.Location = location;
        this.Cart = firebaseCart;
    }

    public void logall()
    {
        Log.i("RZFPUser", "logall: id" + uid);
        Log.i("RZFPUser", "logall: name " + name);
        Log.i("RZFPUser", "logall: email" + email);
        Log.i("RZFPUser", "logall: phone" + phone);
    }


}
