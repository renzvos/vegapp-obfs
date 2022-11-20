package com.aamys.fresh;

/*
The file is licenced under MIT and reserves to Arshad Nazir on 28th July 2022 at renzvos.com
 */
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
