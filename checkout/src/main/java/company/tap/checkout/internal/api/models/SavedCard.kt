package company.tap.checkout.internal.api.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import company.tap.checkout.internal.interfaces.CurrenciesSupport
import company.tap.commonmodels.CardScheme
import company.tap.tapcardvalidator_android.CardBrand
import java.util.*

/**
 * Created by AhlaamK on 6/14/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
data class SavedCard(
    @SerializedName("id") @Expose
    private var id: String,

    @SerializedName("object")
    @Expose
    private val `object`: String,

    @SerializedName("first_six")
    @Expose
    private val firstSix: String,

    @SerializedName("last_four")
    @Expose
    private val lastFour: String,

    @SerializedName("brand")
    @Expose
    private val brand: CardBrand,

    @SerializedName("payment_method_id")
    @Expose
    private val paymentOptionIdentifier: String,

    @SerializedName("expiry")
    @Expose
    private val expiry: ExpirationDate?,

    @SerializedName("name")
    @Expose
    private val cardholderName: String,

    @SerializedName("currency")
    @Expose
    private val currency: String,

    @SerializedName("scheme")
    @Expose
    private val scheme: CardScheme,

    @SerializedName("supported_currencies")
    @Expose
    private val supportedCurrencies: ArrayList<String>,

    @SerializedName("order_by")
    @Expose
    private val orderBy: Int = 0,

    @SerializedName("image")
    private val image: String,

    @SerializedName("fingerprint")
    private val fingerprint: String,

// added for mapping Expiry month and Date in case of get list card API

    @SerializedName("exp_month")
    @Expose
    private val exp_month: String,

    @SerializedName("exp_year")
    @Expose
    private val exp_year: String,


    @SerializedName("funding")
    @Expose
    private val funding: String,

    @SerializedName("customer")
    @Expose
    private val customer: String? = null

): Comparable<SavedCard> ,CurrenciesSupport ,java.io.Serializable{
    override fun compareTo(other: SavedCard): Int {
        return orderBy - other.orderBy
    }

    override fun getSupportedCurrencies(): ArrayList<String>? {
        return supportedCurrencies
    }

}

