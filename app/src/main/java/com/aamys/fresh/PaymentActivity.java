package com.aamys.fresh;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.aamys.fresh.initiate_transaction_callback;import com.aamys.fresh.initiate_transaction_response_object;import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.renzvos.paymentgateway.paytm.Paytm;
import com.renzvos.paymentgateway.paytm.PaytmAccessKeys;
import com.renzvos.paymentgateway.paytm.PaytmOrderData;
import com.renzvos.paymentgateway.paytm.PaytmResponseObject;
import com.renzvos.paymentgateway.paytm.PaytmResponses;
import com.renzvos.paymentgateway.paytm.PaytmSuccessObject;
import com.renzvos.paymentgateway.paytm.SDKTransactioncallback;
import com.renzvos.rzapi.RzapiError;

import org.json.JSONException;

public class PaymentActivity extends AppCompatActivity {
    Paytm paytm;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    FirebaseOrderClass AppOrderData;
    TextView process;
    public static final String PAYMENTPREFERENCES = "Pmnt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedpreferences = getSharedPreferences(PAYMENTPREFERENCES, Context.MODE_PRIVATE);
        setContentView(R.layout.activity_payment_acitivity);
        String orderid = getIntent().getStringExtra("orderid");
        String mid = sharedpreferences.getString("mid",null);
        String secret = sharedpreferences.getString("secret",null);
        String staging = sharedpreferences.getString("staging",null);
        PaytmAccessKeys accessKeys= new PaytmAccessKeys(mid,staging,secret,mid,staging,secret);
        paytm = new Paytm(this,accessKeys,false);
        process = findViewById(R.id.pleasewait);
        process.setText("Initiating Transaction");

        firestore.collection("orders").document(orderid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                AppOrderData = documentSnapshot.toObject(FirebaseOrderClass.class);
                PaytmOrderData orderData = new PaytmOrderData("amysdebug_"+ documentSnapshot.getId(), String.valueOf(AppOrderData.Amount), "INR","001" );
                String TAG = "App_Payment Page";
                paytm.PaytmSDK(orderData, new SDKTransactioncallback() {
                    @Override
                    public void Success(@NonNull PaytmSuccessObject details) {
                        Log.i(TAG, "Success: ");
                        SuccessHandle(documentSnapshot.getId());
                    }

                    @Override
                    public void Failure(@NonNull PaytmSuccessObject details) {
                        Log.i(TAG, "Failure: ");
                        FailureHandle(orderid);
                    }

                    @Override
                    public void onErrorProceed(@Nullable String p0) {
                        Log.i(TAG, "onErrorProceed: ");
                        FailureHandle(orderid);
                    }

                    @Override
                    public void ConnectionErrorinSDK() {
                        Log.i(TAG, "ConnectionErrorinSDK: ");
                        FailureHandle(orderid);
                    }

                    @Override
                    public void clientAuthenticationFailed(@Nullable String p0) {
                        Log.i(TAG, "clientAuthenticationFailed: ");
                        FailureHandle(orderid);
                    }

                    @Override
                    public void someUIErrorOccurred(@Nullable String p0) {
                        Log.i(TAG, "someUIErrorOccurred: ");
                        FailureHandle(orderid);
                    }

                    @Override
                    public void onErrorLoadingWebPage(int p0, @Nullable String p1, @Nullable String p2) {
                        Log.i(TAG, "onErrorLoadingWebPage: ");
                        FailureHandle(orderid);
                    }

                    @Override
                    public void onBackPressedCancelTransaction() {
                        Log.i(TAG, "onBackPressedCancelTransaction: ");
                        FailureHandle(orderid);
                    }

                    @Override
                    public void onTransactionCancel(@Nullable String p0, @Nullable Bundle p1) {
                        Log.i(TAG, "onTransactionCancel: ");
                        FailureHandle(orderid);
                    }

                    @Override
                    public void SDK_NoBuddleException() {
                        Log.i(TAG, "SDK_NoBuddleException: ");
                        FailureHandle(orderid);
                    }

                    @Override
                    public void SDK_ResponseParamaters_Missing(@Nullable String  p0) {
                        Log.i(TAG, "SDK_ResponseParamaters_Missing: " + p0);
                        FailureHandle(orderid);
                    }
                }, new initiate_transaction_callback() {
                    @Override
                    public void Success(@NonNull String txnToken, @NonNull initiate_transaction_response_object response) {
                        Log.i(TAG, "Success: Initiate");
                        process.setText("Waiting for Transaction Response . Please Wait");
                    }

                    @Override
                    public void SuccessIdempotent(@NonNull String txnToken, @NonNull initiate_transaction_response_object response) {
                        Log.i(TAG, "SuccessIdempotent: ");
                        process.setText("Waiting for Transaction Response . Please Wait");
                    }

                    @Override
                    public void InvalidPromo(@NonNull initiate_transaction_response_object response) {
                        Log.i(TAG, "InvalidPromo: ");
                        FailureHandle(orderid);
                    }

                    @Override
                    public void PromoAmountHigh(@NonNull initiate_transaction_response_object response) {
                        Log.i(TAG, "PromoAmountHigh: ");
                        FailureHandle(orderid);
                    }

                    @Override
                    public void TxnAmountInvalid(@NonNull initiate_transaction_response_object response) {
                        Log.i(TAG, "TxnAmountInvalid: ");
                        FailureHandle(orderid);
                    }
                }, new PaytmResponses() {
                    @Override
                    public void SSOTokenInvalid(@NonNull PaytmResponseObject response) {
                        Log.i(TAG, "SSOTokenInvalid: ");
                        FailureHandle(orderid);
                    }

                    @Override
                    public void ChecksumInvalid(@NonNull PaytmResponseObject response) {
                        Log.i(TAG, "ChecksumInvalid: ");
                        FailureHandle(orderid);
                    }

                    @Override
                    public void UnMatchedMid(@NonNull PaytmResponseObject response) {
                        Log.i(TAG, "UnMatchedMid: ");
                        FailureHandle(orderid);
                    }

                    @Override
                    public void UnMatcedOrderId(@NonNull PaytmResponseObject response) {
                        Log.i(TAG, "UnMatcedOrderId: ");
                        FailureHandle(orderid);
                    }

                    @Override
                    public void RepeatRequestInconsistent(@NonNull PaytmResponseObject response) {
                        Log.i(TAG, "RepeatRequestInconsistent: ");
                        FailureHandle(orderid);
                    }

                    @Override
                    public void SystemError(@NonNull PaytmResponseObject response) {
                        Log.i(TAG, "SystemError: ");
                        FailureHandle(orderid);
                    }

                    @Override
                    public void ConnectionError(@NonNull RzapiError error) {
                        Log.i(TAG, "ConnectionError: ");
                        FailureHandle(orderid);
                    }

                    @Override
                    public void JSONException(@NonNull JSONException error) {
                        Log.i(TAG, "JSONException: ");
                        FailureHandle(orderid);
                    }

                    @Override
                    public void PipeCharectorNotAllowed(@NonNull PaytmResponseObject response) {
                        Log.i(TAG, "PipeCharectorNotAllowed: ");
                        FailureHandle(orderid);
                    }

                    @Override
                    public void SessionExplired(@NonNull PaytmResponseObject response) {
                        Log.i(TAG, "SessionExplired: ");
                        FailureHandle(orderid);
                    }

                    @Override
                    public void UserNotCompletedTransaction(@NonNull PaytmResponseObject response) {
                        Log.i(TAG, "UserNotCompletedTransaction: ");
                        FailureHandle(orderid);
                    }

                    @Override
                    public void WalletConsultFailed(@NonNull PaytmResponseObject response) {
                        Log.i(TAG, "WalletConsultFailed: ");
                        FailureHandle(orderid);
                    }

                    @Override
                    public void AmountExceedsLimit(@NonNull PaytmResponseObject response) {
                        Log.i(TAG, "AmountExceedsLimit: ");
                        FailureHandle(orderid);
                    }

                    @Override
                    public void UserDoesNotHaveCredit_BankDeclined(@NonNull PaytmResponseObject response) {
                        Log.i(TAG, "UserDoesNotHaveCredit_BankDeclined: ");
                        FailureHandle(orderid);
                    }

                    @Override
                    public void TransactionDeclinedByBank(@NonNull PaytmResponseObject response) {
                        Log.i(TAG, "TransactionDeclinedByBank: ");
                        FailureHandle(orderid);
                    }

                    @Override
                    public void CardExplired(@NonNull PaytmResponseObject response) {
                        Log.i(TAG, "CardExplired: ");
                        FailureHandle(orderid);
                    }

                    @Override
                    public void CardInvalid(@NonNull PaytmResponseObject response) {
                        Log.i(TAG, "CardInvalid: ");
                        FailureHandle(orderid);
                    }

                    @Override
                    public void LostCardData(@NonNull PaytmResponseObject response) {
                        Log.i(TAG, "LostCardData: ");
                        FailureHandle(orderid);
                    }

                    @Override
                    public void BankCommunicationError(@NonNull PaytmResponseObject response) {
                        Log.i(TAG, "BankCommunicationError: ");
                        FailureHandle(orderid);
                    }

                    @Override
                    public void UnMatchedAmount(@NonNull PaytmResponseObject response) {
                        Log.i(TAG, "UnMatchedAmount: ");
                        FailureHandle(orderid);
                    }

                    @Override
                    public void ThreeDSecureVerificationFailed(@NonNull PaytmResponseObject response) {
                        Log.i(TAG, "ThreeDSecureVerificationFailed: ");
                        FailureHandle(orderid);
                    }

                    @Override
                    public void InvalidAccountDetails(@NonNull PaytmResponseObject response) {
                        Log.i(TAG, "InvalidAccountDetails: ");
                        FailureHandle(orderid);
                    }

                    @Override
                    public void MandatoryFieldMissing(@NonNull PaytmResponseObject response) {
                        Log.i(TAG, "MandatoryFieldMissing: ");
                        FailureHandle(orderid);
                    }

                    @Override
                    public void BankUnavailibleTryAnother(@NonNull PaytmResponseObject response) {
                        Log.i(TAG, "BankUnavailibleTryAnother: ");
                        FailureHandle(orderid);
                    }

                    @Override
                    public void CancelAndRedirectTo3d(@NonNull PaytmResponseObject response) {
                        Log.i(TAG, "CancelAndRedirectTo3d: ");
                        FailureHandle(orderid);
                    }

                    @Override
                    public void InvalidRequestType(@NonNull PaytmResponseObject response) {
                        Log.i(TAG, "InvalidRequestType: ");
                        FailureHandle(orderid);
                    }

                    @Override
                    public void InvalidAmount(@NonNull PaytmResponseObject response) {
                        Log.i(TAG, "InvalidAmount: ");
                        FailureHandle(orderid);
                    }

                    @Override
                    public void InvalidOrderId(@NonNull PaytmResponseObject response) {
                        Log.i(TAG, "InvalidOrderId: ");
                        FailureHandle(orderid);
                    }

                    @Override
                    public void InvalidCardNo(@NonNull PaytmResponseObject response) {
                        Log.i(TAG, "InvalidCardNo: ");
                        FailureHandle(orderid);
                    }

                    @Override
                    public void InvalidMonth(@NonNull PaytmResponseObject response) {
                        Log.i(TAG, "InvalidMonth: ");
                        FailureHandle(orderid);
                    }

                    @Override
                    public void InvalidYear(@NonNull PaytmResponseObject response) {
                        Log.i(TAG, "InvalidYear: ");
                        FailureHandle(orderid);
                    }

                    @Override
                    public void InvalidCVV(@NonNull PaytmResponseObject response) {
                        Log.i(TAG, "InvalidCVV: ");
                        FailureHandle(orderid);
                    }

                    @Override
                    public void InvalidPaymentMode(@NonNull PaytmResponseObject response) {
                        Log.i(TAG, "InvalidPaymentMode: ");
                        FailureHandle(orderid);
                    }

                    @Override
                    public void InvalidCustID(@NonNull PaytmResponseObject response) {
                        Log.i(TAG, "InvalidCustID: ");
                        FailureHandle(orderid);
                    }

                    @Override
                    public void InvalidIndustryID(@NonNull PaytmResponseObject response) {
                        Log.i(TAG, "InvalidIndustryID: ");
                        FailureHandle(orderid);
                    }

                    @Override
                    public void DuplicateOrderID(@NonNull PaytmResponseObject response) {
                        Log.i(TAG, "DuplicateOrderID: ");
                        FailureHandle(orderid);
                    }

                    @Override
                    public void RetryCountBreached(@NonNull PaytmResponseObject response) {
                        Log.i(TAG, "RetryCountBreached: ");
                        FailureHandle(orderid);
                    }

                    @Override
                    public void AbandonedTransaction(@NonNull PaytmResponseObject response) {
                        Log.i(TAG, "AbandonedTransaction: ");
                        FailureHandle(orderid);
                    }

                    @Override
                    public void TransactionAbandonedfromCCAvenue(@NonNull PaytmResponseObject response) {
                        Log.i(TAG, "TransactionAbandonedfromCCAvenue: ");
                        FailureHandle(orderid);
                    }

                    @Override
                    public void BankDeclined(@NonNull PaytmResponseObject response) {
                        Log.i(TAG, "BankDeclined: ");
                        FailureHandle(orderid);
                    }

                    @Override
                    public void MerchentError(@NonNull PaytmResponseObject response) {
                        Log.i(TAG, "MerchentError: ");
                        FailureHandle(orderid);
                    }

                    @Override
                    public void MerchecntNotAbailible(@NonNull PaytmResponseObject response) {
                        Log.i(TAG, "MerchecntNotAbailible: ");
                        FailureHandle(orderid);
                    }

                    @Override
                    public void ClosedPageAfterLoad(@NonNull PaytmResponseObject response) {
                        Log.i(TAG, "ClosedPageAfterLoad: ");
                        FailureHandle(orderid);
                    }

                    @Override
                    public void RequestParameterNotValid(@NonNull PaytmResponseObject response) {
                        Log.i(TAG, "RequestParameterNotValid: ");
                        FailureHandle(orderid);
                    }

                    @Override
                    public void UnIdentifiedError(@NonNull PaytmResponseObject response) {
                        Log.i(TAG, "UnIdentifiedError: ");
                        FailureHandle(orderid);
                    }
                });

            }});



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        paytm.DeprecatedActivityResultCatcher(requestCode,data);
    }

    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

    public void SuccessHandle(String id)
    {
        firestore.collection("orders").document(id).update("payment",true).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                process.setText("Transaction Successfull");
                firestore.collection("users").document(uid).update("Cart",new FirebaseCart());
                Intent intent = new Intent(PaymentActivity.this,OrderStatus.class);
                intent.putExtra("orderid", id);
                intent.putExtra("origin", "checkout");
                startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                process.setText("Transaction Successfull . Retrying to commuinicate with Aamy's");
                SuccessHandle(id);
            }
        });
    }

    public void FailureHandle(String id)
    {
        Intent intent = new Intent(PaymentActivity.this,OrderStatus.class);
        intent.putExtra("orderid", id);
        intent.putExtra("origin", "checkout");
        startActivity(intent);
    }
}
