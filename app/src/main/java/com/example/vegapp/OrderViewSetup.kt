package com.renzvos.ecommerceorderview

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.Fragment
import com.example.vegapp.R

class OrderViewSetup {
    var activity:AppCompatActivity = AppCompatActivity()
    var rootview : View? = null
    var layoutDesign:OrderLayoutDesign? = null
    constructor(activity: AppCompatActivity)
    {
        this.activity = activity
    }
    constructor(fragment: Fragment)
    {
        this.activity = fragment.activity as AppCompatActivity
    }



    fun ProduceLayoutForActivity(layoutparams : OrderLayoutDesign)
    {
        layoutDesign= layoutparams
        activity.setContentView(R.layout.orderview1)
        rootview = activity.window.decorView.rootView
        ProduceLayout(rootview,layoutparams)
    }

    fun ProduceLayout(view: View?, layoutparams: OrderLayoutDesign)
    {
        layoutparams.Render(view!!)
    }



}