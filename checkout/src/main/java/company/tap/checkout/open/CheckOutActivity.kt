package company.tap.checkout.open

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import company.tap.checkout.R
import company.tap.checkout.internal.api.enums.ChargeStatus
import company.tap.checkout.internal.api.models.Authorize
import company.tap.checkout.internal.api.models.Charge
import company.tap.checkout.internal.api.models.Token
import company.tap.checkout.open.controller.SDKSession.tabAnimatedActionButton
import company.tap.checkout.open.interfaces.SessionDelegate
import company.tap.checkout.open.models.CardsList
import company.tap.tapnetworkkit.exception.GoSellError
import company.tap.tapuilibrary.uikit.enums.ActionButtonState

class CheckOutActivity : AppCompatActivity() ,SessionDelegate {
    private val tapCheckoutFragment = CheckoutFragment()
    var hideAllViews:Boolean = false
   lateinit var chargeStatus:ChargeStatus
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       setContentView(R.layout.activity_check_out)
        val intent = intent
        val bundle = intent.extras

        if (bundle != null) {
            hideAllViews = bundle["hideAllViews"] as Boolean? == true
           chargeStatus = bundle["status"] as ChargeStatus
        }
        tapCheckoutFragment.hideAllView =hideAllViews
        tapCheckoutFragment.checkOutActivity = this
        if(::chargeStatus.isInitialized)
        tapCheckoutFragment.status =chargeStatus
        tapCheckoutFragment.show(
            supportFragmentManager.beginTransaction().addToBackStack(null),
            "CheckOutFragment"
        )
    }


    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        tapCheckoutFragment.handleNFCResult(intent)

    }

    override fun paymentSucceed(charge: Charge) {
     //  tapCheckoutFragment.dialog?.dismiss()

        tabAnimatedActionButton?.changeButtonState(ActionButtonState.SUCCESS)
        this.finish()
    }

    override fun paymentFailed(charge: Charge?) {
        println("paymentFailed called here")
        tabAnimatedActionButton?.changeButtonState(ActionButtonState.ERROR)
        this.finish()

    }

    override fun authorizationSucceed(authorize: Authorize) {
        tabAnimatedActionButton?.changeButtonState(ActionButtonState.SUCCESS)
        this.finish()

    }

    override fun authorizationFailed(authorize: Authorize?) {
        tabAnimatedActionButton?.changeButtonState(ActionButtonState.ERROR)

    }

    override fun cardSaved(charge: Charge) {
        tabAnimatedActionButton?.changeButtonState(ActionButtonState.SUCCESS)

    }

    override fun cardSavingFailed(charge: Charge) {
        tabAnimatedActionButton?.changeButtonState(ActionButtonState.ERROR)

    }

    override fun cardTokenizedSuccessfully(token: Token) {
        tabAnimatedActionButton?.changeButtonState(ActionButtonState.SUCCESS)

    }

    override fun savedCardsList(cardsList: CardsList) {
        tabAnimatedActionButton?.changeButtonState(ActionButtonState.SUCCESS)

    }

    override fun sdkError(goSellError: GoSellError?) {
        tabAnimatedActionButton?.changeButtonState(ActionButtonState.ERROR)

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


    }

    override fun backendUnknownError(message: String?) {
        tabAnimatedActionButton?.changeButtonState(ActionButtonState.ERROR)

    }

    override fun invalidTransactionMode() {
        tabAnimatedActionButton?.changeButtonState(ActionButtonState.ERROR)

    }

    override fun invalidCustomerID() {
        tabAnimatedActionButton?.changeButtonState(ActionButtonState.ERROR)

    }

    override fun userEnabledSaveCardOption(saveCardEnabled: Boolean) {
        tabAnimatedActionButton?.changeButtonState(ActionButtonState.SUCCESS)

    }

    override fun getStatusSDK(status: ChargeStatus?) {
       /* tabAnimatedActionButton?.let {
            SDKSession.resetBottomSheetForButton(supportFragmentManager, this,
                it, this, status)
        }
        tabAnimatedActionButton?.visibility = View.VISIBLE*/
    }

    override fun onStop() {
        super.onStop()
        this.finish()
    }
}