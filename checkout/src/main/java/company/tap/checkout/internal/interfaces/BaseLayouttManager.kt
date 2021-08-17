package company.tap.checkout.internal.interfaces

import android.content.Context
import company.tap.checkout.internal.api.enums.PaymentType
import company.tap.checkout.internal.api.models.BINLookupResponse
import company.tap.checkout.internal.api.models.Charge
import company.tap.checkout.internal.api.models.PhoneNumber
import company.tap.checkout.internal.api.responses.DeleteCardResponse
import company.tap.checkout.internal.api.responses.PaymentOptionsResponse
import company.tap.checkout.internal.api.responses.SDKSettings
import company.tap.checkout.internal.apiresponse.CardViewModel
import company.tap.checkout.internal.enums.SectionType

/**
 *
 * Copyright © 2020 Tap Payments. All rights reserved.
 *
 */
interface BaseLayouttManager {
    fun displayStartupLayout(enabledSections: ArrayList<SectionType>)
    fun displayGoPayLogin()
    fun displayGoPay()
    fun controlCurrency(display: Boolean)
    fun displayOTPView(phoneNumber: PhoneNumber?, otpType:String, chargeResponse: Charge?=null)
    fun displayRedirect(url: String)
    fun displaySaveCardOptions()
    fun setBinLookupData(binLookupResponse: BINLookupResponse, context: Context,cardViewModel: CardViewModel)
    fun getDatasfromAPIs(sdkSettings: SDKSettings?,paymentOptionsResponse: PaymentOptionsResponse?)
    fun didDialogueExecute(response: String, cardTypeDialog: Boolean?)
    fun dialogueExecuteExtraFees(response :String,paymentType: PaymentType, savedCardsModel: Any?)
    fun deleteSelectedCardListener(delSelectedCard:DeleteCardResponse)
     fun handleSuccessFailureResponseButton(response: String)

}