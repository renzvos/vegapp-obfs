package com.renzvos.rzapi

import android.content.Context

class APIClient (context: Context, baseurl : String){
    val context = context
    val baseurl = baseurl
    var logSTATUS = false
    var logFullURL = false
    var logAppendURL = false
    var logContent = false
    var logHeaders = false

    constructor(context: Context):this(context,"")
    {
    }

    fun create():RzapiRequest
    {
        return RzapiRequest(this)
    }

    fun LogAll()
    {
         logSTATUS = true
         logFullURL = true
         logAppendURL = true
         logContent = true
         logHeaders = true
    }
}