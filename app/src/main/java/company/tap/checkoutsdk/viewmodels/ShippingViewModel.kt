package company.tap.checkoutsdk.viewmodels

import java.io.Serializable

/**
 * Created by AhlaamK on 9/12/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
class ShippingViewModel  (
shippingName: String,
shippingDescription: String, shippingAmount: String
) : Serializable {

    private var shippingName: String? = null
    private var shippingDecsription: String? = null
    private var shippingAmount: String? = null


    fun getshippingName(): String {
        return shippingName!!
    }

    fun getshippingDecsription(): String? {
        return shippingDecsription
    }

    fun getshippingAmount(): String? {
        return shippingAmount
    }


    private fun setSimpleText(
        _shippingName: String, _shippingDescription: String,
        _shippingAmount: String
    ) {

        this.shippingName = _shippingName
        this.shippingDecsription = _shippingDescription
        this.shippingAmount = _shippingAmount

    }



    init {
        setSimpleText(
            shippingName,shippingDescription,shippingAmount)
    }


}