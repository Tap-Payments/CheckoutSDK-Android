package company.tap.checkout.internal.api.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by AhlaamK on 6/14/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
data class Reference(
    @SerializedName("acquirer") @Expose
    private var acquirer: String? = null,

    @SerializedName("gateway")
    @Expose
    private val gateway: String? = null,

    @SerializedName("payment")
    @Expose
    private val payment: String? = null,

    @SerializedName("track")
    @Expose
    private val track: String? = null,

    @SerializedName("transaction")
    @Expose
    private val transaction: String? = null,

    @SerializedName("order")
    @Expose
    private val order: String? = null,

        @SerializedName("gosell_id")
    @Expose
    private val gosellId: String? = null
): Serializable
