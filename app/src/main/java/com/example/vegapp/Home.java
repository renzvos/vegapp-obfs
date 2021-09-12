package com.example.vegapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.util.ArrayList;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link #newInstance} factory method to
 * create an instance of this fragment.
 */
public class Home extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public FirebaseStorage storage;


    // TODO: Rename and change types of parameters
    private EcommerceMain.LayoutCallbacks layoutCallbacks = new EcommerceMain.LayoutCallbacks() {


        @Override
        public void Searched(String Text) {
            Log.i("RZ", "Searched: " + Text);

        }

        @Override
        public void ProductTapped(ProductCard card) {
            Intent intent = new Intent(getActivity(),ProductViewer.class);
            intent.putExtra("pid",card.pid);
            startActivity(intent);
        }
    };

    public Home() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     *
     *
     * @return A new instance of fragment HomeFragement.
     */
    // TODO: Rename and change types and number of parameters
    public static Home newInstance() {
        Home fragment = new Home();
        Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        EcommerceMain ecommerceMain = new EcommerceMain(getContext(),this);
        ArrayList<ProductCard> productCards = new ArrayList<>();
        // productCards.add(new ProductCard("12","asda","asda","asd"));
        ArrayList<Listings> listings = new ArrayList<>();
        //listings.add(new Listings("title" , productCards,Listings.HORIZONTAL_LAYOUT));
        ArrayList<String> bannerurls = new ArrayList<>();
        //bannerurls.add("https://www.primopt.com/wp-content/uploads/2018/04/Home-Four-Banner-Background-Image-1.png");
        RZEcomLayoutParams layoutParams = new RZEcomLayoutParams(bannerurls,listings);
        View view = ecommerceMain.ProduceLayoutForFragment(inflater, container, layoutCallbacks);
        ecommerceMain.notifyparams(layoutParams);
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore firebaseFirestore =  FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        firebaseFirestore.collection("banner").get().addOnCompleteListener(
                new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful())
                        {
                            for(QueryDocumentSnapshot documentSnapshot : task.getResult())
                            {
                                Map bannerdata = documentSnapshot.getData();
                                String gstoragelink =(String) bannerdata.get("bannerLink");
                                StorageReference imageref= storage.getReferenceFromUrl(gstoragelink);
                                imageref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                bannerurls.add(uri.toString());
                                                RZEcomLayoutParams layoutParamsbanner = new RZEcomLayoutParams(bannerurls,listings);
                                                ecommerceMain.notifyparams(layoutParamsbanner);
                                            }
                                        });

                            }

                        }
                        else
                        {
                            Log.i("RZ", "onComplete: Error");
                        }



                    }
                }
        );

        firebaseFirestore.collection("frontpagelist").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                for(QueryDocumentSnapshot documentSnapshot : task.getResult())
                {

                    Map listingdata = documentSnapshot.getData();
                    String title = (String) listingdata.get("title");
                    ArrayList<String> productids = (ArrayList<String>) listingdata.get("products");
                    Listings thislisting = new Listings(title,new ArrayList<>(),Listings.HORIZONTAL_LAYOUT);
                    listings.add(thislisting);
                    RZEcomLayoutParams layoutParams2 = new RZEcomLayoutParams(bannerurls,listings);
                    ecommerceMain.notifyparams(layoutParams2);
                    for (String productid : productids)
                    {
                        firebaseFirestore.collection("products").document(productid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                Log.i("RZ", "onSuccess: " + documentSnapshot.getData());
                                ProductDt productDt = documentSnapshot.toObject(ProductDt.class);
                                ArrayList<String> images = new ArrayList<>();
                                if(productDt != null) {
                                    productDt.logall();
                                    images.add(productDt.productLink);
                                    ProductCard productCard = new ProductCard(productid,
                                            productDt.productName,
                                            "₹ " + productDt.productPrice,
                                            "₹ " + productDt.productOfferPrice,
                                            ((float)productDt.productOfferPrice/productDt.productPrice) * 100 + "% OFF",
                                            "/" + productDt.productUnit,
                                            images);

                                    for (int i = 0; i < listings.size(); i++) {
                                        if (listings.get(i).title == title) {
                                            listings.get(i).products.add(productCard);
                                        }
                                    }

                                    RZEcomLayoutParams layoutParams2 = new RZEcomLayoutParams(bannerurls, listings);
                                    ecommerceMain.notifyparams(layoutParams2);

                                    for (int imgcount = 0; imgcount < images.size(); imgcount++) {
                                        Log.i("RZ", "Converting Link: " + images.get(imgcount));
                                        StorageReference imageref = null;
                                        boolean imagenotexists = false;
                                        try {
                                            imageref = storage.getReferenceFromUrl(images.get(imgcount));
                                        } catch (Exception e) {
                                            Log.i("RZ", "Not an image: " + images.get(imgcount));
                                            imagenotexists = true;
                                        }

                                        if (!imagenotexists) {

                                            int finalI = imgcount;
                                            imageref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    Log.i("RZ", "Got the link: for " + title + " in " + productid + " img " + finalI);
                                                    for (int i = 0; i < listings.size(); i++) {
                                                        Log.i("RZ", "Looping through listing");
                                                        if (listings.get(i).title == title) {
                                                            Log.i("RZ", "Title found" + i);
                                                            for (int j = 0; j < listings.get(i).products.size(); j++) {
                                                                Log.i("RZ", "Looping through products");
                                                                if (listings.get(i).products.get(j).pid == productid) {
                                                                    Log.i("RZ", "Product found" + j);
                                                                    listings.get(i).products.get(j).imagelinks.set(finalI, uri.toString());
                                                                    RZEcomLayoutParams layoutParams2 = new RZEcomLayoutParams(bannerurls, listings);
                                                                    //layoutParams2.LogDataChange();
                                                                    ecommerceMain.notifyparams(layoutParams2);
                                                                }

                                                            }

                                                        }

                                                    }


                                                }
                                            });


                                        }

                                    }

                                }




                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("RZ", "onFailure:e",e);
                            }
                        });
                    }




                }


            }
        });


        EcommerceCart ecommerceCart = new EcommerceCart((AppCompatActivity) getActivity());
        final RoundTag roundTag = ecommerceCart.DisplaySideCartIcon((ConstraintLayout) ecommerceMain.getRootView(),"₹0", new EcommerceCart.SideCartIcon() {
            @Override
            public void OnSideCartClicked() {
                startActivity(new Intent(getActivity(),CartActivity.class));
            }
        });

        firebaseFirestore.collection("users").document(user.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                FirebaseAppUser appuser = value.toObject(FirebaseAppUser.class);
                CartClass cartClass = new CartClass(30);
                if(appuser.Cart.items != null)
                    for(FirebaseCartItem firebaseCartItem : appuser.Cart.items)
                    {
                        firebaseFirestore.collection("products").document(firebaseCartItem.pid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {

                                ProductDt productDt = documentSnapshot.toObject(ProductDt.class);
                                CartItem cartItem = new CartItem(productDt.productName,productDt.productOfferPrice, firebaseCartItem.quantity, firebaseCartItem.pid,productDt.productLink);
                                cartClass.AddItem(cartItem);
                                roundTag.SetText("₹" + cartClass.CalculateTotalBill());
                            }
                        });
                    }
            }
        });



        
  




        return view;

    }







}

