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
     var id: String? = null,

    @SerializedName("object")
    @Expose
     val `object`: String? = null,

    @SerializedName("card")
    @Expose
     val card: TokenizedCard? = null,

    @SerializedName("type")
    @Expose
     val type: TokenType? = null,

    @SerializedName("created")
    @Expose
     val created: Long = 0,

    @SerializedName("client_ip")
    @Expose
     val client_ip: String? = null,

    @SerializedName("livemode")
    @Expose
     val livemode: Boolean = false,

    @SerializedName("used")
    @Expose
     val used: Boolean = false,

    @SerializedName("currency")
    @Expose
     val currency: String? = null,

    @SerializedName("name")
    @Expose
     var name: String? = null
) : Serializable , BaseResponse
