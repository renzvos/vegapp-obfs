package com.example.vegapp;

import android.content.Intent;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vegapp.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.renzvos.ecommercecheckout.CheckoutDetails;
import com.renzvos.ecommercecheckout.DetailObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class CheckoutActivity extends AppCompatActivity {
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    FirebaseAppUser appUser;
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    Intent signinintent;

    int billtotal;
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


        checkoutDetails = new CheckoutDetails(this, new CheckoutDetails.ProfileCallback() {
            @Override
            public void ProceedtoPayment() {
                //Check for location
                Log.i("RZPR", "ProceedtoPayment: ");
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

                if(appUser.phone == null || appUser.phone.equals(""))
                {
                    Toast.makeText(getApplicationContext(),"Please provide phone for contact",Toast.LENGTH_LONG).show();
                }
                else {phone = true;}

                if(location && phone)
                {
                    FirebaseOrderClass orderClass = new FirebaseOrderClass();
                    orderClass.appUser = appUser;
                    orderClass.uid = FirebaseAuth.getInstance().getUid();
                    orderClass.timestamp = getCurrentTimeStamp();
                    orderClass.location = appUser.Location;
                    orderClass.approved = false;
                    orderClass.cartItems = appUser.Cart.items;
                    orderClass.payment = true;
                    orderClass.intransit = false;
                    orderClass.phone = appUser.phone;
                    orderClass.Amount = billtotal;
                    firestore.collection("orders").add(orderClass).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Intent intent = new Intent(CheckoutActivity.this,OrderStatus.class);
                            intent.putExtra("orderid", documentReference.getId());
                            startActivity(intent);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(),"Unable to create order",Toast.LENGTH_LONG).show();
                        }
                    });
                }


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
                         appUser.phone = obj.getEditorview().getText().toString();
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
                DetailObject pincode = new DetailObject(1, "Pincode" , commonobjectcallback);
                pincode.setStringvalue(appUser.Location.pincode);
                DetailObject phone = new DetailObject(1, "Phone" , commonobjectcallback);
                phone.setStringvalue(appUser.phone);
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
                            Log.i("RZPR", "OnAddressGeocoded: " + address.toString());
                            com.example.vegapp.Location location = new com.example.vegapp.Location();
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
                    obs.getLabelview().getLayoutParams().width = 250;
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
                CartClass cartClass = new CartClass(30);
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
                    billtotal = cartClass.CalculateTotalBill();
                    checkoutDetails.BillMainText( "Bill Total : " + cartClass.CalculateTotalBill());
                    checkoutDetails.BillSubText( "Sub Total : " + cartClass.SumOfItemPrices() + "\n" +
                            "Delivery Charge: " + cartClass.getDeliveryAmount() + " \n" +
                            "   ");


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
            firestore.collection("users").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    FirebaseAppUser appUserget = documentSnapshot.toObject(FirebaseAppUser.class);
                    Log.i("RZ", "onSuccess: Getting ");

                    if(appUserget == null)
                    {
                        Log.i("RZ", "onSuccess: New User ");
                        appUserget = new FirebaseAppUser();
                        appUserget.Location = new com.example.vegapp.Location();
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
                            startActivity(new Intent(CheckoutActivity.this, MainActivity.class));
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