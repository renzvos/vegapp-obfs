package com.aamys.fresh;

/*
The file is licenced under MIT and reserves to Arshad Nazir on 28th July 2022 at renzvos.com
 */

import static com.aamys.fresh.MainActivity.PAYMENTPREFERENCES;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.widget.Toast;

import com.aamys.fresh.CartClass;
import com.aamys.fresh.CartItem;
import com.aamys.fresh.FirebaseAppUser;import com.aamys.fresh.FirebaseCartItem;import com.aamys.fresh.FirebaseOrderClass;import com.aamys.fresh.FirebaseOrderItem;import com.aamys.fresh.FirebasePreferenceDocument;import com.aamys.fresh.ItemsOrderArranger;import com.aamys.fresh.PaymentAcitivity;import com.aamys.fresh.ProductDt;import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.type.DateTime;
import com.renzvos.ecommercecheckout.CheckoutDetails;
import com.renzvos.ecommercecheckout.DetailObject;
import com.aamys.fresh.AuthUI;
import com.aamys.fresh.FirebaseAuthUIActivityResultContract;
import com.aamys.fresh.IdpResponse;
import com.aamys.fresh.data.model.FirebaseAuthUIAuthenticationResult;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class CheckoutActivity extends AppCompatActivity {
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    FirebaseAppUser appUser;
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    public static final String USERPREFERNECES = "userdata";
    Intent signinintent;
    public static final String ZONEPREFERENCES = "Zone" ;
    SharedPreferences sharedpreferences;

    float billtotal;
    CheckoutDetails checkoutDetails;
    DetailObject.OnObjectCallbacks commonobjectcallback = new DetailObject.OnObjectCallbacks() {
        @Override
        public void OnObjectEditingFinished(@NonNull DetailObject profileObjects) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_checkout);
        sharedpreferences = getSharedPreferences(ZONEPREFERENCES, Context.MODE_PRIVATE);
        CheckAvailible();
        checkoutDetails = new CheckoutDetails(this, new CheckoutDetails.ProfileCallback() {
            @Override
            public void ProceedtoPayment() {
                //Check for location
                Log.i("App-Checkout", "ProceedtoPayment: ");
                boolean location= false;
                boolean phone = false;
                if(appUser.Location.log == null || appUser.Location.lat == null)
                {
                    if(appUser.Location.landmark == null ||  appUser.Location.landmark.equals(""))
                    {
                        Toast.makeText(getApplicationContext(),"Could'nt get Location , Please enter Landmark",Toast.LENGTH_LONG).show();
                    }
                    else if (appUser.Location.street == null  ||  appUser.Location.street.equals(""))
                    {
                        Toast.makeText(getApplicationContext(),"Could'nt get Location , Please enter Street",Toast.LENGTH_LONG).show();
                    }
                    else if(appUser.Location.pincode == null  ||  appUser.Location.pincode.equals(""))
                    {
                        Toast.makeText(getApplicationContext(),"Could'nt get Location , Please enter Pincode",Toast.LENGTH_LONG).show();
                    }
                    else
                        {
                            location = true;
                        }
                }
                else
                {
                    location = true;
                }

                if(appUser.phone == null || appUser.phone.equals("") || appUser.phone.length() != 13)
                {
                    Toast.makeText(getApplicationContext(),"Please provide phone for contact",Toast.LENGTH_LONG).show();
                }
                else {phone = true;}

                float deliverycharge = sharedpreferences.getFloat("charge",0);
                if(deliverycharge != 0){
                    if(location && phone)
                    {
                        ItemsOrderArranger itemsOrderArranger = new ItemsOrderArranger();
                        itemsOrderArranger.Make(appUser.Cart, new ItemsOrderArranger.callback() {
                            @Override
                            public void OnComplete(ArrayList<FirebaseOrderItem> orderItems) {


                                FirebaseOrderClass orderClass = new FirebaseOrderClass();
                                orderClass.appUser = appUser;
                                orderClass.timestamp = getCurrentTimeStamp();
                                orderClass.delivery = appUser.Location;
                                orderClass.approved = false;
                                orderClass.Items = orderItems;
                                orderClass.payment = false;
                                orderClass.intransit = false;
                                orderClass.cancelled = false;
                                orderClass.deliverycharge = deliverycharge;
                                orderClass.delivered = false;
                                orderClass.phone = appUser.phone;
                                orderClass.Amount = billtotal;
                                orderClass.uid = firebaseUser.getUid();

                                firestore.collection("orders").add(orderClass).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {

                                        Intent intent = new Intent(CheckoutActivity.this,PaymentAcitivity.class);
                                        intent.putExtra("orderid", documentReference.getId());
                                        intent.putExtra("origin", "checkout");
                                        startActivity(intent);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(),"Unable to create order",Toast.LENGTH_LONG).show();
                                    }
                                });



                            }
                        });

                    }
                }else
                { Toast.makeText(getApplicationContext(),"Delivery is not availible in your area",Toast.LENGTH_SHORT).show(); }
            }
        });

        checkoutDetails.ProduceLayoutForActivity(getDrawable(R.drawable.payicon));
        checkoutDetails.SetToolbar("#9C11A9", "Checkout", true, new CheckoutDetails.OnClickLeftButton() {
            @Override
            public void OnClick() {
                finish();
            }
        });



        checkoutDetails.EditAll(new CheckoutDetails.EditAllCallback() {
            @Override
            public void OnEdit(@NonNull ArrayList<DetailObject> objects) {
                for (DetailObject obj: objects) {
                    if(obj.getLabel().equals("Phone"))
                    {
                        obj.getEditorview() .setText(appUser.phone.substring(3));
                    }
                }
            }

            @Override
            public boolean OnSaved(@NonNull ArrayList<DetailObject> objects) {
                for (DetailObject obj: objects) {
                 switch (obj.getLabel())
                 {
                     case "Landmark":
                         appUser.Location.landmark = obj.getEditorview().getText().toString();
                         break;
                     case "Street":
                         appUser.Location.street = obj.getEditorview().getText().toString();
                         break;
                     case "City":
                         appUser.Location.city = obj.getEditorview().getText().toString();
                         break;
                     case "District":
                         appUser.Location.district = obj.getEditorview().getText().toString();
                         break;
                     case "Pincode":
                         appUser.Location.pincode = obj.getEditorview().getText().toString();
                        break;
                     case "Phone":
                         appUser.phone = "+91" + obj.getEditorview().getText().toString();
                         break;
                 }
                }

                firestore.collection("users").document(firebaseUser.getUid()).set(appUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        LoadDataFromFirebase();
                        checkoutDetails.EditallCompleted();
                    }
                });

                return false;
            }
        });



        LoadDataFromFirebase();
        SetCartVal();

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

    public void LoadDataFromFirebase()
    {
        final AppCompatActivity appCompatActivity = this;
        firestore.collection("users").document(firebaseUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                 appUser = documentSnapshot.toObject(FirebaseAppUser.class);
                checkoutDetails.EmptyFields();
                DetailObject landmark = new DetailObject(1, "Landmark", commonobjectcallback);
                landmark.setStringvalue(appUser.Location.landmark);
                DetailObject street = new DetailObject(1, "Street", commonobjectcallback);
                street.setStringvalue(appUser.Location.street);
                DetailObject city = new DetailObject(1, "City" , commonobjectcallback);
                city.setStringvalue(appUser.Location.city);
                DetailObject district = new DetailObject(1, "District", commonobjectcallback);
                district.setStringvalue(appUser.Location.district);
                DetailObject pincode = new DetailObject(3, "Pincode" , commonobjectcallback);
                pincode.setStringvalue(appUser.Location.pincode);
                DetailObject phone = new DetailObject(2, "Phone" , commonobjectcallback);
                phone.setPhoneCode("+91");
                if(appUser.phone == null || appUser.phone.equals(""))
                {
                    phone.setStringvalue("+91");
                }
                else
                {
                    phone.setStringvalue(appUser.phone);
                }

                checkoutDetails.NewDeliveryField(landmark);
                checkoutDetails.NewDeliveryField(street);
                checkoutDetails.NewDeliveryField(city);
                checkoutDetails.NewDeliveryField(district);
                checkoutDetails.NewDeliveryField(pincode);
                checkoutDetails.NewDeliveryField(phone);

                if (appUser.Location.lat == null || appUser.Location.log == null)
                {
                    RenzvosLocationControl rlocation = new RenzvosLocationControl(appCompatActivity);
                    rlocation.getLastLocation(new RenzvosLocationControl.LocationCallbacks() {
                        @Override
                        public void ifNoPermission() {

                        }

                        @Override
                        public void ifLocationOff() {

                        }

                        @Override
                        public void ifPermitted() {

                        }

                        @Override
                        public void ifNotPermitted() {

                        }

                        @Override
                        public void OnLocationResult(Location location) {

                        }

                        @Override
                        public void OnAddressGeocoded(Address address) {
                            Log.i("App-Checkout", "OnAddressGeocoded: " + address.toString());
                            com.aamys.fresh.Location location = new com.aamys.fresh.Location();
                            location.landmark = address.getFeatureName();
                            location.district = address.getSubAdminArea();
                            location.city = address.getLocality();
                            location.pincode = address.getPostalCode();
                            location.log = String.valueOf(address.getLongitude());
                            location.lat = String.valueOf(address.getLatitude());
                            appUser.Location = location;
                            firestore.collection("users").document(firebaseUser.getUid()).set(appUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                    LoadDataFromFirebase();
                                }
                            });
                            checkoutDetails.StopLoadingBar();

                        }
                    });
                }
                else
                    {
                        checkoutDetails.StopLoadingBar();
                    }

                for(DetailObject obs : checkoutDetails.getObjects())
                {
                    obs.getLabelview().getLayoutParams().width = 270;
                }
            }
        });

    }

    public void SetCartVal( )
    {
        firestore.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                FirebaseAppUser appuser = value.toObject(FirebaseAppUser.class);

                CartClass cartClass = new CartClass(sharedpreferences.getFloat("charge",-1));
                if(appuser.Cart.items != null) {
                    if(appuser.Cart.items.size() == 0)
                    {

                    }
                    for (FirebaseCartItem firebaseCartItem : appuser.Cart.items) {
                        firestore.collection("products").document(firebaseCartItem.pid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                ArrayList<String> images = new ArrayList<>();
                                ProductDt productDt = documentSnapshot.toObject(ProductDt.class);
                                images.add(productDt.productLink);
                    CartItem cartItem = new CartItem(productDt.productName, productDt.productOfferPrice, firebaseCartItem.quantity, firebaseCartItem.pid, "");
                    cartClass.AddItem(cartItem);



                    if(cartClass.getDeliveryAmount() != -1)
                    {
                        checkoutDetails.BillSubText( "Sub Total : " + cartClass.SumOfItemPrices() + "\n" +
                                "Delivery Charge: " + cartClass.getDeliveryAmount() + " \n" +
                                "   ");

                        checkoutDetails.BillMainText( "Bill Total : " + cartClass.CalculateTotalBill());
                        billtotal = cartClass.CalculateTotalBill();
                    }
                    else
                    {
                        checkoutDetails.BillSubText( "Sub Total : " + cartClass.SumOfItemPrices() + "\n" +
                                "Delivery Charge: - \n" +
                                "   ");

                        checkoutDetails.BillMainText( "Bill Total : " + cartClass.SumOfItemPrices());

                    }



                            }
                        });
                    }
                }
                else
                {

                }
            }
        });



    }

    public static String getCurrentTimeStamp(){
        try {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentDateTime = dateFormat.format(new Date()); // Find todays date

            return currentDateTime;
        } catch (Exception e) {
            e.printStackTrace();

            return null;
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
                    Log.i("App-Checkout", "onSuccess: Getting ");

                    if(appUserget == null)
                    {
                        Log.i("App-Checkout", "onSuccess: New User ");
                        appUserget = new FirebaseAppUser();
                        appUserget.Location = new com.aamys.fresh.Location();
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
                    appUserget.Cart = appUser.Cart;


                    Log.i("App-Checkout", "onSuccess: Setting");
                    appUserget.logall();
                    firestore.collection("users").document(user.getUid()).set(appUserget).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            startActivity(new Intent(CheckoutActivity.this, CheckoutActivity.class));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("App-Checkout", "onFailure: ", e);
                        }
                    });
                }
            });



        } else {

            finish();

        }
    }

    public void CheckAvailible()
    {CheckoutActivity checkoutActivity = this;
        firestore.collection("preferences").document("DwjLV65ODWlXDTtkEsdB").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                FirebasePreferenceDocument pref = documentSnapshot.toObject(FirebasePreferenceDocument.class);
                SharedPreferences sharedpreferences = getSharedPreferences(PAYMENTPREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("mid",pref.paytmMid);
                editor.putString("secret",pref.paytmSecret);
                editor.putString("staging",pref.paytmStaging);
                editor.apply();
                if(!pref.filling)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(checkoutActivity, R.style.myDialog));
                    builder.setMessage("Currently we are not accepting orders. \n We will be back soon")
                            .setCancelable(false)
                            .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //  Action for 'NO' Button
                                    dialog.cancel();
                                    finish();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.setTitle("Not Available");
                    alert.show();


                }
            }
        });
    }


}