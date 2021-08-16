package company.tap.checkout.internal.webview

/**
 * Created by OlaMonir on 7/27/20.
Copyright (c) 2020    Tap Payments.
All rights reserved.
 **/
import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import company.tap.checkout.R
import company.tap.checkout.internal.apiresponse.CardViewModel
import company.tap.tapuilibrary.themekit.ThemeManager
import company.tap.tapuilibrary.uikit.ktx.setBorderedView
import company.tap.tapuilibrary.uikit.ktx.setTopBorders
import kotlinx.android.synthetic.main.fragment_web.*


class WebFragment(private val webViewContract: WebViewContract,private val cardViewModel: CardViewModel) : Fragment(),
    CustomWebViewClientContract {

    private var webViewUrl: String? = null
    val progressBar by lazy { view?.findViewById<ProgressBar>(R.id.progressBar) }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_web, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTopDraggerView()
        progressBar?.setBackgroundColor(Color.parseColor(ThemeManager.getValue("merchantHeaderView.backgroundColor")))
        progressBar?.progressDrawable?.setColorFilter(
            Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);
        progressBar?.progressTintList = ColorStateList.valueOf(Color.RED);

        webViewUrl = arguments?.getString(KEY_URL)
        progressBar?.max = 100
        progressBar?.progress = 20
        if (TextUtils.isEmpty(webViewUrl)) {
            throw IllegalArgumentException("Empty URL passed to WebViewFragment!")
        }
        webViewUrl?.let { setUpWebView(it) }

    }

    private fun setTopDraggerView() {
        setTopBorders(
            topLinear,
            40f,// corner raduis
            0.0f,
            Color.parseColor(ThemeManager.getValue("merchantHeaderView.backgroundColor")),// stroke color
            Color.parseColor(ThemeManager.getValue("merchantHeaderView.backgroundColor")),// tint color
            Color.parseColor(ThemeManager.getValue("merchantHeaderView.backgroundColor"))
        )//

        if (ThemeManager.currentTheme.isNotEmpty() && ThemeManager.currentTheme.contains("dark")) {
            setBorderedView(
                draggerView,
                40f,// corner raduis
                0.0f,
                Color.parseColor("#3b3b3c"),// stroke color
                Color.parseColor("#3b3b3c"),// tint color
                Color.parseColor("#3b3b3c")
            )//
        } else {
            setBorderedView(
                draggerView,
                40f,// corner raduis
                0.0f,
                Color.parseColor("#e0e0e0"),// stroke color
                Color.parseColor("#e0e0e0"),// tint color
                Color.parseColor("#e0e0e0")
            )//
        }
    }


    @SuppressLint("SetJavaScriptEnabled")
    private fun setUpWebView(mUrl: String) {
        web_view.settings.javaScriptEnabled = true
        web_view.webChromeClient = WebChromeClient();
        if (Build.VERSION.SDK_INT >= 21) {
            web_view.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
        web_view.webViewClient = TapCustomWebViewClient(this,cardViewModel)
        web_view.settings.loadWithOverviewMode = true
        web_view.loadUrl(webViewUrl)
        web_view.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                if (web_view.canGoBack()) {
                    web_view.goBack()
                    return@setOnKeyListener true
                }
                return@setOnKeyListener false
            }
            false
        }



        web_view.setWebChromeClient(object : WebChromeClient() {
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
                progressBar?.setProgress(newProgress)
                if (newProgress == 100) {
                    // Hide the progressbar
                    progressBar?.setVisibility(View.GONE)
                }
            }
        })
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
        webViewContract.redirectLoadingFinished(success)
    }

    override fun getRedirectedURL(url: String) {
       // webViewContract.redirectLoadingFinished(url.contains("https://www.google.com/search?"))
        if(url.contains("gosellsdk://return_url")){
        webViewContract.redirectLoadingFinished(url.contains("gosellsdk://return_url"))
        }else{
            webViewContract.directLoadingFinished(true)
        }
    }




    companion object {
         const val KEY_URL = "key:url"

        fun newInstance(url: String, webViewContract: WebViewContract, cardViewModel: CardViewModel): WebFragment {
            val fragment = WebFragment(webViewContract,cardViewModel)
            val args = Bundle()
            args.putString(KEY_URL, url)
            fragment.arguments = args
            return fragment
        }
    }

}