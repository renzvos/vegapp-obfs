package com.aamys.fresh;

/*
The file is licenced under MIT and reserves to Arshad Nazir on 28th July 2022 at renzvos.com
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.view.Window;

import com.aamys.fresh.CartClass;
import com.aamys.fresh.CartItem;
import com.aamys.fresh.EcommerceCart;
import com.aamys.fresh.CartActivity;import com.aamys.fresh.FirebaseAppUser;import com.aamys.fresh.FirebaseCartItem;import com.aamys.fresh.ProductDt;import com.aamys.fresh.ProductViewer;import com.aamys.fresh.SearchActivity;import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.rpc.context.AttributeContext;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import org.json.JSONObject;

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
    private FirebaseFirestore database = FirebaseFirestore.getInstance();
    RoundTag roundTag;
    boolean duplicateerrorflag = false;
    ArrayList<Listings> listings = new ArrayList<>();
    String ZONEPREFERENCES = "Zone";
    SharedPreferences sharedPreferences;


    // TODO: Rename and change types of parameters
    private EcommerceMain.LayoutCallbacks layoutCallbacks = new EcommerceMain.LayoutCallbacks() {


        @Override
        public void Searched(String Text) {
            /*
            if (duplicateerrorflag == false) {
                duplicateerrorflag = true;
                Log.i("RZ", "Searched: " + Text);
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                intent.putExtra("keyword", Text);
                startActivity(intent);

            }
            */
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
    public void onResume() {
        super.onResume();
        duplicateerrorflag = false;
        SetCartVal(roundTag);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        sharedPreferences = getActivity().getSharedPreferences(ZONEPREFERENCES, Context.MODE_PRIVATE);


        EcommerceMain ecommerceMain = new EcommerceMain(getContext(),this);

        ArrayList<String> bannerurls = new ArrayList<>();
        RZEcomLayoutParams layoutParams = new RZEcomLayoutParams(bannerurls,listings);
        View view = ecommerceMain.ProduceLayoutForFragment(inflater, container, layoutCallbacks);
        ecommerceMain.notifyparams(layoutParams);
        ecommerceMain.StartLoading();

        ecommerceMain.autocomplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!duplicateerrorflag){
                    duplicateerrorflag = true;
                startActivity(new Intent(getActivity(), SearchActivity.class));
            }}
        });

        ecommerceMain.autocomplete.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!duplicateerrorflag) {
                    duplicateerrorflag = true;
                    startActivity(new Intent(getActivity(), SearchActivity.class));
                    view.clearFocus();
                }
            }
        });

        ecommerceMain.autocomplete.setHint("Tap to Search");


        storage = FirebaseStorage.getInstance();
        database.collection("banner").get().addOnCompleteListener(
                new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful())
                        {
                            for(QueryDocumentSnapshot documentSnapshot : task.getResult())
                            {
                                Map bannerdata = documentSnapshot.getData();
                                String gstoragelink =(String) bannerdata.get("bannerLink");
                                boolean enable = (boolean) bannerdata.get("bannerEnable");
                                if(enable) {
                                    StorageReference imageref = storage.getReferenceFromUrl(gstoragelink);
                                    imageref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            bannerurls.add(uri.toString());
                                            RZEcomLayoutParams layoutParamsbanner = new RZEcomLayoutParams(bannerurls, listings);
                                            ecommerceMain.notifyparams(layoutParamsbanner);
                                        }
                                    });
                                }

                            }

                        }
                        else
                        {
                            Log.i("App-Home", "onComplete: Error");
                        }



                    }
                }
        );



                        database.collection("products").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                    Log.i("App-Home", "onSuccess: " + documentSnapshot.getData());
                                    ProductDt productDt = documentSnapshot.toObject(ProductDt.class);
                                    ArrayList<String> images = new ArrayList<>();
                                    if (productDt != null) {
                                        if (productDt.productEnable) {
                                            images.add(productDt.productLink);


                                            ProductCard productCard = new ProductCard(documentSnapshot.getId(),
                                                    productDt.productName,
                                                    "₹ " + productDt.productPrice,
                                                    "₹ " + productDt.productOfferPrice,
                                                    Math.round(((1 - (float) productDt.productOfferPrice / productDt.productPrice)) * 10000) / 100 + "% OFF",
                                                    "/" + productDt.productUnit,
                                                    images);

                                            String title = "Our Products";
                                            AddProduct(title, productCard);


                                            RZEcomLayoutParams layoutParams2 = new RZEcomLayoutParams(bannerurls, listings);
                                            ecommerceMain.notifyparams(layoutParams2);


                                            for (int imgcount = 0; imgcount < images.size(); imgcount++) {
                                                Log.i("App-Home", "Converting Link: " + images.get(imgcount));
                                                StorageReference imageref = null;
                                                boolean imagenotexists = false;
                                                try {
                                                    imageref = storage.getReferenceFromUrl(images.get(imgcount));
                                                } catch (Exception e) {
                                                    Log.i("App-Home", "Not an image: " + images.get(imgcount));
                                                    imagenotexists = true;
                                                }

                                                if (!imagenotexists) {

                                                    int finalI = imgcount;
                                                    imageref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                        @Override
                                                        public void onSuccess(Uri uri) {

                                                            // Log.i("App-Home", "Got the link: for " + title + " in " + productid + " img " + finalI);
                                                            for (int i = 0; i < listings.size(); i++) {
                                                                //   Log.i("App-Home", "Looping through listing");
                                                                if (listings.get(i).title == title) {
                                                                    //    Log.i("App-Home", "Title found" + i);
                                                                    for (int j = 0; j < listings.get(i).products.size(); j++) {
                                                                        //      Log.i("App-Home", "Looping through products");
                                                                        if (listings.get(i).products.get(j).pid == documentSnapshot.getId()) {
                                                                            //        Log.i("App-Home", "Product found" + j);
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
                                }
                                ecommerceMain.StopLoader();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("App-Home", "onFailure:e",e);
                            }
                        });






        EcommerceCart ecommerceCart = new EcommerceCart((AppCompatActivity) getActivity());
         roundTag = ecommerceCart.DisplaySideCartIcon((ConstraintLayout) ecommerceMain.getRootView(),"₹0", new EcommerceCart.SideCartIcon() {
            @Override
            public void OnSideCartClicked() {
                startActivity(new Intent(getActivity(),CartActivity.class));
            }
        });
        SetCartVal(roundTag);

        return view;

    }





    public void SetCartVal(RoundTag roundTag)
    {
        database.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                FirebaseAppUser appuser = value.toObject(FirebaseAppUser.class);
                CartClass cartClass = new CartClass(sharedPreferences.getFloat("charge",0));
                if(appuser.Cart.items != null)
                    for(FirebaseCartItem firebaseCartItem : appuser.Cart.items)
                    {
                        database.collection("products").document(firebaseCartItem.pid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                ProductDt productDt = documentSnapshot.toObject(ProductDt.class);
                                if(productDt != null) {
                                    if (cartClass.getDeliveryAmount() == -1)
                                    {
                                        CartItem cartItem = new CartItem(productDt.productName, productDt.productOfferPrice, firebaseCartItem.quantity, firebaseCartItem.pid, productDt.productLink);
                                        cartClass.AddItem(cartItem);
                                        roundTag.SetText("₹" + cartClass.CalculateTotalBill());
                                    }
                                    else
                                    {
                                        CartItem cartItem = new CartItem(productDt.productName, productDt.productOfferPrice, firebaseCartItem.quantity, firebaseCartItem.pid, productDt.productLink);
                                        cartClass.AddItem(cartItem);
                                        roundTag.SetText("₹" + cartClass.CalculateTotalBill());
                                    }

                                }
                            }
                        });
                    }
            }
        });



    }

    public void AddProduct(String title, ProductCard productCard)
    {String TAG = "App-Home";
        Log.i(TAG, "New Adding request: ");
        int mainlistlocation = FindMainListing(title,listings);
        if(mainlistlocation == -1)
        {  Log.i(TAG, "Heading row not found Adding Heading");
            Listings emptylisting = new Listings(title,new ArrayList<>());
            emptylisting.HorizontalFitLayout(2);
            listings.add(emptylisting);
            FindRightSpot(title,productCard);
        }
        else
        {
            Log.i(TAG, "Heading row found placing");
            FindRightSpot(title,productCard);
        }
    }

    public void FindRightSpot(String title, ProductCard productCard)
    {
        String TAG = "App-Home";
        Log.i(TAG, "New Product placing request : ");
       int realposition = FindSpace(title);
       /*
       if(realposition == -1)
       {
           Log.i(TAG, "Adding row: ");
           Listings emptylisting = new Listings(title,new ArrayList<>());
           emptylisting.HorizonralScrollableLayout();
           emptylisting.extension = true;
           listings.add(emptylisting);
           FindRightSpot(title,productCard);
       }
       else
       {
           listings.get(realposition).products.add(productCard);
           Log.i(TAG, "Adding Product: on " + realposition);
       }


        */

        listings.get(0).products.add(productCard);

    }

    public int FindSpace(String title)
    {
        String TAG = "App-Home";
        for(int i = 0; i < listings.size() ; i ++)
        {
            Log.i(TAG, "FindSpace: " + listings.get(i).title + title + listings.get(i).products.size());
            if(listings.get(i).title.equals(title) && listings.get(i).products.size() != 2)
            {

                    Log.i(TAG, "The row has : " + listings.get(i).products.size());
                    Log.i(TAG, "Found Space: " + i);
                    return i;

            }
        }
       Log.i(TAG, "Not Found Space: ");
        return -1;


    }

    public int FindMainListing(String title , ArrayList<Listings> listings)
    {
        for(int i = 0; i < listings.size() ; i ++)
        {
            if(listings.get(i).title.equals(title) && listings.get(i).extension == false)
            {
                return i;
            }
        }
        return -1;
    }







}

