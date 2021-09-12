/*
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.vegapp.ui.email;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.vegapp.R;
import com.example.vegapp.data.model.FlowParameters;
import com.example.vegapp.ui.AppCompatBase;
import com.example.vegapp.ui.HelperActivityBase;
import com.example.vegapp.util.ExtraConstants;
import com.example.vegapp.util.data.PrivacyDisclosureUtils;
import com.example.vegapp.util.ui.ImeHelper;
import com.example.vegapp.util.ui.fieldvalidators.EmailFieldValidator;
import com.example.vegapp.viewmodel.ResourceObserver;
import com.example.vegapp.viewmodel.email.RecoverPasswordHandler;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.lifecycle.ViewModelProvider;

/**
 * Activity to initiate the "forgot password" flow by asking for the user's email.
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public class RecoverPasswordActivity extends AppCompatBase implements View.OnClickListener,
        ImeHelper.DonePressedListener {
    private RecoverPasswordHandler mHandler;

    private ProgressBar mProgressBar;
    private Button mSubmitButton;
    private TextInputLayout mEmailInputLayout;
    private EditText mEmailEditText;
    private EmailFieldValidator mEmailFieldValidator;

    public static Intent createIntent(Context context, FlowParameters params, String email) {
        return HelperActivityBase.createBaseIntent(context, RecoverPasswordActivity.class, params)
                .putExtra(ExtraConstants.EMAIL, email);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fui_forgot_password_layout);

        mHandler = new ViewModelProvider(this).get(RecoverPasswordHandler.class);
        mHandler.init(getFlowParams());
        mHandler.getOperation().observe(this, new ResourceObserver<String>(
                this, R.string.fui_progress_dialog_sending) {
            @Override
            protected void onSuccess(@NonNull String email) {
                mEmailInputLayout.setError(null);
                showEmailSentDialog(email);
            }

            @Override
            protected void onFailure(@NonNull Exception e) {
                if (e instanceof FirebaseAuthInvalidUserException
                        || e instanceof FirebaseAuthInvalidCredentialsException) {
                    // No FirebaseUser exists with this email address, show error.
                    mEmailInputLayout.setError(getString(R.string.fui_error_email_does_not_exist));
                } else {
                    // Unknown error
                    mEmailInputLayout.setError(getString(R.string.fui_error_unknown));
                }
            }
        });

        mProgressBar = findViewById(R.id.top_progress_bar);
        mSubmitButton = findViewById(R.id.button_done);
        mEmailInputLayout = findViewById(R.id.email_layout);
        mEmailEditText = findViewById(R.id.email);
        mEmailFieldValidator = new EmailFieldValidator(mEmailInputLayout);

        String email = getIntent().getStringExtra(ExtraConstants.EMAIL);
        if (email != null) {
            mEmailEditText.setText(email);
        }

        ImeHelper.setImeOnDoneListener(mEmailEditText, this);
        mSubmitButton.setOnClickListener(this);

        TextView footerText = findViewById(R.id.email_footer_tos_and_pp_text);
        PrivacyDisclosureUtils.setupTermsOfServiceFooter(this, getFlowParams(), footerText);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button_done) {
            onDonePressed();
        }
    }

    @Override
    public void onDonePressed() {
        if (mEmailFieldValidator.validate(mEmailEditText.getText())) {
            if (getFlowParams().passwordResetSettings != null) {
                resetPassword(mEmailEditText.getText().toString(), getFlowParams().passwordResetSettings);
            }
            else {
                resetPassword(mEmailEditText.getText().toString(), null);
            }
        }
    }

    private void resetPassword(String email, @Nullable ActionCodeSettings passwordResetSettings) {
        mHandler.startReset(email, passwordResetSettings);
    }
    private void showEmailSentDialog(String email) {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.fui_title_confirm_recover_password)
                .setMessage(getString(R.string.fui_confirm_recovery_body, email))
                .setOnDismissListener(dialog -> finish(Activity.RESULT_OK, new Intent()))
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    @Override
    public void showProgress(int message) {
        mSubmitButton.setEnabled(false);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        mSubmitButton.setEnabled(true);
        mProgressBar.setVisibility(View.INVISIBLE);
    }
}
