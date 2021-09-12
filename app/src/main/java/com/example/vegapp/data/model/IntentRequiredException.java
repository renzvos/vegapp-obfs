package com.example.vegapp.data.model;

import android.content.Intent;

import com.example.vegapp.ErrorCodes;
import com.example.vegapp.FirebaseUiException;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public class IntentRequiredException extends FirebaseUiException {
    private final Intent mIntent;
    private final int mRequestCode;

    public IntentRequiredException(@NonNull Intent intent, int requestCode) {
        super(ErrorCodes.UNKNOWN_ERROR);
        mIntent = intent;
        mRequestCode = requestCode;
    }

    @NonNull
    public Intent getIntent() {
        return mIntent;
    }

    public int getRequestCode() {
        return mRequestCode;
    }
}
