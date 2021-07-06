package company.tap.checkout.internal.apiresponse

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.google.gson.JsonElement
import company.tap.checkout.internal.PaymentDataProvider
import company.tap.checkout.internal.api.enums.ChargeStatus
import company.tap.checkout.internal.api.enums.URLStatus
import company.tap.checkout.internal.api.models.*
import company.tap.checkout.internal.api.requests.CreateChargeRequest
import company.tap.checkout.internal.api.requests.PaymentOptionsRequest
import company.tap.checkout.internal.api.responses.PaymentOptionsResponse
import company.tap.checkout.internal.api.responses.SDKSettings
import company.tap.checkout.internal.interfaces.IPaymentDataProvider
import company.tap.checkout.internal.viewmodels.TapLayoutViewModel
import company.tap.checkout.internal.viewmodels.WebPaymentViewModel
import company.tap.checkout.open.controller.SDKSession
import company.tap.checkout.open.data_managers.PaymentDataSource
import company.tap.checkout.open.enums.TransactionMode
import company.tap.checkout.open.models.*
import company.tap.checkout.open.models.Receipt
import company.tap.checkout.open.models.Reference
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapnetworkkit.controller.NetworkController
import company.tap.tapnetworkkit.enums.TapMethodType
import company.tap.tapnetworkkit.exception.GoSellError
import company.tap.tapnetworkkit.interfaces.APIRequestCallback
import company.tap.tapuilibrary.uikit.views.TapBottomSheetDialog.Companion.TAG
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.subjects.BehaviorSubject
import retrofit2.Response
import java.math.BigDecimal
import java.util.*

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

    private var sdkSession : SDKSession = SDKSession
    private var dataProvider: IPaymentDataProvider= PaymentDataProvider()

    fun getDataProvider(): IPaymentDataProvider {
        return dataProvider
    }
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
               PaymentDataSource.getItems(),
               PaymentDataSource.getShipping(),
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
        //  Log.d("PaymentProcessManager",
        //           "startPaymentProcessWithWebPaymentModel >>> paymentOption.getSourceId : " + paymentOption
        //                   .getSourceId());



        paymentOptionsResponse?.paymentOptions?.get(1)?.sourceId?.let { SourceRequest(it) }?.let { callChargeOrAuthorizeOrSaveCardAPI(it, paymentOptionsResponse?.paymentOptions!![0], null, null, context) }
            }
    @RequiresApi(Build.VERSION_CODES.N)
    fun retrieveChargeRequest(context: Context, viewModel: TapLayoutViewModel) {
        this.viewModel = viewModel
        NetworkController.getInstance().processRequest(TapMethodType.GET, ApiService.CHARGE_ID + chargeResponse?.id, null,
                this, CHARGE_RETRIEVE_CODE
        )
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
        else if(requestCode == CHARGE_RETRIEVE_CODE) {
            response?.body().let {
                chargeResponse = Gson().fromJson(it, Charge::class.java)
                if(chargeResponse?.status?.name == ChargeStatus.CAPTURED.name){
                    fireWebPaymentCallBack(chargeResponse)
                    viewModel?.redirectLoadingFinished(true)
                }
            }
        }
        val viewState = CardViewState(
                initResponse = initResponse,
                paymentOptionsResponse = paymentOptionsResponse
        )
        CardViewState.ChargeViewState(charge = chargeResponse)
        if(initResponse!=null && paymentOptionsResponse!=null){
            viewModel.getDatasfromAPIs(initResponse, paymentOptionsResponse)
            resultObservable.onNext(viewState)
            resultObservable.onComplete()
        }
        if(chargeResponse!=null){
            if(::viewModel.isInitialized)
            chargeResponse?.transaction?.url?.let { viewModel.displayRedirect(it) }

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
        private const val CHARGE_RETRIEVE_CODE = 4
    }

     @RequiresApi(Build.VERSION_CODES.N)
     private fun callChargeOrAuthorizeOrSaveCardAPI(source: SourceRequest,
                                                    paymentOption: PaymentOption,
                                                    cardBIN: String?, saveCard: Boolean?, context: Context) {
        Log.e("OkHttp", "CALL CHARGE API OR AUTHORIZE API")
        val provider: IPaymentDataProvider = getDataProvider()
        val supportedCurrencies: java.util.ArrayList<SupportedCurrencies>? = provider.getSupportedCurrencies()
        val orderID: String = provider.getPaymentOptionsOrderID()
         val postURL: String? = provider.getPostURL()
         val post = if (postURL == null) null else TrackingURL(URLStatus.PENDING, postURL)
        val amountedCurrency: AmountedCurrency? = provider.getSelectedCurrency()
        //     Log.d("callChargeOrAuthorizeOr"," step 5 : callChargeOrAuthorizeOrSaveCardAPI : in class "+ "["+this.getClass().getName()+"] with amountedCurrency=["+amountedCurrency.getAmount()+"]  ");
        val transactionCurrency: AmountedCurrency? = provider.getTransactionCurrency()
        val customer: TapCustomer = provider.getCustomer()
        var fee = BigDecimal.ZERO
       //todo if (paymentOption != null) fee = AmountCalculator.calculateExtraFeesAmount(paymentOption.extraFees, supportedCurrencies, amountedCurrency)
        Log.d("PaymentProcessManager", "fee : $fee")
        val order = Order(orderID)
        val redirect = TrackingURL(URLStatus.PENDING, ApiService.RETURN_URL)
        val paymentDescription: String? = provider.getPaymentDescription()
        val paymentMetadata: HashMap<String, String>? = provider.getPaymentMetadata()
        val reference: Reference? = provider.getPaymentReference()
        val shouldSaveCard = saveCard ?: false
        val statementDescriptor: String? = provider.getPaymentStatementDescriptor()
        var require3DSecure: Boolean = provider
                .getRequires3DSecure() // this.dataSource.getRequires3DSecure() || this.chargeRequires3DSecure();
        val receipt: Receipt? = provider.getReceiptSettings()
        val destinations: Destinations? = provider.getDestination()
        val merchant: Merchant? = provider.getMerchant()
        val cardIssuer: CardIssuer? = provider.getCardIssuer()
        val topUp: TopUp? = provider.getTopUp()
        val transactionMode: TransactionMode = provider.getTransactionMode()
        Log.d("PaymentProcessManager", "transactionMode : $transactionMode")
        //        Log.d("PaymentProcessManager", "topUp : " + topUp.toString());
        /**
         * Condition added for 3Ds for merchant
         */
        if (paymentOption.threeDS != null) {
            if (paymentOption.threeDS.equals("N")) {
                require3DSecure = false
            } else if (paymentOption.threeDS.equals("Y")) {
                require3DSecure = true
            } else if (paymentOption.threeDS.equals("U")) {
                require3DSecure = provider.getRequires3DSecure()
            }
        }
        when (transactionMode) {
            TransactionMode.PURCHASE -> {
                val chargeRequest = transactionCurrency?.amount?.let {
                    amountedCurrency?.currency?.let { it1 ->
                        CreateChargeRequest(
                                merchant,
                                it,
                                transactionCurrency?.currency,
                                amountedCurrency?.amount,
                                it1,
                                customer,
                                fee,
                                order,
                                redirect,
                                post,
                                source,
                                paymentDescription,
                                paymentMetadata,
                                reference,
                                shouldSaveCard,
                                statementDescriptor,
                                require3DSecure,
                                receipt,
                                destinations,
                                topUp
                        )
                    }
                }

                val jsonString = Gson().toJson(chargeRequest)
                if (LocalizationManager.getLocale(context).language == "en")
                    NetworkController.getInstance().processRequest(
                            TapMethodType.POST, ApiService.CHARGES, jsonString, this, CHARGE_REQ_CODE
                    )
            }
          /*  TransactionMode.AUTHORIZE_CAPTURE -> {
                val authorizeAction: AuthorizeAction = provider.getAuthorizeAction()
                *//* System.out.println(">>> ["+transactionCurrency.getAmount()+"]");
                System.out.println(">>> ["+transactionCurrency.getCurrency()+"]");
                System.out.println(">>> ["+amountedCurrency.getAmount()+"]");
                System.out.println(">>> ["+amountedCurrency.getCurrency()+"]");*//*
                val authorizeRequest = CreateAuthorizeRequest(
                        merchant,
                        transactionCurrency.getAmount(),
                        transactionCurrency.getCurrency(),
                        amountedCurrency.getAmount(),
                        amountedCurrency.getCurrency(),
                        customer,
                        fee,
                        order,
                        redirect,
                        post,
                        source,
                        paymentDescription,
                        paymentMetadata,
                        reference,
                        shouldSaveCard,
                        statementDescriptor,
                        require3DSecure,
                        receipt,
                        authorizeAction,
                        destinations,
                        topUp
                )

            }*/
          /*  TransactionMode.SAVE_CARD -> {
                val saveCardRequest = CreateSaveCardRequest(
                        amountedCurrency.getCurrency(),
                        customer,
                        order,
                        redirect,
                        post,
                        source,
                        paymentDescription,
                        paymentMetadata,
                        reference,
                        true,
                        statementDescriptor,
                        require3DSecure,
                        receipt,
                        true,
                        true,
                        true,
                        true,
                        true,
                        cardIssuer
                )

            }*/
        }
    }

    private fun fireWebPaymentCallBack(charge: Charge?) {
        when (charge?.status) {
            ChargeStatus.CAPTURED, ChargeStatus.AUTHORIZED -> try {
                // closePaymentActivity()
                sdkSession.getListener()?.paymentSucceed(charge)
                //  SDKSession().getListener()?.paymentSucceed(charge)
            } catch (e: Exception) {
                Log.e(TAG, "fireWebPaymentCallBack: ", e)
                Log.d("cardrepo", " Error while calling fireWebPaymentCallBack >>> method paymentSucceed(charge)")
                //closePaymentActivity()
            }
            ChargeStatus.FAILED, ChargeStatus.ABANDONED, ChargeStatus.CANCELLED, ChargeStatus.DECLINED, ChargeStatus.RESTRICTED, ChargeStatus.UNKNOWN, ChargeStatus.TIMEDOUT -> try {
                //closePaymentActivity()
                SDKSession.getListener()?.paymentFailed(charge)

            } catch (e: Exception) {
                Log.d("cardrepo", " Error while calling fireWebPaymentCallBack >>> method paymentFailed(charge)")
                // closePaymentActivity()
            }
        }
    }


}

