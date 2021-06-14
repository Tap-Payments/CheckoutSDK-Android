package company.tap.checkout.internal.api.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by AhlaamK on 6/14/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
data class ExpirationDate(
    @SerializedName("month")
    @Expose
    private var month: String,

    @SerializedName("year")
    @Expose
    private val year: String
) : Serializable
