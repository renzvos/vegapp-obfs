package com.aamys.fresh;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;import com.aamys.fresh.IdpResponse;

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public class FirebaseAuthAnonymousUpgradeException extends Exception {

    private IdpResponse mResponse;

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    public FirebaseAuthAnonymousUpgradeException(@ErrorCodes.Code int code,
                                                 @NonNull IdpResponse response) {
        super(ErrorCodes.toFriendlyMessage(code));
        mResponse = response;
    }

    public IdpResponse getResponse() {
        return mResponse;
    }
}
