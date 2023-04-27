package company.tap.checkout.internal.api.network

import company.tap.checkout.internal.api.responses.UserIpAddressResponse
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

/**
 * This retrofit Client is really Basic One , just used with Api's not talking to TapBaseUrl
 * other cases we use TapNetwork Module
 */
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