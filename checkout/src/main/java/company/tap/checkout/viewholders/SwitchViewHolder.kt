package company.tap.checkout.viewholders

import android.content.Context
import android.graphics.Color
import company.tap.checkout.enums.SectionType
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapuilibrary.datasource.TapSwitchDataSource

import company.tap.tapuilibrary.views.TapCardSwitch
import company.tap.thememanager.manager.ThemeManager
import company.tap.thememanager.theme.SwitchTheme
import company.tap.thememanager.theme.TextViewTheme

/**
 *
 * Created by Mario Gamal on 7/29/20
 * Copyright © 2020 Tap Payments. All rights reserved.
 *
 */
class SwitchViewHolder(context: Context) : TapBaseViewHolder {

    override val view = TapCardSwitch(context)

    override val type = SectionType.SAVE_CARD

    init {
        bindViewComponents()
//        setThemeView()
    }

    override fun bindViewComponents() {
        view.setSwitchDataSource(
            TapSwitchDataSource(
                switchSave = LocalizationManager.getValue("cardSaveLabel","TapCardInputKit"),
                switchSaveMerchantCheckout = "Save for [merchant_name] Checkouts",
                switchSavegoPayCheckout = "By enabling goPay, your mobile number will be saved with Tap Payments to get faster and more secure checkouts in multiple apps and websites.",
                savegoPayText = "Save for goPay Checkouts",
                alertgoPaySignup = "Please check your email or SMS’s in order to complete the goPay Checkout signup process."
            )
        )
    }



}

