package com.example.vegapp;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import ss.com.bannerslider.Slider;

public class EcomProductView {
    Context context;
    AppCompatActivity activity;
    boolean isFragment = false;
    Slider slider;
    public TextView pname;
    public TextView OldRate;
    public TextView NewRate;
    public TextView Description;
    public EditText Qty;
    public TextView oldunit;
    public TextView newunit;
    public ProgressBar progressBar;
    PviewLayoutParams layoutParams;

    LayoutClicks callbacks;
    ProductSliderAdapter.SliderCallback sliderCallback = new ProductSliderAdapter.SliderCallback() {
        @Override
        public void Loaded() {
            callbacks.OnImageLoaded();
        }
    };



    public EcomProductView(AppCompatActivity activity){
        this.context = activity.getApplicationContext();
        this.activity = activity;
    }

    public EcomProductView(Fragment fragment){
        this.isFragment = true;
        this.context = fragment.getContext();
        this.activity = (AppCompatActivity) fragment.getActivity();

    }

    public void ProduceLayoutForActivity( int Layout,PviewHolder pviewHolder ,LayoutClicks callbacks)
    {
        this.callbacks = callbacks;
        activity.getSupportActionBar().hide();
        activity.setContentView(Layout);
        pviewHolder.ModifyContextForActivity(activity, new PviewHolder.Holdercallbacks() {
            @Override
            public void OnClickLeftButton() {
                callbacks.OnClickLeftButton();
            }
        });
        View masterview = activity.getWindow().getDecorView().getRootView();
        ProduceLayout(masterview,callbacks);
    }

    public View ProduceLayoutForFragment(int Layout, LayoutInflater inflater, ViewGroup container,LayoutClicks callbacks)
    {
        this.callbacks = callbacks;
        View view =  inflater.inflate(Layout, container, false);
        Toolbar toolbar = view.findViewById(R.id.customtoolbar);
        toolbar.setVisibility(View.GONE);
        ProduceLayout(view,callbacks);
        return view;
    }



    public void ProduceLayout(View view,LayoutClicks callbacks)
    {

        ArrayList<String> imageurls = new ArrayList<>();
        PviewLayoutParams layoutParams = new PviewLayoutParams(imageurls, "","", "","",null);

        PicassoImageLoadingService picassoImageLoadingService= new PicassoImageLoadingService(context);
        slider.init(picassoImageLoadingService);
        slider = view.findViewById(R.id.pslider);
        slider.setAdapter(new ProductSliderAdapter(layoutParams.imageurls,sliderCallback));
        pname = view.findViewById(R.id.pname);
        OldRate = view.findViewById(R.id.oldprce);
        NewRate = view.findViewById(R.id.newprice);
        Description = view.findViewById(R.id.desc);
        Qty = view.findViewById(R.id.qty);
        oldunit = view.findViewById(R.id.unitold);
        newunit = view.findViewById(R.id.unitnew);
        progressBar = view.findViewById(R.id.ploader);
        Button QtyAdd =view.findViewById(R.id.add);
        Button Qtyremove = view.findViewById(R.id.remove);

        notifyDatasetChanged(layoutParams,callbacks);

        QtyAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Qty.setText(String.valueOf(Integer.parseInt(Qty.getText().toString())  + 1));
            }
        });

        Qtyremove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Integer.parseInt(Qty.getText().toString()) != 0 )
                Qty.setText(String.valueOf(Integer.parseInt(Qty.getText().toString()) - 1));
            }
        });


        Button addtocart = view.findViewById(R.id.addtocart);
        addtocart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callbacks.AddedtoCart(layoutParams,Integer.valueOf(Qty.getText().toString()));
            }
        });



    }

    public void notifyDatasetChanged(PviewLayoutParams layoutParams,LayoutClicks callbacks)
    {
        this.layoutParams = layoutParams;
        slider.setAdapter(new ProductSliderAdapter(layoutParams.imageurls,sliderCallback));
        pname.setText(layoutParams.pname);
        OldRate.setText(layoutParams.old_rate);
        OldRate.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        NewRate.setText(layoutParams.new_rate);
        Description.setText(layoutParams.Description);
        Log.i("RZPB", "notifyDatasetChanged: " + layoutParams.punit);
        if(layoutParams.punit != null)
        {
            Log.i("RZPA", "notifyDatasetChanged: " + layoutParams.punit);
            oldunit.setVisibility(View.VISIBLE);
            newunit.setVisibility(View.VISIBLE);
            oldunit.setText("/" + layoutParams.punit);
            newunit.setText("/" + layoutParams.punit);
        }
        else
        {
            oldunit.setVisibility(View.INVISIBLE);
            newunit.setVisibility(View.INVISIBLE);
        }

    }

    public View getRootVIew()
    {
        return activity.findViewById(R.id.parent);
    }


    public void StartLoading(){
        progressBar.setVisibility(View.VISIBLE);
    }

    public void StopLoading()
    {
        progressBar.setVisibility(View.INVISIBLE);
    }

    public interface LayoutClicks
    {
        public void AddedtoCart(PviewLayoutParams layoutParams, int Quantity);
        public void OnClickLeftButton();
        public void OnImageLoaded();
    }


}
