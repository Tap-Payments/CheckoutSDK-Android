package company.tap.checkout.internal.interfaces

import company.tap.cardbusinesskit.testmodels.DummyResp
import company.tap.checkout.internal.enums.SectionType

/**
 *
 * Created by Mario Gamal on 7/24/20
 * Copyright © 2020 Tap Payments. All rights reserved.
 *
 */
interface BaseLayoutManager {
    fun displayStartupLayout(enabledSections: ArrayList<SectionType>)
    fun displayGoPayLogin()
    fun controlCurrency(display: Boolean)
    fun displayOTP(otpMobile:String)
    fun displayRedirect(url: String)
    fun displaySaveCardOptions()
    fun getDatafromAPI(dummyResponse: DummyResp)
}