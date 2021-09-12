package com.example.vegapp.viewmodel.idp;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.text.TextUtils;

import com.example.vegapp.ErrorCodes;
import com.example.vegapp.FirebaseUiException;
import com.example.vegapp.IdpResponse;
import com.example.vegapp.data.model.IntentRequiredException;
import com.example.vegapp.data.model.Resource;
import com.example.vegapp.data.model.User;
import com.example.vegapp.data.remote.ProfileMerger;
import com.example.vegapp.ui.email.WelcomeBackEmailLinkPrompt;
import com.example.vegapp.ui.email.WelcomeBackPasswordPrompt;
import com.example.vegapp.ui.idp.WelcomeBackIdpPrompt;
import com.example.vegapp.util.FirebaseAuthError;
import com.example.vegapp.util.data.AuthOperationManager;
import com.example.vegapp.util.data.ProviderUtils;
import com.example.vegapp.viewmodel.RequestCodes;
import com.example.vegapp.viewmodel.SignInViewModelBase;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.PhoneAuthProvider;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;

import static com.example.vegapp.AuthUI.EMAIL_LINK_PROVIDER;

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public class SocialProviderResponseHandler extends SignInViewModelBase {
    public SocialProviderResponseHandler(Application application) {
        super(application);
    }

    public void startSignIn(@NonNull final IdpResponse response) {
        if (!response.isSuccessful() && !response.isRecoverableErrorResponse()) {
            setResult(Resource.forFailure(response.getError()));
            return;
        }

        if (isEmailOrPhoneProvider(response.getProviderType())) {
            throw new IllegalStateException(
                    "This handler cannot be used with email or phone providers");
        }

        setResult(Resource.forLoading());

        // Recoverable error flows (linking) for Generic OAuth providers are handled here.
        // For Generic OAuth providers, the credential is set on the IdpResponse, as
        // a credential made from the id token/access token cannot be used to sign-in.
        if (response.hasCredentialForLinking()) {
            handleGenericIdpLinkingFlow(response);
            return;
        }

        final AuthCredential credential = ProviderUtils.getAuthCredential(response);
        AuthOperationManager.getInstance().signInAndLinkWithCredential(
                getAuth(),
                getArguments(),
                credential)
                .continueWithTask(new ProfileMerger(response))
                .addOnSuccessListener(result -> handleSuccess(response, result))
                .addOnFailureListener(e -> {
                    // For some reason disabled users can hit FirebaseAuthUserCollisionException
                    // so we have to handle this special case.
                    boolean isDisabledUser = (e instanceof FirebaseAuthInvalidUserException);
                    if (e instanceof FirebaseAuthException) {
                        FirebaseAuthException authEx = (FirebaseAuthException) e;
                        FirebaseAuthError fae = FirebaseAuthError.fromException(authEx);
                        if (fae == FirebaseAuthError.ERROR_USER_DISABLED) {
                           isDisabledUser = true;
                        }
                    }

                    if (isDisabledUser) {
                        setResult(Resource.forFailure(
                                new FirebaseUiException(ErrorCodes.ERROR_USER_DISABLED)
                        ));
                    } else if (e instanceof FirebaseAuthUserCollisionException) {
                        final String email = response.getEmail();
                        if (email == null) {
                            setResult(Resource.forFailure(e));
                            return;
                        }

                        // There can be a collision due to:
                        // CASE 1: Anon user signing in with a credential that belongs to an
                        // existing user.
                        // CASE 2: non - anon user signing in with a credential that does not
                        // belong to an existing user, but the email matches an existing user
                        // that has another social IDP. We need to link this new IDP to this
                        // existing user.
                        // CASE 3: CASE 2 with an anonymous user. We link the new IDP to the
                        // same account before handling invoking a merge failure.
                        ProviderUtils.fetchSortedProviders(getAuth(), getArguments(), email)
                                .addOnSuccessListener(providers -> {
                                    if (providers.contains(response.getProviderType())) {
                                        // Case 1
                                        handleMergeFailure(credential);
                                    } else if (providers.isEmpty()) {
                                        setResult(Resource.forFailure(
                                                new FirebaseUiException(
                                                        ErrorCodes.DEVELOPER_ERROR,
                                                        "No supported providers.")));
                                    } else {
                                        // Case 2 & 3 - we need to link
                                        startWelcomeBackFlowForLinking(
                                                providers.get(0), response);
                                    }
                                })
                                .addOnFailureListener(e1 -> setResult(Resource.forFailure(
                                        e1)));
                    }
                });
    }

    public void startWelcomeBackFlowForLinking(String provider, IdpResponse response) {
        if (provider == null) {
            throw new IllegalStateException(
                    "No provider even though we received a FirebaseAuthUserCollisionException");
        }

        if (provider.equals(EmailAuthProvider.PROVIDER_ID)) {
            // Start email welcome back flow
            setResult(Resource.forFailure(new IntentRequiredException(
                    WelcomeBackPasswordPrompt.createIntent(
                            getApplication(),
                            getArguments(),
                            response),
                    RequestCodes.ACCOUNT_LINK_FLOW
            )));
        } else if (provider.equals(EMAIL_LINK_PROVIDER)) {
            // Start email link welcome back flow
            setResult(Resource.forFailure(new IntentRequiredException(
                    WelcomeBackEmailLinkPrompt.createIntent(
                            getApplication(),
                            getArguments(),
                            response),
                    RequestCodes.WELCOME_BACK_EMAIL_LINK_FLOW
            )));
        } else {
            // Start Idp welcome back flow
            setResult(Resource.forFailure(new IntentRequiredException(
                    WelcomeBackIdpPrompt.createIntent(
                            getApplication(),
                            getArguments(),
                            new User.Builder(provider, response.getEmail()).build(),
                            response),
                    RequestCodes.ACCOUNT_LINK_FLOW
            )));
        }
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == RequestCodes.ACCOUNT_LINK_FLOW) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == Activity.RESULT_OK) {
                setResult(Resource.forSuccess(response));
            } else {
                Exception e;
                if (response == null) {
                    e = new FirebaseUiException(
                            ErrorCodes.UNKNOWN_ERROR, "Link canceled by user.");
                } else {
                    e = response.getError();
                }
                setResult(Resource.forFailure(e));
            }
        }
    }

    private void handleGenericIdpLinkingFlow(@NonNull final IdpResponse idpResponse) {
        ProviderUtils.fetchSortedProviders(getAuth(), getArguments(), idpResponse.getEmail())
                .addOnSuccessListener(providers -> {
                    if (providers.isEmpty()) {
                        setResult(Resource.forFailure(
                                new FirebaseUiException(ErrorCodes.DEVELOPER_ERROR,
                                        "No supported providers.")));
                        return;
                    }
                    startWelcomeBackFlowForLinking(providers.get(0), idpResponse);
                })
                .addOnFailureListener(e -> setResult(Resource.forFailure(e)));
    }

    private boolean isEmailOrPhoneProvider(@NonNull String provider) {
        return TextUtils.equals(provider, EmailAuthProvider.PROVIDER_ID)
                || TextUtils.equals(provider, PhoneAuthProvider.PROVIDER_ID);
    }
}
