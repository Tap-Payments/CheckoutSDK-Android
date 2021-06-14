package company.tap.checkout.internal.api.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by AhlaamK on 6/14/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
data class Receipt(
    @SerializedName("id") @Expose
    private var id: String? = null,

    @SerializedName("email")
    @Expose
    private val email: Boolean = false,

    @SerializedName("sms")
    @Expose
    private val sms: Boolean = false
) : Serializable
