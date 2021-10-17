package com.example.vegapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;

import com.example.vegapp.CartClass;
import com.example.vegapp.CartItem;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.renzvos.ecommerceorderview.CartitemParams;
import com.renzvos.ecommerceorderview.OrderLayoutDesign;
import com.renzvos.ecommerceorderview.OrderViewSetup;
import com.example.vegapp.CartLayoutParams;
import com.example.vegapp.LayoutParamsItems;

import java.util.ArrayList;

public class OrderStatus extends AppCompatActivity {

    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    OrderViewSetup orderview;
    ArrayList<CartitemParams> cartitemParams = new ArrayList<>();;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_order_status);
        String orderid = getIntent().getStringExtra("orderid");

        firestore.collection("orders").document(orderid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                FirebaseOrderClass orderClass = documentSnapshot.toObject(FirebaseOrderClass.class);
                CartClass cartClass = new CartClass(30);
                if(orderClass.cartItems != null)
                    for(FirebaseCartItem firebaseCartItem : orderClass.cartItems)
                    {
                        firestore.collection("products").document(firebaseCartItem.pid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                ProductDt productDt = documentSnapshot.toObject(ProductDt.class);
                                if(productDt != null) {
                                    cartClass.AddItem(new CartItem(productDt.productName, productDt.productPrice, firebaseCartItem.quantity, firebaseCartItem.pid,productDt.productLink));
                                    cartitemParams.add(new CartitemParams(productDt.productName,
                                            "Rs " + productDt.productOfferPrice,
                                            "X " + firebaseCartItem.quantity,
                                            firebaseCartItem.pid ,
                                            productDt.productLink,""));
                                    String subtext = "Sub Total :  "+ cartClass.SumOfItemPrices() +
                                            "\nDelivery Charge: " + cartClass.getDeliveryAmount() + "\n ";
                                    String billtotal = "Bill Total \n ₹ " + orderClass.Amount;

                                    orderview.getLayoutDesign().getCartproductsadapter().Update(cartitemParams);
                                    orderview.getLayoutDesign().getCartproductsadapter().notifyDataSetChanged();
                                    if(orderClass.payment)
                                    {
                                        orderview.getLayoutDesign().UpdateSimpleOrderLayout(
                                                "Your Order had been placed",
                                                "#FFFFFF",
                                                orderClass.timestamp,
                                                getDrawable(R.drawable.statusgreen),
                                                billtotal ,
                                                subtext);
                                    }
                                    else
                                    {    orderview.getLayoutDesign().UpdateSimpleOrderLayout(
                                                    "Order Cancelled",
                                                    "#FFFFFF",
                                                    orderClass.timestamp,
                                                    getDrawable(R.drawable.statusred),
                                                    billtotal ,
                                                    subtext);

                                    }


                                }
                            }
                        });
                    }


            }
        });


        orderview = new OrderViewSetup(this);
        OrderLayoutDesign orderLayoutDesign = new OrderLayoutDesign();
        orderLayoutDesign.SimpleOrderLayout(new OrderLayoutDesign.SimpleLayoutCallback() {
            @Override
            public void ProductClicked(@NonNull CartitemParams pparams) {

            }

            @Override
            public void OnClickLeftButton() {

            }
        });
        orderview.ProduceLayoutForActivity(orderLayoutDesign);


    }


    @Override
    public void onBackPressed() {

        startActivity(new Intent(OrderStatus.this, MainActivity.class));

    }

    }