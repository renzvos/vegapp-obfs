package com.renzvos.paymentgateway.paytm

data class PaytmOrderData(
    val paytmOrderId : String,
    val txnAmount : String,
    val txnCurrency : String,
    val paytmcustomer : String,
    val callbackurl : String
)
{
    constructor(     paytmOrderId : String,
                     txnAmount : String,
                     txnCurrency : String,
                     paytmcustomer : String): this(paytmOrderId, txnAmount, txnCurrency, paytmcustomer,"test")


}
