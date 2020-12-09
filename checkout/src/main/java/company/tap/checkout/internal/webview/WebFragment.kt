package company.tap.checkout.internal.webview

/**
 * Created by OlaMonir on 7/27/20.

Copyright (c) 2020    Tap Payments.
All rights reserved.
 **/
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import androidx.fragment.app.Fragment
import company.tap.checkout.R
import kotlinx.android.synthetic.main.fragment_web.*


class WebFragment constructor(private val webViewContract: WebViewContract)  : Fragment() , CustomWebViewClientContract  {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_web, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpWebView()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setUpWebView() {
        web_view.settings.javaScriptEnabled = true
        web_view.webChromeClient = WebChromeClient();
        if (Build.VERSION.SDK_INT >= 21) {
            web_view.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
        web_view.webViewClient = TapCustomWebViewClient(this)
        web_view.loadUrl("https://www.google.com")
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
    }


    fun showLoading() {
        // show tap loading until we receive success or failed
    }

    /**
     * change action button status with success or failed
    if success == true show success gif of action button
    if success == false show error gif of action button
     */
    override fun submitResponseStatus( success : Boolean){
        webViewContract.redirectLoadingFinished(success)
    }
    override fun getRedirectedURL(url : String){
        webViewContract.redirectLoadingFinished(url.contains("https://www.google.com/search?"))
    }




}