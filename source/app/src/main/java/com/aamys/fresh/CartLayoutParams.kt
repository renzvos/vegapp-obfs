package com.aamys.fresh;

/*
The file is licenced under MIT and reserves to Arshad Nazir on 28th July 2022 at renzvos.com
 */

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.aamys.fresh.R
import org.json.JSONObject
import org.json.JSONArray
import org.json.JSONException
import java.util.ArrayList

class CartLayoutParams internal constructor(CartItems : ArrayList<LayoutParamsItems> , DeliveryCharge : String , TotalBill : String, Location :String) {
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
    var ProductName: String,
    var Price: String,
    var Quantity: String,
    var id: String,
    var iconurl: String,
    var closebuttoncolor : String
)