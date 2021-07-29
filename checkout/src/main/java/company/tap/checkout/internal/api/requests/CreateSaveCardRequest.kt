package company.tap.checkout.internal.api.requests

import androidx.annotation.RestrictTo
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import company.tap.checkout.internal.api.models.CardIssuer
import company.tap.checkout.internal.api.models.Order
import company.tap.checkout.internal.api.models.SourceRequest
import company.tap.checkout.internal.api.models.TrackingURL
import company.tap.checkout.open.models.Receipt
import company.tap.checkout.open.models.Reference
import company.tap.checkout.open.models.TapCustomer
import java.util.*

/**
 * Created by AhlaamK on 7/28/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
@RestrictTo(RestrictTo.Scope.LIBRARY)
open class CreateSaveCardRequest(
    currency: String,
    customer: TapCustomer,
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
    card: Boolean,
    risk: Boolean,
    issuer: Boolean,
    promo: Boolean,
    loyalty: Boolean,
    cardIssuer: CardIssuer?
) {

    @SerializedName("currency")
    @Expose
    private var currency: String?= null

    @SerializedName("customer")
    @Expose
    private var customer: TapCustomer? = null

    @SerializedName("order")
    @Expose
    private var order: Order? = null

    @SerializedName("redirect")
    @Expose
    private var redirect: TrackingURL? = null

    @SerializedName("post")
    @Expose
    private var post: TrackingURL? = null

    @SerializedName("source")
    @Expose
    private var source: SourceRequest? = null

    @SerializedName("description")
    @Expose
    private var description: String? = null

    @SerializedName("metadata")
    @Expose
    private var metadata: HashMap<String, String>? = null

    @SerializedName("reference")
    @Expose
    private var reference: Reference? = null

    @SerializedName("save_card")
    @Expose
    private var saveCard = false

    @SerializedName("statement_descriptor")
    @Expose
    private var statementDescriptor: String? = null

    @SerializedName("threeDSecure")
    @Expose
    private var threeDSecure: Boolean? = true

    @SerializedName("receipt")
    @Expose
    private var receipt: Receipt? = null

    @SerializedName("card")
    @Expose
    private var card = false

    @SerializedName("promo")
    @Expose
    private var promo: Boolean? = true

    @SerializedName("loyalty")
    @Expose
    private var loyalty: Boolean? = true

    @SerializedName("risk")
    @Expose
    private var risk: Boolean? = true

    @SerializedName("issuer")
    @Expose
    private var issuer: Boolean? = true

    @SerializedName("card_issuer")
    @Expose
    private var cardIssuer: CardIssuer? = null

    /**
     * Instantiates a new Create save card request.
     *
     * @param currency            the currency
     * @param customer            the customer
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
     * @param card                the card
     * @param risk                the risk
     * @param issuer              the issuer
     * @param promo               the promo
     * @param loyalty             the loyalty
     * @param cardIssuer           the cardIssuer
     */
   init {
        this.currency = currency
        this.customer = customer
        this.order = order
        this.redirect = redirect
        this.post = post
        this.source = source
        this.description = description
        this.metadata = metadata
        this.reference = reference
        this.saveCard = saveCard
        this.statementDescriptor = statementDescriptor
        this.threeDSecure = threeDSecure
        this.receipt = receipt
        this.card = card
        this.risk = risk
        this.issuer = issuer
        this.promo = promo
        this.loyalty = loyalty
        this.cardIssuer = cardIssuer
    }
}
