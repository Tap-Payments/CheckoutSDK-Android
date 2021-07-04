package company.tap.checkout.internal.api.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import company.tap.checkout.internal.api.enums.AmountModificatorType
import java.math.BigDecimal

/**
 * Created by AhlaamK on 6/14/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
data class ExtraFee(
        @SerializedName("currency") @Expose
    var currency: String? = null,
        @SerializedName("minimum_fee")
    @Expose
    val minimum_fee: Double? = null,

        @SerializedName("maximum_fee")
    @Expose
    var maximum_fee: Double? = null,
) : AmountModificator(null,null)
