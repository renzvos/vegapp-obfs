package com.renzvos.rzapi

fun interface Success {
    fun exc(response: RzapiResponse)
}

fun interface Error{
    fun exc(response: RzapiError)
}

