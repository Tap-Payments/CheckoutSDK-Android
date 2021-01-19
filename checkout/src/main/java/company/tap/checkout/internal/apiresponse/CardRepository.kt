package company.tap.checkout.internal.apiresponse

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonElement
import company.tap.checkout.internal.dummygener.JsonResponseDummy1
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapnetworkkit.controller.NetworkController
import company.tap.tapnetworkkit.enums.TapMethodType
import company.tap.tapnetworkkit.exception.GoSellError
import company.tap.tapnetworkkit.interfaces.APIRequestCallback
import io.reactivex.subjects.BehaviorSubject
import retrofit2.Response

/**
 * Created by AhlaamK on 11/15/20.

Copyright (c) 2020    Tap Payments.
All rights reserved.
 **/
class CardRepository : APIRequestCallback {
    val resultObservable = BehaviorSubject.create<CardViewState>()

    fun getInitData(context: Context) {
        if( LocalizationManager.getLocale(context).language  == "en") NetworkController.getInstance().processRequest(TapMethodType.GET, ApiService.INIT, null, this, INIT_CODE)
        else NetworkController.getInstance().processRequest(TapMethodType.GET, ApiService.INIT_AR, null, this, INIT_CODE)
    }
    override fun onSuccess(responseCode: Int, requestCode: Int, response: Response<JsonElement>?) {
        if (requestCode == INIT_CODE) {
            response?.body().let {
                println("response body is"+response?.body())
                val initResponse = Gson().fromJson(it, JsonResponseDummy1::class.java)
                val viewState = CardViewState(initResponse = initResponse)
                resultObservable.onNext(viewState)
                resultObservable.onComplete()
            }
        }
    }

    override fun onFailure(requestCode: Int, errorDetails: GoSellError?) {
        errorDetails?.let {
            if (it.throwable != null)
                resultObservable.onError(it.throwable)
            else
                resultObservable.onError(Throwable(it.errorMessage))
        }
    }

    companion object {
        private const val INIT_CODE = 1
    }
}