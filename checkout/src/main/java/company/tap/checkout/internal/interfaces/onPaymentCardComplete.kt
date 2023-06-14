package company.tap.checkout.internal.interfaces

import company.tap.checkout.internal.api.enums.PaymentType
import company.tap.checkout.internal.api.models.SavedCard
import company.tap.checkout.internal.enums.PaymentTypeEnum

/**

Copyright (c) 2020    Tap Payments.
All rights reserved.
 **/
interface PaymentCardComplete {
    fun onPayCardSwitchAction(
        isCompleted: Boolean,
        paymentType: PaymentType,
        cardBrandString: String? = null
    )

    fun onPayCardCompleteAction(
        isCompleted: Boolean,
        paymentType: PaymentType,
        cardNumber: String? = null,
        expiryDate: String? = null,
        cvvNumber: String? = null,
        holderName: String? = null,
        cardBrandString: String? = null,
        savedCardsModel: Any? = null
    )

    fun onPaymentCardIsLoyaltyCard(isLoyaltyCard: Boolean)

    fun onPaymentCompletedShowingCurrencyWidget(cardBrandString: String)


}