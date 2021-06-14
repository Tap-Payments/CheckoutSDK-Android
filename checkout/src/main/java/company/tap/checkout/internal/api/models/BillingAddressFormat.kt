package company.tap.checkout.internal.api.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import company.tap.checkout.internal.api.enums.AddressFormat
import java.io.Serializable
import java.util.*

/**
 * Created by AhlaamK on 6/14/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
data class BillingAddressFormat(
    /**
     * The Name.
     */
    @SerializedName("name") @Expose
    var name: AddressFormat,

    /**
     * The Fields.
     */
    @SerializedName("fields")
    @Expose
    var fields: ArrayList<BillingAddressField>? = null
) : Serializable
