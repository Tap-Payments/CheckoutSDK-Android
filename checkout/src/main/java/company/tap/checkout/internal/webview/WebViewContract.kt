package company.tap.checkout.internal.webview

/**
 * Created by OlaMonir on 7/27/20.

Copyright (c) 2020    Tap Payments.
All rights reserved.
 **/

interface WebViewContract {
    fun redirectLoadingFinished(done: Boolean)
    fun directLoadingFinished(done: Boolean)
}