package company.tap.checkout.internal.api.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import company.tap.checkout.internal.api.enums.PaymentType
import company.tap.checkout.internal.interfaces.CurrenciesSupport
import company.tap.tapcardvalidator_android.CardBrand
import java.io.Serializable
import java.util.*

/**
 * Created by AhlaamK on 6/14/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
data class PaymentOption(
    @SerializedName("id") @Expose
     var id: String? = null,

    @SerializedName("name")
    @Expose
     val brand: CardBrand? = null,

    @SerializedName("image")
    @Expose
     val image: String? = null,

    @SerializedName("payment_type")
    @Expose
     val paymentType: PaymentType? = null,

    @SerializedName("source_id")
    @Expose
     val sourceId: String? = null,

    @SerializedName("supported_card_brands")
    @Expose
     val supportedCardBrands: ArrayList<CardBrand>? = null,

    @SerializedName("extra_fees")
    @Expose
     var extraFees: ArrayList<ExtraFee>? = null,

    @SerializedName("supported_currencies")
    @Expose
    private val supportedCurrencies: ArrayList<String>,

    @SerializedName("order_by")
    @Expose

    val orderBy: Int = 0,

    @SerializedName("threeDS")
    @Expose
     val threeDS: String? = null,

    @SerializedName("asynchronous")
    @Expose
     val asynchronous: Boolean = false ,
    @SerializedName("cc_markup")
    @Expose
     val cc_markup: Int = 0
):Comparable<PaymentOption>, CurrenciesSupport, Serializable {
    override fun getSupportedCurrencies(): ArrayList<String>? {
       return supportedCurrencies
    }

    override fun compareTo(other: PaymentOption): Int {
        return orderBy - other.orderBy
    }

}
