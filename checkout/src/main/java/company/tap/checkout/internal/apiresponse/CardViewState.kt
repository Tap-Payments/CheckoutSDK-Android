package company.tap.checkout.internal.apiresponse

import company.tap.checkout.internal.api.responses.PaymentOptionsResponse
import company.tap.checkout.internal.api.responses.SDKSettings
import company.tap.checkout.internal.dummygener.JsonResponseDummy1

/**
 * Created by AhlaamK on 11/15/20.

Copyright (c) 2020    Tap Payments.
All rights reserved.
 **/
data class CardViewState(
   // var initResponse: JsonResponseDummy1? = null
    var initResponse: SDKSettings ?= null,
    var paymentOptionsResponse: PaymentOptionsResponse? = null
)