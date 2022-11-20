package com.renzvos.rzapi

import android.util.Log

class RzapiError ( val headers: Map<String, String>?,
                  val body: String ,
                  val networkTimeMs : Long,
                  val statusCode : Int,
                  val requestObject : RzapiRequest){
    init {
        if (requestObject.logContent)
            Log.i(requestObject.TAG, " Response (ERROR) Recieved: " + statusCode)
        if (requestObject.logHeaders)
            for (header in headers!!.entries)
                Log.i(requestObject.TAG, " Header Key " + header.key  + " : " + header.value)
        Log.i(requestObject.TAG, ": Response Content " + body)


    }


}