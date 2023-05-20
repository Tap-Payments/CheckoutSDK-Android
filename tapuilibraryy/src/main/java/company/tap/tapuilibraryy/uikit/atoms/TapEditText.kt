package company.tap.tapuilibraryy.uikit.atoms

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import company.tap.tapuilibraryy.themekit.theme.EditTextTheme
import company.tap.tapuilibraryy.uikit.interfaces.TapView

/**
 * Created by AhlaamK on 4/14/20.

Copyright (c) 2020    Tap Payments.
All rights reserved.
 **/

/**
 * A user interface element for entering and modifying text.
 * Simple constructor to use when creating a TapEditText from code.
 *  @param context The Context the view is running in, through which it can
 *  access the current theme, resources, etc.
 *  @param attributeSet The attributes of the XML Button tag being used to inflate the view.
 * */
open class TapEditText(context: Context, attributeSet: AttributeSet) :
    AppCompatEditText(context, attributeSet),
    TapView<EditTextTheme> {

    /**
     * Set the configure the current theme. If null is provided then the default Theme is returned
     * on the next call
     * @param theme Theme to consume in the wrapper, a value of null resets the theme to the default
     */
    override fun setTheme(theme: EditTextTheme) {
        theme.maxLines?.let { maxLines = it }
        theme.textColor?.let { setTextColor(it) }
        theme.textSize?.let { textSize = it.toFloat()}
        theme.letterSpacing?.let { letterSpacing = it.toFloat() }
        theme.textColorHint?.let { setHintTextColor(it) }
        theme.backgroundTint?.let { backgroundTintList = ColorStateList.valueOf(it) }
        invalidate()
    }

}