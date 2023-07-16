package company.tap.tapuilibraryy.uikit.organisms

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextClock
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapuilibraryy.R
import company.tap.tapuilibraryy.fontskit.enums.TapFont
import company.tap.tapuilibraryy.themekit.ThemeManager
import company.tap.tapuilibraryy.themekit.theme.SwitchTheme
import company.tap.tapuilibraryy.uikit.AppColorTheme
import company.tap.tapuilibraryy.uikit.atoms.*
import company.tap.tapuilibraryy.uikit.datasource.LoyaltyHeaderDataSource
import company.tap.tapuilibraryy.uikit.getColorWithoutOpacity
import company.tap.tapuilibraryy.uikit.ktx.loadAppThemManagerFromPath
import company.tap.tapuilibraryy.uikit.views.CustomEditText
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
     * heeader
     */
    val imageLoyality by lazy { findViewById<ImageView>(R.id.img_loyality) }
    val txtLoyalityTitle by lazy { findViewById<TextView>(R.id.txt_loyality_title) }
    val txtBalanceTitle by lazy { findViewById<TextView>(R.id.txt_balance) }
    val switchLoyalty by lazy { findViewById<TapSwitch>(R.id.tap_switch) }
    val touchPointsDataGroup by lazy { findViewById<androidx.constraintlayout.widget.Group>(R.id.touch_points_data_group) }
    val txtTermsAndConditions by lazy { findViewById<TextView>(R.id.txt_terms_conditions) }

    /**
     * amount view
     */

    val redemptionCard by lazy { findViewById<MaterialCardView>(R.id.redemption_card) }
    val textStaticAmount by lazy { findViewById<TextView>(R.id.tv_redemption_amount) }
    val textRedemptionPoints by lazy { findViewById<TextView>(R.id.tv_total_redemption_points) }
    val textTotalAmount by lazy { findViewById<TextView>(R.id.tv_total_amount) }
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

        switchTheme()


    }

    fun setLayoutTheme() {
        //customEditText.visibility = View.GONE

        cardView.apply {
            setCardBackgroundColor(Color.parseColor(ThemeManager.getValue("loyaltyView.cardView.backgroundColor")))
            radius = 10f
        }
        redemptionCard.apply {
            setCardBackgroundColor(Color.parseColor(ThemeManager.getValue("loyaltyView.cardView.backgroundColor")))
            strokeWidth = 0
            radius = 10f
        }


    }


    private fun textViewsThemeIng() {
        txtTermsAndConditions.apply {
            text = LocalizationManager.getValue(
                "headerView",
                "TapLoyaltySection",
                subcomponentName = "tc"
            )
            setTextColor(loadAppThemManagerFromPath(AppColorTheme.LoyalityWidgetSwitchTintColor))
            paintFlags = this.paintFlags or Paint.UNDERLINE_TEXT_FLAG
            textSize = ThemeManager.getFontSize(AppColorTheme.LoyalityWidgetHeaderFont).toFloat()
            applyFontAccordingToLocalization(this)

        }
        txtLoyalityTitle.apply {
            text = LocalizationManager.getValue(
                "headerView",
                "TapLoyaltySection",
                subcomponentName = "redeem"
            )
            append(": ADCB Touch Points")
            setTextColor(Color.parseColor(ThemeManager.getValue("loyaltyView.headerView.titleTextColor")))
            textSize = ThemeManager.getFontSize("loyaltyView.headerView.titleFont").toFloat()
            applyFontAccordingToLocalization(this)

        }
        txtBalanceTitle.apply {
            text = LocalizationManager.getValue(
                "headerView",
                "TapLoyaltySection",
                subcomponentName = "balance"
            )
            append(": AED 520")
            setTextColor(Color.parseColor(ThemeManager.getValue("loyaltyView.headerView.subTitleTextColor")))
            textSize = ThemeManager.getFontSize("loyaltyView.headerView.titleFont").toFloat()
            typeface = Typeface.createFromAsset(
                context.assets, TapFont.tapFontType(
                    TapFont.RobotoRegular
                )
            )
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
                    ThemeManager.getValue<String?>("loyaltyView.footerView.pointsTextColor")
                        .toString().getColorWithoutOpacity()
                )
            )
            textSize = ThemeManager.getFontSize("loyaltyView.footerView.pointsFont").toFloat()
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
            setTextColor(Color.parseColor(ThemeManager.getValue("loyaltyView.amountView.titleTextColor")))
            textSize = ThemeManager.getFontSize("loyaltyView.amountView.titleFont").toFloat()
            applyFontAccordingToLocalization(this)

        }
        editTextAmount.apply {
            textSize = ThemeManager.getFontSize("loyaltyView.amountView.amountFont").toFloat()
            setHintTextColor(ColorStateList.valueOf(Color.parseColor(ThemeManager.getValue("loyaltyView.amountView.amountPlaceHolderColor"))))
            setTextColor(Color.parseColor(ThemeManager.getValue("loyaltyView.amountView.amountTextColor")))
            applyFontAccordingToLocalization(this)

        }
        textRedemptionPoints.apply {
            text = "(1000 ${resources.getString(R.string.points_abrv)})"
            setTextColor(Color.parseColor(ThemeManager.getValue("loyaltyView.amountView.pointsTextColor")))
            textSize = ThemeManager.getFontSize("loyaltyView.amountView.pointsFont").toFloat()
            applyFontAccordingToLocalization(this)

        }
        textTotalAmount.apply {
            setTextColor(Color.parseColor(ThemeManager.getValue("loyaltyView.amountView.amountTextColor")))
            textSize = ThemeManager.getFontSize("loyaltyView.amountView.amountFont").toFloat()
            applyFontAccordingToLocalization(this)
        }

        errorTextView.apply {
            setBackgroundColor(loadAppThemManagerFromPath((AppColorTheme.LoyalityWidgetInvalidAmountBackgroundColor).getColorWithoutOpacity()))
            setTextColor(loadAppThemManagerFromPath(AppColorTheme.LoyalityWidgetInvalidAmountTextColor))
            textSize =
                ThemeManager.getFontSize(AppColorTheme.LoyalityWidgetInvalidAmountFont).toFloat()

        }


//        textViewClickable.setTextColor(Color.parseColor(ThemeManager.getValue("loyaltyView.headerView.subTitleTextColor")))
//        textViewClickable.textSize =
//            ThemeManager.getFontSize("loyaltyView.headerView.subTitleFont").toFloat()
//
//        editTextAmount.setTextColor(Color.parseColor(ThemeManager.getValue("loyaltyView.amountView.amountTextColor")))
//        textViewCurrency.setTextColor(Color.parseColor(ThemeManager.getValue("loyaltyView.amountView.amountTextColor")))
//        editTextAmount.textSize =
//            ThemeManager.getFontSize("loyaltyView.amountView.amountFont").toFloat()
//
//        textViewSubTitle.setTextColor(Color.parseColor(ThemeManager.getValue("loyaltyView.amountView.titleTextColor")))
//        textViewSubTitle.textSize =
//            ThemeManager.getFontSize("loyaltyView.amountView.titleFont").toFloat()
//
//        textViewCurrency.setTextColor(Color.parseColor(ThemeManager.getValue("loyaltyView.amountView.currencyTextColor")))
//        textViewCurrency.textSize =
//            ThemeManager.getFontSize("loyaltyView.amountView.currencyFont").toFloat()
//
//        textViewTouchPoints.setTextColor(Color.parseColor(ThemeManager.getValue("loyaltyView.amountView.pointsTextColor")))
//        textViewTouchPoints.textSize =
//            ThemeManager.getFontSize("loyaltyView.amountView.pointsFont").toFloat()
//
//        textViewRemainPoints.setTextColor(Color.parseColor(ThemeManager.getValue("loyaltyView.footerView.pointsTextColor")))
//        textViewRemainPoints.textSize =
//            ThemeManager.getFontSize("loyaltyView.footerView.pointsFont").toFloat()
//
//        textViewRemainAmount.setTextColor(Color.parseColor(ThemeManager.getValue("loyaltyView.footerView.amountTextColor")))
//        textViewRemainAmount.textSize =
//            ThemeManager.getFontSize("loyaltyView.footerView.amountFont").toFloat()
    }

    private fun TextView?.applyFontAccordingToLocalization(textView: TextView) {
        with(textView) {
            if (LocalizationManager.getLocale(context).language == "ar") {
                typeface = Typeface.createFromAsset(
                    context.assets, TapFont.tapFontType(
                        TapFont.TajawalMedium
                    )
                )
            } else {
                typeface = Typeface.createFromAsset(
                    context.assets, TapFont.tapFontType(
                        TapFont.RobotoRegular
                    )
                )
            }
        }
    }

    fun switchTheme() {

        if (switchLoyalty?.isChecked == true) {
            touchPointsDataGroup.visibility = View.VISIBLE
            enableSwitchTheme()

        } else {
            touchPointsDataGroup.visibility = View.GONE
            disableSwitchTheme()
        }

    }

    private fun enableSwitchTheme() {
        val switchEnableTheme = SwitchTheme()
        switchEnableTheme.thumbTint =
            Color.parseColor(ThemeManager.getValue("TapSwitchView.main.backgroundColor"))
        switchEnableTheme.trackTint =
            Color.parseColor(ThemeManager.getValue("loyaltyView.headerView.switchOnTintColor"))
        switchLoyalty.setTheme(switchEnableTheme)
    }

    private fun disableSwitchTheme() {
        Log.e(
            "switchColor",
            ThemeManager.getValue<String>("TapSwitchView.main.backgroundColor").toString()
        )
        val switchDisableTheme = SwitchTheme()
        switchDisableTheme.thumbTint =
            Color.parseColor(ThemeManager.getValue("TapSwitchView.main.backgroundColor"))
        switchDisableTheme.trackTint =
            Color.parseColor(ThemeManager.getValue("TapSwitchView.main.backgroundColor"))
        switchLoyalty.setTheme(switchDisableTheme)
    }

//    fun setLinkClickable() {
//        textViewClickable.movementMethod = LinkMovementMethod.getInstance()
//    }


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