package company.tap.checkout.internal.api.enums

import com.google.gson.annotations.SerializedName

/**
 * Created by AhlaamK on 6/13/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/


/**
 * The enum Authorize action status.
 */
enum class AuthorizeActionStatus {
    /**
     * Pending authorize action status.
     */
    @SerializedName("PENDING")
    PENDING,

    /**
     * Scheduled authorize action status.
     */
    @SerializedName("SCHEDULED")
    SCHEDULED,

    /**
     * Captured authorize action status.
     */
    @SerializedName("CAPTURED")
    CAPTURED,

    /**
     * Failed authorize action status.
     */
    @SerializedName("FAILED")
    FAILED,

    /**
     * Declined authorize action status.
     */
    @SerializedName("DECLINED")
    DECLINED,

    /**
     * Void authorize action status.
     */
    @SerializedName("VOID")
    VOID
}
