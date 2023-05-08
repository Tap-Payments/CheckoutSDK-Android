package company.tap.tapuilibrary.uikit.atoms

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import company.tap.tapuilibrary.themekit.ThemeManager
import company.tap.tapuilibrary.themekit.theme.ButtonTheme
import company.tap.tapuilibrary.uikit.interfaces.TapView


/**
Copyright (c) 2020    Tap Payments.
All rights reserved.
 **/

    /**
     * TapButton is a user interface element the user can tap or click to perform an action.
     * Simple constructor to use when creating a button from code.
     *  @param context The Context the view is running in, through which it can
     *  access the current theme, resources, etc.
     *  @param attributeSet The attributes of the XML Button tag being used to inflate the view.
     *
     */

open class TapButton(context: Context, attributeSet: AttributeSet) :
    AppCompatButton(context, attributeSet),
        TapView<ButtonTheme> {
    /**
     * Set the configure the current theme. If null is provided then the default Theme is returned
     * on the next call
     * @param theme Theme to consume in the wrapper, a value of null resets the theme to the default
     */
    override fun setTheme(theme: ButtonTheme) {

        theme.textColor?.let { setTextColor(it) }
        theme.backgroundColor?.let { setBackgroundColor(it) }
        theme.textSize?.let { textSize = it.toFloat() }
        theme.letterSpacing?.let { letterSpacing = it.toFloat() }
        invalidate()
    }
}