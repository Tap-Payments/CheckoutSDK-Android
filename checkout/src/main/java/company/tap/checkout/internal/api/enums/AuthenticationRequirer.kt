package company.tap.checkout.internal.apiresponse.enums

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
    PROVIDER("PROVIDER"),
    /**
     * Merchant authentication requirer.
     */
    MERCHANT("MERCHANT"),
    /**
     * Cardholder authentication requirer.
     */
    CARDHOLDER("CARDHOLDER")

}