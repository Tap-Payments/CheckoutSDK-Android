package company.tap.checkout.internal.apiresponse

import company.tap.checkout.internal.api.models.BINLookupResponse
import company.tap.checkout.internal.api.models.Charge
import company.tap.checkout.internal.api.responses.PaymentOptionsResponse
import company.tap.checkout.internal.api.responses.SDKSettings

/**
 * Created by AhlaamK on 11/15/20.

Copyright (c) 2020    Tap Payments.
All rights reserved.
 **/
data class CardViewState(
    var initResponse: SDKSettings ?= null,
    var paymentOptionsResponse: PaymentOptionsResponse? = null
){
    data class ChargeViewState(var charge: Charge?=null)
    data class  BinLookupViewState(var binLookupResponse: BINLookupResponse?=null)

}

