package com.aamys.fresh.data.remote;

import android.app.Application;

import com.aamys.fresh.ui.HelperActivityBase;
import com.aamys.fresh.viewmodel.ProviderSignInBase;

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
