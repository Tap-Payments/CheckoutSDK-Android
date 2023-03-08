package company.tap.checkout.internal.viewholders

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.text.TextUtils
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import company.tap.checkout.R
import company.tap.checkout.internal.api.models.Charge
import company.tap.checkout.internal.apiresponse.CardViewModel
import company.tap.checkout.internal.enums.SectionType
import company.tap.checkout.internal.webview.CustomWebViewClientContract
import company.tap.checkout.internal.webview.TapCustomWebViewClient
import company.tap.checkout.internal.webview.WebFragment
import company.tap.checkout.internal.webview.WebViewContract
import company.tap.checkout.open.controller.SDKSession
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapuilibrary.themekit.ThemeManager
import company.tap.tapuilibrary.uikit.atoms.TapTextView
import company.tap.tapuilibrary.uikit.organisms.TapLoyaltyView
import kotlinx.android.synthetic.main.fragment_web.*

class WebViewHolder(
    private val context: Context,
    private val fragmentManager: FragmentManager,
    val redirectURL: String, val webViewContract: WebViewContract,
    val cardViewModel: CardViewModel,
    val authenticate: Charge?
) :TapBaseViewHolder , CustomWebViewClientContract {
    override val view: View = LayoutInflater.from(context).inflate(R.layout.web_view_layout, null)

    override val type = SectionType.FRAGMENT

   // private var webViewUrl: String? = null
    //private var chargeResponse: Charge? = null
    val progressBar by lazy { view.findViewById<ProgressBar>(R.id.progressBar) }
    val web_view by lazy { view.findViewById<WebView>(R.id.web_view) }
    val webViewTextTitle by lazy { view.findViewById<TapTextView>(R.id.webView_Header) }
    val webViewLinear by lazy { view.findViewById<LinearLayout>(R.id.webViewLinear) }


    init {
        progressBar?.setBackgroundColor(Color.parseColor(ThemeManager.getValue("merchantHeaderView.backgroundColor")))
        webViewLinear?.setBackgroundColor(Color.parseColor(ThemeManager.getValue("GlobalValues.Colors.whiteTwo")))
        progressBar?.progressDrawable?.setColorFilter(
            Color.RED, android.graphics.PorterDuff.Mode.SRC_IN
        );
        progressBar?.progressTintList = ColorStateList.valueOf(Color.RED)

        // webViewUrl = arguments?.getString(WebFragment.KEY_URL)
        //  webViewUrl = redirectURL
        //  chargeResponse = authenticate
        progressBar?.max = 100
        progressBar?.progress = 10
        if (TextUtils.isEmpty(redirectURL)) {
            throw IllegalArgumentException("Empty URL passed to WebViewFragment!")
        }
        val webViewTitleText :String= LocalizationManager.getValue("GatewayHeader", "HorizontalHeaders", "webViewTitle")
        webViewTextTitle.text = webViewTitleText
        println("redirectURL in holdre"+redirectURL)
        setUpWebView(redirectURL)
    }
    override fun bindViewComponents() {

    }

    override fun submitResponseStatus(success: Boolean) {
        webViewContract.redirectLoadingFinished(success, authenticate, SDKSession.contextSDK)
    }

    override fun getRedirectedURL(url: String) {

    }

    override fun showLoading(showLoading: Boolean) {

    }
    @SuppressLint("SetJavaScriptEnabled")
    private fun setUpWebView(mUrl: String) {
        web_view.settings.javaScriptEnabled = true
        progressBar?.visibility = View.VISIBLE
        web_view.setVisibility(View.INVISIBLE)
        web_view.webChromeClient = object : WebChromeClient() {
            /*
                    public void onProgressChanged (WebView view, int newProgress)
                        Tell the host application the current progress of loading a page.

                    Parameters
                        view : The WebView that initiated the callback.
                        newProgress : Current page loading progress, represented by an integer
                            between 0 and 100.
                */
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                // Update the progress bar with page loading progress
                progressBar?.progress = newProgress
                println("progressBar?.progress"+progressBar?.progress)
                println("progressBar?.progress"+newProgress)
                if (newProgress == 100) {
                     // Hide the progressbar
                     progressBar?.visibility = View.GONE
                    web_view.setVisibility(View.VISIBLE)
                 }

            }
        }
        web_view.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        web_view.webViewClient = cardViewModel?.let { TapCustomWebViewClient(this, it) }!!
        web_view.settings.loadWithOverviewMode = true
       // web_view.scrollBarStyle = View.
        web_view.isVerticalScrollBarEnabled = true
        web_view.isHorizontalScrollBarEnabled = true
        web_view.settings.useWideViewPort = true
        println("redirectURL val is>>"+mUrl)
        redirectURL?.let { web_view.loadUrl(it) }
        web_view.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                if (web_view.canGoBack()) {
                    web_view.goBack()
                    /**
                     * put here listener or delegate thT process cancelled **/
                    return@setOnKeyListener true
                }
                return@setOnKeyListener false
            }
            false
        }




    }

}