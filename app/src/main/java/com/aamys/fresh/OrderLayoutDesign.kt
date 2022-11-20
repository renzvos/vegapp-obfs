package com.renzvos.ecommerceorderview

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.media.Image
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aamys.fresh.R
import com.google.android.material.progressindicator.LinearProgressIndicator
import org.w3c.dom.Text
import java.util.*
import kotlin.collections.ArrayList

const val ORDERLAYOUT_1 = 1


class OrderLayoutDesign {
    var ActionBarTitle : String = "Order Status"
    var ActionBarColor : String = "#000000"
    var ActionBarLeftButton = false
    var StatusText : String = "Loading"
    var StatusColor : String = "#000000"
    var RightSideNote : String = ""
    var HeaderBackgroungImage : Drawable? = null
    var CardStrongText : String = ""
    var CardLightText : String = ""
    var products : ArrayList<CartitemParams> = ArrayList()
    var OrderStatusImage : Drawable? = null
    var ProceedButtonText : String? = null
    var ProceedButtonColor : String? = null

    //designone
    var Design : Int? = null
    private var Status : TextView? = null
    private var Datetime : TextView? = null
    private var backgroundImage : ImageView?= null
    private var mainBill : TextView? = null
    private var subBill : TextView?=null
    private var listing : RecyclerView?=null
    private var OrderStatus : ImageView? = null
    private var ProceedButton : Button? = null
    private var Loader : LinearProgressIndicator? = null
    private var TitleBox : TextView? = null
    private var Topbar : Toolbar? = null
    private var BackButton : ImageView? = null

    var simpleLayoutCallback:SimpleLayoutCallback? = null
    var cartproductsadapter:cartproductsadapter?=null

    fun SimpleOrderLayout(simpleLayoutCallback: SimpleLayoutCallback)
    {
        Design = ORDERLAYOUT_1
        this.simpleLayoutCallback = simpleLayoutCallback
    }

    fun UpdateSimpleOrderLayout()
    {
        TitleBox?.setText(ActionBarTitle)
        Topbar?.setBackgroundColor(Color.parseColor(ActionBarColor))

        if (ActionBarLeftButton)
        {BackButton?.visibility = View.VISIBLE
        BackButton?.setOnClickListener(View.OnClickListener { simpleLayoutCallback?.OnClickLeftButton() })}

        this.Loader?.visibility = View.INVISIBLE
        this.Status?.setText(StatusText)
        this.Status?.setTextColor(Color.parseColor(StatusColor))
        this.Datetime?.setText(RightSideNote)
        this.backgroundImage?.setImageDrawable(HeaderBackgroungImage)
        this.mainBill?.setText(CardStrongText)
        this.subBill?.setText(CardLightText)
        this.OrderStatus?.setImageDrawable(OrderStatusImage)
        this.cartproductsadapter?.Update(products)
        this.cartproductsadapter?.notifyDataSetChanged()
        if(ProceedButtonText != null)
        {
            ProceedButton?.visibility = View.VISIBLE
            ProceedButton?.setText(ProceedButtonText)
            ProceedButton?.setOnClickListener(View.OnClickListener { simpleLayoutCallback?.ProceedButton() })
        }
        if(ProceedButtonColor != null)
        {
            ProceedButton!!.backgroundTintList = (ColorStateList(arrayOf(intArrayOf()), intArrayOf(Color.parseColor(ProceedButtonColor))))
            ProceedButton!!.backgroundTintMode = PorterDuff.Mode.SRC_OVER
        }
    }

    fun Render(view : View)
    {
        if(Design == 1)
        {
            BackButton  = view?.findViewById<ImageView>(R.id.titleleftbutton)
            TitleBox  = view.findViewById<TextView>(R.id.toolbarTitle)
            Topbar = view?.findViewById<Toolbar>(R.id.customtoolbar)
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
            OrderStatus = view.findViewById(R.id.orderstatusimage)
            ProceedButton = view.findViewById(R.id.proceedbutton)
            Loader = view.findViewById(R.id.loader)
        }
    }



     interface SimpleLayoutCallback
    {
        fun ProductClicked(pparams : CartitemParams)
        fun OnClickLeftButton()
        fun ProceedButton()
    }

}