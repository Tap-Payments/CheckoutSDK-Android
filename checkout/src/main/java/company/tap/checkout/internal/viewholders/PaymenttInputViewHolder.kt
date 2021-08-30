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
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.tabs.TabLayout
import company.tap.cardinputwidget.CardBrandSingle
import company.tap.cardinputwidget.widget.CardInputListener
import company.tap.cardinputwidget.widget.inline.InlineCardInput
import company.tap.checkout.R
import company.tap.checkout.internal.api.enums.CardScheme
import company.tap.checkout.internal.api.enums.PaymentType
import company.tap.checkout.internal.api.models.BINLookupResponse
import company.tap.checkout.internal.api.models.CreateTokenCard
import company.tap.checkout.internal.api.models.PaymentOption
import company.tap.checkout.internal.apiresponse.CardViewEvent
import company.tap.checkout.internal.apiresponse.CardViewModel
import company.tap.checkout.internal.enums.PaymentTypeEnum
import company.tap.checkout.internal.enums.SectionType
import company.tap.checkout.internal.interfaces.BaseLayouttManager
import company.tap.checkout.internal.interfaces.PaymentCardComplete
import company.tap.checkout.internal.interfaces.onCardNFCCallListener
import company.tap.checkout.internal.utils.CustomUtils
import company.tap.checkout.internal.viewmodels.CheckoutViewModel
import company.tap.checkout.open.data_managers.PaymentDataSource
import company.tap.checkout.open.enums.CardType
import company.tap.nfcreader.open.utils.TapNfcUtils
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
import kotlinx.android.synthetic.main.switch_layout.view.*
import kotlin.collections.ArrayList


/**
 *
 * Copyright © 2020 Tap Payments. All rights reserved.
 *
 */
@RequiresApi(Build.VERSION_CODES.N)
class PaymenttInputViewHolder(
        private val context: Context,
        private val onPaymentCardComplete: PaymentCardComplete,
        private val onCardNFCCallListener: onCardNFCCallListener,
        private val switchViewHolder11: SwitchViewHolder11?,
        private val baseLayouttManager: BaseLayouttManager,
        private val cardViewModel: CardViewModel
) : TapBaseViewHolder,
    TapSelectionTabLayoutInterface, CardInputListener, TapPaymentShowHideClearImage {
    override val view: View =
            LayoutInflater.from(context).inflate(R.layout.payment_inputt_layout, null)
    override val type = SectionType.PAYMENT_INPUT
     var tabLayout: TapSelectionTabLayout = view.findViewById(R.id.sections_tablayout)
    private var intertabLayout: TabLayout = tabLayout.findViewById(R.id.tab_layout)
    private val paymentInputContainer: LinearLayout
    private val clearView: ImageView
    var selectedType = PaymentTypeEnum.card
    private var shouldShowScannerOptions = true
    private var lastFocusField = CardInputListener.FocusField.FOCUS_CARD
    private var lastCardInput = ""
    private var cardScannerBtn: ImageView? = null
    private var nfcButton: ImageView? = null
    var tapCardInputView: InlineCardInput
    internal var tapMobileInputView: TapMobilePaymentView
    private var linearLayoutPay: LinearLayout? = null
    private var tapSeparatorViewLinear: LinearLayout? = null
    private var tabPosition: Int? = null
    var tapAlertView: TapAlertView? = null
    private var imageURL: String = ""
    //  private  var paymentType: PaymentTypeEnum ?= null
    private var paymentType: PaymentType? = null
    private lateinit var cardBrandType: String
    private var cardNumber: String? = null
    private var expiryDate: String? = null
    private var cvvNumber: String? = null
    private var cardHolderName: String? = null
    private val BIN_NUMBER_LENGTH = 6
    private lateinit var cardSchema: String


    init {
        tabLayout.setTabLayoutInterface(this)
        tapMobileInputView = TapMobilePaymentView(context, null)
        tapMobileInputView.setTapPaymentShowHideClearImage(this)
        tapCardInputView = InlineCardInput(context, null)
        tapAlertView = view.findViewById(R.id.alertView)
        paymentInputContainer = view.findViewById(R.id.payment_input_layout)
        clearView = view.findViewById(R.id.clear_text)
        tapSeparatorViewLinear = view.findViewById(R.id.tapSeparatorViewLinear)
        tapSeparatorViewLinear?.setBackgroundColor(Color.parseColor(ThemeManager.getValue("horizontalList.backgroundColor")))
        tabLayout.setBackgroundColor(Color.parseColor(ThemeManager.getValue("inlineCard.commonAttributes.backgroundColor")))
//        tabLayout.changeTabItemMarginBottomValue(50)
//        tabLayout.changeTabItemMarginTopValue(50)
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
        //  view.separator?.setBackgroundColor(Color.parseColor(ThemeManager.getValue("horizontalList.backgroundColor")))
        //setSeparatorTheme()
    }

    private fun tapMobileInputViewWatcher() {
        tapMobileInputView.mobileNumber.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s?.trim()?.length!! > 2) {
                    if (s.trim()[0].toInt() == 5) {
                        println("is that called")
                        tabLayout.selectTab(CardBrand.ooredoo, true)
                    }
                }else tabLayout.resetBehaviour()

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
        cardScannerBtn?.visibility = View.VISIBLE
        linearLayoutPay = view.findViewById(R.id.linear_paylayout)
        clearView.visibility = View.GONE
        nfcButton?.setOnClickListener {
            onCardNFCCallListener.onClickNFC()
        }
        cardScannerBtn?.setOnClickListener {
            onCardNFCCallListener.onClickCardScanner()
        }
    }

    private fun initClearText() {
        clearView.setOnClickListener {
            clearCardInputAction()
        }
    }

    fun clearCardInputAction() {
        if (selectedType == PaymentTypeEnum.card) {
            tapCardInputView.clear()
            switchViewHolder11?.setSwitchLocals(PaymentTypeEnum.card)
        } else if (selectedType == PaymentTypeEnum.telecom) {
            tapMobileInputView.clearNumber()
            switchViewHolder11?.setSwitchLocals(PaymentTypeEnum.telecom)
        }
//        switchViewHolder11?.view?.cardSwitch?.switchesLayout?.visibility = View.VISIBLE
        switchViewHolder11?.view?.mainSwitch?.mainSwitchLinear?.visibility = View.VISIBLE
        tapAlertView?.visibility = View.GONE
        switchViewHolder11?.view?.cardSwitch?.payButton?.isActivated = false
//        switchViewHolder11?.view?.cardSwitch?.showOnlyPayButton()
        switchViewHolder11?.bindViewComponents()
        switchViewHolder11?.view?.cardSwitch?.payButton?.setButtonDataSource(
            false,
            context.let { LocalizationManager.getLocale(it).language },
            LocalizationManager.getValue("pay", "ActionButton"),
            Color.parseColor(ThemeManager.getValue("actionButton.Invalid.backgroundColor")),
            Color.parseColor(ThemeManager.getValue("actionButton.Invalid.titleLabelColor"))
        )
        tabLayout.resetBehaviour()
        if (PaymentDataSource.getBinLookupResponse() != null) {
            PaymentDataSource.setBinLookupResponse(null)
        }
    }

    private fun initMobileInput() {
        tapMobileInputView.mobileNumber.doAfterTextChanged {
            it?.let {
                println("is this called")
                if (it.isEmpty()) {
                    clearView.visibility = View.GONE
                } else {
                    clearView.visibility = View.VISIBLE
                }
                //check if editable start with number of oridoo or zain etc
                // onPaymentCardComplete.onPaycardSwitchAction(true, PaymentType.MOBILE)
                if (tapMobileInputView.mobileNumber.text.length > 7)
                    baseLayouttManager.displayOTPView(
                            PaymentDataSource.getCustomer().getPhone(),
                            PaymentTypeEnum.telecom.name
                    )

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
//                cardNumAfterTextChangeListener(s, this)
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                onCardTextChange(s)
                   cardNumAfterTextChangeListener(s,this)
            }
        })
    }

    private fun onCardTextChange(s: CharSequence?) {
        if (s.toString().isEmpty()) {
            clearView.visibility = View.GONE
            tapAlertView?.visibility = View.GONE
        } else {
            clearView.visibility = View.VISIBLE
        }
    }

    private fun expiryDateWatcher() {
        tapCardInputView.setExpiryDateTextWatcher(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
            override fun afterTextChanged(s: Editable?) {
                afterTextChangeAction(s)
            }
        })
    }

    private fun afterTextChangeAction(s: Editable?) {
        if (s.isNullOrEmpty()) {
            // tabLayout.resetBehaviour()
        } else {
            /**
             * we will get date value
             */
            expiryDate = s.toString()
            if (s.length >= 5) {
                tapAlertView?.alertMessage?.text = (LocalizationManager.getValue(
                    "Warning",
                    "Hints",
                    "missingCVV"
                ))
                tapAlertView?.visibility = View.VISIBLE

            }


        }
    }


    private fun cvcNumberWatcher() {
        tapCardInputView.setCvcNumberTextWatcher(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.trim()?.length == 3 || s?.trim()?.length == 4) {
                    onPaymentCardComplete.onPayCardSwitchAction(
                        true, PaymentType.CARD
                    )
                    tapAlertView?.visibility = View.GONE
                }else {
                    onPaymentCardComplete.onPayCardSwitchAction(
                        false, PaymentType.CARD
                    )
                    tapAlertView?.visibility = View.VISIBLE
                }
            }

            override fun afterTextChanged(s: Editable?) {
                /**
                 * we will get cvv number
                 */
                cvvNumber = s.toString()
                if (s?.trim()?.length == 3 || s?.trim()?.length == 4) {
                    cardNumber?.let {
                        expiryDate?.let { it1 ->
                            cvvNumber?.let { it2 ->
                                onPaymentCardComplete.onPayCardCompleteAction(
                                    true, PaymentType.CARD,
                                    it, it1, it2
                                )


                            }
                        }
                    }
                }
            }
        })
    }
    fun EditText.updateText(text: String) {
        val focussed = hasFocus()
        if (focussed) {
            clearFocus()
        }
        setText(text)
        if (focussed) {
            requestFocus()
        }
    }

    fun cardNumAfterTextChangeListener(charSequence: CharSequence?, textWatcher: TextWatcher) {
        val card = CardValidator.validate(charSequence.toString())

        if (charSequence != null) {
            if (charSequence.length > 2) callCardBinNumberApi(charSequence, textWatcher)
            else {
                tabLayout.resetBehaviour()
                PaymentDataSource.setBinLookupResponse(null)
            }
        }
        charSequence?.let {
            if (charSequence.isNullOrEmpty()) {
                tapAlertView?.visibility = View.GONE
            }
            if (card?.cardBrand != null) {
                tabLayout.selectTab(
                    card.cardBrand,
                    card.validationState == CardValidationState.valid
                )
                val binLookupResponse: BINLookupResponse? =
                    PaymentDataSource.getBinLookupResponse()
                if (PaymentDataSource.getCardType() != null && PaymentDataSource.getCardType() == CardType.ALL) {
                    setTabLayoutBasedOnApiResponse(binLookupResponse,card)
                } else {
                    checkAllowedCardTypes(binLookupResponse)
                    setTabLayoutBasedOnApiResponse(binLookupResponse,card)
                }
            }

            /**
             * we will get the full card number
             */
            cardNumber = charSequence.toString()
            lastCardInput = it.toString()
            shouldShowScannerOptions = it.isEmpty()
            controlScannerOptions()
            cardBrandDetection(charSequence.toString())
            if (card != null)  checkValidationState(card)
        }
    }

    private fun setTabLayoutBasedOnApiResponse(
        binLookupResponse: BINLookupResponse?,
        cardBrand: DefinedCardBrand
    ) {
        if (binLookupResponse?.cardBrand?.name == binLookupResponse?.scheme?.name) {
            // we will send card brand to validator
            binLookupResponse?.cardBrand?.let { it1 ->
                tabLayout.selectTab(
                    it1,
                     true
                )
            }
//            tabLayout.setUnselectedAlphaLevel(0.5f)
        } else {
            //we will send scheme
                schema = binLookupResponse?.scheme
            binLookupResponse?.scheme?.cardBrand?.let { it1 -> tabLayout.selectTab(it1, false) }
//            tabLayout.setUnselectedAlphaLevel(0.5f)

        }
       // PaymentDataSource.setBinLookupResponse(null)
    }

     var schema: CardScheme?=null

    /*
    This function for calling api to validate card number after 6 digit
     */
    private fun callCardBinNumberApi(s: CharSequence, textWatcher: TextWatcher) {

        if (s.trim().toString().replace(" ", "").length == BIN_NUMBER_LENGTH) {
            cardViewModel.processEvent(
                CardViewEvent.RetreiveBinLookupEvent,
                CheckoutViewModel(), null, s.trim().toString().replace(" ", ""), null, null
            )
            tapCardInputView.removeCardNumberTextWatcher(textWatcher)
            tapCardInputView.setCardNumberTextWatcher(textWatcher)
        }
    }


    private fun checkValidationState(card: DefinedCardBrand) {
        when (card.validationState) {
            CardValidationState.invalid -> {
                if(card.cardBrand!=null)
                tabLayout.selectTab(card.cardBrand, false)
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
                if(schema != null )
                    schema?.cardBrand?.let { tabLayout.selectTab(it, true) }
                else tabLayout.selectTab(card.cardBrand, true)

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
            tapAlertView?.visibility = View.GONE
        }
        val card = CardValidator.validate(cardTyped)
        // checkValidationState(card.cardBrand)
        if (card.cardBrand != null && ::cardSchema.isInitialized) {
            println("card brand: ${card.validationState}")
            nfcButton?.visibility = View.GONE
            cardScannerBtn?.visibility = View.GONE
        }
    }

    private fun controlScannerOptions() {
        if (shouldShowScannerOptions) {
            if (TapNfcUtils.isNfcAvailable(context)) {
                nfcButton?.visibility = View.VISIBLE
            } else nfcButton?.visibility = View.GONE

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
            println("call 1")
            swapInputViewsPosition0()
        } else {
            swapInputViewsPositionNot0()
        }
        tabPosition = position
    }

    private fun swapInputViewsPositionNot0() {
        selectedType = PaymentTypeEnum.telecom
        println("call 2")
        switchViewHolder11?.setSwitchLocals(PaymentTypeEnum.telecom)
        nfcButton?.visibility = View.GONE
        cardScannerBtn?.visibility = View.GONE
        if (tapMobileInputView.mobileNumber.text.isEmpty())
            clearView.visibility = View.INVISIBLE
        else
            clearView.visibility = View.VISIBLE

        paymentInputContainer.addView(tapMobileInputView)
    }

    private fun swapInputViewsPosition0() {
        selectedType = PaymentTypeEnum.card
        switchViewHolder11?.setSwitchLocals(PaymentTypeEnum.card)
        //It will be hidden until goPay is Logged in
        if (switchViewHolder11?.goPayisLoggedin == true) {
            switchViewHolder11.view.cardSwitch?.switchGoPayCheckout?.visibility = View.VISIBLE
        } else switchViewHolder11?.view?.cardSwitch?.switchGoPayCheckout?.visibility = View.GONE

        nfcButton?.visibility = View.VISIBLE
        cardScannerBtn?.visibility = View.VISIBLE
        clearView.visibility = View.GONE
        paymentInputContainer.addView(tapCardInputView)
        checkForFocus()
    }

    private fun checkForFocus() {
        shouldShowScannerOptions =
            lastFocusField == CardInputListener.FocusField.FOCUS_CARD
                    && lastCardInput.isEmpty()
        controlScannerOptions()
    }

    override fun onCardComplete() {}

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
    fun setDataFromAPI(imageURLApi: List<PaymentOption>) {
//        tabLayout.resetBehaviour()

        val itemsMobilesList = ArrayList<SectionTabItem>()
        val itemsCardsList = ArrayList<SectionTabItem>()
        intertabLayout.removeAllTabs()
        PaymentDataSource.setBinLookupResponse(null)
//        tabLayout.changeTabItemAlphaValue(1f)
        decideTapSelection(imageURLApi, itemsMobilesList, itemsCardsList)
        /**
         * if there is only one payment method we will set visibility gone for tablayout
         * and set the payment method icon for inline input card
         * and set visibility  for separator after chips gone
         */

        hideTabLayoutWhenOnlyOnePayment(itemsCardsList, itemsMobilesList)
        tabLayout.addSection(itemsCardsList)
        tabLayout.addSection(itemsMobilesList)
//        tabLayout.

        if(itemsMobilesList.size!=0) tabLayout.addSection(itemsMobilesList)

    }

    private fun hideTabLayoutWhenOnlyOnePayment(
        itemsCardsList: ArrayList<SectionTabItem>,
        itemsMobilesList: ArrayList<SectionTabItem>
    ) {
        if ((itemsCardsList.size == 1 &&  itemsMobilesList.size == 0) || (itemsCardsList.size == 0 &&  itemsMobilesList.size == 1) ) {
            tabLayout.visibility = View.GONE
            tapSeparatorViewLinear?.visibility = View.GONE
            tapCardInputView.setSingleCardInput(CardBrandSingle.fromCode(cardBrandType))
        }
//        else
//            tabLayout.changeTabItemAlphaValue(1f)
    }

    private fun decideTapSelection(
        imageURLApi: List<PaymentOption>,
        itemsMobilesList: ArrayList<SectionTabItem>,
        itemsCardsList: ArrayList<SectionTabItem>
    ) {
        for (i in imageURLApi.indices) {
            imageURL = imageURLApi[i].image.toString()
            paymentType = imageURLApi[i].paymentType
            cardBrandType = if(imageURLApi[i].brand?.name==null){
                "unknown"
            }else
                imageURLApi[i].brand?.name.toString()

    /// set payment option object for all payment types and send it to paymentcompletion action function and i will pass it to show extra fees
            if (paymentType == PaymentType.telecom) {
                itemsMobilesList.add(
                    SectionTabItem(
                        imageURL, imageURL, CardBrand.valueOf(
                            cardBrandType
                        )
                    )
                )
            } else if (paymentType?.name == PaymentType.CARD.name) {
                itemsCardsList.add(
                    SectionTabItem(
                        imageURL,
                        imageURL,
                        CardBrand.valueOf(cardBrandType)
                    )
                )
            }


            }
        }



    override fun showHideClearImage(show: Boolean) {
        if (show) {
            clearView.visibility = View.VISIBLE
        } else {
            clearView.visibility = View.GONE
            tapAlertView?.visibility = View.GONE
        }
    }

    fun getCard(): CreateTokenCard? {
        val number: String? = cardNumber
        val expiryDate: String? = expiryDate
        val cvc: String? = cvvNumber
        //temporrary    val cardholderName: String? = cardholderName
        val cardholderName: String = "cardholder"
        return if (number == null || expiryDate == null || cvc == null) {
            null
        } else {
            val dateParts: List<String> = expiryDate.split("/")
            return dateParts.get(0).let {
                CreateTokenCard(
                    number.replace(" ", ""),
                    it,
                    dateParts[1],
                    cvc,
                    cardholderName, null
                )
            }
        }
        // TODO: Add address handling here.
    }

    fun setCurrentBinData(binLookupResponse: BINLookupResponse?) {
      //  cardNumberWatcher()
        cardSchema = binLookupResponse?.scheme.toString()
    }

    fun checkAllowedCardTypes(binLookupResponse: BINLookupResponse?) {
        if (binLookupResponse != null && PaymentDataSource?.getCardType().toString() != null) {
            if (PaymentDataSource.getCardType().toString() != binLookupResponse.cardType) {
                CustomUtils.showDialog(
                    LocalizationManager.getValue(
                        "alertUnsupportedCardTitle",
                        "AlertBox"
                    ),
                    LocalizationManager.getValue("alertUnsupportedCardMessage", "AlertBox"),
                    context,
                    1,
                    baseLayouttManager,
                    null,
                    null,
                    true
                )

            }
        }

    }



}





