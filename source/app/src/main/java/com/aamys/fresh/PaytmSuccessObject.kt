package com.renzvos.paymentgateway.paytm

import org.json.JSONObject


class PaytmSuccessObject(jsonObject: JSONObject)
 {
     val orderid : String
     val mid : String
     val txnAmount : String
     val currency : String
     val txnStatus : String
     val txnCode : String
     val txnMessage : String

     var txnId : String? = null
     var txnTimeStamp : String?=null


     init{

          orderid = jsonObject.getString("ORDERID")
          mid = jsonObject.getString("MID")
          currency = jsonObject.getString("CURRENCY")
          txnAmount = jsonObject.getString("TXNAMOUNT")
          txnCode = jsonObject.getString("RESPCODE")

          txnStatus = jsonObject.getString("STATUS")
          txnMessage = jsonObject.getString("RESPMSG")



         try {
             txnTimeStamp = jsonObject.getString("TXNDATE")
             txnId = jsonObject.getString("TXNID")
         }
         catch (e :Exception)
         {

         }
     }
 }

