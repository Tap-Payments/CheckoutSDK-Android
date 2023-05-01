package company.tap.checkout.internal.apiresponse

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.google.gson.JsonElement
import company.tap.checkout.internal.api.network.RetrofitClient
import company.tap.checkout.internal.api.responses.UserIpAddressResponse
import company.tap.checkout.internal.api.responses.UserLocalCurrencyModel
import company.tap.checkout.internal.cache.SharedPrefManager
import company.tap.checkout.internal.cache.UserLocalCurrencyModelKey
import company.tap.checkout.internal.viewmodels.CheckoutViewModel
import company.tap.tapnetworkkit.connection.NetworkApp
import company.tap.tapnetworkkit.controller.NetworkController
import company.tap.tapnetworkkit.enums.TapMethodType
import company.tap.tapnetworkkit.exception.GoSellError
import company.tap.tapnetworkkit.interfaces.APIRequestCallback
import company.tap.tapuilibrary.uikit.atoms.TapTextView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


/**
 * User Repository to get IP address of user  + getting local Currency form Tap BaseUrl using TapNetwork Library
 */

class UserRepository constructor(var context: Context,var checkoutViewModel: CheckoutViewModel) {
    @RequiresApi(Build.VERSION_CODES.N)
    fun getUserIpAddress() {
        val call: Call<UserIpAddressResponse>? =
            RetrofitClient.instance?.getMyApi()?.getUserIpAddress()
        call?.enqueue(object : Callback<UserIpAddressResponse> {
            override fun onResponse(
                call: Call<UserIpAddressResponse>,
                response: Response<UserIpAddressResponse>
            ) {
                Log.e("error",response.body().toString())
                NetworkApp.setUserIpAddress(response.body()?.IPv4 ?: "")
                getCurrencyApi()

            }

            override fun onFailure(call: Call<UserIpAddressResponse>, t: Throwable) {
                /**
                 * needed to handle error
                 */
                Log.e("error", t.message.toString())
            }

        })
    }


    @RequiresApi(Build.VERSION_CODES.N)
    fun getCurrencyApi() {
        NetworkController.getInstance().processRequest(
            TapMethodType.GET, ApiService.CURRENCY_API, null, object : APIRequestCallback {
                override fun onSuccess(
                    responseCode: Int,
                    requestCode: Int,
                    response: Response<JsonElement>?
                ) {
                    response?.body().let {
                        val userLocalCurrencyModel = Gson().fromJson(it, UserLocalCurrencyModel::class.java)
                       val isSaved =  SharedPrefManager.saveModelLocally(
                            context = context,
                            dataToBeSaved = userLocalCurrencyModel,
                            keyValueToBeSaved = UserLocalCurrencyModelKey
                        )
                        if (isSaved){
                            checkoutViewModel.localCurrencyReturned.value = true
                        }
                    }
                }

                override fun onFailure(requestCode: Int, errorDetails: GoSellError?) {
                    /**
                     * needed to handle error
                     */
                    Log.e("error currency", errorDetails.toString())
                }
            },
            1
        )
    }

}





