package company.tap.checkout.internal.apiresponse

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.google.gson.JsonElement
import company.tap.checkout.internal.PaymentDataProvider
import company.tap.checkout.internal.api.enums.AuthenticationStatus
import company.tap.checkout.internal.api.enums.AuthenticationType
import company.tap.checkout.internal.api.enums.ChargeStatus
import company.tap.checkout.internal.api.enums.URLStatus
import company.tap.checkout.internal.api.models.*
import company.tap.checkout.internal.api.requests.CreateAuthorizeRequest
import company.tap.checkout.internal.api.requests.CreateChargeRequest
import company.tap.checkout.internal.api.requests.CreateTokenWithCardDataRequest
import company.tap.checkout.internal.api.requests.PaymentOptionsRequest
import company.tap.checkout.internal.api.responses.PaymentOptionsResponse
import company.tap.checkout.internal.api.responses.SDKSettings
import company.tap.checkout.internal.interfaces.IPaymentDataProvider
import company.tap.checkout.internal.utils.AmountCalculator
import company.tap.checkout.internal.viewmodels.TapLayoutViewModel
import company.tap.checkout.open.controller.SDKSession
import company.tap.checkout.open.data_managers.PaymentDataSource
import company.tap.checkout.open.enums.TransactionMode
import company.tap.checkout.open.models.*
import company.tap.checkout.open.models.AuthorizeAction
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
    @JvmField
    var paymentOptionsResponse :PaymentOptionsResponse?= null
    private var initResponse:SDKSettings?=null
    lateinit var chargeResponse:Charge
    lateinit var binLookupResponse: BINLookupResponse
    lateinit var authorizeActionResponse: Authorize
    lateinit var tokenResponse: Token
    lateinit var context: Context
    private lateinit var viewModel: TapLayoutViewModel
    private lateinit var cardViewModel: CardViewModel

    private var sdkSession : SDKSession = SDKSession
    private var dataProvider: IPaymentDataProvider= PaymentDataProvider()

    fun getDataProvider(): IPaymentDataProvider {
        return dataProvider
    }
    @RequiresApi(Build.VERSION_CODES.N)
    fun getInitData(context: Context, viewModel: TapLayoutViewModel, cardViewModel: CardViewModel?) {
        this.viewModel = viewModel
        if (cardViewModel != null) {
            this.cardViewModel = cardViewModel
        }
        if( LocalizationManager.getLocale(context).language  == "en") NetworkController.getInstance().processRequest(
                TapMethodType.GET,
                ApiService.INIT,
                null,
                this,
                INIT_CODE
        )
        this.context = context
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
    fun createChargeRequest(context: Context, viewModel: TapLayoutViewModel, selectedPaymentOption: PaymentOption?, identifier: String?) {
        this.viewModel = viewModel
        if(identifier!=null)callChargeOrAuthorizeOrSaveCardAPI(SourceRequest(identifier), selectedPaymentOption, null, null, context)
        else
            selectedPaymentOption?.sourceId?.let { SourceRequest(it) }?.let { callChargeOrAuthorizeOrSaveCardAPI(it, selectedPaymentOption, null, null, context) }
    }


    @RequiresApi(Build.VERSION_CODES.N)
    fun retrieveChargeRequest(context: Context, viewModel: TapLayoutViewModel) {
        this.viewModel = viewModel
        NetworkController.getInstance().processRequest(TapMethodType.GET, ApiService.CHARGE_ID + chargeResponse?.id, null,
                this, CHARGE_RETRIEVE_CODE
        )
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun createAuthorizeRequest(
            context: Context,
            viewModel: TapLayoutViewModel,
            selectedPaymentOption: PaymentOption?,
            identifier: String?
    ){
        this.viewModel = viewModel

        if(identifier!=null)callChargeOrAuthorizeOrSaveCardAPI(SourceRequest(identifier), selectedPaymentOption, null, null, context)
        else
            selectedPaymentOption?.sourceId?.let { SourceRequest(it) }?.let { callChargeOrAuthorizeOrSaveCardAPI(it, selectedPaymentOption, null, null, context) }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun retrieveAuthorizeRequest(context: Context, viewModel: TapLayoutViewModel) {
        this.viewModel = viewModel
        NetworkController.getInstance().processRequest(TapMethodType.GET, ApiService.AUTHORIZE_ID + authorizeActionResponse?.id, null,
                this, RETRIEVE_AUTHORIZE_CODE
        )
    }


    @RequiresApi(Build.VERSION_CODES.N)
    fun retrieveBinLookup(context: Context, viewModel: TapLayoutViewModel, binValue: String?) {
        this.viewModel = viewModel
        NetworkController.getInstance().processRequest(TapMethodType.GET, ApiService.BIN + binValue, null,
                this, BIN_RETRIEVE_CODE
        )
    }
    @RequiresApi(Build.VERSION_CODES.N)
    fun createTokenWithEncryptedCard(context: Context, viewModel: TapLayoutViewModel, createTokenWithCardDataRequest: CreateTokenCard?) {
        this.viewModel = viewModel
        val createTokenWithCardDataReq = createTokenWithCardDataRequest?.let { CreateTokenWithCardDataRequest(it) }
        val jsonString = Gson().toJson(createTokenWithCardDataReq)
        NetworkController.getInstance().processRequest(TapMethodType.POST, ApiService.TOKEN, jsonString,
                this, CREATE_TOKEN_CODE
        )
    }
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onSuccess(responseCode: Int, requestCode: Int, response: Response<JsonElement>?) {
        if (requestCode == INIT_CODE) {
            response?.body().let {
                initResponse = Gson().fromJson(it, SDKSettings::class.java)
                PaymentDataSource.setSDKSettings(initResponse)
            }
        }else if (requestCode == PAYMENT_OPTIONS_CODE){
            response?.body().let {
                paymentOptionsResponse = Gson().fromJson(it, PaymentOptionsResponse::class.java)
                PaymentDataSource.setPaymentOptionsResponse(paymentOptionsResponse)
            }


        }else if(requestCode == CHARGE_REQ_CODE) {
            response?.body().let {
                chargeResponse = Gson().fromJson(it, Charge::class.java)

            }
            handleChargeResponse(chargeResponse)
        }
        else if(requestCode == CHARGE_RETRIEVE_CODE) {
            response?.body().let {
                chargeResponse = Gson().fromJson(it, Charge::class.java)
                if(chargeResponse?.status?.name == ChargeStatus.CAPTURED.name){
                    fireWebPaymentCallBack(chargeResponse)
                    chargeResponse?.transaction?.url?.let { it1 -> viewModel?.displayRedirect(it1) }
                  //  viewModel?.redirectLoadingFinished(true)
                }
            }
            handleChargeResponse(chargeResponse)
        }
        else if(requestCode == BIN_RETRIEVE_CODE){
            response?.body().let {
                binLookupResponse = Gson().fromJson(it, BINLookupResponse::class.java)
               println("binLookupResponse value is>>>>" + binLookupResponse)
                if(::binLookupResponse.isInitialized)
                viewModel.setBinLookupData(binLookupResponse, context, cardViewModel)
            }
        }
        else if(requestCode == CREATE_TOKEN_CODE){
            response?.body().let {
                tokenResponse = Gson().fromJson(it, Token::class.java)
                if(tokenResponse!=null) {
                    if (PaymentDataSource.getTransactionMode() == TransactionMode.AUTHORIZE_CAPTURE) {
                        createAuthorizeRequest(context, viewModel, null, tokenResponse.id)

                    } else {
                        createChargeRequest(context, viewModel, null, tokenResponse.id)

                    }
                }
            }
        }
        else if(requestCode == CREATE_AUTHORIZE_CODE){
            response?.body().let {
                authorizeActionResponse = Gson().fromJson(it, Authorize::class.java)
               println("authorizeActionResponse value is>>>>" + authorizeActionResponse.status.name)

                    if(authorizeActionResponse?.status?.name == ChargeStatus.INITIATED.name){
                       // fireWebPaymentCallBack(authorizeActionResponse)
                        authorizeActionResponse?.transaction?.url?.let { it1 -> viewModel?.displayRedirect(it1) }
                      //  viewModel?.redirectLoadingFinished(true)

                    }else  handleChargeResponse(authorizeActionResponse)


            }
        }
        else if(requestCode == RETRIEVE_AUTHORIZE_CODE){
            response?.body().let {
                authorizeActionResponse = Gson().fromJson(it, Authorize::class.java)
                println("authorizeActionResponse value is>>>>" + authorizeActionResponse.status.name)
                sdkSession.getListener()?.authorizationSucceed(authorizeActionResponse)
            }
        }
        val viewState = CardViewState(
                initResponse = initResponse,
                paymentOptionsResponse = paymentOptionsResponse
        )
        if(::chargeResponse.isInitialized)
        CardViewState.ChargeViewState(charge = chargeResponse)
        if(::binLookupResponse.isInitialized)
            CardViewState.BinLookupViewState(binLookupResponse = binLookupResponse)
        if(initResponse!=null && paymentOptionsResponse!=null){
            viewModel.getDatasfromAPIs(initResponse, paymentOptionsResponse)
            resultObservable.onNext(viewState)
            resultObservable.onComplete()
        }
        if( ::chargeResponse.isInitialized && chargeResponse!=null){
            if(::viewModel.isInitialized)
                chargeResponse?.transaction?.url?.let { viewModel.displayRedirect(it) }

        }



    }

    private fun handleChargeResponse(chargeResponse: Charge) {
        if (chargeResponse == null) return
        Log.e("Charge status", (chargeResponse.status).name)
        when (chargeResponse.status) {
            ChargeStatus.INITIATED -> {
                if (chargeResponse.authenticate != null) {
                    var authenicate: Authenticate = chargeResponse.authenticate
                    if (authenicate != null && authenicate.status == AuthenticationStatus.INITIATED) {
                        when (authenicate.type) {
                            AuthenticationType.BIOMETRICS -> "d"
                            AuthenticationType.OTP ->{
                                Log.d("cardREpose", " coming charge type is ...  caller setChargeOrAuthorize");
                                PaymentDataSource?.setChargeOrAuthorize(chargeResponse)
                                //  openOTPScreen(charge)
                            }

                        }
                    }
                }
            }
            ChargeStatus.CAPTURED -> {
                SDKSession.getListener()?.paymentSucceed(chargeResponse)

            }
            ChargeStatus.AUTHORIZED ->{
                SDKSession.getListener()?.authorizationSucceed(chargeResponse as Authorize)
            }
            ChargeStatus.FAILED -> SDKSession.getListener()?.paymentFailed(chargeResponse)
            ChargeStatus.ABANDONED -> SDKSession.getListener()?.paymentFailed(chargeResponse)
            ChargeStatus.CANCELLED -> SDKSession.getListener()?.paymentFailed(chargeResponse)
            ChargeStatus.DECLINED -> SDKSession.getListener()?.paymentFailed(chargeResponse)
            ChargeStatus.RESTRICTED -> SDKSession.getListener()?.paymentFailed(chargeResponse)
            ChargeStatus.UNKNOWN -> SDKSession.getListener()?.paymentFailed(chargeResponse)
            ChargeStatus.TIMEDOUT -> SDKSession.getListener()?.paymentFailed(chargeResponse)
            ChargeStatus.IN_PROGRESS -> {
                if (chargeResponse.transaction != null && chargeResponse.transaction.asynchronous) {
                    println("open INPROGRESS")
                }
            }
        }
    }


    override fun onFailure(requestCode: Int, errorDetails: GoSellError?) {
        errorDetails?.let {
            if (it.throwable != null) {
                resultObservable.onError(it.throwable)
                sdkSession.getListener()?.sdkError(errorDetails)
            }else
            // resultObservable.onError(Throwable(it.errorMessage))
                RxJavaPlugins.setErrorHandler(Throwable::printStackTrace)
            sdkSession.getListener()?.backendUnknownError(errorDetails.errorMessage)

        }
    }

    companion object {
        private const val INIT_CODE = 1
        private const val PAYMENT_OPTIONS_CODE = 2
        private const val CHARGE_REQ_CODE = 3
        private const val CHARGE_RETRIEVE_CODE = 4
        private const val BIN_RETRIEVE_CODE = 5
        private const val CREATE_TOKEN_CODE = 6
        private const val CREATE_AUTHORIZE_CODE = 7
        private const val RETRIEVE_AUTHORIZE_CODE = 8
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun callChargeOrAuthorizeOrSaveCardAPI(source: SourceRequest,
                                                   paymentOption: PaymentOption?,
                                                   cardBIN: String?, saveCard: Boolean?, context: Context) {
        Log.e("OkHttp", "CALL CHARGE API OR AUTHORIZE API")
        val provider: IPaymentDataProvider = getDataProvider()
        val supportedCurrencies: java.util.ArrayList<SupportedCurrencies>? = provider.getSupportedCurrencies()
        val orderID: String = provider.getPaymentOptionsOrderID()
        val postURL: String? = provider.getPostURL()
        val post = if (postURL == null) null else TrackingURL(URLStatus.PENDING, postURL)
        val amountedCurrency: AmountedCurrency? = provider.getSelectedCurrency()
        println("amountedCurrency in cad" + amountedCurrency)
        println("source in crad" + source)
        //     Log.d("callChargeOrAuthorizeOr"," step 5 : callChargeOrAuthorizeOrSaveCardAPI : in class "+ "["+this.getClass().getName()+"] with amountedCurrency=["+amountedCurrency.getAmount()+"]  ");
        val transactionCurrency: AmountedCurrency? = provider.getTransactionCurrency()
        println("transactionCurrency in cad" + transactionCurrency)
        val customer: TapCustomer = provider.getCustomer()
        var fee = BigDecimal.ZERO

        if (paymentOption != null) fee = AmountCalculator.calculateExtraFeesAmount(paymentOption.extraFees, supportedCurrencies, amountedCurrency)
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
        println("paymentOption?.threeDS"+paymentOption?.threeDS)
        //        Log.d("PaymentProcessManager", "topUp : " + topUp.toString());
        /**
         * Condition added for 3Ds for merchant
         */
        if (paymentOption?.threeDS != null) {
            if (paymentOption?.threeDS == "N") {
                require3DSecure = false
            } else if (paymentOption?.threeDS == "Y") {
                require3DSecure = true
            } else if (paymentOption?.threeDS == "U") {
                require3DSecure = provider?.getRequires3DSecure()
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
                println("chargere" + chargeRequest)

                val jsonString = Gson().toJson(chargeRequest)
                if (LocalizationManager.getLocale(context).language == "en")
                    NetworkController.getInstance().processRequest(
                            TapMethodType.POST, ApiService.CHARGES, jsonString, this, CHARGE_REQ_CODE
                    )
            }
            TransactionMode.AUTHORIZE_CAPTURE -> {
                val authorizeAction: AuthorizeAction? = provider.getAuthorizeAction()
                val authorizeRequest = authorizeAction?.let {
                    transactionCurrency?.amount?.let { it1 ->
                        amountedCurrency?.amount?.let { it2 ->
                            CreateAuthorizeRequest(
                                    merchant,
                                    it1,
                                    transactionCurrency?.currency,
                                    it2,
                                    amountedCurrency?.currency,
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
                                    it,
                                    destinations,
                                    topUp
                            )
                        }
                    }

                }
                val jsonString = Gson().toJson(authorizeRequest)
                println("jsonString for auth>>" + jsonString)
                if (LocalizationManager.getLocale(context).language == "en")
                    NetworkController.getInstance().processRequest(
                            TapMethodType.POST,
                            ApiService.AUTHORIZE,
                            jsonString,
                            this,
                            CREATE_AUTHORIZE_CODE
                    )

            }
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

