package com.renzvos.rzapi

import android.content.Context
import com.android.volley.toolbox.Volley

import com.android.volley.RequestQueue




class VolleySingleton(context : Context) {
    private val mContext: Context = context
    val RequestQueue: RequestQueue

    init {
        RequestQueue = Volley.newRequestQueue(mContext)
    }


    companion object {
        private var Instance: VolleySingleton? = null

        @JvmStatic
        @Synchronized
        fun getInstance(context: Context): VolleySingleton {
            if (Instance == null) {
                Instance = VolleySingleton(context)
            }
            return Instance as VolleySingleton
        }
    }

}