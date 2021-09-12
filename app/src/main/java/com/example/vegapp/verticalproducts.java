package com.example.vegapp;



import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;


public class verticalproducts extends RecyclerView.Adapter<verticalproducts.ViewHolder> {
    private Listings listing;
    private Context context;
    EcommerceListing.EcomlistingProductOnClick callback;

    // RecyclerView recyclerView;
    public verticalproducts(Context context, Listings listings, EcommerceListing.EcomlistingProductOnClick callback) {
        this.listing = listings;
        this.context = context;
        this.callback = callback;
    }

    @Override
    public int getItemViewType(int position)
    {
        if(listing.layout == Listings.VERTICAL_LAYOUT)
        {
           return R.layout.productwide;
        }
        else if(listing.layout == Listings.HORIZONTAL_LAYOUT)
        {
            if (listing.products.get(position).feature)
            {
                return R.layout.productsmallfeature;
            }
            else
            {
                return R.layout.productsmall;

            }
        }

        return  0;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = null;
        listItem = layoutInflater.inflate(viewType, parent, false);
        ViewHolder viewHolder = null;
        if(viewType == R.layout.productsmallfeature)
            viewHolder = new ViewHolder(listItem,true);
        else
            viewHolder = new ViewHolder(listItem,false);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final String pname = listing.products.get(position).productname;
        holder.pname.setText(pname);
        holder.newprice.setText(listing.products.get(position).newrate);
        Picasso.get().load(listing.products.get(position).imagelinks.get(0)).into(holder.imageView);

        if(listing.products.get(position).feature)
        {
            holder.oldprice.setText(listing.products.get(position).oldrate);
            holder.oldprice.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            holder.message.setText(listing.products.get(position).message);
            holder.mainunit.setText(listing.products.get(position).unit);
            holder.subunit.setText(listing.products.get(position).unit);
        }
        holder.viewproduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.OnClick(listing.products.get(position));
            }
        });
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
        Log.i("ABCDE", "getItemCount: " + listing.products.size());
        return listing.products.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView mainunit;
        public TextView subunit;
        public TextView pname;
        public CardView card;
        public TextView oldprice;
        public TextView newprice;
        public TextView message;
        public Button viewproduct;


        public ViewHolder(View itemView,boolean feature) {
            super(itemView);
            this.imageView = (ImageView) itemView.findViewById(R.id.pimage);
            this.pname = (TextView) itemView.findViewById(R.id.pnamer);
            this.card = itemView.findViewById(R.id.productcard);
            this.newprice = itemView.findViewById(R.id.mainvalue);
            this.viewproduct = itemView.findViewById(R.id.viewproduct);
            this.mainunit = itemView.findViewById(R.id.mainunit);

            if(feature){
            this.oldprice = itemView.findViewById(R.id.subvalue);
            this.message = itemView.findViewById(R.id.fmessage);
            this.subunit = itemView.findViewById(R.id.subunit);}
        }
    }
}
