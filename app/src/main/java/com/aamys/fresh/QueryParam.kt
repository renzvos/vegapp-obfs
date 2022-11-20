package com.renzvos.rzapi

class QueryParam (key : String , pvalue : String){
    val key = key
    val pvalue = pvalue

    companion object{
        fun toHashMap(params : ArrayList<QueryParam> ): HashMap<String,String>
        {
            val hashMap = HashMap<String,String>()
            for(param in params)
            {
               hashMap.put(param.key,param.pvalue)
            }
            return hashMap
        }
    }

}