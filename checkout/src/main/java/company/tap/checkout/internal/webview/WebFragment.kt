package company.tap.checkout.internal.webview

/**
 * Created by OlaMonir on 7/27/20.
Copyright (c) 2020    Tap Payments.
All rights reserved.
 **/
import android.annotation.SuppressLint
import android.app.Activity
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.annotation.DrawableRes
import androidx.fragment.app.DialogFragment
import company.tap.checkout.R
import company.tap.checkout.internal.api.models.Charge
import company.tap.checkout.internal.apiresponse.CardViewModel
import company.tap.checkout.internal.utils.CustomUtils
import company.tap.checkout.internal.utils.showToast
import company.tap.checkout.internal.utils.twoThirdHeightView
import company.tap.checkout.internal.viewholders.SwitchViewHolder
import company.tap.checkout.internal.viewmodels.CheckoutViewModel
import company.tap.checkout.open.controller.SDKSession.contextSDK
import company.tap.checkout.open.data_managers.PaymentDataSource
import company.tap.tapuilibraryy.themekit.ThemeManager
import company.tap.tapuilibraryy.uikit.ktx.setBorderedView
import company.tap.tapuilibraryy.uikit.ktx.setTopBorders
import kotlinx.android.synthetic.main.fragment_web.*
import kotlinx.android.synthetic.main.fragment_web.web_view
import kotlinx.android.synthetic.main.switch_layout.view.*
import kotlinx.android.synthetic.main.web_view_layout.*
import kotlin.math.roundToInt


class WebFragment(
    private val webViewContract: WebViewContract?,
    private val cardViewModel: CardViewModel?,
    private val checkoutViewModel: CheckoutViewModel,
    private val isFirstTimeLoading: Boolean,
    private val onLoadedWebView: () -> Unit
) : DialogFragment(),
    CustomWebViewClientContract {



    private var webViewUrl: String? = null
    private var chargeResponse: Charge? = null
    val progressBar by lazy { view?.findViewById<ProgressBar>(R.id.progressBar) }
    private var displayMetrics: Int? = 0
    private var isFirstTimeLoadingInWeb = this.isFirstTimeLoading

    @DrawableRes
    val loaderGif: Int =
        if (ThemeManager.currentTheme.isNotEmpty() && ThemeManager.currentTheme.contains("dark")) R.drawable.reduced_loader_white else R.drawable.loader_black

    override fun getTheme(): Int = R.style.DialogTheme
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_web, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressBar?.setBackgroundColor(Color.parseColor(ThemeManager.getValue("merchantHeaderView.backgroundColor")))
        progressBar?.progressDrawable?.setColorFilter(
            Color.RED, android.graphics.PorterDuff.Mode.SRC_IN
        );
        progressBar?.progressTintList = ColorStateList.valueOf(Color.RED);

        webViewUrl = arguments?.getString(KEY_URL)
        chargeResponse = arguments?.getSerializable(CHARGE) as Charge?
        progressBar?.max = 100
        progressBar?.progress = 10
        displayMetrics = CustomUtils.getDeviceDisplayMetrics(context as Activity)
        if (TextUtils.isEmpty(webViewUrl)) {
            throw IllegalArgumentException("Empty URL passed to WebViewFragment!")
        }
        if (displayMetrics == DisplayMetrics.DENSITY_400 || displayMetrics == DisplayMetrics.DENSITY_XXHIGH || displayMetrics == DisplayMetrics.DENSITY_450 || displayMetrics == DisplayMetrics.DENSITY_440) {
            progressBar?.indeterminateDrawable =
                (context as Activity).resources.getDrawable(loaderGif)
        } else progressBar?.indeterminateDrawable =
            (context as Activity).resources.getDrawable(loaderGif)

        progressBar?.visibility = View.VISIBLE
        web_view?.visibility = View.GONE
        webViewUrl?.let { setUpWebView(it) }

    }


    @SuppressLint("SetJavaScriptEnabled")
    private fun setUpWebView(mUrl: String) {
        web_view.settings.javaScriptEnabled = true
        web_view.webChromeClient = WebChromeClient()
        web_view.isVerticalScrollBarEnabled = true
        web_view.isHorizontalScrollBarEnabled = true
        web_view.settings.loadWithOverviewMode = true
        web_view.settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.NORMAL
        web_view.settings.useWideViewPort = true
        web_view.settings.domStorageEnabled = true
        web_view.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        web_view.webViewClient =
            cardViewModel?.let { TapCustomWebViewClient(this, it, checkoutViewModel) }!!
        //  web_view.webViewClient = cardViewModel?.let { TapCustomWebViewClient2(this, it) }!!


        webViewUrl?.let { web_view.loadUrl(it) }

        web_view.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {

                //if (web_view.canGoBack()) {
                web_view.goBack()
                CheckoutViewModel().cancelledCall()
                /**
                 * put here listener or delegate thT process cancelled **/
                return@setOnKeyListener true
                //  }
                return@setOnKeyListener false
            }
            false
        }



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
                    if (isFirstTimeLoadingInWeb) {
                        onLoadedWebView.invoke()
                        isFirstTimeLoadingInWeb = false
                    }
                    progressBar?.visibility = View.INVISIBLE
                    web_view.visibility = View.VISIBLE
                    if (isGooglePlayWebView){
                        relative.layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            context?.twoThirdHeightView()?.roundToInt()!!
                        )
                    }

                }
            }


            override fun onCloseWindow(window: WebView?) {
                super.onCloseWindow(window)
                Log.e("webview", "Window trying to close");

            }

        }
    }


    override fun showLoading(showLoading: Boolean) {
        // show tap loading until we receive success or failed
//        if(showLoading) progressBar?.progress = 100
//        else progressBar?.visibility = View.GONE
    }

    /**
     * change action button status with success or failed
    if success == true show success gif of action button
    if success == false show error gif of action button
     */
    override fun submitResponseStatus(success: Boolean) {
        /*  val intent = Intent(activity, CheckoutFragment::class.java)
          startActivity(intent)*/
        //    webViewContract?.redirectLoadingFinished(success, chargeResponse, contextSDK)
    }

    override fun getRedirectedURL(url: String) {
        println("url are>>" + PaymentDataSource.getChargeOrAuthorize())
        println("url are>>" + url)

        // webViewContract.redirectLoadingFinished(url.contains("https://www.google.com/search?"))
        if (url.contains("gosellsdk://return_url")) {
            // webViewContract?.resultObtained(true, contextSDK)

            webViewContract?.redirectLoadingFinished(
                url.contains("gosellsdk://return_url"), chargeResponse,
                contextSDK
            )
        } else {

//            webViewContract.directLoadingFinished(true)
        }
    }


    companion object {
        const val KEY_URL = "key:url"
        const val CHARGE = "charge_response"
        var isGooglePlayWebView = false

        fun newInstance(
            url: String,
            webViewContract: WebViewContract,
            cardViewModel: CardViewModel,
            chargeResponse: Charge?,
            checkoutViewModel: CheckoutViewModel,
            isFirstTimeLoading: Boolean,
            onLoadedWebView: () -> Unit
        ): WebFragment {
            val fragment = WebFragment(
                webViewContract,
                cardViewModel,
                checkoutViewModel,
                isFirstTimeLoading,
                onLoadedWebView
            )
            val args = Bundle()
            args.putString(KEY_URL, url)
            args.putSerializable(CHARGE, chargeResponse)
            fragment.arguments = args
            println("fragment is" + fragment)
            return fragment
        }
    }


}