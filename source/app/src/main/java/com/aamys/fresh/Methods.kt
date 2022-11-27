package com.renzvos.rzapi

import com.android.volley.Request

class Methods {

    companion object {

        val GET = 1
        val POST = 2
        val DELETE = 3
        val HEAD = 4
        val PUT = 5
        val OPTIONS = 6
        val PATCH = 7
        val TRACE = 8



        fun ConvertVolleyMethod(method: Int): Int {
            if (method == GET)
                return Request.Method.GET
            else if (method == POST)
                return Request.Method.POST
            else if (method == DELETE)
                return Request.Method.DELETE
            else if (method == HEAD)
                return Request.Method.HEAD
            else if (method == PUT)
                return Request.Method.PUT
            else if (method == OPTIONS)
                return Request.Method.OPTIONS
            else if (method == PATCH)
                return Request.Method.PATCH
            else if (method == TRACE)
                return Request.Method.TRACE

            return -1
        }
    }



}