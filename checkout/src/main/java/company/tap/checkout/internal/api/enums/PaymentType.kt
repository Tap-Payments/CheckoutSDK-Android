package company.tap.checkout.internal.api.enums



/**
 * Created by AhlaamK on 6/13/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
enum class PaymentType(val paymentType: String) {
    /**
     * Card payment type.
     */
    CARD("card"),
    /**
     * Web payment type.
     */
    WEB("web"),
    /**
     * Saved card payment type.
     */
    SavedCard("savedCard")

}