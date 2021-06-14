package company.tap.checkout.internal.api.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by AhlaamK on 6/14/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
data class ExtraFee(
    @SerializedName("currency") @Expose
    private var currency: String? = null,
    @SerializedName("minimum_fee")
    @Expose
    private val minimum_fee: Double? = null,

    @SerializedName("maximum_fee")
    @Expose
    private var maximum_fee: Double? = null
) : Serializable
