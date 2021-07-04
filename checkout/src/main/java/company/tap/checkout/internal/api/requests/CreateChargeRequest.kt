package company.tap.checkout.internal.api.requests

import androidx.annotation.RestrictTo
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal
import company.tap.checkout.internal.api.models.Merchant

import company.tap.checkout.internal.api.models.Order

import company.tap.checkout.internal.api.models.SourceRequest

import company.tap.checkout.internal.api.models.TrackingURL

import company.tap.checkout.open.models.TapCustomer

import company.tap.checkout.open.models.Destinations

import company.tap.checkout.open.models.Receipt

import company.tap.checkout.open.models.Reference

import company.tap.checkout.open.models.TopUp

import java.util.*

/**
 * Created by AhlaamK on 7/4/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
/**
 * The type Create charge request.
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
class CreateChargeRequest(
    merchant: Merchant?,
    amount: BigDecimal,
    currency: String?,
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
    destinations: Destinations?,
    topUp: TopUp?
) {
    @SerializedName("merchant")
    @Expose
    private val merchant: Merchant? = merchant

    @SerializedName("amount")
    @Expose
    private val amount: BigDecimal? = amount

    @SerializedName("currency")
    @Expose
    private val currency: String? = currency

    @SerializedName("selected_amount")
    @Expose
    private val selectedAmount: BigDecimal = selectedAmount

    @SerializedName("selected_currency")
    @Expose
    private val selectedCurrency: String = selectedCurrency

    @SerializedName("customer")
    @Expose
    private val customer: TapCustomer = customer

    @SerializedName("fee")
    @Expose
    private val fee: BigDecimal = fee

    @SerializedName("order")
    @Expose
    private val order: Order = order

    @SerializedName("redirect")
    @Expose
    private val redirect: TrackingURL = redirect

    @SerializedName("post")
    @Expose
    private val post: TrackingURL? = post

    @SerializedName("source")
    @Expose
    private val source: SourceRequest = source

    @SerializedName("description")
    @Expose
    private val description: String? = description

    @SerializedName("metadata")
    @Expose
    private val metadata: HashMap<String, String>? = metadata

    @SerializedName("reference")
    @Expose
    private val reference: Reference? = reference

    @SerializedName("save_card")
    @Expose
    private val saveCard: Boolean = saveCard

    @SerializedName("statement_descriptor")
    @Expose
    private val statementDescriptor: String? = statementDescriptor

    @SerializedName("threeDSecure")
    @Expose
    private var threeDSecure: Boolean? = true

    @SerializedName("receipt")
    @Expose
    private val receipt: Receipt?

    @SerializedName("destinations")
    @Expose
    private val destinations: Destinations?

    @SerializedName("topup")
    @Expose
    private val topup: TopUp?

    /**
     * Instantiates a new Create charge request.
     * @param merchant            the merchant
     * @param amount              the amount
     * @param currency            the currency
     * @param customer            the customer
     * @param fee                 the fee
     * @param order               the order
     * @param redirect            the redirect
     * @param post                the post
     * @param source              the source
     * @param description         the description
     * @param metadata            the metadata
     * @param reference           the reference
     * @param saveCard            the save card
     * @param statementDescriptor the statement descriptor
     * @param threeDSecure        the three d secure
     * @param receipt             the receipt
     * @param destinations        the destinations
     * @param topUp               the topUp
     */
    init {
        this.threeDSecure = threeDSecure
        this.receipt = receipt
        this.destinations = destinations
        topup = topUp
    }
}
