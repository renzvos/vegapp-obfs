package com.example.vegapp.data.remote;

import android.app.Application;

import com.example.vegapp.ui.HelperActivityBase;
import com.example.vegapp.viewmodel.ProviderSignInBase;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public abstract class SingleProviderSignInHandler<T> extends ProviderSignInBase<T> {

    private final String mProviderId;

    protected SingleProviderSignInHandler(Application application, String providerId) {
        super(application);
        this.mProviderId = providerId;
    }

    @Override
    public final void startSignIn(@NonNull HelperActivityBase activity) {
        this.startSignIn(activity.getAuth(), activity, mProviderId);
    }
}
