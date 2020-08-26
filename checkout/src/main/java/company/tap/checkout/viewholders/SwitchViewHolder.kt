package company.tap.checkout.viewholders

import android.content.Context
import android.graphics.Color
import company.tap.checkout.enums.SectionType
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapuilibrary.atoms.TapSwitch
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


    fun setThemeView(){
        view.switchSaveMobile.textSize = ThemeManager.getFontSize("TapSwitchView").toFloat()
        var switchTheme = SwitchTheme()
        switchTheme.textSize = ThemeManager.getFontSize("TapSwitchView")
        switchTheme.textColor = Color.parseColor(ThemeManager.getValue("TapSwitchView"))
        view.switchSaveMobile.setTheme(switchTheme)


        view.saveTextView.textSize = ThemeManager.getFontSize("TapSwitchView").toFloat()
         var textViewTheme = TextViewTheme()
        textViewTheme.textColor = Color.parseColor(ThemeManager.getValue("TapSwitchView"))
        textViewTheme.textSize = ThemeManager.getFontSize("TapSwitchView")
        textViewTheme.font = ThemeManager.getFontName("TapSwitchView")
        view.saveTextView.setTheme(textViewTheme)


        view.switchSaveMerchant.textSize = ThemeManager.getFontSize("TapSwitchView").toFloat()
        var switchTheme1 = SwitchTheme()
        switchTheme1.textSize = ThemeManager.getFontSize("TapSwitchView")
        switchTheme1.textColor = Color.parseColor(ThemeManager.getValue("TapSwitchView"))
        view.switchSaveMerchant.setTheme(switchTheme1)


        view.switchgoPayCheckout.textSize = ThemeManager.getFontSize("TapSwitchView").toFloat()
        var switchTheme2 = SwitchTheme()
        switchTheme2.textSize = ThemeManager.getFontSize("TapSwitchView")
        switchTheme2.textColor = Color.parseColor(ThemeManager.getValue("TapSwitchView"))
        view.switchgoPayCheckout.setTheme(switchTheme2)


        view.savegoPay.textSize = ThemeManager.getFontSize("TapSwitchView").toFloat()
        var textViewTheme1 = TextViewTheme()
        textViewTheme1.textColor = Color.parseColor(ThemeManager.getValue("TapSwitchView"))
        textViewTheme1.textSize = ThemeManager.getFontSize("TapSwitchView")
        textViewTheme1.font = ThemeManager.getFontName("TapSwitchView")
        view.savegoPay.setTheme(textViewTheme1)


        view.alertgoPaySignup.textSize = ThemeManager.getFontSize("TapSwitchView").toFloat()
        var textViewTheme2 = TextViewTheme()
        textViewTheme2.textColor = Color.parseColor(ThemeManager.getValue("TapSwitchView"))
        textViewTheme2.textSize = ThemeManager.getFontSize("TapSwitchView")
        textViewTheme2.font = ThemeManager.getFontName("TapSwitchView")
        view.alertgoPaySignup.setTheme(textViewTheme2)

    }
}

