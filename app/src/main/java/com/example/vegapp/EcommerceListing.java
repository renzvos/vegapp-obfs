package com.example.vegapp;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class EcommerceListing {
    Context context;
    Activity activity;
    boolean isFragment = false;
    View rootview;

    public EcommerceListing(Activity activity){
        this.context = activity.getApplicationContext();
        this.activity = activity;
    }

    public EcommerceListing(Fragment fragment){
        this.isFragment = true;
        this.context = fragment.getContext();
        this.activity = fragment.getActivity();

    }





    public void Load(RecyclerView listing ,ArrayList<Listings> listings , EcomlistingProductOnClick listener )
    {
        productsadapter adapter = new productsadapter(context,listings , listener);
        listing.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        listing.setLayoutManager(linearLayoutManager);
        listing.setAdapter(adapter);
    }



    public void ProduceLayout(View view)
    {
        this.rootview = view;
    }


    public interface EcomlistingProductOnClick{
        public void OnClick(ProductCard card);

    }

}
