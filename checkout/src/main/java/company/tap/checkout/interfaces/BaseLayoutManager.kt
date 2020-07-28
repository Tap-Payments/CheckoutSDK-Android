package company.tap.checkout.interfaces

import company.tap.checkout.enums.SectionType

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
    fun displayOTP()
    fun displayRedirect(url: String)
    fun displaySaveCardOptions()
}