package com.aamys.fresh

/*
The file is licenced under MIT and reserves to Arshad Nazir on 28th July 2022 at renzvos.com
 */


import android.provider.Telephony
import kotlin.math.sign

class CartClass (DeliveryAmount : Float){
    public var items = ArrayList<CartItem>()
    public var DeliveryAmount = DeliveryAmount

    constructor(item: ArrayList<CartItem>) : this(0f)
    {
        items = item

    }

    constructor(item: ArrayList<CartItem>, delivery : Float): this(delivery)
    {
        items = item
    }

    constructor(item: ArrayList<CartItem>, distance :Float , Rateperdistance :Float) :this(distance * Rateperdistance)
    {
        items = item
    }


    public fun AddItem(item  :CartItem)
    {
        items.add(item)
    }

    public fun RemoveItem(item: CartItem)
    {
        items.remove(item)
    }

    public fun RemoveItem(index :Int)
    {
        items.removeAt(index)
    }

    public fun SumOfItemPrices():Int
    {
        var sum = 0
        for(item in items)
        {
            sum = sum + (item.Price * item.Quantity)
        }
        return sum
    }

    public fun CalculateTotalBill():Float
    {
        return  SumOfItemPrices() + DeliveryAmount
    }




}