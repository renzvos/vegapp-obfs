package com.renzvos.paymentgateway.paytm


import org.json.JSONObject
import java.lang.Exception

open class PaytmResponseObject (val resultCode  : String ,     val responseTimestamp : String)
{
    var clientID : String? = null
    var signature : String? = null
    var resultMessage : String? = null
    var authenticated : Boolean? = null
    var bankRetry : Boolean?=null
    var retry : Boolean? = null
    var extraParamsMap : JSONObject? =null
    var version : String? = null
    var resultStatus : String?=null


    constructor(jsonobj : JSONObject):this(jsonobj.getJSONObject("body").getJSONObject("resultInfo").getString("resultCode"),
        jsonobj.getJSONObject("head").getString("responseTimestamp"))
    {
        val headobj = jsonobj.getJSONObject("head")
        val bodyobj = jsonobj.getJSONObject("body")
        val resultinfo = bodyobj.getJSONObject("resultInfo")
        version = headobj.getString("version")
        resultStatus = resultinfo.getString("resultStatus")



        try{
            clientID = headobj.getString("clientId")

        } catch (e : Exception) { }
        try{
            signature = headobj.getString("signature")

        } catch (e : Exception) { }
        try{
            resultMessage = resultinfo.getString("resultMsg")

        } catch (e : Exception) { }
        try{
            authenticated = bodyobj.getBoolean("authenticated")

        } catch (e : Exception) { }
        try{
            bankRetry = resultinfo.getBoolean("bankRetry")

        } catch (e : Exception) { }
        try{
            retry = resultinfo.getBoolean("retry")

        } catch (e : Exception) { }

        try{
            extraParamsMap = resultinfo.getJSONObject("extraParamsMap")

        } catch (e : Exception) { }
    }


    fun CommonCallBack(callback : PaytmResponses):Boolean
    {
        when(resultCode)
        {

            "2004" ->{callback.SSOTokenInvalid(this)
            }
            "2005" ->{callback.ChecksumInvalid(this)
            }
            "2013" ->{callback.UnMatchedMid(this)
            }
            "2014" ->{callback.UnMatcedOrderId(this)
            }
            "2023" ->{callback.RepeatRequestInconsistent(this)
            }
            "00000900" ->{callback.SystemError(this)
            }

            "00" ->{callback.SystemError(this)
                }
            "141" ->{callback.UserNotCompletedTransaction(this)
                }
            "153" ->{callback.SystemError(this)
               }
            "163" ->{callback.WalletConsultFailed(this)
               }
            "196" ->{callback.AmountExceedsLimit(this)
                }
            "202" ->{callback.UserDoesNotHaveCredit_BankDeclined(this)
                }
            "205" ->{callback.TransactionDeclinedByBank(this)
                }
            "207" ->{callback.CardExplired(this)
               }
            "208" ->{callback.TransactionDeclinedByBank(this)
                }
            "209" ->{callback.CardInvalid(this)
               }
            "210" ->{callback.LostCardData(this)
                }
            "220" ->{callback.BankCommunicationError(this)
               }
            "222" ->{callback.UnMatchedAmount(this)
              }
            "229" ->{callback.SystemError(this)
              }
            "227" ->{callback.UnIdentifiedError(this)
              }
            "232" ->{callback.InvalidAccountDetails(this)
             }
            "283" ->{callback.MandatoryFieldMissing(this)
               }
            "294" ->{callback.UserNotCompletedTransaction(this)
              }
            "295" ->{callback.UnIdentifiedError(this)
               }
            "296" ->{callback.BankUnavailibleTryAnother(this)
              }
            "297" ->{callback.CancelAndRedirectTo3d(this)
              }
            "302" ->{callback.InvalidRequestType(this)
               }
            "308" ->{callback.InvalidAmount(this)
              }
            "309" ->{callback.InvalidOrderId(this)
             }
            "312" ->{callback.InvalidCardNo(this)
             }
            "314" ->{callback.InvalidMonth(this)
             }
            "315" ->{callback.InvalidYear(this)
              }
            "316" ->{callback.InvalidCVV(this)
              }
            "317" ->{callback.InvalidPaymentMode(this)
             }
            "318" ->{callback.InvalidCustID(this)
             }
            "319" ->{callback.InvalidIndustryID(this)
             }
            "325" ->{callback.DuplicateOrderID(this)
              }
            "330" ->{callback.ChecksumInvalid(this)
                }
            "337" ->{callback.UnIdentifiedError(this)
               }
            "372" ->{callback.RetryCountBreached(this)
               }
            "401" ->{callback.AbandonedTransaction(this)
              }
            "402" ->{callback.TransactionAbandonedfromCCAvenue(this)
               }
            "501" ->{callback.BankDeclined(this)
              }
            "504" ->{callback.MerchentError(this)
              }
            "506" ->{callback.MerchecntNotAbailible(this)
               }
            "509" ->{callback.MerchentError(this)
               }
            "810" ->{callback.ClosedPageAfterLoad(this)
              }
            "1001" ->{callback.RequestParameterNotValid(this)
              }
            "1006" ->{callback.SessionExplired(this)
              }
            "1007" ->{callback.MandatoryFieldMissing(this)
               }
            "1008" ->{callback.PipeCharectorNotAllowed(this)
               }
            "2271" ->{callback.UserNotCompletedTransaction(this)
                }
            "2272" ->{callback.UserNotCompletedTransaction(this)
               }
            "3102" ->{callback.CardInvalid(this)
                }
            "9999" ->{callback.UnIdentifiedError(this)
                }

            else -> return false

        }
        return true
    }
}
