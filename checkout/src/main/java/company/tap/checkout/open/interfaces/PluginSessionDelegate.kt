package company.tap.checkout.open.interfaces

import company.tap.checkout.internal.api.models.Authorize
import company.tap.checkout.internal.api.models.Charge
import company.tap.checkout.internal.api.models.Token
import company.tap.checkout.open.models.CardsList
import company.tap.tapnetworkkit.exception.GoSellError

interface PluginSessionDelegate {
    fun paymentSucceed(charge: Charge)
    fun paymentFailed(charge: Charge?)

    fun authorizationSucceed(authorize: Authorize)
    fun authorizationFailed(authorize: Authorize?)


    fun cardSaved(charge: Charge)
    fun cardSavingFailed(charge: Charge)

    fun cardTokenizedSuccessfully(token: Token)

    fun savedCardsList(cardsList: CardsList)

    fun sdkError(goSellError: GoSellError?)

    fun sessionIsStarting()
    fun sessionHasStarted()
    fun sessionCancelled()
    fun sessionFailedToStart()

    fun invalidCardDetails()

    fun backendUnknownError(message: GoSellError?)

    fun invalidTransactionMode()

    fun invalidCustomerID()

    fun userEnabledSaveCardOption(saveCardEnabled: Boolean)

    fun getStatusSDK(response :String ? ,charge: Charge?)
}