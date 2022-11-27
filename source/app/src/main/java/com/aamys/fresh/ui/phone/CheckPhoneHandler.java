package com.aamys.fresh.ui.phone;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;

import com.aamys.fresh.data.model.PendingIntentRequiredException;
import com.aamys.fresh.data.model.PhoneNumber;
import com.aamys.fresh.data.model.Resource;
import com.aamys.fresh.util.data.PhoneNumberUtils;
import com.aamys.fresh.viewmodel.AuthViewModelBase;
import com.aamys.fresh.viewmodel.RequestCodes;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.Credentials;
import com.google.android.gms.auth.api.credentials.HintRequest;

import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public class CheckPhoneHandler extends AuthViewModelBase<PhoneNumber> {
    public CheckPhoneHandler(Application application) {
        super(application);
    }

    public void fetchCredential() {
        setResult(Resource.forFailure(new PendingIntentRequiredException(
                Credentials.getClient(getApplication()).getHintPickerIntent(
                        new HintRequest.Builder().setPhoneNumberIdentifierSupported(true).build()),
                RequestCodes.CRED_HINT
        )));
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode != RequestCodes.CRED_HINT || resultCode != Activity.RESULT_OK) { return; }

        Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
        String formattedPhone = PhoneNumberUtils.formatUsingCurrentCountry(
                credential.getId(), getApplication());
        if (formattedPhone != null) {
            setResult(Resource.forSuccess(PhoneNumberUtils.getPhoneNumber(formattedPhone)));
        }
    }
}
