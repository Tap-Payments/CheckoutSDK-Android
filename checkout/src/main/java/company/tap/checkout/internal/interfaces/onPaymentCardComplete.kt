package company.tap.checkout.internal.interfaces

import company.tap.checkout.internal.enums.PaymentTypeEnum

/**

Copyright (c) 2020    Tap Payments.
All rights reserved.
 **/
interface PaymentCardComplete {
    fun onPayCardSwitchAction(isCompleted: Boolean, paymentType: PaymentTypeEnum)
    fun onPayCardCompleteAction(isCompleted: Boolean, paymentType: PaymentTypeEnum, cardNumber: String, expiryDate: String, cvvNumber : String)
}