package company.tap.checkout.internal.api.enums

import com.google.gson.annotations.SerializedName

/**
 * Created by AhlaamK on 6/13/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/

/**
 * The enum Source type.
 */
enum class SourceType {
    /**
     * Card present source type.
     */
    @SerializedName("CARD_PRESENT")
    CARD_PRESENT,

    /**
     * Card not present source type.
     */
    @SerializedName("CARD_NOT_PRESENT")
    CARD_NOT_PRESENT
}
