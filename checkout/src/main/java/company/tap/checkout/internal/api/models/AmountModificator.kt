package company.tap.checkout.internal.api.models

import androidx.annotation.Nullable
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
open class AmountModificator(
        @SerializedName("type")
    @Expose var amnttype: AmountModificatorType? = null,

        @SerializedName("value")
@Expose var bigvalue: BigDecimal? = null
):Serializable{





/**
 * Gets type.
 *
 * @return the type
 */
fun getType(): AmountModificatorType? {
    return amnttype
}

/**
 * Gets value.
 *
 * @return the value
 */
fun getValue(): BigDecimal? {
    return bigvalue
}

/**
 * Gets normalized value.
 *
 * @return the normalized value
 */
fun getNormalizedValue(): BigDecimal? {
    return if (this.amnttype !== AmountModificatorType.PERCENTAGE) {
        this.bigvalue
    } else this.bigvalue?.multiply(BigDecimal.valueOf(0.01))
    //why
}
    init {
        this.amnttype = amnttype
        this.bigvalue = bigvalue
    }
}