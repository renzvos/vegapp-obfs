package com.renzvos.rzapi

import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser

import com.android.volley.NetworkResponse
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset


open class CustomizedVolleyRequest(val request : RzapiRequest ,method: Int, url: String?, private val mListener: Response.Listener<RzapiResponse>, listener: Response.ErrorListener?) :
    Request<RzapiResponse>(method, url, listener)
{
    override fun deliverResponse(response: RzapiResponse){
        mListener.onResponse(response)
    }

    override fun parseNetworkResponse(response: NetworkResponse): Response<RzapiResponse> {
        val parsed: String
        parsed = try {
            val encode =  HttpHeaderParser.parseCharset(response.headers)
            val charset = Charset.forName(encode)
            String(response.data,charset)
        } catch (e: UnsupportedEncodingException) {
            String(response.data)
        }


        val rzResponse = RzapiResponse(response.headers,parsed,response.networkTimeMs,response.statusCode,request)
        return Response.success(rzResponse, HttpHeaderParser.parseCacheHeaders(response))
    }


}