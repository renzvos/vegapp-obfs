package com.aamys.fresh;

/*
The file is licenced under MIT and reserves to Arshad Nazir on 28th July 2022 at renzvos.com
 */

import static com.renzvos.profileeditor.ProfileObjectsKt.Profile_NUMBER;
import static com.renzvos.profileeditor.ProfileObjectsKt.Profile_PHONE;
import static com.renzvos.profileeditor.ProfileObjectsKt.Profile_TEXTOBJECT;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.aamys.fresh.CartClass;
import com.aamys.fresh.CartItem;
import com.aamys.fresh.EcommerceCart;
import com.example.profileeditor.ProfileEditor;
import com.aamys.fresh.ProductDt;import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.aamys.fresh.AuthUI;
import com.aamys.fresh.FirebaseAuthUIActivityResultContract;
import com.aamys.fresh.IdpResponse;
import com.aamys.fresh.data.model.FirebaseAuthUIAuthenticationResult;
import com.renzvos.profileeditor.ProfileLayoutDesign;
import com.renzvos.profileeditor.ProfileObjects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ProfileActivity extends AppCompatActivity {

    FirebaseStorage storage = FirebaseStorage.getInstance();
    FirebaseFirestore database = FirebaseFirestore.getInstance();
    ProfileEditor profileEditor;
    FirebaseAppUser firebaseAppUser;
    Intent signinintent;
    String ZONEPREFERENCES = "Zone";
    public static final String USERPREFERNECES = "userdata";
    SharedPreferences sharedPreferences;

    ProfileEditor.DpEditedCallback dpbuttoneditcallback = new ProfileEditor.DpEditedCallback() {
        @Override
        public void OnEdit(@NonNull Uri uri) {

            StorageReference dpref = storage.getReference();
            String lastpath = uri.getLastPathSegment();
            String gsref = "profilepicture/"+ UUID.randomUUID().toString() ;
            StorageReference riversRef = dpref.child(gsref);
            UploadTask uploadTask = riversRef.putFile(uri);
            Toast.makeText(getApplicationContext(),"Your Profile Picture is now uploading",Toast.LENGTH_LONG).show();
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(getApplicationContext(),"Upload Failed",Toast.LENGTH_LONG).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String uid = FirebaseAuth.getInstance().getUid();
                    firebaseAppUser.userdpurl = taskSnapshot.getStorage().toString();
                    Log.i("RZP", "ProfilePicUpdateImageSelected: Return String" + firebaseAppUser.userdpurl);
                    database.collection("users").document(uid).set(firebaseAppUser).addOnSuccessListener(editingcomplete);

                }
            });

        }
    };

    ProfileObjects.OnObjectCallbacks objectCallback = new ProfileObjects.OnObjectCallbacks() {
        @Override
        public void OnObjectEditingFinished(@NonNull ProfileObjects profileObjects) {

        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPreferences = getSharedPreferences(ZONEPREFERENCES, Context.MODE_PRIVATE);
        super.onCreate(savedInstanceState);
         profileEditor = new ProfileEditor(this, new ProfileEditor.ProfileCallback() {});
        String uid = FirebaseAuth.getInstance().getUid();
        ProfileLayoutDesign layoutDesign = new ProfileLayoutDesign();
        layoutDesign.SimpleLayoutForctivity("#9C11A9", "Aamy's Fresh", new ProfileLayoutDesign.OnBackPressed() {
            @Override
            public void OnClick() {
finish();
            }
        });



        profileEditor.ProduceLayoutForActivity(layoutDesign,getLayoutInflater());
        profileEditor.EditAll(new ProfileEditor.EditAllCallback() {
            @Override
            public boolean OnSaved(@NonNull ArrayList<ProfileObjects> objects) {
                String uid = FirebaseAuth.getInstance().getUid();
                FirebaseAppUser edited = firebaseAppUser;
                boolean emailwrong = true;
                for(ProfileObjects editobj : objects)
                { switch (editobj.getLabel())
                    {
                        case "Name":
                            edited.name = editobj.getEditorview().getText().toString();
                            break;
                        case "Email":
                            if (isValidEmail(editobj.getEditorview().getText().toString()))
                            {emailwrong = false;
                            edited.email = editobj.getEditorview().getText().toString();}
                            break;
                        case "City":
                            edited.Location.city = editobj.getEditorview().getText().toString();
                            break;
                        case "District":
                            edited.Location.district = editobj.getEditorview().getText().toString();
                            break;
                        case "Street":
                            edited.Location.street = editobj.getEditorview().getText().toString();
                            break;
                        case "Phone":
                            edited.phone = editobj.getEditorview().getText().toString();
                            break;
                        default:
                    }
                }
            if(emailwrong == true)
            {Toast.makeText(getApplicationContext(),"Invalid Email",Toast.LENGTH_SHORT).show();
                return false;}
            else
            {database.collection("users").document(uid).set(edited).addOnSuccessListener(editingcomplete);
                return true;}


            }
        });

        layoutDesign.getEditAllButton().setBackgroundTintList(new ColorStateList(
                new int[][]{new int[]{}},
                new int[]{Color.parseColor("#1EBF33")}
        ));
        layoutDesign.getEditAllButton().setBackgroundTintMode(PorterDuff.Mode.SRC_OVER);

        profileEditor.AttachDpeditor("#9C11A9", dpbuttoneditcallback);

        database.collection("users").document(uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                firebaseAppUser = documentSnapshot.toObject(FirebaseAppUser.class);
                updateuserdata(firebaseAppUser);
            }
        });




        EcommerceCart ecommerceCart = new EcommerceCart((AppCompatActivity) this);
        final RoundTag roundTag = ecommerceCart.DisplaySideCartIcon((ConstraintLayout) profileEditor.getRootView(),"₹0", new EcommerceCart.SideCartIcon() {
            @Override
            public void OnSideCartClicked() {
                startActivity(new Intent(ProfileActivity.this,CartActivity.class));
            }
        });


        database.collection("users").document(uid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
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
                                        roundTag.SetText("NA");
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


    public void updateuserdata(FirebaseAppUser user) {
        profileEditor.StopLoadingBar();
        profileEditor.EmptyFields();
        profileEditor.AttachDpeditor("#9C11A9",dpbuttoneditcallback );

        if(user.name == null)
            profileEditor.setName("Anonymous");
        else
            profileEditor.setName(user.name);



        ProfileObjects name = new ProfileObjects(Profile_TEXTOBJECT, "Name",objectCallback);
        name.setStringvalue(user.name);
        name.setHeading("Basic Info");


        ProfileObjects emailObject = new ProfileObjects(Profile_TEXTOBJECT, "Email",objectCallback);
        emailObject.setStringvalue(user.email);
        emailObject.setHeading("Basic Info");


        ProfileObjects city = new ProfileObjects(Profile_TEXTOBJECT, "City",objectCallback);
        city.setStringvalue(user.Location.city);
        city.setHeading("Location");


        ProfileObjects district = new ProfileObjects(Profile_TEXTOBJECT, "District",objectCallback);
        district.setStringvalue(user.Location.district);
        district.setHeading("Location");

        ProfileObjects street = new ProfileObjects(Profile_TEXTOBJECT, "Street",objectCallback);
        street.setStringvalue(user.Location.street);
        street.setHeading("Location");

        ProfileObjects pincode = new ProfileObjects(Profile_NUMBER, "Pincode",objectCallback);
        pincode.setStringvalue(user.Location.pincode);
        pincode.setHeading("Location");

        ProfileObjects number =  new ProfileObjects(Profile_PHONE , "Phone",objectCallback);
        if(user.phone == null || user.phone.equals(""))
        {
            number.setStringvalue("+91");
        }
        else
        {
            number.setStringvalue(user.phone);
        }

        number.setHeading("Basic Info");


        profileEditor.NewObject(name);
        profileEditor.NewObject(emailObject);
        profileEditor.NewObject(number);
        profileEditor.NewObject(district);
        profileEditor.NewObject(city);
        profileEditor.NewObject(street);
        profileEditor.NewObject(pincode);


        storage.getReferenceFromUrl(user.userdpurl).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                profileEditor.setDP(uri.toString());
            }
        });

        profileEditor.getObjects().get(0).getEditorview().setInputType(InputType.TYPE_CLASS_TEXT);
        profileEditor.getObjects().get(0).getEditorview().setFilters(new InputFilter[]{
                new InputFilter() {
                    public CharSequence filter(CharSequence src, int start,
                                               int end, Spanned dst, int dstart, int dend) {
                        if (src.equals("")) {
                            return src;
                        }
                        if (src.toString().matches("[a-zA-Z ]+")) {
                            return src;
                        }
                        return "";
                    }
                }
        });

        profileEditor.getObjects().get(2).getEditorview().setText("+91");



        for(ProfileObjects obs : profileEditor.getObjects())
        {
            obs.getLabelview().getLayoutParams().width = 250;
        }





    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            // compare the resultCode with the
            // SELECT_PICTURE constant
            if (requestCode == 3256) {
                // Get the url of the image from data
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                     profileEditor.getDpeditedcallback().OnEdit(selectedImageUri);

                }
            }
        }
    }

    private OnSuccessListener  editingcomplete = new OnSuccessListener() {
        @Override
        public void onSuccess(Object o) {
            database.collection("users").document(FirebaseAuth.getInstance().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    firebaseAppUser = documentSnapshot.toObject(FirebaseAppUser.class);
                    updateuserdata(firebaseAppUser);
                }
            });
        }
    };


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
            sharedPreferences = getSharedPreferences(USERPREFERNECES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("provider",FirebaseAuth.getInstance().getCurrentUser().getIdToken(false).getResult().getSignInProvider());
            editor.apply();
            database = FirebaseFirestore.getInstance();
            database.collection("users").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    FirebaseAppUser appUserget = documentSnapshot.toObject(FirebaseAppUser.class);
                    Log.i("RZ", "onSuccess: Getting ");

                    if(appUserget == null)
                    {
                        Log.i("RZ", "onSuccess: New User ");
                        appUserget = new FirebaseAppUser();
                        appUserget.Location = new Location();
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
                    appUserget.Cart = firebaseAppUser.Cart;


                    Log.i("RZ, ", "onSuccess: Setting");
                    appUserget.logall();
                    database.collection("users").document(user.getUid()).set(appUserget).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            startActivity(new Intent(ProfileActivity.this, ProfileActivity.class));
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

    public final static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }




}