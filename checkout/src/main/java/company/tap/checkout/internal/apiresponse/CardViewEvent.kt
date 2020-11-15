package company.tap.checkout.internal.apiresponse

import company.tap.commonmodels.TapCard

/**
 * Created by AhlaamK on 11/15/20.

Copyright (c) 2020    Tap Payments.
All rights reserved.
 **/
sealed class CardViewEvent {
    object InitEvent : CardViewEvent()
    data class SaveCardEvent(val card: TapCard) : CardViewEvent()
    data class TokenizeCardEvent(val card: TapCard) : CardViewEvent()
}