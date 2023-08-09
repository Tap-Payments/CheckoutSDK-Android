package company.tap.tapuilibrary.uikit.views

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.Log
import android.widget.LinearLayout
import androidx.cardview.widget.CardView
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapuilibrary.R
import company.tap.tapuilibrary.fontskit.enums.TapFont
import company.tap.tapuilibrary.themekit.ThemeManager
import company.tap.tapuilibrary.themekit.theme.SwitchTheme
import company.tap.tapuilibrary.themekit.theme.TextViewTheme
import company.tap.tapuilibrary.uikit.atoms.TapChip
import company.tap.tapuilibrary.uikit.atoms.TapSwitch
import company.tap.tapuilibrary.uikit.atoms.TapTextViewNew
import company.tap.tapuilibrary.uikit.datasource.TapSwitchDataSource
import company.tap.tapuilibrary.uikit.ktx.setBorderedView

class MainSwitch :LinearLayout {

    lateinit var attrs: AttributeSet
    private var tapSwitchDataSource: TapSwitchDataSource? = null

    val tapMainSwitchLinear by lazy { findViewById<CardView>(R.id.tapMainSwitchLinear) }
    val mainSwitchChip by lazy { findViewById<TapChip>(R.id.mainSwitchChip) }
    val mainSwitchLinear by lazy { findViewById<LinearLayout>(R.id.mainSwitchLinear) }
    val mainTextSave by lazy { findViewById<TapTextViewNew>(R.id.mainTextSave) }
    val switchSaveMobile by lazy { findViewById<TapSwitch>(R.id.switchSaveMobile) }
    val card by lazy { findViewById<CardView>(R.id.card) }


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
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        inflate(context, R.layout.main_switch_layout, this)
        setTheme()
        if (context?.let { LocalizationManager.getLocale(it).language } == "en") setFontsEnglish() else setFontsArabic()
    }





    /**
     * @param tapSwitchDataSource is set via the consumer app for saveMobile,
     * saveMerchantCheckout and savegoPayCheckout.
     **/
    fun setSwitchDataSource(tapSwitchDataSource: TapSwitchDataSource) {
        this.tapSwitchDataSource = tapSwitchDataSource
        tapSwitchDataSource.switchSave?.let {
            mainTextSave.text = it
        }

    }


    fun setTheme() {
        // Main switch
        switchSaveMobile.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Log.d("true", "true")
                var switchSaveMobileSwitchThemeEnable = SwitchTheme()
                switchSaveMobileSwitchThemeEnable.thumbTint =
                    Color.parseColor(ThemeManager.getValue("TapSwitchView.goPay.SwitchOnColor"))
                switchSaveMobileSwitchThemeEnable.trackTint =
                    Color.parseColor(ThemeManager.getValue("TapSwitchView.goPay.SwitchOnColor"))
                switchSaveMobile.setTheme(switchSaveMobileSwitchThemeEnable)
                setBorderedView(
                    card,
                    40f,// corner raduis
                    0.0f,
                    Color.parseColor(ThemeManager.getValue("TapSwitchView.main.backgroundColor")),// stroke color
                    Color.parseColor(ThemeManager.getValue("TapSwitchView.main.backgroundColor")),// tint color
                    Color.parseColor(ThemeManager.getValue("TapSwitchView.main.backgroundColor"))
                )//

            } else {
                Log.d("false", "false")
                var switchSaveMobileSwitchThemeDisable = SwitchTheme()
                switchSaveMobileSwitchThemeDisable.thumbTint =
                    Color.parseColor(ThemeManager.getValue("TapSwitchView.main.backgroundColor"))
                switchSaveMobileSwitchThemeDisable.trackTint =
                    Color.parseColor(ThemeManager.getValue("TapSwitchView.main.backgroundColor"))
                switchSaveMobile.setTheme(switchSaveMobileSwitchThemeDisable)
                setBorderedView(
                    card,
                    0f,// corner raduis
                    0.0f,
                    Color.parseColor(ThemeManager.getValue("TapSwitchView.main.backgroundColor")),// stroke color
                    Color.parseColor(ThemeManager.getValue("TapSwitchView.main.backgroundColor")),// tint color
                    Color.parseColor(ThemeManager.getValue("TapSwitchView.main.backgroundColor"))
                )//
            }
        }


        // main save
        var saveTextViewTextViewTheme = TextViewTheme()
        saveTextViewTextViewTheme.textColor =
            Color.parseColor(ThemeManager.getValue("TapSwitchView.main.title.textColor"))
        saveTextViewTextViewTheme.textSize =
            ThemeManager.getFontSize("TapSwitchView.main.title.textFont")
        saveTextViewTextViewTheme.font =
            ThemeManager.getFontName("TapSwitchView.main.title.textFont")
        mainTextSave.setTheme(saveTextViewTextViewTheme)

    }


    fun setFontsEnglish() {
        mainTextSave?.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.RobotoLight
            )
        )
    }

    fun setFontsArabic() {
        mainTextSave?.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.TajawalLight
            )
        )
    }

}