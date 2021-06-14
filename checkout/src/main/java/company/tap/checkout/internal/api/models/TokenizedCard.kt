package company.tap.checkout.internal.api.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import company.tap.tapcardvalidator_android.CardBrand
import java.io.Serializable

/**
 * Created by AhlaamK on 6/14/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
data class TokenizedCard(
    @SerializedName("id") @Expose
    private var id: String? = null,

    @SerializedName("object")
    @Expose
    private val `object`: String? = null,

    @SerializedName("address")
    @Expose
    private val address: Address? = null,

    @SerializedName("funding")
    @Expose
    private val funding: String? = null,

    @SerializedName("fingerprint")
    @Expose
    private val fingerprint: String? = null,

    @SerializedName("brand")
    private val brand: CardBrand? = null,


    @SerializedName("exp_month")
    @Expose
    private val expirationMonth: Int = 0,

    @SerializedName("exp_year")
    @Expose
    private val expirationYear: Int = 0,

    @SerializedName("last_four")
    @Expose
    private val lastFour: String? = null,


    @SerializedName("first_six")
    @Expose
    private val firstSix: String? = null,


    @SerializedName("name")
    @Expose
    private var name: String? = null
) : Serializable
