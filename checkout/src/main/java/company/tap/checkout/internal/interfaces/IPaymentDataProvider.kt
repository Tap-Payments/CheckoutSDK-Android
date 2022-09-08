package company.tap.checkout.internal.interfaces

import company.tap.checkout.internal.api.models.AmountedCurrency

import company.tap.checkout.internal.api.models.CardIssuer

import company.tap.checkout.open.models.Merchant
import company.tap.checkout.internal.api.models.SupportedCurrencies

import company.tap.checkout.open.enums.TransactionMode

import company.tap.checkout.open.models.AuthorizeAction

import company.tap.checkout.open.models.TapCustomer

import company.tap.checkout.open.models.Destinations

import company.tap.checkout.open.models.Receipt

import company.tap.checkout.open.models.Reference

import company.tap.checkout.open.models.TopUp

import java.util.*

/**
 * Created by AhlaamK on 7/5/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
interface IPaymentDataProvider {
    /**
     * Gets selected currency.
     *
     * @return the selected currency
     */
    fun getSelectedCurrency(): AmountedCurrency?

    /**
     * Gets transaction currency.
     *
     * @return the transaction currency
     */
    fun getTransactionCurrency(): AmountedCurrency?


    /**
     * Gets supported currencies.
     *
     * @return the supported currencies
     */
    fun getSupportedCurrencies(): ArrayList<SupportedCurrencies>?

    /**
     * Gets payment options order id.
     *
     * @return the payment options order id
     */
    fun getPaymentOptionsOrderID(): String?

    /**
     * Gets post url.
     *
     * @return the post url
     */
    fun getPostURL(): String?

    /**
     * Gets customer.
     *
     * @return the customer
     */
    fun getCustomer(): TapCustomer

    /**
     * Gets payment description.
     *
     * @return the payment description
     */
    fun getPaymentDescription(): String?

    /**
     * Gets payment metadata.
     *
     * @return the payment metadata
     */
    fun getPaymentMetadata(): HashMap<String, String>?

    /**
     * Gets payment reference.
     *
     * @return the payment reference
     */
    fun getPaymentReference(): Reference?

    /**
     * Gets payment statement descriptor.
     *
     * @return the payment statement descriptor
     */
    fun getPaymentStatementDescriptor(): String?

    /**
     * Gets requires 3 d secure.
     *
     * @return the requires 3 d secure
     */
    fun getRequires3DSecure(): Boolean

    /**
     * Gets receipt settings.
     *
     * @return the receipt settings
     */
    fun getReceiptSettings(): Receipt?

    /**
     * Gets transaction mode.
     *
     * @return the transaction mode
     */
    fun getTransactionMode(): TransactionMode

    /**
     * Gets authorize action.
     *
     * @return the authorize action
     */
    fun getAuthorizeAction(): AuthorizeAction?

    /**
     * get Destination
     */
    fun getDestination(): Destinations?

    /**
     * get Merchant
     */
    fun getMerchant(): Merchant?

    /**
     * get CardIssuer object
     */
    fun getCardIssuer(): CardIssuer?

    /**
     * get TopUp object
     */
    fun getTopUp(): TopUp?
}