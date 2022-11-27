package com.aamys.fresh

interface initiate_transaction_callback  {
    fun Success(txnToken: String, response : initiate_transaction_response_object)
    fun SuccessIdempotent(txnToken : String , response : initiate_transaction_response_object)
    fun InvalidPromo(response : initiate_transaction_response_object)
    fun PromoAmountHigh(response : initiate_transaction_response_object)
    fun TxnAmountInvalid(response : initiate_transaction_response_object)

}