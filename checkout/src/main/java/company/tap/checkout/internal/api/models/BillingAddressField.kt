package company.tap.checkout.internal.api.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by AhlaamK on 6/14/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
data class BillingAddressField(
    /**
     * The Name.
     */
    @SerializedName("name") @Expose
    var name: String,

    /**
     * The Required.
     */
    @SerializedName("required")
    @Expose
    var required: Boolean = false,

    /**
     * The Order by.
     */
    @SerializedName("order_by")
    @Expose
    var orderBy: Int = 0,

    /**
     * The Display order.
     */
    @SerializedName("display_order")
    @Expose
    var displayOrder: Int = 0
) : Serializable
