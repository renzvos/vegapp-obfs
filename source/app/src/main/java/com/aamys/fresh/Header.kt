package com.renzvos.rzapi

class Header (key : String , hvalue : String) {
    val key = key
    val hvalue = hvalue

    companion object{
        fun toHashMap(params : ArrayList<Header> ):MutableMap<String, String>
        {
            val hashMap : MutableMap<String, String> = HashMap()
            for(param in params)
            {
                hashMap.put(param.key,param.hvalue)
            }
            return hashMap
        }
    }



}