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
      var addressRequired: Boolean = false,

    @SerializedName("bank")
    @Expose
      val bank: String,

    @SerializedName("bank_logo")
    @Expose
      val bank_logo: String,

    @SerializedName("bin")
    @Expose
      val bin: String,

    @SerializedName("card_brand")
    @Expose
      val cardBrand: CardBrand,

    @SerializedName("card_type")
    @Expose
    val cardType: String,
    @SerializedName("card_category")
    @Expose
    val cardCategory: String,

    @SerializedName("card_scheme")
    @Expose
      val scheme: CardScheme,

    @SerializedName("website")
    @Expose
      val website: String,

    @SerializedName("phone")
    @Expose
      val phone: String,
    @SerializedName("api_version")
    @Expose
      val apiVersion: String,
    @SerializedName("issuer_id")
    @Expose
      val issuerID: String,

    @SerializedName("country")
    @Expose
      val country: String,

    @SerializedName("country_name")
    @Expose
    val countryName: String
) : Serializable
