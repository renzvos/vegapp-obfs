package com.example.vegapp.viewmodel.email;

import android.app.Application;
import android.util.Log;

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
import com.example.vegapp.util.data.AuthOperationManager;
import com.example.vegapp.util.data.ProviderUtils;
import com.example.vegapp.util.data.TaskFailureLogger;
import com.example.vegapp.viewmodel.RequestCodes;
import com.example.vegapp.viewmodel.SignInViewModelBase;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;

import static com.example.vegapp.AuthUI.EMAIL_LINK_PROVIDER;

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public class EmailProviderResponseHandler extends SignInViewModelBase {
    private static final String TAG = "EmailProviderResponseHa";

    public EmailProviderResponseHandler(Application application) {
        super(application);
    }

    public void startSignIn(@NonNull final IdpResponse response, @NonNull final String password) {
        if (!response.isSuccessful()) {
            setResult(Resource.forFailure(response.getError()));
            return;
        }
        if (!response.getProviderType().equals(EmailAuthProvider.PROVIDER_ID)) {
            throw new IllegalStateException(
                    "This handler can only be used with the email provider");
        }
        setResult(Resource.forLoading());

        final AuthOperationManager authOperationManager = AuthOperationManager.getInstance();
        final String email = response.getEmail();
        authOperationManager.createOrLinkUserWithEmailAndPassword(getAuth(),
                getArguments(),
                email,
                password)
                .continueWithTask(new ProfileMerger(response))
                .addOnFailureListener(new TaskFailureLogger(TAG, "Error creating user"))
                .addOnSuccessListener(result -> handleSuccess(response, result))
                .addOnFailureListener(e -> {
                    if (e instanceof FirebaseAuthUserCollisionException) {
                        if (authOperationManager.canUpgradeAnonymous(getAuth(),
                                getArguments())) {
                            AuthCredential credential = EmailAuthProvider.getCredential(email,
                                    password);
                            handleMergeFailure(credential);
                        } else {
                            Log.w(TAG, "Got a collision error during a non-upgrade flow", e);

                            // Collision with existing user email without anonymous upgrade
                            // it should be very hard for the user to even get to this error
                            // due to CheckEmailFragment.
                            ProviderUtils.fetchTopProvider(getAuth(), getArguments(), email)
                                    .addOnSuccessListener(new StartWelcomeBackFlow(email))
                                    .addOnFailureListener(e1 -> setResult(Resource.forFailure(
                                            e1)));
                        }
                    } else {
                        setResult(Resource.forFailure(e));
                    }
                });
    }

    private class StartWelcomeBackFlow implements OnSuccessListener<String> {
        private final String mEmail;

        public StartWelcomeBackFlow(String email) {
            mEmail = email;
        }

        @Override
        public void onSuccess(@Nullable String provider) {
            if (provider == null) {
                Log.w(TAG, "No providers known for user ("
                        + mEmail
                        + ") this email address may be reserved.");
                setResult(Resource.forFailure(
                        new FirebaseUiException(ErrorCodes.UNKNOWN_ERROR)));
                return;
            }

            if (EmailAuthProvider.PROVIDER_ID.equalsIgnoreCase(provider)) {
                setResult(Resource.forFailure(new IntentRequiredException(
                        WelcomeBackPasswordPrompt.createIntent(
                                getApplication(),
                                getArguments(),
                                new IdpResponse.Builder(new User.Builder(
                                        EmailAuthProvider.PROVIDER_ID, mEmail).build()
                                ).build()),
                        RequestCodes.WELCOME_BACK_EMAIL_FLOW
                )));
            } else if (EMAIL_LINK_PROVIDER.equalsIgnoreCase(provider)) {
                setResult(Resource.forFailure(new IntentRequiredException(
                        WelcomeBackEmailLinkPrompt.createIntent(
                                getApplication(),
                                getArguments(),
                                new IdpResponse.Builder(new User.Builder(
                                        EMAIL_LINK_PROVIDER, mEmail).build()
                                ).build()),
                        RequestCodes.WELCOME_BACK_EMAIL_LINK_FLOW
                )));
            } else {
                setResult(Resource.forFailure(new IntentRequiredException(
                        WelcomeBackIdpPrompt.createIntent(
                                getApplication(),
                                getArguments(),
                                new User.Builder(provider, mEmail).build()),
                        RequestCodes.WELCOME_BACK_IDP_FLOW
                )));
            }
        }
    }
}
