package com.aamys.fresh;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class productsadapter extends RecyclerView.Adapter<productsadapter.ViewHolder>{
    ArrayList<Listings> listings;
    Context context;
    EcommerceListing.EcomlistingProductOnClick callback;

    @Override
    public int getItemViewType(int position)
    {
        if(listings.size() == 0)
        {
            return R.layout.noproducts;
        }

    return R.layout.listinger ;}



    // RecyclerView recyclerView;
    public productsadapter(Context context, ArrayList<Listings> listings, EcommerceListing.EcomlistingProductOnClick callback) {
        this.listings = listings;
        this.context = context;
        this.callback = callback;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(viewType, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }



    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        if(listings.size() != 0) {
            final String pname = listings.get(position).title;
            if (listings.get(position).extension == false) {
                holder.textView.setVisibility(View.VISIBLE);
            }
            holder.textView.setText(pname);
            verticalproducts adapter = new verticalproducts(context, listings.get(position), callback);
            holder.recyclerView.setHasFixedSize(true);


            if (listings.get(position).layout == Listings.HORIZONTAL_LAYOUT) {
                holder.recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));

            } else if (listings.get(position).layout == Listings.VERTICAL_LAYOUT) {
                holder.recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
            } else if(listings.get(position).layout == Listings.HORIZONTAL_FIT_LAYOUT){
                holder.recyclerView.setLayoutManager(new GridLayoutManager(context,listings.get(position).horizontalSpanCount));
            }


            holder.recyclerView.setAdapter(adapter);

        }

    }


    @Override
    public int getItemCount() {
        Log.i("RZ_Ecom_Adapter", "getItemCount: " + listings.size());
        if(listings.size() == 0)
            return 1;
        else
            return listings.size();

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;

        public RecyclerView recyclerView;
        public ViewHolder(View itemView) {
            super(itemView);
            try {
                this.textView = (TextView) itemView.findViewById(R.id.listingtitle);
                this.recyclerView = itemView.findViewById(R.id.lister);
            }catch (Exception e){}

        }
    }
}