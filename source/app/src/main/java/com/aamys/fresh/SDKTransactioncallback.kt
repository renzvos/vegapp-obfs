package com.renzvos.paymentgateway.paytm

import android.os.Bundle
import org.json.JSONObject

interface SDKTransactioncallback{

    fun Success(details : PaytmSuccessObject)
    fun Failure(details: PaytmSuccessObject)
    fun onErrorProceed(p0: String?)
    fun ConnectionErrorinSDK()
    fun clientAuthenticationFailed(p0: String?)
    fun someUIErrorOccurred(p0: String?)
    fun onErrorLoadingWebPage(p0: Int, p1: String?, p2: String?)
    fun onBackPressedCancelTransaction()
    fun onTransactionCancel(p0: String?, p1: Bundle?)

    fun SDK_NoBuddleException()
    fun SDK_ResponseParamaters_Missing(p0 : String)



}