package company.tap.checkout.open

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.bugfender.sdk.Bugfender
import com.bumptech.glide.Glide
import com.google.android.gms.wallet.AutoResolveHelper
import com.google.android.gms.wallet.PaymentData
import com.google.android.gms.wallet.PaymentDataRequest
import com.google.android.gms.wallet.PaymentsClient
import company.tap.checkout.R
import company.tap.checkout.internal.api.enums.ChargeStatus
import company.tap.checkout.internal.api.enums.LogsModel
import company.tap.checkout.internal.api.models.PaymentOption
import company.tap.checkout.internal.utils.CustomUtils
import company.tap.checkout.internal.utils.PaymentsUtil
import company.tap.checkout.open.controller.SDKSession
import company.tap.checkout.open.controller.SDKSession.tabAnimatedActionButton
import company.tap.checkout.open.data_managers.PaymentDataSource
import company.tap.nfcreader.open.reader.TapEmvCard
import company.tap.nfcreader.open.reader.TapNfcCardReader
import company.tap.tapnetworkkit.interfaces.APILoggInterface
import company.tap.tapuilibraryy.uikit.models.DialogConfigurations
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables
import kotlinx.android.synthetic.main.googlepay_button.*
import org.json.JSONObject

class CheckOutActivity : AppCompatActivity(), APILoggInterface {
    private val tapCheckoutFragment = CheckoutFragment()
    var hideAllViews: Boolean = false
    lateinit var chargeStatus: ChargeStatus
    var sdkSession: SDKSession = SDKSession
    private lateinit var tapNfcCardReader: TapNfcCardReader
    private lateinit var selectedPaymentOption: PaymentOption
    private var cardReadDisposable: Disposable = Disposables.empty()

    @JvmField
    // Arbitrarily-picked constant integer you define to track a request for payment data activity.
    val LOAD_PAYMENT_DATA_REQUEST_CODE = 991


    lateinit var _paymentsClient: PaymentsClient

    @JvmField
    var displayMetrics: Int? = 0

    @JvmField
    var isGooglePayClicked: Boolean = false

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_check_out)
        val intent = intent
        val bundle = intent.extras
        displayMetrics = CustomUtils.getDeviceDisplayMetrics(this)
        if (bundle != null) {
            if (bundle["hideAllViews"] != null) {
                hideAllViews = bundle["hideAllViews"] as Boolean? == true

            }
            if (bundle["status"] != null) {
                chargeStatus = bundle["status"] as ChargeStatus
            }
        }
        // sdkSession.addSessionDelegate(this)
        tapCheckoutFragment.hideAllView = hideAllViews
        tapCheckoutFragment.checkOutActivity = this
        if (::chargeStatus.isInitialized)
            tapCheckoutFragment.status = chargeStatus
        tapCheckoutFragment.show(
            supportFragmentManager.beginTransaction().addToBackStack(null),
            "CheckOutFragment"
        )

        tapCheckoutFragment.arguments = getArguments()
        tapNfcCardReader = TapNfcCardReader(this)


    }

    private fun getArguments(): Bundle {
        val arguments = Bundle()
        if (displayMetrics == DisplayMetrics.DENSITY_450 || displayMetrics == DisplayMetrics.DENSITY_420 || displayMetrics == DisplayMetrics.DENSITY_400 || displayMetrics == DisplayMetrics.DENSITY_440 || displayMetrics == DisplayMetrics.DENSITY_XXHIGH) {

            arguments.putFloatArray(DialogConfigurations.Corners, floatArrayOf(25f, 25f, 0f, 0f))
        } else if (displayMetrics == DisplayMetrics.DENSITY_280 || displayMetrics == DisplayMetrics.DENSITY_260 || displayMetrics == DisplayMetrics.DENSITY_300 || displayMetrics == DisplayMetrics.DENSITY_XHIGH || displayMetrics == DisplayMetrics.DENSITY_340 || displayMetrics == DisplayMetrics.DENSITY_360) {
            arguments.putFloatArray(DialogConfigurations.Corners, floatArrayOf(5f, 5f, 0f, 0f))
        }

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


    override fun onBackPressed() {
        super.onBackPressed()
        if (isDestroyed) {
            Glide.with(this).pauseRequests()
        }
        finish()
    }

    override fun onPause() {
        super.onPause()
        if (tapCheckoutFragment.isNfcOpened) {
            cardReadDisposable.dispose()
            tapNfcCardReader.disableDispatch()
        } else if (!isGooglePayClicked) {
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
                        tapCheckoutFragment.viewModel?.handleNFCScannedResult(emvCard)
                        println("emvCard$emvCard")
                    }
                },
                    { throwable -> throwable.message?.let { println("error is nfc" + throwable.printStackTrace()) } })
        }

    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        println("<<<<onActivityResult>>>" + resultCode)
        when (requestCode) {
            LOAD_PAYMENT_DATA_REQUEST_CODE -> {
                when (resultCode) {
                    RESULT_OK -> {
                        val paymentData = data?.let { PaymentData.getFromIntent(it) }
                        if (paymentData != null) {
                            tapCheckoutFragment.viewModel?.handlePaymentSuccess(
                                paymentData,
                                selectedPaymentOption
                            )
                        } else {
                            AutoResolveHelper.getStatusFromIntent(data)?.statusCode?.let {
                                tapCheckoutFragment.viewModel?.handleError(
                                    it
                                )
                            }
                        }

                        isGooglePayClicked = false

                    }
                    RESULT_CANCELED -> {
                        tapCheckoutFragment.viewModel?.handleSuccessFailureResponseButton(
                            "Cancelled Google Pay",
                            null,
                            null,
                            tabAnimatedActionButton,
                            SDKSession.contextSDK
                        )
                        isGooglePayClicked = false

                    }
                    AutoResolveHelper.RESULT_ERROR -> {
                        val status = AutoResolveHelper.getStatusFromIntent(data)
                        if (status != null) println(if ("status values are>>$status" != null) status.statusMessage else status.toString() + " >> code " + status.statusCode)
                        tapCheckoutFragment.viewModel?.handleError(status?.statusCode ?: 400)
                        isGooglePayClicked = false

                    }

                }
            }
        }
    }

    fun handleGooglePayApiCall(paymentOption: PaymentOption) {
        // Disables the button to prevent multiple clicks.
        googlePayButton?.isClickable = false
        tapCheckoutFragment.viewModel?.changeButtonToLoading()
        // assert(PaymentDataSource.getInstance().getAmount() != null)
        _paymentsClient = PaymentsUtil.createPaymentsClient(this)
        isGooglePayClicked = true
        val paymentDataRequestJson: JSONObject? = PaymentDataSource.getSelectedAmount()?.toLong()
            ?.let { PaymentsUtil.getPaymentDataRequest(it) }
        if (paymentDataRequestJson == null) {
            Log.e("RequestPayment", "Can't fetch payment data request")
            return
        }
        selectedPaymentOption = paymentOption
        val request = PaymentDataRequest.fromJson(paymentDataRequestJson.toString())
        println("request value is>>>" + request.toJson())
        println("Activity is>>>" + this as Activity)

        // Since loadPaymentData may show the UI asking the user to select a payment method, we use
        // AutoResolveHelper to wait for the user interacting with it. Once completed,
        // onActivityResult will be called with the result.
        if (request != null) {
            AutoResolveHelper.resolveTask(
                _paymentsClient.loadPaymentData(request), this, LOAD_PAYMENT_DATA_REQUEST_CODE
            )
        }
    }

    override fun onLoggingEvent(logs: String?) {
        Bugfender.d(LogsModel.API.name, logs)
        println("logs" + logs)
    }


}
