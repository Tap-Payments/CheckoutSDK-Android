package company.tap.checkout.internal.api.enums

import com.google.gson.annotations.SerializedName

/**
 * Created by AhlaamK on 6/13/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/

/**
 * The enum Token type.
 */
enum class TokenType {
    /**
     * Card token type.
     */
    @SerializedName("CARD")
    CARD,

    /**
     * Saved card token type.
     */
    @SerializedName("SAVED_CARD")
    SAVED_CARD
}
