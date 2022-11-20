package com.renzvos.rzapi

import android.net.Uri
import android.util.Log
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.VolleyError

class RzapiRequest (APIClient : APIClient) {
    private val context = APIClient.context
     val TAG = "RZAPI - "
    private val client = APIClient
    private var queryParams = ArrayList<QueryParam>()
    private var requesttype = RzapiRequest.NoneBody

    private var method = Methods.GET
    var lefturl = ""
    var url = client.baseurl + lefturl
    var Headers = ArrayList<Header>();
    var Body = "";
    var logSTATUS = client.logSTATUS
    var logFullURL = client.logFullURL
    var logAppendURL = client.logAppendURL
    var logContent = client.logContent
    var logHeaders = client.logHeaders

    fun set( method : Int, lefturl : String )
    {
        this.method = method
        this.lefturl = lefturl
        this.url = client.baseurl + lefturl
    }

    fun send(success: Success, error: Error) {
        RequestLog()
        val queue = VolleySingleton.getInstance(context).RequestQueue
        val method = Methods.ConvertVolleyMethod(method)
        val volleyrequest : CustomizedVolleyRequest = object : CustomizedVolleyRequest(this,method,url,Response.Listener
        { success.exc(it) }, Response.ErrorListener { error.exc(MakeError(it))  })

            {
                /*
            override fun getParams(): Map<String, String> {
                return QueryParam.toHashMap(queryParams)
            }
*/
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {

                return Header.toHashMap(Headers)
            }

            override fun getBody(): ByteArray? {
                return Body.toByteArray()
            }


        }

        queue.add(volleyrequest)

    }



    fun AddContentType(contentType: String)
    {
        Headers.add(Header("Content-Type",contentType))
    }

    fun URLparams(params : ArrayList<QueryParam>)
    {
        var uri = Uri.parse(url)
        for(param in params)
        {
            uri = uri.buildUpon().appendQueryParameter(param.key,param.pvalue).build()
        }
        url = uri.toString()
    }

    fun StringBody(body : String)
    {
        this.Body = body
    }






    fun RequestLog()
    {
        if(logSTATUS)Log.i(TAG, "Sending API - ")
        if(logFullURL) Log.i(TAG, "URL : " + url)
        if(logAppendURL) Log.i(TAG, "Appended Path : " + lefturl)
        if(logHeaders)
            for (header in Headers)
                Log.i(TAG, "Header: " + header.key + "  :  " + header.hvalue)

        if(logContent) Log.i(TAG, "Request Content : " + Body)
    }

    private fun MakeError(verror: VolleyError):RzapiError
    {
        val error = RzapiError(verror.networkResponse.headers,verror.networkResponse.data.toString(),verror.networkTimeMs,verror.networkResponse.statusCode,this)
        return error
    }



    companion object{
    val NoneBody = 1
    val FormData = 2
    val TextData = 3
}





}