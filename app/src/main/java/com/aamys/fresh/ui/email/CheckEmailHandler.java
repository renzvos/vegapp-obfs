package com.aamys.fresh.ui.email;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import com.aamys.fresh.data.model.PendingIntentRequiredException;
import com.aamys.fresh.data.model.Resource;
import com.aamys.fresh.data.model.User;
import com.aamys.fresh.util.data.ProviderUtils;
import com.aamys.fresh.viewmodel.AuthViewModelBase;
import com.aamys.fresh.viewmodel.RequestCodes;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.Credentials;
import com.google.android.gms.auth.api.credentials.HintRequest;

import androidx.annotation.Nullable;

public class CheckEmailHandler extends AuthViewModelBase<User> {
    public CheckEmailHandler(Application application) {
        super(application);
    }

    public void fetchCredential() {
        setResult(Resource.forFailure(new PendingIntentRequiredException(
                Credentials.getClient(getApplication()).getHintPickerIntent(
                        new HintRequest.Builder().setEmailAddressIdentifierSupported(true).build()),
                RequestCodes.CRED_HINT
        )));
    }

    public void fetchProvider(final String email) {
        setResult(Resource.forLoading());
        ProviderUtils.fetchTopProvider(getAuth(), getArguments(), email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        setResult(Resource.forSuccess(
                                new User.Builder(task.getResult(), email).build()));
                    } else {
                        setResult(Resource.forFailure(task.getException()));
                    }
                });
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode != RequestCodes.CRED_HINT || resultCode != Activity.RESULT_OK) { return; }

        setResult(Resource.forLoading());
        final Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
        final String email = credential.getId();
        ProviderUtils.fetchTopProvider(getAuth(), getArguments(), email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        setResult(Resource.forSuccess(new User.Builder(task.getResult(), email)
                                .setName(credential.getName())
                                .setPhotoUri(credential.getProfilePictureUri())
                                .build()));
                    } else {
                        setResult(Resource.forFailure(task.getException()));
                    }
                });
    }
}
