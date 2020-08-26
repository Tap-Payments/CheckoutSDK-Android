package company.tap.checkout.viewholders

import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.transition.Fade
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.IntRange
import androidx.core.widget.doAfterTextChanged
import company.tap.cardinputwidget.widget.CardInputListener
import company.tap.cardinputwidget.widget.inline.InlineCardInput
import company.tap.checkout.R
import company.tap.checkout.enums.SectionType
import company.tap.checkout.viewholders.PaymentInputViewHolder.PaymentType.CARD
import company.tap.checkout.viewholders.PaymentInputViewHolder.PaymentType.MOBILE
import company.tap.tapcardvalidator_android.CardBrand
import company.tap.tapuilibrary.interfaces.TapSelectionTabLayoutInterface
import company.tap.tapuilibrary.models.SectionTabItem
import company.tap.tapuilibrary.views.TapMobilePaymentView
import company.tap.tapuilibrary.views.TapSelectionTabLayout
import company.tap.thememanager.manager.ThemeManager
import company.tap.thememanager.theme.ChipTheme
import kotlinx.android.synthetic.main.item_knet.view.*
import kotlinx.android.synthetic.main.item_knet.view.tapcard_Chip
import kotlinx.android.synthetic.main.item_saved_card.view.*

/**
 *
 * Created by Mario Gamal on 7/28/20
 * Copyright © 2020 Tap Payments. All rights reserved.
 *
 */
class PaymentInputViewHolder(private val context: Context) : TapBaseViewHolder,
    TapSelectionTabLayoutInterface, CardInputListener {

    override val view: View =
        LayoutInflater.from(context).inflate(R.layout.payment_input_layout, null)

    override val type = SectionType.PAYMENT_INPUT

    private val tabLayout: TapSelectionTabLayout
    private val paymentInputContainer: LinearLayout
    private val paymentLayoutContainer: LinearLayout
    private val clearText: ImageView
    private val scannerOptions: LinearLayout
    private var selectedType = CARD
    private var shouldShowScannerOptions = true
    private val cardInputWidget = InlineCardInput(context)
    private val mobilePaymentView = TapMobilePaymentView(context, null)
    private var lastFocusField = CardInputListener.FocusField.FOCUS_CARD
    private var lastCardInput = ""

    init {
        tabLayout = view.findViewById(R.id.sections_tablayout)
        paymentInputContainer = view.findViewById(R.id.payment_input_layout)
        paymentLayoutContainer = view.findViewById(R.id.payment_layout_container)
        scannerOptions = view.findViewById(R.id.scanner_options)
        clearText = view.findViewById(R.id.clear_text)
        bindViewComponents()
      //  setThemeInlineCardInput()//TODO
        setThemeMobilePaymentView()
    }

    fun setCardNumber(cardNumber: String?) {
        cardInputWidget.setCardNumber(cardNumber)
    }

    fun setExpDate(
        @IntRange(from = 1, to = 12) month: Int,
        @IntRange(from = 0, to = 9999) year: Int
    ) {
        cardInputWidget.setExpiryDate(month, year)
    }

    fun setCvc(cvcCode: String?) {
        cardInputWidget.setCvcCode(cvcCode)
    }

    override fun bindViewComponents() {
        initTabLayout()
        initCardInput()
        initMobileInput()
        initClearText()
    }

    private fun initTabLayout() {
        tabLayout.addSection(getCardList())
        tabLayout.addSection(getMobileList())
        tabLayout.setTabLayoutInterface(this)
    }

    private fun initClearText() {
        clearText.setOnClickListener {
            when (selectedType) {
                CARD -> cardInputWidget.clear()
                MOBILE -> mobilePaymentView.clearNumber()
            }
        }
    }

    private fun initMobileInput() {
        mobilePaymentView.mobileInputEditText.doAfterTextChanged {
            it?.let {
                if (it.isEmpty())
                    clearText.visibility = View.GONE
                else
                    clearText.visibility = View.VISIBLE
            }
        }
    }

    private fun initCardInput() {
        cardInputWidget.holderNameEnabled = false
        paymentInputContainer.addView(cardInputWidget)
        cardInputWidget.setCardNumberTextWatcher(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    lastCardInput = it.toString()
                    shouldShowScannerOptions = it.isEmpty()
                    controlScannerOptions()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun controlScannerOptions() {
        if (shouldShowScannerOptions)
            scannerOptions.visibility = View.VISIBLE
        else
            scannerOptions.visibility = View.GONE
    }

    private fun getCardList(): ArrayList<SectionTabItem> {
        val items = ArrayList<SectionTabItem>()
        items.add(
            SectionTabItem(
                context.resources.getDrawable(
                    R.drawable.ic_visa
                ), context.resources.getDrawable(R.drawable.ic_visa_black), CardBrand.visa
            )
        )
        items.add(
            SectionTabItem(
                context.resources.getDrawable(
                    R.drawable.mastercard
                ), context.resources.getDrawable(R.drawable.mastercard_gray), CardBrand.masterCard
            )
        )
        items.add(
            SectionTabItem(
                context.resources.getDrawable(
                    R.drawable.amex
                ), context.resources.getDrawable(R.drawable.amex_gray), CardBrand.americanExpress
            )
        )
        return items
    }

    private fun getMobileList(): ArrayList<SectionTabItem> {
        val items = ArrayList<SectionTabItem>()
        items.add(
            SectionTabItem(
                context.resources.getDrawable(
                    R.drawable.zain_gray
                ), context.resources.getDrawable(R.drawable.zain_dark), CardBrand.zain
            )
        )
        items.add(
            SectionTabItem(
                context.resources.getDrawable(
                    R.drawable.ooredoo
                ), context.resources.getDrawable(R.drawable.ooredoo_gray), CardBrand.ooredoo
            )
        )
        return items
    }

    override fun onTabSelected(position: Int?) {
        position?.let { swapInputViews(it) }
    }

    private fun swapInputViews(position: Int) {
        TransitionManager.beginDelayedTransition(paymentLayoutContainer, Fade())
        paymentInputContainer.removeAllViews()
        if (position == 0) {
            selectedType = CARD
            scannerOptions.visibility = View.VISIBLE
            clearText.visibility = View.VISIBLE
            paymentInputContainer.addView(cardInputWidget)
            checkForFocus()
        } else {
            selectedType = MOBILE
            scannerOptions.visibility = View.GONE
            if (mobilePaymentView.mobileInputEditText.text.isEmpty())
                clearText.visibility = View.INVISIBLE
            else
                clearText.visibility = View.VISIBLE

            paymentInputContainer.addView(mobilePaymentView)
        }
    }

    private fun checkForFocus() {
        shouldShowScannerOptions =
            lastFocusField == CardInputListener.FocusField.FOCUS_CARD
                    && lastCardInput.isEmpty()
        controlScannerOptions()
    }

    enum class PaymentType {
        CARD,
        MOBILE
    }

    override fun onCardComplete() {}

    override fun onCvcComplete() {}

    override fun onExpirationComplete() {}

    override fun onFocusChange(focusField: String) {
        lastFocusField = focusField
    }


    fun setThemeInlineCardInput(){
        var chipTheme = ChipTheme()
//        chipTheme.outlineSpotShadowColor = ThemeManager.getValue("inlineCard.commonAttributes.")
        chipTheme.cardCornerRadius = ThemeManager.getValue("inlineCard.commonAttributes.cornerRadius")
        chipTheme.textColor = Color.parseColor(ThemeManager.getValue("inlineCard.textFields.textColor"))
        chipTheme.textSize = ThemeManager.getFontSize("inlineCard.textFields.font")
        cardInputWidget.tapcard_Chip.setTheme(chipTheme)
    }
    fun setThemeMobilePaymentView(){
        var chipTheme = ChipTheme()
//        chipTheme
        // Mobile payment view have edit text and image
//        mobilePaymentView.tapcard_Chip.view.tapcard_Chip.setTheme(chipTheme)
    }
}

