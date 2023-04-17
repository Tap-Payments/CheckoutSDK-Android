package company.tap.checkout.internal.viewholders

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.annotation.DrawableRes
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentManager
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
import company.tap.tapuilibrary.themekit.ThemeManager
import company.tap.tapuilibrary.uikit.atoms.TapTextView
import kotlinx.android.synthetic.main.fragment_web.*


@SuppressLint("UseCompatLoadingForDrawables")
class WebViewHolder(
    private val context: Context,
    private val fragmentManager: FragmentManager,
    val redirectURL: String,
    val webViewContract: WebViewContract,
    val cardViewModel: CardViewModel,
    val authenticate: Charge?,
    val checkoutViewModel: CheckoutViewModel,
    val bottomSheetLayout: FrameLayout,
    val sdkLayout: LinearLayout
) : TapBaseViewHolder, CustomWebViewClientContract {
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

    @DrawableRes
    val loaderGif: Int =
        if (ThemeManager.currentTheme.isNotEmpty() && ThemeManager.currentTheme.contains("dark")) R.drawable.reduced_loader_white else R.drawable.reduced_loader_black

    init {
        progressBar?.setBackgroundColor(Color.parseColor(ThemeManager.getValue("merchantHeaderView.backgroundColor")))
        webViewLinear?.setBackgroundColor(Color.parseColor(ThemeManager.getValue("GlobalValues.Colors.whiteTwo")))

        // progressBar.set(loaderGif)
        // webViewUrl = arguments?.getString(WebFragment.KEY_URL)
        //  webViewUrl = redirectURL
        //  chargeResponse = authenticate
        progressBar?.max = 100
        progressBar?.progress = 10
        displayMetrics = CustomUtils.getDeviceDisplayMetrics(context as Activity)

        if (displayMetrics == DisplayMetrics.DENSITY_400 ||
            displayMetrics == DisplayMetrics.DENSITY_XXHIGH ||
            displayMetrics == DisplayMetrics.DENSITY_450 ||
            displayMetrics == DisplayMetrics.DENSITY_440
        ) {
            //  progressBar.indeterminateDrawable = context.resources.getDrawable( R.drawable.reduced_60)
            progressBar.indeterminateDrawable = context.resources.getDrawable(loaderGif)
        } else progressBar.indeterminateDrawable = context.resources.getDrawable(loaderGif)

        if (TextUtils.isEmpty(redirectURL)) {
            throw IllegalArgumentException("Empty URL passed to WebViewFragment!")
        }
        val webViewTitleText: String =
            LocalizationManager.getValue("GatewayHeader", "HorizontalHeaders", "webViewTitle")
        webViewTextTitle.text = webViewTitleText
        println("redirectURL in holdre" + redirectURL)
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

            webViewContract?.redirectLoadingFinished(
                url.contains("gosellsdk://return_url"), authenticate,
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
//        val transition: Transition = ChangeBounds()
//        TransitionManager.beginDelayedTransition(web_view, transition)
        web_view.settings.javaScriptEnabled = true

        progressBar?.visibility = View.VISIBLE
        web_view.visibility = View.INVISIBLE
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
                    val desired_height_params =
                        (view?.context?.getDeviceSpecs()?.first?.times(2) ?: 1000) / 3
                    val lp = RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        desired_height_params
                    )
                    web_view.layoutParams = lp;
                    progressBar?.addFadeOutAnimation(2000)
                    bottomSheetLayout.resizeAnimation(
                        durationTime = 1300,
                        startHeight = sdkLayout.height
                    )
                    web_view?.addFadeInAnimation(durationTime = 2000)
                    web_view.evaluateJavascript(
                        "(function() { return ('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>'); })();"
                    ) { html ->
                        Log.e("HTML", html)

                        // code here
                    }
                }

            }
        }

        web_view.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        web_view.webViewClient =
            cardViewModel?.let { TapCustomWebViewClient(this, it, checkoutViewModel) }!!
        web_view.settings.loadWithOverviewMode = true
        // web_view.scrollBarStyle = View.
        web_view.isVerticalScrollBarEnabled = true
        web_view.isHorizontalScrollBarEnabled = true
        web_view.setInitialScale(1)
        web_view.settings.defaultZoom = WebSettings.ZoomDensity.FAR;
        web_view.settings.useWideViewPort = true
        /**
         * needed to be removed , just for testing purpose
         */
//        val html =
//            "<div id=\"initiate3dsSimpleRedirect\" xmlns=\"http://www.w3.org/1999/html\"> <iframe style=\"display:none\" id=\"methodFrame\" name=\"methodFrame\" height=\"100\" width=\"200\" > </iframe> <form id =\"initiate3dsSimpleRedirectForm\" method=\"POST\" action=\"https://mtf.gateway.mastercard.com/acs/mastercard/v2/method\" target=\"methodFrame\"> <input type=\"hidden\" name=\"threeDSMethodData\" value=\"eyJ0aHJlZURTTWV0aG9kTm90aWZpY2F0aW9uVVJMIjoiaHR0cHM6Ly9tdGYuZ2F0ZXdheS5tYXN0ZXJjYXJkLmNvbS9jYWxsYmFja0ludGVyZmFjZS9nYXRld2F5LzY2MmZiMDFjNzM1YTllNmMwMDNmNDQwZmM5YWI4NTQ2NGYyNTZmZWJjOTNiOGY5NjI0N2RiNjkyYmI4NThlNDkiLCJ0aHJlZURTU2VydmVyVHJhbnNJRCI6ImYwNDgxNmMwLTM4ODgtNGY1ZC05Y2VmLTVhYTg4Y2YxMGQ1ZCJ9\" /> </form> <script id=\"initiate-authentication-script\"> var e=document.getElementById(\"initiate3dsSimpleRedirectForm\"); if (e) { e.submit(); if (e.parentNode !== null) { e.parentNode.removeChild(e); } } </script> </div><div id=\"threedsChallengeRedirect\" xmlns=\"http://www.w3.org/1999/html\" style=\" height: 100vh\"> <form id =\"threedsChallengeRedirectForm\" method=\"POST\" action=\"https://mtf.gateway.mastercard.com/acs/mastercard/v2/prompt\" target=\"challengeFrame\"> <input type=\"hidden\" name=\"creq\" value=\"eyJ0aHJlZURTU2VydmVyVHJhbnNJRCI6ImYwNDgxNmMwLTM4ODgtNGY1ZC05Y2VmLTVhYTg4Y2YxMGQ1ZCJ9\" /> </form> <iframe style=\"border:0px\" id=\"challengeFrame\" name=\"challengeFrame\" width=\"100%\" height=\"100%\"  frameborder=\"0\" scrolling=\"yes\" allowfullscreen></iframe> <script id=\"authenticate-payer-script\"> var e=document.getElementById(\"threedsChallengeRedirectForm\"); if (e) { e.submit(); if (e.parentNode !== null) { e.parentNode.removeChild(e); } } </script> </div>\n" +
//                    "\n" +
//                    "<!-- create html page include iframe -->\n" +
//                    "<!DOCTYPE html>\n" +
//                    "<html>\n" +
//                    "  <head><meta name=\"robots\" content=\"noindex, noimageindex, nofollow, nosnippet, noarchive\" /><meta http-equiv=\"Content-Type\" content=\"text/html;charset=utf-8\" /><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=no, target-densityDpi=device-dpi\" /><title>\n" +
//                    "\tAccept Card\n" +
//                    "</title></head>\n" +
//                    "  <body id=\"body\" style=\"height: 100vh\">\n" +
//                    "\n" +
//                    "    <!-- Start CC Payment Processing -->\n" +
//                    "    <div>\n" +
//                    "        \n" +
//                    "    </div>\n" +
//                    "    <!-- End CC Payment Processing -->\n" +
//                    "  <script>\n" +
//                    "      window.addEventListener('message', e => {\n" +
//                    "          console.log('message', e.data);\n" +
//                    "      });\n" +
//                    "  </script>\n" +
//                    "  </body>\n" +
//                    "</html>\n"

        redirectURL.let {
          //  web_view.loadData(html, "text/html", "UTF-8");
             web_view.loadUrl(it)


        }
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