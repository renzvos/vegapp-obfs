package com.example.vegapp

import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class EcommerceCart (activity : AppCompatActivity) {
    val activity = activity;
    var context : Context =activity.applicationContext;
    var staticLP :CartStaticLP? = null

    var adapter : cartproductsadapter? = null;
    var subbill : TextView? = null
    var TotalBill : TextView? = null
    var loader : ProgressBar? = null
    var PayButton  : Button?= null
    var PayImage : ImageView?=null

    init {
        context = activity.applicationContext
    }

    constructor(slp: CartStaticLP , activity: AppCompatActivity) : this(activity) {
       staticLP = slp
    }

    fun ProduceLayout(cartClicks: CartClicks)
    {

        activity.setContentView(R.layout.cart);
        subbill = activity.findViewById<TextView>(R.id.subbill)
        TotalBill = activity.findViewById<TextView>(R.id.totalbill)
        loader = activity.findViewById(R.id.loadingp)
        loader?.visibility = View.VISIBLE
        subbill?.visibility = View.INVISIBLE
        TotalBill?.visibility = View.INVISIBLE
        PayButton = activity.findViewById<Button>(R.id.proceedbutton)
        PayButton?.visibility = View.INVISIBLE
        PayImage = activity.findViewById(R.id.pay)


        val listing = activity.findViewById<View>(R.id.cartitems) as RecyclerView

        PayButton?.setOnClickListener(View.OnClickListener { //Payment Page
            cartClicks.Checkout()
        })

        activity.supportActionBar?.hide()
        val toolbar = activity.findViewById<Toolbar>(R.id.customtoolbar)
        toolbar.setBackgroundColor(staticLP?.titlebarColor!!)
        val title = activity.findViewById<TextView>(R.id.toolbarTitle)
        title.text = staticLP?.Title
        if (staticLP?.leftbutton == true) {
            val lb = activity.findViewById<ImageView>(R.id.titleleftbutton)
            lb.visibility = View.VISIBLE
            lb.setOnClickListener { cartClicks.OnClickLeftButton() }
        }




        adapter = cartproductsadapter(context,cartClicks)
        listing.setHasFixedSize(true)
        listing.setLayoutManager(LinearLayoutManager(context))
        listing.setAdapter(adapter)



    }

    fun LoadCartFromClass(classer : CartLayoutParams)
    {
        adapter?.Update(classer.cartitems);
        adapter?.notifyDataSetChanged()
        subbill?.setText(
            """
 Delivery Charge: ${classer.DeliveryCharge}
   
"""
        )

        if(classer.cartitems.size == 0)
        {TotalBill?.setText("No items")}
        else
        {TotalBill?.setText("Total Bill: " + classer.TotalBill)}

        loader?.visibility = View.GONE
        subbill?.visibility = View.VISIBLE
        TotalBill?.visibility = View.VISIBLE
        PayButton?.visibility = View.VISIBLE

    }

    public fun DisplaySideCartIcon(rootView : ConstraintLayout,amount : String , callback : SideCartIcon):RoundTag
    {
        val sideChick = SideChick(activity)
        return sideChick.DisplayRoundTag(rootView,activity.getDrawable(R.drawable.carticondefault)
        , amount, SideChick.OnIconClick {
            callback.OnSideCartClicked()
            });

    }





    public interface CartClicks
    {
        fun Checkout()
        fun ProductClicked(pparams : LayoutParamsItems)
        fun OnClickLeftButton()
        fun OnProductRemoved(pparams: LayoutParamsItems)
    }

    public interface SideCartIcon{
        fun OnSideCartClicked()

    }









}