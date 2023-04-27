package company.tap.checkout.internal.apiresponse

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName
import company.tap.checkout.internal.cache.SharedPrefManager
import company.tap.checkout.internal.cache.UserLocalCurrencyModelKey
import company.tap.tapnetworkkit.connection.NetworkApp
import company.tap.tapnetworkkit.controller.NetworkController
import company.tap.tapnetworkkit.enums.TapMethodType
import company.tap.tapnetworkkit.exception.GoSellError
import company.tap.tapnetworkkit.interfaces.APIRequestCallback
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET


class UserRepository constructor(var context: Context) {
    @RequiresApi(Build.VERSION_CODES.N)
    fun getUserIpAddress() {
        val call: Call<UserIpAddressResponse>? =
            RetrofitClient.instance?.getMyApi()?.getUserIpAddress()
        call?.enqueue(object : Callback<UserIpAddressResponse> {
            override fun onResponse(
                call: Call<UserIpAddressResponse>,
                response: Response<UserIpAddressResponse>
            ) {
                NetworkApp.setUserIpAddress(response.body()?.IPv4 ?: "")
                getCurrencyApi()

            }

            override fun onFailure(call: Call<UserIpAddressResponse>, t: Throwable) {
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
                        SharedPrefManager.saveModelLocally(
                            context = context,
                            dataToBeSaved = userLocalCurrencyModel,
                            keyValueToBeSaved = UserLocalCurrencyModelKey
                        )

                    }
                }

                override fun onFailure(requestCode: Int, errorDetails: GoSellError?) {
                    Log.e("error currency", errorDetails.toString())

                }

            },
            1
        )
    }

}

class RetrofitClient private constructor() {
    private val myApi: UserApi

    init {
        val retrofit: Retrofit = Retrofit.Builder().baseUrl(UserApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        myApi = retrofit.create(UserApi::class.java)
    }

    fun getMyApi(): UserApi {
        return myApi
    }

    companion object {
        @get:Synchronized
        var instance: RetrofitClient? = null
            get() {
                if (field == null) {
                    field = RetrofitClient()
                }
                return field
            }
            private set
    }
}

interface UserApi {
    @GET("/json/")
    fun getUserIpAddress(): Call<UserIpAddressResponse>

    companion object {
        const val BASE_URL = "https://geolocation-db.com"
    }
}

data class UserIpAddressResponse(

    @SerializedName("country_code") var countryCode: String? = null,
    @SerializedName("country_name") var countryName: String? = null,
    @SerializedName("city") var city: String? = null,
    @SerializedName("postal") var postal: String? = null,
    @SerializedName("latitude") var latitude: Double? = null,
    @SerializedName("longitude") var longitude: Double? = null,
    @SerializedName("IPv4") var IPv4: String? = null,
    @SerializedName("state") var state: String? = null

)

data class UserLocalCurrencyModel(

    @SerializedName("code") var code: String? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("symbol") var symbol: String? = null,
    @SerializedName("country_code") var country_code: String? = null,
    @SerializedName("decimal_digits") var decimal_digits: Double? = null,

    )