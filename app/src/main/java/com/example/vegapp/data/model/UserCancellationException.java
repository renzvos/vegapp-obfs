package com.example.vegapp.data.model;

import com.example.vegapp.ErrorCodes;
import com.example.vegapp.FirebaseUiException;

import androidx.annotation.RestrictTo;

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public class UserCancellationException extends FirebaseUiException {
    public UserCancellationException() {
        super(ErrorCodes.UNKNOWN_ERROR);
    }
}
