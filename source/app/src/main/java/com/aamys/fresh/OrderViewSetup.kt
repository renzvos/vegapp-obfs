package com.renzvos.ecommerceorderview

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.Fragment
import com.aamys.fresh.R

class OrderViewSetup {
    var activity:AppCompatActivity = AppCompatActivity()
    var rootview : View? = null
    var layoutDesign:OrderLayoutDesign? = null
    var designcode : Int = 0
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
        activity.supportActionBar?.hide()
        designcode = layoutparams.Design!!
        layoutDesign= layoutparams
        activity.setContentView(R.layout.orderview1)
        rootview = activity.window.decorView.rootView
        ProduceLayout(rootview,layoutparams)
    }

    fun UpdateLayoutParams(layoutparams: OrderLayoutDesign)
    {

        if(layoutparams.Design == 1)
        {
            layoutparams.UpdateSimpleOrderLayout();
        }
    }

    private fun ProduceLayout(view: View?, layoutparams: OrderLayoutDesign)
    {
        layoutparams.Render(view!!)
    }



}