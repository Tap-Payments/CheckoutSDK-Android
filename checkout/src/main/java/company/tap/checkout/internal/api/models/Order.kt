package company.tap.checkout.internal.api.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by AhlaamK on 6/14/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
data class Order(
    @SerializedName("id") @Expose
     var id: String? = null,
    @SerializedName("reference")
@Expose
 val reference: String? = null,

@SerializedName("store_url")
@Expose
 val store_url: String? = null
) : Serializable
