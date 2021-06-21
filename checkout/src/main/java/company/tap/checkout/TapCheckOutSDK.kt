package company.tap.checkout

import android.content.Context
import company.tap.checkout.internal.apiresponse.ApiService
import company.tap.checkout.open.exceptions.ErrorReport
import company.tap.tapnetworkkit.connection.NetworkApp

/**
 * Created by AhlaamK on 6/17/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
class TapCheckOutSDK {
    /**
     * Init.
     *
     * @param context   the context
     * @param authToken the auth token
     * @param packageId registered id
     */
    fun init(context: Context, authToken: String?, packageId: String?) {
        if(packageId?.isEmpty() == true) {
            ErrorReport("Application must have packageId in order to use goSellSDK.")
        }else

        NetworkApp.initNetwork(
            context,
            authToken,
            packageId,
            ApiService.BASE_URL
        )
    }

}