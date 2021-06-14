package company.tap.checkout.internal.api.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.math.BigDecimal

/**
 * Created by AhlaamK on 6/14/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
data class Shipping(
    @SerializedName("name") @Expose
    private var name: String? = null,

    @SerializedName("description")
    @Expose
    private val description: String? = null,

    @SerializedName("amount")
    @Expose
    private var amount: BigDecimal? = null
) : Serializable
