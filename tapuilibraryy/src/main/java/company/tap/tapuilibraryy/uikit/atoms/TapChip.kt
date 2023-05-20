package company.tap.tapuilibraryy.uikit.atoms

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.View
import com.google.android.material.card.MaterialCardView
import company.tap.tapuilibraryy.themekit.theme.ChipTheme
import company.tap.tapuilibraryy.uikit.interfaces.TapView

/**
Copyright (c) 2020    Tap Payments.
All rights reserved.
 **/

/**
 * TapChip which uses default properties of MaterialCardView..
 * Simple constructor to use when creating a chip from code.
 *  @param context The Context the view is running in, through which it can
 *  access the current theme, resources, etc.
 *  @param attributeSet The attributes of the XML Button tag being used to inflate the view.
 */

open class TapChip(context: Context, attributeSet: AttributeSet?) :
    MaterialCardView(context, attributeSet),
    TapView<ChipTheme> {
     lateinit var view: View
     private lateinit var viewsList: List<View>

    /**
     *  @param context The Context the view is running in, through which it can
     *  access the current theme, resources, etc.
     *  @param attributeSet The attributes of the XML Button tag being used to inflate the view.
     *
     */
    constructor(context: Context, attributeSet: AttributeSet?, view: View) : this(
        context,
        attributeSet
    ) {
        this.view = view
    }

    constructor(context: Context, viewList: List<View>) : this(context, null) {
        this.viewsList = viewList
    }

    /**
     * Set the configure the current theme. If null is provided then the default Theme is returned
     * on the next call
     * @param theme Theme to consume in the wrapper, a value of null resets the theme to the default
     **/
    override fun setTheme(theme: ChipTheme) {
        theme.cardCornerRadius?.let { radius = it.toFloat() }
        theme.cardElevation?.let { cardElevation = it.toFloat() }
        theme.backgroundColor?.let { setBackgroundColor(it)  }
        theme.outlineSpotShadowColor?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                outlineSpotShadowColor = it
            }
        }
    }

}