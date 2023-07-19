package company.tap.checkout.open.interfaces


import androidx.annotation.RestrictTo
import company.tap.checkout.internal.api.enums.ChargeStatus
import company.tap.checkout.internal.api.models.Authorize
import company.tap.checkout.internal.api.models.Charge
import company.tap.checkout.internal.api.models.Token
import company.tap.checkout.open.models.CardsList
import company.tap.tapnetworkkit.exception.GoSellError

/**
 * Created by AhlaamK on 7/5/21.
Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
@RestrictTo(RestrictTo.Scope.LIBRARY)
interface CheckOutDelegate {

    fun checkoutChargeCaptured(charge: Charge)
    fun checkoutChargeFailed(charge: Charge?)

    fun checkoutAuthorizeCaptured(authorize: Authorize)
    fun checkoutAuthorizeFailed(authorize: Authorize?)


    fun cardSaved(charge: Charge)
    fun cardSavingFailed(charge: Charge)

    fun cardTokenizedSuccessfully(token: Token , saveCard:Boolean)

    fun savedCardsList(cardsList: CardsList)

    fun checkoutSdkError(goSellError: GoSellError?)

    fun sessionIsStarting()
    fun sessionHasStarted()
    fun sessionCancelled()
    fun sessionFailedToStart()

    fun invalidCardDetails()

    fun backendUnknownError(message: GoSellError?)

    fun invalidTransactionMode()

    fun invalidCustomerID()

    fun userEnabledSaveCardOption(saveCardEnabled: Boolean)

    fun  asyncPaymentStarted(charge:Charge)



    }