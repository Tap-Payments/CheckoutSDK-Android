package company.tap.checkout.internal.api.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by AhlaamK on 6/14/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
data class Expiry(
    @SerializedName("period") @Expose
     var period: Double = 0.0,

    @SerializedName("type")
    @Expose
     var type: String? = null
) : Serializable
