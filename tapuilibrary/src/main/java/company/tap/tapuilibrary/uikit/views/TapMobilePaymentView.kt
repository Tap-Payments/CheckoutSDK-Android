package company.tap.tapuilibrary.uikit.views

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import com.hbb20.CountryCodePicker
import company.tap.tapuilibrary.R
import company.tap.tapuilibrary.fontskit.enums.TapFont
import company.tap.tapuilibrary.themekit.ThemeManager
import company.tap.tapuilibrary.themekit.theme.EditTextTheme
import company.tap.tapuilibrary.uikit.atoms.TapImageView
import company.tap.tapuilibrary.uikit.interfaces.TapPaymentShowHideClearImage
import company.tap.tapuilibrary.uikit.interfaces.TapView

/**
 *
 * Copyright © 2020 Tap Payments. All rights reserved.
 *
 */
class TapMobilePaymentView(context: Context?, attrs: AttributeSet?) :
    LinearLayout(context, attrs), CountryCodePicker.OnCountryChangeListener,
    TapView<EditTextTheme> {
    val mobileNumber by lazy { findViewById<EditText>(R.id.mobileNumber) }
    val mobileImage by lazy { findViewById<TapImageView>(R.id.mobileImage) }
    val mobilePaymentMainLinear by lazy { findViewById<LinearLayout>(R.id.mobilePaymentMainLinear) }
    val countryCodePicker by lazy { findViewById<CountryCodePicker>(R.id.countryCodePicker) }
    private var tapPaymentShowHideClearImage: TapPaymentShowHideClearImage? = null

    init {
        inflate(context, R.layout.tap_mobile_payment_view, this)
        mobileNumber.requestFocus()
        mobileNumber.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                tapPaymentShowHideClearImage?.showHideClearImage(true)
                countryCodePicker.contentColor =Color.parseColor(ThemeManager.getValue("phoneCard.textFields.textColor"))
            }
            override fun afterTextChanged(mobileText: Editable) {
                if (mobileText.length > 1){
                    tapPaymentShowHideClearImage?.showHideClearImage(true)
                    countryCodePicker.contentColor =Color.parseColor(ThemeManager.getValue("phoneCard.textFields.textColor"))

                }else{
                    tapPaymentShowHideClearImage?.showHideClearImage(false)
                    countryCodePicker.contentColor =Color.parseColor(ThemeManager.getValue("phoneCard.textFields.placeHolderColor"))
                }

            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }
        })

        initTheme()
        initCountryCodePicker()
    }



    private fun initCountryCodePicker() {
        countryCodePicker.setDefaultCountryUsingNameCode("KW")
        countryCodePicker.showArrow(false)
        countryCodePicker.ccpDialogShowFlag = false
        countryCodePicker.contentColor = Color.parseColor(ThemeManager.getValue("phoneCard.textFields.placeHolderColor"))
        countryCodePicker.textView_selectedCountry?.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.RobotoLight
            )
        )
        mobileImage.setOnClickListener {
            countryCodePicker.launchCountrySelectionDialog()
            countryCodePicker.visibility = View.VISIBLE
            countryCodePicker.setDefaultCountryUsingNameCode("KW")
        }
        countryCodePicker.setDialogBackgroundColor(Color.parseColor(ThemeManager.getValue("phoneCard.commonAttributes.backgroundColor")))

    }


    fun setTapPaymentShowHideClearImage(tapPaymentShowHideClearImage: TapPaymentShowHideClearImage) {
        this.tapPaymentShowHideClearImage = tapPaymentShowHideClearImage
    }

    fun clearNumber() {
        mobileNumber.text = null
    }

    private fun initTheme() {
        mobileImage.setBackgroundColor(Color.parseColor(ThemeManager.getValue("phoneCard.commonAttributes.backgroundColor")))
        mobilePaymentMainLinear.setBackgroundColor(Color.parseColor(ThemeManager.getValue("phoneCard.commonAttributes.backgroundColor")))
        mobileNumber.setHintTextColor(Color.parseColor(ThemeManager.getValue("phoneCard.textFields.placeHolderColor")))
        mobileNumber.textSize = ThemeManager.getFontSize("phoneCard.textFields.font").toFloat()
        mobileNumber.setTextColor(Color.parseColor(ThemeManager.getValue("phoneCard.textFields.textColor")))
        mobileNumber.setBackgroundResource(android.R.color.transparent)
        mobileNumber?.typeface = Typeface.createFromAsset(
            context?.assets, TapFont.tapFontType(
                TapFont.RobotoLight
            )
        )
    }

    override fun setTheme(theme: EditTextTheme) {
//            theme.backgroundTint?.let { backgroundTintList = ColorStateList.valueOf(it) }
        theme.backgroundTint?.let { mobileNumber.setBackgroundColor(it) }
        theme.textColorHint?.let { mobileNumber.setHintTextColor(it) }
        theme.letterSpacing?.let { mobileNumber.letterSpacing = it.toFloat() }
        theme.textSize?.let { mobileNumber.textSize = it.toFloat() }
    }

    override fun onCountrySelected() {
//        countryCodeText.text = countryCodePicker!!.selectedCountryCode
//        countryCode = countryCodePicker!!.selectedCountryCode
//        countryName = countryCodePicker!!.selectedCountryName

    }

}



