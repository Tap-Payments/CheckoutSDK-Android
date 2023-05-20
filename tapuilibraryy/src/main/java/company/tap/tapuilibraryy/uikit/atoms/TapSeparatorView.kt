package company.tap.tapuilibraryy.uikit.atoms

import android.content.Context
import android.util.AttributeSet
import android.view.View
import company.tap.tapuilibraryy.themekit.theme.SeparatorViewTheme
import company.tap.tapuilibraryy.uikit.interfaces.TapView

/**
 * Created by AhlaamK on 6/10/20.

Copyright (c) 2020    Tap Payments.
All rights reserved.
 **/

/**
 * TapSeparatorView that is called when inflating a view from XML. This is called
 * when a view is being constructed from an XML file, supplying attributes
 * that were specified in the XML file. This version uses a default style of
 * 0, so the only attribute values applied are those in the Context's Theme
 * and the given AttributeSet.
 *
 * @param context The Context the view is running in, through which it can
 *        access the current theme, resources, etc.
 * @param attrs The attributes of the XML tag that is inflating the view.
**/
open class TapSeparatorView(context: Context?, attrs: AttributeSet?): View(context, attrs) ,
    TapView<SeparatorViewTheme> {

    /**
     * Set the configure the current theme. If null is provided then the default Theme is returned
     * on the next call
     * @param theme Theme to consume in the wrapper, a value of null resets the theme to the default
     */
    override fun setTheme(theme: SeparatorViewTheme) {
        theme.strokeColor?.let { setBackgroundColor(it)}
        theme.strokeWidth?.let { minimumWidth = it }
        theme.strokeHeight?.let { minimumHeight = it }
    }
}