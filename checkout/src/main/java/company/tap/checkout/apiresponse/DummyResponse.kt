package company.tap.checkout.apiresponse

/**
 * Created by AhlaamK on 8/31/20.

Copyright (c) 2020    Tap Payments.
All rights reserved.
 **/
data class DummyResponse (val status: String, val data: DataObj, val sdk_settings: SDKSettings, val live_mode:String,
                          val livemode :String, val permissions :List<String>, val verified_application :String,
                          val  encryption_key :String, val  session_token :String)

data class DataObj( val merchant: Merchant)

data class Merchant( val name:String,  val logo:String)

data class SDKSettings(val status_display_duration :Int, val otp_resend_interval :Int , val  otp_resend_attempts :Int)


