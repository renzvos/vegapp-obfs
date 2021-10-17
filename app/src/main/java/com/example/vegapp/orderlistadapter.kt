package com.renzvos.ecommerceorderslist

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.provider.ContactsContract
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.view.LayoutInflater
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.vegapp.R
import java.util.ArrayList

class orderlistadapter(context: Context,orderListCallback: OrdersListerVIew.OrderListCallback) :
    RecyclerView.Adapter<orderlistadapter.ViewHolder>() {
    private var orders: ArrayList<OrderDetails>?
    var context: Context
    var callbacks = orderListCallback

    fun Update(items: ArrayList<OrderDetails>?) {
        Log.i("ABCDE", "Update: ")
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
        holder.subheading.setText(orders!!.get(position).subheading)
        holder.timeframe.setText(orders!!.get(position).timeframe)
    }

    override fun getItemCount(): Int {
        Log.i("RZCart", "getItemCount: " + orders?.size)
        return orders!!.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ordercard: CardView
        var status: TextView
        var timeframe: TextView
        var subheading: TextView

        init {
            ordercard = itemView.findViewById(R.id.ordercard)
            status = itemView.findViewById(R.id.orderstatus)
            timeframe = itemView.findViewById(R.id.timeframe)
            subheading = itemView.findViewById(R.id.subheading)
        }
    }

    // RecyclerView recyclerView;
    init {
        orders = ArrayList<OrderDetails>()
        this.context = context
    }
}