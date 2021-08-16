package company.tap.checkout.open.controller


import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentManager
import com.google.gson.JsonElement
import company.tap.checkout.internal.PaymentDataProvider
import company.tap.checkout.internal.api.models.Merchant
import company.tap.checkout.internal.apiresponse.CardViewEvent
import company.tap.checkout.internal.apiresponse.CardViewModel
import company.tap.checkout.internal.utils.CustomUtils.showDialog
import company.tap.checkout.internal.viewmodels.CheckoutViewModel
import company.tap.checkout.open.data_managers.PaymentDataSource
import company.tap.checkout.open.enums.CardType
import company.tap.checkout.open.enums.TransactionMode
import company.tap.checkout.open.interfaces.SessionDelegate
import company.tap.checkout.open.models.*
import company.tap.tapnetworkkit.exception.GoSellError
import company.tap.tapnetworkkit.interfaces.APIRequestCallback
import company.tap.tapuilibrary.uikit.enums.ActionButtonState
import company.tap.tapuilibrary.uikit.views.TabAnimatedActionButton
import kotlinx.android.synthetic.main.action_button_animation.view.*
import retrofit2.Response
import java.math.BigDecimal
import java.util.*

/**
 * Created by AhlaamK on 10/5/20.
Copyright (c) 2020    Tap Payments.
All rights reserved.
 **/
//Responsible for setting data given by Merchant  and starting the session
@SuppressLint("StaticFieldLeak")
object  SDKSession : APIRequestCallback {
    private var paymentDataSource: PaymentDataSource? = null
    var contextSDK: Context? = null
   var supportFragmentManager: FragmentManager ?=null
    @JvmField
    var sessionDelegate: SessionDelegate? = null

    @JvmField
    var tabAnimatedActionButton: TabAnimatedActionButton? = null

    private var sessionActive = false

    init {
        initPaymentDataSource()

    }

    private fun persistPaymentDataSource() {
        paymentDataSource?.let { PaymentDataProvider().setExternalDataSource(it) }
    }

    private fun checkSessionStatus() {
        if (SessionManager.isSessionEnabled()) {
            println("Session already active!!!")
            return
        }
    }

    private fun initPaymentDataSource() {
        this.paymentDataSource = PaymentDataSource
        if (paymentDataSource != null) {
            println("paymentDataSource sdk ${paymentDataSource.toString()}")
            PaymentDataProvider().setExternalDataSource(paymentDataSource!!)
        }


    }

    open fun addSessionDelegate(_sessionDelegate: SessionDelegate) {
        println("addSessionDelegate sdk ${_sessionDelegate}")
        this.sessionDelegate = _sessionDelegate


    }


    fun getListener(): SessionDelegate? {
        return sessionDelegate
    }

    /**
     * Instantiate payment data source.
     */
    open fun instantiatePaymentDataSource() {
        paymentDataSource = PaymentDataSource
    }

    fun startSDK(supportFragmentManager: FragmentManager, context: Context) {
        println("is session enabled ${SessionManager.isSessionEnabled()}")
        if (SessionManager.isSessionEnabled()) {
            println("Session already active!!!")
            return
        }
        this.contextSDK = context

        getPaymentOptions(supportFragmentManager)
        /*val tapCheckoutFragment = CheckoutFragment()
        tapCheckoutFragment.show(supportFragmentManager, null)*/
        // start session
        SessionManager.setActiveSession(true)

    }

    /**
     * set amount
     */
    open fun setAmount(amount: BigDecimal) {
        println("amount ... $amount")
        paymentDataSource?.setAmount(amount)
    }


    /**
     * set setPaymentType
     */
    open fun setPaymentType(paymentType: String) {
        println("paymentType ... $paymentType")
        paymentDataSource?.setPaymentType(paymentType)
    }

    /**
     * set transaction currency
     *
     * @param tapCurrency the tap currency
     */
    open fun setTransactionCurrency(tapCurrency: TapCurrency) {
        paymentDataSource?.setTransactionCurrency(tapCurrency)
    }


    /**
     * set transaction mode
     *
     * @param transactionMode the transaction mode
     */
    open fun setTransactionMode(transactionMode: TransactionMode) {
        paymentDataSource?.setTransactionMode(transactionMode)
    }

    /**
     * set tapCustomer
     *
     * @param tapCustomer the tapCustomer
     */
    open fun setCustomer(tapCustomer: TapCustomer) {
        paymentDataSource?.setCustomer(tapCustomer)
    }

    /**
     * set payment items
     *
     * @param paymentItems the payment items
     */
    open fun setPaymentItems(paymentItems: ArrayList<PaymentItem>?) {
        paymentDataSource?.setPaymentItems(paymentItems)
    }

    /**
     * set payment tax
     *
     * @param taxes the taxes
     */
    open fun setTaxes(taxes: ArrayList<Tax>?) {
        paymentDataSource?.setTaxes(taxes)
    }

    /**
     * set payment shipping
     *
     * @param shipping the shipping
     */
    open fun setShipping(shipping: ArrayList<Shipping>?) {
        paymentDataSource?.setShipping(shipping)
    }

    /**
     * set post url
     *
     * @param postURL the post url
     */
    open fun setPostURL(postURL: String?) {
        paymentDataSource?.setPostURL(postURL)
    }

    /**
     * set payment description
     *
     * @param paymentDescription the payment description
     */
    open fun setPaymentDescription(paymentDescription: String?) {
        paymentDataSource?.setPaymentDescription(paymentDescription)
    }

    /**
     * set payment meta data
     *
     * @param paymentMetadata the payment metadata
     */
    open fun setPaymentMetadata(paymentMetadata: HashMap<String, String>) {
        paymentDataSource?.setPaymentMetadata(paymentMetadata)
    }

    /**
     * set payment reference
     *
     * @param paymentReference the payment reference
     */
    open fun setPaymentReference(paymentReference: Reference?) {
        paymentDataSource?.setPaymentReference(paymentReference)
    }

    /**
     * set payment statement descriptor
     *
     * @param setPaymentStatementDescriptor the set payment statement descriptor
     */
    open fun setPaymentStatementDescriptor(setPaymentStatementDescriptor: String?) {
        paymentDataSource?.setPaymentStatementDescriptor(setPaymentStatementDescriptor)
    }


    /**
     * enable or disable saving card.
     * @param saveCardStatus
     */
    open fun isUserAllowedToSaveCard(saveCardStatus: Boolean) {
        println("isUserAllowedToSaveCard >>> $saveCardStatus")
        paymentDataSource?.isUserAllowedToSaveCard(saveCardStatus)
    }

    /**
     * enable or disable 3dsecure
     *
     * @param is3DSecure the is 3 d secure
     */
    open fun isRequires3DSecure(is3DSecure: Boolean) {
        println("isRequires3DSecure >>> $is3DSecure")
        paymentDataSource?.isRequires3DSecure(is3DSecure)
    }

    /**
     * set payment receipt
     *
     * @param receipt the receipt
     */
    open fun setReceiptSettings(receipt: Receipt?) {
        paymentDataSource?.setReceiptSettings(receipt)
    }


    /**
     * set Authorize Action
     *
     * @param authorizeAction the authorize action
     */
    open fun setAuthorizeAction(authorizeAction: AuthorizeAction?) {
        paymentDataSource?.setAuthorizeAction(authorizeAction)
    }

    /**
     * set Destination
     */
    open fun setDestination(destination: Destinations?) {
        paymentDataSource?.setDestination(destination)
    }

    /**
     * set Merchant ID
     * @param merchantId
     */
    open fun setMerchantID(merchantId: String?) {
        if (merchantId != null && merchantId.trim { it <= ' ' }
                .isNotEmpty()) paymentDataSource?.setMerchant(
            Merchant(merchantId)
        ) else paymentDataSource?.setMerchant(null)
    }

    /**
     * set setCardType
     * @param cardType the cardType
     */
    open fun setCardType(cardType: CardType) {
        println("cardType ... $cardType")
        paymentDataSource?.setcardType(cardType)
    }

    /**
     * set default cardholderName
     *
     * @param defaultCardHolderName the default cardholderName
     */
    open fun setDefaultCardHolderName(defaultCardHolderName: String?) {
        paymentDataSource?.setDefaultCardHolderName(defaultCardHolderName)
    }

    /**
     * enable or disable edit cardholdername.
     * @param enableCardHolderName
     */
    open fun isUserAllowedToEnableCardHolderName(enableCardHolderName: Boolean) {
        println("isUserAllowedToEnableCardHolderName >>> $enableCardHolderName")
        paymentDataSource?.isUserAllowedToEditCardHolderName(enableCardHolderName)
    }

    override fun onSuccess(responseCode: Int, requestCode: Int, response: Response<JsonElement>?) {
        println("onSuccess is being called ")
    }

    override fun onFailure(requestCode: Int, errorDetails: GoSellError?) {
        println("onFailure is being called ${errorDetails?.errorBody}")
    }

    /**
     * Sets button view.
     *
     * @param buttonView       the button view
     * @param activity         the activity
     * @param SDK_REQUEST_CODE the sdk request code
     */

    fun setButtonView(
        payButtonView: TabAnimatedActionButton,
        context: Context,
        supportFragmentManager: FragmentManager
    ) {
        payButtonView.setOnClickListener {
            if (sessionActive) return@setOnClickListener

            if (PaymentDataSource?.getTransactionMode() == null) {
                sessionDelegate?.invalidTransactionMode()
                return@setOnClickListener
            } else {
                sessionActive = true
                startSDK(supportFragmentManager, context)
                this.tabAnimatedActionButton = payButtonView
                if (tabAnimatedActionButton != null) {
                    tabAnimatedActionButton?.changeButtonState(ActionButtonState.LOADING)
                }

            }
        }

    }


    /**
     * call payment methods API
     */
    @RequiresApi(Build.VERSION_CODES.N)
    private fun getPaymentOptions(supportFragmentManager: FragmentManager) {
        when (isInternetConnectionAvailable()) {
            ErrorTypes.SDK_NOT_CONFIGURED_WITH_VALID_CONTEXT -> contextSDK?.let {
                showDialog(
                    "SDK Error",
                    "SDK Not Configured Correctly", it,null,null,null,null,false
                )
            }
            ErrorTypes.CONNECTIVITY_MANAGER_ERROR -> contextSDK?.let {
                showDialog(
                    "SDK Error",
                    "Device has a problem in Connectivity manager", it,null,null,null,null,false
                )
            }
            ErrorTypes.INTERNET_NOT_AVAILABLE -> contextSDK?.let {
                showDialog(
                    "Connection Error",
                    "Internet connection is not available", it,null,null,null,null,false
                )
            }
            ErrorTypes.INTERNET_AVAILABLE -> startPayment(supportFragmentManager)
        }
    }
    private fun isInternetConnectionAvailable(): ErrorTypes? {
        val ctx: Context = SDKSession.contextSDK
            ?: return ErrorTypes.SDK_NOT_CONFIGURED_WITH_VALID_CONTEXT
        val connectivityManager =
            ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            return if (activeNetworkInfo != null && activeNetworkInfo.isConnected) ErrorTypes.INTERNET_AVAILABLE else ErrorTypes.INTERNET_NOT_AVAILABLE
        }
        return ErrorTypes.CONNECTIVITY_MANAGER_ERROR
    }




    /**
     * Error Type
     */
    enum class ErrorTypes {
        SDK_NOT_CONFIGURED_WITH_VALID_CONTEXT, INTERNET_NOT_AVAILABLE, INTERNET_AVAILABLE, CONNECTIVITY_MANAGER_ERROR
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun startPayment(_supportFragmentManager:FragmentManager) {
        persistPaymentDataSource()
        if (tabAnimatedActionButton != null) tabAnimatedActionButton?.changeButtonState(ActionButtonState.LOADING)
        println(" this.paymentDataSource.getTransactionMode() : " + paymentDataSource?.getTransactionMode())
        CardViewModel().processEvent(CardViewEvent.InitEvent, CheckoutViewModel(),null,null,null,null,null,null,null,null,
            _supportFragmentManager,contextSDK)


    }

}




