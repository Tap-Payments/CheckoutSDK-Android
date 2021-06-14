package company.tap.checkout.internal.api.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import company.tap.checkout.internal.api.enums.TokenType
import company.tap.checkout.internal.interfaces.BaseResponse
import java.io.Serializable

/**
 * Created by AhlaamK on 6/14/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
data class Token(
    @SerializedName("id") @Expose
    private var id: String? = null,

    @SerializedName("object")
    @Expose
    private val `object`: String? = null,

    @SerializedName("card")
    @Expose
    private val card: TokenizedCard? = null,

    @SerializedName("type")
    @Expose
    private val type: TokenType? = null,

    @SerializedName("created")
    @Expose
    private val created: Long = 0,

    @SerializedName("client_ip")
    @Expose
    private val client_ip: String? = null,

    @SerializedName("livemode")
    @Expose
    private val livemode: Boolean = false,

    @SerializedName("used")
    @Expose
    private val used: Boolean = false,

    @SerializedName("currency")
    @Expose
    private val currency: String? = null,

    @SerializedName("name")
    @Expose
    private var name: String? = null
) : Serializable , BaseResponse
