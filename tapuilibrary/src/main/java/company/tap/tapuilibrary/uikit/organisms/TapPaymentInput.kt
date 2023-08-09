package company.tap.tapuilibrary.uikit.organisms

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import company.tap.tapuilibrary.R
import company.tap.tapuilibrary.themekit.ThemeManager
import company.tap.tapuilibrary.themekit.theme.SeparatorViewTheme
import company.tap.tapuilibrary.themekit.theme.TabSelectTheme
import company.tap.tapuilibrary.uikit.atoms.TapSeparatorView
import company.tap.tapuilibrary.uikit.interfaces.TapPaymentShowHideClearImage
import company.tap.tapuilibrary.uikit.interfaces.TapView
import company.tap.tapuilibrary.uikit.models.TabSection
import company.tap.tapuilibrary.uikit.views.TapMobilePaymentView
import company.tap.tapuilibrary.uikit.views.TapSelectionTabLayout

/**
 *
 * Copyright © 2020 Tap Payments. All rights reserved.
 *
 */
class TapPaymentInput(context: Context?, attrs: AttributeSet?) :
    LinearLayout(context, attrs), TapPaymentShowHideClearImage,
    TapView<TabSelectTheme> {

    val tabLayout by lazy { findViewById<TapSelectionTabLayout>(R.id.sections_tablayout) }
    val paymentInputContainer by lazy { findViewById<LinearLayout>(R.id.payment_input_layout) }
    val mainLinear by lazy { findViewById<LinearLayout>(R.id.mainLinear) }

    val tabLinear by lazy { findViewById<RelativeLayout>(R.id.tabLinear) }
    val separator by lazy { findViewById<TapSeparatorView>(R.id.separator) }
    //var cardScannerButton :ImageView
   // var nfcButton :ImageView
    private  var tapMobileInputView: TapMobilePaymentView
    private var displayMetrics: Int? = null
    @DrawableRes
    val scannerIcon: Int =
        if (ThemeManager.currentTheme.isNotEmpty() && ThemeManager.currentTheme.contains("dark")) R.drawable.icon_scan_light else R.drawable.icon_scan
    @DrawableRes
    val nfcIcon: Int =
        if (ThemeManager.currentTheme.isNotEmpty() && ThemeManager.currentTheme.contains("dark")) R.drawable.icon_nfc_light else R.drawable.icon_nfc
    @DrawableRes
    val closeIcon: Int =
        if (ThemeManager.currentTheme.isNotEmpty() && ThemeManager.currentTheme.contains("dark")) R.drawable.icon_close_dark else R.drawable.icon_close2


    init {
        inflate(context, R.layout.tap_payment_input, this)
        applyTheme()
       /* clearView.setOnClickListener {
            rootView.invalidate()
        }*/
        tapMobileInputView = TapMobilePaymentView(context, null)
        tapMobileInputViewTextWatcher()
        tapMobileInputView.setTapPaymentShowHideClearImage(this)
      // cardScannerButton = findViewById(R.id.card_scanner_button)
       // nfcButton = findViewById(R.id.nfc_button)
      //  cardScannerButton.setImageResource(scannerIcon)
      //  clearView.setImageResource(closeIcon)
      //  nfcButton.setImageResource(nfcIcon)
       /* if (ThemeManager.currentTheme.isNotEmpty() && ThemeManager.currentTheme.contains("dark")) {
            cardInputChipView.setBackgroundResource(R.drawable.border_unclick_black)
        } else {
            cardInputChipView.setBackgroundResource(R.drawable.border_unclick)
        }*/
       /* setBorderedView(
            cardInputChipView,
            15.0f,// corner raduis
            0.0f,
            Color.parseColor(ThemeManager.getValue("inlineCard.commonAttributes.borderColor")),
            Color.parseColor(ThemeManager.getValue(" inlineCard.commonAttributes.borderColor")),
            Color.parseColor(ThemeManager.getValue("inlineCard.commonAttributes.borderColor"))
        )*/
    }
    fun setDisplayMetrics(displayMetrics: Int) {
        this.displayMetrics = displayMetrics
    }
    private fun tapMobileInputViewTextWatcher(){
        tapMobileInputView.mobileNumber?.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                showHideClearImage(true)
            }
            override fun afterTextChanged(mobileText: Editable) {
                if (mobileText.length > 2){
                    //clearView?.visibility = View.VISIBLE
                }else{
                   // clearView?.visibility = View.GONE
                }
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun addTabLayoutSection(vararg sections: TabSection) {
        sections.forEach {
            tabLayout.addSection(it.items)
        }
    }

    fun clearCardNumber() {
        paymentInputContainer.clearFocus()
    }

    override fun setTheme(theme: TabSelectTheme) {
        theme.backgroundColor?.let { setBackgroundColor(it) }
        theme.selectedBackgroundColor?.let { setBackgroundColor(it) }
        theme.unselectedBackgroundColor?.let { setBackgroundColor(it) }
    }

    private fun applyTheme() {
        tabLinear.setBackgroundColor(Color.parseColor(ThemeManager.getValue("inlineCard.commonAttributes.backgroundColor")))
        //clearView.setBackgroundColor(Color.parseColor(ThemeManager.getValue("inlineCard.commonAttributes.backgroundColor")))
        setSeparatorTheme()
    }

    private fun setSeparatorTheme() {
        val separatorViewTheme = SeparatorViewTheme()
        separatorViewTheme.strokeColor =
            Color.parseColor(ThemeManager.getValue("tapSeparationLine.backgroundColor"))
        separatorViewTheme.strokeHeight = ThemeManager.getValue("tapSeparationLine.height")
        separator.setTheme(separatorViewTheme)
        separator.setBackgroundColor(Color.parseColor(ThemeManager.getValue("tapSeparationLine.backgroundColor")))
    }

    override fun showHideClearImage(show: Boolean) {
        if (show) {
            //clearView.visibility = View.VISIBLE
        } else {
            //clearView.visibility = View.GONE
        }
    }

}