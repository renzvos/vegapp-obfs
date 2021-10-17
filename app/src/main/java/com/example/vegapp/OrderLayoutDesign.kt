package com.renzvos.ecommerceorderview

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vegapp.R
import java.util.*

const val ORDERLAYOUT_1 = 1


class OrderLayoutDesign {
    var Design : Int? = null
    var Status : TextView? = null
    var Datetime : TextView? = null
    var backgroundImage : ImageView?= null
    var mainBill : TextView? = null
    var subBill : TextView?=null
    var listing : RecyclerView?=null

    var simpleLayoutCallback:SimpleLayoutCallback? = null
    var cartproductsadapter:cartproductsadapter?=null

    fun SimpleOrderLayout(simpleLayoutCallback: SimpleLayoutCallback)
    {
        Design = ORDERLAYOUT_1
        this.simpleLayoutCallback = simpleLayoutCallback
    }

    fun UpdateSimpleOrderLayout(Status : String,StatusColor : String ,TimeStamp : String, BackgroundImage : Drawable, MainBill : String, SubBill : String)
    {
        this.Status?.setText(Status)
        this.Status?.setTextColor(Color.parseColor(StatusColor))
        this.Datetime?.setText(TimeStamp)
        this.backgroundImage?.setImageDrawable(BackgroundImage)
        this.mainBill?.setText(MainBill)
        this.subBill?.setText(SubBill)
    }

    fun Render(view : View)
    {
        if(Design == 1)
        {
            Status = view.findViewById(R.id.orderstatus)
            Datetime = view.findViewById(R.id.timestamp)
            backgroundImage = view.findViewById(R.id.orderimage)
            mainBill = view.findViewById(R.id.totalbill)
            subBill = view.findViewById(R.id.subbill)
            listing = view.findViewById<RecyclerView>(R.id.cartlist)
            cartproductsadapter = cartproductsadapter(view.context,simpleLayoutCallback!!)
            listing?.setHasFixedSize(true)
            listing?.setLayoutManager(LinearLayoutManager(view.context))
            listing?.setAdapter(cartproductsadapter)
        }
    }



    public interface SimpleLayoutCallback
    {
        fun ProductClicked(pparams : CartitemParams)
        fun OnClickLeftButton()
    }

}