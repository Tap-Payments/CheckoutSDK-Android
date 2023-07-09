package company.tap.checkout.internal.viewholders

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.webkit.WebView
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import com.fasterxml.jackson.annotation.ObjectIdGenerators.StringIdGenerator
import company.tap.checkout.R
import company.tap.checkout.internal.api.models.Charge
import company.tap.checkout.internal.apiresponse.CardViewModel
import company.tap.checkout.internal.enums.SectionType
import company.tap.checkout.internal.utils.*
import company.tap.checkout.internal.viewmodels.CheckoutViewModel
import company.tap.checkout.internal.webview.CustomWebViewClientContract
import company.tap.checkout.internal.webview.TapCustomWebViewClient
import company.tap.checkout.internal.webview.WebViewContract
import company.tap.checkout.open.controller.SDKSession
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapuilibraryy.uikit.AppColorTheme
import company.tap.tapuilibraryy.uikit.atoms.TapTextViewNew
import company.tap.tapuilibraryy.uikit.ktx.loadAppThemManagerFromPath
import kotlinx.android.synthetic.main.fragment_web.*
import kotlinx.android.synthetic.main.switch_layout.view.*
import kotlin.math.roundToInt


@RequiresApi(Build.VERSION_CODES.N)
@SuppressLint("UseCompatLoadingForDrawables")
class WebViewHolder(
    val context: Context,
    var redirectURL: String?=null,
    val webViewContract: WebViewContract,
    val cardViewModel: CardViewModel,
    var authenticate: Charge?=null,
    val checkoutViewModel: CheckoutViewModel,
    val bottomSheetLayout: FrameLayout,
    val sdkLayout: LinearLayout,
    val switchViewHolder: SwitchViewHolder?,
    val paymentInlineViewHolder: PaymentInlineViewHolder,
    val cardViewHolder: CardViewHolder
) : TapBaseViewHolder, CustomWebViewClientContract {
    override val view: View = LayoutInflater.from(context).inflate(R.layout.web_view_layout, null)
    override val type = SectionType.FRAGMENT
    val web_view by lazy { view.findViewById<WebView>(R.id.web_view) }
    val webViewTextTitle by lazy { view.findViewById<TapTextViewNew>(R.id.webView_Header) }
    val webViewLinear by lazy { view.findViewById<LinearLayout>(R.id.webViewLinear) }
    val topLinear by lazy { view.findViewById<LinearLayout>(R.id.topLinear1) }
    val webCardview by lazy { view.findViewById<CardView>(R.id.webCardview) }

    init {
        webViewLinear?.setBackgroundColor(loadAppThemManagerFromPath(AppColorTheme.GlobalValuesColor))
        if (TextUtils.isEmpty(redirectURL)) {
            throw IllegalArgumentException("Empty URL passed to WebViewFragment!")
        }
        val webViewTitleText: String =
            LocalizationManager.getValue("GatewayHeader", "HorizontalHeaders", "webViewTitle")
        webViewTextTitle.text = webViewTitleText
        setUpWebView()
    }

    fun setUrlAndCharge(url:String?,authenticateCharge:Charge?){
        this.authenticate = authenticateCharge
        this.redirectURL = url
    }

    override fun bindViewComponents() {

    }

    override fun submitResponseStatus(success: Boolean) {
    }

    override fun getRedirectedURL(url: String) {
        if (url.contains("gosellsdk://return_url")) {
            // webViewContract?.resultObtained(true, contextSDK)
            webViewContract.redirectLoadingFinished(
                url.contains("gosellsdk://return_url"), authenticate,
                SDKSession.contextSDK
            )
        }
    }

    override fun showLoading(showLoading: Boolean) {

    }

    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("SetJavaScriptEnabled")
    private fun setUpWebView() {
        web_view.webViewClient = cardViewModel.let { TapCustomWebViewClient(this, it, checkoutViewModel) }
        checkoutViewModel.isWebViewHolderFor3dsOpened.value = true

        web_view.applyConfigurationForWebView(
            url = redirectURL.toString(),
            onProgressWebViewFinishedLoading = {
                mutableListOf(
                    paymentInlineViewHolder.view,
                    switchViewHolder?.view!!,
                    cardViewHolder.view
                ).addFadeOutAnimationToViews {
                    web_view.layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        context.twoThirdHeightView().roundToInt()
                    )
                    showViewsRelatedToWebView()

                }


            })


    }
    fun destroyWebView(){
        web_view.destroy()
    }
    private fun showViewsRelatedToWebView() {

        animateBS(
            fromView = bottomSheetLayout,
            toView = sdkLayout,
            transitionAnimation = 1000L,
            changeHeight = {
                topLinear.visibility = View.VISIBLE
                web_view.visibility = View.VISIBLE
                webCardview.visibility = View.VISIBLE
            })

    }

}