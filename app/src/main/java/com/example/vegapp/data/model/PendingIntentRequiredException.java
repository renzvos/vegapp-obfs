package com.example.vegapp.data.model;

import android.app.PendingIntent;

import com.example.vegapp.ErrorCodes;
import com.example.vegapp.FirebaseUiException;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public class PendingIntentRequiredException extends FirebaseUiException {
    private final PendingIntent mPendingIntent;
    private final int mRequestCode;

    public PendingIntentRequiredException(@NonNull PendingIntent pendingIntent, int requestCode) {
        super(ErrorCodes.UNKNOWN_ERROR);
        mPendingIntent = pendingIntent;
        mRequestCode = requestCode;
    }

    @NonNull
    public PendingIntent getPendingIntent() {
        return mPendingIntent;
    }

    public int getRequestCode() {
        return mRequestCode;
    }
}
