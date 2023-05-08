package company.tap.tapuilibrary.uikit.atoms

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import androidx.appcompat.widget.SwitchCompat
import company.tap.tapuilibrary.themekit.theme.SwitchTheme
import company.tap.tapuilibrary.uikit.interfaces.TapView

/**
 * Created by AhlaamK on 4/15/20.

Copyright (c) 2020    Tap Payments.
All rights reserved.
 **/

/** TapSwitch is a two-state toggle switch widget that can select between two options. The user may drag the "thumb"
 *  back and forth to choose the selected option, or simply tap to toggle as if it were a checkbox
 * Construct a new Switch with default styling, overriding specific style
 * attributes as requested.
 * @param context The Context that will determine this widget's theming.
 * @param attributeSet Specification of attributes that should deviate from default styling.
**/
open class TapSwitch(context: Context, attributeSet: AttributeSet) :
    SwitchCompat(context, attributeSet),
    TapView<SwitchTheme> {

    /**
     * Set the configure the current theme. If null is provided then the default Theme is returned
     * on the next call
     * @param theme Theme to consume in the wrapper, a value of null resets the theme to the default
     */
    override fun setTheme(theme: SwitchTheme) {
        theme.backgroundColor?.let { setBackgroundColor(it) }
        theme.textColor?.let { setTextColor(it) }
        theme.textSize?.let { textSize = it.toFloat() }
        theme.letterSpacing?.let { letterSpacing = it.toFloat() }
        theme.thumbTint?.let { thumbTintList = ColorStateList.valueOf(it) }
        theme.trackTint?.let { trackTintList = ColorStateList.valueOf(it) }
        invalidate()
    }
}