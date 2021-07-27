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
import com.google.android.material.tabs.TabLayout
import company.tap.cardinputwidget.CardBrandSingle
import company.tap.cardinputwidget.widget.CardInputListener
import company.tap.cardinputwidget.widget.inline.InlineCardInput
import company.tap.checkout.R
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
import company.tap.checkout.internal.viewmodels.TapLayoutViewModel
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
    TapSelectionTabLayoutInterface, CardInputListener, TapPaymentShowHideClearImage
     {
    override val view: View =
        LayoutInflater.from(context).inflate(R.layout.payment_inputt_layout, null)
    override val type = SectionType.PAYMENT_INPUT
    private var tabLayout: TapSelectionTabLayout = view.findViewById(R.id.sections_tablayout)
   private var intertabLayout:TabLayout = tabLayout.findViewById(R.id.tab_layout)
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
    private var tapAlertView: TapAlertView? = null
    private var imageURL: String = ""
  //  private  var paymentType: PaymentTypeEnum ?= null
    private  var paymentType: PaymentType ?= null
    private lateinit var cardBrandType: String
    private var cardNumber: String ?= null
    private var expiryDate: String ?= null
    private var cvvNumber: String ?= null
    private var cardHolderName: String ?= null
    private val BIN_NUMBER_LENGTH = 6
    private lateinit var cardSchema:String


    init {
        tabLayout.setTabLayoutInterface(this)
        tapMobileInputView = TapMobilePaymentView(context, null)
        tapMobileInputView.setTapPaymentShowHideClearImage(this)
        tapCardInputView = InlineCardInput(context, null)
        tapAlertView = view.findViewById(R.id.alertView)
        tabLayout.setTabLayoutInterface(this)
        paymentInputContainer = view.findViewById(R.id.payment_input_layout)
        clearView = view.findViewById(R.id.clear_text)
        tapSeparatorViewLinear = view.findViewById(R.id.tapSeparatorViewLinear)
        tapSeparatorViewLinear?.setBackgroundColor(Color.parseColor(ThemeManager.getValue("horizontalList.backgroundColor")))

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
      //  view.separator?.setBackgroundColor(Color.parseColor(ThemeManager.getValue("horizontalList.backgroundColor")))
        //setSeparatorTheme()
    }

    private fun tapMobileInputViewWatcher() {
        tapMobileInputView.mobileNumber.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val card = CardValidator.validate(s.toString())
                if (s?.trim()?.length!! > 2) {
                    if (s.trim()[0].toInt() == 5) {
                        println("is that called")
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
        nfcButton?.setOnClickListener {
            onCardNFCCallListener.onClickNFC()
        }
        cardScannerBtn?.setOnClickListener {
            onCardNFCCallListener.onClickCardScanner()
        }
    }

    private fun initClearText() {
        clearView.setOnClickListener {
            when (selectedType) {
                PaymentTypeEnum.card -> tapCardInputView.clear()
                PaymentTypeEnum.telecom -> tapMobileInputView.clearNumber()
            }
            switchViewHolder11?.view?.cardSwitch?.switchesLayout?.visibility = View.GONE
            switchViewHolder11?.view?.mainSwitch?.switchSaveMobile?.visibility = View.GONE
            tapAlertView?.visibility = View.GONE
            switchViewHolder11?.view?.cardSwitch?.payButton?.isActivated = false
            switchViewHolder11?.view?.cardSwitch?.payButton?.setButtonDataSource(
                    false,
                    context.let { LocalizationManager.getLocale(it).language },
                    LocalizationManager.getValue("pay", "ActionButton"),
                    Color.parseColor(ThemeManager.getValue("actionButton.Invalid.backgroundColor")),
                    Color.parseColor(ThemeManager.getValue("actionButton.Invalid.titleLabelColor"))
            )
        }
    }

    private fun initMobileInput() {
        tapMobileInputView.mobileNumber.doAfterTextChanged {
            it?.let {
                println("is this called")
                if (it.isEmpty()){
                    clearView.visibility = View.GONE
                }
                else {
                    clearView.visibility = View.VISIBLE
                }
                //check if editable start with number of oridoo or zain etc
               // onPaymentCardComplete.onPaycardSwitchAction(true, PaymentType.MOBILE)
                if(tapMobileInputView.mobileNumber.text.length>7)
                baseLayouttManager.displayOTPView(
                        tapMobileInputView.mobileNumber.text.toString(),
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
                    // tabLayout.resetBehaviour()
                } else {
                    /**
                     * we will get date value
                     */
                    expiryDate = s.toString()
                    println("expiryDate is" + expiryDate)
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
                    onPaymentCardComplete.onPayCardSwitchAction(
                            true, PaymentType.CARD
                    )
                    tapAlertView?.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable?) {
                /**
                 * we will get cvv number
                 */
                cvvNumber = s.toString()
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
        })
    }


    fun cardNumAfterTextChangeListener(s: Editable?) {
        val card = CardValidator.validate(s.toString())
        s?.let {
            if (s.isNullOrEmpty()) {
               // tabLayout.resetBehaviour()
                tapAlertView?.visibility = View.GONE
            }

            if (card.cardBrand != null ) {

                tabLayout.selectTab(
                        card.cardBrand,
                        card.validationState == CardValidationState.valid
                )
                }
                println("s.trim().toString()" + s.trim().toString())

                if(s.trim().toString().replace(" ", "").length == BIN_NUMBER_LENGTH) {
                    cardViewModel.processEvent(
                            CardViewEvent.RetreiveBinLookupEvent,
                            TapLayoutViewModel(), null, s.trim().toString().replace(" ", ""), null, null
                    )

                }
                /**
                 * we will get the full card number
                 */
                cardNumber = s.toString()

            if(card.cardBrand!=null)
                Log.e("cardBrand????", card.cardBrand.toString())
                Log.e("cardBrand????", card.cardBrand.toString())
                Log.e("cardBrand", (card.validationState == CardValidationState.valid).toString())

                lastCardInput = it.toString()
                shouldShowScannerOptions = it.isEmpty()
                controlScannerOptions()
                cardBrandDetection(s.toString())
                checkValidationState(card)
            }
        }
    //}

         private fun comparecardbrandwithcardscheme(cardBrand: CardBrand, cardSchema: String): Boolean {
             println("cardBrand comparator >>" + cardBrand.name + "cardSchema>>>>>>" + cardSchema)
             return if (!cardBrand.name.equals(cardSchema, false)) {
                 return true
             } else false

           /*  return if (cardBrand.toString().compareTo(cardSchema) === 0) {
                 return true
             }*/

         }

         private fun checkValidationState(card: DefinedCardBrand) {
//             println("card check>>" + card.cardBrand.name)
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
        if (cardTyped.toString().isEmpty()) {
            //tabLayout.resetBehaviour()
            tapAlertView?.visibility = View.GONE
        }
        println("cardTyped brand is: ${cardTyped}")
        val card = CardValidator.validate(cardTyped.toString())

        println("card brand on detect is: ${card.cardBrand}")
       // checkValidationState(card.cardBrand)
        if (card.cardBrand != null&& ::cardSchema.isInitialized) {
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
            println("call 1")
            selectedType = PaymentTypeEnum.card
            switchViewHolder11?.setSwitchLocals(PaymentTypeEnum.card)
            switchViewHolder11?.view?.cardSwitch?.switchGoPayCheckout?.visibility = View.VISIBLE
            nfcButton?.visibility = View.VISIBLE
            cardScannerBtn?.visibility = View.VISIBLE
            clearView.visibility = View.GONE
            paymentInputContainer.addView(tapCardInputView)
            checkForFocus()
        } else {
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
        tabPosition = position
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
    fun setDatafromAPI(imageURLApi: List<PaymentOption>) {
        val itemsMobilesList = ArrayList<SectionTabItem>()
        val itemsCardsList = ArrayList<SectionTabItem>()
        intertabLayout.removeAllTabs()
        tabLayout.changeTabItemAlphaValue(1f)
        tabLayout.setUnselectedAlphaLevel(1f)
        //tabLayout.resetBehaviour()

        println("iamage val  are" + imageURLApi.size)
        for (i in imageURLApi.indices) {
            //tabLayout.resetBehaviour()
            imageURL = imageURLApi[i].image.toString()
            paymentType = imageURLApi[i].paymentType
            cardBrandType = imageURLApi[i].brand?.name.toString()
            println("paymentType" + paymentType)

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
        /**
         * if there is only one payment method we will set visibility gone for tablayout
         * and set the payment method icon for inline input card
         * and set visibility  for separator after chips gone
         */
        if(itemsCardsList.size==1 || itemsMobilesList.size==1){
        tabLayout.visibility = View.GONE
        tapSeparatorViewLinear?.visibility = View.GONE
         println("CardBrandSingle" + CardBrandSingle.fromCode(cardBrandType))
        tapCardInputView.setSingleCardInput(CardBrandSingle.fromCode(cardBrandType))

        }else
            tabLayout.changeTabItemAlphaValue(0.9f)
            tabLayout.addSection(itemsCardsList)
            tabLayout.changeTabItemMarginBottomValue(35)
            tabLayout.changeTabItemMarginTopValue(35)
            tabLayout.changeTabItemAlphaValue(0.9f)
            tabLayout.addSection(itemsMobilesList)

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
             val cardholderName: String? = "cardholder"
             return if (number == null || expiryDate == null|| cvc == null || cardholderName ==null) {
                 null
             } else {
                 val dateParts: List<String>? = expiryDate?.split("/")
             return dateParts?.get(0)?.let {
                 CreateTokenCard(
                         number.replace(" ", ""),
                         it,
                         dateParts[1],
                         cvc,
                         cardholderName, null)
             }
             }
             // TODO: Add address handling here.
         }
        fun setCurrentBinData(binLookupResponse: BINLookupResponse){
            cardSchema = binLookupResponse?.scheme.toString()
            println("cardSchema values " + cardSchema)
          //  cardViewModel.setPaymentOption(binLookupResponse?.cardBrand, if (binLookupResponse == null) null else binLookupResponse.scheme)
        }

     }



