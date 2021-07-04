package company.tap.checkout.internal.apiresponse

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.google.gson.JsonElement
import company.tap.checkout.internal.api.enums.URLStatus
import company.tap.checkout.internal.api.models.*
import company.tap.checkout.internal.api.requests.CreateChargeRequest
import company.tap.checkout.internal.api.requests.PaymentOptionsRequest
import company.tap.checkout.internal.api.responses.PaymentOptionsResponse
import company.tap.checkout.internal.api.responses.SDKSettings
import company.tap.checkout.internal.viewmodels.TapLayoutViewModel
import company.tap.checkout.open.controller.SDKSession
import company.tap.checkout.open.data_managers.PaymentDataSource
import company.tap.checkout.open.enums.TransactionMode
import company.tap.checkout.open.models.Destinations
import company.tap.checkout.open.models.TopUp
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapnetworkkit.controller.NetworkController
import company.tap.tapnetworkkit.enums.TapMethodType
import company.tap.tapnetworkkit.exception.GoSellError
import company.tap.tapnetworkkit.interfaces.APIRequestCallback
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.subjects.BehaviorSubject
import retrofit2.Response
import java.math.BigDecimal
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by AhlaamK on 11/15/20.

Copyright (c) 2020    Tap Payments.
All rights reserved.
 **/
class CardRepository : APIRequestCallback {
    val resultObservable = BehaviorSubject.create<CardViewState>()
    private var paymentOptionsResponse :PaymentOptionsResponse?= null
    private var initResponse:SDKSettings?=null
    private var chargeResponse:Charge?=null
    private lateinit var viewModel: TapLayoutViewModel

    private var sdkSession : SDKSession =SDKSession()
    /**
     * The constant RETURN_URL.
     */
    val RETURN_URL : kotlin . String ? = "gosellsdk://return_url"
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
       val requestBody = PaymentOptionsRequest(
           TransactionMode.PURCHASE,
           PaymentDataSource.getAmount(),
           null,
           null,
           null,
           PaymentDataSource.getCurrency()?.isoCode,
           PaymentDataSource.getCustomer()?.identifier,
           null,
           "ALL"
       )
        val jsonString = Gson().toJson(requestBody)
        if( LocalizationManager.getLocale(context).language  == "en") NetworkController.getInstance().processRequest(
            TapMethodType.POST, ApiService.PAYMENT_TYPES, jsonString, this, PAYMENT_OPTIONS_CODE
        )
       // else NetworkController.getInstance().processRequest(TapMethodType.GET, ApiService.INIT_AR, null, this, PAYMENT_OPTIONS_CODE)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun createChargeRequest(context: Context, viewModel: TapLayoutViewModel) {
        this.viewModel = viewModel
        var fee = BigDecimal.ZERO
        //         Log.d("callTokenAPI"," step 4 : startPaymentProcessWithCard>callTokenAPI : in class "+ "["+this.getClass().getName()+"] with token id=["+serializedResponse.getId()+"]  ");




        val chargeRequest = PaymentDataSource.getCustomer()?.let {
            CreateChargeRequest(
                null,
                BigDecimal.valueOf(22),
                "kwd",
                BigDecimal.valueOf(12),
                "kwd",
                it,
                fee,
                Order("sds","ddda","dsad"),
                TrackingURL(URLStatus.PENDING,"gosellsdk://return_url"),
                null,
                SourceRequest("ds"),
                "paymentDescription",
                null,
                null,
                true,
                "statementDescriptor",
                true,
                company.tap.checkout.open.models.Receipt(false,false),
                null,
                null
            )
        }
        val jsonString = Gson().toJson(chargeRequest)
        if( LocalizationManager.getLocale(context).language  == "en") NetworkController.getInstance().processRequest(
            TapMethodType.POST, ApiService.CHARGES, jsonString, this, PAYMENT_OPTIONS_CODE
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
        }else if(requestCode == CHARGE_REQ_CODE) {
            response?.body().let {
                chargeResponse = Gson().fromJson(it, Charge::class.java)
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
        private const val CHARGE_REQ_CODE = 3
    }


}

