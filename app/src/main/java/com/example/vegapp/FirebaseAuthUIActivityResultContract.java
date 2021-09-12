package com.example.vegapp;

import android.content.Context;
import android.content.Intent;

import com.example.vegapp.data.model.FirebaseAuthUIAuthenticationResult;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * A {@link ActivityResultContract} describing that the caller can launch authentication flow with a
 * {@link Intent} and is guaranteed to receive a {@link FirebaseAuthUIAuthenticationResult} as
 * result. The given input intent <b>must</b> be created using a
 * { order to guarantee a successful
 * launch of the authentication flow.
 */
public class FirebaseAuthUIActivityResultContract extends
        ActivityResultContract<Intent, FirebaseAuthUIAuthenticationResult> {

    @NonNull
    @Override
    public Intent createIntent(@NonNull Context context, Intent input) {
        return input;
    }

    @Override
    @NonNull
    public FirebaseAuthUIAuthenticationResult parseResult(int resultCode, @Nullable Intent intent) {
        return new FirebaseAuthUIAuthenticationResult(resultCode, IdpResponse.fromResultIntent(intent));
    }

}
