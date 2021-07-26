package company.tap.checkout.internal.interfaces

import android.content.Context
import company.tap.checkout.internal.api.models.BINLookupResponse
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
    fun displayOTPView(mobileNumber: String, otpType:String)
    fun displayRedirect(url: String)
    fun displaySaveCardOptions()
    fun setBinLookupData(binLookupResponse: BINLookupResponse, context: Context,cardViewModel: CardViewModel)
   // fun getDatafromAPI(dummyResponse1: JsonResponseDummy1)
    fun getDatasfromAPIs(sdkSettings: SDKSettings?,paymentOptionsResponse: PaymentOptionsResponse?)
   // fun getDatafromAPI(sdkSettings: SDKSettings)
   // fun getDataPaymentOptionsResponse(paymentOptionsResponse: PaymentOptionsResponse?)
    fun didDialogueExecute(response :String)
}