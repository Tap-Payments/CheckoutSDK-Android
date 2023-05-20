package company.tap.tapuilibraryy.uikit.views

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapuilibraryy.R
import company.tap.tapuilibraryy.fontskit.enums.TapFont
import company.tap.tapuilibraryy.themekit.ThemeManager
import company.tap.tapuilibraryy.themekit.theme.ButtonTheme
import company.tap.tapuilibraryy.themekit.theme.TextViewTheme
import company.tap.tapuilibraryy.uikit.atoms.TapButton
import company.tap.tapuilibraryy.uikit.atoms.TapChip
import company.tap.tapuilibraryy.uikit.atoms.TapImageView
import company.tap.tapuilibraryy.uikit.atoms.TapTextViewNew
import company.tap.tapuilibraryy.uikit.datasource.AmountViewDataSource
import company.tap.tapuilibraryy.uikit.ktx.setBorderedView

class TapAmountSectionView : LinearLayout {
    val selectedAmountValue by lazy { findViewById<TapTextViewNew>(R.id.selectedAmountValue) }
    val constraint by lazy { findViewById<ConstraintLayout>(R.id.constraint) }
    val mainKDAmountValue by lazy { findViewById<TapTextViewNew>(R.id.mainKDAmountValue) }
    val itemCountButton by lazy { findViewById<TapButton>(R.id.itemCountButton) }
    val itemCountText by lazy { findViewById<TapTextViewNew>(R.id.itemCountText) }
    val itemAmountText by lazy { findViewById<TapTextViewNew>(R.id.itemAmountText) }
    val amountImageView by lazy { findViewById<TapImageView>(R.id.amountImageView) }
    val itemAmountLayout by lazy { findViewById<LinearLayout>(R.id.item_amount_Layout) }
    val tapChipAmount by lazy { findViewById<TapChip>(R.id.tapChipAmount) } //todo framelayout changed to chip was giving error
    val tapChipPopup by lazy { findViewById<TapChip>(R.id.tapChipPopup) }  //todo framelayout changed to chip was giving error
    val itemPopupLayout by lazy { findViewById<LinearLayout>(R.id.item_popup_Layout) }
    val flagImageView by lazy { findViewById<TapImageView>(R.id.flagImageView) }
    val popupTextView by lazy { findViewById<TapTextViewNew>(R.id.popupTextView) }
    val viewSeparator by lazy { findViewById<View>(R.id.viewSeparator) }

    private var amountViewDataSource: AmountViewDataSource? = null

    @DrawableRes
    val dropDownIcon: Int =
        if (ThemeManager.currentTheme.isNotEmpty() && ThemeManager.currentTheme.contains("dark")){
            R.drawable.drop_down_arrow_dark
        } else if (ThemeManager.currentTheme.isNotEmpty() && ThemeManager.currentTheme.contains("light")) {
            R.drawable.drop_down_arrow_light
        }else R.drawable.drop_down_arrow_light
    /**
     * Simple constructor to use when creating a TapAmountSectionView from code.
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
        inflate(context, R.layout.tap_main_amount, this)
        itemCountButton.elevation = 0F
        setTheme()
        if (context?.let { LocalizationManager.getLocale(it).language } == "en") setFontsEnglish() else setFontsArabic()
    }

    fun setTheme() {
        val buttonTheme = ButtonTheme()
        buttonTheme.textColor =
            Color.parseColor(ThemeManager.getValue("amountSectionView.itemsLabelColor"))
        buttonTheme.borderColor =
            Color.parseColor(ThemeManager.getValue("amountSectionView.itemsNumberButtonBorder.color"))
        buttonTheme.backgroundColor =
            Color.parseColor(ThemeManager.getValue("amountSectionView.itemsNumberButtonBackgroundColor"))
        buttonTheme.cornerRadius =
            ThemeManager.getValue("amountSectionView.itemsNumberButtonCorner")
        itemCountButton.setTheme(buttonTheme)

        setBorderedView(
            itemCountButton,
            (ThemeManager.getValue("amountSectionView.itemsNumberButtonCorner") as Int).toFloat(),
            (ThemeManager.getValue("amountSectionView.itemsNumberButtonBorder.width")as Int).toFloat(), Color.parseColor( ThemeManager.getValue("amountSectionView.itemsNumberButtonBorder.color")),
            Color.parseColor(ThemeManager.getValue("amountSectionView.itemsNumberButtonBackgroundColor")),
            Color.parseColor(ThemeManager.getValue("amountSectionView.itemsNumberButtonBackgroundColor"))
        )

        val itemCountTextViewTheme = TextViewTheme()
        itemCountTextViewTheme.textColor =
            Color.parseColor(ThemeManager.getValue("amountSectionView.itemsLabelColor"))
        itemCountTextViewTheme.textSize =
            ThemeManager.getFontSize("amountSectionView.itemsLabelFont")
        itemCountTextViewTheme.font =
            ThemeManager.getFontName("amountSectionView.itemsLabelFont")
        itemCountText.setTheme(itemCountTextViewTheme)

        val itemAmountTextViewTheme = TextViewTheme()
        itemAmountTextViewTheme.textColor =
            Color.parseColor(ThemeManager.getValue("amountSectionView.itemsLabelColor"))
        itemAmountTextViewTheme.textSize =
            ThemeManager.getFontSize("amountSectionView.itemsLabelFont")
        itemAmountTextViewTheme.font =
            ThemeManager.getFontName("amountSectionView.itemsLabelFont")
        itemAmountText.setTheme(itemAmountTextViewTheme)

        val currentCurrencyTextViewTheme = TextViewTheme()
        currentCurrencyTextViewTheme.textColor =
            Color.parseColor(ThemeManager.getValue("amountSectionView.originalAmountLabelColor"))
        currentCurrencyTextViewTheme.textSize =
            ThemeManager.getFontSize("amountSectionView.originalAmountLabelFont")
        currentCurrencyTextViewTheme.font =
            ThemeManager.getFontName("amountSectionView.originalAmountLabelFont")
        selectedAmountValue.setTheme(currentCurrencyTextViewTheme)

        val selectedCurrencyTextViewTheme = TextViewTheme()
        selectedCurrencyTextViewTheme.textColor =
            Color.parseColor(ThemeManager.getValue("amountSectionView.convertedAmountLabelColor"))
        selectedCurrencyTextViewTheme.textSize =
            ThemeManager.getFontSize("amountSectionView.convertedAmountLabelFont")
        selectedCurrencyTextViewTheme.font =
            ThemeManager.getFontName("amountSectionView.convertedAmountLabelFont")
        mainKDAmountValue.setTheme(selectedCurrencyTextViewTheme)

        constraint.setBackgroundColor(Color.parseColor(ThemeManager.getValue("amountSectionView.backgroundColor")))

        amountImageView.setImageResource(dropDownIcon)

        tapChipAmount.setBackgroundColor(Color.parseColor(ThemeManager.getValue("TapCurrencyPromptView.backgroundColor")))
        tapChipAmount.radius = ThemeManager.getValue("amountSectionView.itemsNumberButtonCorner")
      // tapChipAmount.ou(Color.parseColor(ThemeManager.getValue("amountSectionView.itemsNumberButtonBackgroundColor")))
        setBorderedView(
            tapChipAmount,
             100.0f,// corner raduis
            0.0f,
            Color.parseColor(ThemeManager.getValue("TapCurrencyPromptView.backgroundColor")),
            Color.parseColor(ThemeManager.getValue("TapCurrencyPromptView.backgroundColor")),
            Color.parseColor(ThemeManager.getValue("TapCurrencyPromptView.backgroundColor"))
        )
        viewSeparator.setBackgroundColor(Color.parseColor(ThemeManager.getValue("itemsList.separatorColor")))

        tapChipPopup.setBackgroundColor(Color.parseColor(ThemeManager.getValue("amountSectionView.itemsNumberButtonBackgroundColor")))
        tapChipPopup.radius = ThemeManager.getValue("amountSectionView.itemsNumberButtonCorner")
        // tapChipAmount.ou(Color.parseColor(ThemeManager.getValue("amountSectionView.itemsNumberButtonBackgroundColor")))
        setBorderedView(
            tapChipPopup,
            100.0f,// corner raduis
            0.0f,
            Color.parseColor(ThemeManager.getValue("amountSectionView.itemsNumberButtonBackgroundColor")),
            Color.parseColor(ThemeManager.getValue("amountSectionView.itemsNumberButtonBackgroundColor")),
            Color.parseColor(ThemeManager.getValue("amountSectionView.itemsNumberButtonBorder.color"))
        )

        val popupTextViewTheme = TextViewTheme()
        popupTextViewTheme.textColor =
            Color.parseColor(ThemeManager.getValue("amountSectionView.itemsLabelColor"))
        popupTextViewTheme.textSize =
            ThemeManager.getFontSize("amountSectionView.itemsLabelFont")
        popupTextViewTheme.font =
            ThemeManager.getFontName("amountSectionView.itemsLabelFont")
        popupTextView.setTheme(popupTextViewTheme)

    }

    /**
     * @param amountViewDataSource is set via the consumer app for selectedCurrency,
     * currentCurrency and itemCount.
     **/
    fun setAmountViewDataSource(amountViewDataSource: AmountViewDataSource) {
        this.amountViewDataSource = amountViewDataSource
        selectedAmountValue.text = String.format(context.getString(R.string.item_price),amountViewDataSource.selectedCurrText , amountViewDataSource.selectedCurr)
        mainKDAmountValue.text = String.format(context.getString(R.string.item_price),amountViewDataSource.currentCurrText , amountViewDataSource.currentCurr)
       // itemCountButton.text =  amountViewDataSource.itemCount
        itemCountButton.tag =  amountViewDataSource.itemCount
        itemCountText.text =  amountViewDataSource.itemCount
        itemAmountText.text =  amountViewDataSource.selectedCurrText
        itemCountButton.tag =  amountViewDataSource.itemCount


//        amountViewDataSource.itemCount?.let { itemCountButton.text = it }
        itemCountButton.elevation = 0F
    }

    fun setFontsEnglish() {
        selectedAmountValue?.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.RobotoRegular
            )
        )
        mainKDAmountValue?.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.RobotoLight
            )
        )
        itemCountButton?.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.RobotoLight
            )
        )
        itemCountText?.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.RobotoRegular
            )
        )
        popupTextView?.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.RobotoRegular
            )
        )
        itemAmountText?.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.RobotoRegular
            )
        )
    }

    fun setFontsArabic() {
        selectedAmountValue?.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.TajawalMedium
            )
        )
        mainKDAmountValue?.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.TajawalLight
            )
        )
        itemCountButton?.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.TajawalLight
            )
        )
        itemCountText?.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.TajawalMedium
            )
        )
        itemAmountText?.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.TajawalMedium
            )
        )
        popupTextView?.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.TajawalMedium
            )
        )
    }
}