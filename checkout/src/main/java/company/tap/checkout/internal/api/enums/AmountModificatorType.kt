package company.tap.checkout.internal.api.enums

import com.google.gson.annotations.SerializedName

/**
 * Created by AhlaamK on 6/13/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
@kotlinx.serialization.Serializable
enum class AmountModificatorType(val amountModificatorType: String) {
    /**
     * Percentage amount modificator type.
     */
    @SerializedName("P")
    PERCENTAGE("P"),

    /**
     * Fixed amount modificator type.
     */
    @SerializedName("F")
    FIXED("F")
}