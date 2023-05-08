package company.tap.tapuilibrary.uikit.views

import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.cardview.widget.CardView
import company.tap.tapuilibrary.R
import company.tap.tapuilibrary.themekit.ThemeManager



class TapBrandView : LinearLayout {

    val poweredByImage by lazy { findViewById<AppCompatImageView>(R.id.poweredByImage) }
    val backgroundHeader by lazy { findViewById<AppCompatImageView>(R.id.img_background) }
    val outerConstraint by lazy { findViewById<CardView>(R.id.outerConstraint) }

    @DrawableRes
    val logoIcon: Int =
        if (ThemeManager.currentTheme.isNotEmpty() && ThemeManager.currentTheme.contains("dark")) {
            R.drawable.poweredbytap2
        } else if (ThemeManager.currentTheme.isNotEmpty() && ThemeManager.currentTheme.contains("light")) {
            R.drawable.poweredbytap2
        } else R.drawable.poweredbytap2


    /**
     * Simple constructor to use when creating a TapHeader from code.
     *  @param con] ext The Context the view is running in, through which it can
     *  access the current theme, resources, etc.
     **/
    constructor(context: Context) : super(context)

    /**
     *  @param context The Context the view is running in, through which it can
     *  access the current theme, resources, etc.
     *  @param attrs The attributes of the XML Button tag being used to inflate the view.
     *
     */
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    /**
     *  @param context The Context the view is running in, through which it can
     *  access the current theme, resources, etc.
     *  @param attrs The attributes of the XML Button tag being used to inflate the view.
     * @param defStyleAttr The resource identifier of an attribute in the current theme
     * whose value is the the resource id of a style. The specified style’s
     * attribute values serve as default values for the button. Set this parameter
     * to 0 to avoid use of default values.
     */
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        inflate(context, R.layout.tap_brandview, this)
        poweredByImage.setImageResource(logoIcon)
    }


}