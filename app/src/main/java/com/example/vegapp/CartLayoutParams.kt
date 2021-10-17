package com.example.vegapp

import java.util.ArrayList

class CartLayoutParams internal constructor(CartItems : ArrayList<LayoutParamsItems>, DeliveryCharge : String, TotalBill : String, Location :String) {
    var cartitems: ArrayList<LayoutParamsItems> = CartItems
    var DeliveryCharge: String? = DeliveryCharge
    var TotalBill: String? = TotalBill
    var leftbutton = false
    var leftbuttondrawable = 0

     fun CalculateCart()
    {

    }

    fun CalculatePriceSum():Int
    {
        var sum  = 0;
        for(item in cartitems)
        {
            sum = sum + Integer.valueOf(item.Price);
        }
        return sum
    }


    fun setLeftButton() {
        leftbutton = true
    }









}

 class LayoutParamsItems(
     productName: String,
     var ProductName: String,
     var Price: String,
     var Quantity: String,
     var id: String,
     var iconurl: String
 )