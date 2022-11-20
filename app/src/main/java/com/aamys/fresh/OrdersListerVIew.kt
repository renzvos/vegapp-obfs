package com.renzvos.ecommerceorderslist

import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aamys.fresh.R
import com.aamys.fresh.SorterComparater
import java.util.*
import kotlin.collections.ArrayList

class OrdersListerVIew {
    var activity :AppCompatActivity = AppCompatActivity()
    var fragment : Fragment? = null
    var recyclerView : RecyclerView? = null
    var adapter : orderlistadapter? = null
    var rootviewer : View? = null
    constructor(activity: AppCompatActivity)
    {
        this.activity = activity;
    }
    constructor(fragment: Fragment)
    {
        this.fragment = fragment
        this.activity = fragment.activity as AppCompatActivity
    }

    fun ProduceLayoutForActivity(Title : String , TitleColor : String ,callback: OrderListCallback)
    {
        activity.supportActionBar?.hide()
        activity.setContentView(R.layout.listholder)
        ProduceLayout(activity.window.decorView.rootView,callback)
        SetTitle(Title, TitleColor)
    }

    fun ProduceLayout(view :View,callback: OrderListCallback)
    {
        rootviewer = view
        recyclerView = view.findViewById(R.id.orderlistview)
        ApplyOrderListToRecyclerView(recyclerView!!,callback)

    }

    fun ApplyOrderListToRecyclerView(recyclerView: RecyclerView,callback: OrderListCallback)
    {
        this.recyclerView = recyclerView
        adapter = orderlistadapter(activity.applicationContext,callback)
        recyclerView.setHasFixedSize(true)
        recyclerView.setLayoutManager(LinearLayoutManager(activity.applicationContext))
        recyclerView.setAdapter(adapter)
    }

    fun AddOrderList(items :ArrayList<OrderDetails>)
    {
        Log.i("RZ_EcomOrderLister", "AddOrderList: " + items.size)
        if(items.size == 0)
        {
            val orderless = rootviewer?.findViewById<TextView>(R.id.orderless)
            orderless?.visibility = View.VISIBLE
        }
        Collections.sort(items, SorterComparater.DateSorter())
        adapter?.Update(items)
        adapter?.notifyDataSetChanged()
    }

    fun SetBackButton(callback: OnClickBackButtonListener)
    {
        val BackButton  = rootviewer?.findViewById<ImageView>(R.id.titleleftbutton)
        BackButton?.visibility = View.VISIBLE
        BackButton?.setOnClickListener(View.OnClickListener { callback.OnClick() })
    }

    fun SetTitle(Title : String, TitleColor:String )
    {
        val TitleBox  = rootviewer?.findViewById<TextView>(R.id.toolbarTitle)
        TitleBox?.setText(Title)
        val toolbar: Toolbar? = activity?.findViewById<Toolbar>(R.id.customtoolbar)
        toolbar?.setBackgroundColor(Color.parseColor(TitleColor))
    }


    interface OrderListCallback
    {
        fun OnClick(orderDetails: OrderDetails)
    }

    interface OnClickBackButtonListener{
        fun OnClick()
    }



}