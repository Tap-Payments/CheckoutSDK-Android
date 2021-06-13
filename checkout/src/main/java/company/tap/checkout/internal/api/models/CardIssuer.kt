package company.tap.checkout.internal.api.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by AhlaamK on 6/13/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
data class CardIssuer(
    @SerializedName("id") @Expose
    private var id: String,

    @SerializedName("name")
    @Expose
    private val name: String,

    @SerializedName("country")
    @Expose
    private val country: String
)
