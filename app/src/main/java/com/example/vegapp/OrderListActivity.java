package com.example.vegapp;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.vegapp.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.renzvos.ecommerceorderslist.OrderDetails;
import com.renzvos.ecommerceorderslist.OrdersListerVIew;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OrderListActivity extends AppCompatActivity {

    ArrayList<OrderDetails> items = new ArrayList<>();
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    Intent signinintent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OrdersListerVIew ordersListerVIew = new OrdersListerVIew(this);
        ordersListerVIew.ProduceLayoutForActivity(new OrdersListerVIew.OrderListCallback() {
            @Override
            public void OnClick(@NonNull OrderDetails orderDetails) {
                Intent intent = new Intent(OrderListActivity.this, OrderStatus.class);
                intent.putExtra("orderid",orderDetails.getOrderid());
                startActivity(intent);
            }
        });

        firestore.collection("orders").whereEqualTo("uid", FirebaseAuth.getInstance().getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(DocumentSnapshot documentSnapshot:queryDocumentSnapshots.getDocuments())
                {
                    FirebaseOrderClass orderClass = documentSnapshot.toObject(FirebaseOrderClass.class);
                    if(orderClass.payment )
                    {
                        items.add(new OrderDetails(
                                documentSnapshot.getId(),
                                "#089C1C",
                                "Rs " + orderClass.Amount + " /-",
                                orderClass.timestamp,
                                "Placed"
                                ));
                    }
                    else
                    {
                        items.add(new OrderDetails(
                                documentSnapshot.getId(),
                                "#FF514D",
                                "Rs " + orderClass.Amount + " /-",
                                orderClass.timestamp,
                                "Cancelled"
                        ));
                    }
                    ordersListerVIew.AddOrderList(items);
                }
            }
        });

        if(FirebaseAuth.getInstance().getCurrentUser().getIdToken(false).getResult().getSignInProvider().equals("anonymous"))
        {
            List<AuthUI.IdpConfig> providers = Arrays.asList(
                    new AuthUI.IdpConfig.EmailBuilder().build(),
                    new AuthUI.IdpConfig.PhoneBuilder().build()
                    //new AuthUI.IdpConfig.GoogleBuilder().build(),
                    //new AuthUI.IdpConfig.AnonymousBuilder().build()
            );

            signinintent = AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .setLogo(R.drawable.icon)
                    .setTheme(R.style.Theme_VegApp)
                    .build();
            signinintent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

            signInLauncher.launch(signinintent);

        }


    }


    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            new ActivityResultCallback<FirebaseAuthUIAuthenticationResult>() {
                @Override
                public void onActivityResult(FirebaseAuthUIAuthenticationResult result) {
                    onSignInResult(result);
                }
            }
    );

    public void onSignInResult(FirebaseAuthUIAuthenticationResult result)
    {
        IdpResponse response = result.getIdpResponse();
        if (result.getResultCode() == RESULT_OK) {
            // Successfully signed in
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            firestore = FirebaseFirestore.getInstance();
            firestore.collection("users").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    FirebaseAppUser appUserget = documentSnapshot.toObject(FirebaseAppUser.class);
                    Log.i("RZ", "onSuccess: Getting ");

                    if(appUserget == null)
                    {
                        Log.i("RZ", "onSuccess: New User ");
                        appUserget = new FirebaseAppUser();
                        appUserget.Location = new Location();
                        appUserget.Cart = new FirebaseCart();
                        appUserget.uid = user.getUid();
                        appUserget.name = user.getDisplayName();
                        if(user.getPhotoUrl() == null)
                        {
                            appUserget.userdpurl = "gs://vegapp-2b3c1.appspot.com/public/avatar.jpg";
                        }
                        else
                        {
                            appUserget.userdpurl = user.getPhotoUrl().toString();
                        }
                        appUserget.email = user.getEmail();
                        appUserget.phone = user.getPhoneNumber();
                    }


                    Log.i("RZ, ", "onSuccess: Setting");
                    appUserget.logall();
                    firestore.collection("users").document(user.getUid()).set(appUserget).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            startActivity(new Intent(OrderListActivity.this, MainActivity.class));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("RZFB", "onFailure: ", e);
                        }
                    });
                }
            });



        } else {

            finish();

        }
    }


}