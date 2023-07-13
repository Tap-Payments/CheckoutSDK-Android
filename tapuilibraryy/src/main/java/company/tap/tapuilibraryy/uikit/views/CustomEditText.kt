package company.tap.tapuilibraryy.uikit.views

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.util.AttributeSet
import android.widget.*
import androidx.cardview.widget.CardView
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapuilibraryy.R
import company.tap.tapuilibraryy.fontskit.enums.TapFont
import company.tap.tapuilibraryy.themekit.ThemeManager
import kotlinx.android.synthetic.main.tap_loyality_view.view.*

class CustomEditText : FrameLayout {


    val editTextAmount by lazy { findViewById<EditText>(R.id.ed_amount_) }
    val currencyText by lazy { findViewById<TextView>(R.id.tv_currency) }


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
        inflate(context, R.layout.tap_custom_edit_text, this)
        currencyText.apply {
            text = resources.getString(R.string.aed)
            setTextColor(Color.parseColor(ThemeManager.getValue("loyaltyView.amountView.amountTextColor")))
            textSize = ThemeManager.getFontSize("loyaltyView.amountView.amountFont").toFloat()
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
        editTextAmount.apply {
            textSize = ThemeManager.getFontSize("loyaltyView.amountView.amountFont").toFloat()
            setHintTextColor(ColorStateList.valueOf(Color.parseColor(ThemeManager.getValue("loyaltyView.amountView.amountPlaceHolderColor"))))
            setTextColor(Color.parseColor(ThemeManager.getValue("loyaltyView.amountView.amountTextColor")))
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


}
