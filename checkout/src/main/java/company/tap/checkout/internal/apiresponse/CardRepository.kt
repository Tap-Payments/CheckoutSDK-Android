package company.tap.checkout.internal.apiresponse

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.google.gson.JsonElement
import company.tap.checkout.internal.api.requests.PaymentOptionsRequest
import company.tap.checkout.internal.api.responses.PaymentOptionsResponse
import company.tap.checkout.internal.api.responses.SDKSettings
import company.tap.checkout.internal.viewmodels.TapLayoutViewModel
import company.tap.checkout.open.controller.SDKSession
import company.tap.checkout.open.data_managers.PaymentDataSource
import company.tap.checkout.open.enums.TransactionMode
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapnetworkkit.controller.NetworkController
import company.tap.tapnetworkkit.enums.TapMethodType
import company.tap.tapnetworkkit.exception.GoSellError
import company.tap.tapnetworkkit.interfaces.APIRequestCallback
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.subjects.BehaviorSubject
import retrofit2.Response

/**
 * Created by AhlaamK on 11/15/20.

Copyright (c) 2020    Tap Payments.
All rights reserved.
 **/
class CardRepository : APIRequestCallback {
    val resultObservable = BehaviorSubject.create<CardViewState>()
    private var paymentOptionsResponse :PaymentOptionsResponse?= null
    private var initResponse:SDKSettings?=null
    private lateinit var viewModel: TapLayoutViewModel

    private var sdkSession : SDKSession =SDKSession()

    @RequiresApi(Build.VERSION_CODES.N)
    fun getInitData(context: Context, viewModel: TapLayoutViewModel) {
        this.viewModel = viewModel
        if( LocalizationManager.getLocale(context).language  == "en") NetworkController.getInstance().processRequest(
            TapMethodType.GET,
            ApiService.INIT,
            null,
            this,
            INIT_CODE
        )
      //  else NetworkController.getInstance().processRequest(TapMethodType.GET, ApiService.INIT_AR, null, this, INIT_CODE)
    }
    @RequiresApi(Build.VERSION_CODES.N)
    fun getPaymentOptions(context: Context, viewModel: TapLayoutViewModel) {
       this.viewModel = viewModel
        /*
             Passing post request body to obtain
             response for Payment options
             */
     /* val requestBody = PaymentOptionsRequest(TransactionMode.PURCHASE, BigDecimal.valueOf(12),null,null,null,"kwd", TapCustomer.CustomerBuilder(
            ""
        ).firstName("dsd").lastName("last").email("abc@gmail.com").phone( PhoneNumber("00965", "53254252")).build(), null,"ALL")
*/
       val requestBody = PaymentOptionsRequest(TransactionMode.PURCHASE,PaymentDataSource.getAmount(),null,null,null,PaymentDataSource.getCurrency()?.isoCode,PaymentDataSource.getCustomer()?.identifier,null,"ALL")
        val jsonString = Gson().toJson(requestBody)
        if( LocalizationManager.getLocale(context).language  == "en") NetworkController.getInstance().processRequest(
            TapMethodType.POST, ApiService.PAYMENT_TYPES, jsonString, this, PAYMENT_OPTIONS_CODE
        )
       // else NetworkController.getInstance().processRequest(TapMethodType.GET, ApiService.INIT_AR, null, this, PAYMENT_OPTIONS_CODE)
    }
    override fun onSuccess(responseCode: Int, requestCode: Int, response: Response<JsonElement>?) {
        if (requestCode == INIT_CODE) {
            response?.body().let {
                initResponse = Gson().fromJson(it, SDKSettings::class.java)

            }
        }else if (requestCode == PAYMENT_OPTIONS_CODE){
            response?.body().let {
                paymentOptionsResponse = Gson().fromJson(it, PaymentOptionsResponse::class.java)

            }
        }
        val viewState = CardViewState(
            initResponse = initResponse,
            paymentOptionsResponse = paymentOptionsResponse
        )

        if(initResponse!=null && paymentOptionsResponse!=null){
            viewModel.getDatasfromAPIs(initResponse, paymentOptionsResponse)
            resultObservable.onNext(viewState)
            resultObservable.onComplete()
        }

    }

    override fun onFailure(requestCode: Int, errorDetails: GoSellError?) {
        errorDetails?.let {
            if (it.throwable != null)
                resultObservable.onError(it.throwable)
            else
               // resultObservable.onError(Throwable(it.errorMessage))
                   RxJavaPlugins.setErrorHandler(Throwable::printStackTrace)

        }
    }

    companion object {
        private const val INIT_CODE = 1
        private const val PAYMENT_OPTIONS_CODE = 2
    }
}

