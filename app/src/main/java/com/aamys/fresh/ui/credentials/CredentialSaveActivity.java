package com.aamys.fresh.ui.credentials;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.aamys.fresh.IdpResponse;
import com.aamys.fresh.data.model.FlowParameters;
import com.aamys.fresh.data.model.Resource;
import com.aamys.fresh.ui.HelperActivityBase;
import com.aamys.fresh.ui.InvisibleActivityBase;
import com.aamys.fresh.util.ExtraConstants;
import com.aamys.fresh.viewmodel.ResourceObserver;
import com.aamys.fresh.viewmodel.smartlock.SmartLockHandler;
import com.google.android.gms.auth.api.credentials.Credential;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

/**
 * Invisible Activity used for saving credentials to SmartLock.
 */
public class CredentialSaveActivity extends InvisibleActivityBase {
    private static final String TAG = "CredentialSaveActivity";

    private SmartLockHandler mHandler;

    @NonNull
    public static Intent createIntent(Context context,
                                      FlowParameters flowParams,
                                      Credential credential,
                                      IdpResponse response) {
        return HelperActivityBase.createBaseIntent(context, CredentialSaveActivity.class, flowParams)
                .putExtra(ExtraConstants.CREDENTIAL, credential)
                .putExtra(ExtraConstants.IDP_RESPONSE, response);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final IdpResponse response = getIntent().getParcelableExtra(ExtraConstants.IDP_RESPONSE);
        Credential credential = getIntent().getParcelableExtra(ExtraConstants.CREDENTIAL);

        mHandler = new ViewModelProvider(this).get(SmartLockHandler.class);
        mHandler.init(getFlowParams());
        mHandler.setResponse(response);

        mHandler.getOperation().observe(this, new ResourceObserver<IdpResponse>(this) {
            @Override
            protected void onSuccess(@NonNull IdpResponse response) {
                finish(Activity.RESULT_OK, response.toIntent());
            }

            @Override
            protected void onFailure(@NonNull Exception e) {
                // RESULT_OK since we don't want to halt sign-in just because of a credential save
                // error.
                finish(Activity.RESULT_OK, response.toIntent());
            }
        });

        // Avoid double-saving
        Resource<IdpResponse> currentOp = mHandler.getOperation().getValue();
        if (currentOp == null) {
            Log.d(TAG, "Launching save operation.");
            mHandler.saveCredentials(credential);
        } else {
            Log.d(TAG, "Save operation in progress, doing nothing.");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mHandler.onActivityResult(requestCode, resultCode);
    }
}
