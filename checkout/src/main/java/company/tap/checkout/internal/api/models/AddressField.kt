package company.tap.checkout.internal.api.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import company.tap.checkout.internal.api.enums.AddressFieldInputType
import java.io.Serializable

/**
 * Created by AhlaamK on 6/14/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
data class AddressField(
    @SerializedName("name") @Expose
    private var name: String,

    @SerializedName("type")
    @Expose
    private val type: AddressFieldInputType,

    @SerializedName("place_holder")
    @Expose
    private val placeholder: String? = null
) : Serializable
