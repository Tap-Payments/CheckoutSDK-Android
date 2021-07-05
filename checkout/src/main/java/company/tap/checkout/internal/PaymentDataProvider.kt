package company.tap.checkout.internal

import company.tap.checkout.internal.api.models.AmountedCurrency
import company.tap.checkout.internal.api.models.CardIssuer
import company.tap.checkout.internal.api.models.Merchant
import company.tap.checkout.internal.api.models.SupportedCurrencies
import company.tap.checkout.internal.api.responses.PaymentOptionsResponse
import company.tap.checkout.internal.interfaces.IPaymentDataProvider
import company.tap.checkout.open.data_managers.PaymentDataSource
import company.tap.checkout.open.enums.TransactionMode
import company.tap.checkout.open.models.*
import java.math.BigDecimal
import java.util.*

/**
 * Created by AhlaamK on 7/5/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
internal class PaymentDataProvider:IPaymentDataProvider {
    private lateinit var externalDataSource: PaymentDataSource
    override fun getTransactionCurrency(): AmountedCurrency? {
        //return getExternalDataSource()?.getCurrency()?.isoCode?.let { AmountedCurrency(it, getExternalDataSource().getAmount().toString()) }
        return AmountedCurrency("KWD", "kwd", BigDecimal.valueOf(222))
    }

    override fun getSelectedCurrency(): AmountedCurrency? {
       // return getPaymentOptionsDataManager().getSelectedCurrency()
        return AmountedCurrency("kwd","kwd",BigDecimal.valueOf(222))
    }

    override fun getSupportedCurrencies(): ArrayList<SupportedCurrencies>? {
       // return getPaymentOptionsDataManager().getPaymentOptionsResponse().getSupportedCurrencies()
       // return PaymentOptionsResponse.supportedCurrencies
        return null
    }

    override fun getPaymentOptionsOrderID(): String {
      //return getPaymentOptionsDataManager().getPaymentOptionsResponse().getOrderID()
        return "dsfsd"
    }

    override fun getPostURL(): String? {
        if (::externalDataSource.isInitialized){
            return getExternalDataSource()?.getPostURL()
        }else
        return PaymentDataSource?.getPostURL()
    }

    override fun getCustomer(): TapCustomer {
        if (::externalDataSource.isInitialized){
            return getExternalDataSource().getCustomer()
        }else
            return PaymentDataSource.getCustomer()

    }

    override fun getPaymentDescription(): String? {
        if (::externalDataSource.isInitialized){
            return getExternalDataSource().getPaymentDescription()
        }else
        return null
    }

    override fun getPaymentMetadata(): HashMap<String, String>? {
        if (::externalDataSource.isInitialized){
            return getExternalDataSource().getPaymentMetadata()
        }else
        return null
    }

    override fun getPaymentReference(): Reference? {
        if (::externalDataSource.isInitialized){
            return getExternalDataSource().getPaymentReference()
        }else
            return PaymentDataSource.getPaymentReference()

    }

    override fun getPaymentStatementDescriptor(): String? {
        if (::externalDataSource.isInitialized){
            return getExternalDataSource().getPaymentStatementDescriptor()
        }else
        return null
    }

    override fun getRequires3DSecure(): Boolean {
        /**
         * Stop checking SDKsettings in SDK to decide if  the payment will be 3DSecure or not
         * instead always send value configured by the Merchant.
         */
        if (::externalDataSource.isInitialized){
            return getExternalDataSource().getRequires3DSecure()
        }else
        return PaymentDataSource?.getRequires3DSecure()
    }

    override fun getReceiptSettings(): Receipt? {
        if (::externalDataSource.isInitialized){
            return getExternalDataSource()?.getReceiptSettings()
        }else
        return null
    }

    override fun getTransactionMode(): TransactionMode {
        if (::externalDataSource.isInitialized){
            return getExternalDataSource().getTransactionMode()
        }else
       // var transactionMode: TransactionMode = getExternalDataSource().getTransactionMode()
       // if (transactionMode == null) {
       //     transactionMode = TransactionMode.PURCHASE
      //  }
        return TransactionMode.PURCHASE
    }

    override fun getAuthorizeAction(): AuthorizeAction? {
     /*   var authorizeAction: AuthorizeAction = getExternalDataSource().getAuthorizeAction()
      if (authorizeAction == null) {
           // authorizeAction = AuthorizeAction().getDefault()

        }
        return authorizeAction*/
        return null
    }

    override fun getDestination(): Destinations? {
        if (::externalDataSource.isInitialized){
            return getExternalDataSource().getDestination()
        }else
        return null
    }

    override fun getMerchant(): Merchant? {
        if (::externalDataSource.isInitialized){
            return getExternalDataSource().getMerchant()
        }else
        return null
    }

    override fun getCardIssuer(): CardIssuer? {
        if (::externalDataSource.isInitialized){
            return  getExternalDataSource().getCardIssuer()
        }else
        return null
    }

    override fun getTopUp(): TopUp? {
        if (::externalDataSource.isInitialized){
            return  getExternalDataSource().getTopup()
        }else
        return null
    }


    /////////////////////////////////////////  ########### End of Singleton section ##################
    /**
     * Gets external data source.
     *
     * @return the external data source
     */
    fun getExternalDataSource(): PaymentDataSource{
        return this.externalDataSource
    }

    /**
     * Sets external data source.
     *
     * @param externalDataSource the external data source
     */
    fun setExternalDataSource(externalDataSource: PaymentDataSource) {
        println("externalDataSource<<<<>>>>" + externalDataSource)
        this.externalDataSource = externalDataSource

    }
}