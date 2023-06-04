package company.tap.tapuilibraryy.uikit.atoms

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.*
import androidx.cardview.widget.CardView
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapuilibraryy.R
import company.tap.tapuilibraryy.fontskit.enums.TapFont
import company.tap.tapuilibraryy.uikit.AppColorTheme
import company.tap.tapuilibraryy.uikit.ktx.loadAppThemManagerFromPath


class TapCurrencyControlWidget : FrameLayout {
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
        inflate(context, R.layout.tap_currency_control_widget, this)

        val cardView = this.findViewById<CardView>(R.id.card_currency_widget)
        val button = this.findViewById<Button>(R.id.btn_confirm_tap_currency_control)
        val dropDownIv = this.findViewById<ImageView>(R.id.drop_down_iv)
        // val currencyWidgetAmount = this.findViewById<TapTextViewNew>(R.id.currency_widget_money_amount)
        val currencyWidgetDescription =
            this.findViewById<TapTextViewNew>(R.id.currency_widget_description)
        val spinner: Spinner = this.findViewById(R.id.currency_widget_spinner) as Spinner

        spinner.let {
            val currencies = mutableListOf<currncyData>(
                currncyData(resources.getDrawable(R.drawable.usa_flag), "USD 100.00"),
                (currncyData(resources.getDrawable(R.drawable.britian), "GBP 80.99"))
            )

            it.adapter = TapSpinnerAdapter(
                this.context,
                R.layout.custom_spinner,
                R.id.tv_spinnervalue,
                currencies
            )

            it.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (position != 0) {
                        val currncyData = parent.getItemAtPosition(position) as currncyData

                    }
                } // to close the onItemSelected

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }


            it.viewTreeObserver?.addOnWindowFocusChangeListener { hasFocus -> //This updates the arrow icon up/down depending on Spinner opening/closing
                if (hasFocus) {
                    rotateImage(dropDownIv, 0f)
                } else {
                    rotateImage(dropDownIv, -180F)
                }
            }
        }



        applyThemeForViews(cardView, button, currencyWidgetDescription)
    }

    private fun applyThemeForViews(
        cardView: CardView,
        button: Button,
        currencyWidgetDescription: TapTextViewNew
    ) {
        cardView.setCardBackgroundColor(loadAppThemManagerFromPath(AppColorTheme.ControlCurrencyWidgetBackground))
        button.backgroundTintList =
            ColorStateList.valueOf(loadAppThemManagerFromPath(AppColorTheme.ControlCurrencyWidgetConfirmButtonBackgroundColor))
        button.setTextColor(loadAppThemManagerFromPath(AppColorTheme.ControlCurrencyWidgetConfirmButtonTitleFontColor))
        button.text = LocalizationManager.getValue("confirmButton", "CurrencyChangeWidget")
        button.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.RobotoRegular
            )
        )
        currencyWidgetDescription.setTextColor(loadAppThemManagerFromPath(AppColorTheme.ControlCurrencyWidgetMessageColor))
        currencyWidgetDescription.text =
            LocalizationManager.getValue("header", "CurrencyChangeWidget")
        currencyWidgetDescription.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.RobotoRegular
            )
        )
    }

    private fun rotateImage(view: View, rotation: Float) {
        view.animate().rotation(rotation).setDuration(700).setInterpolator(
            AccelerateDecelerateInterpolator()
        ).start()
    }
}