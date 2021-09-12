package com.example.vegapp.viewmodel;

import android.util.Log;

import com.example.vegapp.AuthUI;
import com.example.vegapp.R;
import com.example.vegapp.data.model.Resource;
import com.example.vegapp.data.model.State;
import com.example.vegapp.ui.FragmentBase;
import com.example.vegapp.ui.HelperActivityBase;
import com.example.vegapp.ui.ProgressView;
import com.example.vegapp.util.ui.FlowUtils;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
import androidx.annotation.StringRes;
import androidx.lifecycle.Observer;

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public abstract class ResourceObserver<T> implements Observer<Resource<T>> {

    private final ProgressView mProgressView;
    private final HelperActivityBase mActivity;
    private final FragmentBase mFragment;
    private final int mLoadingMessage;

    protected ResourceObserver(@NonNull HelperActivityBase activity) {
        this(activity, null, activity, R.string.fui_progress_dialog_loading);
    }

    protected ResourceObserver(@NonNull HelperActivityBase activity, @StringRes int message) {
        this(activity, null, activity, message);
    }

    protected ResourceObserver(@NonNull FragmentBase fragment) {
        this(null, fragment, fragment, R.string.fui_progress_dialog_loading);
    }

    protected ResourceObserver(@NonNull FragmentBase fragment, @StringRes int message) {
        this(null, fragment, fragment, message);
    }

    private ResourceObserver(HelperActivityBase activity,
                             FragmentBase fragment,
                             ProgressView progressView,
                             int message) {
        mActivity = activity;
        mFragment = fragment;

        if (mActivity == null && mFragment == null) {
            throw new IllegalStateException("ResourceObserver must be attached to an Activity or a Fragment");
        }

        mProgressView = progressView;
        mLoadingMessage = message;
    }

    @Override
    public final void onChanged(Resource<T> resource) {
        if (resource.getState() == State.LOADING) {
            mProgressView.showProgress(mLoadingMessage);
            return;
        }
        mProgressView.hideProgress();

        if (resource.isUsed()) { return; }

        if (resource.getState() == State.SUCCESS) {
            onSuccess(resource.getValue());
        } else if (resource.getState() == State.FAILURE) {
            Exception e = resource.getException();
            boolean unhandled;
            if (mFragment == null) {
                unhandled = FlowUtils.unhandled(mActivity, e);
            } else {
                unhandled = FlowUtils.unhandled(mFragment, e);
            }
            if (unhandled) {
                Log.e(AuthUI.TAG, "A sign-in error occurred.", e);
                onFailure(e);
            }
        }
    }

    protected abstract void onSuccess(@NonNull T t);

    protected abstract void onFailure(@NonNull Exception e);
}
