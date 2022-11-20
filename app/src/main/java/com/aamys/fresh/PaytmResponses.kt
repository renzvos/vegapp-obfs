package com.renzvos.paymentgateway.paytm

import com.renzvos.rzapi.RzapiError
import org.json.JSONException

interface PaytmResponses {

   fun SSOTokenInvalid(response : PaytmResponseObject)
    fun ChecksumInvalid(response : PaytmResponseObject)
    fun UnMatchedMid(response : PaytmResponseObject)
    fun UnMatcedOrderId(response : PaytmResponseObject)
    fun RepeatRequestInconsistent(response : PaytmResponseObject)
    fun SystemError(response : PaytmResponseObject)
    fun ConnectionError(error : RzapiError)
    fun JSONException(error : JSONException)

    fun PipeCharectorNotAllowed(response: PaytmResponseObject)
    fun SessionExplired(response: PaytmResponseObject)
    fun UserNotCompletedTransaction(response : PaytmResponseObject)
    fun WalletConsultFailed(response : PaytmResponseObject)
    fun AmountExceedsLimit(response : PaytmResponseObject)
    fun UserDoesNotHaveCredit_BankDeclined(response : PaytmResponseObject)
    fun TransactionDeclinedByBank(response : PaytmResponseObject)
    fun CardExplired(response : PaytmResponseObject)
    fun CardInvalid(response : PaytmResponseObject)
    fun LostCardData(response : PaytmResponseObject)
    fun BankCommunicationError(response : PaytmResponseObject)
    fun UnMatchedAmount(response : PaytmResponseObject)
    fun ThreeDSecureVerificationFailed(response : PaytmResponseObject)
    fun InvalidAccountDetails(response : PaytmResponseObject)
    fun MandatoryFieldMissing(response : PaytmResponseObject)
    fun BankUnavailibleTryAnother(response : PaytmResponseObject)
    fun CancelAndRedirectTo3d(response : PaytmResponseObject)
    fun InvalidRequestType(response : PaytmResponseObject)
    fun InvalidAmount(response : PaytmResponseObject)
    fun InvalidOrderId(response : PaytmResponseObject)
    fun InvalidCardNo(response : PaytmResponseObject)
    fun InvalidMonth(response : PaytmResponseObject)
    fun InvalidYear(response : PaytmResponseObject)
    fun InvalidCVV(response : PaytmResponseObject)
    fun InvalidPaymentMode(response : PaytmResponseObject)
    fun InvalidCustID(response : PaytmResponseObject)
    fun InvalidIndustryID(response : PaytmResponseObject)
    fun DuplicateOrderID(response : PaytmResponseObject)
    fun RetryCountBreached(response : PaytmResponseObject)
    fun AbandonedTransaction(response : PaytmResponseObject)
    fun TransactionAbandonedfromCCAvenue(response : PaytmResponseObject)
    fun BankDeclined(response : PaytmResponseObject)
    fun MerchentError(response : PaytmResponseObject)
    fun MerchecntNotAbailible(response : PaytmResponseObject)
    fun ClosedPageAfterLoad(response : PaytmResponseObject)
    fun RequestParameterNotValid(response : PaytmResponseObject)
    fun UnIdentifiedError(response : PaytmResponseObject)


}