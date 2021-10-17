package com.renzvos.ecommerceorderslist

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vegapp.R

class OrdersListerVIew {
    var activity :AppCompatActivity = AppCompatActivity()
    var fragment : Fragment? = null
    var recyclerView : RecyclerView? = null
    var adapter : orderlistadapter? = null
    constructor(activity: AppCompatActivity)
    {
        this.activity = activity;
    }
    constructor(fragment: Fragment)
    {
        this.fragment = fragment
        this.activity = fragment.activity as AppCompatActivity
    }

    fun ProduceLayoutForActivity(callback: OrderListCallback)
    {
        activity?.setContentView(R.layout.listholder)
        ProduceLayout(activity.window.decorView.rootView,callback)
    }

    fun ProduceLayout(view :View,callback: OrderListCallback)
    {
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
        adapter?.Update(items)
        adapter?.notifyDataSetChanged()
    }


    interface OrderListCallback
    {
        fun OnClick(orderDetails: OrderDetails)
    }


}