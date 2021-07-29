package company.tap.checkout.internal.api.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by AhlaamK on 6/13/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
data class CardIssuer(
    @SerializedName("id") @Expose
     var id: String,

    @SerializedName("name")
    @Expose
     val name: String,

    @SerializedName("country")
    @Expose
     val country: String
):Serializable
