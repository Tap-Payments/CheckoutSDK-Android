package company.tap.checkout.internal.api.enums

import com.google.gson.annotations.SerializedName

/**
 * Created by AhlaamK on 6/13/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
/**
 * The enum Extra fees status.
 */
enum class ExtraFeesStatus {
    /**
     * No extra fees extra fees status.
     */
    @SerializedName("NO_EXTRA_FEES")
    NO_EXTRA_FEES,

    /**
     * Accept extra fees extra fees status.
     */
    @SerializedName("ACCEPT_EXTRA_FEES")
    ACCEPT_EXTRA_FEES,

    /**
     * Refuse extra fees extra fees status.
     */
    @SerializedName("REFUSE_EXTRA_FEES")
    REFUSE_EXTRA_FEES
}
