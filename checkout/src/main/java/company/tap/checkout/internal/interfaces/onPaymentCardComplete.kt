package company.tap.checkout.internal.interfaces

import company.tap.checkout.internal.api.enums.PaymentType
import company.tap.checkout.internal.enums.PaymentTypeEnum

/**

Copyright (c) 2020    Tap Payments.
All rights reserved.
 **/
interface PaymentCardComplete {
    fun onPayCardSwitchAction(isCompleted: Boolean, paymentType: PaymentType , cardBrandString: String?=null)
    fun onPayCardCompleteAction(isCompleted: Boolean, paymentType: PaymentType, cardNumber: String, expiryDate: String, cvvNumber : String,holderName:String?,  cardBrandString: String?=null)
    fun onPaymentCardIsLoyaltyCard(isLoyaltyCard:Boolean)

}