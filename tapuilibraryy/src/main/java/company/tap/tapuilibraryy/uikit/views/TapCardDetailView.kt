package company.tap.tapuilibraryy.uikit.views

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapuilibraryy.R

class TapCardDetailView : LinearLayout {

    lateinit var attrs: AttributeSet

    /**
     * Simple constructor to use when creating a TapPayCardSwitch from code.
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
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        inflate(context, R.layout.tap_card_detail_view, this)
        setTheme()
        if (context?.let { LocalizationManager.getLocale(it).language } == "en") setFontsEnglish() else setFontsArabic()


    }
    fun setFontsEnglish() {
     /*   businessName?.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.RobotoRegular
            )
        )*/



    }

    fun setFontsArabic() {
       /* businessName?.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.TajawalRegular
            )
        )*/


    }

    fun setTheme() {

       /* val businessNameTextViewTheme = TextViewTheme()
        businessNameTextViewTheme.textColor =
            Color.parseColor(ThemeManager.getValue("merchantHeaderView.subTitleLabelColor"))
        businessNameTextViewTheme.textSize =
            ThemeManager.getFontSize("merchantHeaderView.subTitleLabelFont")
        businessNameTextViewTheme.font =
            ThemeManager.getFontName("merchantHeaderView.subTitleLabelFont")
        businessName.setTheme(businessNameTextViewTheme)*/

    }
}