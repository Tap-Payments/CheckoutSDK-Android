package company.tap.checkout.internal.api.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import company.tap.checkout.internal.api.enums.CardScheme
import company.tap.tapcardvalidator_android.CardBrand
import java.io.Serializable

/**
 * Created by AhlaamK on 6/14/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
data class BINLookupResponse(
    @SerializedName("address_required") @Expose
    private var addressRequired: Boolean = false,

    @SerializedName("bank")
    @Expose
    private val bank: String,

    @SerializedName("bank_logo")
    @Expose
    private val bank_logo: String,

    @SerializedName("bin")
    @Expose
    private val bin: String,

    @SerializedName("card_brand")
    @Expose
    private val cardBrand: CardBrand,

    @SerializedName("card_scheme")
    @Expose
    private val scheme: CardScheme,

    @SerializedName("country")
    @Expose
    private val country: String
) : Serializable
