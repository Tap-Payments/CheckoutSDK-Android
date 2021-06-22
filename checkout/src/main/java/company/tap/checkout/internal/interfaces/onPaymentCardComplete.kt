package company.tap.checkout.internal.interfaces

import company.tap.checkout.internal.api.enums.PaymentType
import company.tap.checkout.internal.enums.PaymentTypeEnum

/**

Copyright (c) 2020    Tap Payments.
All rights reserved.
 **/
interface PaymentCardComplete {
    fun onPayCardSwitchAction(isCompleted: Boolean, paymentType: PaymentType)
    fun onPayCardCompleteAction(isCompleted: Boolean, paymentType: PaymentType, cardNumber: String, expiryDate: String, cvvNumber : String)
}