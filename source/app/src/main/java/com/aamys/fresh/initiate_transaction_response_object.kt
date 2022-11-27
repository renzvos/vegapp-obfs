package com.aamys.fresh

import com.renzvos.paymentgateway.paytm.PaytmResponseObject
import com.renzvos.paymentgateway.paytm.PaytmResponses
import org.json.JSONObject
import java.lang.Exception

class initiate_transaction_response_object  (jsonObject: JSONObject): PaytmResponseObject(jsonObject)
{
    var txntoken : String?= null
    var isPromoCodeValid : String?= null

    init {
        val headobj = jsonObject.getJSONObject("head")
        val bodyobj = jsonObject.getJSONObject("body")
        val resultinfo = bodyobj.getJSONObject("resultInfo")

        try{
            txntoken = bodyobj.getString("txnToken")

        } catch (e : Exception) { }

        try{
            isPromoCodeValid = headobj.getString("isPromoCodeValid")
        } catch (e : Exception) { }

    }

    fun CallBack( callback : initiate_transaction_callback , responses : PaytmResponses)
    {
        when(resultCode)
        {
            "0000" ->
            {callback.Success(txntoken!!,this)
            }
            "0002" ->
            {callback.SuccessIdempotent(txntoken!!,this)
           }

            "1011" ->
            {callback.InvalidPromo(this)
           }
            "1012"->
            {callback.PromoAmountHigh(this)
           }
            "2007" ->
            {callback.TxnAmountInvalid(this)
           }

        }

        CommonCallBack(responses)
    }
}