package company.tap.checkout.internal.webview

import company.tap.checkout.internal.api.models.Authenticate
import company.tap.checkout.internal.api.models.Charge

/**
 * Created by OlaMonir on 7/27/20.

Copyright (c) 2020    Tap Payments.
All rights reserved.
 **/

interface WebViewContract {
    fun redirectLoadingFinished(done: Boolean, authenticate: Charge?)
    fun directLoadingFinished(done: Boolean)
}