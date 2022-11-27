package com.aamys.fresh;

/*
The file is licenced under MIT and reserves to Arshad Nazir on 28th July 2022 at renzvos.com
 */

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import java.util.ArrayList


class SimpleListAdapter(context: Context, orderListCallback: SimpleListActivity.SimpleListCallback) :
    RecyclerView.Adapter<SimpleListAdapter.ViewHolder>() {
    private var items: ArrayList<item> =  ArrayList<item>()
    var context: Context = context
    var callbacks = orderListCallback

    fun Update(items: ArrayList<item>) {
        Log.i("RZ_SimpleList_Adapter", "Update: ")
        this.items = items
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val listItem: View =
            layoutInflater.inflate(R.layout.itemlayout, parent, false)
        return ViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.parent.setOnClickListener {
            callbacks.OnClick(items.get(position))
        }
        holder.itemlabel.setText(items.get(position).Label)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var itemlabel : TextView
        var parent : ConstraintLayout

        init {
            itemlabel = itemView.findViewById(R.id.label)
            parent = itemView.findViewById(R.id.parent)
        }
    }


}