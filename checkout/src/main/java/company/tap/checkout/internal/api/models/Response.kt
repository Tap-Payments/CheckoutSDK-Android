package company.tap.checkout.internal.api.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by AhlaamK on 6/13/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
data class Response(

    /**
     * The Code.
     */
    @SerializedName("code")
    val code: String,

    /**
     * The Message.
     */
    @SerializedName("message") @Expose
    val message: String
)


