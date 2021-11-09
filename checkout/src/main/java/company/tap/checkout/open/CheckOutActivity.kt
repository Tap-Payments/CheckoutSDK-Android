package company.tap.checkout.open

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import company.tap.checkout.R
import company.tap.checkout.internal.api.enums.ChargeStatus
import company.tap.checkout.internal.api.models.Authorize
import company.tap.checkout.internal.api.models.Charge
import company.tap.checkout.internal.api.models.Token
import company.tap.checkout.open.controller.SDKSession
import company.tap.checkout.open.controller.SDKSession.tabAnimatedActionButton
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

class CheckOutActivity : AppCompatActivity() ,SessionDelegate {
    private val tapCheckoutFragment = CheckoutFragment()
    var hideAllViews:Boolean = false
    lateinit var chargeStatus:ChargeStatus
    var sdkSession: SDKSession = SDKSession
    private lateinit var tapNfcCardReader: TapNfcCardReader
    private var cardReadDisposable: Disposable = Disposables.empty()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_check_out)
        val intent = intent
        val bundle = intent.extras

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
        tabAnimatedActionButton?.changeButtonState(ActionButtonState.SUCCESS)
        Handler().postDelayed({
            tabAnimatedActionButton?.changeButtonState(ActionButtonState.IDLE)
        }, 1000)

        this.finish()
    }

    override fun paymentFailed(charge: Charge?) {
        println("paymentFailed called here")
        tabAnimatedActionButton?.changeButtonState(ActionButtonState.ERROR)
        Handler().postDelayed({
            tabAnimatedActionButton?.changeButtonState(ActionButtonState.IDLE)
        }, 1000)

        this.finish()

    }

    override fun authorizationSucceed(authorize: Authorize) {
        tabAnimatedActionButton?.changeButtonState(ActionButtonState.SUCCESS)
        Handler().postDelayed({
            tabAnimatedActionButton?.changeButtonState(ActionButtonState.IDLE)
        }, 1000)

        this.finish()

    }

    override fun authorizationFailed(authorize: Authorize?) {
        tabAnimatedActionButton?.changeButtonState(ActionButtonState.ERROR)
        Handler().postDelayed({
            tabAnimatedActionButton?.changeButtonState(ActionButtonState.IDLE)
        }, 1000)

        this.finish()

    }

    override fun cardSaved(charge: Charge) {
        tabAnimatedActionButton?.changeButtonState(ActionButtonState.SUCCESS)
        Handler().postDelayed({
            tabAnimatedActionButton?.changeButtonState(ActionButtonState.IDLE)
        }, 1000)

        this.finish()

    }

    override fun cardSavingFailed(charge: Charge) {
        tabAnimatedActionButton?.changeButtonState(ActionButtonState.ERROR)
        Handler().postDelayed({
            tabAnimatedActionButton?.changeButtonState(ActionButtonState.IDLE)
        }, 1000)

        this.finish()

    }

    override fun cardTokenizedSuccessfully(token: Token) {
        tabAnimatedActionButton?.changeButtonState(ActionButtonState.SUCCESS)
        Handler().postDelayed({
            tabAnimatedActionButton?.changeButtonState(ActionButtonState.IDLE)
        }, 1000)

        this.finish()

    }

    override fun savedCardsList(cardsList: CardsList) {
        tabAnimatedActionButton?.changeButtonState(ActionButtonState.SUCCESS)
        Handler().postDelayed({
            tabAnimatedActionButton?.changeButtonState(ActionButtonState.IDLE)
        }, 1000)

        this.finish()

    }

    override fun sdkError(goSellError: GoSellError?) {
        tabAnimatedActionButton?.changeButtonState(ActionButtonState.ERROR)
        Handler().postDelayed({
            tabAnimatedActionButton?.changeButtonState(ActionButtonState.IDLE)
        }, 1000)

        this.finish()

    }

    override fun sessionIsStarting() {
    }

    override fun sessionHasStarted() {
    }

    override fun sessionCancelled() {
    }

    override fun sessionFailedToStart() {
    }

    override fun invalidCardDetails() {
        tabAnimatedActionButton?.changeButtonState(ActionButtonState.ERROR)
        Handler().postDelayed({
            tabAnimatedActionButton?.changeButtonState(ActionButtonState.IDLE)
        }, 1000)

        this.finish()


    }

    override fun backendUnknownError(message: GoSellError?) {
        tabAnimatedActionButton?.changeButtonState(ActionButtonState.ERROR)
        Handler().postDelayed({
            tabAnimatedActionButton?.changeButtonState(ActionButtonState.IDLE)
        }, 1000)
        this.finish()

    }

    override fun invalidTransactionMode() {
        tabAnimatedActionButton?.changeButtonState(ActionButtonState.ERROR)
        Handler().postDelayed({
            tabAnimatedActionButton?.changeButtonState(ActionButtonState.IDLE)
        }, 1000)

        this.finish()

    }

    override fun invalidCustomerID() {
        tabAnimatedActionButton?.changeButtonState(ActionButtonState.ERROR)
        Handler().postDelayed({
            tabAnimatedActionButton?.changeButtonState(ActionButtonState.IDLE)
        }, 1000)

        this.finish()

    }

    override fun userEnabledSaveCardOption(saveCardEnabled: Boolean) {
        tabAnimatedActionButton?.changeButtonState(ActionButtonState.SUCCESS)
        Handler().postDelayed({
            tabAnimatedActionButton?.changeButtonState(ActionButtonState.IDLE)
        }, 1000)

        this.finish()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun getStatusSDK(status: ChargeStatus?) {
        tabAnimatedActionButton?.let {
            SDKSession.resetBottomSheetForButton(supportFragmentManager, this,
                it, this, status)
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
        }else
            finish()

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




}