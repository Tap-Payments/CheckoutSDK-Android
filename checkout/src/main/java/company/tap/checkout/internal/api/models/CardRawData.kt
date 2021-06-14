package company.tap.checkout.internal.api.models

import androidx.annotation.RestrictTo
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by AhlaamK on 6/14/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
@RestrictTo(RestrictTo.Scope.LIBRARY)
data class CardRawData(
    @SerializedName("number") @Expose
    private var number: String,

    @SerializedName("exp_month")
    @Expose
    private val exp_month: String,

    @SerializedName("exp_year")
    @Expose
    private val exp_year: String,

    @SerializedName("cvc")
    @Expose
    private val cvc: String,

    @SerializedName("name")
    @Expose
    private val name: String

) : Serializable
