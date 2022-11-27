package com.aamys.fresh;

/*
The file is licenced under MIT and reserves to Arshad Nazir on 28th July 2022 at renzvos.com
 */

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.aamys.fresh.EcommerceCart;
import com.aamys.fresh.CategoryActivity;import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.aamys.fresh.Page.*;
import com.aamys.fresh.RenzvosLocationControl.LocationCallbacks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    RenzvosNavigation navigation;
    RenzvosLocationControl rlocation;
    RZoneManager zonemanager = new RZoneManager();
    public static final String ZONEPREFERENCES = "Zone" ;
    public static final String PAYMENTPREFERENCES = "Pmnt";
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();


    private long pressedTime;
    RenzvosLocationControl.LocationCallbacks callbacks = new LocationCallbacks() {
        @Override
        public void ifNoPermission() {

        }

        @Override
        public void ifLocationOff() {

        }

        @Override
        public void ifPermitted() {
            rlocation.getLastLocation(callbacks);
        }

        @Override
        public void ifNotPermitted() {

        }

        @Override
        public void OnLocationResult(Location location) {
            ProceedwithZoneChecking();
            zonemanager.UpdateCurrentLocation(location);
        }

        @Override
        public void OnAddressGeocoded(Address address) {

        }
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        navigation = new RenzvosNavigation(getApplicationContext(),this);

        if(!FirebaseAppUser.getSignedIn(this))
            startActivity(new Intent(MainActivity.this, Intro.class));

        if (!isServiceRunning(NotificationService.class))
        startService(new Intent(MainActivity.this,NotificationService.class));

        Page homepage = new Page("Home", R.drawable.ic_baseline_home_24, new Page.RZNavigationClickListener() {
            @Override
            public void OnClick() {
                navigation.loadFragment(Home.newInstance());
            }
        });
        Page profilepage = new Page("Profile", R.drawable.ic_baseline_account_circle_24, new Page.RZNavigationClickListener() {
            @Override
            public void OnClick() {
               startActivity(new Intent(new Intent(MainActivity.this, ProfileActivity.class)));
            }
        });
        Page orders = new Page("Orders", R.drawable.ic_baseline_ballot_24, new Page.RZNavigationClickListener() {
            @Override
            public void OnClick() {
                startActivity(new Intent(MainActivity.this,OrderListActivity.class));
            }
        });

        Page logoutpage = new Page("Log out", R.drawable.ic_baseline_exit_to_app_24, new Page.RZNavigationClickListener() {
            @Override
            public void OnClick() {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, Intro.class));
            }
        });

        Page loginpage = new Page("Log in", R.drawable.ic_baseline_login_24, new Page.RZNavigationClickListener() {
            @Override
            public void OnClick() {

                startActivity(new Intent(MainActivity.this, Intro.class));
            }
        });

        Page categories = new Page("Categories", R.drawable.ic_baseline_login_24, new Page.RZNavigationClickListener() {
            @Override
            public void OnClick() {

                startActivity(new Intent(MainActivity.this, CategoryActivity.class));
            }
        });


        ArrayList<Page> pages = new ArrayList<>();
        pages.add(homepage);
        pages.add(profilepage);
        pages.add(orders);
        pages.add(categories);



        if(!FirebaseAppUser.getProvider(this).equals("anonymous"))
        { pages.add(logoutpage);}else{pages.add(loginpage);}
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        database.collection("users").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                FirebaseAppUser appUser = documentSnapshot.toObject(FirebaseAppUser.class);
                String subheading;
                if(appUser.name != null)
                {
                    subheading = appUser.name;
                }
                else
                {
                    if(appUser.phone != null)
                    {
                        subheading = appUser.phone;
                    }
                    else
                    {
                        if(appUser.email != null)
                        {
                            subheading = appUser.email;
                        }
                        else {
                            subheading = "";
                        }
                    }
                }

                navigation.ChangeDrawerContent("Aamy's Fresh" , subheading ,null);
            }
        });

        navigation.OnCreater("Aamy's Fresh" , "" ,null);
        navigation.Load(pages);
        navigation.OpenPage(0);

        rlocation = new RenzvosLocationControl(this);
        rlocation.getLastLocation(callbacks);

        CheckAvailible();





    }

    public void CheckAvailible()
    {MainActivity mainActivity = this;
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(mainActivity, R.style.myDialog));
                    builder.setMessage("Currently we are not accepting orders. \n We will be back soon")
                            .setCancelable(false)
                            .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //  Action for 'NO' Button
                                    dialog.cancel();
                                    finishAffinity();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.setTitle("Not Available");
                    alert.show();


                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return navigation.OnCreateOptionsMenu(menu);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return navigation.ActionBarSelectItem(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        rlocation.HandlePermissionResult(requestCode,permissions,grantResults,callbacks);
    }

    @Override
    public void onResume() {
        super.onResume();

        navigation.CheckPage(0);

    }







    public void ProceedwithZoneChecking(){
        FirebaseFirestore database = FirebaseFirestore.getInstance();
         database.collection("zones").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
             @Override
             public void onComplete(@NonNull Task<QuerySnapshot> task) {
                 if (task.isSuccessful()){

                     ArrayList<Zone> zones = new ArrayList<>();
                     for (QueryDocumentSnapshot zoneshot: task.getResult())
                     {
                         Zone zone = new Zone();
                         Map result = zoneshot.getData();
                         zone.name = result.get("locationName").toString();
                         zone.lat = Double.parseDouble(result.get("lat").toString());
                         zone.log = Double.parseDouble(result.get("log").toString());
                         zone.radius = Double.parseDouble(result.get("radius").toString());
                         zone.zoneid = zoneshot.getId();
                        zones.add(zone);
                     }

                     zonemanager.UpdateZones(zones);
                     Zone zone = zonemanager.getInsideZones();
                     if(zone != null) {
                         SharedPreferences sharedpreferences = getSharedPreferences(ZONEPREFERENCES, Context.MODE_PRIVATE);
                         SharedPreferences.Editor editor = sharedpreferences.edit();
                         for (QueryDocumentSnapshot zoneshot : task.getResult()) {
                             if (zoneshot.getId().equals(zone.zoneid)) {
                                 editor.putFloat("charge", Float.parseFloat(zoneshot.getData().get("charge").toString()));
                             }
                         }

                         editor.apply();
                     }
                     else
                     {
                         SharedPreferences sharedpreferences = getSharedPreferences(ZONEPREFERENCES, Context.MODE_PRIVATE);
                         SharedPreferences.Editor editor = sharedpreferences.edit();
                         ShowDeiveryNotAvailible();
                         editor.putFloat("charge",0);
                         editor.apply();
                     }



                 }else{
                     throw new RuntimeException("Firebase Error - Cannot get Zones");
                 }




             }
         });




    }

    public void ShowDeiveryNotAvailible(){
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.myDialog));
        builder.setMessage("We are sorry, Delivery is not available in your area. Are you sure you want to continue?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                        dialog.cancel();
                        finishAffinity();
                    }
                });
        AlertDialog alert = builder.create();
        alert.setTitle("Delivery Not Available");
        alert.show();

    }

    @Override
    public void onBackPressed() {

        if (pressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            finishAffinity();
        } else {
            Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT).show();
        }
        pressedTime = System.currentTimeMillis();
    }

    private boolean isServiceRunning(Class<?> serviceClass) {

        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i("App Main", "isServiceRunning: OK");
                return true;
            }
        }
        Log.i("App Main", "isServiceRunning: NOT OK");
        return false;
    }









}