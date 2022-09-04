package company.tap.checkout.internal

import company.tap.checkout.internal.api.enums.AuthorizeActionType
import company.tap.checkout.internal.api.models.AmountedCurrency
import company.tap.checkout.internal.api.models.CardIssuer
import company.tap.checkout.internal.api.models.Merchant
import company.tap.checkout.internal.api.models.SupportedCurrencies
import company.tap.checkout.internal.interfaces.IPaymentDataProvider
import company.tap.checkout.open.data_managers.PaymentDataSource
import company.tap.checkout.open.enums.TransactionMode
import company.tap.checkout.open.models.*
import java.util.*

/**
 * Created by AhlaamK on 7/5/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
internal class PaymentDataProvider:IPaymentDataProvider {
    private  var externalDataSource: PaymentDataSource = PaymentDataSource
    override fun getTransactionCurrency(): AmountedCurrency? {
        return getExternalDataSource()?.getCurrency()?.isoCode?.let { getExternalDataSource().getCurrency()?.isoCode?.let { it1 -> getExternalDataSource().getAmount()?.let { it2 -> AmountedCurrency(
            it,
            it1,
            it2
        ) } } }

    }

    override fun getSelectedCurrency(): AmountedCurrency? {


     //   return AmountedCurrency("kwd","kwd",BigDecimal.valueOf(222))
        return if(getExternalDataSource().getSelectedCurrency()!=null){
            PaymentDataSource.getSelectedAmount()?.let {
                AmountedCurrency(PaymentDataSource.getSelectedCurrency().toString(),PaymentDataSource.getSelectedCurrency().toString(),
                    it
                )
            }


        }else{
            getExternalDataSource().getCurrency()?.isoCode?.let { getExternalDataSource().getCurrency()?.isoCode?.let { it1 -> getExternalDataSource().getAmount()?.let { it2 -> AmountedCurrency(
                it,
                it1,
                it2
            ) } } }

        }
    }

    override fun getSupportedCurrencies(): ArrayList<SupportedCurrencies>? {
       // return getPaymentOptionsDataManager().getPaymentOptionsResponse().getSupportedCurrencies()
       // return PaymentOptionsResponse.supportedCurrencies
        return PaymentDataSource?.getPaymentOptionsResponse()?.supportedCurrencies
    }

    override fun getPaymentOptionsOrderID(): String? {
      //return getPaymentOptionsDataManager().getPaymentOptionsResponse().getOrderID()
        return if (PaymentDataSource.getPaymentOptionsResponse()?.orderID == null
        ) {
            ""
        } else  PaymentDataSource.getPaymentOptionsResponse()?.orderID
    }

    override fun getPostURL(): String? {
            return getExternalDataSource().getPostURL()

    }

    override fun getCustomer(): TapCustomer {

            return getExternalDataSource().getCustomer()


    }

    override fun getPaymentDescription(): String? {

            return getExternalDataSource().getPaymentDescription()

    }

    override fun getPaymentMetadata(): HashMap<String, String>? {

            return getExternalDataSource().getPaymentMetadata()

    }

    override fun getPaymentReference(): Reference? {

            return getExternalDataSource().getPaymentReference()


    }

    override fun getPaymentStatementDescriptor(): String? {

            return getExternalDataSource().getPaymentStatementDescriptor()

    }

    override fun getRequires3DSecure(): Boolean {
        /**
         * Stop checking SDKsettings in SDK to decide if  the payment will be 3DSecure or not
         * instead always send value configured by the Merchant.
         */

        return PaymentDataSource?.getRequires3DSecure()
    }

    override fun getReceiptSettings(): Receipt? {

            return getExternalDataSource()?.getReceiptSettings()

    }

    override fun getTransactionMode(): TransactionMode {
if(getExternalDataSource().getTransactionMode()!=null)
            return getExternalDataSource().getTransactionMode()
else return TransactionMode.PURCHASE


    }

    override fun getAuthorizeAction(): AuthorizeAction? {
           var authorizeAction: AuthorizeAction? = getExternalDataSource().getAuthorizeAction()
        println("authorizeAction>>>" + authorizeAction)
      if (authorizeAction == null) {
           return AuthorizeAction(AuthorizeActionType.VOID, 168)
      }
      else
        return authorizeAction
    }

    override fun getDestination(): Destinations? {

            return getExternalDataSource().getDestination()

    }

    override fun getMerchant(): Merchant? {

            return getExternalDataSource().getMerchant()

    }

    override fun getCardIssuer(): CardIssuer? {
            return  getExternalDataSource().getCardIssuer()

    }

    override fun getTopUp(): TopUp? {
            return  getExternalDataSource().getTopup()

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