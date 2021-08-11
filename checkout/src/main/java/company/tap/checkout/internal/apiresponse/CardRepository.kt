package company.tap.checkout.internal.apiresponse

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.google.gson.JsonElement
import company.tap.checkout.internal.PaymentDataProvider
import company.tap.checkout.internal.api.enums.*
import company.tap.checkout.internal.api.models.*
import company.tap.checkout.internal.api.models.Merchant
import company.tap.checkout.internal.api.requests.*
import company.tap.checkout.internal.api.responses.*
import company.tap.checkout.internal.enums.PaymentTypeEnum
import company.tap.checkout.internal.interfaces.IPaymentDataProvider
import company.tap.checkout.internal.utils.AmountCalculator
import company.tap.checkout.internal.viewmodels.TapLayoutViewModel

import company.tap.checkout.open.CheckoutFragment
import company.tap.checkout.open.controller.SDKSession
import company.tap.checkout.open.controller.SDKSession.tabAnimatedActionButton
import company.tap.checkout.open.data_managers.PaymentDataSource
import company.tap.checkout.open.enums.TransactionMode
import company.tap.checkout.open.models.*
import company.tap.checkout.open.models.AuthorizeAction
import company.tap.checkout.open.models.Receipt
import company.tap.checkout.open.models.Reference
import company.tap.tapnetworkkit.controller.NetworkController
import company.tap.tapnetworkkit.enums.TapMethodType
import company.tap.tapnetworkkit.exception.GoSellError
import company.tap.tapnetworkkit.interfaces.APIRequestCallback
import company.tap.tapuilibrary.uikit.enums.ActionButtonState
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
    lateinit var listCardsResponse: ListCardsResponse
    lateinit var binLookupResponse: BINLookupResponse
    lateinit var authorizeActionResponse: Authorize
    lateinit var deleteCardResponse: DeleteCardResponse
    lateinit var saveCardResponse: SaveCard
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
         NetworkController.getInstance().processRequest(
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
        NetworkController.getInstance().processRequest(
                TapMethodType.POST, ApiService.PAYMENT_TYPES, jsonString, this, PAYMENT_OPTIONS_CODE
        )
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
    fun retrieveSaveCard(context: Context, viewModel: TapLayoutViewModel) {
        this.viewModel = viewModel
        NetworkController.getInstance().processRequest(TapMethodType.GET, ApiService.SAVE_CARD_ID + saveCardResponse.id, null, this,
                RETRIEVE_SAVE_CARD_CODE
        )
    }
    @RequiresApi(Build.VERSION_CODES.N)
    fun callDeleteCardAPI(context: Context, viewModel: TapLayoutViewModel, deleteCardId: String?, customerId: String?) {
        this.viewModel = viewModel
        NetworkController.getInstance().processRequest(TapMethodType.DELETE, ApiService.DELETE_CARD + "/" + customerId + "/" + deleteCardId, null, this,
                DEL_SAVE_CARD_CODE
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
    fun createSaveCard(
            context: Context,
            viewModel: TapLayoutViewModel,
            selectedPaymentOption: PaymentOption?,
            identifier: String?
    ) {
        this.viewModel = viewModel

        if(identifier!=null)callChargeOrAuthorizeOrSaveCardAPI(SourceRequest(identifier), selectedPaymentOption, null, null, context)
        else
            selectedPaymentOption?.sourceId?.let { SourceRequest(it) }?.let { callChargeOrAuthorizeOrSaveCardAPI(it, selectedPaymentOption, null, null, context) }
    }
    @RequiresApi(Build.VERSION_CODES.N)
    fun requestAuthenticateForChargeTransaction(
        viewModel: TapLayoutViewModel,
        chargeResponse: Charge
    ) {
        NetworkController.getInstance().processRequest(TapMethodType.PUT, ApiService.CHARGES+"/"+ ApiService.AUTHENTICATE+ "/" + chargeResponse.id,null,
            this, AUTHENTICATE_CODE
        )
        }


    @RequiresApi(Build.VERSION_CODES.N)
    fun createTokenWithExistingCard(
            context: Context, viewModel: TapLayoutViewModel, createTokenSavedCard: CreateTokenSavedCard
    ) {
        this.viewModel = viewModel
        val createTokenSavedCardReq: CreateTokenWithExistingCardDataRequest = CreateTokenWithExistingCardDataRequest.Builder(createTokenSavedCard).build()

        val jsonString = Gson().toJson(createTokenSavedCardReq)
        NetworkController.getInstance().processRequest(TapMethodType.POST, ApiService.TOKEN, jsonString,
                this, CREATE_SAVE_EXISTING_CODE
        )
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun  authenticate(context: Context, viewModel: TapLayoutViewModel,otpCode:String) {
        this.viewModel = viewModel
        val createOTPVerificationRequest: CreateOTPVerificationRequest = CreateOTPVerificationRequest.Builder(AuthenticationType.OTP, otpCode).build()

        val jsonString = Gson().toJson(createOTPVerificationRequest)
        NetworkController.getInstance().processRequest(TapMethodType.POST, ApiService.CHARGES+"/"+ ApiService.AUTHENTICATE+ "/" + chargeResponse.id, jsonString,
                this, CHARGE_REQ_CODE
        )
    }
    @RequiresApi(Build.VERSION_CODES.N)
    fun  authenticateAuthorizeTransaction(context: Context, viewModel: TapLayoutViewModel,otpCode:String) {
        this.viewModel = viewModel
        val createOTPVerificationRequest: CreateOTPVerificationRequest = CreateOTPVerificationRequest.Builder(AuthenticationType.OTP, otpCode).build()

        val jsonString = Gson().toJson(createOTPVerificationRequest)
        NetworkController.getInstance().processRequest(TapMethodType.POST, ApiService.AUTHORIZE+"/"+ ApiService.AUTHENTICATE+ "/" + authorizeActionResponse.id, jsonString,
                this, CREATE_AUTHORIZE_CODE
        )
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun requestAuthenticateForAuthorizeTransaction(viewModel: TapLayoutViewModel, authorize: Authorize?) {
        NetworkController.getInstance().processRequest(TapMethodType.PUT, ApiService.AUTHORIZE+"/"+ ApiService.AUTHENTICATE+ "/" + authorize?.id,null,
            this, AUTHENTICATE_CODE
        )
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun listAllCards(viewModel: TapLayoutViewModel, customerId: String?) {
        NetworkController.getInstance().processRequest(TapMethodType.GET, ApiService.LIST_CARD+"/"+customerId,null,
            this, LIST_ALL_CARD_CODE
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
                viewModel.displayStartupLayout(CheckoutFragment().enableSections())
                sdkSession?.sessionDelegate?.sessionHasStarted()
                if(tabAnimatedActionButton!=null){
                    tabAnimatedActionButton?.changeButtonState(ActionButtonState.SUCCESS)
                }

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
                if(::binLookupResponse.isInitialized)
                viewModel.setBinLookupData(binLookupResponse, context, cardViewModel)
                PaymentDataSource?.setBinLookupResponse(binLookupResponse)
            }
        }
        else if(requestCode == CREATE_TOKEN_CODE){
            response?.body().let {
                tokenResponse = Gson().fromJson(it, Token::class.java)
                if(tokenResponse!=null) {
                    if (PaymentDataSource.getTransactionMode() == TransactionMode.AUTHORIZE_CAPTURE) {
                        createAuthorizeRequest(context, viewModel, null, tokenResponse.id)

                    }
                    else if(PaymentDataSource.getTransactionMode()==TransactionMode.TOKENIZE_CARD){
                        SDKSession?.getListener()?.cardTokenizedSuccessfully(tokenResponse)
                    }
                    else if(PaymentDataSource.getTransactionMode()==TransactionMode.SAVE_CARD){
                        createSaveCard(context, viewModel, null, tokenResponse.id)
                    }
                    else {
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
                           if(authorizeActionResponse?.transaction.url != null){
                               authorizeActionResponse?.transaction?.url?.let { it1 -> viewModel?.displayRedirect(it1) }
                           }else handleAuthorizeResponse(authorizeActionResponse)

                    }else  handleAuthorizeResponse(authorizeActionResponse)


            }
        }
        else if(requestCode == DEL_SAVE_CARD_CODE){
            response?.body().let {
                deleteCardResponse = Gson().fromJson(it, DeleteCardResponse::class.java)
                println("deleteCardResponse is" + deleteCardResponse)
                viewModel?.deleteSelectedCardListener(deleteCardResponse)
            }
        }
        else if(requestCode == CREATE_SAVE_CARD){
            println("data in resp body>>" + response?.body())
            response?.body().let {
                saveCardResponse = Gson().fromJson(it, SaveCard::class.java)
                if(saveCardResponse?.status?.name == ChargeStatus.INITIATED.name){
                    saveCardResponse?.transaction?.url?.let { it1 -> viewModel?.displayRedirect(it1) }

                }else {
                    handleSaveCardResponse(saveCardResponse)
                }
            }

        }
        else if(requestCode == RETRIEVE_SAVE_CARD_CODE){

            response?.body().let {
                saveCardResponse = Gson().fromJson(it, SaveCard::class.java)
                println("saveCardResponse value is>>>>" + saveCardResponse.status.name)
                sdkSession.getListener()?.cardSaved(saveCardResponse)
            }


        }
        else if(requestCode == RETRIEVE_AUTHORIZE_CODE){
            response?.body().let {
                authorizeActionResponse = Gson().fromJson(it, Authorize::class.java)
                println("authorizeActionResponse value is>>>>" + authorizeActionResponse.status.name)
                sdkSession.getListener()?.authorizationSucceed(authorizeActionResponse)
            }
        }
        else if(requestCode == CREATE_SAVE_EXISTING_CODE){
            response?.body().let {
                tokenResponse = Gson().fromJson(it, Token::class.java)
                println("CREATE_SAVE_EXISTING_CODE tokenResponse >>>>" + tokenResponse)
                if(tokenResponse!=null) {
                    if (PaymentDataSource.getTransactionMode() == TransactionMode.AUTHORIZE_CAPTURE) {
                        createAuthorizeRequest(context, viewModel, null, tokenResponse.id)

                    }
                    else if(PaymentDataSource.getTransactionMode()==TransactionMode.TOKENIZE_CARD){
                        SDKSession?.getListener()?.cardTokenizedSuccessfully(tokenResponse)
                        SDKSession?.tabAnimatedActionButton?.changeButtonState(ActionButtonState.SUCCESS)
                    }
                    else if(PaymentDataSource.getTransactionMode()==TransactionMode.SAVE_CARD){
                        createSaveCard(context, viewModel, null, tokenResponse.id)
                    }
                    else {
                        createChargeRequest(context, viewModel, null, tokenResponse.id)

                    }
                }
            }
        }
        else if(requestCode == AUTHENTICATE_CODE){
            response?.body().let {
                chargeResponse = Gson().fromJson(it, Charge::class.java)
                println("AUTHENTICATE_CODE tokenResponse >>>>" +  response?.body())
                if(chargeResponse is Authorize)handleAuthorizeResponse(chargeResponse as Authorize)
                else
                    handleChargeResponse(chargeResponse)
            }
        }
        else if(requestCode == LIST_ALL_CARD_CODE){
            response?.body().let {
                listCardsResponse = Gson().fromJson(it, ListCardsResponse::class.java)
                println("LIST_ALL_CARD_CODE >>>>" + listCardsResponse)
                println("")
            }
        }

        if(::chargeResponse.isInitialized)
        CardViewState.ChargeViewState(charge = chargeResponse)
        if(::binLookupResponse.isInitialized)
            CardViewState.BinLookupViewState(binLookupResponse = binLookupResponse)
        if(initResponse!=null && paymentOptionsResponse!=null){
            val viewState = CardViewState(
                    initResponse = initResponse,
                    paymentOptionsResponse = paymentOptionsResponse
            )
            viewModel.getDatasfromAPIs(initResponse, paymentOptionsResponse)

            resultObservable.onNext(viewState)
            resultObservable.onComplete()
        }
        if( ::chargeResponse.isInitialized && chargeResponse!=null){
            if(::viewModel.isInitialized)
                chargeResponse?.transaction?.url?.let { viewModel.displayRedirect(it) }

        }



    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun handleChargeResponse(chargeResponse: Charge) {
        if (chargeResponse == null) return
        Log.e("Charge status", (chargeResponse.status).name)
        if (chargeResponse is Authorize) {
            handleAuthorizeResponse(chargeResponse)
        }else
        when (chargeResponse.status) {
            ChargeStatus.INITIATED -> {
                if (chargeResponse.authenticate != null) {
                    var authenicate: Authenticate = chargeResponse.authenticate
                    if (authenicate != null && authenicate.status == AuthenticationStatus.INITIATED) {
                        when (authenicate.type) {
                            AuthenticationType.BIOMETRICS -> "d"
                            AuthenticationType.OTP -> {
                                Log.d("cardREpose", " coming charge type is ...  caller setChargeOrAuthorize");
                                PaymentDataSource?.setChargeOrAuthorize(chargeResponse)
                                viewModel?.displayOTPView(chargeResponse?.customer?.getPhone(), PaymentTypeEnum.SAVEDCARD.toString(), chargeResponse)

                            }

                        }
                    }
                }
            }
            ChargeStatus.CAPTURED -> {
                SDKSession.getListener()?.paymentSucceed(chargeResponse)
            }
            ChargeStatus.AUTHORIZED -> {
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

    private fun handleAuthorizeResponse(authorize: Authorize) {
        Log.d("cardRepository", " Cards >> didReceiveAuthorize * * * ")
        if (authorize == null) return

        when (authorize.status) {
            ChargeStatus.INITIATED -> {
                val authenticate: Authenticate = authorize.authenticate
                if (authenticate != null && authenticate.status === AuthenticationStatus.INITIATED) {
                    when (authenticate.type) {
                        AuthenticationType.BIOMETRICS -> {
                        }
                        AuthenticationType.OTP -> {
                            // PaymentDataManager.getInstance().setChargeOrAuthorize(authorize as Authorize?)
                            viewModel?.displayOTPView(authorize?.customer.getPhone(), PaymentTypeEnum.SAVEDCARD.toString(), authorize as Authorize)
                        }
                    }
                }
            }
            ChargeStatus.CAPTURED, ChargeStatus.AUTHORIZED -> try {

                SDKSession.getListener()?.authorizationSucceed(authorize)
            } catch (e: java.lang.Exception) {
                Log.d("cardRepository", " Error while calling delegate method authorizationSucceed(authorize)")
                // closePaymentActivity()
            }
            ChargeStatus.FAILED, ChargeStatus.ABANDONED, ChargeStatus.CANCELLED, ChargeStatus.DECLINED, ChargeStatus.RESTRICTED -> try {
                //    closePaymentActivity()
                SDKSession.getListener()?.authorizationFailed(authorize)
            } catch (e: java.lang.Exception) {
                Log.d("cardRepository", " Error while calling delegate method authorizationFailed(authorize)")

            }
        }
    }

    private fun handleSaveCardResponse(saveCard: SaveCard) {
        // Log.d("CardRepository"," Cards >> didReceiveSaveCard * * * " + saveCard);
        if (saveCard == null) return
        //   Log.d("CardRepository"," Cards >> didReceiveSaveCard * * * status :" + saveCard.getStatus());
        when (saveCard.status) {
            ChargeStatus.INITIATED -> {
                val authenticate: Authenticate = saveCard.authenticate
                if (authenticate != null && authenticate.status === AuthenticationStatus.INITIATED) {
                    when (authenticate.type) {
                        AuthenticationType.BIOMETRICS -> {
                        }
                        AuthenticationType.OTP -> {
                            Log.d("CardRepository", " start otp for save card mode........")
                            // PaymentDataManager.getInstance().setChargeOrAuthorize(saveCard)
                            //  openOTPScreen(saveCard)
                        }
                    }
                }
            }
            ChargeStatus.CAPTURED, ChargeStatus.AUTHORIZED, ChargeStatus.VALID -> try {
                SDKSession.getListener()?.cardSaved(saveCard)
            } catch (e: java.lang.Exception) {
                Log.d("CardRepository", " Error while calling delegate method cardSaved(saveCard)")

            }
            ChargeStatus.INVALID, ChargeStatus.FAILED, ChargeStatus.ABANDONED, ChargeStatus.CANCELLED, ChargeStatus.DECLINED, ChargeStatus.RESTRICTED -> try {
                SDKSession.getListener()?.cardSavingFailed(saveCard)
            } catch (e: java.lang.Exception) {
                Log.d("CardRepository", " Error while calling delegate method cardSavingFailed(saveCard)")

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
        private const val CREATE_SAVE_CARD = 9
        private const val RETRIEVE_SAVE_CARD_CODE = 10
        private const val DEL_SAVE_CARD_CODE = 11
        private const val CREATE_SAVE_EXISTING_CODE = 12
        private const val AUTHENTICATE_CODE = 13
        private const val LIST_ALL_CARD_CODE = 14


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
        println( "  --->>> extraFeeeeeesee--->>  "+ paymentOption?.extraFees)

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
        println("paymentOption?.threeDS" + paymentOption?.threeDS)
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
                NetworkController.getInstance().processRequest(
                        TapMethodType.POST,
                        ApiService.AUTHORIZE,
                        jsonString,
                        this,
                        CREATE_AUTHORIZE_CODE
                )

            }
            TransactionMode.SAVE_CARD -> {
                val saveCardRequest = amountedCurrency?.currency?.let {
                    CreateSaveCardRequest(
                            it,
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
                }
                val jsonString = Gson().toJson(saveCardRequest)
                NetworkController.getInstance().processRequest(
                        TapMethodType.POST,
                        ApiService.SAVE_CARD,
                        jsonString,
                        this,
                        CREATE_SAVE_CARD
                )

            }
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

