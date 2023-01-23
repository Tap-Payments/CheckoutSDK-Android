package company.tap.checkout.open.interfaces

import androidx.annotation.RestrictTo
import company.tap.checkout.internal.api.models.BINLookupResponse
import company.tap.checkout.internal.api.models.CardIssuer
import company.tap.checkout.open.models.Merchant
import company.tap.checkout.internal.api.models.PaymentOption
import company.tap.checkout.internal.api.responses.InitResponseModel
import company.tap.checkout.internal.api.responses.MerchantData
import company.tap.checkout.internal.api.responses.PaymentOptionsResponse
import company.tap.checkout.internal.enums.WebViewType
import company.tap.checkout.open.enums.CardType
import company.tap.checkout.open.enums.SdkMode
import company.tap.checkout.open.enums.TransactionMode
import company.tap.checkout.open.models.*
import java.math.BigDecimal
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * Created by AhlaamK on 6/13/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/

/**
 * The interface Payment data source.
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
interface PaymentDataSource {
    /**
     * Transaction currency. @return the currency
     */
    fun getCurrency(): TapCurrency?

    /**
     * TapCustomer. @return the customer
     */
    fun getCustomer(): TapCustomer?

    /**
     * Amount. Either amount or items should return nonnull value. If both return nonnull, then items is preferred. @return the amount
     */
    fun getAmount(): BigDecimal?

    /**
     * List of items to pay for. Either amount or items should return nonnull value. If both return nonnull, then items is preferred. @return the items
     */
    fun getItems(): ArrayList<ItemsModel>?

    /**
     * Transaction mode. If you return null in this method, it will be treated as PURCHASE. @return the transaction mode
     */
    fun getTransactionMode(): TransactionMode?

    /**
     * List of taxes. Optional. Note: specifying taxes will affect total payment amount. @return the taxes
     */
    fun getTaxes(): ArrayList<Tax>?

    /**
     * Shipping list. Optional. Note: specifying shipping will affect total payment amount. @return the shipping
     */
    fun getShipping(): ArrayList<Shipping>?

    /**
     * Tap will post to this URL after transaction finishes. Optional. @return the post url
     */
    fun getPostURL(): String?

    /**
     * Description of the payment. @return the payment description
     */
    fun getPaymentDescription(): String?

    /**
     * If you would like to pass additional information with the payment, pass it here. @return the payment metadata
     */
    fun getPaymentMetadata(): HashMap<String, String>?

    /**
     * Payment reference. Implement this property to keep a reference to the transaction on your backend. @return the payment reference
     */
    fun getPaymentReference(): Reference?

    /**
     * Payment statement descriptor. @return the payment statement descriptor
     */
    fun getPaymentStatementDescriptor(): String?

    /**
     * Defines if user allowed to save card. @return the allowUserToSaveCard
     * @return
     */
    fun getAllowedToSaveCard(): Boolean

    /**
     * Defines if 3D secure check is required. @return the requires 3 d secure
     */
    fun getRequires3DSecure(): Boolean

    /**
     * Receipt dispatch settings. @return the receipt settings
     */
    fun getReceiptSettings(): Receipt?

    /**
     * Action to perform after authorization succeeds. Used only if transaction mode is AUTHORIZE_CAPTURE. @return the authorize action
     */
    fun getAuthorizeAction(): AuthorizeAction?

    /**
     * The Destination array contains list of Merchant desired destinations accounts to receive money from payment transactions
     */
    fun getDestination(): Destinations?

    fun getMerchant(): Merchant?
    fun getPaymentDataType(): String?

    /**
     * Defines if user wants all cards or specific card types.
     */
    fun getCardType(): CardType?

    /**
     * Defines the default cardHolderName. Optional. @return the default CardHolderName
     */
    fun getDefaultCardHolderName(): String?

    /**
     * Defines if user allowed to edit the cardHolderName. @return the enableEditCardHolderName
     * @return
     */
    fun getEnableEditCardHolderName(): Boolean

    /**
     * Defines the cardIssuer details. Optional. @return the default CardIssuer
     */
    fun getCardIssuer(): CardIssuer?

    fun getTopup(): TopUp?

    fun getSelectedCurrency():String?

    fun getSelectedAmount():BigDecimal?

    fun getPaymentOptionsResponse():PaymentOptionsResponse?

    fun getMerchantData(): MerchantData?

    fun getBinLookupResponse() : BINLookupResponse?

    /**
     * Defines the SDK mode . Optional. @return the default Sandbox
     */
    fun getSDKMode(): SdkMode?
     /**
     * Defines the Payment options. Optional.
     */
    fun getAvailablePaymentOptionsCardBrands(): ArrayList<PaymentOption>?
    /**
     * Defines the TokenConfig for header
     */
    fun getTokenConfig(): String?

    /**
     * Defines the AuthKeys.
     */
    fun getAuthKeys(): String?

    fun getInitOptionsResponse():InitResponseModel?

    /**
     * Defines the PaymentOption details. Optional. @return the default PaymentOption
     */
    fun getGooglePaymentOptions(): java.util.ArrayList<PaymentOption>?




    /**
     * Defines the OrderObject ItemsModel. Optional. @return the default OrderItems
     */
    fun getOrderItems(): java.util.ArrayList<ItemsModel>?

    /**
     * Defines the OrderObject details. Optional. @return the default Order
     */
    fun getOrderObject():OrderObject ?

    fun getSDKLocale():Locale?
    fun getSelectedCurrencySymbol():String?

    fun getWebViewType():WebViewType?

    fun getCardHolderNameShowHide():Boolean
}