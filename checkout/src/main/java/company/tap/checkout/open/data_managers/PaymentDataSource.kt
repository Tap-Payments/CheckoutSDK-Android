package company.tap.checkout.open.data_managers

import android.content.Context
import company.tap.checkout.internal.api.models.BINLookupResponse
import company.tap.checkout.internal.api.models.CardIssuer
import company.tap.checkout.internal.api.models.Charge
import company.tap.checkout.internal.api.models.Merchant
import company.tap.checkout.internal.api.responses.PaymentOptionsResponse
import company.tap.checkout.internal.api.responses.SDKSettings
import company.tap.checkout.open.enums.CardType
import company.tap.checkout.open.enums.TransactionMode
import company.tap.checkout.open.interfaces.PaymentDataSource
import company.tap.checkout.open.models.*
import java.math.BigDecimal
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by AhlaamK on 10/5/20.

Copyright (c) 2020    Tap Payments.
All rights reserved.
 **/

object PaymentDataSource :PaymentDataSource {
    /////////////////////////////////    Props Area  //////////////////////////////////////////////
    private var currency: TapCurrency? = null
    private lateinit var tapCustomer: TapCustomer
    private var amount: BigDecimal? = null
    private var items: ArrayList<PaymentItem>? = null
    private lateinit var transactionMode: TransactionMode
    private var taxes: ArrayList<Tax>? = null
    private var shipping: ArrayList<Shipping>? = null
    private var postURL: String? = null
    private var paymentDescription: String? = null
    private var paymentType: String? = null
    private lateinit var paymentMetadata: HashMap<String, String>
    private var paymentReference: Reference? = null
    private val paymentStatementDescriptor: String? = null
    private var requires3DSecure = false
    private var allowUserToSaveCard = true
    private var receiptSettings: Receipt? = null
    private  var authorizeAction: AuthorizeAction?=null
    private var destination: Destinations? = null
    private var merchant: Merchant? = null

    private var cardType: CardType? = null
    private val context: Context? = null

    private const val trx_curr = "kwd"
    private const val trx_mode = "fullscreen"

    private var defaultCardHolderName: String? = null

    private var enableEditCardHolderName = false

    private val cardIssuer: CardIssuer? = null

    private var topup: TopUp? = null
    private var selectedCurrency: String? = null
    private var selectedAmount: BigDecimal? = null
    private var sdkSettings: SDKSettings? = null
    private var paymentOptionsResponse: PaymentOptionsResponse? = null
    private var binLookupResponse: BINLookupResponse? = null
    private var chargeOrAuthorize: Charge? = null


    //////////////////////// Setter's Area  ///////////////////////////////////////
    /**
     * Set transaction currency.
     *
     * @param tapCurrency the tap currency
     */
    fun setTransactionCurrency(tapCurrency: TapCurrency) {
        currency = tapCurrency
    }

    /**
     * Set tapCustomer.
     *
     * @param tapCustomer the tapCustomer
     */
    fun setCustomer(tapCustomer: TapCustomer) {
        this.tapCustomer = tapCustomer
    }

    /**
     * Set amount.
     *
     * @param amount the amount
     */
    fun setAmount(amount: BigDecimal?) {
        this.amount = amount
    }

    /**
     * Set payment items.
     *
     * @param paymentItems the payment items
     */
    fun setPaymentItems(paymentItems: ArrayList<PaymentItem>?) {
        this.items = paymentItems
    }

    /**
     * Set transaction mode.
     *
     * @param transactionMode the transaction mode
     */
    fun setTransactionMode(transactionMode: TransactionMode) {
        this.transactionMode = transactionMode
    }

    /**
     * Set taxes.
     *
     * @param taxes the taxes
     */
    fun setTaxes(taxes: ArrayList<Tax>?) {
        this.taxes = taxes
    }

    /**
     * Set shipping.
     *
     * @param shippingList the shipping list
     */
    fun setShipping(shippingList: ArrayList<Shipping>?) {
        shipping = shippingList
    }

    /**
     * Set post url.
     *
     * @param postURL the post url
     */
    fun setPostURL(postURL: String?) {
        this.postURL = postURL
    }

    /**
     * Set payment description.
     *
     * @param paymentDescription the payment description
     */
    fun setPaymentDescription(paymentDescription: String?) {
        this.paymentDescription = paymentDescription
    }

    /**
     * Set payment metadata.
     *
     * @param paymentMetadata the payment metadata
     */
    fun setPaymentMetadata(paymentMetadata: HashMap<String, String>) {
        this.paymentMetadata = paymentMetadata
    }

    /**
     * Set payment reference.
     *
     * @param paymentReference the payment reference
     */
    fun setPaymentReference(paymentReference: Reference?) {
        this.paymentReference = paymentReference
    }

    /**
     * Set payment statement descriptor.
     *
     * @param paymentDescription the payment description
     */
    fun setPaymentStatementDescriptor(paymentDescription: String?) {
        this.paymentDescription = paymentDescription
    }

    /**
     * Is user allowed to save his card
     * @param saveCardStatus
     */
    fun isUserAllowedToSaveCard(saveCardStatus: Boolean) {
        allowUserToSaveCard = saveCardStatus
    }

    /**
     * choose paymentType
     * @param paymentType
     */
    fun setPaymentType(paymentType: String?) {
        this.paymentType = paymentType
    }

    /**
     * Is requires 3 d secure.
     *
     * @param requires3DSecure the requires 3 d secure
     */
    fun isRequires3DSecure(requires3DSecure: Boolean) {
        this.requires3DSecure = requires3DSecure
    }

    /**
     * choose cardType
     * @param cardType
     */
    fun setcardType(cardType: CardType?) {
        this.cardType = cardType
    }


    /**
     * Set receipt settings.
     *
     * @param receipt the receipt
     */
    fun setReceiptSettings(receipt: Receipt?) {
        receiptSettings = receipt
    }

    /**
     * Set authorize action.
     *
     * @param authorizeAction the authorize action
     */
    fun setAuthorizeAction(authorizeAction: AuthorizeAction?) {
        this.authorizeAction = authorizeAction
    }

    /**
     * set Destination
     * @return
     */
    fun setDestination(destination: Destinations?) {
        this.destination = destination
    }

    /**
     * set Merchant
     * @param merchant
     */
    fun setMerchant(merchant: Merchant?) {
        this.merchant = merchant
    }

    /**
     * Set default CardholderName.
     *
     * @param defaultCardHolderName the  default CardholderName
     */
    fun setDefaultCardHolderName(defaultCardHolderName: String?) {
        this.defaultCardHolderName = defaultCardHolderName
    }

    /**
     * Is user allowed to edit his cardHolderName
     * @param enableEditCardHolderName
     */
    fun isUserAllowedToEditCardHolderName(enableEditCardHolderName: Boolean) {
        this.enableEditCardHolderName = enableEditCardHolderName
    }

    fun setSelectedCurrency(selectedCurrency: String){
        this.selectedCurrency =selectedCurrency
    }

    fun setSelectedAmount(selectedAmount: BigDecimal){
        this.selectedAmount =selectedAmount
    }

    /**
     * Set sdkSettings.
     *
     * @param sdkSettings the sdkSettings
     */
    fun setSDKSettings(sdkSettings: SDKSettings?) {
        this.sdkSettings = sdkSettings
    }

    /**
     * Sets charge or authorize.
     *
     * @param charge the charge
     */
    fun setChargeOrAuthorize(charge: Charge) {
        this.chargeOrAuthorize = charge
    }
    /**
     * Set sdkSettings.
     *
     * @param sdkSettings the sdkSettings
     */
    fun setPaymentOptionsResponse(paymentOptionsResponse:PaymentOptionsResponse?) {
        this.paymentOptionsResponse = paymentOptionsResponse
    }

    fun setBinLookupResponse(binLookupResponse: BINLookupResponse?){
        this.binLookupResponse = binLookupResponse
    }
/////<<<<<<<<<<<<<<<<<<<<<<<<<,Getters Area >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>/////////////////


    override fun getCurrency(): TapCurrency? {
        return currency
    }

    override fun getCustomer(): TapCustomer {
       return tapCustomer
    }

    override fun getAmount(): BigDecimal? {
       return amount
    }

    override fun getItems(): ArrayList<PaymentItem>? {

       return items
    }

    override fun getTransactionMode(): TransactionMode {
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

    override fun getPaymentMetadata(): HashMap<String, String> {
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
        //return null
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

    override fun getTopup(): TopUp? {
       return topup
    }

    override fun getSelectedCurrency(): String? {
       return selectedCurrency
    }

    override fun getSelectedAmount(): BigDecimal? {
     return selectedAmount
    }

    override fun getPaymentOptionsResponse(): PaymentOptionsResponse? {
        return paymentOptionsResponse
    }

    override fun getSDKSettings(): SDKSettings? {
        return sdkSettings
    }

    override fun getBinLookupResponse(): BINLookupResponse? {
       return binLookupResponse
    }
}