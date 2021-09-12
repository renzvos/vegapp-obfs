package com.example.vegapp.viewmodel.smartlock;

import android.app.Activity;
import android.app.Application;
import android.util.Log;

import com.example.vegapp.ErrorCodes;
import com.example.vegapp.FirebaseUiException;
import com.example.vegapp.IdpResponse;
import com.example.vegapp.data.model.PendingIntentRequiredException;
import com.example.vegapp.data.model.Resource;
import com.example.vegapp.util.CredentialUtils;
import com.example.vegapp.util.GoogleApiUtils;
import com.example.vegapp.util.data.ProviderUtils;
import com.example.vegapp.viewmodel.AuthViewModelBase;
import com.example.vegapp.viewmodel.RequestCodes;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;

/**
 * ViewModel for initiating saves to the Credentials API (SmartLock).
 */
public class SmartLockHandler extends AuthViewModelBase<IdpResponse> {
    private static final String TAG = "SmartLockViewModel";

    private IdpResponse mResponse;

    public SmartLockHandler(Application application) {
        super(application);
    }

    public void setResponse(@NonNull IdpResponse response) {
        mResponse = response;
    }

    /**
     * Forward the result of a resolution from the Activity to the ViewModel.
     */
    public void onActivityResult(int requestCode, int resultCode) {
        if (requestCode == RequestCodes.CRED_SAVE) {
            if (resultCode == Activity.RESULT_OK) {
                setResult(Resource.forSuccess(mResponse));
            } else {
                Log.e(TAG, "SAVE: Canceled by user.");
                FirebaseUiException exception = new FirebaseUiException(
                        ErrorCodes.UNKNOWN_ERROR, "Save canceled by user.");
                setResult(Resource.forFailure(exception));
            }
        }
    }

    /** @see #saveCredentials(Credential) */
    @RestrictTo(RestrictTo.Scope.TESTS)
    public void saveCredentials(FirebaseUser firebaseUser,
                                @Nullable String password,
                                @Nullable String accountType) {
        saveCredentials(CredentialUtils.buildCredential(firebaseUser, password, accountType));
    }

    /** Initialize saving a credential. */
    public void saveCredentials(@Nullable Credential credential) {
        if (!getArguments().enableCredentials) {
            setResult(Resource.forSuccess(mResponse));
            return;
        }
        setResult(Resource.forLoading());

        if (credential == null) {
            setResult(Resource.forFailure(new FirebaseUiException(
                    ErrorCodes.UNKNOWN_ERROR, "Failed to build credential.")));
            return;
        }

        deleteUnusedCredentials();
        getCredentialsClient().save(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        setResult(Resource.forSuccess(mResponse));
                    } else if (task.getException() instanceof ResolvableApiException) {
                        ResolvableApiException rae = (ResolvableApiException) task.getException();
                        setResult(Resource.forFailure(
                                new PendingIntentRequiredException(
                                        rae.getResolution(), RequestCodes.CRED_SAVE)));
                    } else {
                        Log.w(TAG, "Non-resolvable exception: " + task.getException());
                        setResult(Resource.forFailure(new FirebaseUiException(
                                ErrorCodes.UNKNOWN_ERROR,
                                "Error when saving credential.",
                                task.getException())));
                    }
                });
    }

    private void deleteUnusedCredentials() {
        if (mResponse.getProviderType().equals(GoogleAuthProvider.PROVIDER_ID)) {
            // Since Google accounts upgrade email ones, we don't want to end up
            // with duplicate credentials so delete the email ones.
            String type = ProviderUtils.providerIdToAccountType(
                    GoogleAuthProvider.PROVIDER_ID);
            GoogleApiUtils.getCredentialsClient(getApplication()).delete(
                    CredentialUtils.buildCredentialOrThrow(getCurrentUser(), "pass", type));
        }
    }
}
