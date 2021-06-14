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
    private var id: String? = null,

    @SerializedName("object")
    @Expose
    private val `object`: String? = null,

    @SerializedName("first_six")
    @Expose
    private val firstSix: String? = null,

    @SerializedName("last_four")
    @Expose
    private val lastFour: String? = null,

    @SerializedName("exp_month")
    @Expose
    private val exp_month: String? = null,

    @SerializedName("exp_year")
    @Expose
    private val exp_year: String? = null,

    @SerializedName("brand")
    @Expose
    private val brand: String? = null,

    @SerializedName("name")
    @Expose
    private val name: String? = null,

    @SerializedName("bin")
    @Expose
    private val bin: String? = null,

    @SerializedName("expiry")
    @Expose
    private var expiry: ExpirationDate? = null
) : Serializable
