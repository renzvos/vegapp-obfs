package com.renzvos.ecommerceorderslist

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.view.LayoutInflater
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.aamys.fresh.R
import java.util.ArrayList

class orderlistadapter(context: Context,orderListCallback: OrdersListerVIew.OrderListCallback) :
    RecyclerView.Adapter<orderlistadapter.ViewHolder>() {
    private var orders: ArrayList<OrderDetails>?
    var context: Context
    var callbacks = orderListCallback

    fun Update(items: ArrayList<OrderDetails>?) {
        Log.i("RZ_EcomOrderLister", "Adapter Update: ")
        orders = items
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val listItem: View =
            layoutInflater.inflate(R.layout.orderitem, parent, false)
        return ViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.ordercard.backgroundTintList = (ColorStateList(arrayOf(intArrayOf()), intArrayOf(Color.parseColor(orders?.get(position)?.backgroudColor))))
        holder.ordercard.backgroundTintMode = PorterDuff.Mode.SRC_OVER
        holder.ordercard.setOnClickListener {
            callbacks.OnClick(orders!!.get(position))
        }

        holder.status.setText(orders!!.get(position).Status)
        holder.amount.setText(orders!!.get(position).Amount)
        holder.purchasedate.setText(orders!!.get(position).PurchaseDisplayDateTime)
        holder.orderid.setText(orders!!.get(position).displayOrderid)
    }

    override fun getItemCount(): Int {
        Log.i("RZ_EcomOrderLister", "getItemCount: " + orders?.size)
        return orders!!.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ordercard: CardView
        var status: TextView
        var amount: TextView
        var purchasedate : TextView
        var orderid : TextView

        init {
            ordercard = itemView.findViewById(R.id.ordercard)
            status = itemView.findViewById(R.id.orderstatus)
            amount = itemView.findViewById(R.id.orderamount)
            purchasedate = itemView.findViewById(R.id.orderdate)
            orderid = itemView.findViewById(R.id.orderid)
        }
    }

    // RecyclerView recyclerView;
    init {
        orders = ArrayList<OrderDetails>()
        this.context = context
    }
}