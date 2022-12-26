package company.tap.checkout

import android.content.Context
import com.fasterxml.jackson.databind.ObjectMapper
import company.tap.checkout.internal.api.models.TapLoyaltyModel
import company.tap.checkout.internal.apiresponse.ApiService
import company.tap.checkout.open.controller.SDKSession
import company.tap.checkout.open.data_managers.PaymentDataSource
import company.tap.checkout.open.enums.SdkMode
import company.tap.checkout.open.exceptions.ErrorReport
import company.tap.tapnetworkkit.connection.NetworkApp
import java.io.File
import java.util.*

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
    fun init(context: Context, secretKeyTest: String?,secretKeyLive: String? ,packageId: String?,locale:Locale?) {
        if(packageId?.isEmpty() == true) {
            ErrorReport("Application must have packageId in order to use goSellSDK.")
        }else {

            when (PaymentDataSource.getSDKMode()) {
                SdkMode.SAND_BOX -> {
                    initNetworkCall(context, secretKeyTest, packageId ,SDKSession.sdkIdentifier)
                    PaymentDataSource.setInitializeKeys(secretKeyTest)
                }
                SdkMode.PRODUCTION -> {
                    initNetworkCall(
                        context,
                        secretKeyLive,
                        packageId,
                        SDKSession.sdkIdentifier
                    )
                    PaymentDataSource.setInitializeKeys(secretKeyLive)
                }
                else -> {
                    initNetworkCall(
                        context,
                        secretKeyTest,
                        packageId,
                        SDKSession.sdkIdentifier
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
        sdkIdentifier: String?
    ){
        NetworkApp.initNetwork(
            context,
            secretKey,
            packageId,
            ApiService.BASE_URL,
         //   sdkIdentifier,BuildConfig.EncryptAPIKEY)
            sdkIdentifier,true)
    }
}