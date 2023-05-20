package company.tap.tapuilibraryy.uikit.utils

import android.graphics.Color
import company.tap.tapuilibraryy.themekit.ThemeManager
import company.tap.tapuilibraryy.themekit.theme.TextViewTheme

/**
 *
 * Created on 7/15/20
 * Copyright © 2020 Tap Payments. All rights reserved.
 *
 */
object FakeThemeManager {

    fun getGoPayTabLayoutTextTheme(isSelected: Boolean): TextViewTheme {
        val theme = TextViewTheme()
        if (isSelected)
            theme.textColor =
                getGoPaySelectedTabColor()
        else
            theme.textColor =
                getGoPayUnSelectedTabColor()
        return theme
    }

    fun getGoPaySelectedTabColor() = Color.parseColor(ThemeManager.getValue("goPay.loginBar.title.selected.textColor"))
    fun getGoPayUnSelectedTabColor() = Color.parseColor(ThemeManager.getValue("goPay.loginBar.title.otherSegmentSelected.textColor"))
    fun getGoPayValidatedColor() = Color.parseColor(ThemeManager.getValue("goPay.loginBar.underline.selected.backgroundColor"))
    fun getGoPayUnValidatedColor() = Color.parseColor(ThemeManager.getValue("goPay.loginBar.underline.unselected.backgroundColor"))
    fun getGoPaySignInButtonColor() = Color.parseColor("#b9b9b9")
}