package company.tap.checkout.internal.viewholders


import android.content.Context
import android.graphics.Color
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.transition.Fade
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.core.widget.doAfterTextChanged
import company.tap.cardinputwidget.widget.CardInputListener
import company.tap.cardinputwidget.widget.inline.InlineCardInput
import company.tap.checkout.R
import company.tap.checkout.internal.dummygener.TapCardPhoneListDataSource
import company.tap.checkout.internal.enums.SectionType
import company.tap.checkout.internal.interfaces.onCardNFCCallListener
import company.tap.checkout.internal.interfaces.onPaymentCardComplete
import company.tap.tapcardvalidator_android.CardBrand
import company.tap.tapcardvalidator_android.CardValidationState
import company.tap.tapcardvalidator_android.CardValidator
import company.tap.tapcardvalidator_android.DefinedCardBrand
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapuilibrary.themekit.ThemeManager
import company.tap.tapuilibrary.uikit.interfaces.TapPaymentShowHideClearImage
import company.tap.tapuilibrary.uikit.interfaces.TapSelectionTabLayoutInterface
import company.tap.tapuilibrary.uikit.models.SectionTabItem
import company.tap.tapuilibrary.uikit.views.TapAlertView
import company.tap.tapuilibrary.uikit.views.TapMobilePaymentView
import company.tap.tapuilibrary.uikit.views.TapSelectionTabLayout
import kotlinx.android.synthetic.main.payment_inputt_layout.view.*
import kotlinx.android.synthetic.main.switch_layout.view.*


/**
 *
 * Copyright © 2020 Tap Payments. All rights reserved.
 *
 */
@RequiresApi(Build.VERSION_CODES.N)
class PaymenttInputViewHolder(
    context: Context,
    private val onPaymentCardComplete: onPaymentCardComplete,
    private val onCardNFCCallListener: onCardNFCCallListener
) : TapBaseViewHolder,
    TapSelectionTabLayoutInterface, CardInputListener, TapPaymentShowHideClearImage {
    override val view: View =
        LayoutInflater.from(context).inflate(R.layout.payment_inputt_layout, null)
    override val type = SectionType.PAYMENT_INPUT
    private var tabLayout: TapSelectionTabLayout
    private val paymentInputContainer: LinearLayout
    private val clearView: ImageView
    var selectedType = PaymentType.CARD
    private var shouldShowScannerOptions = true
    private var lastFocusField = CardInputListener.FocusField.FOCUS_CARD
    private var lastCardInput = ""
    private var cardScannerBtn: ImageView? = null
    private var nfcButton: ImageView? = null
    private var tapCardInputView: InlineCardInput
    private var tapMobileInputView: TapMobilePaymentView
    private var linearLayoutPay: LinearLayout? = null
    private var tabPosition: Int? = null
    private var tapAlertView: TapAlertView? = null
    private var switchViewHolder11 = SwitchViewHolder11(context)
    private var imageURL: String = ""
    private var isadded: Boolean = false
    private lateinit var paymentType: String
    private lateinit var cardBrandType: String


    init {
        tabLayout = view.findViewById(R.id.sections_tablayout)
        tabLayout.setTabLayoutInterface(this)
        tapMobileInputView = TapMobilePaymentView(context, null)
        tapMobileInputView.setTapPaymentShowHideClearImage(this)
        tapCardInputView = InlineCardInput(context, null)
        tapAlertView = view.findViewById(R.id.alertView)
        tabLayout.setTabLayoutInterface(this)
        paymentInputContainer = view.findViewById(R.id.payment_input_layout)
        clearView = view.findViewById(R.id.clear_text)
        tabLayout.setBackgroundColor(Color.parseColor(ThemeManager.getValue("inlineCard.commonAttributes.backgroundColor")))
        tabLayout.changeTabItemAlphaValue(0.9f)
        bindViewComponents()
    }


    override fun bindViewComponents() {
        initCardInput()
        initMobileInput()
        initClearText()
        initializeCardForm()
        tapMobileInputViewWatcher()
        /**
         * set separator background
         */
        view.separator?.setBackgroundColor(Color.parseColor(ThemeManager.getValue("horizontalList.backgroundColor")))
    }

    fun tapMobileInputViewWatcher() {
        tapMobileInputView.mobileNumber.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val card = CardValidator.validate(s.toString())
                if (s?.trim()?.length!! > 2) {
                    if (s.trim()[0].toInt() == 5) {
                        tabLayout.selectTab(CardBrand.ooredoo, true)
                    }
                }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
    }


    private fun initializeCardForm() {
        cardScannerBtn = view.findViewById(R.id.card_scanner_button)
        nfcButton = view.findViewById(R.id.nfc_button)
        nfcButton?.visibility = View.VISIBLE
        cardScannerBtn?.visibility = View.VISIBLE
        linearLayoutPay = view.findViewById(R.id.linear_paylayout)
        tapCardInputView.clearFocus()
        clearView.visibility = View.GONE
        clearView.setOnClickListener {
            tabLayout.resetBehaviour()
            println("is it called")
            tapMobileInputView.clearNumber()
            tapCardInputView.clear()
            onPaymentCardComplete.onPaycardSwitchAction(false)
            if (tabPosition == 1) {
                nfcButton?.visibility = View.INVISIBLE
                cardScannerBtn?.visibility = View.INVISIBLE
            }
            switchViewHolder11.view.cardviewSwitch.visibility = View.INVISIBLE
            switchViewHolder11.view.mainSwitch.visibility = View.GONE
            switchViewHolder11.view.cardSwitch.visibility = View.GONE
            clearView.visibility = View.GONE
        }

        nfcButton?.setOnClickListener {
            onCardNFCCallListener.onClickNFC()
        }
        cardScannerBtn?.setOnClickListener {
            tabLayout.visibility = View.GONE
            onCardNFCCallListener.onClickCardScanner()
        }
    }

    private fun initClearText() {
        clearView.setOnClickListener {
            when (selectedType) {
                PaymentType.CARD -> tapCardInputView.clear()
                PaymentType.MOBILE -> tapMobileInputView.clearNumber()
            }
        }
    }

    private fun initMobileInput() {
        tapMobileInputView.mobileNumber.doAfterTextChanged {
            it?.let {
                if (it.isEmpty()) clearView.visibility = View.GONE
                else clearView.visibility = View.VISIBLE
                //check if editable start with number of oridoo or zain etc
            }
        }
    }

    private fun initCardInput() {
        tapCardInputView.holderNameEnabled = false
        paymentInputContainer.addView(tapCardInputView)
        tapCardInputView.clearFocus()
        cardNumberWatcher()
        expiryDateWatcher()
        cvcNumberWatcher()
    }

    private fun cardNumberWatcher() {
        tapCardInputView.setCardNumberTextWatcher(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                cardNumAfterTextChangeListener(s)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().isEmpty()) {
                    clearView.visibility = View.GONE
                    tapAlertView?.visibility = View.GONE
                } else {
                    clearView.visibility = View.VISIBLE
                }
            }
        })
    }

    private fun expiryDateWatcher() {
        tapCardInputView.setExpiryDateTextWatcher(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) {
                    tabLayout.resetBehaviour()
                } else {
                    tapAlertView?.alertMessage?.text = (LocalizationManager.getValue(
                        "Warning",
                        "Hints",
                        "missingCVV"
                    ))
                    tapAlertView?.visibility = View.VISIBLE
                }
            }
        })
    }

    private fun cvcNumberWatcher() {
        tapCardInputView.setCvcNumberTextWatcher(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.trim()?.length == 3 || s?.trim()?.length == 4) {
                    onPaymentCardComplete.onPaycardSwitchAction(true)
                    tapAlertView?.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }


    fun cardNumAfterTextChangeListener(s: Editable?) {
        val card = CardValidator.validate(s.toString())
        s?.let {
            if (s.isNullOrEmpty()) {
                tabLayout.resetBehaviour()
                tapAlertView?.visibility = View.GONE
            }
            if (card.cardBrand != null) {
                Log.d("cardBrand", card.cardBrand.toString())
                Log.d("cardBrand", (card.validationState == CardValidationState.valid).toString())
                tabLayout.selectTab(card.cardBrand, true)
            }
            if (s.trim().length == 19 && (card.validationState == CardValidationState.invalid)) {
                tapAlertView?.visibility = View.VISIBLE
                tapAlertView?.alertMessage?.text =
                    (LocalizationManager.getValue("Warning", "Hints", "missingExpiryCVV"))
            }
            lastCardInput = it.toString()
            shouldShowScannerOptions = it.isEmpty()
            controlScannerOptions()
            cardBrandDetection(s.toString())
            checkValidationState(card)
        }
    }

    private fun checkValidationState(card: DefinedCardBrand) {
        when (card.validationState) {
            CardValidationState.invalid -> {
                tapAlertView?.visibility = View.VISIBLE
                tapAlertView?.alertMessage?.text =
                    (LocalizationManager.getValue("Error", "Hints", "wrongCardNumber"))
            }
            CardValidationState.incomplete -> {
                tapAlertView?.visibility = View.VISIBLE
                tapAlertView?.alertMessage?.text =
                    (LocalizationManager.getValue("Error", "Hints", "wrongCardNumber"))
            }
            CardValidationState.valid -> {
                tapAlertView?.visibility = View.GONE
            }
            else -> {
                tapAlertView?.visibility = View.VISIBLE
                tapAlertView?.alertMessage?.text =
                    (LocalizationManager.getValue("Warning", "Hints", "missingExpiryCVV"))
            }
        }
    }

    // Logic to show the switches when card details are valid
    private fun cardBrandDetection(cardTyped: String) {
        if (cardTyped.isEmpty()) {
            tabLayout.resetBehaviour()
            tapAlertView?.visibility = View.GONE
        }
        val card = CardValidator.validate(cardTyped)
        if (card.cardBrand != null) {
            println("card brand: ${card.validationState}")
            nfcButton?.visibility = View.GONE
            cardScannerBtn?.visibility = View.GONE
        }
    }

    private fun controlScannerOptions() {
        if (shouldShowScannerOptions) {
            nfcButton?.visibility = View.VISIBLE
            cardScannerBtn?.visibility = View.VISIBLE
        } else {

            nfcButton?.visibility = View.GONE
            cardScannerBtn?.visibility = View.GONE
        }
    }


    override fun onTabSelected(position: Int?) {
        tabPosition = position
        position?.let { swapInputViews(it) }
    }

    private fun swapInputViews(position: Int) {
        // TransitionManager.beginDelayedTransition(paymentLayoutContainer, Fade())
        TransitionManager.beginDelayedTransition(paymentInputContainer, Fade())
        paymentInputContainer.removeAllViews()
        if (position == 0) {
            selectedType = PaymentType.CARD
            switchViewHolder11.setSwitchLocals(selectedType)
            switchViewHolder11.view.cardSwitch.switchGoPayCheckout.visibility = View.VISIBLE
            nfcButton?.visibility = View.VISIBLE
            cardScannerBtn?.visibility = View.VISIBLE

            clearView.visibility = View.GONE
            paymentInputContainer.addView(tapCardInputView)
            checkForFocus()
        } else {
            selectedType = PaymentType.MOBILE
            switchViewHolder11.setSwitchLocals(selectedType)

            nfcButton?.visibility = View.GONE
            cardScannerBtn?.visibility = View.GONE
            if (tapMobileInputView.mobileNumber.text.isEmpty())
                clearView.visibility = View.INVISIBLE
            else
                clearView.visibility = View.VISIBLE

            paymentInputContainer.addView(tapMobileInputView)
        }
        tabPosition = position
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

    override fun onCardComplete() {
    }

    override fun onCvcComplete() {}

    override fun onExpirationComplete() {}

    override fun onFocusChange(focusField: String) {
        lastFocusField = focusField
    }

    /**
     * Sets data from API through LayoutManager
     * @param imageURLApi represents the images of payment methods.
     * */
    @RequiresApi(Build.VERSION_CODES.N)
    fun setDatafromAPI(imageURLApi: List<TapCardPhoneListDataSource>) {
        val itemsMobilesList = ArrayList<SectionTabItem>()
        val itemsCardsList = ArrayList<SectionTabItem>()

        println("iamage val  are" + imageURLApi)
        for (i in 0 until imageURLApi.size) {
            tabLayout.resetBehaviour()
            imageURL = imageURLApi[i].icon
            paymentType = imageURLApi[i].paymentType
            cardBrandType = imageURLApi[i].brand
            println("imageURL in loop" + imageURL)
            if (paymentType == "telecom") {
                itemsMobilesList.add(SectionTabItem(imageURL, imageURL, CardBrand.ooredoo))
            } else {
                itemsCardsList.add(SectionTabItem(imageURL, imageURL, CardBrand.visa))
            }
        }
        tabLayout.addSection(itemsCardsList)
        tabLayout.addSection(itemsMobilesList)

    }

    override fun showHideClearImage(show: Boolean) {
        if (show) {
            clearView.visibility = View.VISIBLE
        } else {
            clearView.visibility = View.VISIBLE
        }
    }
}



