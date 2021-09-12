package com.example.vegapp.viewmodel.email;

import android.app.Application;

import com.example.vegapp.IdpResponse;
import com.example.vegapp.data.model.Resource;
import com.example.vegapp.util.data.AuthOperationManager;
import com.example.vegapp.util.data.ContinueUrlBuilder;
import com.example.vegapp.util.data.EmailLinkPersistenceManager;
import com.example.vegapp.util.data.SessionUtils;
import com.example.vegapp.viewmodel.AuthViewModelBase;
import com.google.firebase.auth.ActionCodeSettings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class EmailLinkSendEmailHandler extends AuthViewModelBase<String> {
    private static final int SESSION_ID_LENGTH = 10;

    public EmailLinkSendEmailHandler(Application application) {
        super(application);
    }

    public void sendSignInLinkToEmail(@NonNull final String email,
                                      @NonNull final ActionCodeSettings actionCodeSettings,
                                      @Nullable final IdpResponse idpResponseForLinking,
                                      final boolean forceSameDevice) {
        if (getAuth() == null) {
            return;
        }
        setResult(Resource.forLoading());

        final String anonymousUserId =
                AuthOperationManager.getInstance().canUpgradeAnonymous(getAuth(), getArguments())
                ? getAuth().getCurrentUser().getUid() : null;
        final String sessionId =
                SessionUtils.generateRandomAlphaNumericString(SESSION_ID_LENGTH);

        ActionCodeSettings mutatedSettings = addSessionInfoToActionCodeSettings(actionCodeSettings,
                sessionId, anonymousUserId, idpResponseForLinking, forceSameDevice);

        getAuth().sendSignInLinkToEmail(email, mutatedSettings)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        EmailLinkPersistenceManager.getInstance().saveEmail(getApplication(),
                                email, sessionId, anonymousUserId);

                        setResult(Resource.forSuccess(email));
                    } else {
                        setResult(Resource.forFailure(task.getException()));
                    }
                });
    }

    private ActionCodeSettings addSessionInfoToActionCodeSettings(@NonNull ActionCodeSettings
                                                                          actionCodeSettings,
                                                                  @NonNull String sessionId,
                                                                  @NonNull String anonymousUserId,
                                                                  @Nullable IdpResponse response,
                                                                  boolean forceSameDevice) {

        String continueUrl = actionCodeSettings.getUrl();
        ContinueUrlBuilder continueUrlBuilder = new ContinueUrlBuilder(continueUrl);
        continueUrlBuilder.appendSessionId(sessionId);
        continueUrlBuilder.appendAnonymousUserId(anonymousUserId);
        continueUrlBuilder.appendForceSameDeviceBit(forceSameDevice);
        if (response != null) {
            continueUrlBuilder.appendProviderId(response.getProviderType());
        }

        return ActionCodeSettings.newBuilder()
                .setUrl(continueUrlBuilder.build())
                .setHandleCodeInApp(true)
                .setAndroidPackageName(actionCodeSettings.getAndroidPackageName(),
                        actionCodeSettings.getAndroidInstallApp(),
                        actionCodeSettings.getAndroidMinimumVersion())
                .setIOSBundleId(actionCodeSettings.getIOSBundle())
                .build();
    }
}
