package com.aamys.fresh;

/*
The file is licenced under MIT and reserves to Arshad Nazir on 28th July 2022 at renzvos.com
 */
import android.util.Log;

import com.aamys.fresh.FirebaseCart;import com.aamys.fresh.FirebaseCartItem;import com.aamys.fresh.ProductDt;import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ItemsOrderArranger {

    ArrayList<FirebaseOrderItem> orderitems = new ArrayList<>();
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    String TAG = "App - Items Order Arranger";

    public void Make(FirebaseCart cart , callback callback)
    {
        ArrayList<FirebaseCartItem> cartItems = cart.items;
        for (FirebaseCartItem item: cartItems)
        {
            FirebaseOrderItem orderitem = new FirebaseOrderItem();
            orderitem.cartobject = item;
            orderitems.add(orderitem);

            firestore.collection("products").document(item.pid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    ProductDt productDt = documentSnapshot.toObject(ProductDt.class);
                    orderitem.productObject = productDt;
                    AddProductData(productDt, item.pid);


                    if(CheckForCompletion(cart.items.size()))
                    {
                        callback.OnComplete(orderitems);
                    }
                }
            });


        }
    }

    void AddProductData(ProductDt productDt,String id)
    {
        for(FirebaseOrderItem orderItem : orderitems)
        {
            if(orderItem.cartobject.pid == id)
            {
                orderItem.productObject = productDt;
                break;
            }
        }
    }


     boolean CheckForCompletion(int size)
    {
        if(orderitems.size() != size)
        {Log.i(TAG, "CheckForCompletion: Size not complete");
            return false;}

        for (FirebaseOrderItem orderItem: orderitems) {
            if(orderItem.productObject == null)
            {
                Log.i(TAG, "CheckForCompletion: Product not found");
                return false;
            }
        }

        return true;
    }

    interface callback {
        public void OnComplete(ArrayList<FirebaseOrderItem> orderItems);

    }


}
