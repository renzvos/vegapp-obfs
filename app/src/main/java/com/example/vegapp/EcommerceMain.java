package com.example.vegapp;

import android.app.Activity;
import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import ss.com.bannerslider.Slider;

public class EcommerceMain {
    Slider slider;
    Activity activity;
    Context context;
    EcommerceListing ecommerceListing;
    boolean IsFragment = false;
    LayoutCallbacks callbacks;
    Fragment fragment;
    View view;


    public EcommerceMain(Context context, Activity activity)
    {
        this.context = context;
        this.activity = activity;
        this.ecommerceListing = new EcommerceListing(activity);
    }

    public EcommerceMain(Context context, Fragment fragment)
    {
        this.context = context;
        this.fragment = fragment;
        this.IsFragment = true;
        this.ecommerceListing = new EcommerceListing(fragment);
    }



    public View ProduceLayoutForFragment(LayoutInflater inflater, ViewGroup container, LayoutCallbacks callbacks)
    {
        view =  inflater.inflate(R.layout.mainpage, container, false);
        this.callbacks = callbacks;
        return view;
    }

    public void notifyparams(RZEcomLayoutParams layoutParams)
    {
        //layoutParams.LogDataChange();
        PicassoImageLoadingService picassoImageLoadingService= new PicassoImageLoadingService(context);
        slider.init(picassoImageLoadingService);
        slider = view.findViewById(R.id.slider1);
        slider.setAdapter(new MainSliderAdapter(layoutParams.bannerUrls));
        RecyclerView listingview = view.findViewById(R.id.mainpagelist);


        AutoCompleteTextView autocomplete = view.findViewById(R.id.autoCompleteTextView1);
        String[] arr = {"Ulli","Thakkali","Tomato"};
        ArrayAdapter<String> adapter = null;
        if(IsFragment == true)
        {
            adapter = new ArrayAdapter<String>(fragment.getActivity(),android.R.layout.select_dialog_item, arr);
        }
        else
        {
            adapter = new ArrayAdapter<String>(activity,android.R.layout.select_dialog_item, arr);
        }
        autocomplete.setThreshold(2);
        autocomplete.setAdapter(adapter);




        ecommerceListing.Load(listingview, layoutParams.Content, new EcommerceListing.EcomlistingProductOnClick() {
            @Override
            public void OnClick(ProductCard card) {
                callbacks.ProductTapped(card);
            }
        });

        autocomplete.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) ||
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    callbacks.Searched(autocomplete.getText().toString());
                    return true;
                }
                return false;
            }
        });











    }




    public void ProduceLayoutForActivity(LayoutCallbacks callbacks){
        activity.setContentView(R.layout.mainpage);
        view  = activity.getWindow().getDecorView().getRootView();
        this.callbacks = callbacks;
    }

    public View getRootView()
    {
        return view.findViewById(R.id.parent);
    }






    public interface LayoutCallbacks
    {
        public void Searched(String Text);
        public void ProductTapped(ProductCard productDetails);
    }

}
