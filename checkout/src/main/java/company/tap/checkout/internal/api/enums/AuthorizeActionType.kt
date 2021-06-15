package company.tap.checkout.internal.api.enums

import com.google.gson.annotations.SerializedName

/**
 * Created by AhlaamK on 6/13/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/

/**
 * The enum Authorize action type.
 */
enum class AuthorizeActionType {
    /**
     * Capture authorize action type.
     */
    @SerializedName("CAPTURE")
    CAPTURE,

    /**
     * Void authorize action type.
     */
    @SerializedName("VOID")
    VOID
}
