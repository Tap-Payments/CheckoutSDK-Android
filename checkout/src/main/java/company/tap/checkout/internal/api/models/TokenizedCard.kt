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
     var id: String? = null,

    @SerializedName("object")
    @Expose
     val `object`: String? = null,

    @SerializedName("address")
    @Expose
     val address: Address? = null,

    @SerializedName("funding")
    @Expose
     val funding: String? = null,

    @SerializedName("fingerprint")
    @Expose
     val fingerprint: String? = null,

    @SerializedName("brand")
     val brand: CardBrand? = null,


    @SerializedName("exp_month")
    @Expose
     val expirationMonth: Int = 0,

    @SerializedName("exp_year")
    @Expose
     val expirationYear: Int = 0,

    @SerializedName("last_four")
    @Expose
     val lastFour: String? = null,


    @SerializedName("first_six")
    @Expose
     val firstSix: String? = null,


    @SerializedName("name")
    @Expose
     var name: String? = null
) : Serializable
