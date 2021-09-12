package com.example.vegapp.viewmodel.phone;

import android.app.Application;

import com.example.vegapp.IdpResponse;
import com.example.vegapp.data.model.Resource;
import com.example.vegapp.util.data.AuthOperationManager;
import com.example.vegapp.viewmodel.SignInViewModelBase;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public class PhoneProviderResponseHandler extends SignInViewModelBase {
    public PhoneProviderResponseHandler(Application application) {
        super(application);
    }

    public void startSignIn(@NonNull PhoneAuthCredential credential,
                            @NonNull final IdpResponse response) {
        if (!response.isSuccessful()) {
            setResult(Resource.forFailure(response.getError()));
            return;
        }
        if (!response.getProviderType().equals(PhoneAuthProvider.PROVIDER_ID)) {
            throw new IllegalStateException(
                    "This handler cannot be used without a phone response.");
        }

        setResult(Resource.forLoading());

        AuthOperationManager.getInstance()
                .signInAndLinkWithCredential(getAuth(), getArguments(), credential)
                .addOnSuccessListener(result -> handleSuccess(response, result))
                .addOnFailureListener(e -> {
                    if (e instanceof FirebaseAuthUserCollisionException) {
                        // With phone auth, this only happens if we are trying to upgrade
                        // an anonymous account using a phone number that is already registered
                        // on another account
                        handleMergeFailure(((FirebaseAuthUserCollisionException) e).getUpdatedCredential());
                    } else {
                        setResult(Resource.forFailure(e));
                    }
                });
    }
}
