package company.tap.checkout.open.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.math.BigDecimal

/**
 * Created by AhlaamK on 7/4/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
data class TopUp(
    @SerializedName("id") @Expose
    var Id: String? = null,
    @SerializedName("wallet_id")
    @Expose
    var walletId: String? = null,

    @SerializedName("created")
    @Expose
    var created: Long? = null,

    @SerializedName("amount")
    @Expose
    var amount: BigDecimal? = null,

    @SerializedName("currency")
    @Expose
    var currency: String? = null,

    @SerializedName("charge")
    @Expose
    var charge: TopchargeModel? = null,

    @SerializedName("customer")
    @Expose
    var customer: TopCustomerModel? = null,

    @SerializedName("reference")
    @Expose
    var topUpReference: TopUpReference? = null,

    @SerializedName("application")
    @Expose
    var application: TopUpApplication? = null
) : Serializable

data class TopchargeModel(
    @SerializedName("id") @Expose
    private var id: String? = null
)

data class TopCustomerModel(
    @SerializedName("id") @Expose
    private var id: String? = null
)

data class TopUpApplication(
    @SerializedName("amount") @Expose
     var amount: BigDecimal? = null,
    @SerializedName("currency") @Expose
     var currency: String? = null
)

data class TopUpReference(
    @SerializedName("order") @Expose
     var order: String? = null,
    @SerializedName("transaction")
    @Expose
     val transaction: String? = null
)
