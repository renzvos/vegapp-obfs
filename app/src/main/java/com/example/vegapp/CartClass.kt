package com.example.vegapp

class CartClass (DeliveryAmount :Int){
    public var items = ArrayList<CartItem>()
    public var DeliveryAmount = DeliveryAmount

    constructor(item: ArrayList<CartItem>) : this(0)
    {
        items = item

    }

    constructor(item: ArrayList<CartItem>, delivery : Int): this(delivery)
    {
        items = item
    }

    constructor(item: ArrayList<CartItem>, distance :Int, Rateperdistance :Int) :this(distance * Rateperdistance)
    {
        items = item
    }


    public fun AddItem(item  : CartItem)
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

    public fun CalculateTotalBill():Int
    {
        return  SumOfItemPrices() + DeliveryAmount
    }




}