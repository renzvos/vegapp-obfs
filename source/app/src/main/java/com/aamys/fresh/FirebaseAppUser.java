package com.aamys.fresh;

/*
The file is licenced under MIT and reserves to Arshad Nazir on 28th July 2022 at renzvos.com
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.util.Pair;

import com.aamys.fresh.Location;import com.google.firebase.auth.FirebaseUser;

import java.io.Serializable;

public class FirebaseAppUser implements Serializable {
    public String userdpurl = null;
    public String name = null;
    public String phone = null;
    public String email;
    public com.aamys.fresh.Location Location;
    public String uid = null;
    public FirebaseCart Cart;
    static SharedPreferences sharedPreferences;
    static String USERPREFERNECES = "userdata";

    public  FirebaseAppUser(){}

    public  FirebaseAppUser(String uid,String name,String userdpurl,String email ,String phone , Location location,FirebaseCart firebaseCart)
    {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.Location = location;
        this.Cart = firebaseCart;
    }

    public void logall()
    {
        Log.i("App-FirebaseUser", "logall: id" + uid);
        Log.i("App-FirebaseUser", "logall: name " + name);
        Log.i("App-FirebaseUser", "logall: email" + email);
        Log.i("App-FirebaseUser", "logall: phone" + phone);
    }

    public static boolean getSignedIn(Context context)
    {
        sharedPreferences = context.getSharedPreferences(USERPREFERNECES, Context.MODE_PRIVATE);
        boolean state  = sharedPreferences.getBoolean("signin",false);
        return state;
    }

    public static String getProvider(Context context)
    {
        sharedPreferences = context.getSharedPreferences(USERPREFERNECES, Context.MODE_PRIVATE);
        String provider = sharedPreferences.getString("provider",null);
        return provider;

    }

    public static void setSignedIn(Context context, FirebaseUser user , String idpresponse)
    {
        sharedPreferences = context.getSharedPreferences(USERPREFERNECES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String provider = null;
        provider = user.getIdToken(false).getResult().getSignInProvider();
        if(provider == null)
        {
            if(!user.getProviderId().equals("firebase"))
            {provider = user.getProviderId(); }
            else
            {
                if(!user.getProviderData().get(0).getProviderId().equals("firebase"))
                provider = user.getProviderData().get(0).getProviderId();
                else
                {
                    if(idpresponse != null && !idpresponse.equals("firebase"))
                    {
                        provider = idpresponse;
                    }
                    else
                    {
                        throw new RuntimeException("Cannot find User Provider");
                    }
                }
            }

        }
        editor.putString("provider",provider);
        editor.putBoolean("signin", true);
        editor.apply();


    }


}
