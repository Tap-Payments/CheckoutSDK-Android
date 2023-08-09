package company.tap.checkout

import android.content.Context
import com.bugfender.sdk.Bugfender
import company.tap.checkout.internal.apiresponse.ApiService
import company.tap.checkout.open.CheckOutActivity
import company.tap.checkout.open.controller.SDKSession
import company.tap.checkout.open.data_managers.PaymentDataSource
import company.tap.checkout.open.enums.SdkMode
import company.tap.checkout.open.exceptions.ErrorReport
import company.tap.tapnetworkkit.connection.NetworkApp
import java.util.*


/**
 * Created by AhlaamK on 6/17/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
object TapCheckOutSDK {
   var displayColoredDark :Boolean = false
   var displayMonoLight :Boolean = false
    /**
     * Init.
     *
     * @param context   the context
     * @param authToken the auth token
     * @param packageId registered id
     */
    fun init(context: Context, secretKeyTest: String?,secretKeyLive: String? ,packageId: String?,locale:Locale?) {

        if(packageId?.isEmpty() == true) {
            ErrorReport("Application must have packageId in order to use CheckoutSDK.")
        }else {

            when (PaymentDataSource.getSDKMode()) {
                SdkMode.SAND_BOX -> {
                    initNetworkCall(context, secretKeyTest, packageId ,SDKSession.sdkIdentifier,
                        context.resources.getString(R.string.enryptkey)
                    )
                    PaymentDataSource.setInitializeKeys(secretKeyTest)
                }
                SdkMode.PRODUCTION -> {
                    initNetworkCall(
                        context,
                        secretKeyLive,
                        packageId,
                        SDKSession.sdkIdentifier,  context.resources.getString(R.string.enryptkey)
                    )
                    PaymentDataSource.setInitializeKeys(secretKeyLive)
                }
                else -> {
                    initNetworkCall(
                        context,
                        secretKeyTest,
                        packageId,
                        SDKSession.sdkIdentifier,    context.resources.getString(R.string.enryptkey)
                    )
                    PaymentDataSource.setInitializeKeys(secretKeyTest)
                }
            }
        }

        if (locale != null) {
            PaymentDataSource.setSDKLanguage(locale)
        }

    }

    private fun initNetworkCall(
        context: Context,
        secretKey: String?,
        packageId: String?,
        sdkIdentifier: String?,
        encryptKeys:String?
    ){
        NetworkApp.initNetwork(
            context,
            secretKey,
            packageId,
            ApiService.BASE_URL,
            "android-checkout-sdk",true, encryptKeys,CheckOutActivity())
        Bugfender.setDeviceString("Static Headers",NetworkApp.getApplicationInfo())
    }


}