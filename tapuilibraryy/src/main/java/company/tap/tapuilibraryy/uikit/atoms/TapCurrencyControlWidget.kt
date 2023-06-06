package company.tap.tapuilibraryy.uikit.atoms

import SupportedCurrencies
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.*
import androidx.cardview.widget.CardView
import com.google.android.material.shape.*
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapuilibraryy.R
import company.tap.tapuilibraryy.fontskit.enums.TapFont
import company.tap.tapuilibraryy.uikit.AppColorTheme
import company.tap.tapuilibraryy.uikit.ktx.loadAppThemManagerFromPath


class TapCurrencyControlWidget : FrameLayout {


    val cardView by lazy { findViewById<CardView>(R.id.card_currency_widget) }
    val confitmButton by lazy { findViewById<Button>(R.id.btn_confirm_tap_currency_control) }
    val dropDownIv by lazy { findViewById<ImageView>(R.id.drop_down_iv) }
    val currencyWidgetLogo by lazy { findViewById<ImageView>(R.id.currency_widget_logo) }
    val currencyWidgetDescription by lazy { findViewById<TapTextViewNew>(R.id.currency_widget_description) }
    val spinner: Spinner by lazy { findViewById<Spinner>(R.id.currency_widget_spinner) }
    lateinit var selectedCurrency: SupportedCurrencies

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
        applyThemeForViews(cardView, confitmButton, currencyWidgetDescription)
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

        val shapeDrawable =
            MaterialShapeDrawable(createSpinnerBackgroundShapeWithTrianleAtTopEdge())
        shapeDrawable.apply {
            strokeWidth = 1f
            strokeColor =
                ColorStateList.valueOf(resources.getColor(R.color.dropdown_stroke))
            this.fillColor =
                ColorStateList.valueOf(loadAppThemManagerFromPath(AppColorTheme.ControlCurrencyWidgetBackground))
        }
        spinner.setPopupBackgroundDrawable(shapeDrawable)
    }

    private fun createSpinnerBackgroundShapeWithTrianleAtTopEdge(): ShapeAppearanceModel {
        return ShapeAppearanceModel()
            .toBuilder()
            .setAllCorners(CornerFamily.ROUNDED,8f)
            .setTopEdge(TriangleEdgeTreatment(10f, false))
            .build()
    }

    @SuppressLint("SetTextI18n")
    fun setCurrencyWidgetDescription(displayNamePaymentOption: String?) {
        currencyWidgetDescription.text =
            "$displayNamePaymentOption " + LocalizationManager.getValue(
                "header",
                "CurrencyChangeWidget"
            )

    }

    fun setSupportedCurrunciesForControlWidget(displayNamePaymentOption: MutableList<SupportedCurrencies>) {
        spinner.let {
            it.adapter = TapSpinnerAdapter(
                this.context,
                R.layout.custom_spinner,
                R.id.tv_spinnervalue,
                displayNamePaymentOption
            )

            it.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    selectedCurrency = parent.getItemAtPosition(position) as SupportedCurrencies
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
    }


    fun getSelectedSupportedCurrency() = selectedCurrency

    private fun rotateImage(view: View, rotation: Float) {
        view.animate().rotation(rotation).setDuration(700).setInterpolator(
            AccelerateDecelerateInterpolator()
        ).start()
    }
}
