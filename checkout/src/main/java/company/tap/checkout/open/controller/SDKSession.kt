package company.tap.checkout.open.controller


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentManager
import com.google.gson.JsonElement
import company.tap.checkout.internal.PaymentDataProvider
import company.tap.checkout.internal.api.enums.ChargeStatus
import company.tap.checkout.open.models.Merchant
import company.tap.checkout.internal.apiresponse.CardViewEvent
import company.tap.checkout.internal.apiresponse.CardViewModel
import company.tap.checkout.internal.utils.CustomUtils
import company.tap.checkout.internal.utils.CustomUtils.showDialog
import company.tap.checkout.internal.viewmodels.CheckoutViewModel
import company.tap.checkout.open.CheckOutActivity
import company.tap.checkout.open.data_managers.PaymentDataSource
import company.tap.checkout.open.enums.CardType
import company.tap.checkout.open.enums.SdkIdentifier
import company.tap.checkout.open.enums.SdkMode
import company.tap.checkout.open.enums.TransactionMode
import company.tap.checkout.open.interfaces.PluginSessionDelegate
import company.tap.checkout.open.interfaces.CheckOutDelegate
import company.tap.checkout.open.models.*
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapnetworkkit.exception.GoSellError
import company.tap.tapnetworkkit.interfaces.APIRequestCallback
import company.tap.tapuilibraryy.uikit.enums.ActionButtonState
import company.tap.tapuilibraryy.themekit.ThemeManager
import company.tap.tapuilibraryy.uikit.views.TabAnimatedActionButton
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
    var activity: Activity? = null
   var supportFragmentManager: FragmentManager ?=null
    @JvmField
    var checkOutDelegate: CheckOutDelegate? = null

    @JvmField
    var tabAnimatedActionButton: TabAnimatedActionButton? = null

     var sessionActive = false
    @JvmField
    var sdkIdentifier: String? = SdkIdentifier.Native.name

    @JvmField
    var pluginSessionDelegate: PluginSessionDelegate? = null

    @JvmField
    var showCloseImage: Boolean? = false

    @JvmField
    var enableLoyalty: Boolean? = false
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
           // println("paymentDataSource sdk ${paymentDataSource.toString()}")
            PaymentDataProvider().setExternalDataSource(paymentDataSource!!)
        }


    }

    fun addSessionDelegate(_checkOutDelegate: CheckOutDelegate) {
        //println("addSessionDelegate sdk ${_checkOutDelegate}")
        this.checkOutDelegate = _checkOutDelegate


    }
    fun addPluginSessionDelegate(_pluginSessionDelegate: PluginSessionDelegate) {
       // println("addPluginSessionDelegate sdk ${_pluginSessionDelegate}")
        this.pluginSessionDelegate = _pluginSessionDelegate


    }

    fun getListener(): CheckOutDelegate? {
        return checkOutDelegate
    }

    fun getPluginListener(): PluginSessionDelegate? {
        return pluginSessionDelegate
    }
    /**
     * Instantiate payment data source.
     */
    fun instantiatePaymentDataSource() {
        paymentDataSource = PaymentDataSource
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun startSDK(supportFragmentManager: FragmentManager?, context: Context, activity:Activity) {
        println("is session enabled ${SessionManager.isSessionEnabled()}")
        if (SessionManager.isSessionEnabled()) {
           // println("Session already active!!!")
            checkOutDelegate?.sessionFailedToStart()
            return
        }
        this.contextSDK = context
        this.activity =activity


        getInitOptions(supportFragmentManager)

        // start session
        SessionManager.setActiveSession(true)

    }

    /**
     * set amount
     */
    fun setAmount(amount: BigDecimal) {
       // println("amount ... $amount")
        paymentDataSource?.setAmount(amount)
    }


    /**
     * set setPaymentType
     */
    fun setPaymentType(paymentType: String) {
      //  println("paymentType ... $paymentType")
        paymentDataSource?.setPaymentType(paymentType)
    }

    /**
     * set transaction currency
     *
     * @param tapCurrency the tap currency
     */
    fun setTransactionCurrency(tapCurrency: TapCurrency) {
        paymentDataSource?.setTransactionCurrency(tapCurrency)
    }


    /**
     * set transaction mode
     *
     * @param transactionMode the transaction mode
     */
    fun setTransactionMode(transactionMode: TransactionMode) {
        paymentDataSource?.setTransactionMode(transactionMode)
    }

    /**
     * set tapCustomer
     *
     * @param tapCustomer the tapCustomer
     */
    fun setCustomer(tapCustomer: TapCustomer) {
        paymentDataSource?.setCustomer(tapCustomer)
    }

    /**
     * set payment items
     *
     * @param paymentItems the payment items
     */
    fun setPaymentItems(paymentItems: ArrayList<ItemsModel>?) {
        paymentDataSource?.setPaymentItems(paymentItems)
    }

    /**
     * set payment tax
     *
     * @param taxes the taxes
     */
    fun setTaxes(taxes: ArrayList<Tax>?) {
        paymentDataSource?.setTaxes(taxes)
    }

    /**
     * set payment shipping
     *
     * @param shipping the shipping
     */
    fun setShipping(shipping: ArrayList<Shipping>?) {
        paymentDataSource?.setShipping(shipping)
    }

    /**
     * set post url
     *
     * @param postURL the post url
     */
    fun setPostURL(postURL: String?) {
        paymentDataSource?.setPostURL(postURL)
    }

    /**
     * set payment description
     *
     * @param paymentDescription the payment description
     */
    fun setPaymentDescription(paymentDescription: String?) {
        paymentDataSource?.setPaymentDescription(paymentDescription)
    }

    /**
     * set payment meta data
     *
     * @param paymentMetadata the payment metadata
     */
     fun setPaymentMetadata(paymentMetadata: HashMap<String, String>) {
        paymentDataSource?.setPaymentMetadata(paymentMetadata)
    }

    /**
     * set payment reference
     *
     * @param paymentReference the payment reference
     */
    fun setPaymentReference(paymentReference: Reference?) {
        paymentDataSource?.setPaymentReference(paymentReference)
    }

    /**
     * set payment statement descriptor
     *
     * @param setPaymentStatementDescriptor the set payment statement descriptor
     */
    fun setPaymentStatementDescriptor(setPaymentStatementDescriptor: String?) {
        paymentDataSource?.setPaymentStatementDescriptor(setPaymentStatementDescriptor)
    }


    /**
     * enable or disable saving card.
     * @param saveCardStatus
     */
    fun isUserAllowedToSaveCard(saveCardStatus: Boolean) {
        //println("isUserAllowedToSaveCard >>> $saveCardStatus")
        paymentDataSource?.isUserAllowedToSaveCard(saveCardStatus)
    }

    /**
     * enable or disable 3dsecure
     *
     * @param is3DSecure the is 3 d secure
     */
    fun isRequires3DSecure(is3DSecure: Boolean) {
       // println("isRequires3DSecure >>> $is3DSecure")
        paymentDataSource?.isRequires3DSecure(is3DSecure)
    }

    /**
     * set payment receipt
     *
     * @param receipt the receipt
     */
    fun setReceiptSettings(receipt: Receipt?) {
        paymentDataSource?.setReceiptSettings(receipt)
    }


    /**
     * set Authorize Action
     *
     * @param authorizeAction the authorize action
     */
    fun setAuthorizeAction(authorizeAction: AuthorizeAction?) {
        paymentDataSource?.setAuthorizeAction(authorizeAction)
    }

    /**
     * set Destination
     */
    fun setDestination(destination: Destinations?) {
        paymentDataSource?.setDestination(destination)
    }

    /**
     * set Merchant ID
     * @param merchantId
     */
    fun setMerchantID(merchantId: String?) {
        if (merchantId != null && merchantId.trim { it <= ' ' }
                .isNotEmpty()) paymentDataSource?.setMerchant(
            Merchant(merchantId)
        ) else paymentDataSource?.setMerchant(null)
    }

    /**
     * set setCardType
     * @param cardType the cardType
     */
    fun setCardType(cardType: CardType) {
       // println("cardType ... $cardType")
        paymentDataSource?.setcardType(cardType)
    }

    /**
     * set default cardholderName
     *
     * @param defaultCardHolderName the default cardholderName
     */
    fun setDefaultCardHolderName(defaultCardHolderName: String?) {
        paymentDataSource?.setDefaultCardHolderName(defaultCardHolderName)
    }

    /**
     * enable or disable edit cardholdername.
     * @param enableCardHolderName
     */
    fun isUserAllowedToEnableCardHolderName(enableCardHolderName: Boolean) {
      //  println("isUserAllowedToEnableCardHolderName >>> $enableCardHolderName")
        paymentDataSource?.isUserAllowedToEditCardHolderName(enableCardHolderName)
    }

    /**
     * show or hide  cardholdername.
     * @param showHideCardHolderName
     */
    fun showHideCardHolderName(showHideCardHolderName: Boolean) {
       // println("showHideCardHolderName >>> $showHideCardHolderName")
        paymentDataSource?.showHideCardHolderName(showHideCardHolderName)
    }
    /**
     * set default sdkMode
     *
     * @param defaultCardHolderName the default cardholderName
     */
    fun setSdkMode(sdkMode: SdkMode) {
        paymentDataSource?.setSDKMode(sdkMode)
    }

    /**
     * set OrderObject
     */
    fun setOrderObject(orderObject: OrderObject) {
        paymentDataSource?.setOrder(orderObject)
    }

    /**
     * set OrderObject ItemsModel
     */
    fun setOrderItems(orderItems: ArrayList<ItemsModel>) {
        paymentDataSource?.setOrderItems(orderItems)
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

    @RequiresApi(Build.VERSION_CODES.N)
    fun setButtonView(
        payButtonView: TabAnimatedActionButton,
        context: Context,
        supportFragmentManager: FragmentManager,
        activity: Activity
    ) {

        payButtonView.isFocusableInTouchMode = true
      //  payButtonView.setOnClickListener {
           // if (sessionActive) return@setOnClickListener
            if (sessionActive){
                tabAnimatedActionButton?.changeButtonState(ActionButtonState.ERROR)
                tabAnimatedActionButton?.isEnabled= true
                return
            }

            if (PaymentDataSource?.getTransactionMode() == null) {
                checkOutDelegate?.invalidTransactionMode()
                tabAnimatedActionButton?.changeButtonState(ActionButtonState.ERROR)
                tabAnimatedActionButton?.isEnabled= true
               // return@setOnClickListener
                return
            } else {
                sessionActive = true
                startSDK(supportFragmentManager, context,activity)
                this.tabAnimatedActionButton = payButtonView
               if (tabAnimatedActionButton != null) {
                     tabAnimatedActionButton?.setDisplayMetricsTheme(CustomUtils.getDeviceDisplayMetrics(activity),CustomUtils.getCurrentTheme())
                    tabAnimatedActionButton?.changeButtonState(ActionButtonState.LOADING)
                   tabAnimatedActionButton?.isEnabled= false
                }

            }
      //  }

    }


    /**
     * call payment methods API
     */
    @RequiresApi(Build.VERSION_CODES.N)
    private fun getInitOptions(supportFragmentManager: FragmentManager?) {
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
    fun startPayment(_supportFragmentManager:FragmentManager?) {

        tabAnimatedActionButton?.changeButtonState(ActionButtonState.LOADING)
        CardViewModel().processEvent(event = CardViewEvent.InitEvent, viewModel = CheckoutViewModel(),
            supportFragmentManager = _supportFragmentManager, context = contextSDK)


    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun resetBottomSheetForButton(
        __supportFragmentManager: FragmentManager,
        context: Context,
        payButtonView: TabAnimatedActionButton,
        activity: Activity,
        status: ChargeStatus?
    ) {
        val intent = Intent(SDKSession.activity, CheckOutActivity::class.java)
        intent.putExtra("hideAllViews",true)
        intent.putExtra("status",status)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        contextSDK?.startActivity(intent)
       /* val checkoutFragment =CheckoutFragment()
        __supportFragmentManager.let { checkoutFragment.show(it,"CheckOutFragment") }
        CheckoutFragment().hideAllView = true
        if (status != null) {
            CheckoutFragment().status =status
        }*/

        sessionActive =false
        payButtonView.changeButtonState(ActionButtonState.RESET)
        payButtonView.visibility =View.GONE
        payButtonView.setButtonDataSource(
            true,
            context.let { LocalizationManager.getLocale(it).language },
            LocalizationManager.getValue("pay", "ActionButton"),
            Color.parseColor(ThemeManager.getValue("actionButton.Valid.paymentBackgroundColor")),
            Color.parseColor(ThemeManager.getValue("actionButton.Valid.titleLabelColor"))
        )

        payButtonView.setOnClickListener {
            payButtonView.changeButtonState(ActionButtonState.LOADING)
            startSDK(__supportFragmentManager,context, activity)
        }




    }



}




