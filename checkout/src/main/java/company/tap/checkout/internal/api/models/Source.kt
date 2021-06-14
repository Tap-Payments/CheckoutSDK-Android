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
    private var id: String? = null,


    @SerializedName("object")
    @Expose
    private val `object`: String? = null,

    @SerializedName("type")
    @Expose
    private val type: SourceType? = null,


    @SerializedName("payment_type")
    @Expose
    private val paymentType: String? = null,


    @SerializedName("payment_method")
    @Expose
    private val paymentMethod: CardBrand? = null,

    @SerializedName("channel")
    @Expose
    private var channel: SourceChannel? = null
) : Serializable
