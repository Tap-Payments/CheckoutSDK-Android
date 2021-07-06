package company.tap.checkout.internal.api.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import company.tap.checkout.internal.api.enums.SourceChannel
import company.tap.checkout.internal.api.enums.SourceType
import company.tap.tapcardvalidator_android.CardBrand
import java.io.Serializable

/**
 * Created by AhlaamK on 6/14/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
data class Source(
    @SerializedName("id") @Expose
     var id: String? = null,


    @SerializedName("object")
    @Expose
     val `object`: String? = null,

    @SerializedName("type")
    @Expose
     val type: SourceType? = null,


    @SerializedName("payment_type")
    @Expose
     val paymentType: String? = null,


    @SerializedName("payment_method")
    @Expose
     val paymentMethod: CardBrand? = null,

    @SerializedName("channel")
    @Expose
     var channel: SourceChannel? = null
) : Serializable
