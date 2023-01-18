package company.tap.checkout.internal.viewholders


import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.transition.Fade
import android.transition.TransitionManager
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.TooltipCompat
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.tabs.TabLayout
import company.tap.cardinputwidget.Card
import company.tap.cardinputwidget.CardBrandSingle
import company.tap.cardinputwidget.CardInputUIStatus
import company.tap.cardinputwidget.widget.CardInputListener
import company.tap.cardinputwidget.widget.inline.InlineCardInput
import company.tap.checkout.R
import company.tap.checkout.internal.api.enums.CardScheme
import company.tap.checkout.internal.api.enums.PaymentType
import company.tap.checkout.internal.api.models.*
import company.tap.checkout.internal.apiresponse.CardViewEvent
import company.tap.checkout.internal.apiresponse.CardViewModel
import company.tap.checkout.internal.enums.PaymentTypeEnum
import company.tap.checkout.internal.enums.SectionType
import company.tap.checkout.internal.interfaces.BaseLayoutManager
import company.tap.checkout.internal.interfaces.PaymentCardComplete
import company.tap.checkout.internal.interfaces.onCardNFCCallListener
import company.tap.checkout.internal.utils.CustomUtils
import company.tap.checkout.internal.viewmodels.CheckoutViewModel
import company.tap.checkout.open.CheckoutFragment
import company.tap.checkout.open.data_managers.PaymentDataSource
import company.tap.checkout.open.enums.CardType
import company.tap.nfcreader.open.utils.TapNfcUtils
import company.tap.tapcardvalidator_android.CardBrand
import company.tap.tapcardvalidator_android.CardValidationState
import company.tap.tapcardvalidator_android.CardValidator
import company.tap.tapcardvalidator_android.DefinedCardBrand
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapuilibrary.themekit.ThemeManager
import company.tap.tapuilibrary.uikit.atoms.TapImageView
import company.tap.tapuilibrary.uikit.atoms.TapTextView
import company.tap.tapuilibrary.uikit.datasource.TapSwitchDataSource
import company.tap.tapuilibrary.uikit.interfaces.TapPaymentShowHideClearImage
import company.tap.tapuilibrary.uikit.interfaces.TapSelectionTabLayoutInterface
import company.tap.tapuilibrary.uikit.models.SectionTabItem
import company.tap.tapuilibrary.uikit.organisms.TapPaymentInput
import company.tap.tapuilibrary.uikit.views.*
import kotlinx.android.synthetic.main.loyalty_view_layout.view.*
import kotlinx.android.synthetic.main.switch_layout.view.*


/**
 *
 * Copyright © 2020 Tap Payments. All rights reserved.
 *
 */
@RequiresApi(Build.VERSION_CODES.N)
class PaymentInputViewHolder(
    private val context: Context,
    private val checkoutViewModel: CheckoutViewModel,
    private val onPaymentCardComplete: PaymentCardComplete,
    private val onCardNFCCallListener: onCardNFCCallListener,
    private val switchViewHolder: SwitchViewHolder?,
    private val baseLayoutManager: BaseLayoutManager,
    private val cardViewModel: CardViewModel,
    private val checkoutFragment: CheckoutFragment,
    private val loyaltyViewHolder: LoyaltyViewHolder?,
) : TapBaseViewHolder,
    TapSelectionTabLayoutInterface, CardInputListener, TapPaymentShowHideClearImage {
    override val view: View =
        LayoutInflater.from(context).inflate(R.layout.payment_inputt_layout, null)
    override val type = SectionType.PAYMENT_INPUT
    var tabLayout: TapSelectionTabLayout = view.findViewById(R.id.sections_tablayout)
    private var intertabLayout: TabLayout = tabLayout.findViewById(R.id.tab_layout)

    private val paymentInputContainer: LinearLayout

    // private val clearView: ImageView
    var selectedType = PaymentTypeEnum.card
    private var shouldShowScannerOptions = true
    private var lastFocusField = CardInputListener.FocusField.FOCUS_CARD
    private var lastCardInput = ""
    private var cardScannerBtn: ImageView? = null
    private var savedCardsModel: SavedCard? = null

    // private var nfcButton: ImageView? = null
    var tapCardInputView: InlineCardInput
    internal var tapMobileInputView: TapMobilePaymentView
    private var linearLayoutPay: LinearLayout? = null
    private var tapSeparatorViewLinear: LinearLayout? = null
    private var tabPosition: Int? = null
    var tapAlertView: TapAlertView? = null
    private var imageURL: String = ""
    private var selectedImageURL: String = ""

    //  private  var paymentType: PaymentTypeEnum ?= null
    private var paymentType: PaymentType? = null
    private lateinit var cardBrandType: String
    private var cardNumber: String? = null
    private var expiryDate: String? = null
    private var cvvNumber: String? = null
    private var cardHolderName: String? = null
    private val BIN_NUMBER_LENGTH = 6
    private lateinit var cardSchema: String
    var schema: CardScheme? = null
    var itemsCardsList = ArrayList<SectionTabItem>()
    var resetView: Boolean = false
    private var displayMetrics: Int? = 0
    private var height: Int = 0
    var constraintt: LinearLayout
    var acceptedCardText: TapTextView
    var tapInlineCardSwitch: TapInlineCardSwitch? = null
    var secondaryLayout: LinearLayout? = null
    var textViewPowered: TapTextView? = null
    var saveForOtherCheckBox: CheckBox? = null
    var toolstipImageView: TapImageView? = null
    var nfcButton: ImageView? = null
    var scannerButton: ImageView? = null
    var closeButton: ImageView? = null
    var cardInputUIStatus: CardInputUIStatus? = null
    var backArrow: TapImageView? = null
    var contactDetailsView: TapContactDetailsView? = null
    var shippingDetailView: TapShippingDetailView? = null
    var tapPaymentInput: TapPaymentInput? = null

    init {
        tabLayout.setTabLayoutInterface(this)
        tapMobileInputView = TapMobilePaymentView(context, null)
        tapMobileInputView.setTapPaymentShowHideClearImage(this)
        tapCardInputView = InlineCardInput(context, null)
        // tapAlertView = tapCardInputView.findViewById(R.id.alertView)
        nfcButton = tapCardInputView.findViewById(R.id.nfc_button)
        scannerButton = tapCardInputView.findViewById(R.id.card_scanner_button)
        closeButton = tapCardInputView.findViewById(R.id.clear_text)
        tapPaymentInput = view.findViewById(R.id.tap_payment_input)
        tapAlertView = tapPaymentInput?.findViewById(R.id.alertView)
        //cardSwitchLayout = tapCardInputView.findViewById(R.id.mainSwitchInline)
        paymentInputContainer = view.findViewById(R.id.payment_input_layout)
        // clearView = view.findViewById(R.id.clear_text)
        backArrow = tapCardInputView.findViewById(R.id.backView)
        contactDetailsView = view.findViewById(R.id.contactDetailsView)
        tapInlineCardSwitch = tapPaymentInput?.findViewById(R.id.switch_Inline_card)
        secondaryLayout = tapCardInputView.findViewById(R.id.secondary_Layout)
        textViewPowered = tapCardInputView.findViewById(R.id.textViewPowered)
        saveForOtherCheckBox = tapCardInputView.findViewById(R.id.saveForOtherCheckBox)
        toolstipImageView = tapCardInputView.findViewById(R.id.toolsTipImageView)
        initToolsTip()
        tapInlineCardSwitch?.setSwitchDataSource(
            TapSwitchDataSource(
                "dummy",
                LocalizationManager.getValue("cardSaveLabel", "TapCardInputKit"),
                "dummy",
                "dummy",
                "dummy"
            )
        )

        tapSeparatorViewLinear = view.findViewById(R.id.tapSeparatorViewLinear)
        tapSeparatorViewLinear?.setBackgroundColor(Color.parseColor(ThemeManager.getValue("horizontalList.backgroundColor")))
        // tabLayout.setBackgroundColor(Color.parseColor(ThemeManager.getValue("inlineCard.commonAttributes.backgroundColor")))
        tabLayout.changeTabItemMarginBottomValue(10)
        tabLayout.changeTabItemMarginTopValue(10)
        constraintt = view.findViewById(R.id.constraintt)
        constraintt.setBackgroundColor(Color.parseColor(ThemeManager.getValue("horizontalList.backgroundColor")))
        acceptedCardText = view.findViewById(R.id.acceptedCardText)
        acceptedCardText.text = LocalizationManager.getValue("weSupport", "TapCardInputKit")
        saveForOtherCheckBox?.text = LocalizationManager.getValue("cardSaveForTapLabel", "TapCardInputKit")
        bindViewComponents()

    }

    private fun initToolsTip() {
        val toolsTipText:String = LocalizationManager.getValue("cardSaveForTapInfo","TapCardInputKit")
        toolstipImageView?.setOnClickListener {
            TooltipCompat.setTooltipText(
                toolstipImageView!!,
                 toolsTipText
            )

        }
        toolstipImageView?.let {
            TooltipCompat.setTooltipText(
                it,
                toolsTipText
            )
        }

    }


    override fun bindViewComponents() {
        initCardInput()
        initMobileInput()
        initializeCardForm()
        initializeIcons()
        tapMobileInputViewWatcher()
        initializeCardBrandView()
        initCustomerDetailView()
        /**
         * set separator background
         */
        //  view.separator?.setBackgroundColor(Color.parseColor(ThemeManager.getValue("horizontalList.backgroundColor")))
        //setSeparatorTheme()
    }

    private fun initCustomerDetailView() {
        contactDetailsView?.cardDetailTitle?.text = LocalizationManager.getValue(
            "SaveCardHeader",
            "HorizontalHeaders",
            "contactDetailsSectionTitle"
        )
        shippingDetailView?.shippingDetailTitle?.text = LocalizationManager.getValue(
            "SaveCardHeader",
            "HorizontalHeaders",
            "shippingSectionTitle"
        )
        tapInlineCardSwitch?.switchSaveCard?.isChecked = false
        contactDetailsView?.contactEmailET?.setBackgroundColor(Color.parseColor(ThemeManager.getValue("customerDataCollection.backgroundColor")))
        contactDetailsView?.contactEmailET?.setBackgroundColor(Color.parseColor(ThemeManager.getValue("customerDataCollection.backgroundColor")))
    }

    private fun initializeIcons() {
        nfcButton?.setOnClickListener {
            checkoutViewModel.onClickNFC()
        }
        scannerButton?.setOnClickListener {
            checkoutViewModel.onClickCardScanner(true)
        }
        closeButton?.setOnClickListener {
            tapCardInputView.clear()
            clearCardInputAction()
            CustomUtils.hideKeyboardFrom(context, it)
            loyaltyViewHolder?.view?.loyaltyView?.constraintLayout?.visibility = View.GONE
            tabLayout.visibility = View.VISIBLE
            closeButton?.visibility = View.GONE

        }
    }

    private fun initializeCardBrandView() {
        if (displayMetrics == DisplayMetrics.DENSITY_260 || displayMetrics == DisplayMetrics.DENSITY_280 || displayMetrics == DisplayMetrics.DENSITY_300 || displayMetrics == DisplayMetrics.DENSITY_XHIGH || displayMetrics == DisplayMetrics.DENSITY_340 || displayMetrics == DisplayMetrics.DENSITY_360) {

            tabLayout.changeTabItemMarginBottomValue(15)
            tabLayout.changeTabItemMarginTopValue(15)
            tabLayout.changeTabItemMarginLeftValue(-15)

        } else {
            tabLayout.changeTabItemMarginBottomValue(20)
            tabLayout.changeTabItemMarginTopValue(30)
            tabLayout.changeTabItemMarginLeftValue(-30)
            //  tabLayout.changeTabItemMarginRightValue(10)
        }


    }

    fun resetView() {
        // clearView.visibility= View.VISIBLE
        CustomUtils.hideKeyboardFrom(context, view)
    }

    private fun tapMobileInputViewWatcher() {
        tapMobileInputView.mobileNumber.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s?.trim()?.length!! > 2) {
                    if (s.trim()[0].toInt() == 5) {
                        println("is that called")
                        tabLayout.selectTab(CardBrand.ooredoo, true)
                    }
                } else tabLayout.resetBehaviour()

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
        // clearView.visibility = View.INVISIBLE
        closeButton?.visibility = View.GONE
        nfcButton?.setOnClickListener {
            onCardNFCCallListener.onClickNFC()
        }
        cardScannerBtn?.setOnClickListener {
            onCardNFCCallListener.onClickCardScanner(true)
        }
        backArrow?.setOnClickListener {
            tabLayout.resetBehaviour()
            tapCardInputView.clear()
            baseLayoutManager.resetViewHolder()
            // clearView.visibility =View.GONE
            closeButton?.visibility = View.GONE
            tapAlertView?.visibility = View.GONE
           // nfcButton?.visibility = View.VISIBLE
            controlScannerOptions()
          //  cardScannerBtn?.visibility = View.VISIBLE
            tapInlineCardSwitch?.visibility = View.GONE
            tapCardInputView.isSavedCard = false

            tapCardInputView.updateIconCvc(
                false,
                "",
              company.tap.cardinputwidget.CardBrand.Unknown
            )

            tapCardInputView.setSingleCardInput(CardBrandSingle.Unknown, null
            )
        }

    }


    fun clearCardInputAction() {

        if (selectedType == PaymentTypeEnum.card) {
            tapCardInputView.clear()
            tapCardInputView.clearFocus()
            switchViewHolder?.setSwitchLocals(PaymentTypeEnum.card)
        } else if (selectedType == PaymentTypeEnum.telecom) {
            tapMobileInputView.clearNumber()
            switchViewHolder?.setSwitchLocals(PaymentTypeEnum.telecom)
        }
        tapCardInputView.setSingleCardInput(CardBrandSingle.Unknown, null
        )
//        switchViewHolder11?.view?.cardSwitch?.switchesLayout?.visibility = View.VISIBLE
        switchViewHolder?.view?.mainSwitch?.mainSwitchLinear?.visibility = View.VISIBLE
        tapAlertView?.visibility = View.GONE
        switchViewHolder?.view?.cardSwitch?.payButton?.isActivated = false
//        switchViewHolder11?.view?.cardSwitch?.showOnlyPayButton()
        switchViewHolder?.bindViewComponents()
        val payString: String = LocalizationManager.getValue("pay", "ActionButton")
        switchViewHolder?.view?.cardSwitch?.payButton?.setButtonDataSource(
            false,
            "en",
            payString + " " + checkoutViewModel.currentCurrencySymbol + " " + checkoutViewModel.currentAmount,
            Color.parseColor(ThemeManager.getValue("actionButton.Valid.paymentBackgroundColor")),
            null,
        )
        tabLayout.resetBehaviour()
        if (PaymentDataSource.getBinLookupResponse() != null) {
            PaymentDataSource.setBinLookupResponse(null)
        }
        controlScannerOptions()
        tapInlineCardSwitch?.saveForOtherCheckBox?.isChecked = true
        tapInlineCardSwitch?.switchSaveCard?.isChecked = true
        tapInlineCardSwitch?.visibility = View.GONE
        closeButton?.visibility = View.GONE
        tapCardInputView.setVisibilityOfHolderField(false)
    }

    private fun initMobileInput() {
        tapMobileInputView.mobileNumber.doAfterTextChanged {
            it?.let {
                println("is this called")
                if (it.isEmpty()) {
                    closeButton?.visibility = View.INVISIBLE
                } else {
                    closeButton?.visibility = View.VISIBLE
                }
                //check if editable start with number of oridoo or zain etc
                // onPaymentCardComplete.onPaycardSwitchAction(true, PaymentType.MOBILE)
                if (tapMobileInputView.mobileNumber.text.length > 7)
                    baseLayoutManager.displayOTPView(
                        PaymentDataSource.getCustomer().getPhone(),
                        PaymentTypeEnum.telecom.name
                    )

            }
        }
    }

    private fun initCardInput() {
        tapInlineCardSwitch?.visibility = View.GONE
        // tapCardInputView.holderNameEnabled = true
        //  PaymentDataSource.getEnableEditCardHolderName() != null && PaymentDataSource.getEnableEditCardHolderName()
        if (PaymentDataSource.getDefaultCardHolderName() != null) {
            tapCardInputView.setCardHolderName(PaymentDataSource.getDefaultCardHolderName())
        }
        height = Resources.getSystem().displayMetrics.heightPixels
        addViewsToPaymentViewContainer()
        tapCardInputView.clearFocus()
        cardNumberWatcher()
        expiryDateWatcher()
        cvcNumberWatcher()
        cardHolderNameWatcher()
        switchCheckedState()
    }

    private fun cardHolderNameWatcher() {
        tapCardInputView.setCardHolderHint(
            LocalizationManager.getValue(
                "cardNamePlaceHolder",
                "TapCardInputKit"
            )
        )
        tapCardInputView.setHolderNameTextWatcher(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
    }


    private fun switchCheckedState() {
       /* tapCardInputView.setSwitchSaveCardListener { buttonView, isChecked ->
            if (isChecked) {
                secondaryLayout?.visibility = View.VISIBLE
            } else secondaryLayout?.visibility = View.GONE


            tapCardInputView.switchCardEnabled = isChecked

        }*/

        tapInlineCardSwitch?.switchSaveCard?.setOnCheckedChangeListener {
                buttonView, isChecked ->
            secondaryLayout?.visibility = View.GONE
            intertabLayout?.visibility = View.GONE
            tapAlertView?.visibility = View.GONE
            tabLayout?.visibility = View.GONE
            intertabLayout?.visibility = View.GONE
            if(isChecked){
                tapInlineCardSwitch?.saveForOtherCheckBox?.isChecked = true
                tapInlineCardSwitch?.saveForOtherCheckBox?.visibility = View.VISIBLE
                tapInlineCardSwitch?.toolsTipImageView?.visibility = View.VISIBLE
            }else {
                tapInlineCardSwitch?.saveForOtherCheckBox?.isChecked = false
                tapInlineCardSwitch?.saveForOtherCheckBox?.visibility = View.GONE
             tapInlineCardSwitch?.toolsTipImageView?.visibility = View.GONE
            }


        }
        tapInlineCardSwitch?.saveForOtherCheckBox?.setOnCheckedChangeListener{ buttonView, isChecked ->
            secondaryLayout?.visibility = View.GONE
            tapAlertView?.visibility = View.GONE
            tabLayout?.visibility = View.GONE
            intertabLayout?.visibility = View.GONE
            tapInlineCardSwitch?.saveForOtherCheckBox?.isChecked = isChecked
            if(isChecked){
                if (cardInputUIStatus == CardInputUIStatus.SavedCard) {
                    contactDetailsView?.visibility = View.GONE
                }else {contactDetailsView?.visibility = View.VISIBLE

                tapInlineCardSwitch?.switchSaveCard?.isChecked = true
                tapInlineCardSwitch?.saveForOtherCheckBox?.visibility = View.VISIBLE
                }
            }else {
                contactDetailsView?.visibility = View.GONE
                tapInlineCardSwitch?.switchSaveCard?.isChecked = false
                tapInlineCardSwitch?.saveForOtherCheckBox?.visibility = View.GONE
            }

        }
    }

    private fun addViewsToPaymentViewContainer() {
        /*  val layoutParams = RelativeLayout.LayoutParams(
              RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT
          )
          displayMetrics = CustomUtils.getDeviceDisplayMetrics(context as Activity)
          println("displayMetrics>>")
         if(displayMetrics == DisplayMetrics.DENSITY_420||displayMetrics == DisplayMetrics.DENSITY_450 ||displayMetrics == DisplayMetrics.DENSITY_440||displayMetrics == DisplayMetrics.DENSITY_560){
              layoutParams.setMargins(0, -10, -115, 0) //for holder enabel

          }else if (displayMetrics == DisplayMetrics.DENSITY_300||displayMetrics == DisplayMetrics.DENSITY_XHIGH || displayMetrics == DisplayMetrics.DENSITY_340||displayMetrics == DisplayMetrics.DENSITY_360){
              layoutParams.setMargins(0, 0, -175, 0)
          }else if(displayMetrics == DisplayMetrics.DENSITY_XXHIGH ){
              layoutParams.setMargins(0, 0, -235, 0)
          }else if(displayMetrics == DisplayMetrics.DENSITY_400){
              if(TapNfcUtils.isNfcAvailable(context)){
                  layoutParams.setMargins(0, -10, -80, 0)

              }else layoutParams.setMargins(0, -10, 50, 0)
          }*/
        //   layoutParams.setMargins(0, -10, 50, 0)
        // paymentInputContainer.layoutParams = layoutParams
        paymentInputContainer.addView(tapCardInputView)

    }

    private fun cardNumberWatcher() {
        tapCardInputView.setCardHint(
            LocalizationManager.getValue(
                "cardNumberPlaceHolder",
                "TapCardInputKit"
            )
        )
        tapCardInputView.setCardNumberTextWatcher(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
//                cardNumAfterTextChangeListener(s, this)


            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (cardInputUIStatus != CardInputUIStatus.SavedCard) {
                    onCardTextChange(s)
                    cardNumAfterTextChangeListener(s, this)

                }
            }
        })
    }

    private fun onCardTextChange(s: CharSequence?) {
        if (s.toString().isEmpty()) {
            closeButton?.visibility = View.GONE
            tapAlertView?.visibility = View.GONE
        } else {
            closeButton?.visibility = View.VISIBLE
        }

    }

    private fun expiryDateWatcher() {
        tapCardInputView.setExpiryHint(
            LocalizationManager.getValue(
                "cardExpiryPlaceHolder",
                "TapCardInputKit"
            )
        )

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
        tapInlineCardSwitch?.visibility = View.VISIBLE
        if (s.isNullOrEmpty()) {
            // tabLayout.resetBehaviour()
        } else {
            /**
             * we will get date value
             */
            expiryDate = s.toString()
            if (s.length >= 5) {
                if(cardInputUIStatus?.equals(CardInputUIStatus.SavedCard) == true){
                    tapAlertView?.visibility = View.GONE
                }else{
                    tapAlertView?.visibility = View.VISIBLE
                }
                tapAlertView?.alertMessage?.text = (LocalizationManager.getValue(
                    "Warning",
                    "Hints",
                    "missingCVV"
                ))
               // tapAlertView?.visibility = View.VISIBLE
                lastFocusField = CardInputListener.FocusField.FOCUS_CVC
                // checkoutFragment.scrollView?.scrollTo(0,height)
                tapInlineCardSwitch?.switchSaveCard?.isChecked = true
            }


        }
    }


    private fun cvcNumberWatcher() {
        tapCardInputView.setCVVHint(
            LocalizationManager.getValue(
                "cardCVVPlaceHolder",
                "TapCardInputKit"
            )
        )
        tapCardInputView.setCvcNumberTextWatcher(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //   checkoutFragment.scrollView?.scrollTo(0,height)
                // tapCardInputView.requestFocus()


            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.trim()?.length == 3 || s?.trim()?.length == 4) {
                    onPaymentCardComplete.onPayCardSwitchAction(
                        true, PaymentType.CARD
                    )
                    tapAlertView?.visibility = View.GONE
                    CustomUtils.hideKeyboardFrom(context, view)
                } else {
                    onPaymentCardComplete.onPayCardSwitchAction(
                        false, PaymentType.CARD
                    )
                    //     tapAlertView?.visibility = View.VISIBLE
                }
            }

            override fun afterTextChanged(s: Editable?) {
                tapPaymentInput?.visibility = View.VISIBLE
                /**
                 * we will get cvv number
                 */
                cvvNumber = s.toString()
                if (s?.trim()?.length == 3 || s?.trim()?.length == 4) {
                    maskCardNumber(cardNumber.toString()).let {
                        expiryDate?.let { it1 ->
                            cvvNumber?.let { it2 ->
                                onPaymentCardComplete.onPayCardCompleteAction(
                                    true, PaymentType.CARD,
                                    it, it1, it2
                                )
                                tapInlineCardSwitch?.switchSaveCard?.isChecked = true

                            }
                        }
                    }
                }
                tapCardInputView.holderNameEnabled =
                    PaymentDataSource.getEnableEditCardHolderName() != null && PaymentDataSource.getEnableEditCardHolderName()
                //  tapCardInputView.holderNameEnabled= true
                if (tapCardInputView.holderNameEnabled) {
                    tapInlineCardSwitch?.setPaddingRelative(0, 100, 0, 0)

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
            baseLayoutManager.resetViewHolder()

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

                println("card brand value is>>>" + card.cardBrand)

                val binLookupResponse: BINLookupResponse? =
                    PaymentDataSource.getBinLookupResponse()
                // println("binLookupResponse" + binLookupResponse)
                if (charSequence.length > 4) checkIfCardTypeExistInList(card.cardBrand)
                if (PaymentDataSource.getCardType() != null && PaymentDataSource.getCardType() == CardType.ALL) {

                    setTabLayoutBasedOnApiResponse(binLookupResponse, card)
                } else {
                    checkAllowedCardTypes(binLookupResponse)
                    setTabLayoutBasedOnApiResponse(binLookupResponse, card)
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
            if (card != null) checkValidationState(card)
        }
        /* if(resetView){
             resetTouchView()
         }*/
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
            if (itemsCardsList.isNotEmpty()) {
                for (i in itemsCardsList.indices) {
                    //   println("binLookupResponse>>>"+binLookupResponse?.cardBrand?.name)
                    //    println("selectedImageURL>>>"+itemsCardsList[i].selectedImageURL)
                    if (binLookupResponse?.cardBrand?.name?.let {
                            itemsCardsList[i].selectedImageURL.contains(
                                it
                            )
                        } == true) {
                        selectedImageURL = itemsCardsList[i].selectedImageURL
                        tapCardInputView.setSingleCardInput(
                            CardBrandSingle.fromCode(
                                binLookupResponse?.cardBrand.toString()
                            ), selectedImageURL
                        )

                    }
                }
            }

//            tabLayout.setUnselectedAlphaLevel(0.5f)
        } else {
            //we will send scheme
            schema = binLookupResponse?.scheme


            binLookupResponse?.scheme?.cardBrand?.let { it1 ->
                tabLayout.selectTab(it1, false)
            }
            if (itemsCardsList.isNotEmpty()) {
                for (i in itemsCardsList.indices) {
                    // println("binLookupResponse/////"+binLookupResponse?.scheme?.cardBrand?.name)
                    // println("selectedImageURL////"+itemsCardsList[i].selectedImageURL)
                    if (binLookupResponse?.scheme?.cardBrand?.name?.toLowerCase()
                            ?.let { itemsCardsList[i].selectedImageURL?.contains(it) } == true
                    ) {
                        selectedImageURL = itemsCardsList[i].selectedImageURL
                        tapCardInputView.setSingleCardInput(
                            CardBrandSingle.fromCode(
                                binLookupResponse?.scheme?.cardBrand.toString()
                            ), selectedImageURL
                        )
                        return
                    }
                }
            }

//            tabLayout.setUnselectedAlphaLevel(0.5f)

        }
        tabLayout.visibility = View.GONE
        // PaymentDataSource.setBinLookupResponse(null)
    }


    /*
    This function for calling api to validate card number after 6 digit
     */
    private fun callCardBinNumberApi(s: CharSequence, textWatcher: TextWatcher) {

        if (s.trim().toString().replace(" ", "").length == BIN_NUMBER_LENGTH) {
            cardViewModel.processEvent(
                CardViewEvent.RetreiveBinLookupEvent,
                CheckoutViewModel(), null, null, s.trim().toString().replace(" ", ""), null, null
            )
            tapCardInputView.removeCardNumberTextWatcher(textWatcher)
            tapCardInputView.setCardNumberTextWatcher(textWatcher)
        }
    }


    private fun checkValidationState(card: DefinedCardBrand) {

        when (card.validationState) {
            CardValidationState.invalid -> {
                if (card.cardBrand != null)
                    tabLayout.selectTab(card.cardBrand, false)
                tapAlertView?.visibility = View.VISIBLE
                tapAlertView?.alertMessage?.text =
                    (LocalizationManager.getValue("Error", "Hints", "wrongCardNumber"))
                // checkoutFragment.scrollView?.smoothScrollTo(0,height)
            }
            CardValidationState.incomplete -> {
                //tapAlertView?.visibility = View.VISIBLE
               // tapAlertView?.alertMessage?.text =
                //    (LocalizationManager.getValue("Error", "Hints", "wrongCardNumber"))
                // checkoutFragment.scrollView?.smoothScrollTo(0,height)
                //checkoutFragment.scrollView?.smoothScrollTo(0,0)
            }
            CardValidationState.valid -> {
                if (schema != null)
                    schema?.cardBrand?.let { tabLayout.selectTab(it, true) }
                else tabLayout.selectTab(card.cardBrand, true)

                tapAlertView?.visibility = View.GONE
                // lastFocusField =CardInputListener.FocusField.FOCUS_EXPIRY
            }
            else -> {
                if(cardInputUIStatus?.equals(CardInputUIStatus.SavedCard) == true){
                    tapAlertView?.visibility = View.GONE
                }else{
                    tapAlertView?.visibility = View.VISIBLE
                }
               // tapAlertView?.visibility = View.VISIBLE
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
        if (card.cardBrand != null) {
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
        switchViewHolder?.setSwitchLocals(PaymentTypeEnum.telecom)
        nfcButton?.visibility = View.GONE
        cardScannerBtn?.visibility = View.GONE
        if (tapMobileInputView.mobileNumber.text.isEmpty())
            closeButton?.visibility = View.GONE
        else
            closeButton?.visibility = View.VISIBLE

        paymentInputContainer.addView(tapMobileInputView)
    }

    private fun swapInputViewsPosition0() {
        selectedType = PaymentTypeEnum.card
        switchViewHolder?.setSwitchLocals(PaymentTypeEnum.card)
        //It will be hidden until goPay is Logged in
        if (switchViewHolder?.goPayisLoggedin == true) {
            switchViewHolder.view.cardSwitch?.switchGoPayCheckout?.visibility = View.VISIBLE
        } else switchViewHolder?.view?.cardSwitch?.switchGoPayCheckout?.visibility = View.GONE

        controlScannerOptions()
        cardScannerBtn?.visibility = View.VISIBLE
        closeButton?.visibility = View.GONE
        paymentInputContainer.addView(tapCardInputView)
        checkForFocus()
    }

    private fun checkForFocus() {
        shouldShowScannerOptions =
            lastFocusField == CardInputListener.FocusField.FOCUS_CARD
                    && lastCardInput.isEmpty()
        controlScannerOptions()
    }

    override fun onCardComplete() {
        if (view.layoutDirection == View.LAYOUT_DIRECTION_RTL) {
            cardNumber = cardNumber?.reversed()
        }

    }

    override fun onCvcComplete() {}

    override fun onExpirationComplete() {
        if (cardInputUIStatus == CardInputUIStatus.SavedCard) {
            onFocusChange(CardInputListener.FocusField.FOCUS_CVC)
        }
    }

    override fun onFocusChange(focusField: String) {
        lastFocusField = focusField
        println("lastFocusField>>>>" + lastFocusField)
        /* if(resetView){
             tapCardInputView.onTouchView()
             resetView = false
         }*/

    }

    fun resetTouchView() {
        if (resetView) {
            if (!cardNumber.isNullOrEmpty() && !cardNumber.isNullOrBlank() && CardValidator.validate(
                    cardNumber
                ).validationState == CardValidationState.valid
            ) {
                tapCardInputView.onTouchView()
                resetView = false
            }

        }
    }

    /**
     * Sets data from API through LayoutManager
     * @param imageURLApi represents the images of payment methods.
     * */
    @RequiresApi(Build.VERSION_CODES.N)
    fun setDataFromAPI(imageURLApi: List<PaymentOption>) {
//        tabLayout.resetBehaviour()

        val itemsMobilesList = ArrayList<SectionTabItem>()
        itemsCardsList = ArrayList<SectionTabItem>()
        intertabLayout.removeAllTabs()
        //  intertabLayout.setPadding(-50, 0, 0, 0)
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
        // tabLayout.addSection(itemsMobilesList)
//        tabLayout.

        if (itemsMobilesList.size != 0) tabLayout.addSection(itemsMobilesList)

    }

    private fun hideTabLayoutWhenOnlyOnePayment(
        itemsCardsList: ArrayList<SectionTabItem>,
        itemsMobilesList: ArrayList<SectionTabItem>
    ) {
        if ((itemsCardsList.size == 1 && itemsMobilesList.size == 0) || (itemsCardsList.size == 0 && itemsMobilesList.size == 1)) {
            tabLayout.visibility = View.GONE
            tapSeparatorViewLinear?.visibility = View.GONE
            tapCardInputView.setSingleCardInput(CardBrandSingle.fromCode(cardBrandType), imageURL)
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
            cardBrandType = if (imageURLApi[i].brand?.name == null) {
                "unknown"
            } else
                imageURLApi[i].brand?.name.toString()

            /// set payment option object for all payment types and send it to paymentcompletion action function and i will pass it to show extra fees
            /*      if (paymentType == PaymentType.telecom) {
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
                  }*/
            // println("itemsCardsList are" + itemsCardsList)
            itemsCardsList.add(
                SectionTabItem(
                    imageURL,
                    imageURL,
                    CardBrand.valueOf(cardBrandType)
                )
            )
        }
    }


    override fun showHideClearImage(show: Boolean) {
        if (show) {
            closeButton?.visibility = View.VISIBLE
        } else {
            closeButton?.visibility = View.GONE
            tapAlertView?.visibility = View.GONE
        }
    }

    fun getCard(): CreateTokenCard? {
        val number: String? = cardNumber
        val expiryDate: String? = expiryDate
        val cvc: String? = cvvNumber
        //temporrary    val cardholderName: String? = cardholderName
        var cardholderName: String? = null
        if (PaymentDataSource.getDefaultCardHolderName() != null) {
            cardholderName = PaymentDataSource.getDefaultCardHolderName()
            tapCardInputView.setCardHolderName(cardholderName)
        } else {
            cardholderName = "cardholderName"
        }
        // val cardholderName: String = "cardholder"
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
                    baseLayoutManager,
                    null,
                    null,
                    true
                )

            }
        }

    }

    private fun checkIfCardTypeExistInList(cardBrand: CardBrand?) {
        val cardBrandArrayList = ArrayList<String>()

        for (i in 0 until itemsCardsList.size) {
            cardBrandArrayList.add(itemsCardsList[i].type.name)
        }
        // println("cardBrand is>>"+cardBrand?.name)
        // println("cardBrandArrayList is>>"+cardBrandArrayList)

        if (cardBrand != null && !cardBrandArrayList.contains(cardBrand.name)) {
            clearCardInputAction()
            tapCardInputView.clear()
            tabLayout.resetBehaviour()
            CustomUtils.showDialog(
                LocalizationManager.getValue(
                    "alertUnsupportedCardTitle",
                    "AlertBox"
                ),
                LocalizationManager.getValue("alertUnsupportedCardMessage", "AlertBox"),
                context,
                1,
                baseLayoutManager,
                null,
                null,
                true
            )

        }
    }


    private fun maskCardNumber(cardInput: String): String {
        val maskLen: Int = cardInput.length - 4
        if (maskLen <= 0) return cardInput // Nothing to mask
        return (cardInput).replaceRange(0, maskLen, "•••• ")
    }

    private fun maskCardNumber2(cardInput: String): String {
        val maskLen: Int = cardInput.length - 4
        if (maskLen <= 0) return cardInput // Nothing to mask
        return (cardInput).replaceRange(0, 6, "•••• ")
    }

    fun setDataForSavedCard(_savedCardsModel: SavedCard, cardInputUIStatus: CardInputUIStatus) {
        println("cardInputUIStatus>>" +cardInputUIStatus)
        this.cardInputUIStatus = cardInputUIStatus
        this.savedCardsModel = _savedCardsModel
        val cardModel = Card(
            maskCardNumber2(_savedCardsModel.firstSix + _savedCardsModel.lastFour),
            null,
            _savedCardsModel.expiry?.month?.toInt(),
            _savedCardsModel.expiry?.year?.toInt(),
            _savedCardsModel.cardholderName,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            _savedCardsModel.lastFour,
            company.tap.cardinputwidget.CardBrand.fromCardNumber(_savedCardsModel.firstSix),
            null,
            null,
            _savedCardsModel.currency,
            _savedCardsModel.customer,
            null,
            null
        )
        expiryDate = _savedCardsModel.expiry?.month + "/" + _savedCardsModel.expiry?.year
        println("expiryDate saved" + expiryDate)
        tapCardInputView.isSavedCard = true
        tapCardInputView.updateIconCvc(
            false,
            cvvNumber,
            company.tap.cardinputwidget.CardBrand.fromCardNumber(_savedCardsModel.firstSix)
        )
        tapCardInputView.setSavedCardDetails(cardModel, cardInputUIStatus)

        tapCardInputView.setSingleCardInput(
            CardBrandSingle.fromCode(
                company.tap.cardinputwidget.CardBrand.fromCardNumber(_savedCardsModel.firstSix)
                    .toString()
            ), _savedCardsModel.image
        )
        /* val bitmap = BitmapFactory.decodeStream(URL(savedCardsModel.image).content as InputStream)
         tapCardInputView.cvvIcon.setImageBitmap(bitmap)*/
        tapCardInputView.updateIconCvc(
            false,
            cvvNumber,
            company.tap.cardinputwidget.CardBrand.fromCardNumber(_savedCardsModel.firstSix)
        )
        // Glide.with(context).load(savedCardsModel.image).into( tapCardInputView.cvvIcon)
        tapCardInputView.onTouchView()
        tapInlineCardSwitch?.visibility = View.GONE
    }

    fun getSavedCardData(): CreateTokenSavedCard? {
        return if (savedCardsModel == null && PaymentDataSource.getCustomer().identifier != null) {
            null
        } else {
            CreateTokenSavedCard(
                savedCardsModel?.id,
                PaymentDataSource.getCustomer().identifier
            )


        }

    }

}













