package company.tap.checkout.internal.viewholders


import android.content.Context
import android.graphics.Color
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.transition.Fade
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.IntRange
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
import company.tap.tapuilibrary.themekit.ThemeManager
import company.tap.tapuilibrary.uikit.interfaces.TapPaymentShowHideClearImage
import company.tap.tapuilibrary.uikit.interfaces.TapSelectionTabLayoutInterface
import company.tap.tapuilibrary.uikit.models.SectionTabItem
import company.tap.tapuilibrary.uikit.views.TapMobilePaymentView
import company.tap.tapuilibrary.uikit.views.TapSelectionTabLayout
import kotlinx.android.synthetic.main.payment_inputt_layout.view.*
import kotlinx.android.synthetic.main.switch_layout.view.*


/**
 *
 * Created by Mario Gamal on 7/28/20
 * Modified by Ahlaam K
 * Copyright © 2020 Tap Payments. All rights reserved.
 *
 */
@RequiresApi(Build.VERSION_CODES.N)
class PaymenttInputViewHolder(
    private val context: Context,
    private val onPaymentCardComplete: onPaymentCardComplete,
    private val onCardNFCCallListener: onCardNFCCallListener
) : TapBaseViewHolder,
    TapSelectionTabLayoutInterface, CardInputListener,TapPaymentShowHideClearImage {

    override val view: View =
        LayoutInflater.from(context).inflate(R.layout.payment_inputt_layout, null)

    override val type = SectionType.PAYMENT_INPUT

    private var tabLayout: TapSelectionTabLayout
    private val paymentInputContainer: LinearLayout

    // private val paymentLayoutContainer: LinearLayout
    private val clearView: ImageView

    //  private val scannerOptions: LinearLayout
    var selectedType = PaymentType.CARD
    private var shouldShowScannerOptions = true
    private val cardInputWidget = InlineCardInput(context)
    private val mobilePaymentView = TapMobilePaymentView(context, null)
    private var lastFocusField = CardInputListener.FocusField.FOCUS_CARD
    private var lastCardInput = ""
    private var cardScannerBtn: ImageView? = null
    private var nfcButton: ImageView? = null
    private lateinit var tapCardInputView: InlineCardInput
    private lateinit var tapMobileInputView: TapMobilePaymentView
    private var linearLayoutPay: LinearLayout? = null
    private var tabPosition: Int? = null

    private var switchViewHolder11 = SwitchViewHolder11(context)
    private var imageURL: String=""
    private var isadded: Boolean = false
    private lateinit var paymentType: String
    private lateinit var cardBrandType: String


    init {

        tabLayout = view.findViewById(R.id.sections_tablayout)
        tabLayout.setTabLayoutInterface(this)
        tapMobileInputView = TapMobilePaymentView(context, null)
        tapCardInputView = InlineCardInput(context,null)
        tabLayout.setTabLayoutInterface(this)

        paymentInputContainer = view.findViewById(R.id.payment_input_layout)
        //  paymentLayoutContainer = view.findViewById(R.id.payment_layout_container)
        //    scannerOptions = view.findViewById(R.id.scanner_options)
        clearView = view.findViewById(R.id.clear_text)

        // switchViewHolder = SwitchViewHolder(context)
        bindViewComponents()
        tapMobileInputView.setTapPaymentShowHideClearImage(this)
        tabLayout.setBackgroundColor(Color.parseColor(ThemeManager.getValue("inlineCard.commonAttributes.backgroundColor")))
        tabLayout.changeTabItemAlphaValue(0.9f)

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
        initializeCardForm()
        /**
         * set separator background
         */
        view.separator?.setBackgroundColor(Color.parseColor(ThemeManager.getValue("horizontalList.backgroundColor")))

    }

    private fun initTabLayout() {
        // tabLayout.addSection(getCardList(imageURL))
        //   tabLayout.addSection(getCardList())
        //  tabLayout.addSection(getMobileList(imageURL))
        //  tabLayout.setTabLayoutInterface(this)
    }

    private fun initializeCardForm() {
        cardScannerBtn = view.findViewById(R.id.card_scanner_button)
        nfcButton = view.findViewById(R.id.nfc_button)
        //  mobileNumberEditText = view.findViewById(R.id.mobile_number)
//        alertMessage = view.findViewById(R.id.textview_alert_message)
        nfcButton?.visibility = View.VISIBLE
        cardScannerBtn?.visibility = View.VISIBLE
        linearLayoutPay = view.findViewById(R.id.linear_paylayout)
        cardInputWidget.clearFocus()
        clearView.visibility = View.GONE
        clearView.setOnClickListener {
            tabLayout.resetBehaviour()
            println("is it called")
            mobilePaymentView.clearNumber()
            /* tapCardInputView.setCardNumber("")
             tapCardInputView.setCvcCode("")*/
            cardInputWidget.clear()
            //  alert_text.visibility= View.GONE
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
                PaymentType.CARD -> cardInputWidget.clear()
                PaymentType.MOBILE -> mobilePaymentView.clearNumber()

            }
            println("is it selectedType")

        }
    }

    private fun initMobileInput() {
        mobilePaymentView.mobileNumber.doAfterTextChanged {
            it?.let {
                if (it.isEmpty())
                    clearView.visibility = View.GONE
                else
                    clearView.visibility = View.VISIBLE
            }
        }
    }

    //Setting on the cardInput with logics
    private fun initCardInput() {
        cardInputWidget.holderNameEnabled = false
        //paymentInputContainer.removeView(cardInputWidget)
        paymentInputContainer.addView(cardInputWidget)
        cardInputWidget.clearFocus()
        //Textwatcher for cardNumber
        cardInputWidget.setCardNumberTextWatcher(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    lastCardInput = it.toString()
                    shouldShowScannerOptions = it.isEmpty()
                    controlScannerOptions()
                    cardBrandDetection(s.toString())
                    if (s.trim().length == 19) {
                        // onPaymentCardComplete.onPaycardAction(true)
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().isEmpty())
                    clearView.visibility = View.GONE
                else
                    clearView.visibility = View.VISIBLE
            }

        })

        // Textwatcher for CVV
        cardInputWidget.setCvcNumberTextWatcher(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.trim()?.length == 3 || s?.trim()?.length == 4) {
                    onPaymentCardComplete.onPaycardSwitchAction(true)
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
        //Textwatcher for Expiry date
        cardInputWidget.setExpiryDateTextWatcher(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {

            }

        })


    }

    // Logic to show the switches when card details are valid
    private fun cardBrandDetection(cardTyped: String) {
        if (cardTyped.isEmpty())
            tabLayout.resetBehaviour()
        val card = CardValidator.validate(cardTyped.toString())
        if (card.cardBrand != null) {
            tabLayout.selectTab(
                card.cardBrand,
                card.validationState == CardValidationState.valid
            )
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
            paymentInputContainer.addView(cardInputWidget)
            checkForFocus()
        } else {
            selectedType = PaymentType.MOBILE
            switchViewHolder11.setSwitchLocals(selectedType)

            nfcButton?.visibility = View.GONE
            cardScannerBtn?.visibility = View.GONE
            if (mobilePaymentView.mobileNumber.text.isEmpty())
                clearView.visibility = View.INVISIBLE
            else
                clearView.visibility = View.VISIBLE

            paymentInputContainer.addView(mobilePaymentView)
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

                itemsMobilesList.add(SectionTabItem(imageURL,imageURL,CardBrand.masterCard))

                } else {
                    itemsCardsList.add(SectionTabItem(imageURL, imageURL,CardBrand.ooredoo))
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



