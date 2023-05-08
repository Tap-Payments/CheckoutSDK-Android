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
import company.tap.tapuilibrary.uikit.atoms.*


class TapContactDetailsView : LinearLayout {

    val cardDetailTitle by lazy { findViewById<TapTextView>(R.id.cardDetails_Title) }
    val contactEmailET by lazy { findViewById<TapEditText>(R.id.emailId_EditText) }
    val mobileMainLinear by lazy { findViewById<LinearLayout>(R.id.mobilePaymentMainLinear) }
    val contactCountryPicker by lazy { findViewById<CountryCodePicker>(R.id.contact_countryCodePicker) }
    val contactMobileNumber by lazy { findViewById<TapEditText>(R.id.contact_mobileNumber) }
    val contactSeparator by lazy { findViewById<TapSeparatorView>(R.id.contactSeparator) }
    val cardDetailMainLL by lazy { findViewById<LinearLayout>(R.id.cardDet_mainLL) }
    val fieldLinearLayout by lazy { findViewById<LinearLayout>(R.id.field_LinearLayout) }
    val mainContactCardView by lazy { findViewById<TapChip>(R.id.mainContactCardView) }
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
        inflate(context, R.layout.tap_contact_detail_view, this)
        setTheme()
        setSeparatorTheme()
        if (context?.let { LocalizationManager.getLocale(it).language } == "en") setFontsEnglish() else setFontsArabic()

        initCountryCodePicker()
    }

    fun setTheme(){
        val contactTitleTextViewTheme = TextViewTheme()
        contactTitleTextViewTheme.textColor =
            Color.parseColor(ThemeManager.getValue("customerDataCollection.textfields.color"))
        contactTitleTextViewTheme.textSize =
            ThemeManager.getFontSize("customerDataCollection.textfields.font")
        contactTitleTextViewTheme.font =
            ThemeManager.getFontName("customerDataCollection.textfields.font")
        cardDetailTitle.setTheme(contactTitleTextViewTheme)

     //   cardDetailMainLL.setBackgroundColor(Color.parseColor(ThemeManager.getValue("customerDataCollection.backgroundColor")))
        contactEmailET.setHintTextColor(Color.parseColor(ThemeManager.getValue("customerDataCollection.textfields.placeHolderColor")))
        contactEmailET.setTextColor(Color.parseColor(ThemeManager.getValue("customerDataCollection.textfields.color")))
     //   contactEmailET.setBackgroundColor(Color.parseColor(ThemeManager.getValue("customerDataCollection.backgroundColor")))
        fieldLinearLayout.setBackgroundColor(Color.parseColor(ThemeManager.getValue("customerDataCollection.backgroundColor")))

        contactMobileNumber.setHintTextColor(Color.parseColor(ThemeManager.getValue("customerDataCollection.textfields.placeHolderColor")))
        contactMobileNumber.setTextColor(Color.parseColor(ThemeManager.getValue("customerDataCollection.textfields.color")))

       // mobileMainLinear.setBackgroundColor(Color.parseColor(ThemeManager.getValue("customerDataCollection.countryPicker.countryTable.backgroundColor")))
        mobileMainLinear.setBackgroundColor(Color.parseColor(ThemeManager.getValue("customerDataCollection.backgroundColor")))
    }


    fun setSeparatorTheme() {
        val separatorViewTheme = SeparatorViewTheme()
        separatorViewTheme.strokeColor =
            Color.parseColor(ThemeManager.getValue("tapSeparationLine.backgroundColor"))
        separatorViewTheme.strokeHeight = ThemeManager.getValue("tapSeparationLine.height")
        contactSeparator.setTheme(separatorViewTheme)
    }

    fun setFontsEnglish() {
        cardDetailTitle?.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.RobotoRegular
            )
        )

    }

    fun setFontsArabic() {
        cardDetailTitle?.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.TajawalMedium
            )
        )

    }

    private fun initCountryCodePicker() {
        contactCountryPicker.visibility = View.VISIBLE
        contactCountryPicker.setDefaultCountryUsingNameCode("KW")
        countryCode = contactCountryPicker.defaultCountryCodeAsInt.toString()
        contactCountryPicker.ccpDialogShowFlag = false
        contactCountryPicker.showArrow(false)
        contactCountryPicker?.contentColor =
            Color.parseColor(ThemeManager.getValue("customerDataCollection.countryPicker.countryCell.titleLabelColor"))
        contactCountryPicker.setDialogBackgroundColor(Color.parseColor(ThemeManager.getValue("customerDataCollection.countryPicker.countryTable.backgroundColor")))
    }
}