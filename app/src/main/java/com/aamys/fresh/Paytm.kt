package com.renzvos.paymentgateway.paytm


import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.paytm.pgsdk.PaytmOrder
import com.paytm.pgsdk.PaytmPaymentTransactionCallback
import com.paytm.pgsdk.TransactionManager
import com.aamys.fresh.initiate_transaction
import com.aamys.fresh.initiate_transaction_callback
import com.aamys.fresh.initiate_transaction_response_object
import com.renzvos.rzapi.RzapiError
import org.json.JSONException
import org.json.JSONObject


class Paytm(IsFragment: Boolean, activity: AppCompatActivity, access : PaytmAccessKeys , debug :Boolean) {
    private val Isfragment = IsFragment
    private var fragment : Fragment?=null
    private var activity  = activity
    private val access = access
    private val mid : String
    private val staging : String
    private val secret : String
    private  val debug = debug
    private var SDKcallback : SDKTransactioncallback?= null

    val PAYTM_REQUESTCODE = 2356

    init {
        if (debug)
        {
            mid = access.paytm_development_mid
            staging = access.paytm_development_staging
            secret = access.paytm_development_secret
        }else
        {
            mid = access.paytm_production_mid
            staging = access.paytm_production_staging
            secret = access.paytm_production_secret
        }

    }




    constructor(fragment: Fragment , access : PaytmAccessKeys, debug : Boolean) : this(true, fragment.activity as AppCompatActivity ,access,debug) {
        this.fragment = fragment
    }

    constructor(activity: AppCompatActivity , access : PaytmAccessKeys, debug : Boolean) : this(false,activity,access,debug)
    {}

    fun PaytmSDK(order: PaytmOrderData, txnToken : String , callback: SDKTransactioncallback , responses: PaytmResponses)
    {StartSDK(order,txnToken, callback,responses) }

    fun PaytmSDK(order: PaytmOrderData, callback : SDKTransactioncallback, init : initiate_transaction_callback, errorCallbacks : PaytmResponses)
    {
        ClientSideInitiate(order,object : initiate_transaction_callback{
            override fun Success(txnToken: String, response: initiate_transaction_response_object) {
                StartSDK(order,txnToken,callback,errorCallbacks) }

            override fun SuccessIdempotent(txnToken: String, response: initiate_transaction_response_object
            ) { StartSDK(order,txnToken,callback,errorCallbacks) }

            override fun InvalidPromo(response: initiate_transaction_response_object) { init.InvalidPromo(response) }
            override fun PromoAmountHigh(response: initiate_transaction_response_object) { init.PromoAmountHigh(response) }
            override fun TxnAmountInvalid(response: initiate_transaction_response_object) { init.TxnAmountInvalid(response) }
        },object : PaytmResponses{
            override fun SSOTokenInvalid(response: PaytmResponseObject) { errorCallbacks.SSOTokenInvalid(response) }
            override fun ChecksumInvalid(response: PaytmResponseObject) { errorCallbacks.ChecksumInvalid(response) }
            override fun UnMatchedMid(response: PaytmResponseObject) { errorCallbacks.UnMatchedMid(response) }
            override fun UnMatcedOrderId(response: PaytmResponseObject) { errorCallbacks.UnMatcedOrderId(response) }
            override fun RepeatRequestInconsistent(response: PaytmResponseObject) { errorCallbacks.RepeatRequestInconsistent(response) }
            override fun SystemError(response: PaytmResponseObject) { errorCallbacks.SystemError(response) }
            override fun ConnectionError(error: RzapiError) { errorCallbacks.ConnectionError(error) }
            override fun JSONException(error: JSONException) { errorCallbacks.JSONException(error) }
            override fun PipeCharectorNotAllowed(response: PaytmResponseObject) { errorCallbacks.PipeCharectorNotAllowed(response) }
            override fun SessionExplired(response: PaytmResponseObject) { errorCallbacks.SessionExplired(response) }
            override fun UserNotCompletedTransaction(response: PaytmResponseObject) { errorCallbacks.SessionExplired(response) }
            override fun WalletConsultFailed(response: PaytmResponseObject) { errorCallbacks.WalletConsultFailed(response) }
            override fun AmountExceedsLimit(response: PaytmResponseObject) { errorCallbacks.AmountExceedsLimit(response) }
            override fun UserDoesNotHaveCredit_BankDeclined(response: PaytmResponseObject) { errorCallbacks.UserNotCompletedTransaction(response) }
            override fun TransactionDeclinedByBank(response: PaytmResponseObject) { errorCallbacks.TransactionDeclinedByBank(response) }
            override fun CardExplired(response: PaytmResponseObject) { errorCallbacks.CardExplired(response) }
            override fun CardInvalid(response: PaytmResponseObject) { errorCallbacks.CardExplired(response) }
            override fun LostCardData(response: PaytmResponseObject) { errorCallbacks.LostCardData(response) }
            override fun BankCommunicationError(response: PaytmResponseObject) { errorCallbacks.BankCommunicationError(response) }
            override fun UnMatchedAmount(response: PaytmResponseObject) { errorCallbacks.UnMatchedAmount(response) }
            override fun ThreeDSecureVerificationFailed(response: PaytmResponseObject) { errorCallbacks.ThreeDSecureVerificationFailed(response) }
            override fun InvalidAccountDetails(response: PaytmResponseObject) { errorCallbacks.InvalidAccountDetails(response) }
            override fun MandatoryFieldMissing(response: PaytmResponseObject) { errorCallbacks.MandatoryFieldMissing(response) }
            override fun BankUnavailibleTryAnother(response: PaytmResponseObject) { errorCallbacks.BankUnavailibleTryAnother(response) }
            override fun CancelAndRedirectTo3d(response: PaytmResponseObject) { errorCallbacks.CancelAndRedirectTo3d(response) }
            override fun InvalidRequestType(response: PaytmResponseObject) { errorCallbacks.InvalidRequestType(response) }
            override fun InvalidAmount(response: PaytmResponseObject) { errorCallbacks.InvalidAmount(response) }
            override fun InvalidOrderId(response: PaytmResponseObject) { errorCallbacks.InvalidOrderId(response) }
            override fun InvalidCardNo(response: PaytmResponseObject) { errorCallbacks.InvalidCardNo(response) }
            override fun InvalidMonth(response: PaytmResponseObject) { errorCallbacks.InvalidMonth(response) }
            override fun InvalidYear(response: PaytmResponseObject) { errorCallbacks.InvalidYear(response) }
            override fun InvalidCVV(response: PaytmResponseObject) { errorCallbacks.InvalidCVV(response) }
            override fun InvalidPaymentMode(response: PaytmResponseObject) { errorCallbacks.InvalidPaymentMode(response) }
            override fun InvalidCustID(response: PaytmResponseObject) { errorCallbacks.InvalidCustID(response) }
            override fun InvalidIndustryID(response: PaytmResponseObject) { errorCallbacks.InvalidIndustryID(response) }
            override fun DuplicateOrderID(response: PaytmResponseObject) { errorCallbacks.DuplicateOrderID(response) }
            override fun RetryCountBreached(response: PaytmResponseObject) { errorCallbacks.RetryCountBreached(response) }
            override fun AbandonedTransaction(response: PaytmResponseObject) { errorCallbacks.AbandonedTransaction(response) }
            override fun TransactionAbandonedfromCCAvenue(response: PaytmResponseObject) { errorCallbacks.TransactionAbandonedfromCCAvenue(response) }
            override fun BankDeclined(response: PaytmResponseObject) { errorCallbacks.BankDeclined(response) }
            override fun MerchentError(response: PaytmResponseObject) { errorCallbacks.MerchentError(response) }
            override fun MerchecntNotAbailible(response: PaytmResponseObject) { errorCallbacks.MerchecntNotAbailible(response) }
            override fun ClosedPageAfterLoad(response: PaytmResponseObject) { errorCallbacks.ClosedPageAfterLoad(response) }
            override fun RequestParameterNotValid(response: PaytmResponseObject) { errorCallbacks.RequestParameterNotValid(response) }
            override fun UnIdentifiedError(response: PaytmResponseObject) { errorCallbacks.UnIdentifiedError(response) }

        })

    }

    private fun StartSDK(order: PaytmOrderData , txnToken: String, callback: SDKTransactioncallback,err_responses: PaytmResponses)
    {
        SDKcallback = callback
        val paytmOrder = PaytmOrder(order.paytmOrderId, mid, txnToken, order.txnAmount, order.callbackurl)
        val transactionManager =  TransactionManager(paytmOrder, object : PaytmPaymentTransactionCallback{
            override fun onTransactionResponse(p0: Bundle?) {HandleSDKSuccess(p0,callback,err_responses)}
            override fun networkNotAvailable() {
                callback.ConnectionErrorinSDK() }
            override fun onErrorProceed(p0: String?) {
                callback.onErrorProceed(p0) }
            override fun clientAuthenticationFailed(p0: String?) {
                callback.clientAuthenticationFailed(p0) }
            override fun someUIErrorOccurred(p0: String?) {
                callback.someUIErrorOccurred(p0) }
            override fun onErrorLoadingWebPage(p0: Int, p1: String?, p2: String?) {
                callback.onErrorLoadingWebPage(p0,p1,p2) }
            override fun onBackPressedCancelTransaction() {
                callback.onBackPressedCancelTransaction() }
            override fun onTransactionCancel(p0: String?, p1: Bundle?) {
                callback.onTransactionCancel(p0,p1) }
        } )

        transactionManager.setAppInvokeEnabled(false)
        if(debug) {transactionManager.setShowPaymentUrl("https://securegw-stage.paytm.in/theia/api/v1/showPaymentPage");}
        else { transactionManager.setShowPaymentUrl("https://securegw.paytm.in/theia/api/v1/showPaymentPage"); }
        transactionManager.startTransactionAfterCheckingLoginStatus(activity, null, PAYTM_REQUESTCODE);


    }




    fun ClientSideInitiate(order: PaytmOrderData ,responses: initiate_transaction_callback,errorCallbacks: PaytmResponses) {
        val initiateTransaction =
            initiate_transaction(activity.applicationContext, access, order, debug)
        initiateTransaction.run(responses,errorCallbacks)
    }


    fun DeprecatedActivityResultCatcher(requestCode : Int, data : Intent)
    {
        if (requestCode == PAYTM_REQUESTCODE) {


            Toast.makeText(
                activity.applicationContext,
                data.getStringExtra("nativeSdkForMerchantMessage") + data.getStringExtra("response"),
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    fun HandleSDKSuccess(bundle : Bundle? , callback: SDKTransactioncallback,err_responses: PaytmResponses)
    {

        if (bundle != null) {
            val bundleresponse = bundle.getString("response",null)
            if (bundleresponse == null){callback.SDK_NoBuddleException()
                return
            }
            else
            {
                try {
                    val p0 = JSONObject(bundleresponse)
                    try {
                        val successObject  = PaytmSuccessObject(p0)
                        if (successObject.txnStatus.equals("TXN_SUCCESS"))
                        {
                            callback.Success(successObject)
                        }
                        else
                        {
                            val apiResponses = PaytmResponseObject(successObject.txnCode,"")
                            val found  = apiResponses.CommonCallBack(err_responses)
                            if(!found)
                            {
                                callback.Failure(successObject)
                            }
                        }
                    }
                    catch (e : JSONException){callback.SDK_ResponseParamaters_Missing(p0.toString());
                        e.printStackTrace()
                    }
                }catch (e : Exception){
                    callback.SDK_ResponseParamaters_Missing(bundleresponse)}

            }




        }
        else
        {
            callback.SDK_NoBuddleException()
        }

    }









}