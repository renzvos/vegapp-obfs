package com.example.vegapp.data.remote;

import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookRequestError;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.WebDialog;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.example.vegapp.AuthUI;
import com.example.vegapp.ErrorCodes;
import com.example.vegapp.FirebaseUiException;
import com.example.vegapp.IdpResponse;
import com.example.vegapp.data.model.Resource;
import com.example.vegapp.data.model.User;
import com.example.vegapp.data.model.UserCancellationException;
import com.example.vegapp.ui.HelperActivityBase;
import com.example.vegapp.util.ExtraConstants;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public class FacebookSignInHandler extends SingleProviderSignInHandler<AuthUI.IdpConfig> {
    private static final String EMAIL = "email";
    private static final String PUBLIC_PROFILE = "public_profile";

    private List<String> mPermissions;

    private final FacebookCallback<LoginResult> mCallback = new Callback();
    private final CallbackManager mCallbackManager = CallbackManager.Factory.create();

    public FacebookSignInHandler(Application application) {
        super(application, FacebookAuthProvider.PROVIDER_ID);
    }

    private static IdpResponse createIdpResponse(
            LoginResult result, @Nullable String email, String name, Uri photoUri) {
        return new IdpResponse.Builder(
                new User.Builder(FacebookAuthProvider.PROVIDER_ID, email)
                        .setName(name)
                        .setPhotoUri(photoUri)
                        .build())
                .setToken(result.getAccessToken().getToken())
                .build();
    }

    @Override
    protected void onCreate() {
        List<String> permissions = getArguments().getParams()
                .getStringArrayList(ExtraConstants.FACEBOOK_PERMISSIONS);
        permissions = new ArrayList<>(
                permissions == null ? Collections.emptyList() : permissions);

        // Ensure we have email and public_profile permissions
        if (!permissions.contains(EMAIL)) { permissions.add(EMAIL); }
        if (!permissions.contains(PUBLIC_PROFILE)) { permissions.add(PUBLIC_PROFILE); }

        mPermissions = permissions;

        LoginManager.getInstance().registerCallback(mCallbackManager, mCallback);
    }

    @Override
    public void startSignIn(@NonNull FirebaseAuth auth,
                            @NonNull HelperActivityBase activity,
                            @NonNull String providerId) {
        WebDialog.setWebDialogTheme(activity.getFlowParams().themeId);
        LoginManager.getInstance().logInWithReadPermissions(activity, mPermissions);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        LoginManager.getInstance().unregisterCallback(mCallbackManager);
    }

    private class Callback implements FacebookCallback<LoginResult> {
        @Override
        public void onSuccess(LoginResult result) {
            setResult(Resource.forLoading());

            GraphRequest request = GraphRequest.newMeRequest(result.getAccessToken(),
                    new ProfileRequest(result));

            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,email,picture");
            request.setParameters(parameters);
            request.executeAsync();
        }

        @Override
        public void onCancel() {
            setResult(Resource.forFailure(new UserCancellationException()));
        }

        @Override
        public void onError(FacebookException e) {
            setResult(Resource.forFailure(new FirebaseUiException(
                    ErrorCodes.PROVIDER_ERROR, e)));
        }
    }

    private class ProfileRequest implements GraphRequest.GraphJSONObjectCallback {
        private final LoginResult mResult;

        public ProfileRequest(LoginResult result) {
            mResult = result;
        }

        @Override
        public void onCompleted(JSONObject object, GraphResponse response) {
            FacebookRequestError error = response.getError();
            if (error != null) {
                setResult(Resource.forFailure(new FirebaseUiException(
                        ErrorCodes.PROVIDER_ERROR, error.getException())));
                return;
            }
            if (object == null) {
                setResult(Resource.forFailure(new FirebaseUiException(
                        ErrorCodes.PROVIDER_ERROR, "Facebook graph request failed")));
                return;
            }

            String email = null;
            String name = null;
            Uri photoUri = null;

            try {
                email = object.getString("email");
            } catch (JSONException ignored) {}
            try {
                name = object.getString("name");
            } catch (JSONException ignored) {}
            try {
                photoUri = Uri.parse(object.getJSONObject("picture")
                        .getJSONObject("data")
                        .getString("url"));
            } catch (JSONException ignored) {}

            setResult(Resource.forSuccess(createIdpResponse(mResult, email, name, photoUri)));
        }
    }
}
