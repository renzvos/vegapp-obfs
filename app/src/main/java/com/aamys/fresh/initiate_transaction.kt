package com.aamys.fresh


import android.content.Context
import com.renzvos.paymentgateway.paytm.PaytmAccessKeys
import com.renzvos.paymentgateway.paytm.PaytmOrderData
import com.renzvos.paymentgateway.paytm.PaytmResponses


import com.renzvos.rzapi.*

import org.json.JSONObject
import java.net.URL


class initiate_transaction ( context:Context ,access : PaytmAccessKeys , order :PaytmOrderData , debug : Boolean) {
    private val order = order
    private val context = context
    private val mid : String
    private val staging : String
    private val secret : String
    private val debug = debug

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





   fun run(callback : initiate_transaction_callback, errors : PaytmResponses){

        val paytmParams = JSONObject()
        val body = JSONObject()
        body.put("requestType", "Payment")
        body.put("mid", mid)
        body.put("websiteName", staging)
        body.put("orderId", order.paytmOrderId)

        body.put("callbackUrl", order.callbackurl)

        val txnAmount = JSONObject()
        txnAmount.put("value", order.txnAmount)
        txnAmount.put("currency", order.txnCurrency)

        val userInfo = JSONObject()
        userInfo.put("custId", order.paytmcustomer)
        body.put("txnAmount", txnAmount)
        body.put("userInfo", userInfo)


        val checksum = PaytmChecksum.generateSignature(body.toString(), secret)

        val head = JSONObject()
        head.put("signature", checksum)

        paytmParams.put("body", body)
        paytmParams.put("head", head)

       val post_data = paytmParams.toString()


        val url : URL
        if (debug) {url = URL("https://securegw-stage.paytm.in/theia/api/v1/initiateTransaction?mid="+ mid +"&orderId=" + order.paytmOrderId) }
        else {url = URL("https://securegw.paytm.in/theia/api/v1/initiateTransaction?mid="+ mid + "&orderId=" + order.paytmOrderId) }


       val apiClient = APIClient(context)


       val request = apiClient.create()
       request.set(Methods.POST,url.toString())
       request.AddContentType(ContentType.APPLICATION_JSON)
       request.StringBody(post_data)

       request.send({ Success(it,callback,errors) }, { APIError(it,errors) })




    }

    fun Success(response : RzapiResponse, callback: initiate_transaction_callback , errors: PaytmResponses)
    {
        val jsonObject = JSONObject(response.body)

        val respObj : initiate_transaction_response_object

        try {
             respObj = initiate_transaction_response_object(jsonObject)
        }
        catch (e : org.json.JSONException) { errors.JSONException(e)
            return
        }



        respObj.CallBack(callback,errors)








    }

    fun APIError(response: RzapiError,callback: PaytmResponses)
    {
        callback.ConnectionError(response)
    }









}