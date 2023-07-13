package company.tap.tapuilibraryy.uikit.views

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.widget.*
import androidx.cardview.widget.CardView
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapuilibraryy.R
import kotlinx.android.synthetic.main.tap_loyality_view.view.*

class TapLoyalityView : FrameLayout {


    val cardView by lazy { findViewById<CardView>(R.id.card_touch_point) }
    val imageLoyality by lazy { findViewById<ImageView>(R.id.img_loyality) }
    val txtLoyalityTitle by lazy { findViewById<TextView>(R.id.txt_loyality_title) }
    val txtRemainingAmountToPay by lazy { findViewById<TextView>(R.id.txt_remaining_amount_to_pay) }
    val txtRemainingTouchPoints by lazy { findViewById<TextView>(R.id.txt_remaining_touch_points) }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        initView()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {
        initView()
    }

    constructor(context: Context) : super(context) {
        initView()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initView() {
        inflate(context, R.layout.tap_loyality_view, this)
        applyThemeForViews()
    }

    private fun applyThemeForViews(

    ) {
        txt_loyality_title.text = LocalizationManager.getValue("headerView","TapLoyaltySection")

//        cardView.setCardBackgroundColor(loadAppThemManagerFromPath(AppColorTheme.ControlCurrencyWidgetBackground))
//        button.backgroundTintList =
//            ColorStateList.valueOf(loadAppThemManagerFromPath(AppColorTheme.ControlCurrencyWidgetConfirmButtonBackgroundColor))
//        button.setTextColor(loadAppThemManagerFromPath(AppColorTheme.ControlCurrencyWidgetConfirmButtonTitleFontColor))
//        button.text = LocalizationManager.getValue("confirmButton", "CurrencyChangeWidget")
//        button.typeface = Typeface.createFromAsset(
//            context?.assets, TapFont.tapFontType(
//                TapFont.RobotoRegular
//            )
//        )
//
//        currencyWidgetDescription.setTextColor(loadAppThemManagerFromPath(AppColorTheme.ControlCurrencyWidgetMessageColor))
//        currencyWidgetDescription.text =
//            LocalizationManager.getValue("header", "CurrencyChangeWidget")
//        currencyWidgetDescription.typeface = Typeface.createFromAsset(
//            context?.assets, TapFont.tapFontType(
//                TapFont.RobotoRegular
//            )
//        )

    }

}
