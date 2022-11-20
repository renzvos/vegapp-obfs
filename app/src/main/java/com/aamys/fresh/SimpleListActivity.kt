package com.aamys.fresh;

/*
The file is licenced under MIT and reserves to Arshad Nazir on 28th July 2022 at renzvos.com
 */

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SimpleListActivity(IsFragment : Boolean,activity: AppCompatActivity, inflater: LayoutInflater , Rootviewer : View ,callback: SimpleListCallback ) {
    var recyclerView : RecyclerView? =null
    var adapter : SimpleListAdapter?=null

    private var Isfragment: Boolean = IsFragment
    private var inflator: LayoutInflater = inflater
    private var layoutfile = R.layout.listlayout
    private var containew: ViewGroup? = null
    private var savedInstanceStater: Bundle? = null
    private var callback:SimpleListCallback = callback
    private var fragment : Fragment?=null
    private var activity  = activity
    var rootviewr  = Rootviewer

    constructor(fragment: Fragment,
        inflater: LayoutInflater,
        container: ViewGroup,
        savedInstanceStater: Bundle?,
        callback: SimpleListCallback
    ) : this(true, fragment.activity as AppCompatActivity,inflater ,fragment.layoutInflater.inflate(R.layout.listlayout, container, false), callback) {
        this.Isfragment = true
        this.inflator = inflater
        this.containew = container
        this.savedInstanceStater = savedInstanceStater
        this.callback = callback
        this.fragment = fragment
        ProduceLayout(rootviewr)
    }

    constructor(activity: AppCompatActivity, callback: SimpleListCallback) : this(false, activity ,activity.layoutInflater,activity.window.decorView.rootView,callback) {
        this.activity = activity
        this.callback = callback
         activity.supportActionBar?.hide()
        activity.setContentView(layoutfile)
        ProduceLayout(rootviewr)
    }





    fun ProduceLayout(view: View) {

        recyclerView = view.findViewById(R.id.list)
        adapter = SimpleListAdapter(activity.applicationContext,callback)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.setLayoutManager(LinearLayoutManager(activity.applicationContext))
        recyclerView?.setAdapter(adapter)
    }

    fun Update(items : ArrayList<item>)
    {
        adapter?.Update(items)
        adapter?.notifyDataSetChanged()
    }

    fun setTitle(title : String)
    {
        val titletext = rootviewr.findViewById<TextView>(R.id.title)
        titletext.visibility = View.VISIBLE
        titletext.setText(title)
    }

    fun setAppTitle(Title : String , TitleColor : String)
    {
        val toolbar: Toolbar = rootviewr.findViewById<Toolbar>(R.id.customtoolbar)
        toolbar.setBackgroundColor(Color.parseColor(TitleColor))
        val title: TextView = rootviewr.findViewById<TextView>(R.id.toolbarTitle)
        title.text = Title
    }

    fun SetBackButton(backPressed: OnBackPressed)
    {
        var BackIcon = rootviewr.findViewById<ImageView>(R.id.titleleftbutton)
        BackIcon?.visibility = View.VISIBLE
        BackIcon?.setOnClickListener(View.OnClickListener {
            backPressed?.OnClick()
        })
    }





     interface SimpleListCallback{
          fun OnClick(item: item)
    }

    interface OnBackPressed{
        fun OnClick()
    }





}