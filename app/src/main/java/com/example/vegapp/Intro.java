package com.example.vegapp;


import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.example.vegapp.AuthUI;
import com.example.vegapp.FirebaseAuthUIActivityResultContract;
import com.example.vegapp.IdpResponse;
import com.example.vegapp.data.model.FirebaseAuthUIAuthenticationResult;


import java.util.Arrays;
import java.util.List;

public class Intro extends AppCompatActivity {

    ProgressBar progressBar;
    Intent signinintent;
    private FirebaseFirestore mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
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
                .setLogo(R.drawable.icon)
                .setTheme(R.style.Theme_VegApp)
                .build();
        signinintent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

        new Handler().postDelayed(new Runnable() {
            public void run() {
                signInLauncher.launch(signinintent);
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
        if (result.getResultCode() == RESULT_OK) {
            // Successfully signed in
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            progressBar.setVisibility(View.INVISIBLE);
            tryagain.setVisibility(View.INVISIBLE);
            TextView resultt = findViewById(R.id.result);
            resultt.setVisibility(View.VISIBLE);
            mDatabase = FirebaseFirestore.getInstance();
            mDatabase.collection("users").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
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
                        appUserget.email = user.getEmail();
                        appUserget.phone = user.getPhoneNumber();
                    }


                    Log.i("RZ, ", "onSuccess: Setting");
                    appUserget.logall();
                    mDatabase.collection("users").document(user.getUid()).set(appUserget).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            startActivity(new Intent(Intro.this, MainActivity.class));
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
}