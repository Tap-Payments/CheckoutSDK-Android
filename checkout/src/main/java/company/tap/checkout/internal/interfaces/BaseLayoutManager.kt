package company.tap.checkout.internal.interfaces

import android.content.Context
import company.tap.checkout.internal.api.enums.ChargeStatus
import company.tap.checkout.internal.api.enums.PaymentType
import company.tap.checkout.internal.api.models.Authenticate
import company.tap.checkout.internal.api.models.BINLookupResponse
import company.tap.checkout.internal.api.models.Charge
import company.tap.checkout.internal.api.models.PhoneNumber
import company.tap.checkout.internal.api.responses.*
import company.tap.checkout.internal.apiresponse.CardViewModel
import company.tap.checkout.internal.enums.SectionType
import company.tap.tapuilibraryy.uikit.views.TabAnimatedActionButton

/**
 *
 * Copyright © 2020 Tap Payments. All rights reserved.
 *
 */
interface BaseLayoutManager {
    fun displayStartupLayout(enabledSections: ArrayList<SectionType>)
    fun displayGoPayLogin()
    fun displayGoPay()
    fun controlCurrency(display: Boolean)
    fun reOpenSDKState()
    fun displayOTPView(phoneNumber: PhoneNumber?, otpType:String, chargeResponse: Charge?=null)
    fun displayRedirect(url: String,authenticate: Charge?)
    fun displaySaveCardOptions()
    fun setBinLookupData(binLookupResponse: BINLookupResponse, context: Context,cardViewModel: CardViewModel)
    fun getDatasfromAPIs(merchantData: MerchantData?,paymentOptionsResponse: PaymentOptionsResponse?)
    fun didDialogueExecute(response: String, cardTypeDialog: Boolean?)
    fun dialogueExecuteExtraFees(response :String,paymentType: PaymentType, savedCardsModel: Any?)
    fun deleteSelectedCardListener(delSelectedCard:DeleteCardResponse)
     fun handleSuccessFailureResponseButton(
         response: String,
         authenticate: Authenticate?,
         chargeResponse: Charge?,
         tabAnimatedActionButton: TabAnimatedActionButton?,
         contextSDK: Context?
     )
    fun displayAsynchronousPaymentView(chargeResponse:Charge)

    fun resetViewHolder()

}