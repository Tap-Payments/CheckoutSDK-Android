package company.tap.checkout.internal.apiresponse

import SupportedCurrencies
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Handler
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentManager
import com.google.gson.Gson
import com.google.gson.JsonElement
import company.tap.checkout.TapCheckOutSDK
import company.tap.checkout.internal.PaymentDataProvider
import company.tap.checkout.internal.api.enums.*
import company.tap.checkout.internal.api.models.*
import company.tap.checkout.open.models.Merchant
import company.tap.checkout.internal.api.requests.*
import company.tap.checkout.internal.api.responses.*
import company.tap.checkout.internal.cache.SharedPrefManager
import company.tap.checkout.internal.cache.UserLocalCurrencyModelKey
import company.tap.checkout.internal.enums.PaymentTypeEnum
import company.tap.checkout.internal.enums.ThemeMode
import company.tap.checkout.internal.interfaces.IPaymentDataProvider
import company.tap.checkout.internal.utils.AmountCalculator
import company.tap.checkout.internal.utils.CustomUtils
import company.tap.checkout.internal.viewmodels.CheckoutViewModel
import company.tap.checkout.open.CheckOutActivity
import company.tap.checkout.open.controller.SDKSession
import company.tap.checkout.open.controller.SDKSession.activity
import company.tap.checkout.open.controller.SDKSession.contextSDK
import company.tap.checkout.open.controller.SDKSession.tabAnimatedActionButton
import company.tap.checkout.open.controller.SessionManager
import company.tap.checkout.open.data_managers.PaymentDataSource
import company.tap.checkout.open.enums.TransactionMode
import company.tap.checkout.open.models.*
import company.tap.checkout.open.models.AuthorizeAction
import company.tap.checkout.open.models.Receipt
import company.tap.checkout.open.models.Reference
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapnetworkkit.connection.NetworkApp
import company.tap.tapnetworkkit.controller.NetworkController
import company.tap.tapnetworkkit.enums.TapMethodType
import company.tap.tapnetworkkit.exception.GoSellError
import company.tap.tapnetworkkit.interfaces.APIRequestCallback
import company.tap.tapuilibraryy.themekit.ThemeManager
import company.tap.tapuilibraryy.uikit.enums.ActionButtonState
import company.tap.tapuilibraryy.uikit.views.TapBottomSheetDialog.Companion.TAG
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
    var paymentOptionsResponse: PaymentOptionsResponse? = null
    var merchantDataModel: MerchantData? = null
    var assetsModel: AssetsModel? = null

    lateinit var chargeResponse: Charge
    lateinit var chargeResponseForGoogle: Charge

    lateinit var listCardsResponse: ListCardsResponse
    lateinit var binLookupResponse: BINLookupResponse
    lateinit var authorizeActionResponse: Authorize
    lateinit var deleteCardResponse: DeleteCardResponse
    lateinit var saveCardResponse: SaveCard
    lateinit var tokenResponse: Token
    lateinit var supportFragmentManager: FragmentManager
    lateinit var cardRepositoryContext: Context
    private lateinit var viewModel: CheckoutViewModel
    private  var savedCardValue: Boolean = true
    private lateinit var cardViewModel: CardViewModel

    private var sdkSession: SDKSession = SDKSession
    private var dataProvider: IPaymentDataProvider = PaymentDataProvider()
    lateinit var jsonString: String
    private var configResponse: TapConfigResponseModel? = null
    private var selectedPaymentOption: PaymentOption? = null

    @JvmField
    var initResponseModel: InitResponseModel? = null
    var logsModel: LogsModel? = null
    fun getDataProvider(): IPaymentDataProvider {
        return dataProvider
    }


    @RequiresApi(Build.VERSION_CODES.N)
    fun getINITData(
        _context: Context,
        viewModel: CheckoutViewModel,
        cardViewModel: CardViewModel?,
        supportFragmentManagerdata: FragmentManager
    ) {
        this.viewModel = viewModel
        this.cardRepositoryContext = _context
        if (cardViewModel != null) {
            this.cardViewModel = cardViewModel
        }
        this.supportFragmentManager = supportFragmentManagerdata
        this.viewModel = viewModel
        val requestBody = PaymentOptionsRequest(
            PaymentDataSource.getTransactionMode(),
            PaymentDataSource.getAmount(),
            PaymentDataSource.getShipping(),
            PaymentDataSource.getTaxes(),
            PaymentDataSource.getDestination(),
            PaymentDataSource.getCurrency()?.isoCode,
            PaymentDataSource.getCustomer().identifier,
            PaymentDataSource.getMerchant()?.id,
            PaymentDataSource.getPaymentDataType().toString(),
            PaymentDataSource.getTopup(),
            PaymentDataSource.getOrderObject()
        )
        println("getTransactionMode" + PaymentDataSource.getTransactionMode())

        val jsonString = Gson().toJson(requestBody)
        NetworkController.getInstance().processRequest(
            TapMethodType.POST, ApiService.INIT, jsonString, this, INIT_CODE
        )
        this.supportFragmentManager = supportFragmentManagerdata

        tabAnimatedActionButton?.changeButtonState(ActionButtonState.LOADING)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun getCurrencyAPI(_context: Context,_viewModel: CheckoutViewModel){
        this.viewModel = _viewModel
        this.cardRepositoryContext = _context
        NetworkController.getInstance().processRequest(
            TapMethodType.GET, ApiService.CURRENCY_API,null,this, CURRENCY_CODE)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun createChargeRequest(
        viewModel: CheckoutViewModel,
        selectedPaymentOption: PaymentOption?,
        identifier: String?,
        saveCardValue: Boolean?
    ) {
        this.viewModel = viewModel
        if (identifier != null) callChargeOrAuthorizeOrSaveCardAPI(
            SourceRequest(identifier),
            selectedPaymentOption,
            null,
            saveCardValue
        )
        else
            selectedPaymentOption?.sourceId?.let { SourceRequest(it) }?.let {
                callChargeOrAuthorizeOrSaveCardAPI(
                    it,
                    selectedPaymentOption,
                    null,
                    saveCardValue
                )
            }
    }


    @RequiresApi(Build.VERSION_CODES.N)
    fun retrieveChargeRequest(context: Context, viewModel: CheckoutViewModel) {
        this.viewModel = viewModel
//        context.showToast(chargeResponseForGoogle.id.toString())
//        Log.e("chargeResponseForGoogle",chargeResponseForGoogle.id.toString())
        Log.e("chargeResponseForGoogle",viewModel.globalChargeResponse?.id.toString())

        NetworkController.getInstance().processRequest(
            TapMethodType.GET, ApiService.CHARGE_ID + if (::chargeResponse.isInitialized)chargeResponse.id else  viewModel.globalChargeResponse?.id , null,
            this, CHARGE_RETRIEVE_CODE
        )
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun createAuthorizeRequest(
        viewModel: CheckoutViewModel,
        selectedPaymentOption: PaymentOption?,
        identifier: String?
    ) {
        this.viewModel = viewModel

        if (identifier != null) callChargeOrAuthorizeOrSaveCardAPI(
            SourceRequest(identifier),
            selectedPaymentOption,
            null,
            null
        )
        else
            selectedPaymentOption?.sourceId?.let { SourceRequest(it) }?.let {
                callChargeOrAuthorizeOrSaveCardAPI(
                    it,
                    selectedPaymentOption,
                    null,
                    null
                )
            }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun retrieveAuthorizeRequest(context: Context, viewModel: CheckoutViewModel) {
        this.viewModel = viewModel
        NetworkController.getInstance().processRequest(
            TapMethodType.GET, ApiService.AUTHORIZE_ID + authorizeActionResponse?.id, null,
            this, RETRIEVE_AUTHORIZE_CODE
        )
    }


    @RequiresApi(Build.VERSION_CODES.N)
    fun retrieveBinLookup(viewModel: CheckoutViewModel, binValue: String?) {
        this.viewModel = viewModel
        val reqBody = BinLookUpRequestModel(binValue)
        jsonString = Gson().toJson(reqBody)
        NetworkController.getInstance().processRequest(
            TapMethodType.POST, ApiService.BIN, jsonString,
            this, BIN_RETRIEVE_CODE
        )
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun retrieveSaveCard(context: Context, viewModel: CheckoutViewModel) {
        this.viewModel = viewModel
        NetworkController.getInstance().processRequest(
            TapMethodType.GET, ApiService.SAVE_CARD_ID + saveCardResponse.id, null, this,
            RETRIEVE_SAVE_CARD_CODE
        )
    }



    @RequiresApi(Build.VERSION_CODES.N)
    fun callDeleteCardAPI(
        context: Context,
        viewModel: CheckoutViewModel,
        deleteCardId: String?,
        customerId: String?
    ) {
        this.viewModel = viewModel
        NetworkController.getInstance().processRequest(
            TapMethodType.DELETE,
            ApiService.DELETE_CARD + "/" + customerId + "/" + deleteCardId,
            null,
            this,
            DEL_SAVE_CARD_CODE
        )
        /**
         * Just for scope of testing to resemble delete function of chips without api
         */
//        var deleteCardResponses  = DeleteCardResponse(delete = true, deleted = true)
//
//        this.viewModel.deleteSelectedCardListener(deleteCardResponses)

    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun createTokenWithEncryptedCard(
        context: Context,
        viewModel: CheckoutViewModel,
        createTokenWithCardDataRequest: CreateTokenCard? , saveCardValue :Boolean?
    ) {
        this.viewModel = viewModel
        if (saveCardValue != null) {
            this.savedCardValue = saveCardValue
        }
        val createTokenWithCardDataReq = createTokenWithCardDataRequest?.let {
            CreateTokenWithCardDataRequest(
                it
            )
        }
        val jsonString = Gson().toJson(createTokenWithCardDataReq)
        NetworkController.getInstance().processRequest(
            TapMethodType.POST, ApiService.TOKEN, jsonString,
            this, CREATE_TOKEN_CODE
        )
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun createSaveCard(
        viewModel: CheckoutViewModel,
        selectedPaymentOption: PaymentOption?,
        identifier: String?
    ) {
        this.viewModel = viewModel

        if (identifier != null) callChargeOrAuthorizeOrSaveCardAPI(
            SourceRequest(identifier),
            selectedPaymentOption,
            null,
            null
        )
        else
            selectedPaymentOption?.sourceId?.let { SourceRequest(it) }?.let {
                callChargeOrAuthorizeOrSaveCardAPI(
                    it,
                    selectedPaymentOption,
                    null,
                    null
                )
            }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun requestAuthenticateForChargeTransaction(
        viewModel: CheckoutViewModel,
        chargeResponse: Charge
    ) {
        NetworkController.getInstance().processRequest(
            TapMethodType.PUT,
            ApiService.CHARGES + "/" + ApiService.AUTHENTICATE + "/" + chargeResponse.id,
            null,
            this,
            AUTHENTICATE_CODE
        )
    }


    @RequiresApi(Build.VERSION_CODES.N)
    fun createTokenWithExistingCard(
        context: Context, viewModel: CheckoutViewModel, createTokenSavedCard: CreateTokenSavedCard
    ) {
        this.viewModel = viewModel
        val createTokenSavedCardReq: CreateTokenWithExistingCardDataRequest =
            CreateTokenWithExistingCardDataRequest.Builder(
                createTokenSavedCard
            ).build()

        val jsonString = Gson().toJson(createTokenSavedCardReq)
        NetworkController.getInstance().processRequest(
            TapMethodType.POST, ApiService.TOKEN, jsonString,
            this, CREATE_SAVE_EXISTING_CODE
        )
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun createGoogleToken(
        context: Context,
        viewModel: CheckoutViewModel,
        createTokenGPayRequest: CreateTokenGPayRequest, selectedPaymentOption : PaymentOption
    ) {
        this.viewModel = viewModel
        this.selectedPaymentOption = selectedPaymentOption


        val jsonString = Gson().toJson(createTokenGPayRequest)
        NetworkController.getInstance().processRequest(
            TapMethodType.POST, ApiService.TOKEN, jsonString,
            this, CREATE_GOOGLE_TOKEN
        )
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun authenticate(context: Context, viewModel: CheckoutViewModel, otpCode: String) {
        this.viewModel = viewModel
        val createOTPVerificationRequest: CreateOTPVerificationRequest =
            CreateOTPVerificationRequest.Builder(
                AuthenticationType.OTP,
                otpCode
            ).build()

        val jsonString = Gson().toJson(createOTPVerificationRequest)
        if (::chargeResponse.isInitialized)
            NetworkController.getInstance().processRequest(
                TapMethodType.POST,
                ApiService.CHARGES + "/" + ApiService.AUTHENTICATE + "/" + chargeResponse.id,
                jsonString,
                this,
                CHARGE_REQ_CODE
            )
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun authenticateAuthorizeTransaction(
        context: Context,
        viewModel: CheckoutViewModel,
        otpCode: String
    ) {
        this.viewModel = viewModel
        val createOTPVerificationRequest: CreateOTPVerificationRequest =
            CreateOTPVerificationRequest.Builder(
                AuthenticationType.OTP,
                otpCode
            ).build()

        val jsonString = Gson().toJson(createOTPVerificationRequest)
        NetworkController.getInstance().processRequest(
            TapMethodType.POST,
            ApiService.AUTHORIZE + "/" + ApiService.AUTHENTICATE + "/" + authorizeActionResponse.id,
            jsonString,
            this,
            CREATE_AUTHORIZE_CODE
        )
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun requestAuthenticateForAuthorizeTransaction(
        viewModel: CheckoutViewModel,
        authorize: Authorize?
    ) {
        NetworkController.getInstance().processRequest(
            TapMethodType.PUT,
            ApiService.AUTHORIZE + "/" + ApiService.AUTHENTICATE + "/" + authorize?.id,
            null,
            this,
            AUTHENTICATE_CODE
        )
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun listAllCards(viewModel: CheckoutViewModel, customerId: String?) {
        NetworkController.getInstance().processRequest(
            TapMethodType.GET, ApiService.LIST_CARD + "/" + customerId, null,
            this, LIST_ALL_CARD_CODE
        )
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onSuccess(responseCode: Int, requestCode: Int, response: Response<JsonElement>?) {
        if (requestCode == USER_IP_CODE) {
            if (response?.body() != null) {
                val userIpAddressResponse = Gson().fromJson(response.body(), UserIpAddressResponse::class.java)
                NetworkApp.setUserIpAddress(userIpAddressResponse.IPv4 ?: "")
                CardViewModel().processEvent(event = CardViewEvent.CurrencyEvent ,viewModel= viewModel, context = cardRepositoryContext)
            }

        }
       else if (requestCode == CURRENCY_CODE) {
            if (response?.body() != null) {
                response.body().let {
                    val userLocalCurrencyModel = Gson().fromJson(it, UserLocalCurrencyModel::class.java)
                    val isSaved = cardRepositoryContext.let { it1 ->
                        SharedPrefManager.saveModelLocally(
                            context = cardRepositoryContext,
                            dataToBeSaved = userLocalCurrencyModel,
                            keyValueToBeSaved = UserLocalCurrencyModelKey
                        )
                    }
                    if (isSaved){
                        viewModel.localCurrencyReturned.value = true
                    }
                }
            }

        }
       else if (requestCode == INIT_CODE) {
            if (response?.body() != null) {
                response.body().let {

                    if (response.body() != null) {
                        response.body().let {
                            initResponseModel = Gson().fromJson(it, InitResponseModel::class.java)
                            paymentOptionsResponse = initResponseModel?.paymentOptionsResponse
                            merchantDataModel = initResponseModel?.merchant
                            assetsModel = initResponseModel?.assestsModel
                            //   logsModel = initResponseModel?.merchant?.logs?.name

                            if (LocalizationManager.currentLocalized.length() == 0) {
                                LocalizationManager.setLocale(cardRepositoryContext, Locale("en"))
                                LocalizationManager.loadTapLocale(
                                    cardRepositoryContext,
                                    assetsModel?.localisation?.urlLocale.toString()
                                )
                            }
                            println("pre set theme value"+ThemeManager.currentThemeName)
                      //      if (ThemeManager.currentTheme.isEmpty()) {
                                when (cardRepositoryContext.resources?.configuration?.uiMode?.and(
                                    Configuration.UI_MODE_NIGHT_MASK
                                )) {
                                    Configuration.UI_MODE_NIGHT_YES -> {
                                        ThemeManager.currentThemeName.ifEmpty {
                                            ThemeManager.currentThemeName = ThemeMode.dark.name
                                        }
                                        if(TapCheckOutSDK.displayColoredDark) ThemeManager.currentThemeName = ThemeMode.dark_colored.name

                                        ThemeManager.loadTapTheme(
                                            cardRepositoryContext,
                                            assetsModel?.theme?.darkUrl.toString(),
                                            ThemeManager.currentThemeName
                                        )
                                    }
                                    Configuration.UI_MODE_NIGHT_NO -> {
                                        ThemeManager.currentThemeName.ifEmpty {
                                            ThemeManager.currentThemeName = ThemeMode.light.name
                                        }

                                        if(TapCheckOutSDK.displayMonoLight) ThemeManager.currentThemeName = ThemeMode.light_mono.name

                                        ThemeManager.loadTapTheme(
                                            cardRepositoryContext,
                                            assetsModel?.theme?.lightUrl.toString(),
                                            ThemeManager.currentThemeName
                                        )

                                    }
                                    Configuration.UI_MODE_NIGHT_UNDEFINED -> {

                                    }
                                    else -> {
                                        ThemeManager.loadTapTheme(
                                            cardRepositoryContext,
                                            assetsModel?.theme?.darkUrl.toString(),
                                            "darktheme"
                                        )

                                    }
                                }
                    //        }

                            NetworkApp.initNetworkToken(
                                initResponseModel?.session,
                                cardRepositoryContext,
                                ApiService.BASE_URL,
                                true,
                                CheckOutActivity()
                            )
                            PaymentDataSource.setPaymentOptionsResponse(paymentOptionsResponse)
                            PaymentDataSource.setMerchantData(merchantDataModel)
                            PaymentDataSource.setInitResponse(initResponseModel)
                            PaymentDataSource.setTokenConfig(initResponseModel?.session)
                            if (tabAnimatedActionButton != null) {
                                tabAnimatedActionButton?.changeButtonState(ActionButtonState.LOADING)
                            }
                        }

                    } else {
                        sdkSession.checkOutDelegate?.sessionFailedToStart()
                    }

                    if (tabAnimatedActionButton != null) {
                        activity?.let { it1 ->
                            CustomUtils.getDeviceDisplayMetrics(
                                it1
                            )
                        }?.let { it2 -> tabAnimatedActionButton?.setDisplayMetricsTheme(it2 , CustomUtils.getCurrentTheme()) }
                        tabAnimatedActionButton?.changeButtonState(ActionButtonState.LOADING)
                    }
                }

            } else {
                sdkSession.checkOutDelegate?.sessionFailedToStart()
                tabAnimatedActionButton?.changeButtonState(ActionButtonState.RESET)
            }

        } else if (requestCode == CHARGE_REQ_CODE) {

            response?.body().let {
                chargeResponse = Gson().fromJson(it, Charge::class.java)

            }
            viewModel.globalChargeResponse = chargeResponse
            handleChargeResponse(chargeResponse)
        } else if (requestCode == CHARGE_RETRIEVE_CODE) {
            println("CHARGE_RETRIEVE_CODE")
            response?.body().let {
                chargeResponse = Gson().fromJson(it, Charge::class.java)
                viewModel.globalChargeResponse = chargeResponse
                if (chargeResponse.status?.name == ChargeStatus.CAPTURED.name) {
                    fireWebPaymentCallBack(chargeResponse)
                    //chargeResponse?.transaction?.url?.let { it1 -> viewModel?.displayRedirect(it1,chargeResponse) }
                    //  viewModel?.redirectLoadingFinished(true,chargeResponse, contextSDK)

                } else
                    handleChargeResponse(chargeResponse)
            }

        } else if (requestCode == BIN_RETRIEVE_CODE) {
            response?.body().let {
                binLookupResponse = Gson().fromJson(it, BINLookupResponse::class.java)
                //Log.e("binLookupResponse",binLookupResponse.toString())
                if (::binLookupResponse.isInitialized && ::cardRepositoryContext.isInitialized &&  ::cardViewModel.isInitialized )
                    viewModel.setBinLookupData(
                        binLookupResponse,
                        cardRepositoryContext,
                        cardViewModel
                    )
                PaymentDataSource.setBinLookupResponse(binLookupResponse)

            }
        } else if (requestCode == CREATE_TOKEN_CODE) {
            response?.body().let {
                tokenResponse = Gson().fromJson(it, Token::class.java)
                if (tokenResponse != null) {
                    when {
                        PaymentDataSource.getTransactionMode() == TransactionMode.AUTHORIZE_CAPTURE -> {
                            createAuthorizeRequest(viewModel, null, tokenResponse.id)

                        }
                        PaymentDataSource.getTransactionMode() == TransactionMode.TOKENIZE_CARD -> {
                            SDKSession.getListener()?.cardTokenizedSuccessfully(tokenResponse, savedCardValue)
                            viewModel.handleSuccessFailureResponseButton(
                                "tokenized",
                                null,
                                null,
                                tabAnimatedActionButton,
                                contextSDK
                            )

                        }
                        PaymentDataSource.getTransactionMode() == TransactionMode.SAVE_CARD -> {
                            createSaveCard(viewModel, null, tokenResponse.id)
                        }
                        else -> {
                            createChargeRequest(viewModel, null, tokenResponse.id, saveCardValue = savedCardValue)
                        }
                    }


                }
            }
        } else if (requestCode == CREATE_GOOGLE_TOKEN) {
            response?.body().let {
                tokenResponse = Gson().fromJson(it, Token::class.java)
                if (tokenResponse != null &&  selectedPaymentOption !=null) {
                    createChargeRequest(viewModel, selectedPaymentOption, tokenResponse.id, true)
                }


            }
        } else if (requestCode == CREATE_AUTHORIZE_CODE) {
            response?.body().let {
                authorizeActionResponse = Gson().fromJson(it, Authorize::class.java)
                println("authorizeActionResponse value is>>>>" + authorizeActionResponse.status.name)

                if (authorizeActionResponse.status?.name == ChargeStatus.INITIATED.name) {
                    // fireWebPaymentCallBack(authorizeActionResponse)
                    if (authorizeActionResponse.transaction.url != null) {
                        authorizeActionResponse.transaction?.url?.let { it1 ->
                            viewModel.displayRedirect(
                                it1,
                                authorizeActionResponse
                            )
                        }
                    } else handleAuthorizeResponse(authorizeActionResponse)
                } else handleAuthorizeResponse(authorizeActionResponse)

            }
        } else if (requestCode == DEL_SAVE_CARD_CODE) {
            response?.body().let {
                deleteCardResponse = Gson().fromJson(it, DeleteCardResponse::class.java)
                println("deleteCardResponse is" + deleteCardResponse)
                viewModel.deleteSelectedCardListener(deleteCardResponse)
            }
        } else if (requestCode == CREATE_SAVE_CARD) {
            println("data in resp body>>" + response?.body())
            response?.body().let {
                saveCardResponse = Gson().fromJson(it, SaveCard::class.java)
                if (saveCardResponse.status?.name == ChargeStatus.INITIATED.name) {
                    saveCardResponse.transaction?.url?.let { it1 ->
                        viewModel.displayRedirect(
                            it1,
                            saveCardResponse
                        )
                    }

                } else {
                    handleSaveCardResponse(saveCardResponse)
                }
            }

        } else if (requestCode == RETRIEVE_SAVE_CARD_CODE) {

            response?.body().let {
                saveCardResponse = Gson().fromJson(it, SaveCard::class.java)
                println("saveCardResponse value is>>>>" + saveCardResponse.status.name)
                sdkSession.getListener()?.cardSaved(saveCardResponse)
                handleSaveCardResponse(saveCardResponse)

            }
        } else if (requestCode == RETRIEVE_AUTHORIZE_CODE) {
            response?.body().let {
                authorizeActionResponse = Gson().fromJson(it, Authorize::class.java)
                println("authorizeActionResponse ret value is>>>>" + authorizeActionResponse.status.name)
                sdkSession.getListener()?.checkoutAuthorizeCaptured(authorizeActionResponse)
                handleAuthorizeResponse(authorizeActionResponse)
            }
        } else if (requestCode == CREATE_SAVE_EXISTING_CODE) {
            response?.body().let {
                tokenResponse = Gson().fromJson(it, Token::class.java)
                println("CREATE_SAVE_EXISTING_CODE tokenResponse >>>>" + tokenResponse)
                if (tokenResponse != null) {
                    if (PaymentDataSource.getTransactionMode() == TransactionMode.AUTHORIZE_CAPTURE) {
                        createAuthorizeRequest(viewModel, null, tokenResponse.id)

                    } else if (PaymentDataSource.getTransactionMode() == TransactionMode.TOKENIZE_CARD) {
                        SDKSession.getListener()?.cardTokenizedSuccessfully(tokenResponse, savedCardValue)
                        SDKSession.tabAnimatedActionButton?.changeButtonState(ActionButtonState.SUCCESS)
                    } else if (PaymentDataSource.getTransactionMode() == TransactionMode.SAVE_CARD) {
                        createSaveCard(viewModel, null, tokenResponse.id)
                    } else {
                        createChargeRequest(viewModel, null, tokenResponse.id, savedCardValue)

                    }
                }
            }
        } else if (requestCode == AUTHENTICATE_CODE) {
            response?.body().let {
                chargeResponse = Gson().fromJson(it, Charge::class.java)
                println("AUTHENTICATE_CODE tokenResponse >>>>" + response?.body())
                if (chargeResponse is Authorize) handleAuthorizeResponse(chargeResponse as Authorize)
                else
                    handleChargeResponse(chargeResponse)
            }
        } else if (requestCode == LIST_ALL_CARD_CODE) {
            response?.body().let {
                listCardsResponse = Gson().fromJson(it, ListCardsResponse::class.java)
                println("LIST_ALL_CARD_CODE >>>>" + listCardsResponse)

            }
        }

        if (::chargeResponse.isInitialized)
            CardViewState.ChargeViewState(charge = chargeResponse)
        if (::binLookupResponse.isInitialized)
            CardViewState.BinLookupViewState(binLookupResponse = binLookupResponse)
        // if(configResponse!=null && paymentOptionsResponse!=null){
        if (paymentOptionsResponse != null) {
            val viewState = CardViewState(
                configResponseModel = configResponse,
                paymentOptionsResponse = paymentOptionsResponse
            )
            tabAnimatedActionButton?.changeButtonState(ActionButtonState.LOADING)
            /* val tapCheckoutFragment = CheckoutFragment()
             tapCheckoutFragment.show(supportFragmentManager.beginTransaction().addToBackStack(null), "CheckOutFragment")*/

            Handler().postDelayed({
                val intent = Intent(SDKSession.activity, CheckOutActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                contextSDK?.startActivity(intent)
                //   println("fragments" + supportFragmentManager.fragments)

                SDKSession.checkOutDelegate?.sessionIsStarting()
                SessionManager.setActiveSession(false)
                resultObservable.onNext(viewState)
            }, 4000)
        }
        if (::chargeResponse.isInitialized && chargeResponse != null) {
            if (::viewModel.isInitialized && chargeResponse.status != ChargeStatus.IN_PROGRESS && chargeResponse.status != ChargeStatus.CANCELLED) {
                chargeResponse?.transaction?.url?.let {
                    viewModel.displayRedirect(
                        it,
                        chargeResponse
                    )
                }
            }
        }


    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun handleChargeResponse(chargeResponse: Charge) {
        /**
         * Charge response is a generic one whether you get response from ssaved card , otp or webpayments this is going to handle the cases**/
        if (chargeResponse == null) return
        Log.e("Charge status", (chargeResponse.status).name)
        Log.e("Charge status", (chargeResponse.id))
        chargeResponseForGoogle= chargeResponse
        viewModel.globalChargeResponse = chargeResponse

        if (chargeResponse is Authorize) {
            handleAuthorizeResponse(chargeResponse)
        } else
            when (chargeResponse.status) {

                /**
                 * Charge response is Initiated is first call if it should be otp or not s**/
                ChargeStatus.INITIATED -> {
                    if (chargeResponse.authenticate != null) {
                        var authenicate: Authenticate = chargeResponse.authenticate
                        if (authenicate != null && authenicate.status == AuthenticationStatus.INITIATED) {
                            when (authenicate.type) {
                                AuthenticationType.BIOMETRICS -> "d"
                                AuthenticationType.OTP -> {
                                    Log.d(
                                        "cardRepose",
                                        " coming charge type is ...  caller setChargeOrAuthorize"
                                    );
                                    PaymentDataSource.setChargeOrAuthorize(chargeResponse)
                                    viewModel.displayOTPView(
                                        chargeResponse?.customer?.getPhone(),
                                        PaymentTypeEnum.SAVEDCARD.toString(),
                                        chargeResponse
                                    )
                                    /**
                                     * Charge response is Initiated and type is OTP we display OTP and after entering again from api
                                     * handleChargeResponse is called  to check the status after entering OTP which can be any of the below cases*/
                                }

                            }
                        }
                    }
                }
                ChargeStatus.CAPTURED -> {
                    if (chargeResponse is Authorize) handleAuthorizeResponse(chargeResponse as Authorize)
                    SDKSession.getListener()?.checkoutChargeCaptured(chargeResponse)
                    SDKSession.sessionActive = false
                    viewModel.handleSuccessFailureResponseButton(
                        "success",
                        chargeResponse.authenticate,
                        chargeResponse,
                        tabAnimatedActionButton,
                        contextSDK
                    )

                }
                ChargeStatus.AUTHORIZED -> {
                    SDKSession.getListener()?.checkoutAuthorizeCaptured(chargeResponse as Authorize)
                }
                ChargeStatus.FAILED -> {
                    //  println("FAILED caleld")
                    viewModel.handleSuccessFailureResponseButton(
                        "failure",
                        chargeResponse.authenticate,
                        chargeResponse, tabAnimatedActionButton, contextSDK
                    )
                    SDKSession.getListener()?.checkoutChargeFailed(chargeResponse)

                    SDKSession.sessionActive = false
                    //  viewModel.redirectLoadingFinished(false, chargeResponse, contextSDK)
                }
                ChargeStatus.ABANDONED, ChargeStatus.CANCELLED, ChargeStatus.DECLINED -> {

                    //  viewModel?.handleSuccessFailureResponseButton("failure",chargeResponse.authenticate,chargeResponse.status)
                    viewModel.handleSuccessFailureResponseButton(
                        "failure",
                        chargeResponse.authenticate,
                        chargeResponse,
                        tabAnimatedActionButton, contextSDK
                    )
                    SDKSession.getListener()?.checkoutChargeFailed(
                        chargeResponse
                    )
                    //  viewModel.redirectLoadingFinished(true,chargeResponse, contextSDK)

                }

                ChargeStatus.RESTRICTED -> SDKSession.getListener()
                    ?.checkoutChargeFailed(chargeResponse)
                ChargeStatus.UNKNOWN -> SDKSession.getListener()?.checkoutChargeFailed(chargeResponse)
                ChargeStatus.TIMEDOUT -> SDKSession.getListener()?.checkoutChargeFailed(chargeResponse)
                ChargeStatus.IN_PROGRESS -> {
                    if (chargeResponse.transaction != null && chargeResponse.transaction.asynchronous) {
                        viewModel?.displayAsynchronousPaymentView(chargeResponse)
                    }
                }
            }
    }

    @RequiresApi(Build.VERSION_CODES.N)
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
                            viewModel.displayOTPView(
                                authorize.customer.getPhone(),
                                PaymentTypeEnum.SAVEDCARD.toString(),
                                authorize as Authorize
                            )
                        }
                    }
                }
            }
            ChargeStatus.CAPTURED, ChargeStatus.AUTHORIZED -> try {
                SDKSession.getListener()?.checkoutAuthorizeCaptured(authorize)
                viewModel.handleSuccessFailureResponseButton(
                    "success",
                    authorize.authenticate,
                    authorize,
                    tabAnimatedActionButton,
                    contextSDK
                )

            } catch (e: java.lang.Exception) {
                Log.d(
                    "cardRepository",
                    " Error while calling delegate method checkoutAuthorizeCaptured(authorize)" + e
                )
                // closePaymentActivity()
            }
            ChargeStatus.FAILED, ChargeStatus.ABANDONED, ChargeStatus.CANCELLED, ChargeStatus.DECLINED, ChargeStatus.RESTRICTED ->
                try {
                    //    closePaymentActivity()
                    SDKSession.getListener()?.checkoutAuthorizeFailed(authorize)
                    viewModel.handleSuccessFailureResponseButton(
                        "failure",
                        authorize.authenticate,
                        authorize,
                        tabAnimatedActionButton,
                        contextSDK
                    )
                } catch (e: java.lang.Exception) {
                    Log.d(
                        "cardRepository",
                        "Error while calling delegate method checkoutAuthorizeFailed(authorize)"
                    )

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
                viewModel.handleSuccessFailureResponseButton(
                    "failure",
                    saveCard.authenticate,
                    saveCard,
                    tabAnimatedActionButton,
                    contextSDK

                )
            } catch (e: java.lang.Exception) {
                Log.d(
                    "CardRepository",
                    " Error while calling delegate method cardSaved(saveCard)" + e
                )

            }
            ChargeStatus.INVALID, ChargeStatus.FAILED, ChargeStatus.ABANDONED, ChargeStatus.CANCELLED, ChargeStatus.DECLINED, ChargeStatus.RESTRICTED -> try {
                SDKSession.getListener()?.cardSavingFailed(saveCard)
                viewModel?.handleSuccessFailureResponseButton(
                    "failure",
                    saveCard.authenticate,
                    saveCard,
                    tabAnimatedActionButton,
                    contextSDK

                )
            } catch (e: java.lang.Exception) {
                Log.d(
                    "CardRepository",
                    " Error while calling delegate method cardSavingFailed(saveCard)"
                )

            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onFailure(requestCode: Int, errorDetails: GoSellError?) {
        if (requestCode == CONFIG_CODE || requestCode == CHARGE_REQ_CODE || requestCode == INIT_CODE || requestCode == CHARGE_RETRIEVE_CODE) {
            sdkSession.getListener()?.checkoutSdkError(errorDetails)
            SDKSession.sessionActive = false
          //  cardRepositoryContext?.showToast("Handle Fail")

            viewModel.handleSuccessFailureResponseButton(
                "failure",
                null,
                null, tabAnimatedActionButton, contextSDK

            )
        }
        SDKSession.sessionActive = false
        SessionManager.setActiveSession(false)
        println("response body CHARGE_REQ_CODE>>" + errorDetails?.errorBody)


        errorDetails?.let {
            if (it.throwable != null) {
                resultObservable.onError(it.throwable)
                sdkSession.getListener()?.backendUnknownError(errorDetails)
                viewModel.handleSuccessFailureResponseButton(
                    "failure",
                    null,
                    null,
                    tabAnimatedActionButton,
                    contextSDK

                )
            } else
                try {
                    // resultObservable.onError(Throwable(it.errorMessage))
                    RxJavaPlugins.setErrorHandler(Throwable::printStackTrace)
                    // sdkSession.getListener()?.backendUnknownError(errorDetails)
                    viewModel.handleSuccessFailureResponseButton(
                        "failure",
                        null,
                        null,
                        tabAnimatedActionButton,
                        contextSDK

                    )

                } catch (e: Exception) {

                }


        }
    }

    companion object {
        private const val CONFIG_CODE = 1
        private const val INIT_CODE = 2
        private const val PAYMENT_OPTIONS_CODE = 3
        private const val CHARGE_REQ_CODE = 4
        private const val CHARGE_RETRIEVE_CODE = 5
        private const val BIN_RETRIEVE_CODE = 6
        private const val CREATE_TOKEN_CODE = 7
        private const val CREATE_AUTHORIZE_CODE = 8
        private const val RETRIEVE_AUTHORIZE_CODE = 9
        private const val CREATE_SAVE_CARD = 10
        private const val RETRIEVE_SAVE_CARD_CODE = 11
        private const val DEL_SAVE_CARD_CODE = 12
        private const val CREATE_SAVE_EXISTING_CODE = 13
        private const val AUTHENTICATE_CODE = 14
        private const val LIST_ALL_CARD_CODE = 15
        private const val CREATE_GOOGLE_TOKEN = 16
        private const val CURRENCY_CODE = 17
        private const val USER_IP_CODE = 18


    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun callChargeOrAuthorizeOrSaveCardAPI(
        source: SourceRequest,
        paymentOption: PaymentOption?,
        cardBIN: String?, saveCard: Boolean?
    ) {
        Log.e("OkHttp", "CALL CHARGE API OR AUTHORIZE API")
        val provider: IPaymentDataProvider = getDataProvider()
        val supportedCurrencies: java.util.ArrayList<SupportedCurrencies>? =
            provider.getSupportedCurrencies()
        val orderID: String? = provider.getPaymentOptionsOrderID()
        val postURL: String? = provider.getPostURL()
        val post = if (postURL == null) null else TrackingURL(URLStatus.PENDING, postURL)
        val amountedCurrency: AmountedCurrency? = provider.getSelectedCurrency()
        val amountedCurrency1: BigDecimal? = PaymentDataSource?.getSelectedAmount()
        val amountedCurrency2: String? = PaymentDataSource?.getSelectedCurrency()

        //     Log.d("callChargeOrAuthorizeOr"," step 5 : callChargeOrAuthorizeOrSaveCardAPI : in class "+ "["+this.getClass().getName()+"] with amountedCurrency=["+amountedCurrency.getAmount()+"]  ");
        val transactionCurrency: AmountedCurrency? = provider.getTransactionCurrency()
        //   println("transactionCurrency in cad" + transactionCurrency)
        val customer: TapCustomer = provider.getCustomer()
        var fee = BigDecimal.ZERO
        // println( "  --->>> extraFeeeeeesee--->>  "+ paymentOption?.extraFees)

        if (paymentOption != null) fee = AmountCalculator.calculateExtraFeesAmount(
            paymentOption.extraFees,
            supportedCurrencies,
            amountedCurrency
        )
        // Log.d("PaymentProcessManager", "fee : $fee")
        val order = Order(orderID)
        val redirect = TrackingURL(URLStatus.PENDING, ApiService.RETURN_URL)
        val paymentDescription: String? = provider.getPaymentDescription()
        val paymentMetadata: HashMap<String, String>? = provider.getPaymentMetadata()
        val reference: Reference? = provider.getPaymentReference()
        val shouldSaveCard = saveCard ?: true
        val statementDescriptor: String? = provider.getPaymentStatementDescriptor()
        var require3DSecure: Boolean = provider
            .getRequires3DSecure() // this.dataSource.getRequires3DSecure() || this.chargeRequires3DSecure();
        val receipt: Receipt? = provider.getReceiptSettings()
        val destinations: Destinations? = provider.getDestination()
        val merchant: Merchant? = provider.getMerchant()
        val cardIssuer: CardIssuer? = provider.getCardIssuer()
        val topUp: TopUp? = provider.getTopUp()
        val transactionMode: TransactionMode = provider.getTransactionMode()
        // Log.d("PaymentProcessManager", "transactionMode : $transactionMode")
       //  println("paymentOption?.threeDS" + paymentOption?.threeDS)
         println("paymentOption?.threeDS" + paymentOption?.threeDS)
        //        Log.d("PaymentProcessManager", "topUp : " + topUp.toString());
        /**
         * Condition added for 3Ds for merchant
         */
        if (paymentOption?.threeDS != null) {
            if (paymentOption.threeDS == "N") {
                require3DSecure = false
            } else if (paymentOption.threeDS == "Y") {
                require3DSecure = true
            } else if (paymentOption.threeDS == "U") {
                require3DSecure = provider?.getRequires3DSecure()
            }
        }



        var selectedTotalAmount: String
        if (viewModel.selectedTotalAmount?.isNotBlank() == true && viewModel.selectedTotalAmount?.isNotEmpty() == true) {
            if (viewModel.selectedTotalAmount!!.contains(","))
                selectedTotalAmount = viewModel.selectedTotalAmount!!.replace(",", "")
            else selectedTotalAmount = viewModel.selectedTotalAmount!!
        } else {
            if (viewModel.selectedTotalAmount?.contains(",") == true)
                selectedTotalAmount = amountedCurrency?.amount.toString().replace(",", "")
            else selectedTotalAmount = amountedCurrency?.amount.toString()
        }

        // println("selectedTotalAmount in repo"+selectedTotalAmount)
        when (transactionMode) {
            TransactionMode.PURCHASE -> {
                val chargeRequest = transactionCurrency?.amount?.let {
                    amountedCurrency?.currency?.let { it1 ->
                        CreateChargeRequest(
                            merchant,
                            it,
                            transactionCurrency?.currency,
                            BigDecimal(selectedTotalAmount),
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
                jsonString = Gson().toJson(chargeRequest)
                NetworkController.getInstance().processRequest(
                    TapMethodType.POST, ApiService.CHARGES, jsonString, this, CHARGE_REQ_CODE
                )
            }
            TransactionMode.AUTHORIZE_CAPTURE -> {
                val authorizeAction: AuthorizeAction? = provider.getAuthorizeAction()
                val authorizeRequest = authorizeAction?.let {
                    transactionCurrency?.amount?.let { it1 ->
                        CreateAuthorizeRequest(
                            merchant,
                            it1,
                            transactionCurrency?.currency,
                            BigDecimal(selectedTotalAmount),
                            amountedCurrency?.currency.toString(),
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
                val jsonString = Gson().toJson(authorizeRequest)
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

    @RequiresApi(Build.VERSION_CODES.N)
    private fun fireWebPaymentCallBack(charge: Charge?) {
        when (charge?.status) {
            ChargeStatus.CAPTURED, ChargeStatus.AUTHORIZED -> try {
                // closePaymentActivity()
                println("fireWebPaymentCallBack>>" + charge?.status)
                viewModel.handleSuccessFailureResponseButton(
                    "success",
                    chargeResponse.authenticate,
                    chargeResponse,
                    tabAnimatedActionButton,
                    contextSDK
                )

                sdkSession.getListener()?.checkoutChargeCaptured(charge)


                //  SDKSession().getListener()?.checkoutChargeCaptured(charge)
            } catch (e: Exception) {
                Log.e(TAG, "fireWebPaymentCallBack: ", e)
                Log.d(
                    "cardrepo",
                    " Error while calling fireWebPaymentCallBack >>> method checkoutChargeCaptured(charge)"
                )
                //closePaymentActivity()
            }
            ChargeStatus.FAILED, ChargeStatus.ABANDONED, ChargeStatus.CANCELLED, ChargeStatus.DECLINED, ChargeStatus.RESTRICTED, ChargeStatus.UNKNOWN, ChargeStatus.TIMEDOUT -> try {
                //closePaymentActivity()
                viewModel.handleSuccessFailureResponseButton(
                    "failure",
                    chargeResponse.authenticate,
                    chargeResponse,
                    tabAnimatedActionButton,
                    contextSDK
                )

                SDKSession.getListener()?.checkoutChargeFailed(charge)
            } catch (e: Exception) {
                Log.d(
                    "cardrepo",
                    " Error while calling fireWebPaymentCallBack >>> method checkoutChargeFailed(charge)"
                )
                // closePaymentActivity()
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.N)
    fun getUserIpAddress(_context: Context, _viewModel: CheckoutViewModel){
        this.viewModel =_viewModel
        this.cardRepositoryContext =_context
        NetworkController.getInstance().processRequest(
            TapMethodType.GET,  ApiService.BASE_URL_IP+ApiService.GET_IP_VAL,null,this, USER_IP_CODE)

    }


}

