package company.tap.checkout.internal.viewholders

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.DisplayMetrics
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentManager
import androidx.transition.ChangeBounds
import androidx.transition.Transition
import androidx.transition.TransitionManager
import company.tap.checkout.R
import company.tap.checkout.internal.api.models.Charge
import company.tap.checkout.internal.apiresponse.CardViewModel
import company.tap.checkout.internal.enums.SectionType
import company.tap.checkout.internal.utils.CustomUtils
import company.tap.checkout.internal.viewmodels.CheckoutViewModel
import company.tap.checkout.internal.webview.CustomWebViewClientContract
import company.tap.checkout.internal.webview.TapCustomWebViewClient
import company.tap.checkout.internal.webview.WebViewContract
import company.tap.checkout.open.controller.SDKSession
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapuilibrary.themekit.ThemeManager
import company.tap.tapuilibrary.uikit.atoms.TapTextView
import kotlinx.android.synthetic.main.fragment_web.*


@SuppressLint("UseCompatLoadingForDrawables")
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
    val webCardview by lazy { view.findViewById<CardView>(R.id.webCardview) }
    private var displayMetrics: Int? = 0

    init {
        progressBar?.setBackgroundColor(Color.parseColor(ThemeManager.getValue("merchantHeaderView.backgroundColor")))
        webViewLinear?.setBackgroundColor(Color.parseColor(ThemeManager.getValue("GlobalValues.Colors.whiteTwo")))


        // webViewUrl = arguments?.getString(WebFragment.KEY_URL)
        //  webViewUrl = redirectURL
        //  chargeResponse = authenticate
        progressBar?.max = 100
        progressBar?.progress = 10
        displayMetrics = CustomUtils.getDeviceDisplayMetrics(context as Activity)

        if (displayMetrics == DisplayMetrics.DENSITY_400 ||displayMetrics == DisplayMetrics.DENSITY_XXHIGH || displayMetrics == DisplayMetrics.DENSITY_450 || displayMetrics == DisplayMetrics.DENSITY_440) {
            progressBar.indeterminateDrawable = context.resources.getDrawable( R.drawable.reduced_60)
        }else  progressBar.indeterminateDrawable = context.resources.getDrawable( R.drawable.output_black_loader_nobg)

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
       // webViewContract.redirectLoadingFinished(success, authenticate, SDKSession.contextSDK)
    }

    override fun getRedirectedURL(url: String) {
        if (url.contains("gosellsdk://return_url")) {
            // webViewContract?.resultObtained(true, contextSDK)

            webViewContract?.redirectLoadingFinished(url.contains("gosellsdk://return_url"), authenticate,
                SDKSession.contextSDK
            )
        } else {

//            webViewContract.directLoadingFinished(true)
        }
    }

    override fun showLoading(showLoading: Boolean) {

    }
    @SuppressLint("SetJavaScriptEnabled")
    private fun setUpWebView(mUrl: String) {
        val transition: Transition = ChangeBounds()
        TransitionManager.beginDelayedTransition(web_view, transition)
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
                
                if (newProgress == 100) {
                     // Hide the progressbar
                     progressBar?.visibility = View.GONE
                    web_view.setVisibility(View.VISIBLE)
                    if (displayMetrics == DisplayMetrics.DENSITY_400 ||displayMetrics == DisplayMetrics.DENSITY_XXHIGH || displayMetrics == DisplayMetrics.DENSITY_450 || displayMetrics == DisplayMetrics.DENSITY_440) {
                        webCardview.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,1400).also { it.setMargins(35, 20, 35, 20) }
                        web_view.layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 1400).also { it.setMargins(35, 20, 35, 20) }


                    }
                    webCardview.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1250).also { it.setMargins(35, 20, 35, 20) }
                    web_view.layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 1250).also { it.setMargins(35, 20, 35, 20) }




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