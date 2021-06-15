package company.tap.checkout.internal.api.enums

import com.google.gson.annotations.SerializedName

/**
 * Created by AhlaamK on 6/13/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/

/**
 * The enum Transaction mode.
 */
enum class TransactionMode {
    /**
     * Purchase transaction mode.
     */
    @SerializedName("PURCHASE")
    PURCHASE,

    /**
     * Authorize capture transaction mode.
     */
    @SerializedName("AUTHORIZE_CAPTURE")
    AUTHORIZE_CAPTURE
}