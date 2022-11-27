package com.aamys.fresh.data.remote;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

import com.aamys.fresh.AuthUI;
import com.aamys.fresh.IdpResponse;
import com.aamys.fresh.data.model.FlowParameters;
import com.aamys.fresh.data.model.Resource;
import com.aamys.fresh.ui.HelperActivityBase;
import com.aamys.fresh.util.data.AuthOperationManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.OAuthCredential;
import com.google.firebase.auth.OAuthProvider;

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public class GenericIdpAnonymousUpgradeLinkingHandler extends GenericIdpSignInHandler {

    public GenericIdpAnonymousUpgradeLinkingHandler(Application application) {
        super(application);
    }

    @Override
    public void startSignIn(@NonNull FirebaseAuth auth,
                            @NonNull HelperActivityBase activity,
                            @NonNull String providerId) {
        setResult(Resource.forLoading());

        FlowParameters flowParameters = activity.getFlowParams();
        OAuthProvider provider = buildOAuthProvider(providerId, auth);
        if (flowParameters != null
                && AuthOperationManager.getInstance().canUpgradeAnonymous(auth, flowParameters)) {
            handleAnonymousUpgradeLinkingFlow(activity, provider, flowParameters);
            return;
        }

        handleNormalSignInFlow(auth, activity, provider);
    }

    private void handleAnonymousUpgradeLinkingFlow(final HelperActivityBase activity,
                                                   final OAuthProvider provider,
                                                   final FlowParameters flowParameters) {
        final boolean useEmulator = activity.getAuthUI().isUseEmulator();
        AuthOperationManager.getInstance().safeGenericIdpSignIn(activity, provider, flowParameters)
                .addOnSuccessListener(authResult -> {
                    // Pass the credential so we can sign-in on the after the merge
                    // conflict is resolved.
                    handleSuccess(
                            useEmulator,
                            provider.getProviderId(),
                            authResult.getUser(), (OAuthCredential) authResult.getCredential(),
                            /* setPendingCredential= */true);
                })
                .addOnFailureListener(e -> setResult(Resource.forFailure(e)));

    }
}
