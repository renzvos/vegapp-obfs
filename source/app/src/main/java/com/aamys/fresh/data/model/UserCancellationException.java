package com.aamys.fresh.data.model;

import com.aamys.fresh.ErrorCodes;
import com.aamys.fresh.FirebaseUiException;

import androidx.annotation.RestrictTo;

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public class UserCancellationException extends FirebaseUiException {
    public UserCancellationException() {
        super(ErrorCodes.UNKNOWN_ERROR);
    }
}
