package company.tap.checkout.internal.webview

/**
 * Created by OlaMonir on 7/27/20.

Copyright (c) 2020    Tap Payments.
All rights reserved.
 **/

interface CustomWebViewClientContract {
    fun submitResponseStatus( success : Boolean)
    fun getRedirectedURL(url: String, chargeStatus: Any?)
    fun showLoading(showLoading: Boolean)

}