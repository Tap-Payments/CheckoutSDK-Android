package company.tap.checkout.internal.apiresponse.testmodels

import androidx.annotation.Nullable
import com.google.gson.annotations.SerializedName
import company.tap.tapnetworkkit.interfaces.TapRequestBodyBase

/**
 * Created by AhlaamK on 11/19/20.

Copyright (c) 2020    Tap Payments.
All rights reserved.
 **/
internal class  InitOptionsRequest(@SerializedName("action")  var action: String,
                                   @SerializedName("split_amount")  var splitAmount: Boolean = false,
                                   @SerializedName("customer")  var customer: Customer,
                                   @Nullable @SerializedName("order") var order: Order
)  : TapRequestBodyBase{


    init{
        this.action = action
        this.splitAmount = splitAmount
        this.customer = customer
        this.order = order
    }
}