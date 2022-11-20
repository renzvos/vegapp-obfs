package com.aamys.fresh;

/*
The file is licenced under MIT and reserves to Arshad Nazir on 28th July 2022 at renzvos.com
 */

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;


import com.aamys.fresh.ProductDt;import com.aamys.fresh.ProductViewer;import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    ArrayList<Listings> listings;
    EcommerceListing listing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_search);
        String keyword = getIntent().getStringExtra("keyword");
        listing = new EcommerceListing(this);
        listing.ProduceLayoutForActivity(new EcommerceListing.EcomlistingOnSearch() {
            @Override
            public void OnSearch(String Text) {
                /*
                Log.i("App-Search", "OnSearch: " + Text);
                listings = new ArrayList<>();
                listings.add(new Listings("Search Results",new ArrayList<ProductCard>(),Listings.VERTICAL_LAYOUT));
                firestore.collection("products").whereGreaterThanOrEqualTo("productName",Text).get().addOnSuccessListener(resultcame);

                 */}
        });

        listing.setAppTitle("Aamy's Fresh", "#9C11A9");
        listing.SetBackButton(new EcommerceListing.OnBackPressed() {
            @Override
            public void OnClick() {
                finish();
            }
        });
        listing.searchview.setHint("Enter your text");
        listing.Load(ListViewLayoutParams.EmptySet(),productclick);



        String category  = getIntent().getStringExtra("category");
        Log.i("App-Search", "onCreate: " + category);
        if(category != null)
        {  listings = new ArrayList<>();
            Log.i("App-Search", "onCreate: notnull " + category);
            Listings row = new Listings(category,new ArrayList<ProductCard>());
            row.HorizontalFitLayout(2);
            listings.add(row);
            firestore.collection("products").whereEqualTo("productCategory", category).get().addOnSuccessListener(resultcame);
        }else
        {   listings = new ArrayList<>();
            Log.i("App-Search", "onCreate: null " + category);
            Listings row  = new Listings("Search Results" , new ArrayList<>());
            row.HorizontalFitLayout(2);
            listings.add(row);
            firestore.collection("products").get().addOnSuccessListener(resultcame);
        }


        listing.searchview.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                FilterandUpdate("Search Results",editable.toString());
            }
        });


        ListViewLayoutParams layoutParams = new ListViewLayoutParams(listings);
        listing.Load(layoutParams,productclick);




    }

    private void FilterandUpdate(String Title,String keyword)
    {
        ArrayList<ProductCard> filteredlist  = new ArrayList<>();
        for(ProductCard productCard : listings.get(0).products)
        {
            if(productCard.productname.toLowerCase().contains(keyword.toLowerCase()))
            {
                filteredlist.add(productCard);
            }
        }
        ArrayList<Listings> filllistings = new ArrayList<>();
        Listings row = new Listings(Title,filteredlist);
        row.HorizontalFitLayout(2);
        filllistings.add(row);
        ListViewLayoutParams layoutParams = new ListViewLayoutParams(filllistings);
        listing.Load(layoutParams, productclick);

    }

    private   EcommerceListing.EcomlistingProductOnClick productclick  = new EcommerceListing.EcomlistingProductOnClick() {
        @Override
        public void OnClick(ProductCard card) {


            Intent intent = new Intent(SearchActivity.this,ProductViewer.class);
            intent.putExtra("pid",card.pid);
            startActivity(intent);

        }
    };



    private OnSuccessListener resultcame = new OnSuccessListener<QuerySnapshot>() {
        @Override
        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

            Log.i("App-Search", "result came: " + queryDocumentSnapshots.getDocuments().size());

            for(DocumentSnapshot snap : queryDocumentSnapshots.getDocuments())
            {
                ProductDt productDt = snap.toObject(ProductDt.class);
                ArrayList<String> images = new ArrayList<>();
                if(productDt != null) {
                    if(productDt.productEnable)
                    {
                    productDt.logall();
                    images.add(productDt.productLink);
                    ProductCard productCard = new ProductCard(snap.getId(),
                            productDt.productName,
                            "₹ " + productDt.productPrice,
                            "₹ " + productDt.productOfferPrice,
                            ((float)productDt.productOfferPrice/productDt.productPrice) * 100 + "% OFF",
                            "/" + productDt.productUnit,
                            images);


                    listings.get(0).products.add(productCard);
                    ListViewLayoutParams layoutParams = new ListViewLayoutParams(listings);
                    listing.Load(layoutParams, productclick);


                }}}


        }


    };
}