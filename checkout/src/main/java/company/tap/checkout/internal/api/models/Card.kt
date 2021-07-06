package company.tap.checkout.internal.api.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by AhlaamK on 6/14/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
data class Card(
    @SerializedName("id") @Expose
     var id: String? = null,

    @SerializedName("object")
    @Expose
     val `object`: String? = null,

    @SerializedName("first_six")
    @Expose
     val firstSix: String? = null,

    @SerializedName("last_four")
    @Expose
     val lastFour: String? = null,

    @SerializedName("exp_month")
    @Expose
     val exp_month: String? = null,

    @SerializedName("exp_year")
    @Expose
     val exp_year: String? = null,

    @SerializedName("brand")
    @Expose
     val brand: String? = null,

    @SerializedName("name")
    @Expose
     val name: String? = null,

    @SerializedName("bin")
    @Expose
     val bin: String? = null,

    @SerializedName("expiry")
    @Expose
     var expiry: ExpirationDate? = null
) : Serializable
