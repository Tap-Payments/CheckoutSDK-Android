package company.tap.tapuilibraryy.uikit.organisms

import android.content.Context
import android.graphics.Color
import android.text.method.LinkMovementMethod
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.cardview.widget.CardView
import com.bumptech.glide.Glide
import company.tap.tapuilibraryy.themekit.ThemeManager
import company.tap.tapuilibraryy.themekit.theme.SwitchTheme
import company.tap.tapuilibraryy.uikit.atoms.*
import company.tap.tapuilibraryy.uikit.datasource.LoyaltyHeaderDataSource
import company.tap.tapuilibraryy.uikit.ktx.setBorderedView
import company.tap.tapuilibraryy.uikit.views.TapAlertView
import company.tap.tapuilibraryy.uikit.views.TextDrawable
import company.tap.tapuilibraryy.R
import company.tap.tapuilibraryy.uikit.atoms.TapImageView

class TapLoyaltyView (context: Context?, attrs: AttributeSet?) :
LinearLayout(context, attrs){
    val constraintLayout by lazy { findViewById<LinearLayout>(R.id.constraintLayout) }
    val linearLayout1 by lazy { findViewById<LinearLayout>(R.id.linearLayout1) }
    val linearLayout2 by lazy { findViewById<LinearLayout>(R.id.linearLayout2) }
    val linearLayout3 by lazy { findViewById<LinearLayout>(R.id.linearLayout3) }
    val cardViewOut by lazy { findViewById<CardView>(R.id.cardViewOut) }
    val mainChip by lazy { findViewById<TapChip>(R.id.mainChip) }
    val iconBank by lazy { findViewById<TapImageView>(R.id.iconBank) }
    val textViewTitle by lazy { findViewById<TapTextViewNew>(R.id.textViewTitle) }
    val textViewClickable by lazy { findViewById<TapTextViewNew>(R.id.textViewClickable) }
    val switchLoyalty by lazy { findViewById<TapSwitch>(R.id.switchLoyalty) }
    val textViewSubTitle by lazy { findViewById<TapTextViewNew>(R.id.textViewSubTitle) }
    val editTextAmount by lazy { findViewById<TextInputEditText>(R.id.editTextAmount) }
    val textViewTouchPoints by lazy { findViewById<TapTextViewNew>(R.id.textViewTouchPoints) }
    val textViewCurrency by lazy { findViewById<TapTextViewNew>(R.id.textViewCurrency) }
    val loyaltyAlertView by lazy { findViewById<TapAlertView>(R.id.loyalty_alertView) }
    val textViewRemainPoints by lazy { findViewById<TapTextViewNew>(R.id.textViewRemainPoints) }
    val textViewRemainAmount by lazy { findViewById<TapTextViewNew>(R.id.textViewRemainAmount) }


    private var loyaltyHeaderDataSource: LoyaltyHeaderDataSource? = null
    init {
        inflate(context, R.layout.tap_loyalty_view, this)
        applyTheme()


    }
    private fun applyTheme() {
        cardViewOut.setBackgroundColor(Color.parseColor(ThemeManager.getValue("loyaltyView.cardView.backgroundColor")))
        mainChip.setBackgroundColor(Color.parseColor(ThemeManager.getValue("loyaltyView.cardView.backgroundColor")))
        constraintLayout.setBackgroundColor(Color.parseColor(ThemeManager.getValue("loyaltyView.cardView.backgroundColor")))
       // cardViewOut.radius = ThemeManager.getValue("loyaltyView.cardView.radius")
       // mainChip.outlineSpotShadowColor = (Color.parseColor(ThemeManager.getValue("loyaltyView.cardView.shadowColor")))
        setBorderedView(
            cardViewOut,
            23f,// corner raduis
            0.0f,
            Color.parseColor(ThemeManager.getValue("loyaltyView.cardView.shadowColor")),// stroke color
            Color.parseColor(ThemeManager.getValue("loyaltyView.cardView.backgroundColor")),// tint color
            Color.parseColor(ThemeManager.getValue("loyaltyView.cardView.backgroundColor"))//shadow color
        )
        setLayoutTheme()

        textViewsThemeIng()

        switchTheme()


    }

    fun setLayoutTheme(){

        linearLayout1.setBackgroundColor(Color.parseColor(ThemeManager.getValue("loyaltyView.headerView.backgroundColor")))

        linearLayout2.setBackgroundColor(Color.parseColor(ThemeManager.getValue("loyaltyView.headerView.backgroundColor")))

        linearLayout3.setBackgroundColor(Color.parseColor(ThemeManager.getValue("loyaltyView.headerView.backgroundColor")))
    }




    private fun textViewsThemeIng() {
        textViewTitle.setTextColor(Color.parseColor(ThemeManager.getValue("loyaltyView.headerView.titleTextColor")))
        textViewTitle.textSize = ThemeManager.getFontSize("loyaltyView.headerView.titleFont").toFloat()

        textViewClickable.setTextColor(Color.parseColor(ThemeManager.getValue("loyaltyView.headerView.subTitleTextColor")))
        textViewClickable.textSize = ThemeManager.getFontSize("loyaltyView.headerView.subTitleFont").toFloat()

        editTextAmount.setTextColor(Color.parseColor(ThemeManager.getValue("loyaltyView.amountView.amountTextColor")))
        textViewCurrency.setTextColor(Color.parseColor(ThemeManager.getValue("loyaltyView.amountView.amountTextColor")))
        editTextAmount.textSize= ThemeManager.getFontSize("loyaltyView.amountView.amountFont").toFloat()

        textViewSubTitle.setTextColor(Color.parseColor(ThemeManager.getValue("loyaltyView.amountView.titleTextColor")))
        textViewSubTitle.textSize =ThemeManager.getFontSize("loyaltyView.amountView.titleFont").toFloat()

        textViewCurrency.setTextColor(Color.parseColor(ThemeManager.getValue("loyaltyView.amountView.currencyTextColor")))
        textViewCurrency.textSize =ThemeManager.getFontSize("loyaltyView.amountView.currencyFont").toFloat()

        textViewTouchPoints.setTextColor(Color.parseColor(ThemeManager.getValue("loyaltyView.amountView.pointsTextColor")))
        textViewTouchPoints.textSize =ThemeManager.getFontSize("loyaltyView.amountView.pointsFont").toFloat()

        textViewRemainPoints.setTextColor(Color.parseColor(ThemeManager.getValue("loyaltyView.footerView.pointsTextColor")))
        textViewRemainPoints.textSize =ThemeManager.getFontSize("loyaltyView.footerView.pointsFont").toFloat()

        textViewRemainAmount.setTextColor(Color.parseColor(ThemeManager.getValue("loyaltyView.footerView.amountTextColor")))
        textViewRemainAmount.textSize =ThemeManager.getFontSize("loyaltyView.footerView.amountFont").toFloat()
    }

    fun switchTheme() {
       if (switchLoyalty?.isChecked == true) {
         enableSwitchTheme()

       }else {
       disableSwitchTheme()
    }

    }

    private fun enableSwitchTheme(){
        val switchEnableTheme = SwitchTheme()
        switchEnableTheme.thumbTint =
            Color.parseColor(ThemeManager.getValue("TapSwitchView.main.backgroundColor"))
        switchEnableTheme.trackTint =
            Color.parseColor(ThemeManager.getValue("TapSwitchView.goPay.SwitchOnColor"))
        switchLoyalty.setTheme(switchEnableTheme)
    }

    private fun disableSwitchTheme(){
        val switchDisableTheme = SwitchTheme()
        switchDisableTheme.thumbTint =
            Color.parseColor(ThemeManager.getValue("TapSwitchView.main.backgroundColor"))
        switchDisableTheme.trackTint =
            Color.parseColor(ThemeManager.getValue("TapSwitchView.main.backgroundColor"))
        switchLoyalty.setTheme(switchDisableTheme)
    }

    fun setLinkClickable(){
        textViewClickable.movementMethod = LinkMovementMethod.getInstance()
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
                .into(iconBank)




    }




}