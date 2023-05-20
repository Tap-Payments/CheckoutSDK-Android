package company.tap.tapuilibraryy.uikit.organisms

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapuilibraryy.R
import company.tap.tapuilibraryy.fontskit.enums.TapFont
import company.tap.tapuilibraryy.themekit.ThemeManager
import company.tap.tapuilibraryy.themekit.theme.TextViewTheme
import company.tap.tapuilibraryy.uikit.atoms.TapImageView
import company.tap.tapuilibraryy.uikit.atoms.TapTextViewNew
import company.tap.tapuilibraryy.uikit.views.TabAnimatedActionButton


class FawryPaymentView  (context: Context?, attrs: AttributeSet?) :
    LinearLayout(context, attrs) {


        lateinit var attrs: AttributeSet
        val titleText by lazy { findViewById<TapTextViewNew>(R.id.titleText) }
        val descText by lazy { findViewById<TapTextViewNew>(R.id.descText) }
        val orderCodeText by lazy { findViewById<TapTextViewNew>(R.id.orderCodeText) }
        val orderCodeValue by lazy { findViewById<TapTextViewNew>(R.id.orderCodeValue) }
        val codeExpireText by lazy { findViewById<TapTextViewNew>(R.id.codeExpireText) }
        val codeExpireValue by lazy { findViewById<TapTextViewNew>(R.id.codeExpireValue) }
        val linkDescText by lazy { findViewById<TapTextViewNew>(R.id.linkDescText) }
        val linkValue by lazy { findViewById<TapTextViewNew>(R.id.linkValue) }
        val payButton by lazy { findViewById<TabAnimatedActionButton>(R.id.payButton) }
    val textViewPowered by lazy { findViewById<TapTextViewNew>(R.id.textViewPowered) }
    val tapLogoImage by lazy { findViewById<TapImageView>(R.id.tapLogoImage) }
//    val tapTextView by lazy { findViewById<TapTextView>(R.id.textTap_label) }
    val brandingLayout by lazy { findViewById<LinearLayout>(R.id.brandingLayout) }



    @DrawableRes
    val logoIcon: Int =
        if (ThemeManager.currentTheme.isNotEmpty() && ThemeManager.currentTheme.contains("dark")){
            R.drawable.poweredby_dark_mode
        } else if (ThemeManager.currentTheme.isNotEmpty() && ThemeManager.currentTheme.contains("light")) {
            R.drawable.poweredby_light_mode
        }else R.drawable.poweredby_light_mode


    init {
        inflate(context, R.layout.fawry_payment_view, this)
            setTheme()
            initButton()
            if (context?.let { LocalizationManager.getLocale(it).language } == "en") setFontsEnglish() else setFontsArabic()
        }


    fun initButton(){
        payButton.isEnabled = true
        payButton.setButtonDataSource(
            true,
            context?.let { LocalizationManager.getLocale(it).language },
            LocalizationManager.getValue("close", "Common"),
            Color.parseColor(ThemeManager.getValue("actionButton.Invalid.backgroundColor")),
            Color.parseColor(ThemeManager.getValue("actionButton.Valid.titleLabelColor"))
        )
        tapLogoImage.setImageResource(logoIcon)

        var poweredByTextViewTheme = TextViewTheme()
        poweredByTextViewTheme.textColor =
            Color.parseColor(ThemeManager.getValue("poweredByTap.powerLabel.textColor"))
        poweredByTextViewTheme.textSize =
            ThemeManager.getFontSize("poweredByTap.powerLabel.font")
        poweredByTextViewTheme.font = ThemeManager.getFontName("poweredByTap.powerLabel.font")
        textViewPowered.setTheme(poweredByTextViewTheme)

      /*  var tapTextViewTheme = TextViewTheme()
        tapTextViewTheme.textColor = Color.parseColor(ThemeManager.getValue("poweredByTap.powerLabel.textColor"))
        tapTextViewTheme.textSize =
            ThemeManager.getFontSize("poweredByTap.powerLabel.font")
        tapTextViewTheme.font = ThemeManager.getFontName("poweredByTap.powerLabel.font")*/
    }

    fun initViews(orderCode:String,codeExpire : String , link:String ){
        orderCodeValue.text = orderCode
        codeExpireValue.text = codeExpire
        linkValue.text = link
    }




    fun setTheme() {
        val titleTextViewTheme = TextViewTheme()
        titleTextViewTheme.textColor =
            Color.parseColor(ThemeManager.getValue("asyncView.labelsColor"))
        titleTextViewTheme.textSize =
            ThemeManager.getFontSize("asyncView.labelsFont")
        titleTextViewTheme.font =
            ThemeManager.getFontName("asyncView.labelsFont")
        titleText.setTheme(titleTextViewTheme)


        val descTextViewTheme = TextViewTheme()
        descTextViewTheme.textColor =
            Color.parseColor(ThemeManager.getValue("asyncView.labelsColor"))
        descTextViewTheme.textSize =
            ThemeManager.getFontSize("asyncView.labelsFont")
        descTextViewTheme.font =
            ThemeManager.getFontName("asyncView.labelsFont")
        descText.setTheme(descTextViewTheme)


        val orderCodeTextViewTheme = TextViewTheme()
        orderCodeTextViewTheme.textColor =
            Color.parseColor(ThemeManager.getValue("asyncView.labelsColor"))
        orderCodeTextViewTheme.textSize =
            ThemeManager.getFontSize("asyncView.labelsFont")
        orderCodeTextViewTheme.font =
            ThemeManager.getFontName("asyncView.labelsFont")
        orderCodeText.setTheme(orderCodeTextViewTheme)


        val orderCodeValueViewTheme = TextViewTheme()
        orderCodeValueViewTheme.textColor =
            Color.parseColor(ThemeManager.getValue("asyncView.codeLabelColor"))
        orderCodeValueViewTheme.textSize =
            ThemeManager.getFontSize("asyncView.codeLabelFont")
        orderCodeValueViewTheme.font =
            ThemeManager.getFontName("asyncView.codeLabelFont")
        orderCodeValue.setTheme(orderCodeValueViewTheme)


        val codeExpireTextViewTheme = TextViewTheme()
        codeExpireTextViewTheme.textColor =
            Color.parseColor(ThemeManager.getValue("asyncView.labelsColor"))
        codeExpireTextViewTheme.textSize =
            ThemeManager.getFontSize("asyncView.labelsFont")
        codeExpireTextViewTheme.font =
            ThemeManager.getFontName("asyncView.labelsFont")
        codeExpireText.setTheme(codeExpireTextViewTheme)


        val codeExpireValueViewTheme = TextViewTheme()
        codeExpireValueViewTheme.textColor =
            Color.parseColor(ThemeManager.getValue("asyncView.codeLabelColor"))
        codeExpireValueViewTheme.textSize =
            ThemeManager.getFontSize("asyncView.labelsFont")
        codeExpireValueViewTheme.font =
            ThemeManager.getFontName("asyncView.labelsFont")
        codeExpireValue.setTheme(codeExpireValueViewTheme)

        val linkDescTextViewTheme = TextViewTheme()
        linkDescTextViewTheme.textColor =
            Color.parseColor(ThemeManager.getValue("asyncView.labelsColor"))
        linkDescTextViewTheme.textSize =
            ThemeManager.getFontSize("asyncView.labelsFont")
        linkDescTextViewTheme.font =
            ThemeManager.getFontName("asyncView.labelsFont")
        linkDescText.setTheme(linkDescTextViewTheme)


        val linkValueViewTheme = TextViewTheme()
        linkValueViewTheme.textColor =
            Color.parseColor(ThemeManager.getValue("asyncView.labelsColor"))
        linkValueViewTheme.textSize =
            ThemeManager.getFontSize("asyncView.labelsFont")
        linkValueViewTheme.font =
            ThemeManager.getFontName("asyncView.labelsFont")
        linkValue.setTheme(linkValueViewTheme)

    }






    fun setFontsEnglish() {
        titleText?.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.RobotoRegular
            )
        )

        descText?.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.RobotoRegular
            )
        )

        orderCodeText?.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.RobotoRegular
            )
        )
        orderCodeValue?.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.RobotoRegular
            )
        )
        codeExpireText?.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.RobotoRegular
            )
        )

        codeExpireValue?.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.RobotoRegular
            )
        )


        linkDescText?.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.RobotoRegular
            )
        )
        linkValue?.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.RobotoRegular
            )
        )

    }

    fun setFontsArabic() {
        titleText?.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.TajawalMedium
            )
        )

        descText?.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.TajawalMedium
            )
        )

        orderCodeText?.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.TajawalMedium
            )
        )
        orderCodeValue?.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.TajawalMedium
            )
        )
        codeExpireText?.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.TajawalMedium
            )
        )


        codeExpireValue?.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.TajawalMedium
            )
        )

        linkDescText?.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.TajawalMedium
            )
        )
        linkValue?.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.TajawalMedium
            )
        )

    }






}