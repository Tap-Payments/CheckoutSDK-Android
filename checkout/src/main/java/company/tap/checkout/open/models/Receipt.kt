package company.tap.checkout.open.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by AhlaamK on 6/13/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/

data class Receipt(
    @SerializedName("id") @Expose
    var id: Boolean,

    @SerializedName("email")
    @Expose
    val email: Boolean = false,

    @SerializedName("sms")
    @Expose
    private val sms: Boolean = false
) : Serializable
