package company.tap.tapuilibraryy.uikit.organisms

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.Group
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapuilibraryy.R
import company.tap.tapuilibraryy.fontskit.enums.TapFont
import company.tap.tapuilibraryy.themekit.ThemeManager
import company.tap.tapuilibraryy.themekit.theme.SwitchTheme
import company.tap.tapuilibraryy.uikit.AppColorTheme
import company.tap.tapuilibraryy.uikit.AppColorTheme.GlobalValuesColorWhite30
import company.tap.tapuilibraryy.uikit.AppColorTheme.LoyalityWidgetAmountPlaceHolderColor
import company.tap.tapuilibraryy.uikit.AppColorTheme.LoyalityWidgetAmountViewAmountTextColor
import company.tap.tapuilibraryy.uikit.AppColorTheme.LoyalityWidgetAmountViewAmountTextFont
import company.tap.tapuilibraryy.uikit.AppColorTheme.LoyalityWidgetAmountViewTitleTextColor
import company.tap.tapuilibraryy.uikit.AppColorTheme.LoyalityWidgetAmountViewTitleTextFont
import company.tap.tapuilibraryy.uikit.AppColorTheme.LoyalityWidgetAmountViewPointsFont
import company.tap.tapuilibraryy.uikit.AppColorTheme.LoyalityWidgetAmountViewPointsTextColor
import company.tap.tapuilibraryy.uikit.AppColorTheme.LoyalityWidgetCardViewBackgroundColor
import company.tap.tapuilibraryy.uikit.AppColorTheme.LoyalityWidgetCardViewRadius
import company.tap.tapuilibraryy.uikit.AppColorTheme.LoyalityWidgetFooterViewPointsFont
import company.tap.tapuilibraryy.uikit.AppColorTheme.LoyalityWidgetFooterViewPointsTextColor
import company.tap.tapuilibraryy.uikit.AppColorTheme.LoyalityWidgetHeaderFont
import company.tap.tapuilibraryy.uikit.AppColorTheme.LoyalityWidgetHeaderSubtitle
import company.tap.tapuilibraryy.uikit.AppColorTheme.LoyalityWidgetHeaderTitleTextColor
import company.tap.tapuilibraryy.uikit.AppColorTheme.LoyalityWidgetInvalidAmountBackgroundColor
import company.tap.tapuilibraryy.uikit.AppColorTheme.LoyalityWidgetInvalidAmountFont
import company.tap.tapuilibraryy.uikit.AppColorTheme.LoyalityWidgetInvalidAmountTextColor
import company.tap.tapuilibraryy.uikit.AppColorTheme.LoyalityWidgetSwitchTintColor
import company.tap.tapuilibraryy.uikit.AppColorTheme.LoyalityWidgetTapSwitchBackgroundColor
import company.tap.tapuilibraryy.uikit.AppColorTheme.LoyalityWidgetTapSwitchTintColor
import company.tap.tapuilibraryy.uikit.atoms.TapSwitch
import company.tap.tapuilibraryy.uikit.datasource.LoyaltyHeaderDataSource
import company.tap.tapuilibraryy.uikit.doOnLanguageChange
import company.tap.tapuilibraryy.uikit.getColorWithoutOpacity
import company.tap.tapuilibraryy.uikit.ktx.loadAppThemManagerFromPath
import company.tap.tapuilibraryy.uikit.views.TextDrawable


class TapLoyaltyView(context: Context?, attrs: AttributeSet?) :
    LinearLayout(context, attrs) {
//    val constraintLayout by lazy { findViewById<LinearLayout>(R.id.constraintLayout) }
//    val linearLayout1 by lazy { findViewById<LinearLayout>(R.id.linearLayout1) }
//    val linearLayout2 by lazy { findViewById<LinearLayout>(R.id.linearLayout2) }
//    val linearLayout3 by lazy { findViewById<LinearLayout>(R.id.linearLayout3) }
//    val cardViewOut by lazy { findViewById<CardView>(R.id.cardViewOut) }
//    val mainChip by lazy { findViewById<TapChip>(R.id.mainChip) }
//    val iconBank by lazy { findViewById<TapImageView>(R.id.iconBank) }
//    val textViewTitle by lazy { findViewById<TapTextViewNew>(R.id.textViewTitle) }
//    val textViewClickable by lazy { findViewById<TapTextViewNew>(R.id.textViewClickable) }
//    val textViewSubTitle by lazy { findViewById<TapTextViewNew>(R.id.textViewSubTitle) }
//    val editTextAmount by lazy { findViewById<TextInputEditText>(R.id.editTextAmount) }
//    val textViewTouchPoints by lazy { findViewById<TapTextViewNew>(R.id.textViewTouchPoints) }
//    val textViewCurrency by lazy { findViewById<TapTextViewNew>(R.id.textViewCurrency) }
//    val loyaltyAlertView by lazy { findViewById<TapAlertView>(R.id.loyalty_alertView) }
//    val textViewRemainPoints by lazy { findViewById<TapTextViewNew>(R.id.textViewRemainPoints) }
//    val textViewRemainAmount by lazy { findViewById<TapTextViewNew>(R.id.textViewRemainAmount) }


    val cardView by lazy { findViewById<CardView>(R.id.card_touch_point) }

    /**
     * header
     */
    val imageLoyality by lazy { findViewById<ImageView>(R.id.img_loyality) }
    val txtLoyalityTitle by lazy { findViewById<TextView>(R.id.txt_loyality_title) }
    val txtBalanceTitle by lazy { findViewById<TextView>(R.id.txt_balance) }
    val switchLoyalty by lazy { findViewById<TapSwitch>(R.id.tap_switch) }
    val touchPointsDataGroup by lazy { findViewById<Group>(R.id.touch_points_data_group) }
    val txtTermsAndConditions by lazy { findViewById<TextView>(R.id.txt_terms_conditions) }

    /**
     * amount view
     */

    val redemptionCard by lazy { findViewById<MaterialCardView>(R.id.redemption_card) }
    val textStaticAmount by lazy { findViewById<TextView>(R.id.tv_redemption_amount) }
    val textRedemptionPoints by lazy { findViewById<TextView>(R.id.tv_total_redemption_points) }
    val textCurrency by lazy { findViewById<TextView>(R.id.tv_total_amount) }
    val editTextAmount by lazy { findViewById<EditText>(R.id.ed_amount) }
    val errorTextView by lazy { findViewById<TextView>(R.id.tv_error) }

    /**
     * footer
     */
    val txtRemainingAmountToPay by lazy { findViewById<TextView>(R.id.txt_remaining_amount_to_pay) }
    val txtRemainingTouchPoints by lazy { findViewById<TextView>(R.id.txt_remaining_touch_points) }


    private var loyaltyHeaderDataSource: LoyaltyHeaderDataSource? = null

    init {
        inflate(context, R.layout.tap_loyalty_view, this)
        applyTheme()


    }

    private fun applyTheme() {
//        mainChip.setBackgroundColor(Color.parseColor(ThemeManager.getValue("loyaltyView.cardView.backgroundColor")))
//        constraintLayout.setBackgroundColor(Color.parseColor(ThemeManager.getValue("loyaltyView.cardView.backgroundColor")))
        // cardViewOut.radius = ThemeManager.getValue("loyaltyView.cardView.radius")
        // mainChip.outlineSpotShadowColor = (Color.parseColor(ThemeManager.getValue("loyaltyView.cardView.shadowColor")))
//        setBorderedView(
//            cardViewOut,
//            23f,// corner raduis
//            0.0f,
//            Color.parseColor(ThemeManager.getValue("loyaltyView.cardView.shadowColor")),// stroke color
//            Color.parseColor(ThemeManager.getValue("loyaltyView.cardView.backgroundColor")),// tint color
//            Color.parseColor(ThemeManager.getValue("loyaltyView.cardView.backgroundColor"))//shadow color
//        )
        setLayoutTheme()
        textViewsThemeIng()


    }

    fun setLayoutTheme() {
        cardView.apply {
            setCardBackgroundColor(loadAppThemManagerFromPath(LoyalityWidgetCardViewBackgroundColor))
            radius = ThemeManager.getValue(LoyalityWidgetCardViewRadius)
        }
        redemptionCard.apply {
            setCardBackgroundColor(loadAppThemManagerFromPath(LoyalityWidgetCardViewBackgroundColor))
            strokeWidth = 0
            radius = ThemeManager.getValue(LoyalityWidgetCardViewRadius)
        }


    }


    private fun textViewsThemeIng() {
        txtTermsAndConditions.apply {
            text = LocalizationManager.getValue(
                "headerView",
                "TapLoyaltySection",
                subcomponentName = "tc"
            )
            setTextColor(loadAppThemManagerFromPath(LoyalityWidgetSwitchTintColor))
            paintFlags = this.paintFlags or Paint.UNDERLINE_TEXT_FLAG
            textSize = ThemeManager.getFontSize(LoyalityWidgetHeaderFont).toFloat()
            applyFontAccordingToLocalization(this)

        }
        txtLoyalityTitle.apply {
            text = LocalizationManager.getValue(
                "headerView",
                "TapLoyaltySection",
                subcomponentName = "redeem"
            )
            append(": ADCB Touch Points")
            setTextColor(loadAppThemManagerFromPath(LoyalityWidgetHeaderTitleTextColor))
            textSize = ThemeManager.getFontSize(AppColorTheme.LoyalityWidgetHeaderFont).toFloat()
            applyFontAccordingToLocalization(this)

        }
        txtBalanceTitle.apply {
            text = LocalizationManager.getValue(
                "headerView",
                "TapLoyaltySection",
                subcomponentName = "balance"
            )
            append(" : AED 520")
            setTextColor(loadAppThemManagerFromPath(LoyalityWidgetHeaderSubtitle))
            textSize = ThemeManager.getFontSize(LoyalityWidgetHeaderFont).toFloat()
            applyFontAccordingToLocalization(this)

        }
        txtRemainingTouchPoints.apply {
            text = LocalizationManager.getValue(
                "footerView",
                "TapLoyaltySection",
                subcomponentName = "points"
            )
            append(": 4,200 ${resources.getString(R.string.points)} (AED 420.00)")
            setTextColor(
                Color.parseColor(
                    ThemeManager.getValue<String?>(LoyalityWidgetFooterViewPointsTextColor)
                        .toString().getColorWithoutOpacity()
                )
            )
            textSize = ThemeManager.getFontSize(LoyalityWidgetFooterViewPointsFont).toFloat()
            applyFontAccordingToLocalization(this)

            alpha = 0.5f
        }
        txtRemainingAmountToPay.apply {
            text = LocalizationManager.getValue(
                "footerView",
                "TapLoyaltySection",
                subcomponentName = "amount"
            )
            append(": AED 0.00")
            setTextColor(
                Color.parseColor(
                    ThemeManager.getValue<String?>("loyaltyView.footerView.amountTextColor")
                        .toString().getColorWithoutOpacity()
                )
            )
            textSize = ThemeManager.getFontSize("loyaltyView.footerView.amountFont").toFloat()
            applyFontAccordingToLocalization(this)

            alpha = 0.9f

        }
        textStaticAmount.apply {
            text = LocalizationManager.getValue(
                "amountView",
                "TapLoyaltySection",
                subcomponentName = "title"
            )
            textSize = ThemeManager.getFontSize(LoyalityWidgetAmountViewTitleTextFont)
                .toFloat()
            applyFontAccordingToLocalization(this)

        }
        editTextAmount.apply {
            textSize =
                ThemeManager.getFontSize(LoyalityWidgetAmountViewAmountTextFont)
                    .toFloat()
            setHintTextColor(
                ColorStateList.valueOf(
                    loadAppThemManagerFromPath(
                        LoyalityWidgetAmountPlaceHolderColor
                    )
                )
            )
            applyFontAccordingToLocalization(this)

        }
        textRedemptionPoints.apply {
            text = "(1000 ${resources.getString(R.string.points_abrv)})"
            setTextColor(loadAppThemManagerFromPath(LoyalityWidgetAmountViewPointsTextColor))
            textSize = ThemeManager.getFontSize(LoyalityWidgetAmountViewPointsFont).toFloat()
            applyFontAccordingToLocalization(this)

        }
        textCurrency.apply {
            textSize = ThemeManager.getFontSize(LoyalityWidgetAmountViewAmountTextFont).toFloat()
            applyFontAccordingToLocalization(this)
        }

        errorTextView.apply {
            setBackgroundColor(
                Color.parseColor(
                    ThemeManager.getValue<String?>(LoyalityWidgetInvalidAmountBackgroundColor)
                        .toString().getColorWithoutOpacity()
                )
            )
            background.alpha = 60
            setTextColor(loadAppThemManagerFromPath(LoyalityWidgetInvalidAmountTextColor))
            textSize = ThemeManager.getFontSize(LoyalityWidgetInvalidAmountFont).toFloat()

        }
        changeColorOfTextsInNormalView()

    }

    fun changeColorOfTextsInErrorView() {
        textStaticAmount.apply {
            setTextColor(loadAppThemManagerFromPath(LoyalityWidgetInvalidAmountTextColor))
        }
        textCurrency.apply {
            setTextColor(loadAppThemManagerFromPath(LoyalityWidgetInvalidAmountTextColor))
        }
        editTextAmount.apply {
            setTextColor(loadAppThemManagerFromPath(LoyalityWidgetInvalidAmountTextColor))
            context.doOnLanguageChange(doOnArabic = {
                setCompoundDrawablesWithIntrinsicBounds(R.drawable.error_msg_icon, 0, 0, 0);
            }, doOnEnGlish = {
                setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.error_msg_icon, 0);
            })

        }
    }

    fun changeColorOfTextsInNormalView() {
        textStaticAmount.apply {
            setTextColor(loadAppThemManagerFromPath(LoyalityWidgetAmountViewTitleTextColor))
        }
        textCurrency.apply {
            setTextColor(loadAppThemManagerFromPath(LoyalityWidgetAmountViewAmountTextColor))
        }
        editTextAmount.apply {
            setTextColor(loadAppThemManagerFromPath(LoyalityWidgetAmountViewAmountTextColor))
            setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

        }
    }

    private fun TextView?.applyFontAccordingToLocalization(textView: TextView) {
        with(textView) {
            context.doOnLanguageChange(doOnArabic = {
                typeface = Typeface.createFromAsset(
                    context.assets, TapFont.tapFontType(
                        TapFont.TajawalMedium
                    )
                )
            }, doOnEnGlish = {
                typeface = Typeface.createFromAsset(
                    context.assets, TapFont.tapFontType(
                        TapFont.RobotoRegular
                    )
                )
            })
        }
    }

    fun enableSwitchTheme() {
        val switchEnableTheme = SwitchTheme()
        switchEnableTheme.thumbTint =
            loadAppThemManagerFromPath(LoyalityWidgetTapSwitchBackgroundColor)
        switchEnableTheme.trackTint = loadAppThemManagerFromPath(LoyalityWidgetTapSwitchTintColor)
        switchLoyalty.setTheme(switchEnableTheme)
    }

    fun disableSwitchTheme() {
        val switchDisableTheme = SwitchTheme()
//        switchDisableTheme.thumbTint =
//            loadAppThemManagerFromPath(LoyalityWidgetTapSwitchBackgroundColor)

        switchDisableTheme.thumbTint = Color.parseColor(
            ThemeManager.getValue<String?>(GlobalValuesColorWhite30)
                .toString().dropLast(2)
        )
        switchDisableTheme.trackTint =
            loadAppThemManagerFromPath(LoyalityWidgetTapSwitchBackgroundColor)
        switchLoyalty.setTheme(switchDisableTheme)
    }

    /**
     * @param loyaltyHeaderDataSource is set via the consumer app for bankName,
     * bankIcon.
     **/
    fun setLoyaltyHeaderDataSource(loyaltyHeaderDataSource: LoyaltyHeaderDataSource) {
        this.loyaltyHeaderDataSource = loyaltyHeaderDataSource


        Glide.with(this)
            .load(loyaltyHeaderDataSource.bankImageResources)
            .placeholder(
                TextDrawable(
                    loyaltyHeaderDataSource.bankName?.get(0).toString()
                )
            )
            .error(
                TextDrawable(
                    loyaltyHeaderDataSource.bankName?.get(0).toString()
                )
            )
            .into(imageLoyality)


    }


}