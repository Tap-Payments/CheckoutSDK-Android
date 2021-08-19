package company.tap.checkout.internal.api.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import company.tap.checkout.internal.api.enums.AuthenticationRequirer
import company.tap.checkout.internal.api.enums.AuthenticationStatus
import company.tap.checkout.internal.api.enums.AuthenticationType
import java.io.Serializable

/**
 * Created by AhlaamK on 6/14/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
data class Authenticate(
    @SerializedName("id") @Expose
    private var id: String,

    @SerializedName("object")
    @Expose
     val `object`: String,

    @SerializedName("type")
    @Expose
     val type: AuthenticationType,

    @SerializedName("by")
    @Expose
     val by: AuthenticationRequirer,

    @SerializedName("status")
    @Expose
     val status: AuthenticationStatus,

    @SerializedName("retry_attempt")
    @Expose
     val retryAttempt: Int = 0,

    @SerializedName("url")
    @Expose
    private val url: String,

    @SerializedName("created")
    @Expose
    private val created: Long = 0,

    @SerializedName("authenticated")
    @Expose
    private val authenticated: Long,

    @SerializedName("count")
    @Expose
    private val count: Int = 0,

    @SerializedName("value")
    @Expose
     var value: String? = null,
    @SerializedName("message")
    @Expose
     var message: String? = null
) : Serializable
