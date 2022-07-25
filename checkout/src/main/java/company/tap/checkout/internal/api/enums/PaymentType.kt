package company.tap.checkout.internal.api.enums

import com.google.gson.annotations.SerializedName


/**
 * Created by AhlaamK on 6/13/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
enum class PaymentType(val paymentType: String) {
    /**
     * Card payment type.
     */
    @SerializedName("card")
    CARD("card"),
    /**
     * Web payment type.
     */
    @SerializedName("web")
    WEB("web"),
    /**
     * Saved card payment type.
     */
    /**
     * Google Payment  payment type.
     */
    @SerializedName("google_pay")
     GOOGLE_PAY("google_pay"),
    @SerializedName("savedCard")
    SavedCard("savedCard"),
    telecom ("telecom")

}