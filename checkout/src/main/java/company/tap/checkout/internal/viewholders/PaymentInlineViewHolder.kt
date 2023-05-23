package company.tap.checkout.internal.viewholders

import android.annotation.SuppressLint
import android.app.Activity
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
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.widget.doAfterTextChanged
import com.bugfender.sdk.Bugfender
import com.google.android.material.tabs.TabLayout
import company.tap.cardinputwidget2.CardBrandSingle
import company.tap.cardinputwidget2.CardInputUIStatus
import company.tap.cardinputwidget2.views.CardBrandView
import company.tap.cardinputwidget2.widget.CardInputListener
import company.tap.cardinputwidget2.widget.inline.InlineCardInput2
import company.tap.cardscanner.TapCard
import company.tap.checkout.R
import company.tap.checkout.internal.api.enums.CardScheme
import company.tap.checkout.internal.api.enums.LogsModel
import company.tap.checkout.internal.api.enums.PaymentType
import company.tap.checkout.internal.api.models.*
import company.tap.checkout.internal.apiresponse.CardViewEvent
import company.tap.checkout.internal.apiresponse.CardViewModel
import company.tap.checkout.internal.enums.PaymentTypeEnum
import company.tap.checkout.internal.enums.SectionType
import company.tap.checkout.internal.interfaces.BaseLayoutManager
import company.tap.checkout.internal.interfaces.PaymentCardComplete
import company.tap.checkout.internal.interfaces.onCardNFCCallListener
import company.tap.checkout.internal.utils.*
import company.tap.checkout.internal.viewmodels.CheckoutViewModel
import company.tap.checkout.open.CheckoutFragment
import company.tap.checkout.open.data_managers.PaymentDataSource
import company.tap.checkout.open.enums.CardType
import company.tap.nfcreader.open.reader.TapEmvCard
import company.tap.nfcreader.open.utils.TapNfcUtils
import company.tap.tapcardvalidator_android.CardBrand
import company.tap.tapcardvalidator_android.CardValidationState
import company.tap.tapcardvalidator_android.CardValidator
import company.tap.tapcardvalidator_android.DefinedCardBrand
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapuilibraryy.uikit.views.TapAlertView
import company.tap.tapuilibraryy.themekit.ThemeManager
import company.tap.tapuilibraryy.uikit.atoms.TapSeparatorView
import company.tap.tapuilibraryy.uikit.atoms.TapTextViewNew
import company.tap.tapuilibraryy.uikit.datasource.TapSwitchDataSource
import company.tap.tapuilibraryy.uikit.interfaces.TapPaymentShowHideClearImage
import company.tap.tapuilibraryy.uikit.interfaces.TapSelectionTabLayoutInterface
import company.tap.tapuilibraryy.uikit.models.SectionTabItem
import company.tap.tapuilibraryy.uikit.organisms.TapPaymentInput
import company.tap.tapuilibraryy.uikit.views.*
import kotlinx.android.synthetic.main.cardviewholder_layout1.view.*
import kotlinx.android.synthetic.main.switch_layout.view.*


@RequiresApi(Build.VERSION_CODES.N)
class PaymentInlineViewHolder(
    private val context: Context,
    private val checkoutViewModel: CheckoutViewModel,
    private val onPaymentCardComplete: PaymentCardComplete,
    private val onCardNFCCallListener: onCardNFCCallListener,
    private val switchViewHolder: SwitchViewHolder?,
    private val baseLayoutManager: BaseLayoutManager,
    private val cardViewModel: CardViewModel,
    private val checkoutFragment: CheckoutFragment,
    private val loyaltyViewHolder: LoyaltyViewHolder?,
    private val sdkLayout: LinearLayout,
    private val bottomSheetLayout: FrameLayout,
    private val headerLayout: LinearLayout,
) : TapBaseViewHolder,
    TapSelectionTabLayoutInterface, CardInputListener, TapPaymentShowHideClearImage {
    override val view: View =
        LayoutInflater.from(context).inflate(R.layout.payment_inline_viewholder, null)
    override val type = SectionType.PAYMENT_INPUT
    var tabLayout: TapSelectionTabLayout = view.findViewById(R.id.sections_tablayout)
    private var intertabLayout: TabLayout = tabLayout.findViewById(R.id.tab_layout)

    val paymentInputContainer: LinearLayout

    // private val clearView: ImageView
    var selectedType = PaymentTypeEnum.card
    private var shouldShowScannerOptions = true
    private var lastFocusField = CardInputListener.FocusField.FOCUS_CARD
    private var lastCardInput = ""
    private var cardScannerBtn: ImageView? = null
    private var savedCardsModel: SavedCard? = null

    // private var nfcButton: ImageView? = null
    //  var tapCardInputView: InlineCardInput
    var tapCardInputView: InlineCardInput2
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
    private var fullCardNumber: String? = null
    private var expiryDate: String? = null
    private var cvvNumber: String? = null
    private var cardHolderName: String? = null
    private var cardHolderNamePrev: String? = null
    private var cvvNumberPrev: String? = null
    private var expiryDatePrev: String? = null
    private val BIN_NUMBER_LENGTH = 6
    private lateinit var cardSchema: String
    var schema: CardScheme? = null
    var itemsCardsList = ArrayList<SectionTabItem>()
    var resetView: Boolean = false
    private var displayMetrics: Int? = 0
    private var height: Int = 0
    var constraintt: LinearLayout
    var acceptedCardText: TapTextViewNew
    var tapInlineCardSwitch: TapInlineCardSwitch? = null
    var secondaryLayout: LinearLayout? = null
    var textViewPowered: TapTextViewNew? = null
    var saveForOtherCheckBox: CheckBox? = null
    var nfcButton: ImageView? = null
    var scannerButton: ImageView? = null
    var cardBrandView: CardBrandView? = null
    var closeButton: ImageView? = null
    var cardInputUIStatus: CardInputUIStatus? = CardInputUIStatus.NormalCard

    var contactDetailsView: TapContactDetailsView? = null
    var shippingDetailView: TapShippingDetailView? = null
    var tapPaymentInput: TapPaymentInput? = null
    var allFieldsValid: Boolean? = false
    var separator1: TapSeparatorView? = null
    var cardNumValidation: Boolean = false
    var mPreviousCount: Int = 0
    var saveLocalBinLookup: BINLookupResponse? = null
    var prevSetCardBrand: CardBrand? = CardBrand.unknown


    init {

        tabLayout.setTabLayoutInterface(this)
        tapMobileInputView = TapMobilePaymentView(context, null)
        tapMobileInputView.setTapPaymentShowHideClearImage(this)
        tapCardInputView = InlineCardInput2(context, null)
        // tapAlertView = tapCardInputView.findViewById(R.id.alertView)
        nfcButton = tapCardInputView.findViewById(R.id.nfc_button)
        cardBrandView = tapCardInputView.findViewById(R.id.card_brand_view)
        scannerButton = tapCardInputView.findViewById(R.id.card_scanner_button)
        closeButton = tapCardInputView.findViewById(R.id.clear_text)
        tapPaymentInput = view.findViewById(R.id.tap_payment_input_layout)
        separator1 = tapCardInputView.findViewById(R.id.separator_1)
        tapAlertView = tapPaymentInput?.findViewById(R.id.alertView)
        paymentInputContainer = view.findViewById(R.id.payment_input_layout)
        tapCardInputView.backArrow?.visibility = View.GONE
        contactDetailsView = view.findViewById(R.id.contact_detailsView)
        shippingDetailView = view.findViewById(R.id.ship_detailsView)

        tapInlineCardSwitch = tapPaymentInput?.findViewById(R.id.switch_Inline_card)
        tapInlineCardSwitch?.brandingLayout?.visibility = View.GONE
        tapPaymentInput?.separator?.visibility = View.GONE

        secondaryLayout = tapCardInputView.findViewById(R.id.secondary_Layout)
        textViewPowered = tapCardInputView.findViewById(R.id.textViewPowered)
        saveForOtherCheckBox = tapCardInputView.findViewById(R.id.saveForOtherCheckBox)
        secondaryLayout?.visibility = View.GONE
        tapInlineCardSwitch?.setSwitchDataSource(
            TapSwitchDataSource(
                switchSaveMerchantCheckout = LocalizationManager.getValue(
                    "cardSaveLabel",
                    "TapCardInputKit"
                )

            )
        )
        // tabLayout.visibility=View.VISIBLE
        tabLayout?.fadeVisibility(View.VISIBLE)
        // tapSeparatorViewLinear = view.findViewById(R.id.tapSeparatorViewLinear)
        // tapSeparatorViewLinear?.setBackgroundColor(Color.parseColor(ThemeManager.getValue("horizontalList.backgroundColor")))
        // tabLayout.setBackgroundColor(Color.parseColor(ThemeManager.getValue("inlineCard.commonAttributes.backgroundColor")))
        //tabLayout.changeTabItemMarginBottomValue(5)
        tabLayout.changeTabItemMarginTopValue(5)

        constraintt = view.findViewById(R.id.constraintt)
        constraintt.setBackgroundColor(Color.parseColor(ThemeManager.getValue("horizontalList.backgroundColor")))
        acceptedCardText = view.findViewById(R.id.acceptedCardText)

        acceptedCardText.text = LocalizationManager.getValue("weSupport", "TapCardInputKit")
        saveForOtherCheckBox?.text =
            LocalizationManager.getValue("cardSaveForTapLabel", "TapCardInputKit")
        //cardBrandView?.iconView?.setImageResource(iconViewRes1)
        bindViewComponents()

    }


    @RequiresApi(Build.VERSION_CODES.N)
    override fun bindViewComponents() {
        initCardInput()
        /**
         * @TODO : waiting for backend to finish it
         */
        // initMobileInput()
        // tapMobileInputViewWatcher()
        initializeCardForm()
        initializeIcons()
        // initializeCardBrandView()
        initCustomerDetailView()
        tapCardInputView.setCardInputListener(this)

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

        contactDetailsView?.contactEmailET?.hint = LocalizationManager.getValue(
            "email",
            "Common"
        )
        shippingDetailView?.shippingDetailTitle?.text = LocalizationManager.getValue(
            "SaveCardHeader",
            "HorizontalHeaders",
            "shippingSectionTitle"
        )
        shippingDetailView?.flatEditText?.hint = LocalizationManager.getValue(
            "flatPlaceHolder",
            "TapCardInputKit"
        )
        shippingDetailView?.additionalLineEditText?.hint = LocalizationManager.getValue(
            "additionalLinePlaceHolder",
            "TapCardInputKit"
        )
        shippingDetailView?.cityEditText?.hint = LocalizationManager.getValue(
            "cityPlaceHolder",
            "TapCardInputKit"
        )
        tapInlineCardSwitch?.switchSaveCard?.isChecked = false
        contactDetailsView?.visibility = View.GONE
        contactDetailsView?.contactEmailET?.setBackgroundColor(
            Color.parseColor(
                ThemeManager.getValue(
                    "customerDataCollection.backgroundColor"
                )
            )
        )
        shippingDetailView?.flatEditText?.setBackgroundColor(
            Color.parseColor(
                ThemeManager.getValue(
                    "customerDataCollection.backgroundColor"
                )
            )
        )
        shippingDetailView?.additionalLineEditText?.setBackgroundColor(
            Color.parseColor(
                ThemeManager.getValue(
                    "customerDataCollection.backgroundColor"
                )
            )
        )
        shippingDetailView?.cityEditText?.setBackgroundColor(
            Color.parseColor(
                ThemeManager.getValue(
                    "customerDataCollection.backgroundColor"
                )
            )
        )
        // contactDetailsView?.contactEmailET?.setBackgroundColor(Color.parseColor(ThemeManager.getValue("customerDataCollection.backgroundColor")))
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun initializeIcons() {

        nfcButton?.setOnClickListener {
            checkoutViewModel.onClickNFC()
        }
        scannerButton?.setOnClickListener {
            checkoutViewModel.onClickCardScanner(true)
        }
        controlScannerOptions()
        closeButton?.setOnClickListener {

            tapCardInputView.clear()
            closeButton?.visibility = View.GONE
            controlScannerOptions()
            cardInputUIStatus = CardInputUIStatus.NormalCard
            /*tapCardInputView.setSingleCardInput(
                  CardBrandSingle.Unknown, null
              )*/
            tapInlineCardSwitch?.visibility = View.GONE
            //tapCardInputView.separatorcard2.visibility = View.INVISIBLE
            separator1?.visibility = View.GONE
            // resetCardBrandIcon()
            if (PaymentDataSource.getBinLookupResponse() != null) {
                PaymentDataSource.setBinLookupResponse(null)

            }

            /*   if(tapCardInputView.fullCardNumber!=null){
                   tapCardInputView.fullCardNumber= null
                   tabLayout.resetBehaviour()
               }*/

            tapInlineCardSwitch?.saveForOtherCheckBox?.isChecked = false
            tapInlineCardSwitch?.switchSaveCard?.isChecked = false
            contactDetailsView?.visibility = View.GONE
            shippingDetailView?.visibility = View.GONE
            closeButton?.visibility = View.GONE
            tapCardInputView.setVisibilityOfHolderField(false)
            tapCardInputView.holderNameEnabled = false
            checkoutViewModel.incrementalCount = 0
            //tabLayout.visibility =View.VISIBLE
            // intertabLayout.visibility =View.VISIBLE
            tabLayout.fadeVisibility(View.VISIBLE)
            intertabLayout.fadeVisibility(View.VISIBLE)
            acceptedCardText.fadeVisibility(View.VISIBLE)
            allFieldsValid = false
            tapAlertView?.fadeVisibility(View.GONE, 500)
            expiryDatePrev = null
            cvvNumberPrev = null
            if (getPreTypedCardData() != null) {
                getPreTypedCardData()?.cardNumber = null
                getPreTypedCardData()?.cvc = null
                getPreTypedCardData()?.cardholderName = null
                getPreTypedCardData()?.expirationMonth = null
                getPreTypedCardData()?.expirationYear = null
            }
            savedCardsModel = null
        }
    }

    private fun initializeCardBrandView() {
        // println("displayMetrics"+displayMetrics)
        if (displayMetrics == DisplayMetrics.DENSITY_260 || displayMetrics == DisplayMetrics.DENSITY_280 || displayMetrics == DisplayMetrics.DENSITY_300 || displayMetrics == DisplayMetrics.DENSITY_XHIGH || displayMetrics == DisplayMetrics.DENSITY_340 || displayMetrics == DisplayMetrics.DENSITY_360) {

            tabLayout.changeTabItemMarginBottomValue(25)
            tabLayout.changeTabItemMarginTopValue(25)
            //tabLayout.changeTabItemMarginBottomValue(30)
            tabLayout.changeTabItemMarginLeftValue(-21)
            tabLayout.changeTabItemMarginRightValue(-21)

        } else if (displayMetrics == DisplayMetrics.DENSITY_400 || displayMetrics == DisplayMetrics.DENSITY_XXHIGH || displayMetrics == DisplayMetrics.DENSITY_450 || displayMetrics == DisplayMetrics.DENSITY_440) {
            tabLayout.changeTabItemMarginBottomValue(35)
            tabLayout.changeTabItemMarginTopValue(35)
            tabLayout.changeTabItemMarginLeftValue(-35)
            tabLayout.changeTabItemMarginRightValue(-35)
        } else {
            tabLayout.changeTabItemMarginBottomValue(12)
            tabLayout.changeTabItemMarginTopValue(18)
            tabLayout.changeTabItemMarginLeftValue(-25)
            tabLayout.changeTabItemMarginRightValue(-25)
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

    }


    fun clearCardInputAction() {

        if (selectedType == PaymentTypeEnum.card) {
            //  tapCardInputView.clear()
            tapCardInputView.clearFocus()
            switchViewHolder?.setSwitchLocals(PaymentTypeEnum.card)
        } else if (selectedType == PaymentTypeEnum.telecom) {
            tapMobileInputView.clearNumber()
            switchViewHolder?.setSwitchLocals(PaymentTypeEnum.telecom)
        }
        tapCardInputView.setSingleCardInput(
            CardBrandSingle.Unknown, null
        )


//        switchViewHolder11?.view?.cardSwitch?.switchesLayout?.visibility = View.VISIBLE
        switchViewHolder?.view?.mainSwitch?.mainSwitchLinear?.visibility = View.VISIBLE
        tapAlertView?.fadeVisibility(View.GONE, 500)
        switchViewHolder?.view?.cardSwitch?.payButton?.isActivated = false
//        switchViewHolder11?.view?.cardSwitch?.showOnlyPayButton()
        switchViewHolder?.bindViewComponents()
        val payString: String = LocalizationManager.getValue("pay", "ActionButton")
        val nowString: String = LocalizationManager.getValue("pay", "ActionButton")
        switchViewHolder?.view?.cardSwitch?.payButton?.setButtonDataSource(
            false,
            "en",
            payString + " " + nowString,
            Color.parseColor(ThemeManager.getValue("actionButton.Invalid.backgroundColor")),
            Color.parseColor(ThemeManager.getValue("actionButton.Invalid.titleLabelColor")),
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
        // tapCardInputView.setVisibilityOfHolderField(false)
        tapCardInputView.holderNameEnabled = false
        checkoutViewModel.incrementalCount = 0
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

    @SuppressLint("ClickableViewAccessibility")
    private fun initCardInput() {
        resetCardBrandIcon()
        tapInlineCardSwitch?.visibility = View.GONE
        tapPaymentInput?.separator?.visibility = View.GONE
        /*  tapCardInputView.holderNameEnabled = PaymentDataSource.getCardHolderNameShowHide() != null && PaymentDataSource.getCardHolderNameShowHide()
          if (PaymentDataSource.getDefaultCardHolderName() != null) {
              tapCardInputView.setCardHolderName(PaymentDataSource.getDefaultCardHolderName())
          }*/
        height = Resources.getSystem().displayMetrics.heightPixels
        addViewsToPaymentViewContainer()
        tapCardInputView.clearFocus()
        cardNumberWatcher()
        expiryDateWatcher()
        cvcNumberWatcher()
        cardHolderNameWatcher()
        switchCheckedState()


        /*tapCardInputView.backArrow.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                //  println("getPreTypedCardData data was there"+getPreTypedCardData())
                checkoutViewModel.setTitleNormalCard()
                if (getPreTypedCardData() != null) setPrevTypedCard()
                else {

                    tabLayout.resetBehaviour()
                    cardInputUIStatus = CardInputUIStatus.NormalCard
                    tabLayout.resetBehaviour()
                    tabLayout.getChildAt(0).minimumHeight=15
                    tapCardInputView.clear()
                    closeButton?.visibility = View.GONE
                    controlScannerOptions()
                    *//* tapCardInputView.setSingleCardInput(
                       CardBrandSingle.Unknown, null
                       )*//*
                    tapInlineCardSwitch?.visibility = View.GONE
                    //  tapCardInputView.separatorcard2.visibility = View.INVISIBLE
                    // resetCardBrandIcon()
                    tapAlertView?.fadeVisibility(View.GONE, 500)
                    checkoutViewModel.resetCardSelection()

                    checkoutViewModel.isSavedCardSelected = false
                    //resetPaymentCardView()
                    // intertabLayout.visibility = View.VISIBLE
                    // tabLayout.visibility = View.VISIBLE
                    // acceptedCardText.visibility = View.VISIBLE
                    tabLayout.fadeVisibility(View.VISIBLE)
                    intertabLayout.fadeVisibility(View.VISIBLE)
                    acceptedCardText.fadeVisibility(View.VISIBLE)
                    checkoutViewModel.resetViewHolder()
                    expiryDate = null
                    cvvNumber = null
                    cardHolderName = null
                    fullCardNumber = null
                }

                return false
            }
        })*/
        tapCardInputView.backArrow.setOnClickListener {
            //  println("getPreTypedCardData data was there"+getPreTypedCardData())
            checkoutViewModel.setTitleNormalCard()
            if (getPreTypedCardData() != null) setPrevTypedCard()
            else {

                tabLayout.resetBehaviour()
                cardInputUIStatus = CardInputUIStatus.NormalCard
                tabLayout.resetBehaviour()
                tabLayout.getChildAt(0).minimumHeight = 15
                tapCardInputView.clear()
                closeButton?.visibility = View.GONE
                controlScannerOptions()
                /* tapCardInputView.setSingleCardInput(
                   CardBrandSingle.Unknown, null
                   )*/
                tapInlineCardSwitch?.visibility = View.GONE
                //  tapCardInputView.separatorcard2.visibility = View.INVISIBLE
                // resetCardBrandIcon()
                tapAlertView?.fadeVisibility(View.GONE, 500)
                checkoutViewModel.resetCardSelection()

                checkoutViewModel.isSavedCardSelected = false
                //resetPaymentCardView()
                // intertabLayout.visibility = View.VISIBLE
                // tabLayout.visibility = View.VISIBLE
                // acceptedCardText.visibility = View.VISIBLE
                tabLayout.fadeVisibility(View.VISIBLE)
                intertabLayout.fadeVisibility(View.VISIBLE)
                acceptedCardText.fadeVisibility(View.VISIBLE)
                checkoutViewModel.resetViewHolder()
                expiryDate = null
                cvvNumber = null
                cardHolderName = null
                fullCardNumber = null
            }
        }
    }


    private fun setPrevTypedCard() {
        println("setPrevTypedCard is called")
        cardInputUIStatus = CardInputUIStatus.NormalCard
        val updateCardString: String = getPreTypedCardData()?.cardNumber?.trim().toString()
            .substring(0, 6) + getPreTypedCardData()?.cardNumber?.length?.minus(4)
            ?.let {
                getPreTypedCardData()?.cardNumber?.trim().toString().substring(
                    it
                )
            }
            println("getPreTypedCardData()"+getPreTypedCardData()?.cvc)
        val cardModel = company.tap.cardinputwidget2.Card(
            number = getPreTypedCardData()?.cardNumber,
            cvc = getPreTypedCardData()?.cvc,
           expMonth =  getPreTypedCardData()?.expirationMonth?.toInt(),
           expYear =  getPreTypedCardData()?.expirationYear?.toInt(),
            name = getPreTypedCardData()?.cardholderName,
            last4 = getPreTypedCardData()?.cardNumber?.length?.minus(4)
                ?.let { getPreTypedCardData()?.cardNumber?.substring(it) },
            brand = company.tap.cardinputwidget2.CardBrand.fromCardNumber(getPreTypedCardData()?.cardNumber),
            metadata = null
            )


        if (getPreTypedCardData()?.cardholderName != null) {
            tapInlineCardSwitch?.visibility = View.VISIBLE
            tapInlineCardSwitch?.switchesLayout?.visibility = View.VISIBLE
            tapInlineCardSwitch?.switchSaveCard?.visibility = View.VISIBLE
            tapInlineCardSwitch?.switchSaveCard?.isChecked = true
            tapCardInputView.setVisibilityOfHolderField(true)
            tapCardInputView.holderNameEnabled = true
            separator1?.visibility = View.VISIBLE
            tapInlineCardSwitch?.switchSaveCard?.isChecked = true

        }

        tapCardInputView.setNormalCardDetails(cardModel, CardInputUIStatus.NormalCard)

        val card = CardValidator.validate(getPreTypedCardData()?.cardNumber)
        getPreTypedCardData()?.cardNumber?.let {
            logicTosetImageDynamic(
                card.cardBrand,
                it
            )
        }

        prevSetCardBrand = card.cardBrand
        contactDetailsView?.visibility = View.GONE
        shippingDetailView?.visibility = View.GONE
        // intertabLayout.visibility = View.GONE
        tabLayout?.fadeVisibility(View.GONE, 2000)
        intertabLayout?.fadeVisibility(View.GONE, 2000)
        if(getPreTypedCardData()!=null && getPreTypedCardData()?.cvc!=null) tapAlertView?.fadeVisibility(View.GONE, 2000)
        //Added for opening as soon as cvv focus
        CustomUtils.showKeyboard(context)
    }


    fun setNFCCardData(emvCard: TapEmvCard , month :Int , year :Int)
    {
        cardInputUIStatus = CardInputUIStatus.NormalCard
        val updateCardString: String = emvCard.cardNumber?.trim().toString()
            .substring(0, 6) + emvCard.cardNumber?.length?.minus(4)
            ?.let {
                emvCard.cardNumber?.trim().toString().substring(
                    it
                )
            }
        println("updateCardString"+updateCardString)

        val cardModel = company.tap.cardinputwidget2.Card(
            updateCardString,
            null,
          month,
            year,
            emvCard.holderFirstname,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            emvCard.cardNumber?.length?.minus(4)
                ?.let { emvCard?.cardNumber?.substring(it) },
            company.tap.cardinputwidget2.CardBrand.fromCardNumber(emvCard.cardNumber),
            null,
            null,
            null,
            null,
            null,
            null
        )


        if (emvCard?.holderFirstname != null) {
            tapInlineCardSwitch?.visibility = View.VISIBLE
            tapInlineCardSwitch?.switchesLayout?.visibility = View.VISIBLE
            tapInlineCardSwitch?.switchSaveCard?.visibility = View.VISIBLE
            tapInlineCardSwitch?.switchSaveCard?.isChecked = true
            tapCardInputView.setVisibilityOfHolderField(true)
            tapCardInputView.holderNameEnabled = true
            separator1?.visibility = View.VISIBLE
            tapInlineCardSwitch?.switchSaveCard?.isChecked = true

        }

        tapCardInputView.setScanNFCCardDetails(cardModel, CardInputUIStatus.NormalCard)
        /*  val alertMessage:String = LocalizationManager.getValue("Warning", "Hints", "missingCVV")
           tapAlertView?.alertMessage?.text =alertMessage.replace("%i","3")

           tapAlertView?.visibility =View.VISIBLE*/
        if (CustomUtils.getCurrentTheme() != null && CustomUtils.getCurrentTheme()
                .contains("dark")
        ) {
            val card = CardValidator.validate(emvCard?.cardNumber)
            emvCard.cardNumber?.let {
                logicTosetImageDynamic(
                    card.cardBrand,
                    it
                )
            }
        } else {
            val card = CardValidator.validate(emvCard?.cardNumber)
            emvCard.cardNumber?.let {
                logicTosetImageDynamic(
                    card.cardBrand,
                    it
                )
            }
        }

        contactDetailsView?.visibility = View.GONE
        shippingDetailView?.visibility = View.GONE
        // intertabLayout.visibility = View.GONE
        tabLayout?.fadeVisibility(View.GONE, 2000)
        intertabLayout?.fadeVisibility(View.GONE, 2000)

        fullCardNumber= emvCard.cardNumber
       expiryDate = month.toString()+"/"+year.toString()
        cardHolderName = emvCard.holderFirstname
        //Added for opening as soon as cvv focus
        CustomUtils.showKeyboard(context)

    }

    fun setCardScanData(tapCard: TapCard, month :Int, year :Int)
    {
        cardInputUIStatus = CardInputUIStatus.NormalCard
        val updateCardString: String = tapCard.cardNumber?.trim().toString()
            .substring(0, 6) + tapCard.cardNumber?.length?.minus(4)
            ?.let {
                tapCard.cardNumber?.trim().toString().substring(
                    it
                )
            }
        println("updateCardString"+updateCardString)
        println("cardnumber scanner"+tapCard.cardNumber)

        val cardModel = company.tap.cardinputwidget2.Card(
            tapCard.cardNumber,
            null,
            month,
            year,
            tapCard.cardHolder,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            tapCard.cardNumber?.length?.minus(4)
                ?.let { tapCard?.cardNumber?.substring(it) },
            company.tap.cardinputwidget2.CardBrand.fromCardNumber(tapCard.cardNumber),
            null,
            null,
            null,
            null,
            null,
            null
        )


        if (tapCard?.cardHolder != null) {
            tapInlineCardSwitch?.visibility = View.VISIBLE
            tapInlineCardSwitch?.switchesLayout?.visibility = View.VISIBLE
            tapInlineCardSwitch?.switchSaveCard?.visibility = View.VISIBLE
            tapInlineCardSwitch?.switchSaveCard?.isChecked = true
            tapCardInputView.setVisibilityOfHolderField(true)
            tapCardInputView.holderNameEnabled = true
            separator1?.visibility = View.VISIBLE
            tapInlineCardSwitch?.switchSaveCard?.isChecked = true
            cardHolderName = tapCard.cardHolder

        }
        println("cardNumValidation is"+cardNumValidation)

        tapCardInputView.setScanNFCCardDetails(cardModel, CardInputUIStatus.NormalCard)
        /*  val alertMessage:String = LocalizationManager.getValue("Warning", "Hints", "missingCVV")
           tapAlertView?.alertMessage?.text =alertMessage.replace("%i","3")

           tapAlertView?.visibility =View.VISIBLE*/
        if (CustomUtils.getCurrentTheme() != null && CustomUtils.getCurrentTheme()
                .contains("dark")
        ) {
            val card = CardValidator.validate(tapCard?.cardNumber)
            tapCard.cardNumber?.let {
                logicTosetImageDynamic(
                    card.cardBrand,
                    it
                )
            }
        } else {
            val card = CardValidator.validate(tapCard?.cardNumber)
            tapCard.cardNumber?.let {
                logicTosetImageDynamic(
                    card.cardBrand,
                    it
                )
            }
        }

        contactDetailsView?.visibility = View.GONE
        shippingDetailView?.visibility = View.GONE
        // intertabLayout.visibility = View.GONE
        tabLayout?.fadeVisibility(View.GONE, 2000)
        intertabLayout?.fadeVisibility(View.GONE, 2000)
        println("tapCard val"+tapCard.cardNumber.trim())
        fullCardNumber= tapCard.cardNumber
        expiryDate = month.toString()+"/"+year.toString()
        cardHolderName = tapCard.cardHolder
        //Added for opening as soon as cvv focus
        CustomUtils.showKeyboard(context)

    }

    private fun resetCardBrandIcon() {
        if (ThemeManager.currentTheme.isNotEmpty() && ThemeManager.currentTheme.contains("dark")) {
            cardBrandView?.iconView?.setImageResource(R.drawable.card_icon_dark)
        } else if (ThemeManager.currentTheme.isNotEmpty() && ThemeManager.currentTheme.contains("light")) {
            cardBrandView?.iconView?.setImageResource(R.drawable.card_icon_light)
        }
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
                if (allFieldsValid == true) {
                    intertabLayout.visibility = View.GONE
                    tabLayout.visibility = View.GONE
                    acceptedCardText.visibility = View.INVISIBLE

                }

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().length > 3) {
                    if (PaymentDataSource.getCardHolderNameShowHide()) {
                        // tapInlineCardSwitch?.visibility = View.VISIBLE
                        tapInlineCardSwitch?.fadeVisibility(View.VISIBLE)
                        contactDetailsView?.visibility = View.GONE //temp visible to gone
                        shippingDetailView?.visibility = View.GONE //temp visible to gone
                        allFieldsValid = true
                    } else {
                        contactDetailsView?.visibility = View.GONE
                        shippingDetailView?.visibility = View.GONE
                    }

                } else {
                    contactDetailsView?.visibility = View.GONE
                    shippingDetailView?.visibility = View.GONE
                    // tapInlineCardSwitch?.visibility = View.GONE
                    tapInlineCardSwitch?.fadeVisibility(View.GONE, 2000)
                }
            }

            override fun afterTextChanged(s: Editable?) {
                /* if(s.toString().length>3){
                     if(PaymentDataSource.getCardHolderNameShowHide()){
                         tapInlineCardSwitch?.visibility = View.VISIBLE
                         contactDetailsView?.visibility = View.GONE //temp visible to gone
                         shippingDetailView?.visibility = View.GONE //temp visible to gone
                         allFieldsValid = true
                     }else {
                         contactDetailsView?.visibility = View.GONE
                         shippingDetailView?.visibility = View.GONE
                     }

                 }else {
                     contactDetailsView?.visibility = View.GONE
                     shippingDetailView?.visibility = View.GONE
                     tapInlineCardSwitch?.visibility = View.GONE

                 }*/
                println("PaymentDataSource?.getBinLookupResponse()"+PaymentDataSource?.getBinLookupResponse())
                println("cardHolderName>>"+cardHolderName)
                //On Details complete
                if (s.toString().length > 3) {
                    cardNumber?.let {
                        expiryDate?.let { it1 ->
                            if(PaymentDataSource.getBinLookupResponse()!=null)
                            onPaymentCardComplete.onPayCardCompleteAction(
                                true, PaymentType.CARD,
                                it, it1, cvvNumber, cardHolderName ,PaymentDataSource.getBinLookupResponse()?.scheme?.cardBrand?.rawValue
                            )
                        }
                    }
                    Bugfender.d(
                        LogsModel.EVENT.name,
                        "Finished valid raw card data for:" + PaymentType.CARD
                    )

                    cardHolderNamePrev = s.toString()

                }

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
        /* if(tapInlineCardSwitch?.switchSaveCard?.isChecked == true && tapInlineCardSwitch?.saveForOtherCheckBox?.isChecked == true){
             contactDetailsView?.visibility = View.VISIBLE
         }*/

        tapInlineCardSwitch?.switchSaveCard?.setOnCheckedChangeListener { buttonView, isChecked ->
            secondaryLayout?.visibility = View.GONE
            tapAlertView?.fadeVisibility(View.GONE, 500)


            if (isChecked) {
                tapInlineCardSwitch?.saveForOtherCheckBox?.isChecked = true
                tapInlineCardSwitch?.saveForOtherCheckBox?.visibility = View.VISIBLE
                tapInlineCardSwitch?.toolsTipImageView?.visibility = View.VISIBLE
                tapPaymentInput?.separator?.visibility = View.VISIBLE
                tapInlineCardSwitch?.saveForOtherTextView?.visibility = View.VISIBLE
                if (!PaymentDataSource.getCardHolderNameShowHide()) {
                    shippingDetailView?.visibility = View.GONE
                    contactDetailsView?.visibility = View.GONE
                }


            } else {
                tapPaymentInput?.separator?.visibility = View.GONE
                tapInlineCardSwitch?.saveForOtherCheckBox?.isChecked = false
                tapInlineCardSwitch?.saveForOtherCheckBox?.visibility = View.GONE
                tapInlineCardSwitch?.saveForOtherTextView?.visibility = View.GONE
                tapInlineCardSwitch?.toolsTipImageView?.visibility = View.GONE


            }


        }


        tapInlineCardSwitch?.saveForOtherCheckBox?.setOnCheckedChangeListener(object :
            CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                secondaryLayout?.visibility = View.GONE
                tapAlertView?.fadeVisibility(View.GONE, 500)
                // tabLayout?.visibility = View.GONE
                // intertabLayout?.visibility = View.GONE
                //tapInlineCardSwitch?.saveForOtherCheckBox?.isChecked = isChecked

                if (isChecked) {
                    println("saveForOtherCheckBox " + isChecked)

                    if (cardInputUIStatus == CardInputUIStatus.SavedCard) {
                        contactDetailsView?.visibility = View.GONE
                        shippingDetailView?.visibility = View.GONE
                    } else if (allFieldsValid == true) {
                        if (!PaymentDataSource.getCardHolderNameShowHide()) {
                            shippingDetailView?.visibility = View.GONE
                            contactDetailsView?.visibility = View.GONE
                        } else {
                            contactDetailsView?.visibility = View.GONE //temp visibel to gone
                            shippingDetailView?.visibility = View.GONE //temp visibel to gone
                        }
                    }

                } else {
                    contactDetailsView?.visibility = View.GONE
                    shippingDetailView?.visibility = View.GONE

                    // tapInlineCardSwitch?.switchSaveCard?.isChecked = false
                    tapInlineCardSwitch?.saveForOtherCheckBox?.isChecked = false
                }

            }

        })

    }

    private fun addViewsToPaymentViewContainer() {
        displayMetrics = CustomUtils.getDeviceDisplayMetrics(context as Activity)
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
        /* if (ThemeManager.currentTheme.isNotEmpty() && ThemeManager.currentTheme.contains("dark")) {
          //   tapPaymentInput?.cardInputChipView?.setBackgroundResource(R.drawable.border_unclick_black)
         } else {
             tapPaymentInput?.cardInputChipView?.setBackgroundResource(R.drawable.border_unclick_cardinput)
         }
         tapPaymentInput?.cardInputChipView?.let {
             setBorderedView(
                 it,
                 15.0f,// corner raduis
                 0.0f,
                 Color.parseColor(ThemeManager.getValue("inlineCard.commonAttributes.shadow.color")),
                 Color.parseColor(ThemeManager.getValue("inlineCard.commonAttributes.shadow.color")),
                 Color.parseColor(ThemeManager.getValue("inlineCard.commonAttributes.shadow.color"))

             )
         }
         tapPaymentInput?.cardInputChipView?.cardElevation= 0.2f*/
        /*  if (ThemeManager.currentTheme.isNotEmpty() && ThemeManager.currentTheme.contains("dark")) {
              tapPaymentInput?.cardInputChipView?.setBackgroundResource(R.drawable.border_unclick_black)
          } else {
              tapPaymentInput?.cardInputChipView?.setBackgroundResource(R.drawable.border_unclick_cardinput)
          }
          tapPaymentInput?.cardInputChipView?.let {
              setBorderedView(
                  it,
                  15.0f,// corner raduis
                  //(ThemeManager.getValue("horizontalList.chips.radius") as Int).toFloat(),
                  0.0f,
                  Color.parseColor(ThemeManager.getValue("horizontalList.chips.currencyChip.unSelected.shadow.color")),
                  Color.parseColor(ThemeManager.getValue("horizontalList.chips.currencyChip.backgroundColor")),
                  Color.parseColor(ThemeManager.getValue("horizontalList.chips.currencyChip.unSelected.shadow.color"))
              )
          }*/
        paymentInputContainer.addView(tapCardInputView)


    }

    private fun cardNumberWatcher() {
        tapCardInputView.setCardHint(
            LocalizationManager.getValue(
                "cardNumberPlaceHolder",
                "TapCardInputKit"
            )
        )
        if(tapCardInputView.cardFormHasFocus) checkoutViewModel?.resetViewHolder()
        tapCardInputView.setCardNumberTextWatcher(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
//                cardNumAfterTextChangeListener(s, this)
                /*          if(cardNumber?.length!=null)
                          if(cardNumber?.length!! >=19) {
                              println("tapCardInputView no" + tapCardInputView.isDeleting)
                              if (tapCardInputView.isDeleting == true) {

                              } else {
                                  println("full no" + cardNumber)

                                  tapCardInputView.setCardNumberMasked(cardNumber?.let {
                                      maskCardNumber(
                                          it
                                      )
                                  })
                              }
                              tapCardInputView.removeCardNumberTextWatcher(this)
                            //  tapCardInputView.setCardNumberTextWatcher(this)
                          }*/

                var length: Int? = s?.length;

                //mPreviousLength is a field
                var mPreviousLength = 0
                if (mPreviousLength > length!!) {
                    // delete character action have done
                    // do what ever you want
                    // Log.d("MainActivityTag", "Character deleted");
                    //  intertabLayout.visibility = View.VISIBLE
                    //  tabLayout.visibility = View.VISIBLE
                    // acceptedCardText.visibility = View.VISIBLE
                    tabLayout?.fadeVisibility(View.VISIBLE)
                }
                mPreviousLength = length;



                if (s != null) {
                    /* if(s.isEmpty() || s.length!!  < 19 || s.length > 10 ){
                         acceptedCardText.visibility = View.VISIBLE
                         intertabLayout.visibility = View.VISIBLE
                         tabLayout.visibility = View.VISIBLE
                     }*/
                    if (s?.length!! >= 19) afterValidation()
                }


            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                checkoutViewModel.resetViewHolder()
                //tapAlertView?.fadeVisibility(View.GONE, 500) // removed
                if (after < count) {
                    // delete character action have done
                    // do what ever you want
                    /* intertabLayout.visibility = View.VISIBLE
                     tabLayout.visibility = View.VISIBLE
                     acceptedCardText.visibility = View.VISIBLE*/
                }

            }

            @RequiresApi(Build.VERSION_CODES.N)
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                println("cardInputUIStatus>>" + cardInputUIStatus)
                println("start>>" + start)
                println("before>>" + before)
                println("count>>" + count)

                if (mPreviousCount > count) {
                    // delete character action have done
                    // do what ever you want
                    // Log.d("MainActivityTag", "Character deleted");
                   /* if(tapCardInputView.cardNumberIsValid()){
                        tapCardInputView.setCardNumberTextWatcher(textWatcher)
                    }else  tapCardInputView.removeCardNumberTextWatcher(textWatcher)*/
                    tabLayout?.fadeVisibility(View.VISIBLE)
                    intertabLayout?.fadeVisibility(View.VISIBLE)
                    acceptedCardText?.fadeVisibility(View.VISIBLE)

                }
                mPreviousCount = count;


                if (cardInputUIStatus != CardInputUIStatus.SavedCard ) {
                    onCardTextChange(s)
                   cardNumAfterTextChangeListener(s.toString().trim(), this)

                }

            }
        })
    }

    private fun afterValidation() {
        if (!fullCardNumber.isNullOrBlank() && !fullCardNumber.isNullOrEmpty() && cardNumValidation && !expiryDate.isNullOrBlank() && !expiryDate.isNullOrEmpty() && !cvvNumber.isNullOrBlank() && !cvvNumber.isNullOrEmpty()
        ) {

            if (PaymentDataSource?.getCardHolderNameShowHide()) {
                tapCardInputView.setVisibilityOfHolderField(PaymentDataSource?.getCardHolderNameShowHide())
                tapCardInputView.holderNameEnabled = true
                //  tapInlineCardSwitch?.visibility = View.VISIBLE
                tapInlineCardSwitch?.fadeVisibility(View.VISIBLE)
            }

        }
    }

    private fun onCardTextChange(s: CharSequence?) {
        if (s.toString().isEmpty()) {
            closeButton?.visibility = View.GONE
            tapAlertView?.fadeVisibility(View.GONE, 500)
        } else {
            closeButton?.visibility = View.VISIBLE
        }
        tapInlineCardSwitch?.visibility = View.GONE

    }

    private fun expiryDateWatcher() {
        tapCardInputView.setExpiryHint(
            LocalizationManager.getValue(
                "cardExpiryPlaceHolder",
                "TapCardInputKit"
            )
        )

        tapCardInputView.setExpiryDateTextWatcher(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(!tapCardInputView.isExpDateValid){
                    checkoutViewModel.unActivateActionButton()
                    tapInlineCardSwitch?.visibility =View.GONE
                }

            }

            override fun afterTextChanged(s: Editable?) {
                afterTextChangeAction(s)
            }
        })
    }

    private fun afterTextChangeAction(s: Editable?) {

        // tapInlineCardSwitch?.visibility = View.VISIBLE
        if (s.isNullOrEmpty()) {
            // tabLayout.resetBehaviour()
        } else {
            /**
             * we will get date value
             */
            expiryDate = s.toString()

            println("isExpDateValid>>>"+tapCardInputView.isExpDateValid)
            if (s.length >= 5 ) {
                if (cardInputUIStatus?.equals(CardInputUIStatus.SavedCard) == true) {
                    tapAlertView?.fadeVisibility(View.GONE, 500)
                } else {
                    expiryDatePrev = s.toString()
                    if (tapCardInputView.isExpDateValid) {
                        tapAlertView?.fadeVisibility(View.VISIBLE)
                        val alertMessage: String =
                            LocalizationManager.getValue("Warning", "Hints", "missingCVV")
                        tapAlertView?.alertMessage?.text = alertMessage.replace("%i", "3")
                    } else{
                        checkoutViewModel.unActivateActionButton()
                        tapInlineCardSwitch?.visibility =View.GONE
                       // tapAlertView?.fadeVisibility(View.GONE, 500)
                    }
                }
                // tapAlertView?.visibility = View.VISIBLE
                lastFocusField = CardInputListener.FocusField.FOCUS_CVC
                // checkoutFragment.scrollView?.scrollTo(0,height)
                tapInlineCardSwitch?.switchSaveCard?.isChecked = true
            } else {
              tapAlertView?.fadeVisibility(View.GONE, 500)
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
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if (s?.trim()?.length == 3 || s?.trim()?.length == 4) {
                    if (cardInputUIStatus == CardInputUIStatus.NormalCard) {
                        if (PaymentDataSource?.getBinLookupResponse()?.scheme != null) {
                            PaymentDataSource?.getBinLookupResponse()?.scheme?.cardBrand?.let {
                                logicForImageOnCVV(
                                    it,
                                    s.toString()
                                )
                            }
                        } else {

                            if (fullCardNumber != null)
                                logicForImageOnCVV(
                                    CardValidator.validate(fullCardNumber).cardBrand,
                                    s.toString()
                                )
                        }
                        acceptedCardText.visibility = View.GONE
                        tabLayout.fadeVisibility(View.GONE, 2000)
                    } else logicForImageOnCVV(
                        CardValidator.validate(savedCardsModel?.firstSix).cardBrand,
                        s.toString()
                    )
                }
            }

            override fun afterTextChanged(s: Editable?) {
                tapPaymentInput?.visibility = View.VISIBLE
                if (cardInputUIStatus == CardInputUIStatus.SavedCard) tapAlertView?.visibility =
                    View.GONE

                /**
                 * we will get cvv number
                 */
                cvvNumber = s.toString()

                if (s?.trim()?.length == 3 || s?.trim()?.length == 4 && tapCardInputView.isExpDateValid) {
                    if (!PaymentDataSource.getCardHolderNameShowHide()) {
                        var paymentTyper :PaymentType ?= PaymentType.CARD
                        println("savedCardsModel   hhshhs"+savedCardsModel)
                        if(savedCardsModel ==null) {
                            paymentTyper = PaymentType.CARD
                        } else  paymentTyper = PaymentType.SavedCard

                        cardNumber.toString().let {
                            expiryDate?.let { it1 ->
                                cvvNumber?.let { it2 ->
                                    onPaymentCardComplete.onPayCardCompleteAction(
                                        true, paymentTyper,
                                        it, it1, it2, null,PaymentDataSource?.getBinLookupResponse()?.scheme?.cardBrand?.rawValue
                                    )

                                    /* tapCardInputView.separatorcard2.setBackgroundColor(
                                         Color.parseColor(
                                             ThemeManager.getValue("tapSeparationLine.backgroundColor")
                                         )
                                     )*/

                                    tapInlineCardSwitch?.switchSaveCard?.isChecked = true

                                    tapInlineCardSwitch?.fadeVisibility(View.VISIBLE)
                                    Bugfender.d(
                                        CustomUtils.tagEvent,
                                        "Finished valid raw card data for:" + PaymentType.CARD
                                    )

                                }
                            }
                        }
                        allFieldsValid = true

                    } else {
                        if (cardInputUIStatus != null && cardInputUIStatus == CardInputUIStatus.NormalCard) {
                            if (PaymentDataSource.getCardHolderNameShowHide()) {
                                tapCardInputView.holderNameEnabled = true
                                tapCardInputView.setVisibilityOfHolderField(PaymentDataSource.getCardHolderNameShowHide())
                                /* tapCardInputView.separatorcard2.setBackgroundColor(
                                     Color.parseColor(
                                         ThemeManager.getValue("tapSeparationLine.backgroundColor")
                                     )
                                 )*/
                                // tapCardInputView.separatorcard2.visibility = View.VISIBLE
                                separator1?.visibility = View.VISIBLE
                                if (PaymentDataSource.getDefaultCardHolderName()
                                        ?.isNotEmpty() == true || PaymentDataSource.getDefaultCardHolderName()
                                        ?.isNotBlank() == true
                                ) {
                                    tapCardInputView.setCardHolderName(PaymentDataSource.getDefaultCardHolderName())
                                    //  tapInlineCardSwitch?.visibility = View.VISIBLE
                                    tapInlineCardSwitch?.fadeVisibility(View.VISIBLE)
                                    // tapCardInputView.separatorcard2.visibility = View.VISIBLE
                                }
                            }
                            println("cvvNumberPrev" + cvvNumberPrev)

                            cvvNumberPrev = s.toString()
                        } else tapInlineCardSwitch?.visibility = View.GONE
                    }

                    tapAlertView?.visibility = View.GONE

                    if (cardInputUIStatus != null && cardInputUIStatus == CardInputUIStatus.SavedCard) {
                        println("avedCardsModel?.brand?.name"+savedCardsModel?.brand?.name)
                        tapAlertView?.fadeVisibility(View.GONE, 500)
                        //  tapCardInputView.setVisibilityOfHolderField(false)
                        tapCardInputView.holderNameEnabled = false
                        separator1?.visibility = View.GONE
                        //tapCardInputView.separatorcard2.visibility = View.INVISIBLE
                        tapInlineCardSwitch?.visibility = View.GONE

                        if (s.trim().length == 3 || s.trim().length == 4) {
                            if(savedCardsModel!=null){

                                onPaymentCardComplete.onPayCardSwitchAction(
                                    true, PaymentType.SavedCard , savedCardsModel?.brand?.name
                                )
                                cardNumber?.let {
                                    expiryDate?.let { it1 ->
                                        onPaymentCardComplete.onPayCardCompleteAction(
                                            true, PaymentType.SavedCard,
                                            it, it1, cvvNumber!!, cardHolderName, savedCardsModel?.brand?.name ,savedCardsModel
                                        )
                                    }
                                }
                            }else {
                                onPaymentCardComplete.onPayCardSwitchAction(
                                    true, PaymentType.CARD , savedCardsModel?.brand?.name
                                )
                                cardNumber?.let {
                                    expiryDate?.let { it1 ->
                                        onPaymentCardComplete.onPayCardCompleteAction(
                                            true, PaymentType.CARD,
                                            it, it1, cvvNumber!!, cardHolderName, savedCardsModel?.brand?.name ,null
                                        )
                                    }
                                }
                            }
                            tapAlertView?.fadeVisibility(View.GONE, 500)
                            CustomUtils.hideKeyboardFrom(context, view)
                            Bugfender.d(
                                CustomUtils.tagEvent,
                                "Finished valid raw card data for:" + PaymentType.CARD
                            )
                        }

                    }
                    /*  cardNumber
                          ?.let { logicTosetImageDynamic(CardBrand.fromString(cardNumber), it) }*/
                    cvvNumberPrev = s.toString()
                } else {

                    if(cardInputUIStatus == CardInputUIStatus.NormalCard)
                        onPaymentCardComplete.onPayCardSwitchAction(
                        false, PaymentType.CARD
                    )else {
                        onPaymentCardComplete.onPayCardSwitchAction(
                            false, PaymentType.SavedCard
                        )

                    }
                    //  tapAlertView?.visibility = View.VISIBLE


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

    @RequiresApi(Build.VERSION_CODES.N)
    fun cardNumAfterTextChangeListener(charSequence: CharSequence?, textWatcher: TextWatcher) {
        val card = CardValidator.validate(charSequence.toString())
        // var card:DefinedCardBrand?=null
        // if(tapCardInputView.fullCardNumber!=null)
        //  card  = CardValidator.validate(tapCardInputView.fullCardNumber)

        if (charSequence != null) {
            baseLayoutManager.resetViewHolder()


            if (charSequence.length <=2) {
                if (card.cardBrand != null)
                    logicTosetImageDynamic(card.cardBrand, charSequence.toString())

            }

            if (charSequence.length > 2) {
                callCardBinNumberApi(charSequence, textWatcher)

            }else {
                tabLayout.resetBehaviour()
                PaymentDataSource.setBinLookupResponse(null)
            }
        }
        charSequence?.let {
            if (charSequence.isNullOrEmpty()) {
                tapAlertView?.fadeVisibility(View.GONE, 500)
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

                    if(charSequence.length == 8) // added length check to avoid flickering
                    setTabLayoutBasedOnApiResponse(binLookupResponse, card)
                } else {
                    checkAllowedCardTypes(binLookupResponse)
                    if(charSequence.length == 8)// added length check to avoid flickering
                    setTabLayoutBasedOnApiResponse(binLookupResponse, card)
                }
            }
            println("charSequence.le" + charSequence.length)
            if (charSequence.length == 19) {
                fullCardNumber = charSequence.toString()
            }
            if (card != null && card.cardBrand != null && card.cardBrand.name == CardBrand.americanExpress.name) {
                fullCardNumber = charSequence.toString()
            }
            /**
             * we will get the full card number
             */
            cardNumber = charSequence.toString()



            lastCardInput = it.toString()
            shouldShowScannerOptions = it.isEmpty()
            controlScannerOptions()
            cardBrandDetection(charSequence.toString())
            if (card != null) checkValidationState(card,charSequence.toString(),textWatcher)
        }
         if(charSequence.toString().isEmpty()){
             tapCardInputView.setSingleCardInput(
                 CardBrandSingle.Unknown, null
             )
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
            if (itemsCardsList.isNotEmpty()) {
                for (i in itemsCardsList.indices) {
                    //   println("binLookupResponse>>>"+binLookupResponse?.cardBrand?.name)
                    //    println("selectedImageURL>>>"+itemsCardsList[i].selectedImageURL)
                    if (binLookupResponse?.cardBrand?.name?.let {
                            itemsCardsList[i].selectedImageURL.contains(
                                it
                            )
                        } == true  ) {
                        selectedImageURL = itemsCardsList[i].selectedImageURL

                        tapCardInputView.setSingleCardInput(
                            CardBrandSingle.fromCode(
                                binLookupResponse?.cardBrand.toString()
                            ), selectedImageURL
                        )

                        tabLayout?.visibility = View.GONE
                        tapAlertView?.fadeVisibility(View.GONE, 500)

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

                        tabLayout?.visibility = View.GONE
                      tapAlertView?.fadeVisibility(View.GONE, 500)

                    }
                }
            }

//            tabLayout.setUnselectedAlphaLevel(0.5f)

        }

        // PaymentDataSource.setBinLookupResponse(null)
    }


    /*
    This function for calling api to validate card number after 6 digit
     */
    @RequiresApi(Build.VERSION_CODES.N)
    private fun callCardBinNumberApi(s: CharSequence, textWatcher: TextWatcher) {
        if (s.trim().toString().replace(" ", "").length == BIN_NUMBER_LENGTH) {
            cardViewModel.processEvent(
                CardViewEvent.RetreiveBinLookupEvent,
                CheckoutViewModel(), null, s.trim().toString().replace(" ", ""), null, null
            )

        }
        tapCardInputView.removeCardNumberTextWatcher(textWatcher)
        tapCardInputView.setCardNumberTextWatcher(textWatcher)
    }


    private fun checkValidationState(card: DefinedCardBrand, charSequence :String,textWatcher :TextWatcher) {
        if (card.cardBrand != null)
            when (card.validationState) {

                CardValidationState.invalid -> {
                    println("cardBrand val" + card.cardBrand)
                    if (card.cardBrand != null)
                        tapCardInputView.setSingleCardInput(
                            CardBrandSingle.Unknown, null
                        )
                       // tabLayout.selectTab(card.cardBrand, false)
                    tapAlertView?.fadeVisibility(View.VISIBLE,1000)
                    tapAlertView?.alertMessage?.text =
                        (LocalizationManager.getValue("Error", "Hints", "wrongCardNumber"))
                    cardNumValidation = false

                }
                CardValidationState.incomplete -> {
                    //tapAlertView?.visibility = View.VISIBLE
                    // tapAlertView?.alertMessage?.text =
                    //    (LocalizationManager.getValue("Error", "Hints", "wrongCardNumber"))
                    // checkoutFragment.scrollView?.smoothScrollTo(0,height)
                    //checkoutFragment.scrollView?.smoothScrollTo(0,0)
                    intertabLayout.visibility = View.VISIBLE
                    tabLayout.visibility = View.VISIBLE
                    acceptedCardText.visibility = View.VISIBLE
                    cardNumValidation = false
                }
                CardValidationState.valid -> {
                    if (schema != null){
                        schema?.cardBrand?.let { tabLayout.selectTab(it, true)
                          if( PaymentDataSource.getBinLookupResponse()?.scheme?.cardBrand != null)
                              PaymentDataSource?.getBinLookupResponse()?.scheme?.cardBrand?.let { it1 ->
                                  logicTosetImageDynamic(
                                      it1, charSequence.toString())
                              }
                        }
                    }else{
                        logicTosetImageDynamic(card.cardBrand, charSequence.toString())
                        tabLayout.selectTab(card.cardBrand, true)
                    }

                    tapAlertView?.fadeVisibility(View.VISIBLE,1000)
                    tapAlertView?.alertMessage?.text =
                        (LocalizationManager.getValue("Warning", "Hints", "missingExpiryCVV"))

                    // lastFocusField =CardInputListener.FocusField.FOCUS_EXPIRY
                    /*tapCardInputView.setCardNumberMasked(cardNumber?.let {
                        maskCardNumber(
                            it
                        )
                    })*/

                    intertabLayout.visibility = View.GONE
                    tabLayout.visibility = View.GONE
                    acceptedCardText.visibility = View.INVISIBLE
                    cardNumValidation = true
                }
                else -> {
                    if (cardInputUIStatus?.equals(CardInputUIStatus.SavedCard) == true) {
                        tapAlertView?.visibility = View.GONE
                    } else {
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
            tapAlertView?.fadeVisibility(View.GONE, 500)
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
        //  position?.let { swapInputViews(it) }
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
        // checkForFocus()
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
    override fun cardFormHasFocus(hasFocus: Boolean) {

       checkoutViewModel.resetViewHolder()
    }

    override fun cvvFieldHasFocus(hasFocus: Boolean) {
       
        if(hasFocus){
            tapAlertView?.fadeVisibility(View.GONE, 500)
        }else {
            if(cvvNumber?.length!! <3) {
                tapAlertView?.fadeVisibility(View.VISIBLE)
                val alertMessage: String =
                    LocalizationManager.getValue("Warning", "Hints", "missingCVV")
                tapAlertView?.alertMessage?.text = alertMessage.replace("%i", "3")
            }

        }
    }


    override fun onExpirationComplete() {
        /*if (cardInputUIStatus == CardInputUIStatus.SavedCard) {
            onFocusChange(CardInputListener.FocusField.FOCUS_CVC)
        }*/
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
        println("imageURLApi>>" + imageURLApi)
//        tabLayout.resetBehaviour()

        val itemsMobilesList = ArrayList<SectionTabItem>()
        itemsCardsList = ArrayList<SectionTabItem>()
        intertabLayout.removeAllTabs()
        //  intertabLayout.setPadding(-50, 0, 0, 0)
        PaymentDataSource.setBinLookupResponse(null)
//        tabLayout.changeTabItemAlphaValue(1f)
        /**
         * Sorted cardpayment types based on orderBY*/
        decideTapSelection(imageURLApi.sortedBy { it.orderBy }, itemsMobilesList, itemsCardsList)
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
            if (CustomUtils.getCurrentTheme() != null && CustomUtils.getCurrentTheme()
                    .contains("dark")
            ) {
                imageURL = imageURLApi[i].logos?.dark?.png.toString()
            } else imageURL = imageURLApi[i].logos?.light?.png.toString()
            // imageURL = imageURLApi[i].image.toString()
            paymentType = imageURLApi[i].paymentType
            cardBrandType = if (imageURLApi[i].brand == null) {
                "unknown"
            } else
                imageURLApi[i].brand.toString()

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
            println("imageURL are" + cardBrandType)
         
            itemsCardsList.add(
                SectionTabItem(
                    imageURL,
                    imageURL,
                    CardBrand.fromString(cardBrandType)
                )
            )
        }
    }


    override fun showHideClearImage(show: Boolean) {
        if (show) {
            // closeButton?.visibility = View.VISIBLE hiding for mobile view
        } else {
            //  closeButton?.visibility = View.GONE
            tapAlertView?.fadeVisibility(View.GONE, 500)
        }
    }

    fun getCard(): CreateTokenCard? {
        val number: String? = fullCardNumber
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

    fun getPreTypedCardData(): NormalCardData? {
        val number: String? = fullCardNumber
        val expiryDate: String? = expiryDatePrev
        val cvc: String? = cvvNumberPrev
        val holderName: String? = cardHolderNamePrev

        return if (number == null || expiryDate == null || cvc == null) {
            null
        } else {
            val dateParts: List<String> = expiryDate.split("/")

            return dateParts.get(0).let {
                cvvNumberPrev?.let { it1 ->
                    NormalCardData(
                        number.replace(" ", ""),
                        it,
                        dateParts[1],
                        it1,
                        holderName
                    )
                }
            }
        }

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
          //  tapCardInputView.clear()
            tabLayout.resetBehaviour()
            /*CustomUtils.showDialog(
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
            )*/
            CustomUtils.showDialog(
                "alert",
                "cardnot supported",
                context,
                1,
                baseLayoutManager,
                null,
                null,
                true
            )

        }
    }


    fun maskCardNumber(cardInput: String): String {
        val maskLen: Int = cardInput.length - 4
        if (maskLen <= 0) return cardInput // Nothing to mask
        return (cardInput).replaceRange(0, maskLen, "•••• ")
    }

    private fun maskCardNumber2(cardInput: String): String {
        val maskLen: Int = cardInput.length - 4
        if (maskLen <= 0) return cardInput // Nothing to mask
        return (cardInput).replaceRange(0, 6, "•••• ")
    }

    /**
     * here when user enter CVV in input Credit Card
     */
    fun setDataForSavedCard(_savedCardsModel: SavedCard, cardInputUIStatus: CardInputUIStatus) {
        println("cardInputUIStatus>>" + cardInputUIStatus)
        tapCardInputView.holderNameEnabled = false
        // tapCardInputView.setVisibilityOfHolderField(false)
        this.cardInputUIStatus = cardInputUIStatus
        this.savedCardsModel = _savedCardsModel
        val cardModel = company.tap.cardinputwidget2.Card(
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
            company.tap.cardinputwidget2.CardBrand.fromCardNumber(_savedCardsModel.firstSix),
            null,
            null,
            _savedCardsModel.currency,
            _savedCardsModel.customer,
            null,
            null
        )

        expiryDate = _savedCardsModel.expiry?.month + "/" + _savedCardsModel.expiry?.year
        println("expiryDate saved" + expiryDate)
        //  tapCardInputView.isSavedCard = true
        /* tapCardInputView.updateIconCvc(
             false,
             cvvNumber,
             company.tap.cardinputwidget.CardBrand.fromCardNumber(_savedCardsModel.firstSix)
         )*/
        tapCardInputView.setSavedCardDetails(cardModel, cardInputUIStatus)
        /*  val alertMessage:String = LocalizationManager.getValue("Warning", "Hints", "missingCVV")
           tapAlertView?.alertMessage?.text =alertMessage.replace("%i","3")

           tapAlertView?.visibility =View.VISIBLE*/
        if (CustomUtils.getCurrentTheme() != null && CustomUtils.getCurrentTheme()
                .contains("dark")
        ) {
            tapCardInputView.setSingleCardInput(
                CardBrandSingle.fromCode(
                    company.tap.cardinputwidget2.CardBrand.fromCardNumber(_savedCardsModel.firstSix)
                        .toString()
                ), _savedCardsModel.logos?.dark?.png
            )
        } else {
            tapCardInputView.setSingleCardInput(
                CardBrandSingle.fromCode(
                    company.tap.cardinputwidget2.CardBrand.fromCardNumber(_savedCardsModel.firstSix)
                        .toString()
                ), _savedCardsModel.logos?.light?.png
            )
        }
        tapInlineCardSwitch?.visibility = View.GONE
        separator1?.visibility = View.GONE
        acceptedCardText.visibility = View.INVISIBLE
        contactDetailsView?.visibility = View.GONE
        shippingDetailView?.visibility = View.GONE
        //  intertabLayout.visibility = View.GONE
        tabLayout.fadeVisibility(View.GONE, 2000)
        //Added for opening as soon as cvv focus
        CustomUtils.showKeyboard(context)
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

    fun resetPaymentCardView() {


        tapCardInputView.clearFocus()
        tapCardInputView.clear()
        closeButton?.visibility = View.GONE
        controlScannerOptions()
        cardInputUIStatus = CardInputUIStatus.NormalCard
        checkoutViewModel.setTitleNormalCard()
        /*tapCardInputView.setSingleCardInput(
              CardBrandSingle.Unknown, null
          )*/
        tapInlineCardSwitch?.visibility = View.GONE
        // tapCardInputView.separatorcard2.visibility = View.INVISIBLE
        separator1?.visibility = View.GONE
        // resetCardBrandIcon()
        if (PaymentDataSource.getBinLookupResponse() != null) {
            PaymentDataSource.setBinLookupResponse(null)

        }
        if (getPreTypedCardData()?.cardholderName != null) {
            tapCardInputView.setVisibilityOfHolderField(false)
            tapCardInputView.holderNameEnabled = false
        }
        if (getPreTypedCardData() != null) {
            getPreTypedCardData()?.cardholderName = null
            getPreTypedCardData()?.cardNumber = null
            getPreTypedCardData()?.expirationYear = null
            getPreTypedCardData()?.expirationMonth = null
            getPreTypedCardData()?.cvc = null

        }


        /*  if(tapCardInputView.fullCardNumber!=null){
              tapCardInputView.fullCardNumber= null
              tabLayout.resetBehaviour()
          }*/

        tapInlineCardSwitch?.saveForOtherCheckBox?.isChecked = false
        tapInlineCardSwitch?.switchSaveCard?.isChecked = false
        contactDetailsView?.visibility = View.GONE
        shippingDetailView?.visibility = View.GONE
        closeButton?.visibility = View.GONE
        // tapCardInputView.setVisibilityOfHolderField(false)
        tapCardInputView.holderNameEnabled = false
        checkoutViewModel.incrementalCount = 0
        //  tabLayout.visibility =View.VISIBLE
        //   intertabLayout.visibility =View.VISIBLE
        allFieldsValid = false
        tapAlertView?.fadeVisibility(View.GONE, 500)
        //  acceptedCardText.visibility = View.VISIBLE
        tabLayout.fadeVisibility(View.VISIBLE)
        acceptedCardText.fadeVisibility(View.VISIBLE)
        intertabLayout.fadeVisibility(View.VISIBLE)


    }

    fun logicTosetImageDynamic(card: CardBrand, cardCharSeq: String) {
        for (i in itemsCardsList.indices) {

            if (itemsCardsList[i].selectedImageURL != null && itemsCardsList[i].selectedImageURL.contentEquals(
                    "dark"
                )
            ) {
                val iconStr = itemsCardsList[i].selectedImageURL.replace(
                    "https://tap-assets.b-cdn.net/payment-options/v2/dark/",
                    ""
                )
                if (iconStr.replace(".png", "").toLowerCase().contains(card.name.toLowerCase())) {
                    tapCardInputView.setSingleCardInput(
                        CardBrandSingle.fromCode(card.name), itemsCardsList[i].selectedImageURL
                    )

                }
            } else {
                println("itemsCardsList[i] light" + itemsCardsList[i].selectedImageURL)
                val iconStr = itemsCardsList[i].selectedImageURL.replace(
                    "https://tap-assets.b-cdn.net/payment-options/v2/light/",
                    ""
                )
                if (iconStr.replace(".png", "").replace("_", "").toLowerCase()
                        .contains(card.name.toLowerCase())
                ) {
                    tapCardInputView.setSingleCardInput(
                        CardBrandSingle.fromCode(card.name), itemsCardsList[i].selectedImageURL
                    )

                }
            }

        }
    }

    fun logicForImageOnCVV(card: CardBrand, cardCharSeq: String) {

        println("cardSchema is" + card.name)
        //TODO 19MAR
        for (i in itemsCardsList.indices) {

            if (itemsCardsList[i].selectedImageURL != null && itemsCardsList[i].selectedImageURL.contentEquals(
                    "dark"
                )
            ) {
                val iconStr = itemsCardsList[i].selectedImageURL.replace(
                    "https://tap-assets.b-cdn.net/payment-options/v2/dark/",
                    ""
                )

                if (iconStr.replace(".png", "").replace("_", "").toLowerCase()
                        .contains(card.name.toLowerCase())
                ) {
                    tapCardInputView.setCardBrandUrl(itemsCardsList[i].selectedImageURL)

                }
            } else {
                println("itemsCardsList[i] light" + itemsCardsList[i].selectedImageURL)
                val iconStr = itemsCardsList[i].selectedImageURL.replace(
                    "https://tap-assets.b-cdn.net/payment-options/v2/light/",
                    ""
                )
                if (iconStr.replace(".png", "").replace("_", "").toLowerCase()
                        .contains(card.name.toLowerCase())
                ) {
                    tapCardInputView.setCardBrandUrl(itemsCardsList[i].selectedImageURL)
                    saveLocalBinLookup = null
                }
            }
        }
    }

    /*   override fun onClick(v: View?) {


           if(v?.id == R.id.backView) {

               Toast.makeText(context, "clicking!!", Toast.LENGTH_SHORT).show()
               cardInputUIStatus = CardInputUIStatus.NormalCard
               tabLayout.resetBehaviour()
               tapCardInputView.clear()
               closeButton?.visibility = View.GONE
               controlScannerOptions()
               cardInputUIStatus = CardInputUIStatus.NormalCard
               *//* tapCardInputView.setSingleCardInput(
                CardBrandSingle.Unknown, null
            )*//*
            tapInlineCardSwitch?.visibility = View.GONE
            //  tapCardInputView.separatorcard2.visibility = View.INVISIBLE
            // resetCardBrandIcon()
            tapAlertView?.visibility = View.GONE
            checkoutViewModel.resetCardSelection()

            checkoutViewModel.isSavedCardSelected = false
            //resetPaymentCardView()
            intertabLayout.visibility = View.VISIBLE
            tabLayout.visibility = View.VISIBLE
            acceptedCardText.visibility = View.VISIBLE
            checkoutViewModel.resetViewHolder()

        }
    }*/

    fun hideViewONScanNFC() {
        intertabLayout.visibility = View.GONE
        tabLayout.visibility = View.GONE
        acceptedCardText.visibility = View.INVISIBLE
        tapCardInputView.onTouchView()

    }



}
