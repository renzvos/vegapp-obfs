package com.example.vegapp;

public class FirebaseCartItem {
    public String pid;
    public int quantity;

    public FirebaseCartItem(){}
    public FirebaseCartItem(String pid,int quantity)
    {
        this.pid= pid;
        this.quantity = quantity;
    }
}
