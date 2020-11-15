package company.tap.checkout.open.controller

import android.graphics.Color
import androidx.fragment.app.FragmentManager
import company.tap.checkout.TapCheckoutFragment1


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
        TapCheckoutFragment1().apply {
            show(supportFragmentManager, tag)
        }
        // start session
        SessionManager.setActiveSession(true)


    }
}