package company.tap.checkout.internal.api.enums

import com.google.gson.annotations.SerializedName

/**
 * Created by AhlaamK on 6/13/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/

/**
 * The enum Url status.
 */
enum class URLStatus {
    /**
     * Pending url status.
     */
    @SerializedName("PENDING")
    PENDING,

    /**
     * Success url status.
     */
    @SerializedName("SUCCESS")
    SUCCESS,

    /**
     * Failed url status.
     */
    @SerializedName("FAILED")
    FAILED
}