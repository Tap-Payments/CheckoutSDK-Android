package company.tap.tapuilibrary.uikit.views

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.AttributeSet
import android.widget.LinearLayout
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapuilibrary.R
import company.tap.tapuilibrary.fontskit.enums.TapFont
import company.tap.tapuilibrary.themekit.ThemeManager
import company.tap.tapuilibrary.themekit.theme.SeparatorViewTheme
import company.tap.tapuilibrary.themekit.theme.TextViewTheme
import company.tap.tapuilibrary.uikit.atoms.TapSeparatorView
import company.tap.tapuilibrary.uikit.atoms.TapTextView

/**
 * Created  on 8/10/20.

Copyright (c) 2020   Tap Payments.
All rights reserved.
 **/
class TapAlertView : LinearLayout {

    val alertMessage by lazy { findViewById<TapTextView>(R.id.textViewAlertMessage) }
    val tapAlertLinear by lazy { findViewById<LinearLayout>(R.id.tapAlertLinear) }
    val topSeparator by lazy { findViewById<TapSeparatorView>(R.id.topSeparator) }
    val bottomSeparator by lazy { findViewById<TapSeparatorView>(R.id.bottomSeparator) }

    /**
     * Simple constructor to use when creating a TapAlertView from code.
     *  @param context The Context the view is running in, through which it can
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
        inflate(context, R.layout.tap_alert_view, this)
        initTheme()
        if (context?.let { LocalizationManager.getLocale(it).language } == "en")  setFontsEnglish()  else setFontsArabic()
    }

    private fun initTheme(){
        val warningBgColor:String = ThemeManager.getValue<String>("Hints.Warning.backgroundColor").toString()
        var opacityVal: String? = null
        //Workaround since we don't have direct method for extraction
        opacityVal = warningBgColor.substring(warningBgColor.length - 2)
        tapAlertLinear.setBackgroundColor(Color.parseColor("#"+opacityVal+warningBgColor.substring(0, warningBgColor.length -2).replace("#","")))

//        if (ThemeManager.currentTheme.isNotEmpty() && ThemeManager.currentTheme.contains("dark")) {
//            tapAlertLinear.setBackgroundResource(R.drawable.blur_background_dark)
//        } else {
//            tapAlertLinear.setBackgroundResource(R.drawable.blurbackground)
//        }

        var textTheme = TextViewTheme()
        textTheme.textColor = Color.parseColor(ThemeManager.getValue("Hints.Warning.textColor"))
        textTheme.textSize = ThemeManager.getFontSize("Hints.Warning.textFont")
        alertMessage.setTheme(textTheme)
        alertMessage.setTextColor(Color.parseColor(ThemeManager.getValue("Hints.Warning.textColor")))
        alertMessage.textSize = ThemeManager.getFontSize("Hints.Warning.textFont").toFloat()


        val separatorViewTheme = SeparatorViewTheme()
        val borderColor:String = ThemeManager.getValue<String>("Hints.Warning.borderColor").toString()
        var borderOpacityVal: String? = null
        //Workaround since we don't have direct method for extraction
        borderOpacityVal = borderColor.substring(borderColor.length - 2)
        separatorViewTheme.strokeColor =
            Color.parseColor("#"+borderOpacityVal+borderColor.substring(0, borderColor.length -2).replace("#",""))
      //  separatorViewTheme.strokeHeight = ThemeManager.getValue("TapAlertMessage.separatorHeight")
        topSeparator.setTheme(separatorViewTheme)
        bottomSeparator.setTheme(separatorViewTheme)
    }

    fun setFontsEnglish() {
        alertMessage?.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.RobotoLight
            )
        )
    }

    fun setFontsArabic() {
        alertMessage?.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.TajawalLight
            )
        )
    }


}