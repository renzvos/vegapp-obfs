package com.example.vegapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.util.ArrayList;

public class ProductViewer extends AppCompatActivity {

    String productid;
    FirebaseFirestore database;
    FirebaseUser user;

    EcomProductView.LayoutClicks callback = new EcomProductView.LayoutClicks() {
        @Override
        public void AddedtoCart(PviewLayoutParams layoutParams,int Quantity) {

            database.collection("users").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
               FirebaseAppUser appUser = documentSnapshot.toObject(FirebaseAppUser.class);
               FirebaseCartItem cartItem = new FirebaseCartItem(productid,Quantity);
               if(appUser.Cart == null)
               {
                   appUser.Cart = new FirebaseCart();
                   appUser.Cart.items = new ArrayList<FirebaseCartItem>();
               }
               if(appUser.Cart.items == null)
               {
                   appUser.Cart.items = new ArrayList<FirebaseCartItem>();
               }
               appUser.Cart.items.add(cartItem);
               database.collection("users").document(user.getUid()).set(appUser);
                }
            });


        }

        @Override
        public void OnClickLeftButton() {

            finish();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_product_viewer);
        user = FirebaseAuth.getInstance().getCurrentUser();
        productid = getIntent().getStringExtra("pid");
        EcomProductView productView = new EcomProductView(this);
        PviewHolder pviewHolder = new PviewHolder(Color.parseColor("#9C11A9"),"Aamy's Fresh");
        pviewHolder.setLeftButton();
        productView.ProduceLayoutForActivity(R.layout.myntra_layout,pviewHolder,callback);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        database = FirebaseFirestore.getInstance();
        final PviewLayoutParams[] layoutParams = new PviewLayoutParams[1];
        database.collection("products").document(productid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                ArrayList<String> images = new ArrayList<>();
                ProductDt productDt = documentSnapshot.toObject(ProductDt.class);
                images.add(productDt.productLink);
                for(int imgcount = 0 ; imgcount < images.size() ; imgcount++) {
                    Log.i("RZ", "Converting Link: " + images.get(imgcount));
                    StorageReference imageref = null;
                    boolean imagenotexists = false;

                    try
                    {  imageref = storage.getReferenceFromUrl(images.get(imgcount));}
                    catch (Exception e)
                    { Log.i("RZ", "Not an image: " + images.get(imgcount));
                        imagenotexists = true;
                    }

                    if(!imagenotexists) {
                        int finalI = imgcount;
                        imageref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                layoutParams[0].imageurls.set(finalI,uri.toString());
                                layoutParams[0].Logall();
                                productView.notifyDatasetChanged(layoutParams[0]);
                            }
                        });

                    }
                    }

                layoutParams[0] = new PviewLayoutParams(images.size(),
                        productDt.productName,
                        "Rs " + productDt.productPrice,
                        "Rs " + productDt.productOfferPrice,
                        productDt.productDescription,
                        productDt.productUnit
                );
                layoutParams[0].Logall();
                productView.notifyDatasetChanged(layoutParams[0]);



            }
        });


        EcommerceCart ecommerceCart = new EcommerceCart(this);
        final RoundTag roundTag = ecommerceCart.DisplaySideCartIcon((ConstraintLayout) productView.getRootVIew(),"₹0", new EcommerceCart.SideCartIcon() {
            @Override
            public void OnSideCartClicked() {
                startActivity(new Intent(ProductViewer.this,CartActivity.class));
            }
        });

        database.collection("users").document(user.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                FirebaseAppUser appuser = value.toObject(FirebaseAppUser.class);
                CartClass cartClass = new CartClass(30);
                if(appuser.Cart.items != null)
                    for(FirebaseCartItem firebaseCartItem : appuser.Cart.items)
                    {
                        database.collection("products").document(firebaseCartItem.pid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                ProductDt productDt = documentSnapshot.toObject(ProductDt.class);
                                if (productDt != null) {
                                    CartItem cartItem = new CartItem(productDt.productName, productDt.productOfferPrice, firebaseCartItem.quantity, firebaseCartItem.pid, productDt.productLink);
                                    cartClass.AddItem(cartItem);
                                    roundTag.SetText("₹" + cartClass.CalculateTotalBill());
                                }
                            }
                        });
                    }
            }
        });






    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(new Intent(ProductViewer.this, MainActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }
}