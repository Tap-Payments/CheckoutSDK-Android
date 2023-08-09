package company.tap.checkout.internal.api.requests

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import company.tap.checkout.open.models.AuthorizeAction
import company.tap.checkout.open.models.TapCustomer

import company.tap.checkout.open.models.Destinations
import company.tap.checkout.open.models.Receipt
import company.tap.checkout.open.models.Reference
import company.tap.checkout.open.models.TopUp
import java.math.BigDecimal
import company.tap.checkout.open.models.Merchant
import company.tap.checkout.internal.api.models.Order
import company.tap.checkout.internal.api.models.SourceRequest
import company.tap.checkout.internal.api.models.TrackingURL
import java.util.*
import kotlin.collections.List

/**
 * Created by AhlaamK on 7/26/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
class CreateAuthorizeRequest(
    merchant: Merchant?,
    amount: BigDecimal,
    currency: String,
    selectedAmount: BigDecimal,
    selectedCurrency: String,
    customer: TapCustomer,
    fee: BigDecimal,
    order: Order,
    redirect: TrackingURL,
    post: TrackingURL?,
    source: SourceRequest,
    description: String?,
    metadata: HashMap<String, String>?,
    reference: Reference?,
    saveCard: Boolean,
    statementDescriptor: String?,
    threeDSecure: Boolean,
    receipt: Receipt?,
    authorizeAction: AuthorizeAction,
    destinations: List<Destinations>?,
    topUp: TopUp?
) : CreateChargeRequest(
    merchant,
    amount,
    currency,
    selectedAmount,
    selectedCurrency,
    customer,
    fee,
    order,
    redirect,
    post,
    source,
    description,
    metadata,
    reference,
    saveCard,
    statementDescriptor,
    threeDSecure,
    receipt,
    destinations,
    topUp
) {
    @SerializedName("auto")
    @Expose
    private val authorizeAction: AuthorizeAction = authorizeAction

}