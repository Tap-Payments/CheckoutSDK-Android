package company.tap.checkout.internal.api.responses

import androidx.annotation.RestrictTo
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import company.tap.checkout.internal.api.enums.Permission
import java.io.Serializable
import java.util.*

/**
 * Created by AhlaamK on 6/15/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
@RestrictTo(RestrictTo.Scope.LIBRARY)
data class SDKSettings(
    @SerializedName("data")
    @Expose
    var data: Data
) : Serializable
/////////////////////////////////////////////////////////////////////
data class Data(
    @SerializedName("live_mode") @Expose
     var livemode: Boolean = false,

    @SerializedName("livemode") @Expose
    var livemode1: Boolean = false,

    @SerializedName("permissions")
    @Expose
     val permissions: ArrayList<Permission>,

    @SerializedName("encryption_key")
    @Expose
     val encryptionKey: String? = null,

    @SerializedName("merchant")
    @Expose
     val merchant: Merchant? = null,

    @SerializedName("sdk_settings")
    @Expose
     val internalSDKSettings: InternalSDKSettings? =
        null,

    @SerializedName("verified_application")
    @Expose
     val verified_application: Boolean = false,
    @SerializedName("session_token")
    @Expose
    val sessionToken:String?=null,
    @SerializedName("device_id")
    @Expose
    val  device_id:String?=null

)
/////////////////////////////////////////////////////////////////////
data class Merchant(
    @SerializedName("name") @Expose
     var name: String? = null,

    @SerializedName("logo")
    @Expose
     val logo: String? = null

)
/////////////////////////////////////////////////////////////////////
data class InternalSDKSettings(
    @SerializedName("status_display_duration") @Expose
     var statusDisplayDuration: Int = 0,

    @SerializedName("otp_resend_interval")
    @Expose
     val otpResendInterval: Double = 0.0,

    @SerializedName("otp_resend_attempts")
    @Expose
     val otpResendAttempts: Int = 0
)