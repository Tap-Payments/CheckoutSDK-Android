package company.tap.checkout.open.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import company.tap.checkout.internal.api.models.Response
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

    @SerializedName("status")
    @Expose
    var status: String? = null,

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
    var application: TopUpApplication? = null,

    @SerializedName("response")
    @Expose
    var response: Response? = null,

    @SerializedName("post")
    @Expose
    var post: TopupPost? = null,

    @SerializedName("metadata")
    @Expose
    var metadata: MetaData? = null
) : Serializable

data class TopchargeModel(
    @SerializedName("id") @Expose
    private var id: String? = null
) : Serializable

data class TopCustomerModel(
    @SerializedName("id") @Expose
    private var id: String? = null
) : Serializable

data class TopUpApplication(
    @SerializedName("amount") @Expose
    var amount: BigDecimal? = null,
    @SerializedName("currency") @Expose
    var currency: String? = null
) : Serializable

data class TopUpReference(
    @SerializedName("order") @Expose
    var order: String? = null,
    @SerializedName("transaction")
    @Expose
    val transaction: String? = null
) : Serializable

data class TopupPost(
    @SerializedName("url") @Expose
    val url: String? = null
) : Serializable

data class MetaData(
    @SerializedName("udf1") @Expose
    private var udf1: String? = null,
    @SerializedName("udf2")
    @Expose
    private var udf2: String? = null
) : Serializable


