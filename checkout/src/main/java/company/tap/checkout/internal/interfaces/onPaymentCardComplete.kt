package company.tap.checkout.internal.interfaces

import company.tap.checkout.internal.viewholders.PaymenttInputViewHolder

/**
 * Created by AhlaamK on 9/15/20.

Copyright (c) 2020    Tap Payments.
All rights reserved.
 **/
interface onPaymentCardComplete {
    fun onPaycardSwitchAction(isCompleted: Boolean, paymentType: PaymenttInputViewHolder.PaymentType)
}