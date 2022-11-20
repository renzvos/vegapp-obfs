package com.renzvos.ecommerceorderslist

import android.telephony.mbms.StreamingServiceInfo
import android.util.Log
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class OrderDetails(val orderid : String ,
                        val displayOrderid : String,
                        val backgroudColor : String,
                        val Amount : String,
                        val PurchaseDisplayDateTime : String,
                        val Status: String,
                        val PurchaseTimestamp : String)

{
    fun GetDate(format : String): Date? {
        var date: Date?
        Log.i("RZ_Order Details", "GetDate: " + PurchaseTimestamp)
        val format = SimpleDateFormat(format)
        try {
            date = format.parse(PurchaseTimestamp)
        } catch (e: ParseException) {
            date = null
            e.printStackTrace()
        }
        return date
    }
}

