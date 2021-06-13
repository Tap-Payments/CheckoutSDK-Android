package company.tap.checkout.internal.api.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import company.tap.checkout.internal.api.enums.AmountModificatorType
import java.io.Serializable
import java.math.BigDecimal

/**
 * Created by AhlaamK on 6/13/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/

/**
 * The type Amount modificator.
 */
class AmountModificator(type: AmountModificatorType, value: BigDecimal) :
    Serializable {
    @SerializedName("type")
    @Expose
    private val type: AmountModificatorType = type

    /**
     * Gets value.
     *
     * @return the value
     */
    @SerializedName("value")
    @Expose
    val value: BigDecimal = value

    /**
     * Gets type.
     *
     * @return the type
     */
    fun getType(): AmountModificatorType {
        return type
    }//why

    /**
     * Gets normalized value.
     *
     * @return the normalized value
     */
    val normalizedValue: BigDecimal
        get() = if (type !== AmountModificatorType.PERCENTAGE) {
            value
        } else value.multiply(BigDecimal.valueOf(0.01))
    //why

}
