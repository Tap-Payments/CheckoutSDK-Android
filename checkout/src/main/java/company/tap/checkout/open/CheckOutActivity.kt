package company.tap.checkout.open

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.wallet.AutoResolveHelper
import com.google.android.gms.wallet.PaymentData
import com.google.android.gms.wallet.PaymentDataRequest
import com.google.android.gms.wallet.PaymentsClient
import company.tap.checkout.R
import company.tap.checkout.internal.api.enums.ChargeStatus
import company.tap.checkout.internal.api.models.Authorize
import company.tap.checkout.internal.api.models.Charge
import company.tap.checkout.internal.api.models.Token
import company.tap.checkout.internal.utils.CustomUtils
import company.tap.checkout.internal.utils.PaymentsUtil
import company.tap.checkout.internal.viewmodels.CheckoutViewModel
import company.tap.checkout.open.controller.SDKSession
import company.tap.checkout.open.controller.SDKSession.tabAnimatedActionButton
import company.tap.checkout.open.data_managers.PaymentDataSource
import company.tap.checkout.open.enums.SdkIdentifier
import company.tap.checkout.open.interfaces.SessionDelegate
import company.tap.checkout.open.models.CardsList
import company.tap.nfcreader.open.reader.TapEmvCard
import company.tap.nfcreader.open.reader.TapNfcCardReader
import company.tap.tapnetworkkit.exception.GoSellError
import company.tap.tapuilibrary.uikit.enums.ActionButtonState
import company.tap.tapuilibrary.uikit.models.DialogConfigurations
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables
import kotlinx.android.synthetic.main.item_googlepay.*
import org.json.JSONObject

class CheckOutActivity : AppCompatActivity() ,SessionDelegate {
    private val tapCheckoutFragment = CheckoutFragment()
    var hideAllViews:Boolean = false
    lateinit var chargeStatus:ChargeStatus
    var sdkSession: SDKSession = SDKSession
    private lateinit var tapNfcCardReader: TapNfcCardReader
    private var cardReadDisposable: Disposable = Disposables.empty()
    @JvmField
    // Arbitrarily-picked constant integer you define to track a request for payment data activity.
    val LOAD_PAYMENT_DATA_REQUEST_CODE = 991

    lateinit var _paymentsClient: PaymentsClient
    @JvmField
     var displayMetrics: Int?=0
    @JvmField
    var isGooglePayClicked:Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_check_out)
        val intent = intent
        val bundle = intent.extras
        displayMetrics= CustomUtils.getDeviceDisplayMetrics(this)
        if (bundle != null) {
            if(bundle["hideAllViews"]!=null){
                hideAllViews = bundle["hideAllViews"] as Boolean? == true

            }
            if( bundle["status"]!=null){
                chargeStatus = bundle["status"] as ChargeStatus
            }
        }
        sdkSession.addSessionDelegate(this)
        tapCheckoutFragment.hideAllView =hideAllViews
        tapCheckoutFragment.checkOutActivity = this
        if(::chargeStatus.isInitialized)
            tapCheckoutFragment.status =chargeStatus
        tapCheckoutFragment.show(
            supportFragmentManager.beginTransaction().addToBackStack(null),
            "CheckOutFragment"
        )
        tapCheckoutFragment.arguments=getArguments()
        tapNfcCardReader = TapNfcCardReader(this)

    }

    private fun getArguments(): Bundle {
        val arguments = Bundle()
        arguments.putFloatArray(DialogConfigurations.Corners, floatArrayOf(25f, 25f, 0f, 0f))
        arguments.putInt(DialogConfigurations.Color, Color.TRANSPARENT)
        arguments.putBoolean(DialogConfigurations.Cancelable, false)
        arguments.putFloat(DialogConfigurations.Dim, 0.75f)
        return arguments
    }


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleNFCResult(intent)

    }

    override fun paymentSucceed(charge: Charge) {
        if(sdkSession.sdkIdentifier!=null && (SDKSession.sdkIdentifier ==SdkIdentifier.Flutter.name|| SDKSession.sdkIdentifier ==SdkIdentifier.ReactNative.name)){
            SDKSession.getPluginListener()?.paymentSucceed(charge)
            this.finish()
        }else {
            tabAnimatedActionButton?.changeButtonState(ActionButtonState.SUCCESS)
            Handler().postDelayed({
                tabAnimatedActionButton?.changeButtonState(ActionButtonState.IDLE)
            }, 1000)

            this.finish()
        }
    }

    override fun paymentFailed(charge: Charge?) {
        if(sdkSession.sdkIdentifier!=null && (SDKSession.sdkIdentifier ==SdkIdentifier.Flutter.name|| SDKSession.sdkIdentifier ==SdkIdentifier.ReactNative.name)){
            SDKSession.getPluginListener()?.paymentFailed(charge)
            this.finish()
        }else {
            println("paymentFailed called here")
            tabAnimatedActionButton?.changeButtonState(ActionButtonState.ERROR)
            Handler().postDelayed({
                tabAnimatedActionButton?.changeButtonState(ActionButtonState.IDLE)
            }, 1000)

            this.finish()
        }
    }

    override fun authorizationSucceed(authorize: Authorize) {
        if(sdkSession.sdkIdentifier!=null && (SDKSession.sdkIdentifier ==SdkIdentifier.Flutter.name|| SDKSession.sdkIdentifier ==SdkIdentifier.ReactNative.name)){
            SDKSession.getPluginListener()?.authorizationSucceed(authorize)
            this.finish()
        }else {
            tabAnimatedActionButton?.changeButtonState(ActionButtonState.SUCCESS)
            Handler().postDelayed({
                tabAnimatedActionButton?.changeButtonState(ActionButtonState.IDLE)
            }, 1000)

            this.finish()
        }
    }

    override fun authorizationFailed(authorize: Authorize?) {
        if(sdkSession.sdkIdentifier!=null && (SDKSession.sdkIdentifier ==SdkIdentifier.Flutter.name|| SDKSession.sdkIdentifier ==SdkIdentifier.ReactNative.name)){
            SDKSession.getPluginListener()?.authorizationFailed(authorize)
            this.finish()
        }else {
            tabAnimatedActionButton?.changeButtonState(ActionButtonState.ERROR)
            Handler().postDelayed({
                tabAnimatedActionButton?.changeButtonState(ActionButtonState.IDLE)
            }, 1000)

            this.finish()
        }

    }

    override fun cardSaved(charge: Charge) {
        if(sdkSession.sdkIdentifier!=null && (SDKSession.sdkIdentifier ==SdkIdentifier.Flutter.name|| SDKSession.sdkIdentifier ==SdkIdentifier.ReactNative.name)){
            SDKSession.getPluginListener()?.cardSaved(charge)
            this.finish()
        }else {
            tabAnimatedActionButton?.changeButtonState(ActionButtonState.SUCCESS)
            Handler().postDelayed({
                tabAnimatedActionButton?.changeButtonState(ActionButtonState.IDLE)
            }, 1000)

            this.finish()
        }
    }

    override fun cardSavingFailed(charge: Charge) {
        if(sdkSession.sdkIdentifier!=null && (SDKSession.sdkIdentifier ==SdkIdentifier.Flutter.name|| SDKSession.sdkIdentifier ==SdkIdentifier.ReactNative.name)){
            SDKSession.getPluginListener()?.cardSavingFailed(charge)
            this.finish()
        }else {
            tabAnimatedActionButton?.changeButtonState(ActionButtonState.ERROR)
            Handler().postDelayed({
                tabAnimatedActionButton?.changeButtonState(ActionButtonState.IDLE)
            }, 1000)

            this.finish()
        }
    }

    override fun cardTokenizedSuccessfully(token: Token) {
        if(sdkSession.sdkIdentifier!=null && (SDKSession.sdkIdentifier ==SdkIdentifier.Flutter.name|| SDKSession.sdkIdentifier ==SdkIdentifier.ReactNative.name)){
            SDKSession.getPluginListener()?.cardTokenizedSuccessfully(token)
            this.finish()
        }else {
            tabAnimatedActionButton?.changeButtonState(ActionButtonState.SUCCESS)
            Handler().postDelayed({
                tabAnimatedActionButton?.changeButtonState(ActionButtonState.IDLE)
            }, 1000)

            this.finish()
        }
    }

    override fun savedCardsList(cardsList: CardsList) {
        if(sdkSession.sdkIdentifier!=null && (SDKSession.sdkIdentifier ==SdkIdentifier.Flutter.name|| SDKSession.sdkIdentifier ==SdkIdentifier.ReactNative.name)){
            SDKSession.getPluginListener()?.savedCardsList(cardsList)
            this.finish()
        }else {
            tabAnimatedActionButton?.changeButtonState(ActionButtonState.SUCCESS)
            Handler().postDelayed({
                tabAnimatedActionButton?.changeButtonState(ActionButtonState.IDLE)
            }, 1000)

            this.finish()
        }

    }

    override fun sdkError(goSellError: GoSellError?) {
        if(sdkSession.sdkIdentifier!=null && (SDKSession.sdkIdentifier ==SdkIdentifier.Flutter.name|| SDKSession.sdkIdentifier ==SdkIdentifier.ReactNative.name)){
            SDKSession.getPluginListener()?.sdkError(goSellError)
            this.finish()
        }else {
            tabAnimatedActionButton?.changeButtonState(ActionButtonState.ERROR)
            Handler().postDelayed({
                tabAnimatedActionButton?.changeButtonState(ActionButtonState.IDLE)
            }, 1000)

            this.finish()
        }

    }

    override fun sessionIsStarting() {
        if(sdkSession.sdkIdentifier!=null && (SDKSession.sdkIdentifier ==SdkIdentifier.Flutter.name|| SDKSession.sdkIdentifier ==SdkIdentifier.ReactNative.name)){
            SDKSession.getPluginListener()?.sessionIsStarting()
        }
    }

    override fun sessionHasStarted() {
        if(sdkSession.sdkIdentifier!=null && (SDKSession.sdkIdentifier ==SdkIdentifier.Flutter.name|| SDKSession.sdkIdentifier ==SdkIdentifier.ReactNative.name)){
            SDKSession.getPluginListener()?.sessionHasStarted()
        }
    }

    override fun sessionCancelled() {
        if(sdkSession.sdkIdentifier!=null && (SDKSession.sdkIdentifier ==SdkIdentifier.Flutter.name|| SDKSession.sdkIdentifier ==SdkIdentifier.ReactNative.name)){
            SDKSession.getPluginListener()?.sessionCancelled()
        }
    }

    override fun sessionFailedToStart() {
        if(sdkSession.sdkIdentifier!=null && (SDKSession.sdkIdentifier ==SdkIdentifier.Flutter.name|| SDKSession.sdkIdentifier ==SdkIdentifier.ReactNative.name)){
            SDKSession.getPluginListener()?.sessionFailedToStart()
        }
    }

    override fun invalidCardDetails() {
        if(sdkSession.sdkIdentifier!=null && (SDKSession.sdkIdentifier ==SdkIdentifier.Flutter.name|| SDKSession.sdkIdentifier ==SdkIdentifier.ReactNative.name)){
            SDKSession.getPluginListener()?.invalidCardDetails()
            this.finish()
        }else {
            tabAnimatedActionButton?.changeButtonState(ActionButtonState.ERROR)
            Handler().postDelayed({
                tabAnimatedActionButton?.changeButtonState(ActionButtonState.IDLE)
            }, 1000)

            this.finish()
        }

    }

    override fun backendUnknownError(message: GoSellError?) {
        if(sdkSession.sdkIdentifier!=null && (SDKSession.sdkIdentifier ==SdkIdentifier.Flutter.name|| SDKSession.sdkIdentifier ==SdkIdentifier.ReactNative.name)){
            SDKSession.getPluginListener()?.backendUnknownError(message)
            this.finish()
        }else {
            tabAnimatedActionButton?.changeButtonState(ActionButtonState.ERROR)
            Handler().postDelayed({
                tabAnimatedActionButton?.changeButtonState(ActionButtonState.IDLE)
            }, 1000)
            this.finish()
        }

    }

    override fun invalidTransactionMode() {
        if(sdkSession.sdkIdentifier!=null && (SDKSession.sdkIdentifier ==SdkIdentifier.Flutter.name|| SDKSession.sdkIdentifier ==SdkIdentifier.ReactNative.name)){
            SDKSession.getPluginListener()?.invalidTransactionMode()
            this.finish()
        }else {
            tabAnimatedActionButton?.changeButtonState(ActionButtonState.ERROR)
            Handler().postDelayed({
                tabAnimatedActionButton?.changeButtonState(ActionButtonState.IDLE)
            }, 1000)

            this.finish()
        }

    }

    override fun invalidCustomerID() {
        if(sdkSession.sdkIdentifier!=null && (SDKSession.sdkIdentifier ==SdkIdentifier.Flutter.name|| SDKSession.sdkIdentifier ==SdkIdentifier.ReactNative.name)){
            SDKSession.getPluginListener()?.invalidCustomerID()
            this.finish()
        }else {
            tabAnimatedActionButton?.changeButtonState(ActionButtonState.ERROR)
            Handler().postDelayed({
                tabAnimatedActionButton?.changeButtonState(ActionButtonState.IDLE)
            }, 1000)

            this.finish()
        }

    }

    override fun userEnabledSaveCardOption(saveCardEnabled: Boolean) {
        if(sdkSession.sdkIdentifier!=null && (SDKSession.sdkIdentifier ==SdkIdentifier.Flutter.name|| SDKSession.sdkIdentifier ==SdkIdentifier.ReactNative.name)){
            SDKSession.getPluginListener()?.userEnabledSaveCardOption(saveCardEnabled)
            this.finish()
        }else {
            tabAnimatedActionButton?.changeButtonState(ActionButtonState.SUCCESS)
            Handler().postDelayed({
                tabAnimatedActionButton?.changeButtonState(ActionButtonState.IDLE)
            }, 1000)

            this.finish()
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun getStatusSDK(response:String? ,charge: Charge?) {
        println("tabAnimatedActionButton is"+tabAnimatedActionButton)
        tabAnimatedActionButton?.let {
            SDKSession.resetBottomSheetForButton(
                supportFragmentManager, this,
                it, this, charge?.status
            )
        }
        tabAnimatedActionButton?.visibility = View.VISIBLE
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onPause() {
        super.onPause()
        if(tapCheckoutFragment.isNfcOpened){
            cardReadDisposable.dispose()
            tapNfcCardReader.disableDispatch()
        }
        else if(!isGooglePayClicked){
             finish()
        }
//changed above condition ELSE of simply finish to check gpay and finish , otherwise it ws not calling onactivity result

    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun handleNFCResult(intent: Intent?) {
        if (tapNfcCardReader.isSuitableIntent(intent)) {
            cardReadDisposable = tapNfcCardReader
                .readCardRx2(intent)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ emvCard: TapEmvCard? ->
                    if (emvCard != null) {
                        tapCheckoutFragment._viewModel?.handleNFCScannedResult(emvCard)
                        println("emvCard$emvCard")
                    }
                },
                    { throwable -> throwable.message?.let { println("error is nfc" + throwable.printStackTrace()) } })
        }

    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onActivityResult(requestCode:Int, resultCode:Int, data:Intent?){
        super.onActivityResult(requestCode, resultCode, data)
        println("<<<<onActivityResult>>>"+resultCode)
        when (requestCode) {
            LOAD_PAYMENT_DATA_REQUEST_CODE -> {
                when (resultCode) {
                    RESULT_OK -> {
                        val paymentData = data?.let { PaymentData.getFromIntent(it) }
                        if (paymentData != null) {
                            tapCheckoutFragment._viewModel?.handlePaymentSuccess(paymentData)
                        }else {
                            AutoResolveHelper.getStatusFromIntent(data)?.statusCode?.let { tapCheckoutFragment._viewModel?.handleError(it) }
                        }

                        isGooglePayClicked = false

                    }
                    RESULT_CANCELED -> {
                        tapCheckoutFragment._viewModel?.handleSuccessFailureResponseButton("Cancelled Google Pay",null,null)
                        isGooglePayClicked = false

                    }
                    AutoResolveHelper.RESULT_ERROR -> {
                        val status = AutoResolveHelper.getStatusFromIntent(data)
                        if (status != null) println(if ("status values are>>$status" != null) status.statusMessage else status.toString() + " >> code " + status.statusCode)
                        tapCheckoutFragment._viewModel?.handleError(status?.statusCode ?: 400)
                        isGooglePayClicked = false

                    }

                }
            }
        }
    }

    fun handleGooglePayApiCall(){
        // Disables the button to prevent multiple clicks.
          googlePayButton?.isClickable = false
        // assert(PaymentDataSource.getInstance().getAmount() != null)
        _paymentsClient = PaymentsUtil.createPaymentsClient(this)
        isGooglePayClicked = true
        val paymentDataRequestJson: JSONObject? = PaymentDataSource.getSelectedAmount()?.toLong()?.let { PaymentsUtil.getPaymentDataRequest(it) }
        if (paymentDataRequestJson == null) {
            Log.e("RequestPayment", "Can't fetch payment data request")
            return
        }

        val request = PaymentDataRequest.fromJson(paymentDataRequestJson.toString())
        println("request value is>>>" + request.toJson())
        println("Activity is>>>" + this as Activity)

        // Since loadPaymentData may show the UI asking the user to select a payment method, we use
        // AutoResolveHelper to wait for the user interacting with it. Once completed,
        // onActivityResult will be called with the result.
        if (request != null) {
            AutoResolveHelper.resolveTask(
                    _paymentsClient.loadPaymentData(request), this, LOAD_PAYMENT_DATA_REQUEST_CODE)
        }
    }

    }
