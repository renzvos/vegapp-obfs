package com.aamys.fresh;

/*
The file is licenced under MIT and reserves to Arshad Nazir on 28th July 2022 at renzvos.com
 */


import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aamys.fresh.FirebaseAppUser;import com.aamys.fresh.FirebaseCart;import com.aamys.fresh.Location;import com.aamys.fresh.MainActivity;
import com.aamys.fresh.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.aamys.fresh.AuthUI;
import com.aamys.fresh.FirebaseAuthUIActivityResultContract;
import com.aamys.fresh.IdpResponse;
import com.aamys.fresh.FirebaseAuthUIActivityResultContract;
import com.aamys.fresh.data.model.FirebaseAuthUIAuthenticationResult;


import java.util.Arrays;
import java.util.List;

public class Intro extends AppCompatActivity {

    ProgressBar progressBar;
    Intent signinintent;
    private FirebaseFirestore mDatabase;
    public static final String USERPREFERNECES = "userdata";
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        RenzvosWork work = new RenzvosWork(this);
        work.WorkPermit();
        progressBar = findViewById(R.id.progressBar2);
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.PhoneBuilder().build(),
                //new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.AnonymousBuilder().build()
        );

// Create and launch sign-in intent

        signinintent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setLogo(R.drawable.ic_logo)
                .setTheme(R.style.Theme_VegApp)
                .build();
        signinintent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);



            new Handler().postDelayed(new Runnable() {
                public void run() {
                    if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                        signInLauncher.launch(signinintent);
                    }
                    else{
                        Log.i("RZ_App", "run: " + FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                        startActivity(new Intent(Intro.this,MainActivity.class));}
                }
            }, 2000);



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
        Button tryagain = findViewById(R.id.tryagain);
        IdpResponse response = result.getIdpResponse();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (result.getResultCode() == RESULT_OK && user != null) {
            // Successfully signed in
            progressBar.setVisibility(View.INVISIBLE);
            tryagain.setVisibility(View.INVISIBLE);
            TextView resultt = findViewById(R.id.result);
            resultt.setVisibility(View.VISIBLE);
            FirebaseAppUser.setSignedIn(this,user,response.getProviderType());
            mDatabase = FirebaseFirestore.getInstance();
            mDatabase.collection("users").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    FirebaseAppUser appUserget = documentSnapshot.toObject(FirebaseAppUser.class);
                    Log.i("App-Intro", "onSuccess: Getting ");

                    if(appUserget == null)
                    {
                        Log.i("App-Intro", "onSuccess: New User ");
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


                    Log.i("App-Intro, ", "onSuccess: Setting");
                    appUserget.logall();
                    mDatabase.collection("users").document(user.getUid()).set(appUserget).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            startActivity(new Intent(Intro.this, MainActivity.class));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("App-Intro", "onFailure: ", e);
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("App-Intro", "onFailure: ", e);
                }
            });



        } else {


            progressBar.setVisibility(View.INVISIBLE);
            tryagain.setVisibility(View.VISIBLE);
            tryagain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    signInLauncher.launch(signinintent);
                }
            });


        }
    }


    private long pressedTime;

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