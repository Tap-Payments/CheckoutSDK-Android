package company.tap.tapuilibrary.uikit.views

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.hbb20.CountryCodePicker
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapuilibrary.R
import company.tap.tapuilibrary.fontskit.enums.TapFont
import company.tap.tapuilibrary.themekit.ThemeManager
import company.tap.tapuilibrary.themekit.theme.SeparatorViewTheme
import company.tap.tapuilibrary.themekit.theme.TextViewTheme
import company.tap.tapuilibrary.uikit.atoms.TapEditText
import company.tap.tapuilibrary.uikit.atoms.TapSeparatorView
import company.tap.tapuilibrary.uikit.atoms.TapTextView

class TapShippingDetailView : LinearLayout {

    val shippingDetailTitle by lazy { findViewById<TapTextView>(R.id.cardShipping_Title) }
    val flatEditText by lazy { findViewById<TapEditText>(R.id.flat_EditText) }
    val additionalLineEditText by lazy { findViewById<TapEditText>(R.id.additionalLine_EditText) }
    val cityEditText by lazy { findViewById<TapEditText>(R.id.cityName_EditText) }
    val shippingCountryPicker by lazy { findViewById<CountryCodePicker>(R.id.shipping_countryCodePicker) }
    val shippingSeparator0 by lazy { findViewById<TapSeparatorView>(R.id.shippingSeparator0) }
    val shippingSeparator1 by lazy { findViewById<TapSeparatorView>(R.id.shippingSeparator1) }
    val shippingSeparator2 by lazy { findViewById<TapSeparatorView>(R.id.shippingSeparator2) }
    val cardShippingMainLL by lazy { findViewById<LinearLayout>(R.id.cardShipping_mainLL) }
    val mainShipLL by lazy { findViewById<LinearLayout>(R.id.mainShipLL) }
    private var countryCode: String? = null

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

    init {
        inflate(context, R.layout.tap_shipping_detail_view, this)
        setTheme()
        setSeparatorTheme()
        if (context?.let { LocalizationManager.getLocale(it).language } == "en") setFontsEnglish() else setFontsArabic()
        initCountryCodePicker()

    }

    fun setTheme(){
        val shippingTitleTextViewTheme = TextViewTheme()
        shippingTitleTextViewTheme.textColor =
            Color.parseColor(ThemeManager.getValue("customerDataCollection.textfields.color"))
        shippingTitleTextViewTheme.textSize =
            ThemeManager.getFontSize("customerDataCollection.textfields.font")
        shippingTitleTextViewTheme.font =
            ThemeManager.getFontName("customerDataCollection.textfields.font")

        shippingDetailTitle.setTheme(shippingTitleTextViewTheme)

      //  cardShippingMainLL.setBackgroundColor(Color.parseColor(ThemeManager.getValue("customerDataCollection.backgroundColor")))
        flatEditText.setHintTextColor(Color.parseColor(ThemeManager.getValue("customerDataCollection.textfields.placeHolderColor")))
        flatEditText.setTextColor(Color.parseColor(ThemeManager.getValue("customerDataCollection.textfields.color")))

        additionalLineEditText.setHintTextColor(Color.parseColor(ThemeManager.getValue("customerDataCollection.textfields.placeHolderColor")))
        additionalLineEditText.setTextColor(Color.parseColor(ThemeManager.getValue("customerDataCollection.textfields.color")))

        cityEditText.setHintTextColor(Color.parseColor(ThemeManager.getValue("customerDataCollection.textfields.placeHolderColor")))
        cityEditText.setTextColor(Color.parseColor(ThemeManager.getValue("customerDataCollection.textfields.color")))
        mainShipLL.setBackgroundColor(Color.parseColor(ThemeManager.getValue("customerDataCollection.backgroundColor")))

    }


    fun setSeparatorTheme() {
        val separatorViewTheme = SeparatorViewTheme()
        separatorViewTheme.strokeColor =
            Color.parseColor(ThemeManager.getValue("tapSeparationLine.backgroundColor"))
        separatorViewTheme.strokeHeight = ThemeManager.getValue("tapSeparationLine.height")
        shippingSeparator0.setTheme(separatorViewTheme)
        shippingSeparator1.setTheme(separatorViewTheme)
        shippingSeparator2.setTheme(separatorViewTheme)
    }


    fun setFontsEnglish() {
        shippingDetailTitle?.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.RobotoRegular
            )
        )

    }

    fun setFontsArabic() {
        shippingDetailTitle?.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.TajawalMedium
            )
        )

    }
    private fun initCountryCodePicker() {
        shippingCountryPicker.visibility = View.VISIBLE
        shippingCountryPicker.setDefaultCountryUsingNameCode("KW")
       // countryCode = shippingCountryPicker.defaultCountryCodeAsInt.toString()
        shippingCountryPicker.ccpDialogShowFlag = true
        shippingCountryPicker.showArrow(false)
        shippingCountryPicker.contentColor =
            Color.parseColor(ThemeManager.getValue("customerDataCollection.countryPicker.countryCell.titleLabelColor"))
        shippingCountryPicker.setDialogBackgroundColor(Color.parseColor(ThemeManager.getValue("customerDataCollection.countryPicker.countryTable.backgroundColor")))
    }
}