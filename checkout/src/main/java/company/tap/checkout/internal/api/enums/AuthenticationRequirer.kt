package company.tap.checkout.internal.api.enums

import com.google.gson.annotations.SerializedName

/**
 * Created by AhlaamK on 6/13/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/

/**
 * The enum Authentication requirer.
 */
enum class AuthenticationRequirer(val authenticationRequirer: String) {
    /**
     * Provider authentication requirer.
     */
    @SerializedName("PROVIDER")
    PROVIDER("PROVIDER"),
    /**
     * Merchant authentication requirer.
     */
    @SerializedName("MERCHANT")
    MERCHANT("MERCHANT"),
    /**
     * Cardholder authentication requirer.
     */
    @SerializedName("CARDHOLDER")
    CARDHOLDER("CARDHOLDER")

}