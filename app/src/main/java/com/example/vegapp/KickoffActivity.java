package com.example.vegapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;


import com.example.vegapp.data.model.FlowParameters;
import com.example.vegapp.data.model.UserCancellationException;
import com.example.vegapp.data.remote.SignInKickstarter;
import com.example.vegapp.ui.HelperActivityBase;
import com.example.vegapp.util.ExtraConstants;
import com.example.vegapp.viewmodel.RequestCodes;
import com.example.vegapp.viewmodel.ResourceObserver;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.example.vegapp.ui.InvisibleActivityBase;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.lifecycle.ViewModelProvider;

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public class KickoffActivity extends InvisibleActivityBase {
    private SignInKickstarter mKickstarter;

    public static Intent createIntent(Context context, FlowParameters flowParams) {
        return HelperActivityBase.createBaseIntent(context, KickoffActivity.class, flowParams);
    }

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mKickstarter = new ViewModelProvider(this).get(SignInKickstarter.class);
        mKickstarter.init(getFlowParams());
        mKickstarter.getOperation().observe(this, new ResourceObserver<IdpResponse>(this) {
            @Override
            protected void onSuccess(@NonNull IdpResponse response) {
                finish(Activity.RESULT_OK, response.toIntent());
            }

            @Override
            protected void onFailure(@NonNull Exception e) {
                if (e instanceof UserCancellationException) {
                    finish(Activity.RESULT_CANCELED, null);
                } else if (e instanceof FirebaseAuthAnonymousUpgradeException) {
                    IdpResponse res = ((FirebaseAuthAnonymousUpgradeException) e).getResponse();
                    finish(Activity.RESULT_CANCELED, new Intent().putExtra(ExtraConstants.IDP_RESPONSE,
                            res));
                } else {
                    finish(Activity.RESULT_CANCELED, IdpResponse.getErrorIntent(e));
                }
            }
        });

        Task<Void> checkPlayServicesTask = getFlowParams().isPlayServicesRequired()
                ? GoogleApiAvailability.getInstance().makeGooglePlayServicesAvailable(this)
                : Tasks.forResult((Void) null);

        checkPlayServicesTask
                .addOnSuccessListener(this, aVoid -> {
                    if (savedInstanceState != null) {
                        return;
                    }

                    mKickstarter.start();
                })
                .addOnFailureListener(this, e -> finish(Activity.RESULT_CANCELED, IdpResponse.getErrorIntent(new FirebaseUiException(
                        ErrorCodes.PLAY_SERVICES_UPDATE_CANCELLED, e))));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RequestCodes.EMAIL_FLOW
                && (resultCode == RequestCodes.EMAIL_LINK_WRONG_DEVICE_FLOW
                || resultCode == RequestCodes.EMAIL_LINK_INVALID_LINK_FLOW)) {
            invalidateEmailLink();
        }

        mKickstarter.onActivityResult(requestCode, resultCode, data);
    }

    public void invalidateEmailLink() {
        FlowParameters flowParameters = getFlowParams();
        flowParameters.emailLink = null;
        setIntent(getIntent().putExtra(ExtraConstants.FLOW_PARAMS,
                flowParameters));
    }
}
