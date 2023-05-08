package company.tap.tapuilibrary.uikit.views

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapuilibrary.R
import company.tap.tapuilibrary.fontskit.enums.TapFont
import company.tap.tapuilibrary.themekit.ThemeManager
import company.tap.tapuilibrary.themekit.theme.SeparatorViewTheme
import company.tap.tapuilibrary.themekit.theme.SwitchTheme
import company.tap.tapuilibrary.themekit.theme.TextViewTheme
import company.tap.tapuilibrary.uikit.atoms.TapImageView
import company.tap.tapuilibrary.uikit.atoms.TapSeparatorView
import company.tap.tapuilibrary.uikit.atoms.TapSwitch
import company.tap.tapuilibrary.uikit.atoms.TapTextView
import company.tap.tapuilibrary.uikit.datasource.TapSwitchDataSource
import company.tap.tapuilibrary.uikit.interfaces.TapActionButtonInterface

/**
Copyright (c) 2020    Tap Payments.
All rights reserved.
 **/

/**
 * TapCardSwitch is a molecule element for setting saveMobile ,saveMerchantCheckout and
 *  saveGoPayCheckout for Merchant
 **/
class TapCardSwitch : LinearLayout {
    val saveGoPay by lazy { findViewById<TapTextView>(R.id.save_goPay) }
    val alertGoPaySignUp by lazy { findViewById<TapTextView>(R.id.alert_gopay_signup) }
    val switchSaveMerchant by lazy { findViewById<TapSwitch>(R.id.switch_merchant_checkout) }
    val switchGoPayCheckout by lazy { findViewById<TapSwitch>(R.id.switch_gopay_checkout) }
    val tapCardSwitchLinear by lazy { findViewById<LinearLayout>(R.id.tapCardSwitchLinear) }
    val switchesLayout by lazy { findViewById<LinearLayout>(R.id.switches_layout) }
    val switchSeparator by lazy { findViewById<TapSeparatorView>(R.id.switch_separator) }
    val payButton by lazy { findViewById<TabAnimatedActionButton>(R.id.payButton) }
    val brandingLayout by lazy { findViewById<LinearLayout>(R.id.brandingLayout) }
    val textViewPowered by lazy { findViewById<TapTextView>(R.id.textViewPowered) }
    val tapLogoImage by lazy { findViewById<TapImageView>(R.id.tapLogoImage) }
   // val tapTextView by lazy { findViewById<TapTextView>(R.id.textTap_label) }
    private var actionButtonInterface: TapActionButtonInterface? = null
    @DrawableRes
    val logoIcon: Int =
        if (ThemeManager.currentTheme.isNotEmpty() && ThemeManager.currentTheme.contains("dark")){
            R.drawable.poweredby_dark_mode
        } else if (ThemeManager.currentTheme.isNotEmpty() && ThemeManager.currentTheme.contains("light")) {
            R.drawable.poweredby_light_mode
        }else R.drawable.poweredby_light_mode


    lateinit var attrs: AttributeSet
    private var tapSwitchDataSource: TapSwitchDataSource? = null

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
        inflate(context, R.layout.tap_card_switch, this)
        setTheme()
        if (context?.let { LocalizationManager.getLocale(it).language } == "en") setFontsEnglish() else setFontsArabic()
        initActionButton()

    }

    fun setSwitchInterface(actionButtonInterface: TapActionButtonInterface) {
        this.actionButtonInterface = actionButtonInterface
    }
    fun onEnterValidCardNumberActionListener(){
        payButton.setOnClickListener { actionButtonInterface?.onEnterValidCardNumberActionListener() }
    }
    fun onEnterValidPhoneNumberActionListener(){
        payButton.setOnClickListener { actionButtonInterface?.onEnterValidPhoneNumberActionListener() }
    }
    fun onSelectPaymentOptionActionListener(){
        payButton.setOnClickListener { actionButtonInterface?.onSelectPaymentOptionActionListener() }
    }

    private fun initActionButton() {
        payButton.setButtonDataSource(
            false,
            context?.let { LocalizationManager.getLocale(it).language },
            LocalizationManager.getValue("pay", "ActionButton"),
            Color.parseColor(ThemeManager.getValue("actionButton.Invalid.backgroundColor")),
            Color.parseColor(ThemeManager.getValue("actionButton.Invalid.titleLabelColor"))
        )
        payButton.isEnabled = true
        payButton.isClickable = true

        tapLogoImage.setImageResource(logoIcon)
    }

    fun showOnlyPayButton(){
        switchesLayout.visibility = View.GONE
        payButton.visibility = View.VISIBLE
        tapCardSwitchLinear.setBackgroundColor(Color.parseColor(ThemeManager.getValue("TapSwitchView.main.backgroundColor")))
    }


    /**
     * @param tapSwitchDataSource is set via the consumer app for saveMobile,
     * saveMerchantCheckout and savegoPayCheckout.
     **/
    fun setSwitchDataSource(tapSwitchDataSource: TapSwitchDataSource) {
        this.tapSwitchDataSource = tapSwitchDataSource
        tapSwitchDataSource.switchSaveMerchantCheckout?.let {
            switchSaveMerchant.text = it
        }
        tapSwitchDataSource.switchSavegoPayCheckout?.let {
            saveGoPay.text = it
        }
        tapSwitchDataSource.alertgoPaySignup?.let {
            alertGoPaySignUp.text = it
        }
        tapSwitchDataSource.savegoPayText?.let {
            switchGoPayCheckout.text = it
        }
    }


    fun setTheme() {
        tapCardSwitchLinear.setBackgroundColor(Color.parseColor(ThemeManager.getValue("TapSwitchView.main.backgroundColor")))

        // Merchant
        switchSaveMerchant.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                var switchSaveMerchantSwitchThemeEnable = SwitchTheme()
                switchSaveMerchantSwitchThemeEnable.thumbTint =
                    Color.parseColor(ThemeManager.getValue("TapSwitchView.merchant.SwitchOnColor"))
                switchSaveMerchantSwitchThemeEnable.trackTint =
                    Color.parseColor(ThemeManager.getValue("TapSwitchView.merchant.SwitchOnColor"))
                switchSaveMerchant.setTheme(switchSaveMerchantSwitchThemeEnable)
                payButton.setButtonDataSource(
                    true,
                    context?.let { LocalizationManager.getLocale(it).language },
                    LocalizationManager.getValue("pay", "ActionButton"),
                    Color.parseColor(ThemeManager.getValue("actionButton.Valid.paymentBackgroundColor")),
                    Color.parseColor(ThemeManager.getValue("actionButton.Valid.titleLabelColor"))
                )

                if (ThemeManager.currentTheme.isNotEmpty() && ThemeManager.currentTheme.contains("dark")) {
                    tapCardSwitchLinear.setBackgroundResource(R.drawable.blur_background_dark)
                } else {
                    tapCardSwitchLinear.setBackgroundResource(R.drawable.blurbackground)
                }

            } else {
                tapCardSwitchLinear.setBackgroundColor(Color.parseColor(ThemeManager.getValue("TapSwitchView.main.backgroundColor")))
                var switchSaveMerchantSwitchThemeDisable = SwitchTheme()
                switchSaveMerchantSwitchThemeDisable.thumbTint =
                    Color.parseColor(ThemeManager.getValue("TapSwitchView.main.backgroundColor"))
                switchSaveMerchantSwitchThemeDisable.trackTint =
                    Color.parseColor(ThemeManager.getValue("TapSwitchView.main.backgroundColor"))
                switchSaveMerchant.setTheme(switchSaveMerchantSwitchThemeDisable)
                payButton.setButtonDataSource(
                    false,
                    context?.let { LocalizationManager.getLocale(it).language },
                    LocalizationManager.getValue("pay", "ActionButton"),
                    Color.parseColor(ThemeManager.getValue("actionButton.Invalid.backgroundColor")),
                    Color.parseColor(ThemeManager.getValue("actionButton.Invalid.titleLabelColor"))
                )
            }
        }


        // Go Pay
        switchGoPayCheckout.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                var switchGoPayCheckoutSwitchThemeEnable = SwitchTheme()
                switchGoPayCheckoutSwitchThemeEnable.thumbTint =
                    Color.parseColor(ThemeManager.getValue("TapSwitchView.goPay.SwitchOnColor"))
                switchGoPayCheckoutSwitchThemeEnable.trackTint =
                    Color.parseColor(ThemeManager.getValue("TapSwitchView.goPay.SwitchOnColor"))
                switchGoPayCheckout.setTheme(switchGoPayCheckoutSwitchThemeEnable)

                activateButton(true)
                if (ThemeManager.currentTheme.isNotEmpty() && ThemeManager.currentTheme.contains("dark")) {
                    tapCardSwitchLinear.setBackgroundResource(R.drawable.blur_background_dark)
                } else {
                    tapCardSwitchLinear.setBackgroundResource(R.drawable.blurbackground)
                }

            } else {
                tapCardSwitchLinear.setBackgroundColor(Color.parseColor(ThemeManager.getValue("TapSwitchView.main.backgroundColor")))
                var switchGoPayCheckoutSwitchThemeDisable = SwitchTheme()
                switchGoPayCheckoutSwitchThemeDisable.thumbTint =
                    Color.parseColor(ThemeManager.getValue("TapSwitchView.main.backgroundColor"))
                switchGoPayCheckoutSwitchThemeDisable.trackTint =
                    Color.parseColor(ThemeManager.getValue("TapSwitchView.main.backgroundColor"))
                switchGoPayCheckout.setTheme(switchGoPayCheckoutSwitchThemeDisable)
                activateButton(false)
            }
        }

        // Go Pay TextTheme
        var saveGoPayTextViewTheme = TextViewTheme()
        saveGoPayTextViewTheme.textColor =
            Color.parseColor(ThemeManager.getValue("TapSwitchView.goPay.title.textColor"))
        saveGoPayTextViewTheme.textSize =
            ThemeManager.getFontSize("TapSwitchView.goPay.title.textFont")
        saveGoPayTextViewTheme.font = ThemeManager.getFontName("TapSwitchView.goPay.title.textFont")
        saveGoPay.setTheme(saveGoPayTextViewTheme)

        var alertGoPaySignUpTextViewTheme = TextViewTheme()
        alertGoPaySignUpTextViewTheme.textColor =
            Color.parseColor(ThemeManager.getValue("TapSwitchView.merchant.notes.textColor"))
        alertGoPaySignUpTextViewTheme.textSize =
            ThemeManager.getFontSize("TapSwitchView.merchant.notes.textFont")
        alertGoPaySignUpTextViewTheme.font =
            ThemeManager.getFontName("TapSwitchView.merchant.notes.textFont")
        alertGoPaySignUp.setTheme(alertGoPaySignUpTextViewTheme)


        var separatorViewTheme = SeparatorViewTheme()
        separatorViewTheme.strokeColor =
            Color.parseColor(ThemeManager.getValue("TapSwitchView.CurvedSeparator.BackgroundColor"))
        switchSeparator.setTheme(separatorViewTheme)

        var poweredByTextViewTheme = TextViewTheme()
        poweredByTextViewTheme.textColor =
            Color.parseColor(ThemeManager.getValue("poweredByTap.powerLabel.textColor"))
        poweredByTextViewTheme.textSize =
            ThemeManager.getFontSize("poweredByTap.powerLabel.font")
        poweredByTextViewTheme.font = ThemeManager.getFontName("poweredByTap.powerLabel.font")
        textViewPowered.setTheme(poweredByTextViewTheme)

        var tapTextViewTheme = TextViewTheme()
        tapTextViewTheme.textColor = Color.parseColor(ThemeManager.getValue("poweredByTap.powerLabel.textColor"))
        tapTextViewTheme.textSize =
            ThemeManager.getFontSize("poweredByTap.powerLabel.font")
        tapTextViewTheme.font = ThemeManager.getFontName("poweredByTap.powerLabel.font")
       // tapTextView.setTheme(tapTextViewTheme)
    }


    private fun activateButton(isActive: Boolean) {
        if (isActive) {
            payButton.setButtonDataSource(
                true,
                context?.let { LocalizationManager.getLocale(it).language },
                LocalizationManager.getValue("pay", "ActionButton"),
                Color.parseColor(ThemeManager.getValue("actionButton.Valid.paymentBackgroundColor")),
                Color.parseColor(ThemeManager.getValue("actionButton.Valid.titleLabelColor"))
            )
        } else {
            payButton.setButtonDataSource(
                false,
                context?.let { LocalizationManager.getLocale(it).language },
                LocalizationManager.getValue("pay", "ActionButton"),
                Color.parseColor(ThemeManager.getValue("actionButton.Invalid.backgroundColor")),
                Color.parseColor(ThemeManager.getValue("actionButton.Invalid.titleLabelColor"))
            )
        }
    }


    fun setFontsEnglish() {
        saveGoPay?.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.RobotoLight
            )
        )

        alertGoPaySignUp?.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.RobotoLight
            )
        )

    }

    fun setFontsArabic() {
        saveGoPay?.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.TajawalLight
            )
        )

        alertGoPaySignUp?.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.TajawalLight
            )
        )

    }


}