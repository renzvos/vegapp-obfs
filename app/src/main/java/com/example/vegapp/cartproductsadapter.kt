package com.example.vegapp

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.view.LayoutInflater
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.squareup.picasso.Picasso
import java.util.ArrayList

class cartproductsadapter(context: Context,cartClicks: EcommerceCart.CartClicks) :
    RecyclerView.Adapter<cartproductsadapter.ViewHolder>() {
    private var cart: ArrayList<LayoutParamsItems>?
    var context: Context
    var callbacks = cartClicks

    fun Update(items: ArrayList<LayoutParamsItems>?) {
        Log.i("ABCDE", "Update: ")
        cart = items
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val listItem: View =
            layoutInflater.inflate(R.layout.cartitem, parent, false)
        return ViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pname: String = cart?.get(position)!!.ProductName
        holder.name.text = pname
        holder.quantity.setText(cart?.get(position)!!.Quantity)
        holder.price.setText(cart?.get(position)!!.Price)
        Picasso.get().load(cart?.get(position)!!.iconurl).into(holder.Image)
        if(position == (cart?.size?.minus(1))){

            var lp = holder.cartcard.layoutParams as ViewGroup.MarginLayoutParams
            lp.setMargins(10,10,10,500)
            holder.cartcard.layoutParams = lp
        }
        else
        {
            var lp = holder.cartcard.layoutParams as ViewGroup.MarginLayoutParams
            lp.setMargins(10,10,10,10)
            holder.cartcard.layoutParams = lp
        }
        holder.cartcard.setOnClickListener {
            callbacks.ProductClicked(cart?.get(position)!!)
        }


        //holder.imageView.setImageResource(listdata[position].getImgId());
        /*holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(),"click on item: "+myListData.getDescription(),Toast.LENGTH_LONG).show();
            }
        });

        */
    }

    override fun getItemCount(): Int {
        Log.i("RZCart", "getItemCount: " + cart?.size)
        return cart!!.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name: TextView
        var price: TextView
        var quantity: TextView
        var cartcard: ConstraintLayout
        var Image : ImageView

        init {
            //this.imageView = (ImageView) itemView.findViewById(R.id.imageView);
            name = itemView.findViewById<View>(R.id.pname2) as TextView
            price = itemView.findViewById(R.id.pprice2)
            quantity = itemView.findViewById(R.id.quantity2)
            cartcard = itemView.findViewById(R.id.cartcard)
            Image = itemView.findViewById(R.id.pimage2)
        }
    }

    // RecyclerView recyclerView;
    init {
        cart = ArrayList<LayoutParamsItems>()
        this.context = context
    }
}