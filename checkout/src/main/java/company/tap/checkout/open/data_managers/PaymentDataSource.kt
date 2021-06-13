package company.tap.checkout.open.data_managers

import android.content.Context
import company.tap.checkout.internal.api.models.CardIssuer
import company.tap.checkout.internal.api.models.Merchant
import company.tap.checkout.open.enums.CardType
import company.tap.checkout.open.enums.TransactionMode
import company.tap.checkout.open.interfaces.PaymentDataSource
import company.tap.checkout.open.models.*
import java.math.BigDecimal
import java.util.*

/**
 * Created by AhlaamK on 10/5/20.

Copyright (c) 2020    Tap Payments.
All rights reserved.
 **/

object PaymentDataSource :PaymentDataSource {
    /////////////////////////////////    Props Area  //////////////////////////////////////////////
    private val currency: TapCurrency? = null
    private val customer: Customer? = null
    private val amount: BigDecimal? = null
    private val items: ArrayList<PaymentItem>? = null
    private val transactionMode: TransactionMode? = null
    private val taxes: ArrayList<Tax>? = null
    private val shipping: ArrayList<Shipping>? = null
    private val postURL: String? = null
    private val paymentDescription: String? = null
    private val paymentType: String? = null
    private val paymentMetadata: HashMap<String, String>? = null
    private val paymentReference: Reference? = null
    private val paymentStatementDescriptor: String? = null
    private const val requires3DSecure = false
    private const val allowUserToSaveCard = true
    private val receiptSettings: Receipt? = null
    private val authorizeAction: AuthorizeAction? = null
    private val destination: Destinations? = null
    private val merchant: Merchant? = null

    private val cardType: CardType? = null
    private val context: Context? = null

    private const val trx_curr = "kwd"
    private const val trx_mode = "fullscreen"

    private val defaultCardHolderName: String? = null

    private const val enableEditCardHolderName = false

    private val cardIssuer: CardIssuer? = null

    override fun getCurrency(): TapCurrency? {
        return currency
    }

    override fun getCustomer(): Customer? {
       return customer
    }

    override fun getAmount(): BigDecimal? {
       return amount
    }

    override fun getItems(): ArrayList<PaymentItem> {
        return items ?: ArrayList()
    }

    override fun getTransactionMode(): TransactionMode? {
        return transactionMode
    }

    override fun getTaxes(): ArrayList<Tax>?{
        return taxes ?: ArrayList()
    }

    override fun getShipping(): ArrayList<Shipping> ?{
        return shipping ?: ArrayList()
    }

    override fun getPostURL(): String? {
       return postURL
    }

    override fun getPaymentDescription(): String? {
       return paymentDescription
    }

    override fun getPaymentMetadata(): HashMap<String, String>? {
       return paymentMetadata
    }

    override fun getPaymentReference(): Reference? {
       return paymentReference
    }

    override fun getPaymentStatementDescriptor(): String? {
        return paymentStatementDescriptor
    }

    override fun getAllowedToSaveCard(): Boolean {
       return allowUserToSaveCard
    }

    override fun getRequires3DSecure(): Boolean {
       return requires3DSecure
    }

    override fun getReceiptSettings(): Receipt? {
       return receiptSettings
    }

    override fun getAuthorizeAction(): AuthorizeAction? {
        return authorizeAction
    }

    override fun getDestination(): Destinations? {
        return destination
    }

    override fun getMerchant(): Merchant? {
        return merchant
    }

    override fun getPaymentDataType(): String? {
       return paymentType
    }

    override fun getCardType(): CardType? {
       return cardType
    }

    override fun getDefaultCardHolderName(): String? {
       return defaultCardHolderName
    }

    override fun getEnableEditCardHolderName(): Boolean {
       return  enableEditCardHolderName
    }

    override fun getCardIssuer(): CardIssuer? {
       return cardIssuer
    }
}