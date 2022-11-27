package com.aamys.fresh;



import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;


public class verticalproducts extends RecyclerView.Adapter<verticalproducts.ViewHolder> {
    private Listings listing;
    private Context context;
    EcommerceListing.EcomlistingProductOnClick callback;
    private static final int FEATURE_HORIZONTAL_LAYOUT = 2;
    private static final int FEATURE_VERTICAL_LAYOUT = 3;
    private static final int DEFAULT_HORIZONTAL_LAYOUT = 4;
    private static final int DEFAULT_VERTICAL_LAYOUT = 5;
    private static final int NOPRODUCTS_LAYOUT = 1;

    // RecyclerView recyclerView;
    public verticalproducts(Context context, Listings listings, EcommerceListing.EcomlistingProductOnClick callback) {
        this.listing = listings;
        this.context = context;
        this.callback = callback;
    }

    @Override
    public int getItemViewType(int position)
    {
        return position;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(LayoutResourceID(PrepareLayout(position)), parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem,PrepareLayout(position));
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
       if(PrepareLayout(position) == NOPRODUCTS_LAYOUT)
       {
           holder.Noitemlabel.setText(listing.getNoItemText());
       }
       else
       {
           final String pname = listing.products.get(position).productname;
           holder.pname.setText(pname);
           holder.newprice.setText(listing.products.get(position).newrate);
           Picasso.get().load(listing.products.get(position).imagelinks.get(0)).into(holder.imageView);

           holder.viewproduct.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   callback.OnClick(listing.products.get(position));
               }
           });

           if (isFeature(position))
           {
               holder.oldprice.setText(listing.products.get(position).oldrate);
               holder.oldprice.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
               holder.message.setText(listing.products.get(position).message);
               holder.mainunit.setText(listing.products.get(position).unit);
               holder.subunit.setText(listing.products.get(position).unit);
           }

           if(listing.layout == Listings.HORIZONTAL_FIT_LAYOUT)
           {
               holder.parent.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
               holder.card.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
           }
       }





    }


    @Override
    public int getItemCount() {
        Log.i("RZ_Ecom_VerticalAdapter", "getItemCount: " + listing.products.size());
        if(listing.products.size() == 0)
        {
            return 1;
        }else
        return listing.products.size();
    }

    public boolean isFeature(int position)
    {
        if (listing.products.get(position).feature)
        {
            return true;
        }
        return false;
    }

    public int PrepareLayout(int position)
    { int currentLayout = 0;
        if(listing.products.size() != 0){
        if(listing.layout == Listings.VERTICAL_LAYOUT)
            {if (isFeature(position))
                { currentLayout = FEATURE_VERTICAL_LAYOUT; }
                else
                {currentLayout = DEFAULT_VERTICAL_LAYOUT; }
            }
            else if(listing.layout == Listings.HORIZONTAL_LAYOUT || listing.layout == Listings.HORIZONTAL_FIT_LAYOUT)
            { if (isFeature(position))
                { currentLayout = FEATURE_HORIZONTAL_LAYOUT; }
                else
                { currentLayout = DEFAULT_HORIZONTAL_LAYOUT; }
            }
        }
        else
        {
            currentLayout = NOPRODUCTS_LAYOUT;
        }
        return currentLayout;
    }

    public int LayoutResourceID(int Layout)
    {
        switch (Layout)
        {
            case DEFAULT_VERTICAL_LAYOUT:
                return R.layout.productwide;
            case FEATURE_VERTICAL_LAYOUT:
                return R.layout.productwidefeature;
            case FEATURE_HORIZONTAL_LAYOUT:
                return R.layout.productsmallfeature;
            case DEFAULT_HORIZONTAL_LAYOUT:
                return R.layout.productsmall;
            case NOPRODUCTS_LAYOUT:
                return R.layout.sub_noproducts;
        }
        return 0;
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
        public TextView Noitemlabel;
        public LinearLayout parent;



        public ViewHolder(View itemView,int Layout) {
            super(itemView);
            if(Layout == verticalproducts.DEFAULT_VERTICAL_LAYOUT || Layout == verticalproducts.DEFAULT_HORIZONTAL_LAYOUT) {
                this.imageView = (ImageView) itemView.findViewById(R.id.pimage);
                this.pname = (TextView) itemView.findViewById(R.id.pnamer);
                this.card = itemView.findViewById(R.id.productcard);
                this.newprice = itemView.findViewById(R.id.mainvalue);
                this.viewproduct = itemView.findViewById(R.id.viewproduct);
                this.mainunit = itemView.findViewById(R.id.mainunit);
            }

            if(Layout == verticalproducts.FEATURE_VERTICAL_LAYOUT || Layout == verticalproducts.FEATURE_HORIZONTAL_LAYOUT) {
                this.imageView = (ImageView) itemView.findViewById(R.id.pimage);
                this.pname = (TextView) itemView.findViewById(R.id.pnamer);
                this.card = itemView.findViewById(R.id.productcard);
                this.newprice = itemView.findViewById(R.id.mainvalue);
                this.parent = itemView.findViewById(R.id.parent);
                this.viewproduct = itemView.findViewById(R.id.viewproduct);
                this.mainunit = itemView.findViewById(R.id.mainunit);
                this.oldprice = itemView.findViewById(R.id.subvalue);
                this.message = itemView.findViewById(R.id.fmessage);
                this.subunit = itemView.findViewById(R.id.subunit);
            }

            if(Layout == verticalproducts.NOPRODUCTS_LAYOUT)
            {
                this.Noitemlabel = itemView.findViewById(R.id.noitemlabel);
            }
        }
    }
}
