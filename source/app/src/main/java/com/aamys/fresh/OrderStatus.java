package com.aamys.fresh;

/*
The file is licenced under MIT and reserves to Arshad Nazir on 28th July 2022 at renzvos.com
 */
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.aamys.fresh.CartClass;
import com.aamys.fresh.CartItem;
import com.aamys.fresh.MainActivity;import com.aamys.fresh.ProductDt;import com.aamys.fresh.ProductViewer;import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.renzvos.ecommerceorderview.CartitemParams;
import com.renzvos.ecommerceorderview.OrderLayoutDesign;
import com.renzvos.ecommerceorderview.OrderViewSetup;


import java.util.ArrayList;

public class OrderStatus extends AppCompatActivity {

    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    OrderViewSetup orderview;
    ArrayList<CartitemParams> cartitemParams = new ArrayList<>();
    String origin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_order_status);
        String orderid = getIntent().getStringExtra("orderid");
        origin  = getIntent().getStringExtra("origin");


        orderview = new OrderViewSetup(this);
        OrderLayoutDesign orderLayoutDesign = new OrderLayoutDesign();
        orderLayoutDesign.SimpleOrderLayout(new OrderLayoutDesign.SimpleLayoutCallback() {
            @Override
            public void ProceedButton() {
                startActivity(new Intent(OrderStatus.this, OrderListActivity.class));
            }

            @Override
            public void ProductClicked(@NonNull CartitemParams pparams) {

                Intent intent =  new Intent(OrderStatus.this, ProductViewer.class);
                intent.putExtra("pid",pparams.getId());
                startActivity(intent);

            }

            @Override
            public void OnClickLeftButton() {
               finish();
            }
        });
        orderview.ProduceLayoutForActivity(orderLayoutDesign);

        firestore.collection("orders").document(orderid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                FirebaseOrderClass orderClass = documentSnapshot.toObject(FirebaseOrderClass.class);
                CartClass cartClass = new CartClass(orderClass.deliverycharge);
                String subbill = "";
                for(FirebaseOrderItem item : orderClass.Items)
                {           ArrayList<String> images = new ArrayList<>();
                            ProductDt productDt = item.productObject;
                            images.add(productDt.productLink);
                            CartItem cartItem = new CartItem(productDt.productName, productDt.productOfferPrice, item.cartobject.quantity, item.cartobject.pid, item.productObject.productLink);
                            cartClass.AddItem(cartItem);

                     subbill = "Sub Total : " + cartClass.SumOfItemPrices() + "\n" +
                            "Delivery Charge: " + cartClass.getDeliveryAmount() + " \n" +
                            "   ";

                }

                ArrayList<CartitemParams> itemsin= ConvertCartClasstoCartLayout(cartClass);
                orderLayoutDesign.setActionBarColor("#9C11A9");
                orderLayoutDesign.setProducts(itemsin);
                orderLayoutDesign.setRightSideNote(orderClass.timestamp);
                orderLayoutDesign.setCardStrongText( "Bill Total: " + orderClass.Amount + "/-");
                orderLayoutDesign.setCardLightText(subbill);

                if(origin.equals("checkout"))
                {
                    orderLayoutDesign.setProceedButtonText("GO TO MY ORDERS");
                    orderLayoutDesign.setProceedButtonColor("#7CB342");
                }
                else if(origin.equals("history"))
                {
                    orderLayoutDesign.setActionBarLeftButton(true);
                }


                if(orderClass.payment)
                {
                  if(orderClass.cancelled)
                    {
                    orderLayoutDesign.setStatusText("Order Cancelled");
                    orderLayoutDesign.setStatusColor("#FFFFFF");
                    orderLayoutDesign.setHeaderBackgroungImage(getDrawable(R.drawable.statusred));
                    orderLayoutDesign.setOrderStatusImage(getDrawable(R.drawable.ic_baseline_close_24));
                     }

                    else if(orderClass.approved)
                    {
                       if(orderClass.intransit)
                       {
                           if(orderClass.delivered)
                           {
                                orderLayoutDesign.setStatusText("Your Order with "+orderClass.Items.size()+" items with order ID "+orderClass.timestamp+" is Delivered");
                                orderLayoutDesign.setStatusColor("#FFFFFF");
                                orderLayoutDesign.setHeaderBackgroungImage(getDrawable(R.drawable.statusgreen));
                                orderLayoutDesign.setOrderStatusImage(getDrawable(R.drawable.ic_baseline_done_outline_24));
                           }
                           else if(!orderClass.delivered)
                           {

                               orderLayoutDesign.setStatusText("Your Order with "+orderClass.Items.size()+" items with order ID "+orderClass.timestamp+" is in Transit");
                               orderLayoutDesign.setStatusColor("#FFFFFF");
                               orderLayoutDesign.setHeaderBackgroungImage(getDrawable(R.drawable.statusgreen));
                               orderLayoutDesign.setOrderStatusImage(getDrawable(R.drawable.ic_baseline_done_outline_24));

                           }
                       }
                       else if (!orderClass.intransit)
                       {
                           orderLayoutDesign.setStatusText("Your Order with "+orderClass.Items.size()+" items with order ID "+orderClass.timestamp+" has been approved");
                           orderLayoutDesign.setStatusColor("#FFFFFF");
                           orderLayoutDesign.setHeaderBackgroungImage(getDrawable(R.drawable.statusgreen));
                           orderLayoutDesign.setOrderStatusImage(getDrawable(R.drawable.ic_baseline_done_outline_24));

                       }
                    }
                    else if(!orderClass.approved)
                    {
                        orderLayoutDesign.setStatusText("Your Order with "+orderClass.Items.size()+" items with order ID "+orderClass.timestamp+" has been placed successfully");
                        orderLayoutDesign.setStatusColor("#FFFFFF");
                        orderLayoutDesign.setHeaderBackgroungImage(getDrawable(R.drawable.statusgreen));
                        orderLayoutDesign.setOrderStatusImage(getDrawable(R.drawable.ic_baseline_done_outline_24));

                    }

                }
                else if(!orderClass.payment)
                {
                    orderLayoutDesign.setStatusText("Payment Failed");
                    orderLayoutDesign.setStatusColor("#FFFFFF");
                    orderLayoutDesign.setHeaderBackgroungImage(getDrawable(R.drawable.statusred));
                    orderLayoutDesign.setOrderStatusImage(getDrawable(R.drawable.ic_baseline_close_24));
                }

                orderview.UpdateLayoutParams(orderLayoutDesign);
            }
        });


    }


    @Override
    public void onBackPressed() {

        if(origin.equals("checkout"))
        {
            startActivity(new Intent(OrderStatus.this, MainActivity.class));
        }
        else if(origin.equals("history"))
        {
            finish();
        }

    }



    public ArrayList<CartitemParams> ConvertCartClasstoCartLayout(CartClass cartClass){
        ArrayList<CartitemParams> cartitems = new ArrayList<>();
        for(CartItem ccitem : cartClass.getItems())
        {
            Log.i("App-OrderStatus", "ConvertCartClasstoCartLayout: ");
            cartitems.add(new CartitemParams(ccitem.getProductName(),"₹" + ccitem.getPrice()," x " + ccitem.getQuantity(),ccitem.getID(),ccitem.getImages()));
        }
        return cartitems;
    }



}