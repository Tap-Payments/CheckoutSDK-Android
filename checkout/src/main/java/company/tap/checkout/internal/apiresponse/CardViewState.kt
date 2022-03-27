package company.tap.checkout.internal.apiresponse

import company.tap.checkout.internal.api.models.BINLookupResponse
import company.tap.checkout.internal.api.models.Charge
import company.tap.checkout.internal.api.responses.PaymentOptionsResponse
import company.tap.checkout.internal.api.responses.SDKSettings
import company.tap.checkout.internal.api.responses.TapConfigResponseModel

/**
 * Created by AhlaamK on 11/15/20.

Copyright (c) 2020    Tap Payments.
All rights reserved.
 **/
data class CardViewState(
    var configResponseModel: TapConfigResponseModel ?= null,
    var paymentOptionsResponse: PaymentOptionsResponse? = null
){
    data class ChargeViewState(var charge: Charge?=null)
    data class  BinLookupViewState(var binLookupResponse: BINLookupResponse?=null)

}

