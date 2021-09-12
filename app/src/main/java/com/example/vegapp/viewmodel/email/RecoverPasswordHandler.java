package com.example.vegapp.viewmodel.email;

import android.app.Application;

import com.example.vegapp.data.model.Resource;
import com.example.vegapp.viewmodel.AuthViewModelBase;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public class RecoverPasswordHandler extends AuthViewModelBase<String> {
    public RecoverPasswordHandler(Application application) {
        super(application);
    }

    public void startReset(@NonNull final String email, @Nullable ActionCodeSettings actionCodeSettings) {
        setResult(Resource.forLoading());
        Task<Void> reset = actionCodeSettings != null
                ? getAuth().sendPasswordResetEmail(email, actionCodeSettings)
                : getAuth().sendPasswordResetEmail(email);

        reset.addOnCompleteListener(task -> {
            Resource<String> resource = task.isSuccessful()
                    ? Resource.forSuccess(email)
                    : Resource.forFailure(task.getException());
            setResult(resource);
        });
    }
}
