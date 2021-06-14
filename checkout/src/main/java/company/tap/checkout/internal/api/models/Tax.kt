package company.tap.checkout.internal.api.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by AhlaamK on 6/14/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
data class Tax(
    @SerializedName("name") @Expose
    private var name: String? = null,

    @SerializedName("description")
    @Expose
    private val description: String? = null,

    @SerializedName("amount")
    @Expose
    private var amount: AmountModificator? = null
) : Serializable
