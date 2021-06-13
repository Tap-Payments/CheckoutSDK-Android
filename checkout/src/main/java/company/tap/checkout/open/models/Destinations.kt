package company.tap.checkout.open.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal
import java.util.*

/**
 * Created by AhlaamK on 6/13/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
data class Destinations(

    @SerializedName("amount") @Expose
    private var amount: BigDecimal? = null,

    @SerializedName("currency")
    @Expose
    private val currency: String? = null,

    @SerializedName("count")
    @Expose
    private val count: Int = 0,

    @SerializedName("destination")
    @Expose
    private val destination: ArrayList<Destination>? = null
)
