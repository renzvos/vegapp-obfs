package com.aamys.fresh;

/*
The file is licenced under MIT and reserves to Arshad Nazir on 28th July 2022 at renzvos.com
 */


import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.style.UpdateLayout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.aamys.fresh.CartClass;
import com.aamys.fresh.CartItem;
import com.aamys.fresh.CartStaticLP;
import com.aamys.fresh.EcommerceCart;
import com.aamys.fresh.ProductDt;
import com.aamys.fresh.ProductViewer;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.aamys.fresh.AuthUI;
import com.aamys.fresh.FirebaseAuthUIActivityResultContract;
import com.aamys.fresh.IdpResponse;
import com.aamys.fresh.data.model.FirebaseAuthUIAuthenticationResult;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    EcommerceCart ecommerceCart;
    FirebaseStorage storage= FirebaseStorage.getInstance();
    FirebaseFirestore database;
    String ZONEPREFERENCES = "Zone";
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_cart)
        sharedPreferences = getSharedPreferences(ZONEPREFERENCES, MODE_PRIVATE);
        database = FirebaseFirestore.getInstance();
        CartStaticLP staticLP = new CartStaticLP(Color.parseColor("#9C11A9"),"Aamy's Fresh");
        staticLP.setLeftButton();
         ecommerceCart = new EcommerceCart(staticLP,this);
        ecommerceCart.ProduceLayout(new EcommerceCart.CartClicks() {
            @Override
            public void OnProductRemoved(@NonNull LayoutParamsItems pparams) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                database.collection("users").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        FirebaseAppUser appUser = documentSnapshot.toObject(FirebaseAppUser.class);
                        for(int i = 0 ; i < appUser.Cart.items.size() ; i++)
                        {
                            if (appUser.Cart.items.get(i).pid.equals(pparams.getId()))
                            {
                               appUser.Cart.items.remove(i);
                            }
                        }
                        database.collection("users").document(user.getUid()).set(appUser);
                    }
                });
            }

            @Override
            public void OnClickLeftButton() {
                finish();
            }

            @Override
            public void Checkout() {
                startActivity(new Intent(CartActivity.this, CheckoutActivity.class));
            }

            @Override
            public void ProductClicked(LayoutParamsItems item) {
                Intent intent = new Intent(CartActivity.this,ProductViewer.class);
                intent.putExtra("pid",item.getId());
                startActivity(intent);
            }
        });

        ecommerceCart.getPayImage().setImageDrawable(getDrawable(R.drawable.payicon));



       ReloadCart();





    }

    public CartLayoutParams ConvertCartClasstoCartLayout(CartClass cartClass){
        ArrayList<LayoutParamsItems> cartitems = new ArrayList<>();
        for(CartItem ccitem : cartClass.getItems())
        {
            cartitems.add(new LayoutParamsItems(ccitem.getProductName(),"₹" + ccitem.getPrice()," x " + ccitem.getQuantity(),ccitem.getID(),ccitem.getImages(),"#9C11A9"));
        }


        CartLayoutParams cartLayoutParams;

        if(cartClass.getDeliveryAmount() != -1)
        {
            cartLayoutParams = new CartLayoutParams(cartitems,String.valueOf(cartClass.getDeliveryAmount()),"₹" + cartClass.CalculateTotalBill(), "");
        }
        else
        {
            cartLayoutParams = new CartLayoutParams(cartitems,"-","₹" + cartClass.CalculateTotalBill(), "");
        }


        return cartLayoutParams;
    }





    public void ReloadCart()
    {  FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        database.collection("users").document(user.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                FirebaseAppUser appuser = value.toObject(FirebaseAppUser.class);
                CartClass cartClass = new CartClass(sharedPreferences.getFloat("charge",-1));
                if(appuser.Cart.items != null) {
                    if(appuser.Cart.items.size() == 0)
                    {
                        ecommerceCart.LoadCartFromClass(ConvertCartClasstoCartLayout(cartClass));
                    }
                    for (FirebaseCartItem firebaseCartItem : appuser.Cart.items) {
                        database.collection("products").document(firebaseCartItem.pid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                ArrayList<String> images = new ArrayList<>();
                                ProductDt productDt = documentSnapshot.toObject(ProductDt.class);
                                images.add(productDt.productLink);
                                String fstreurl = images.get(0);
                                storage.getReferenceFromUrl(fstreurl).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        CartItem cartItem = new CartItem(productDt.productName, productDt.productOfferPrice, firebaseCartItem.quantity, firebaseCartItem.pid, uri.toString());
                                        cartClass.AddItem(cartItem);
                                        ecommerceCart.LoadCartFromClass(ConvertCartClasstoCartLayout(cartClass));
                                    }
                                });


                            }
                        });
                    }
                }
                else
                {
                    ecommerceCart.LoadCartFromClass(ConvertCartClasstoCartLayout(cartClass));
                }


            }
        });

    }

}


