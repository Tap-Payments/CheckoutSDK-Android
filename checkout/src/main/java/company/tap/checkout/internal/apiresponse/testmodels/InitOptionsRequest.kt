package company.tap.checkout.internal.apiresponse.testmodels

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import company.tap.cardbusinesskit.testmodels.Customer
import company.tap.cardbusinesskit.testmodels.Order

/**
 * Created by AhlaamK on 11/19/20.

Copyright (c) 2020    Tap Payments.
All rights reserved.
 **/
data class InitOptionsRequest constructor(
    @SerializedName("action") var action: String,
    @SerializedName("split_amount") var splitAmount: Boolean,
    @SerializedName("customer") var customer: Customer,
    @SerializedName("order") var order: Order
)