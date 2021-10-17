package com.example.vegapp;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
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

import com.example.vegapp.EcommerceCart;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.example.vegapp.Page;
import com.example.vegapp.RenzvosNavigation;
import com.example.vegapp.RZoneManager;
import com.example.vegapp.RenzvosLocationControl;
import com.example.vegapp.RenzvosLocationControl.LocationCallbacks;
import com.example.vegapp.Zone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    RenzvosNavigation navigation;
    RenzvosLocationControl rlocation;
    RZoneManager zonemanager = new RZoneManager();

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
            Log.i("RZPR", "OnAddressGeocoded: " + address.toString());
        }
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        navigation = new RenzvosNavigation(getApplicationContext(),this);

        Page homepage = new Page("Home" ,R.drawable.ic_baseline_home_24,Home.newInstance() );
        Page profilepage = new Page("Profile" ,R.drawable.ic_baseline_account_circle_24, ProfileActivity.class );
        Page orders = new Page("Orders" ,R.drawable.ic_baseline_ballot_24, OrderListActivity.class );
        ArrayList<Page> pages = new ArrayList<>();
        pages.add(homepage);
        pages.add(profilepage);
        pages.add(orders);
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
                         zone.lat = Double.parseDouble(result.get("lat").toString());
                         zone.log = Double.parseDouble(result.get("log").toString());
                         zone.radius = Double.parseDouble(result.get("radius").toString());
                        zones.add(zone);
                     }

                     zonemanager.UpdateZones(zones);
                     if(zonemanager.isInsideZones())
                     {

                     }
                     else
                     {
                         ShowDeiveryNotAvailible();
                     }



                 }



             }
         });




    }

    public void ShowDeiveryNotAvailible(){
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.myDialog));
        builder.setMessage("We are sorry, Delivery is not available  in your area. Are you sure you want to continue?")
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







}