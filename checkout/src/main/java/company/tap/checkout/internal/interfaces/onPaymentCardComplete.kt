package company.tap.checkout.internal.interfaces

import company.tap.checkout.internal.enums.PaymentActionType
import company.tap.checkout.internal.viewholders.PaymenttInputViewHolder

/**

Copyright (c) 2020    Tap Payments.
All rights reserved.
 **/
interface PaymentCardComplete {
    fun onPayCardSwitchAction(isCompleted: Boolean, paymentType: PaymentActionType)
    fun onPayCardCompleteAction(isCompleted: Boolean, paymentType: PaymentActionType, cardNumber: String, expiryDate: String, cvvNumber : String)
}