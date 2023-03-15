package company.tap.checkout.internal.webview

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import company.tap.checkout.internal.apiresponse.ApiService
import company.tap.checkout.internal.apiresponse.CardViewEvent
import company.tap.checkout.internal.apiresponse.CardViewModel
import company.tap.checkout.internal.viewmodels.CheckoutViewModel

/**
 * Created by OlaMonir on 7/27/20.

Copyright (c) 2020    Tap Payments.
All rights reserved.
 **/
class TapCustomWebViewClient2 constructor(private val customWebViewClientContract: CustomWebViewClientContract, private val cardViewModel:CardViewModel) : WebViewClient() {

    var counter=0
    @RequiresApi(Build.VERSION_CODES.N)
    override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
        checkPaymentError(request.url.toString().toLowerCase())
        checkKnetPaymentStatus(request.url.toString().toLowerCase())

        getCustomHeaders()?.let { view.loadUrl(request.url.toString(), it) }
        return true
    }


    @RequiresApi(Build.VERSION_CODES.N)
    private fun checkKnetPaymentStatus(url: String) {
       // if (url.contains("response/receiptKnet".toLowerCase())) {
        val urlIsReturnURL: Boolean = url.startsWith(ApiService.RETURN_URL)
        val shouldLoad = !urlIsReturnURL
        val redirectionFinished = urlIsReturnURL
        val shouldCloseWebPaymentScreen = false
        if (urlIsReturnURL) {
            counter=+1
            println("counter"+counter)
            if (checkPaymentSuccess(url)) {
                customWebViewClientContract.submitResponseStatus(true)
            } else {
                val urlQuerySanitizer: Uri = Uri.parse(url)
                val msg: String = urlQuerySanitizer.getQueryParameter("message").toString()
                customWebViewClientContract.submitResponseStatus(false)
            }
        }
    }


    private fun checkPaymentError(url: String) {
        if (url.contains("errorPage".toLowerCase())) {
            val urlQuerySanitizer: Uri = Uri.parse(url)
            val msg: String = urlQuerySanitizer.getQueryParameter("message").toString()
          //  if (msg.isNotEmpty()) customWebViewClientContract.submitResponseStatus(true) else customWebViewClientContract.submitResponseStatus(false)
        }
    }




    @RequiresApi(Build.VERSION_CODES.N)
    private fun checkPaymentSuccess(url: String): Boolean {
        return try {
            val urlQuerySanitizer: Uri = Uri.parse(url)
            println("urlQuerySanitizer on checkpayment" + urlQuerySanitizer)
            when {
                url.contains("auth_ts") -> {
                    cardViewModel.processEvent(CardViewEvent.RetreiveAuthorizeEvent, CheckoutViewModel(), null, null, null, null)

                }
                url.contains("auth") -> {
                    cardViewModel.processEvent(CardViewEvent.RetreiveSaveCardEvent, CheckoutViewModel(), null, null, null, null)

                }
                else -> {
                    cardViewModel.processEvent(CardViewEvent.RetreiveChargeEvent, CheckoutViewModel(), null, null, null, null)}
            }
            val status: String = urlQuerySanitizer.getQueryParameter("tap_id").toString()
            println("status on checkpayment" + status)
            status.equals("CAPTURED", ignoreCase = true) || status.equals(
                    "0",
                    ignoreCase = true
            )
        } catch (ex: Exception) {
            false
        }
    }


    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        Log.d("url", url.toString())
        customWebViewClientContract.showLoading(true)

     //   url?.let { customWebViewClientContract.getRedirectedURL(it) }
    }

    override fun onPageFinished(@NonNull view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        Log.d("onPageFinished", url.toString())
        customWebViewClientContract.showLoading(false)

        url?.let { customWebViewClientContract.getRedirectedURL(it) }

    }


    @SuppressLint("NewApi")
    override fun shouldInterceptRequest(
            view: WebView?,
            request: WebResourceRequest?
    ): WebResourceResponse? {
        return null
    }

    private fun getCustomHeaders(): Map<String, String>? {
        val headers: MutableMap<String, String> = HashMap()
        //  headers["cid"] =  ""
        return headers
    }


}