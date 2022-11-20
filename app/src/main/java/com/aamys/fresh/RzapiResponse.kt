package com.renzvos.rzapi

import android.util.Log

data class RzapiResponse (  val headers: Map<String, String>?,
                            val body: String ,
                            val networkTimeMs : Long,
                            val statusCode : Int,
                            val requestObject : RzapiRequest)
{
    init {
        if (requestObject.logSTATUS)
            Log.i(requestObject.TAG, " Response (SUCCESS) Recieved: ")
            if (requestObject.logHeaders)
                for (header in headers!!.entries)
                    Log.i(requestObject.TAG, " Header Key " + header.key  + " : " + header.value)
        if(requestObject.logContent)
        Log.i(requestObject.TAG, ": Response Content " + body)


    }


}