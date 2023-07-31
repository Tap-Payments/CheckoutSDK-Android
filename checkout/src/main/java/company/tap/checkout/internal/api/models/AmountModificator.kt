package company.tap.checkout.internal.api.models

import com.google.gson.annotations.Expose
import company.tap.checkout.internal.api.enums.AmountModificatorType
import kotlinx.serialization.SerialName
import java.math.BigDecimal

/**
 * Created by AhlaamK on 6/13/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/

/**
 * The type Amount modificator.
 */
@kotlinx.serialization.Serializable
open class AmountModificator(
    @SerialName("type")
    @Expose var type: AmountModificatorType? = null,

    @SerialName("value")
    @Expose var value: Double? = null
) {


    /**
     * Gets value.
     *
     * @return the value
     */
    fun value(): Double? {
        return value
    }

    /**
     * Gets normalized value.
     *
     * @return the normalized value
     */
    fun getNormalizedValue(): BigDecimal? {
        return if (this.type !== AmountModificatorType.PERCENTAGE) {
            this.value?.toBigDecimal()
        } else this.value?.toBigDecimal()?.multiply(BigDecimal.valueOf(0.01))
        //why
    }

    init {
        this.type = type
        this.value = value
    }
}