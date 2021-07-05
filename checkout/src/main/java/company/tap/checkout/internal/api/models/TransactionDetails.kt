package company.tap.checkout.internal.api.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by AhlaamK on 6/14/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
data class TransactionDetails(
    @SerializedName("created") @Expose
    private var created: Long = 0,

    @SerializedName("timezone")
    @Expose
    private val timezone: String,

    @SerializedName("authorization_id")
    @Expose
    private val authorizationID: String? = null,

    @SerializedName("url")
    @Expose
     val url: String? = null,

    @SerializedName("order")
    @Expose
    private val order: Order? = null,

    @SerializedName("expiry")
    @Expose
    private val expiry: Expiry? = null,

    @SerializedName("asynchronous")
    @Expose
    private val asynchronous: Boolean = false
) : Serializable
