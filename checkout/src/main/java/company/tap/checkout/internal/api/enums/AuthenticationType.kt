package company.tap.checkout.internal.api.enums

import com.google.gson.annotations.SerializedName

/**
 * Created by AhlaamK on 6/13/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/

/**
 * The enum Authentication type.
 */
enum class AuthenticationType {
    /**
     * Otp authentication type.
     */
    @SerializedName("OTP")
    OTP,

    /**
     * Biometrics authentication type.
     */
    @SerializedName("BIOMETRICS")
    BIOMETRICS
}