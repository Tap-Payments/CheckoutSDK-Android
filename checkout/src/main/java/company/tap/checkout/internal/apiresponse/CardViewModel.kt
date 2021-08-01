package company.tap.checkout.internal.apiresponse

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import company.tap.checkout.internal.api.models.CreateTokenCard
import company.tap.checkout.internal.api.models.PaymentOption
import company.tap.checkout.internal.viewmodels.TapLayoutViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.*
import kotlin.random.Random

/**
 * Created by AhlaamK on 11/15/20.

Copyright (c) 2020    Tap Payments.
All rights reserved.
 **/
class CardViewModel : ViewModel() {

    private val repository = CardRepository()
    private val compositeDisposable = CompositeDisposable()
    @SuppressLint("StaticFieldLeak")
    private lateinit  var context : Context
    val liveData = MutableLiveData<Resource<CardViewState>>()


    init {
        compositeDisposable.add(repository.resultObservable
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { liveData.value = Resource.Loading() }
            .doOnTerminate { liveData.value = Resource.Finished() }
            .subscribe(
                { data -> liveData.value = Resource.Success(data)},
                { error -> liveData.value = error.message?.let { Resource.Error(it) } }
            ))

    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun getInitData(viewModel: TapLayoutViewModel,cardViewModel: CardViewModel?) {
        repository.getInitData(context,viewModel,cardViewModel)
        GlobalScope.launch(Dispatchers.Main) { // launch coroutine in the main thread
            val apiResponseTime = Random.nextInt(1000, 20000)
            delay(apiResponseTime.toLong())
            repository.getPaymentOptions(context,viewModel)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun processEvent(event: CardViewEvent, viewModel: TapLayoutViewModel, selectedPaymentOption: PaymentOption?, binValue: String? = null, cardDataRequest: CreateTokenCard?, cardViewModel: CardViewModel? = null, customerId: String?=null, cardId: String?=null) {
        when (event) {
            CardViewEvent.InitEvent -> getInitData(viewModel,cardViewModel)
            CardViewEvent.ChargeEvent -> createChargeRequest(viewModel,selectedPaymentOption,null)
            CardViewEvent.RetreiveChargeEvent -> retrieveChargeRequest(viewModel)
            CardViewEvent.RetreiveBinLookupEvent -> retrieveBinlookup(viewModel,binValue)
            CardViewEvent.CreateTokenEvent -> createTokenWithEncryptedCard(viewModel,cardDataRequest)
            CardViewEvent.CreateAuthorizeEvent -> createAuthorizeCard(viewModel,selectedPaymentOption)
            CardViewEvent.RetreiveAuthorizeEvent -> retrieveAuthorize(viewModel)
            CardViewEvent.RetreiveSaveCardEvent -> retrieveSaveCard(viewModel)
            CardViewEvent.DeleteSaveCardEvent -> callDeleteCardAPI(viewModel,cardId,customerId )
        }
    }


    @RequiresApi(Build.VERSION_CODES.N)
    private fun createChargeRequest(viewModel: TapLayoutViewModel, selectedPaymentOption: PaymentOption?,cardtoken:String?) {
        repository.createChargeRequest(context, viewModel, selectedPaymentOption,cardtoken)

    }
    @RequiresApi(Build.VERSION_CODES.N)
    private fun retrieveChargeRequest(viewModel: TapLayoutViewModel) {
        repository.retrieveChargeRequest(context,viewModel)

    }
    @RequiresApi(Build.VERSION_CODES.N)
    private fun retrieveBinlookup(viewModel: TapLayoutViewModel,binValue: String?) {
        repository.retrieveBinLookup(context,viewModel,binValue)

    }
    @RequiresApi(Build.VERSION_CODES.N)
    private fun retrieveAuthorize(viewModel: TapLayoutViewModel) {
        repository.retrieveAuthorizeRequest(context,viewModel)

    }
    @RequiresApi(Build.VERSION_CODES.N)
    private fun retrieveSaveCard(viewModel: TapLayoutViewModel) {
        repository.retrieveSaveCard(context,viewModel)

    }
    @RequiresApi(Build.VERSION_CODES.N)
    private fun callDeleteCardAPI(viewModel: TapLayoutViewModel, deleteCardId: String?, customerId: String?) {
        println("<<customerId>>"+customerId+"<<<deleteCardId>>>"+deleteCardId)
        repository.callDeleteCardAPI(context,viewModel,deleteCardId,customerId)

    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun createTokenWithEncryptedCard(viewModel: TapLayoutViewModel, createTokenWithEncryptedDataRequest: CreateTokenCard?) {
        println("createTokenWithEncryptedDataRequest>>."+createTokenWithEncryptedDataRequest)
        if (createTokenWithEncryptedDataRequest != null) {
            repository.createTokenWithEncryptedCard(context,viewModel,createTokenWithEncryptedDataRequest)
        }

    }
    @RequiresApi(Build.VERSION_CODES.N)
    private fun createAuthorizeCard(viewModel: TapLayoutViewModel,selectedPaymentOption: PaymentOption?) {
        println("createAuthorizeCard>>."+selectedPaymentOption)
        if (selectedPaymentOption != null) {
            repository.createAuthorizeRequest(context, viewModel, selectedPaymentOption, repository.tokenResponse.id)
        }

    }
    @RequiresApi(Build.VERSION_CODES.N)
    private fun createSaveCard(viewModel: TapLayoutViewModel,selectedPaymentOption: PaymentOption?) {
        println("createSaveCard>>."+selectedPaymentOption)
        if (selectedPaymentOption != null) {
            repository.createSaveCard(context, viewModel, null, repository.tokenResponse.id)
        }

    }

    fun getContext(context: Context){
        this.context = context
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}