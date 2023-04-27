package company.tap.checkout.internal.apiresponse

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import company.tap.checkout.internal.api.models.*
import company.tap.checkout.internal.api.requests.CreateTokenGPayRequest
import company.tap.checkout.internal.viewmodels.CheckoutViewModel
import company.tap.checkout.open.data_managers.PaymentDataSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable


/**
 * Created by AhlaamK on 11/15/20.

Copyright (c) 2020    Tap Payments.
All rights reserved.
 **/
class CardViewModel : ViewModel() {

    private val repository = CardRepository()
    private val compositeDisposable = CompositeDisposable()

    @SuppressLint("StaticFieldLeak")
    private lateinit var context: Context
    private val liveData = MutableLiveData<Resource<CardViewState>>()


    val _isTokenForCardRecieved = MutableLiveData<Boolean>()
    val isTokenForCardRecieved: LiveData<Boolean> = _isTokenForCardRecieved


    init {
        compositeDisposable.add(repository.resultObservable
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { liveData.postValue(Resource.Loading()) }
            .doOnTerminate { liveData.postValue(Resource.Finished()) }
            .subscribe(
                { data -> liveData.postValue(Resource.Success(data)) },
                { error -> liveData.postValue(error.message?.let { Resource.Error(it) }) }
            ))
        _isTokenForCardRecieved.value = false

    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun getINITData(
        _context: Context?, viewModel: CheckoutViewModel, cardViewModel: CardViewModel?,
        supportFragmentManagerdata: FragmentManager?
    ) {
        if (_context != null) {
            if (supportFragmentManagerdata != null) {
                // if(PaymentDataSource.getTokenConfig()!=null){
                repository.getINITData(
                    _context,
                    viewModel,
                    cardViewModel,
                    supportFragmentManagerdata
                )

                // }
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.N)
    fun processEvent(
        event: CardViewEvent,
        viewModel: CheckoutViewModel,
        selectedPaymentOption: PaymentOption?,
        binValue: String? = null,
        cardDataRequest: CreateTokenCard?,
        cardViewModel: CardViewModel? = null,
        customerId: String? = null,
        cardId: String? = null,
        createTokenWithExistingCardRequest: CreateTokenSavedCard? = null,
        otpString: String? = null,
        supportFragmentManager: FragmentManager? = null,
        context: Context? = null,
        createTokenGPayRequest: CreateTokenGPayRequest? = null
    ) {
        when (event) {
            CardViewEvent.InitEvent -> getINITData(
                context,
                viewModel,
                cardViewModel,
                supportFragmentManager
            )
            CardViewEvent.ChargeEvent -> createChargeRequest(viewModel, selectedPaymentOption, null)
            CardViewEvent.RetreiveChargeEvent -> retrieveChargeRequest(viewModel)
            CardViewEvent.RetreiveBinLookupEvent -> retrieveBinlookup(viewModel, binValue)
            CardViewEvent.CreateTokenEvent -> createTokenWithEncryptedCard(
                viewModel,
                cardDataRequest
            )
            CardViewEvent.CreateAuthorizeEvent -> createAuthorizeCard(
                viewModel,
                selectedPaymentOption
            )
            CardViewEvent.RetreiveAuthorizeEvent -> retrieveAuthorize(viewModel)
            CardViewEvent.RetreiveSaveCardEvent -> retrieveSaveCard(viewModel)
            CardViewEvent.DeleteSaveCardEvent -> callDeleteCardAPI(viewModel, cardId, customerId)
            CardViewEvent.CreateTokenExistingCardEvent -> createTokenWithExistingCard(
                viewModel,
                createTokenWithExistingCardRequest
            )
            CardViewEvent.AuthenticateChargeTransaction -> authenticateChargeTransaction(
                viewModel,
                otpString
            )
            CardViewEvent.AuthenticateAuthorizeTransaction -> authenticateAuthorizeTransaction(
                viewModel,
                otpString
            )
            CardViewEvent.ListAllCards -> listAllCards(viewModel, customerId)
            CardViewEvent.CreateGoogleTokenEvent -> context?.let {
                createGoogleTokenRequest(
                    viewModel,
                    it, createTokenGPayRequest
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun listAllCards(viewModel: CheckoutViewModel, customerId: String?) {
        repository?.listAllCards(viewModel, customerId)
    }


    @RequiresApi(Build.VERSION_CODES.N)
    private fun createChargeRequest(
        viewModel: CheckoutViewModel,
        selectedPaymentOption: PaymentOption?,
        cardtoken: String?
    ) {
        repository.createChargeRequest(viewModel, selectedPaymentOption, cardtoken)

    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun retrieveChargeRequest(viewModel: CheckoutViewModel) {
        repository.retrieveChargeRequest(context, viewModel)

    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun retrieveBinlookup(viewModel: CheckoutViewModel, binValue: String?) {
        repository.retrieveBinLookup(viewModel, binValue)

    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun retrieveAuthorize(viewModel: CheckoutViewModel) {
        repository.retrieveAuthorizeRequest(context, viewModel)

    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun retrieveSaveCard(viewModel: CheckoutViewModel) {
        repository.retrieveSaveCard(context, viewModel)

    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun callDeleteCardAPI(
        viewModel: CheckoutViewModel,
        deleteCardId: String?,
        customerId: String?
    ) {
        // println("<<customerId>>"+customerId+"<<<deleteCardId>>>"+deleteCardId)
        repository.callDeleteCardAPI(context, viewModel, deleteCardId, customerId)

    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun createTokenWithEncryptedCard(
        viewModel: CheckoutViewModel,
        createTokenWithEncryptedDataRequest: CreateTokenCard?
    ) {
        println("createTokenWithEncryptedDataRequest>>." + createTokenWithEncryptedDataRequest)
        if (createTokenWithEncryptedDataRequest != null) {
            repository.createTokenWithEncryptedCard(
                context,
                viewModel,
                createTokenWithEncryptedDataRequest
            )
        }

    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun createTokenWithExistingCard(
        viewModel: CheckoutViewModel,
        createTokenSavedCard: CreateTokenSavedCard?
    ) {
        println("createTokenSavedCard>>." + createTokenSavedCard)
        if (createTokenSavedCard != null) {
            repository.createTokenWithExistingCard(context, viewModel, createTokenSavedCard)
        }

    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun createGoogleTokenRequest(
        viewModel: CheckoutViewModel,
        context: Context,
        createTokenGPayRequest: CreateTokenGPayRequest?
    ) {
        println("createTokenSavedCard>>." + createTokenGPayRequest)
        if (createTokenGPayRequest != null) {
            repository.createGoogleToken(context, viewModel, createTokenGPayRequest)
        }

    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun authenticateChargeTransaction(viewModel: CheckoutViewModel, otpString: String?) {
        // println("otpString>>."+otpString)
        if (otpString != null) {
            repository.authenticate(context, viewModel, otpString)
        }

    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun authenticateAuthorizeTransaction(viewModel: CheckoutViewModel, otpString: String?) {
        println("authenticateAuthorizeTransaction>>." + otpString)
        if (otpString != null) {
            repository.authenticateAuthorizeTransaction(context, viewModel, otpString)
        }

    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun createAuthorizeCard(
        viewModel: CheckoutViewModel,
        selectedPaymentOption: PaymentOption?
    ) {
        println("createAuthorizeCard>>." + selectedPaymentOption)
        if (selectedPaymentOption != null) {
            repository.createAuthorizeRequest(
                viewModel,
                selectedPaymentOption,
                repository.tokenResponse.id
            )
        }

    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun createSaveCard(
        viewModel: CheckoutViewModel,
        selectedPaymentOption: PaymentOption?
    ) {
        println("createSaveCard>>." + selectedPaymentOption)
        if (selectedPaymentOption != null) {
            repository.createSaveCard(viewModel, null, repository.tokenResponse.id)
        }

    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun requestAuthenticateForChargeTransaction(
        viewModel: CheckoutViewModel,
        chargeResponse: Charge
    ) {
        repository.requestAuthenticateForChargeTransaction(viewModel, chargeResponse)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun requestAuthenticateForAuthorizeTransaction(
        viewModel: CheckoutViewModel,
        authorize: Authorize?
    ) {
        repository.requestAuthenticateForAuthorizeTransaction(viewModel, authorize)
    }

    fun getContext(context: Context) {
        this.context = context
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }




}