package company.tap.checkout.open.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import company.tap.checkout.internal.api.models.AmountModificator

/**
 * Created by AhlaamK on 6/13/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/

/**
 * The type Tax. ad
 */
data class Tax(
    @SerializedName("name") var name: String,
    @SerializedName("description") val description: String,
    @SerializedName("amount") val amount: AmountModificator
)
