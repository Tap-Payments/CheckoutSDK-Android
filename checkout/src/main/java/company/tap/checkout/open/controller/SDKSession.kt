package company.tap.checkout.open.controller

import androidx.fragment.app.FragmentManager
import company.tap.cardbusinesskit.testmodels.*
import company.tap.checkout.internal.apiresponse.testmodels.InitOptionsRequest
import company.tap.checkout.open.TapCheckoutFragment


import company.tap.checkout.open.data_managers.PaymentDataSource

/**
 * Created by AhlaamK on 10/5/20.
Copyright (c) 2020    Tap Payments.
All rights reserved.
 **/
//Responsible for setting data given by Merchant  and starting the session
open class SDKSession {
    private var paymentDataSource: PaymentDataSource? = null

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

    fun startSDK(supportFragmentManager: FragmentManager) {
        println("is session enabled ${SessionManager.isSessionEnabled()}")
        if (SessionManager.isSessionEnabled()) {
            println("Session already active!!!")
            return
        }
       /* val tapCheckoutFragment = TapCheckoutFragment()
        tapCheckoutFragment.show(supportFragmentManager, null)*/

         fun getCustomer(): Customer { // test customer id cus_Kh1b4220191939i1KP2506448
            val customer: Customer? = null
             val phoneNumber: Phone =
                if (customer != null) customer.phone else Phone(965, 69045932)
            return Customer("firstname","middlename",
                "lastname","abcd@gmail.com",
               Phone(phoneNumber.country_code, phoneNumber.number),"descr",null,"KWD")

        }
        fun getItems(): Items? {
            var discount:Discount?=null
            discount?.type="P"
            discount?.value=10
            return discount?.let {
                Items(21,"kwd","descrp1",
                    it,"wewqewewqeqwewewewewew","dsa","name",1)
            }
        }

 fun getOrder(): Order {

            return Order("KWD",100,getItems(),
                null,null)

        }


        /*
             Passing post request body to obtain
             response for Payment options
             */
       // val requestBody = InitOptionsRequest("charge", true, getCustomer(), "order")

        TapCheckoutFragment().apply {
            show(supportFragmentManager, tag)
        }
        // start session
        SessionManager.setActiveSession(true)


    }
}