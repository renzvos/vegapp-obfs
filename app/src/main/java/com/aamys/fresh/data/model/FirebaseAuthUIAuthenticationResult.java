package com.aamys.fresh.data.model;

import com.aamys.fresh.FirebaseAuthUIActivityResultContract;
import com.aamys.fresh.IdpResponse;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Result of launching a {@link FirebaseAuthUIActivityResultContract}
 */
public class FirebaseAuthUIAuthenticationResult {

    @Nullable
    private final IdpResponse idpResponse;
    @NonNull
    private final Integer resultCode;

    public FirebaseAuthUIAuthenticationResult(@NonNull Integer resultCode, @Nullable IdpResponse idpResponse) {
        this.idpResponse = idpResponse;
        this.resultCode = resultCode;
    }

    /**
     * The contained {@link IdpResponse} returned from the Firebase library
     */
    @Nullable
    public IdpResponse getIdpResponse() {
        return idpResponse;
    }

    /**
     * The result code of the received activity result
     *
     * @see android.app.Activity.RESULT_CANCELED
     * @see android.app.Activity.RESULT_OK
     */
    @NonNull
    public Integer getResultCode() {
        return resultCode;
    }

    @Override
    public int hashCode() {
        int result = idpResponse == null ? 0 : idpResponse.hashCode();
        result = 31 * result + resultCode.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "FirebaseAuthUIAuthenticationResult{" +
                "idpResponse=" + idpResponse +
                ", resultCode='" + resultCode +
                '}';
    }
}
