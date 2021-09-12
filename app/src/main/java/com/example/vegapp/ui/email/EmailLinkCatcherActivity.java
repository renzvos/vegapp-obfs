package com.example.vegapp.ui.email;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.vegapp.ErrorCodes;
import com.example.vegapp.FirebaseAuthAnonymousUpgradeException;
import com.example.vegapp.FirebaseUiException;
import com.example.vegapp.IdpResponse;
import com.example.vegapp.R;
import com.example.vegapp.data.model.FlowParameters;
import com.example.vegapp.data.model.UserCancellationException;
import com.example.vegapp.ui.HelperActivityBase;
import com.example.vegapp.ui.InvisibleActivityBase;
import com.example.vegapp.util.ExtraConstants;
import com.example.vegapp.viewmodel.RequestCodes;
import com.example.vegapp.viewmodel.ResourceObserver;
import com.example.vegapp.viewmodel.email.EmailLinkSignInHandler;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.lifecycle.ViewModelProvider;

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public class EmailLinkCatcherActivity extends InvisibleActivityBase {

    private EmailLinkSignInHandler mHandler;

    public static Intent createIntent(Context context, FlowParameters flowParams) {
        return HelperActivityBase.createBaseIntent(context, EmailLinkCatcherActivity.class, flowParams);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initHandler();

        if (getFlowParams().emailLink != null) {
            mHandler.startSignIn();
        }
    }

    private void initHandler() {
        mHandler = new ViewModelProvider(this).get(EmailLinkSignInHandler.class);
        mHandler.init(getFlowParams());
        mHandler.getOperation().observe(this, new ResourceObserver<IdpResponse>(this) {
            @Override
            protected void onSuccess(@NonNull IdpResponse response) {
                finish(Activity.RESULT_OK, response.toIntent());
            }

            @Override
            protected void onFailure(@NonNull final Exception e) {
                if (e instanceof UserCancellationException) {
                    finish(Activity.RESULT_CANCELED, null);
                } else if (e instanceof FirebaseAuthAnonymousUpgradeException) {
                    IdpResponse res = ((FirebaseAuthAnonymousUpgradeException) e).getResponse();
                    finish(Activity.RESULT_CANCELED, new Intent().putExtra(ExtraConstants.IDP_RESPONSE, res));
                } else if (e instanceof FirebaseUiException) {
                    int errorCode = ((FirebaseUiException) e).getErrorCode();
                    if (errorCode == ErrorCodes.EMAIL_LINK_WRONG_DEVICE_ERROR
                            || errorCode == ErrorCodes.INVALID_EMAIL_LINK_ERROR
                            || errorCode == ErrorCodes.EMAIL_LINK_DIFFERENT_ANONYMOUS_USER_ERROR) {
                        buildAlertDialog(errorCode).show();
                    } else if (errorCode == ErrorCodes.EMAIL_LINK_PROMPT_FOR_EMAIL_ERROR
                            || errorCode == ErrorCodes.EMAIL_MISMATCH_ERROR) {
                        startErrorRecoveryFlow(RequestCodes.EMAIL_LINK_PROMPT_FOR_EMAIL_FLOW);
                    } else if (errorCode == ErrorCodes.EMAIL_LINK_CROSS_DEVICE_LINKING_ERROR) {
                        startErrorRecoveryFlow(RequestCodes.EMAIL_LINK_CROSS_DEVICE_LINKING_FLOW);
                    }
                } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    startErrorRecoveryFlow(RequestCodes.EMAIL_LINK_PROMPT_FOR_EMAIL_FLOW);
                } else {
                    finish(Activity.RESULT_CANCELED, IdpResponse.getErrorIntent(e));
                }
            }
        });
    }

    /**
     * @param flow must be one of RequestCodes.EMAIL_LINK_PROMPT_FOR_EMAIL_FLOW or
     *             RequestCodes.EMAIL_LINK_CROSS_DEVICE_LINKING_FLOW
     */
    private void startErrorRecoveryFlow(int flow) {
        if (flow != RequestCodes.EMAIL_LINK_CROSS_DEVICE_LINKING_FLOW
                && flow != RequestCodes.EMAIL_LINK_PROMPT_FOR_EMAIL_FLOW) {
            throw new IllegalStateException("Invalid flow param. It must be either " +
                    "RequestCodes.EMAIL_LINK_CROSS_DEVICE_LINKING_FLOW or " +
                    "RequestCodes.EMAIL_LINK_PROMPT_FOR_EMAIL_FLOW");
        }
        Intent intent = EmailLinkErrorRecoveryActivity.createIntent(getApplicationContext(),
                getFlowParams(), flow);
        startActivityForResult(intent, flow);
    }

    private AlertDialog buildAlertDialog(final int errorCode) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        String titleText;
        String messageText;
        if (errorCode == ErrorCodes.EMAIL_LINK_DIFFERENT_ANONYMOUS_USER_ERROR) {
            titleText = getString(R.string.fui_email_link_different_anonymous_user_header);
            messageText = getString(R.string.fui_email_link_different_anonymous_user_message);
        } else if (errorCode == ErrorCodes.INVALID_EMAIL_LINK_ERROR) {
            titleText = getString(R.string.fui_email_link_invalid_link_header);
            messageText = getString(R.string.fui_email_link_invalid_link_message);
        } else {
            // Default value - ErrorCodes.EMAIL_LINK_WRONG_DEVICE_ERROR
            titleText = getString(R.string.fui_email_link_wrong_device_header);
            messageText = getString(R.string.fui_email_link_wrong_device_message);
        }

        return alertDialog.setTitle(titleText)
                .setMessage(messageText)
                .setPositiveButton(R.string.fui_email_link_dismiss_button,
                        (dialog, id) -> finish(errorCode, null))
                .create();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCodes.EMAIL_LINK_PROMPT_FOR_EMAIL_FLOW
                || requestCode == RequestCodes.EMAIL_LINK_CROSS_DEVICE_LINKING_FLOW) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            // CheckActionCode is called before starting this flow, so we only get here
            // if the sign in link is valid - it can only fail by being cancelled.
            if (resultCode == Activity.RESULT_OK) {
                finish(Activity.RESULT_OK, response.toIntent());
            } else {
                finish(Activity.RESULT_CANCELED, null);
            }
        }
    }
}
