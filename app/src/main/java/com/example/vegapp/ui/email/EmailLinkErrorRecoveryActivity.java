package com.example.vegapp.ui.email;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.vegapp.IdpResponse;
import com.example.vegapp.R;
import com.example.vegapp.data.model.FlowParameters;
import com.example.vegapp.ui.AppCompatBase;
import com.example.vegapp.ui.HelperActivityBase;
import com.example.vegapp.viewmodel.RequestCodes;

import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;

/**
 * Handles the recovery flow for finishing the cross-device email link sign in flow. We either
 * need the user to input their email, or we need them to determine if they want to continue
 * the linking flow.
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public class EmailLinkErrorRecoveryActivity extends AppCompatBase
        implements EmailLinkPromptEmailFragment.EmailLinkPromptEmailListener,
        EmailLinkCrossDeviceLinkingFragment.FinishEmailLinkSignInListener {

    private static final String RECOVERY_TYPE_KEY = "com.firebase.ui.auth.ui.email.recoveryTypeKey";

    public static Intent createIntent(Context context, FlowParameters flowParams, int flow) {
        return HelperActivityBase.createBaseIntent(context, EmailLinkErrorRecoveryActivity.class, flowParams)
                .putExtra(RECOVERY_TYPE_KEY, flow);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fui_activity_register_email);

        if (savedInstanceState != null) {
            return;
        }

        boolean linkingFlow = getIntent().getIntExtra(RECOVERY_TYPE_KEY, -1) ==
                RequestCodes.EMAIL_LINK_CROSS_DEVICE_LINKING_FLOW;

        Fragment fragment;
        if (linkingFlow) {
            fragment = EmailLinkCrossDeviceLinkingFragment.newInstance();
        } else {
            fragment = EmailLinkPromptEmailFragment.newInstance();
        }
        switchFragment(fragment, R.id.fragment_register_email, EmailLinkPromptEmailFragment.TAG);
    }

    @Override
    public void onEmailPromptSuccess(IdpResponse response) {
        finish(Activity.RESULT_OK, response.toIntent());
    }

    @Override
    public void completeCrossDeviceEmailLinkFlow() {
        EmailLinkPromptEmailFragment fragment
                = EmailLinkPromptEmailFragment.newInstance();
        switchFragment(fragment, R.id.fragment_register_email,
                EmailLinkCrossDeviceLinkingFragment.TAG, /*withTransition=*/true,
                /*addToBackStack=*/true);
    }

    @Override
    public void showProgress(@StringRes int message) {
        throw new UnsupportedOperationException("Fragments must handle progress updates.");
    }

    @Override
    public void hideProgress() {
        throw new UnsupportedOperationException("Fragments must handle progress updates.");
    }
}
