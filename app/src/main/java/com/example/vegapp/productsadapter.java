package com.example.vegapp;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class productsadapter extends RecyclerView.Adapter<productsadapter.ViewHolder>{
    private ArrayList<Listings> listings;
    Context context;
    EcommerceListing.EcomlistingProductOnClick callback;


    // RecyclerView recyclerView;
    public productsadapter(Context context, ArrayList<Listings> listings, EcommerceListing.EcomlistingProductOnClick callback) {
        this.listings = listings;
        this.context = context;
        this.callback = callback;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.listinger, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }



    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final String pname = listings.get(position).title;
        holder.textView.setText(pname);
        verticalproducts adapter = new verticalproducts(context,listings.get(position),callback);
        holder.recyclerView.setHasFixedSize(true);


        if(listings.get(position).layout == Listings.HORIZONTAL_LAYOUT)
        {
            holder.recyclerView.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false));

        }
        else if(listings.get(position).layout == Listings.VERTICAL_LAYOUT)
        {
            holder.recyclerView.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false));
        }


        holder.recyclerView.setAdapter(adapter);

        //holder.imageView.setImageResource(listdata[position].getImgId());
        /*holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(),"click on item: "+myListData.getDescription(),Toast.LENGTH_LONG).show();
            }
        });

        */
    }


    @Override
    public int getItemCount() {
        Log.i("ABCDE", "getItemCount: " + listings.size());
        return listings.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView textView;

        public RecyclerView recyclerView;
        public ViewHolder(View itemView) {
            super(itemView);
            //this.imageView = (ImageView) itemView.findViewById(R.id.imageView);
            this.textView = (TextView) itemView.findViewById(R.id.listingtitle);
            this.recyclerView = itemView.findViewById(R.id.lister);

        }
    }
}