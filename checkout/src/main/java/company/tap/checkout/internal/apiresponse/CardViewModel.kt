package company.tap.checkout.internal.apiresponse

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import company.tap.checkout.internal.viewmodels.TapLayoutViewModel
import company.tap.checkout.open.controller.SDKSession
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
    private fun getInitData(viewModel: TapLayoutViewModel) {
        repository.getInitData(context,viewModel)
        GlobalScope.launch(Dispatchers.Main) { // launch coroutine in the main thread
            val apiResponseTime = Random.nextInt(1000, 20000)
            delay(apiResponseTime.toLong())
            repository.getPaymentOptions(context,viewModel)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun processEvent(event: CardViewEvent, viewModel: TapLayoutViewModel) {
        when (event) {
            CardViewEvent.InitEvent -> getInitData(viewModel)
            CardViewEvent.ChargeEvent -> createChargeRequest(viewModel)
            CardViewEvent.RetreiveChargeEvent -> retrieveChargeRequest(viewModel)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun createChargeRequest(viewModel: TapLayoutViewModel) {
        repository.createChargeRequest(context,viewModel)

    }
    @RequiresApi(Build.VERSION_CODES.N)
    private fun retrieveChargeRequest(viewModel: TapLayoutViewModel) {
        repository.retrieveChargeRequest(context,viewModel)

    }

    fun getContext(context: Context){
        this.context = context
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}