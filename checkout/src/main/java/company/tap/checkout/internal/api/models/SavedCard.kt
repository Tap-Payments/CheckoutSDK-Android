package company.tap.checkout.internal.api.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
//import company.tap.cardinputwidget.Card
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
     var id: String,

    @SerializedName("object")
    @Expose
     val `object`: String,

    @SerializedName("first_six")
    @Expose
     val firstSix: String,

    @SerializedName("last_four")
    @Expose val lastFour: String,

    @SerializedName("brand")
    @Expose
     val brand: CardBrand,

    @SerializedName("payment_method_id")
    @Expose
     val paymentOptionIdentifier: String,

    @SerializedName("expiry")
    @Expose
     val expiry: ExpirationDate?,

    @SerializedName("name")
    @Expose
     val cardholderName: String,

    @SerializedName("currency")
    @Expose
     val currency: String,

    @SerializedName("scheme")
    @Expose
     val scheme: CardScheme,

    @SerializedName("supported_currencies")
    @Expose
    private val supportedCurrencies: ArrayList<String>,

    @SerializedName("order_by")
    @Expose
     val orderBy: Int = 0,

    @SerializedName("image") val image: String,

    @SerializedName("fingerprint")
    private val fingerprint: String,

// added for mapping Expiry month and Date in case of get list card API

    @SerializedName("exp_month")
    @Expose
     val exp_month: String,

    @SerializedName("exp_year")
    @Expose
     val exp_year: String,


    @SerializedName("funding")
    @Expose
     val funding: String,

    @SerializedName("customer")
    @Expose
     val customer: String? = null,

    @SerializedName("logos")
    @Expose
     val logos: Logos? = null,


): Comparable<SavedCard> ,CurrenciesSupport ,java.io.Serializable{
    override fun compareTo(other: SavedCard): Int {
        return orderBy - other.orderBy
    }

    override fun getSupportedCurrencies(): ArrayList<String>? {
        return supportedCurrencies
    }

}


