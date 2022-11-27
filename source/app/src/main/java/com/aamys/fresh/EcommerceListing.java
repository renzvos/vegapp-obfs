package com.aamys.fresh;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class EcommerceListing {
    Context context;
    AppCompatActivity activity;
    boolean isFragment = false;
    View rootview;
    public AutoCompleteTextView searchview;


    public EcommerceListing(AppCompatActivity activity){
        this.context = activity.getApplicationContext();
        this.activity = activity;
    }

    public EcommerceListing(Fragment fragment){
        this.isFragment = true;
        this.context = fragment.getContext();
        this.activity = (AppCompatActivity) fragment.getActivity();

    }

    public void ProduceLayoutForActivity(EcomlistingOnSearch searchcallback)
    {
        activity.setContentView(R.layout.shoplisting);
        activity.getSupportActionBar().hide();
        View masterview = activity.getWindow().getDecorView().getRootView();
        ProduceLayout(masterview,searchcallback);
    }

    public View ProduceLayoutForFragment(LayoutInflater inflater, ViewGroup container,EcomlistingOnSearch searchcallback)
    {
        View view =  inflater.inflate(R.layout.shoplisting, container, false);
        ProduceLayout(view,searchcallback);
        return view;
    }

    public void Load(ListViewLayoutParams productLister,EcomlistingProductOnClick callback)
    {
        RecyclerView listing = (RecyclerView) rootview.findViewById(R.id.listerm);
        Load(listing,productLister.listings,callback);
    }


    public void Load( RecyclerView listing, ArrayList<Listings> listings , EcomlistingProductOnClick listener)
    {
        if(listing.getAdapter() == null) {
            productsadapter adapter = new productsadapter(context, listings, listener);
            LinearLayoutManager linearLayoutManager;
            linearLayoutManager = new LinearLayoutManager(context);
            listing.setLayoutManager(linearLayoutManager);
            listing.setAdapter(adapter);
        }
        else
        {
            Log.i("RZ_EComListing", "Updating Listing: on View " +  listings.size());
            productsadapter adapter = (productsadapter) listing.getAdapter();
            adapter.listings = listings;
            adapter.callback = listener;
            adapter.notifyDataSetChanged();
        }
    }






    public void ProduceLayout(View view,EcomlistingOnSearch searchcallback)
    {
        this.rootview = view;
        searchview = view.findViewById(R.id.autoCompleteTextView1);
        String[] arr = {};
        ArrayAdapter<String> adapter = null;
        adapter = new ArrayAdapter<String>(activity,android.R.layout.select_dialog_item, arr);
        searchview.setFocusableInTouchMode(true);
        searchview.setThreshold(2);
        searchview.setAdapter(adapter);

        searchview.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) ||
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    searchview.clearFocus();
                    searchcallback.OnSearch(searchview.getText().toString());
                    return true;
                }
                return false;
            }
        });

    }


    public interface EcomlistingProductOnClick{
        public void OnClick(ProductCard card);

    }

    public void setAppTitle(String Title , String TitleColor)
    {
        Toolbar toolbar = rootview.findViewById(R.id.customtoolbar);
        toolbar.setBackgroundColor(Color.parseColor(TitleColor));
        TextView title  = rootview.findViewById(R.id.toolbarTitle);
        title.setText(Title);
    }

    public void SetBackButton(OnBackPressed backPressed)
    {
       ImageView BackIcon = rootview.findViewById(R.id.titleleftbutton);
        BackIcon.setVisibility(View.VISIBLE);
        BackIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backPressed.OnClick();
            }
        });

    }


    public interface EcomlistingOnSearch
    {
        public void OnSearch(String Text);
    }

    public interface OnBackPressed{
        public void OnClick();
    }



}
