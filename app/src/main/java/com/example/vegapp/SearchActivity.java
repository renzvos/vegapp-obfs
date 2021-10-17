package com.example.vegapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.vegapp.EcommerceListing;
import com.google.android.gms.tasks.OnCompleteListener;
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
                Log.i("RZP", "OnSearch: " + Text);
                firestore.collection("products").whereGreaterThanOrEqualTo("productName",Text).get().addOnSuccessListener(resultcame);
            }
        });
        listing.searchview.setHint("Enter your text");
        firestore.collection("products").whereGreaterThanOrEqualTo("productName",keyword).get().addOnSuccessListener(resultcame);





    }

    private OnSuccessListener resultcame = new OnSuccessListener<QuerySnapshot>() {
        @Override
        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

            listings = new ArrayList<>();
            listings.add(new Listings("Search Results",new ArrayList<ProductCard>(),Listings.VERTICAL_LAYOUT));
            for(DocumentSnapshot snap : queryDocumentSnapshots.getDocuments())
            {

                ProductDt productDt = snap.toObject(ProductDt.class);
                ArrayList<String> images = new ArrayList<>();
                if(productDt != null) {
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
                    listing.Load(layoutParams, new EcommerceListing.EcomlistingProductOnClick() {
                        @Override
                        public void OnClick(ProductCard card) {

                            Intent intent = new Intent(SearchActivity.this,ProductViewer.class);
                            intent.putExtra("pid",card.pid);
                            startActivity(intent);

                        }
                    });


                }}


        }


    };
}