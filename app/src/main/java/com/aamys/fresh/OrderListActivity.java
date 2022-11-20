package com.aamys.fresh;

/*
The file is licenced under MIT and reserves to Arshad Nazir on 28th July 2022 at renzvos.com
 */
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.aamys.fresh.MainActivity;import com.aamys.fresh.OrderStatus;import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.renzvos.ecommerceorderslist.OrderDetails;
import com.renzvos.ecommerceorderslist.OrdersListerVIew;
import com.aamys.fresh.AuthUI;
import com.aamys.fresh.FirebaseAuthUIActivityResultContract;
import com.aamys.fresh.IdpResponse;
import com.aamys.fresh.data.model.FirebaseAuthUIAuthenticationResult;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class OrderListActivity extends AppCompatActivity {

    ArrayList<OrderDetails> items = new ArrayList<>();
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    Intent signinintent;
    public static final String USERPREFERNECES = "userdata";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OrdersListerVIew ordersListerVIew = new OrdersListerVIew(this);
        ordersListerVIew.ProduceLayoutForActivity("Order History","#9C11A9",new OrdersListerVIew.OrderListCallback() {
            @Override
            public void OnClick(@NonNull OrderDetails orderDetails) {
                Intent intent = new Intent(OrderListActivity.this, OrderStatus.class);
                intent.putExtra("orderid",orderDetails.getOrderid());
                intent.putExtra("origin", "history");
                startActivity(intent);
            }
        });

        ordersListerVIew.SetBackButton(new OrdersListerVIew.OnClickBackButtonListener() {
            @Override
            public void OnClick() {
               finish();
            }
        });

        firestore.collection("orders").whereEqualTo("uid", FirebaseAuth.getInstance().getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(DocumentSnapshot documentSnapshot:queryDocumentSnapshots.getDocuments())
                {
                    FirebaseOrderClass orderClass = documentSnapshot.toObject(FirebaseOrderClass.class);
                    Date date = ConvertoDate(orderClass.timestamp);
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                    String strDate= formatter.format(date);



                    if(orderClass.payment)
                    {
                        if(orderClass.cancelled)
                        {
                            items.add(new OrderDetails(documentSnapshot.getId(),
                                    orderClass.timestamp,
                                    "#C32420",
                                    "Rs " + orderClass.Amount + " /-",
                                    strDate,
                                    "Cancelled",
                                    orderClass.timestamp)
                                    );


                        }

                        else if(orderClass.approved)
                        {
                            if(orderClass.intransit)
                            {
                                if(orderClass.delivered)
                                {
                                    items.add(new OrderDetails(documentSnapshot.getId(),
                                            orderClass.timestamp,
                                            "#006104",
                                            "Rs " + orderClass.Amount + " /-",
                                            strDate,
                                            "Delivered",
                                            orderClass.timestamp));

         }
                                else if(!orderClass.delivered)
                                {
                                    items.add(new OrderDetails(documentSnapshot.getId(),
                                            orderClass.timestamp,
                                            "#006104",
                                            "Rs " + orderClass.Amount + " /-",
                                            strDate,
                                            "In Transit",
                                            orderClass.timestamp));


                                }
                            }
                            else if (!orderClass.intransit)
                            {
                                items.add(new OrderDetails(documentSnapshot.getId(),
                                        orderClass.timestamp,
                                        "#006104",
                                        "Rs " + orderClass.Amount + " /-",
                                        strDate,
                                        "Approved",
                                        orderClass.timestamp));



                            }
                        }
                        else if(!orderClass.approved)
                        {
                            items.add(new OrderDetails(documentSnapshot.getId(),
                                    orderClass.timestamp,
                                    "#006104",
                                    "Rs " + orderClass.Amount + " /-",
                                    strDate,
                                    "Placed",
                                    orderClass.timestamp));

                        }

                    }
                    else if(!orderClass.payment)
                    {
                        items.add(new OrderDetails(documentSnapshot.getId(),
                                orderClass.timestamp,
                                "#C32420",
                                "Rs " + orderClass.Amount + " /-",
                                strDate,
                                "Payment Failed",
                                orderClass.timestamp));

                      }




                }
                ordersListerVIew.AddOrderList(items);
            }
        });

        if(FirebaseAppUser.getProvider(this).equals("anonymous"))
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
            SharedPreferences sharedPreferences = getSharedPreferences(USERPREFERNECES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("provider",FirebaseAuth.getInstance().getCurrentUser().getIdToken(false).getResult().getSignInProvider());
            editor.apply();
            firestore.collection("users").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    FirebaseAppUser appUserget = documentSnapshot.toObject(FirebaseAppUser.class);
                    Log.i("App-OrderList", "onSuccess: Getting ");

                    if(appUserget == null)
                    {
                        Log.i("App-OrderList", "onSuccess: New User ");
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


                    Log.i("App-OrderList, ", "onSuccess: Setting");
                    appUserget.logall();
                    firestore.collection("users").document(user.getUid()).set(appUserget).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            startActivity(new Intent(OrderListActivity.this, MainActivity.class));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("App-OrderList", "onFailure: ", e);
                        }
                    });
                }
            });



        } else {

            finish();

        }
    }


    public Date ConvertoDate(String dtStart)
    {
        Date date;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
          date = format.parse(dtStart);
        } catch (ParseException e) {
            date = null;
            e.printStackTrace();
        }
        return date;
    }

}