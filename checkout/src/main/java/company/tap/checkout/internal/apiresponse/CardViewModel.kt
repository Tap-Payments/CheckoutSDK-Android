package company.tap.checkout.internal.apiresponse

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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
        repository.getInitData()
    }

   /* fun processEvent(event: CardViewEvent) {
        when (event) {
            CardViewEvent.InitEvent -> getInitData()
        }
    }*/

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}