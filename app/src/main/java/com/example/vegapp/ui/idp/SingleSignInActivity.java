package com.example.vegapp.ui.idp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.example.vegapp.AuthUI;
import com.example.vegapp.ErrorCodes;
import com.example.vegapp.FirebaseAuthAnonymousUpgradeException;
import com.example.vegapp.FirebaseUiException;
import com.example.vegapp.IdpResponse;
import com.example.vegapp.data.model.FlowParameters;
import com.example.vegapp.data.model.User;
import com.example.vegapp.data.remote.FacebookSignInHandler;
import com.example.vegapp.data.remote.GenericIdpSignInHandler;
import com.example.vegapp.data.remote.GoogleSignInHandler;
import com.example.vegapp.ui.HelperActivityBase;
import com.example.vegapp.ui.InvisibleActivityBase;
import com.example.vegapp.util.ExtraConstants;
import com.example.vegapp.util.data.ProviderUtils;
import com.example.vegapp.viewmodel.ProviderSignInBase;
import com.example.vegapp.viewmodel.ResourceObserver;
import com.example.vegapp.viewmodel.idp.SocialProviderResponseHandler;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.GoogleAuthProvider;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.lifecycle.ViewModelProvider;

import static com.example.vegapp.util.ExtraConstants.GENERIC_OAUTH_PROVIDER_ID;

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public class SingleSignInActivity extends InvisibleActivityBase {
    private SocialProviderResponseHandler mHandler;
    private ProviderSignInBase<?> mProvider;

    public static Intent createIntent(Context context, FlowParameters flowParams, User user) {
        return HelperActivityBase.createBaseIntent(context, SingleSignInActivity.class, flowParams)
                .putExtra(ExtraConstants.USER, user);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        User user = User.getUser(getIntent());
        final String provider = user.getProviderId();

        AuthUI.IdpConfig providerConfig =
                ProviderUtils.getConfigFromIdps(getFlowParams().providers, provider);
        if (providerConfig == null) {
            finish(Activity.RESULT_CANCELED, IdpResponse.getErrorIntent(new FirebaseUiException(
                    ErrorCodes.DEVELOPER_ERROR,
                    "Provider not enabled: " + provider)));
            return;
        }

        ViewModelProvider supplier = new ViewModelProvider(this);

        mHandler = supplier.get(SocialProviderResponseHandler.class);
        mHandler.init(getFlowParams());

        boolean useEmulator = getAuthUI().isUseEmulator();

        switch (provider) {
            case GoogleAuthProvider.PROVIDER_ID:
                if (useEmulator) {
                    mProvider = supplier.get(GenericIdpSignInHandler.class)
                            .initWith(GenericIdpSignInHandler.getGenericGoogleConfig());
                } else {
                    mProvider = supplier.get(GoogleSignInHandler.class).initWith(
                            new GoogleSignInHandler.Params(providerConfig, user.getEmail()));
                }
                break;
            case FacebookAuthProvider.PROVIDER_ID:
                if (useEmulator) {
                    mProvider = supplier.get(GenericIdpSignInHandler.class)
                            .initWith(GenericIdpSignInHandler.getGenericFacebookConfig());
                } else {
                    mProvider = supplier.get(FacebookSignInHandler.class).initWith(providerConfig);
                }
                break;
            default:
                if (!TextUtils.isEmpty(
                        providerConfig.getParams().getString(ExtraConstants.GENERIC_OAUTH_PROVIDER_ID))) {
                    mProvider = supplier.get(GenericIdpSignInHandler.class).initWith(providerConfig);
                    break;
                }
                throw new IllegalStateException("Invalid provider id: " + provider);
        }

        mProvider.getOperation().observe(this, new ResourceObserver<IdpResponse>(this) {
            @Override
            protected void onSuccess(@NonNull IdpResponse response) {
                boolean useSocialHandler = AuthUI.SOCIAL_PROVIDERS.contains(provider)
                        && !getAuthUI().isUseEmulator();

                if (useSocialHandler || !response.isSuccessful()) {
                    mHandler.startSignIn(response);
                    return;
                }
                finish(response.isSuccessful() ? Activity.RESULT_OK : Activity.RESULT_CANCELED,
                        response.toIntent());
            }

            @Override
            protected void onFailure(@NonNull Exception e) {
                if (e instanceof FirebaseAuthAnonymousUpgradeException) {
                    finish(Activity.RESULT_CANCELED, new Intent().putExtra(ExtraConstants.IDP_RESPONSE,
                            IdpResponse.from(e)));
                    return;
                }
                mHandler.startSignIn(IdpResponse.from(e));
            }
        });

        mHandler.getOperation().observe(this, new ResourceObserver<IdpResponse>(this) {
            @Override
            protected void onSuccess(@NonNull IdpResponse response) {
                startSaveCredentials(mHandler.getCurrentUser(), response, null);
            }

            @Override
            protected void onFailure(@NonNull Exception e) {
                if (e instanceof FirebaseAuthAnonymousUpgradeException) {
                    IdpResponse res = ((FirebaseAuthAnonymousUpgradeException) e).getResponse();
                    finish(Activity.RESULT_CANCELED, new Intent().putExtra(ExtraConstants.IDP_RESPONSE, res));
                } else {
                    finish(Activity.RESULT_CANCELED, IdpResponse.getErrorIntent(e));
                }
            }
        });

        if (mHandler.getOperation().getValue() == null) {
            mProvider.startSignIn(getAuth(), this, provider);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mHandler.onActivityResult(requestCode, resultCode, data);
        mProvider.onActivityResult(requestCode, resultCode, data);
    }
}
