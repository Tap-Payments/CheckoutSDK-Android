package company.tap.checkout.open.controller


import android.content.Context
import androidx.fragment.app.FragmentManager
import com.google.gson.JsonElement
import company.tap.cardbusinesskit.testmodels.*
import company.tap.checkout.internal.apiresponse.testmodels.*
import company.tap.checkout.internal.apiresponse.testmodels.InitOptionsRequest
import company.tap.checkout.open.data_managers.PaymentDataSource
import company.tap.tapnetworkkit.connection.NetworkApp
import company.tap.tapnetworkkit.controller.NetworkController
import company.tap.tapnetworkkit.enums.TapMethodType
import company.tap.tapnetworkkit.exception.GoSellError
import company.tap.tapnetworkkit.interfaces.APIRequestCallback
import retrofit2.Response

/**
 * Created by AhlaamK on 10/5/20.
Copyright (c) 2020    Tap Payments.
All rights reserved.
 **/
//Responsible for setting data given by Merchant  and starting the session
open class SDKSession : APIRequestCallback {
    private var paymentDataSource: PaymentDataSource? = null
    private var contextSDK: Context? = null
    init {
        initPaymentDataSource()

    }

    private fun checkSessionStatus() {
        if (SessionManager.isSessionEnabled()) {
            println("Session already active!!!")
            return
        }
    }

    private fun initPaymentDataSource() {
        this.paymentDataSource = PaymentDataSource

    }

    fun startSDK(supportFragmentManager: FragmentManager, context: Context) {
        println("is session enabled ${SessionManager.isSessionEnabled()}")
       /* if (SessionManager.isSessionEnabled()) {
            println("Session already active!!!")
            return
        }*/
        contextSDK = context
        println("we are already active!!!")
       /* val tapCheckoutFragment = TapCheckoutFragment()
        tapCheckoutFragment.show(supportFragmentManager, null)*/

         fun getCustomer(): Customer { // test customer id cus_Kh1b4220191939i1KP2506448
            val customer: Customer? = null


             val phoneNumber: Phone =
                if (customer != null) customer.phone else Phone(965, 69045932)
            return Customer(
                "firstname", "middlename",
                "lastname", "abcd@gmail.com",
                Phone(phoneNumber.country_code, phoneNumber.number), "description", Metadata(
                    "abc1",
                    "def2"
                ), "KWD"
            )

        }
        fun getItems(): Items {

            return Items(
                21, "kwd", "descrp1",
                Discount("P", 10), "wewqewewqeqwewewewewew", "dsa", "name", 1
            )

        }
        fun getShipping(): Shipping {

            return Shipping(
                21, "kwd", "descrp1",
                "provider1", "ship1", "hello"
            )

        }
        fun getRate(): Rate {

            return Rate(
                "descr1", 1
            )

        }
        fun getTax(): Tax {

            return Tax(
                "descr1", "name1", getRate()
            )

        }

 fun getOrder(): Order {

            return Order(
                "KWD", 100, listOf(getItems()),
                getShipping(), listOf(getTax())
            )

        }


        /*
             Passing post request body to obtain
             response for Payment options
             */
        val requestBody = InitOptionsRequest("charge", true, getCustomer(), getOrder())
        println("requests body" + requestBody)


        NetworkApp.initNetwork(
            contextSDK,
            "sk_test_kovrMB0mupFJXfNZWx6Etg5y",
            "company.tap.goSellSDKExample",
            "https://api.tap.company/v2/"
        )

        NetworkController.getInstance().processRequest(
            TapMethodType.POST,
            "intent",
            requestBody,
            this,
            11
        )
      /*  TapCheckoutFragment().apply {
            show(supportFragmentManager, tag)
        }*/
        // start session
        SessionManager.setActiveSession(true)


    }

    override fun onSuccess(responseCode: Int, requestCode: Int, response: Response<JsonElement>?) {
       println("onSuccess is being called ")
    }

    override fun onFailure(requestCode: Int, errorDetails: GoSellError?) {
        println("onFailure is being called ${errorDetails?.errorBody}")
    }
}



