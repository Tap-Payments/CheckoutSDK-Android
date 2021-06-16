package company.tap.checkout.internal.apiresponse

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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
    private lateinit  var context : Context
    val liveData = MutableLiveData<Resource<CardViewState>>()

    init {
        compositeDisposable.add(repository.resultObservable
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { liveData.value = Resource.Loading() }
            .doOnTerminate { liveData.value = Resource.Finished() }
            .subscribe(
                { data -> liveData.value = Resource.Success(data) },
                { error -> liveData.value = error.message?.let { Resource.Error(it) } }
            ))
    }

    private fun getInitData() {
        repository.getInitData(context)
        GlobalScope.launch(Dispatchers.Main) { // launch coroutine in the main thread
            val apiResponseTime = Random.nextInt(1000, 10000)
            delay(apiResponseTime.toLong())
            repository.getPaymentOptions(context)
        }
    }

    fun processEvent(event: CardViewEvent) {
        when (event) {
            CardViewEvent.InitEvent -> getInitData()
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