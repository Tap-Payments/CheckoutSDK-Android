package company.tap.checkout.internal.viewholders

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
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
import company.tap.checkout.internal.enums.ThemeMode
import company.tap.checkout.internal.interfaces.BaseLayoutManager
import company.tap.checkout.internal.interfaces.PaymentCardComplete
import company.tap.checkout.internal.utils.*
import company.tap.checkout.internal.viewmodels.CheckoutViewModel
import company.tap.checkout.open.data_managers.PaymentDataSource
import company.tap.checkout.open.enums.CardType
import company.tap.nfcreader.open.reader.TapEmvCard
import company.tap.nfcreader.open.utils.TapNfcUtils
import company.tap.tapcardvalidator_android.CardBrand
import company.tap.tapcardvalidator_android.CardValidationState
import company.tap.tapcardvalidator_android.CardValidator
import company.tap.tapcardvalidator_android.DefinedCardBrand
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapuilibraryy.themekit.ThemeManager
import company.tap.tapuilibraryy.uikit.atoms.TapCurrencyControlWidget
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


const val AMERICAN_EXPRESS_VALUE = "AMERICAN_EXPRESS"
const val SAUDI_CURRENCY = "SAR"
const val MADA_SCHEME = "MADA"


/**
 * This class needed to be refactored ASAP :/
 */
@RequiresApi(Build.VERSION_CODES.N)
class PaymentInlineViewHolder(
    private val context: Context,
    private val checkoutViewModel: CheckoutViewModel,
    private val onPaymentCardComplete: PaymentCardComplete,
    private val switchViewHolder: SwitchViewHolder?,
    private val baseLayoutManager: BaseLayoutManager,
    private val cardViewModel: CardViewModel,
) : TapBaseViewHolder,
    TapSelectionTabLayoutInterface, CardInputListener, TapPaymentShowHideClearImage {
    override val view: View =
        LayoutInflater.from(context).inflate(R.layout.payment_inline_viewholder, null)
    override val type = SectionType.PAYMENT_INPUT
    var tabLayout: TapSelectionTabLayout = view.findViewById(R.id.sections_tablayout)
    var intertabLayout: TabLayout = tabLayout.findViewById(R.id.tab_layout)
    val tapCurrencyControlWidgetPaymentInline by lazy {
        tapPaymentInput?.findViewById<TapCurrencyControlWidget>(
            R.id.tap_currency_widget_payment_inline
        )
    }


    val paymentInputContainer: LinearLayout


    var selectedType = PaymentTypeEnum.card
    private var shouldShowScannerOptions = true
    private var lastFocusField = CardInputListener.FocusField.FOCUS_CARD
    private var lastCardInput = ""
    private var cardScannerBtn: ImageView? = null
    var savedCardsModel: SavedCard? = null


    var tapCardInputView: InlineCardInput2
    internal var tapMobileInputView: TapMobilePaymentView
    private var linearLayoutPay: LinearLayout? = null
    private var tapSeparatorViewLinear: LinearLayout? = null
    private var tabPosition: Int? = null
    var tapAlertView: TapAlertView? = null
    var mainLinear: LinearLayout? = null
    var inlineCardView: CardView? = null

    private var imageURL: String = ""
    private var selectedImageURL: String = ""

    //  private  var paymentType: PaymentTypeEnum ?= null
    private var paymentType: PaymentType? = null
    lateinit var cardBrandType: String
    var cardNumber: String? = null
    private var fullCardNumber: String? = null
    var expiryDate: String? = null
    var cvvNumber: String? = null
    var cardHolderName: String? = null
    var cardBrandInString: String? = null
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
    var touchLayer: View
    var outerFrame: LinearLayout

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
    var isCVCLengthMax: Boolean? = false
    var isDisabledBrandSelected: Boolean? = false
    var _enabledPaymentsList: MutableList<PaymentOption>? = ArrayList()
    var _disabledPaymentList: MutableList<PaymentOption>? = ArrayList()

    init {

        tapMobileInputView = TapMobilePaymentView(context, null)
        tapMobileInputView.setTapPaymentShowHideClearImage(this)
        tapCardInputView = InlineCardInput2(context, null)
        nfcButton = tapCardInputView.findViewById(R.id.nfc_button)
        cardBrandView = tapCardInputView.findViewById(R.id.card_brand_view)
        scannerButton = tapCardInputView.findViewById(R.id.card_scanner_button)
        closeButton = tapCardInputView.findViewById(R.id.clear_text)
        tapPaymentInput = view.findViewById(R.id.tap_payment_input_layout)
        separator1 = tapCardInputView.findViewById(R.id.separator_1)
        tapAlertView = tapPaymentInput?.findViewById(R.id.alertView)
        mainLinear = tapPaymentInput?.findViewById(R.id.mainLinear)
        inlineCardView = tapPaymentInput?.findViewById(R.id.inline_CardView)
        paymentInputContainer = view.findViewById(R.id.payment_input_layout)
        tapCardInputView.backArrow.visibility = View.GONE
        contactDetailsView = view.findViewById(R.id.contact_detailsView)
        shippingDetailView = view.findViewById(R.id.ship_detailsView)

        tapInlineCardSwitch = tapPaymentInput?.findViewById(R.id.switch_Inline_card)
        tapInlineCardSwitch?.brandingLayout?.visibility = View.GONE
        tapPaymentInput?.separator?.visibility = View.GONE
        touchLayer = tapCardInputView?.findViewById(R.id.touch_layer_inside)
        outerFrame = tapCardInputView?.findViewById(R.id.linear_payout)
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
        tabLayout?.fadeVisibility(View.VISIBLE)

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
        initializeCardForm()
        initializeIcons()
        initCustomerDetailView()
        tapCardInputView.setCardInputListener(this)


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
            dismsisCurrencyWidget()
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


            tapInlineCardSwitch?.saveForOtherCheckBox?.isChecked = false
            tapInlineCardSwitch?.switchSaveCard?.isChecked = false
            contactDetailsView?.visibility = View.GONE
            shippingDetailView?.visibility = View.GONE
            closeButton?.visibility = View.GONE
            tapCardInputView.setVisibilityOfHolderField(false)
            tapCardInputView.holderNameEnabled = false
            checkoutViewModel.incrementalCount = 0
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
            getPreTypedCardData()?.cvc
            savedCardsModel = null
            prevSetCardBrand = CardBrand.unknown
        }
    }


    private fun initializeCardForm() {
        cardScannerBtn = view.findViewById(R.id.card_scanner_button)
        nfcButton = view.findViewById(R.id.nfc_button)
        cardScannerBtn?.visibility = View.VISIBLE
        linearLayoutPay = view.findViewById(R.id.linear_paylayout)
        closeButton?.visibility = View.GONE


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
        // tapInlineCardSwitch?.switchSaveCard?.isChecked = true
        tapInlineCardSwitch?.visibility = View.GONE
        closeButton?.visibility = View.GONE
        // tapCardInputView.setVisibilityOfHolderField(false
        tapCardInputView.holderNameEnabled = false
        checkoutViewModel.incrementalCount = 0
    }


    @SuppressLint("ClickableViewAccessibility")
    private fun initCardInput() {
        resetCardBrandIcon()
        tapInlineCardSwitch?.visibility = View.GONE
        tapPaymentInput?.separator?.visibility = View.GONE

        addViewsToPaymentViewContainer()
        tapCardInputView.clearFocus()
        cardNumberWatcher()
        expiryDateWatcher()
        cvcNumberWatcher()
        cardHolderNameWatcher()
        switchCheckedState()


        tapCardInputView.backArrow.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                checkoutViewModel.setTitleNormalCard()
                if (getPreTypedCardData() != null) setPrevTypedCard()
                else {

                    clearTextInput()
                }

                return false
            }
        })

    }

    fun clearTextInput() {
        tabLayout.resetBehaviour()
        cardInputUIStatus = CardInputUIStatus.NormalCard
        tabLayout.resetBehaviour()
        tabLayout.getChildAt(0).minimumHeight = 2
        tapCardInputView.clear()
        closeButton?.visibility = View.GONE
        controlScannerOptions()
        tapInlineCardSwitch?.visibility = View.GONE
        tapAlertView?.fadeVisibility(View.GONE, 500)
        checkoutViewModel.resetCardSelection()
        checkoutViewModel.isSavedCardSelected = false
        tabLayout.fadeVisibility(View.VISIBLE)
        intertabLayout.fadeVisibility(View.VISIBLE)
        acceptedCardText.fadeVisibility(View.VISIBLE)
        checkoutViewModel.dismisControlWidget()
        checkoutViewModel.resetViewHolder()
        expiryDate = null
        cvvNumber = null
        cardHolderName = null
        fullCardNumber = null

        separator1?.visibility = View.GONE
        if (PaymentDataSource.getBinLookupResponse() != null) {
            PaymentDataSource.setBinLookupResponse(null)

        }

        tapInlineCardSwitch?.saveForOtherCheckBox?.isChecked = false
        tapInlineCardSwitch?.switchSaveCard?.isChecked = false
        contactDetailsView?.visibility = View.GONE
        shippingDetailView?.visibility = View.GONE
        closeButton?.visibility = View.GONE
        tapCardInputView.setVisibilityOfHolderField(false)
        tapCardInputView.holderNameEnabled = false
        checkoutViewModel.incrementalCount = 0
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
        getPreTypedCardData()?.cvc
        savedCardsModel = null
        prevSetCardBrand = CardBrand.unknown

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
        println("getPreTypedCardData()" + getPreTypedCardData()?.cvc)
        val cardModel = company.tap.cardinputwidget2.Card(
            number = getPreTypedCardData()?.cardNumber,
            cvc = getPreTypedCardData()?.cvc,
            expMonth = getPreTypedCardData()?.expirationMonth?.toInt(),
            expYear = getPreTypedCardData()?.expirationYear?.toInt(),
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
            //  tapInlineCardSwitch?.switchSaveCard?.isChecked = true
            tapCardInputView.setVisibilityOfHolderField(true)
            tapCardInputView.holderNameEnabled = true
            separator1?.visibility = View.VISIBLE
            //  tapInlineCardSwitch?.switchSaveCard?.isChecked = true

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
        cardBrandInString = cardModel.brand.name

        contactDetailsView?.visibility = View.GONE
        shippingDetailView?.visibility = View.GONE
        tabLayout.fadeVisibility(View.GONE, 2000)
        intertabLayout.fadeVisibility(View.GONE, 2000)
        savedCardsModel = null
        if (getPreTypedCardData() != null && getPreTypedCardData()?.cvc != null) {
            tapAlertView?.fadeVisibility(View.GONE, 2000)
            tapInlineCardSwitch?.fadeVisibility(View.VISIBLE)
            onCvcComplete()
            checkoutViewModel.isSavedCardSelected = false
        }
        //Added for opening as soon as cvv focus
        else CustomUtils.showKeyboard(context)
    }


    fun setNFCCardData(emvCard: TapEmvCard, month: Int, year: Int) {
        cardInputUIStatus = CardInputUIStatus.NormalCard
        val updateCardString: String = emvCard.cardNumber?.trim().toString()
            .substring(0, 6) + emvCard.cardNumber?.length?.minus(4)
            ?.let {
                emvCard.cardNumber?.trim().toString().substring(
                    it
                )
            }
        println("updateCardString" + updateCardString)

        val cardModel = company.tap.cardinputwidget2.Card(
            emvCard?.cardNumber,
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
            //  tapInlineCardSwitch?.switchSaveCard?.isChecked = true
            tapCardInputView.setVisibilityOfHolderField(true)
            tapCardInputView.holderNameEnabled = true
            separator1?.visibility = View.VISIBLE
            //   tapInlineCardSwitch?.switchSaveCard?.isChecked = true

        }

        tapCardInputView.setScanNFCCardDetails(cardModel, CardInputUIStatus.NormalCard)
        /*  val alertMessage:String = LocalizationManager.getValue("Warning", "Hints", "missingCVV")
           tapAlertView?.alertMessage?.text =alertMessage.replace("%i","3")

           tapAlertView?.visibility =View.VISIBLE*/

        val card = CardValidator.validate(emvCard?.cardNumber)
        emvCard.cardNumber?.let {
            logicTosetImageDynamic(
                card.cardBrand,
                it
            )
        }


        contactDetailsView?.visibility = View.GONE
        shippingDetailView?.visibility = View.GONE
        tabLayout.fadeVisibility(View.GONE, 2000)
        intertabLayout.fadeVisibility(View.GONE, 2000)

        fullCardNumber = emvCard.cardNumber
        expiryDate = month.toString() + "/" + year.toString()
        cardHolderName = emvCard.holderFirstname
        //Added for opening as soon as cvv focus
        CustomUtils.showKeyboard(context)

    }

    fun setCardScanData(tapCard: TapCard, month: Int, year: Int) {
        cardInputUIStatus = CardInputUIStatus.NormalCard
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
            //  tapInlineCardSwitch?.switchSaveCard?.isChecked = true
            tapCardInputView.setVisibilityOfHolderField(true)
            tapCardInputView.holderNameEnabled = true
            separator1?.visibility = View.VISIBLE
            // tapInlineCardSwitch?.switchSaveCard?.isChecked = true
            cardHolderName = tapCard.cardHolder

        }
        println("cardNumValidation is" + cardNumValidation)

        tapCardInputView.setScanNFCCardDetails(cardModel, CardInputUIStatus.NormalCard)
        /*  val alertMessage:String = LocalizationManager.getValue("Warning", "Hints", "missingCVV")
           tapAlertView?.alertMessage?.text =alertMessage.replace("%i","3")

           tapAlertView?.visibility =View.VISIBLE*/

        val card = CardValidator.validate(tapCard?.cardNumber)
        tapCard.cardNumber?.let {
            logicTosetImageDynamic(
                card.cardBrand,
                it
            )
        }


        contactDetailsView?.visibility = View.GONE
        shippingDetailView?.visibility = View.GONE
        // intertabLayout.visibility = View.GONE
        tabLayout?.fadeVisibility(View.GONE, 2000)
        intertabLayout?.fadeVisibility(View.GONE, 2000)
        println("tapCard val" + tapCard.cardNumber.trim())
        fullCardNumber = tapCard.cardNumber
        expiryDate = month.toString() + "/" + year.toString()
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

    fun setCardBrandViewIcon() {
        cardBrandView?.iconView?.setImageResource(R.drawable.card_icon_dark)

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
                        if (isCardEnterdShouldBeDisabledPaymentOptions()) {
                            tapInlineCardSwitch?.cardviewSwitch?.visibility = (View.GONE)
                        } else {
                            tapInlineCardSwitch?.fadeVisibility(View.VISIBLE)
                        }
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
                    tapCurrencyControlWidgetPaymentInline?.fadeVisibility(View.GONE)
                    tapInlineCardSwitch?.fadeVisibility(View.GONE, 2000)
                }
            }

            override fun afterTextChanged(s: Editable?) {

                println("PaymentDataSource?.getBinLookupResponse()" + PaymentDataSource?.getBinLookupResponse())
                println("cardHolderName>>" + cardHolderName)
                //On Details complete
                if (s.toString().length > 3) {
                    if (isCVCLengthMax == true)
                        cardNumber?.let {
                            expiryDate?.let { it1 ->
                                if (PaymentDataSource.getBinLookupResponse() != null) {
                                    cardBrandInString = PaymentDataSource.getBinLookupResponse()?.scheme?.cardBrand?.rawValue
                                    doOnCurrencySupported {cardBrand ->
                                        onPaymentCardComplete.onPayCardCompleteAction(
                                            true,
                                            PaymentType.CARD,
                                            it,
                                            it1,
                                            cvvNumber,
                                            cardHolderName,
                                            cardBrand ?: cardBrandInString
                                        )
                                    }
                                }

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
        paymentInputContainer.addView(tapCardInputView)


    }

    private fun cardNumberWatcher() {
        tapCardInputView.setCardHint(
            LocalizationManager.getValue(
                "cardNumberPlaceHolder",
                "TapCardInputKit"
            )
        )
        if (tapCardInputView.cardFormHasFocus) checkoutViewModel.resetViewHolder()
        tapCardInputView.setCardNumberTextWatcher(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {


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


                //This was added for a purpose in regards to logic
               if (s != null) {
                    if (s.length >= 19) afterValidation()
                }


            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                checkoutViewModel.resetViewHolder()


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

                    tabLayout?.fadeVisibility(View.GONE, 500)
                    intertabLayout?.fadeVisibility(View.GONE, 500)
                    acceptedCardText?.fadeVisibility(View.GONE, 500)

                }
                if (s?.length == 0) {
                    intertabLayout.visibility = View.VISIBLE
                    tabLayout.visibility = View.VISIBLE
                    acceptedCardText.visibility = View.VISIBLE
                }
                mPreviousCount = count;


                if (cardInputUIStatus != CardInputUIStatus.SavedCard) {
                    onCardTextChange(s)
                    cardNumAfterTextChangeListener(s.toString().trim(), this)

                }

            }
        })
    }

    private fun afterValidation() {
        if (!fullCardNumber.isNullOrBlank() && !fullCardNumber.isNullOrEmpty() && cardNumValidation && !expiryDate.isNullOrBlank() && !expiryDate.isNullOrEmpty() && !cvvNumber.isNullOrBlank() && !cvvNumber.isNullOrEmpty()
        ) {

            if (PaymentDataSource.getCardHolderNameShowHide()) {
                tapCardInputView.setVisibilityOfHolderField(PaymentDataSource.getCardHolderNameShowHide())
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
                if (!tapCardInputView.isExpDateValid) {
                    checkoutViewModel.unActivateActionButton()
                    tapInlineCardSwitch?.visibility = View.GONE
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

            println("isExpDateValid>>>" + tapCardInputView.isExpDateValid)
            if (s.length >= 5) {
                if (cardInputUIStatus?.equals(CardInputUIStatus.SavedCard) == true) {
                    tapAlertView?.fadeVisibility(View.GONE, 500)
                } else {
                    expiryDatePrev = s.toString()
                    if (tapCardInputView.isExpDateValid) {
                        tapAlertView?.fadeVisibility(View.VISIBLE)
                        val alertMessage: String =
                            LocalizationManager.getValue("Warning", "Hints", "missingCVV")
                        cardBrandInString =
                            PaymentDataSource.getBinLookupResponse()?.scheme?.cardBrand?.rawValue
                        Log.e("card", cardBrandInString.toString())
                        if (cardBrandInString == "AMERICAN_EXPRESS") {
                            tapAlertView?.alertMessage?.text = alertMessage.replace("%i", "4")
                        } else {
                            tapAlertView?.alertMessage?.text = alertMessage.replace("%i", "3")

                        }
                    } else {
                        checkoutViewModel.unActivateActionButton()
                        tapInlineCardSwitch?.visibility = View.GONE
                        // tapAlertView?.fadeVisibility(View.GONE, 500)
                    }
                }
                // tapAlertView?.visibility = View.VISIBLE
                lastFocusField = CardInputListener.FocusField.FOCUS_CVC
                // checkoutFragment.scrollView?.scrollTo(0,height)
                //  tapInlineCardSwitch?.switchSaveCard?.isChecked = true
            } else {
                dismsisCurrencyWidget()
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
                        if (PaymentDataSource.getBinLookupResponse()?.scheme != null) {

                            if (PaymentDataSource.getBinLookupResponse()?.scheme?.cardBrand?.rawValue.toString() == MADA_SCHEME && isCurrencySelectedRelatedToSaudiReal().not()) {
                                PaymentDataSource?.getBinLookupResponse()?.let { it1 ->
                                    logicTosetImageDynamic(
                                        it1.cardBrand, s.toString()
                                    )
                                }
                            }else{
                                PaymentDataSource?.getBinLookupResponse()?.scheme?.let { it1 ->
                                    logicTosetImageDynamic(
                                        it1.cardBrand, s.toString()
                                    )
                                }
                            }
                        } else {

//                            if (fullCardNumber != null)
//                                logicForImageOnCVV(
//                                    CardValidator.validate(fullCardNumber).cardBrand,
//                                    s.toString()
//                                )
                            //}
                        }
                        acceptedCardText.visibility = View.GONE
                        tabLayout.visibility = View.GONE
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

                if ((s?.trim()?.length == 3 && isCardBrandOfTypeAmericanExpress().not()) || s?.trim()?.length == 4 && tapCardInputView.isExpDateValid) {
                    if (!PaymentDataSource.getCardHolderNameShowHide()) {
                        var paymentTyper: PaymentType? = PaymentType.CARD
                        println("savedCardsModel   hhshhs" + savedCardsModel)

                        if (savedCardsModel == null) {
                            paymentTyper = PaymentType.CARD
                        } else paymentTyper = PaymentType.SavedCard


                        if (!prevSetCardBrand?.name?.contains(CardBrand.unknown.name)!!)
                            fullCardNumber.toString().let {
                                expiryDate?.let { it1 ->
                                    cvvNumber?.let { it2 ->
                                        if (savedCardsModel?.brand?.name == null) {
                                            cardBrandInString =
                                                PaymentDataSource?.getBinLookupResponse()?.scheme?.cardBrand?.rawValue
                                        } else {
                                            cardBrandInString = savedCardsModel?.brand?.name
                                        }
                                        onPaymentCardComplete.onPayCardCompleteAction(
                                            true, paymentTyper,
                                            // it, it1, it2, null,prevSetCardBrand?.toString() , savedCardsModel
                                            it, it1, it2, null, cardBrandInString, savedCardsModel
                                        )
                                        //    tapInlineCardSwitch?.switchSaveCard?.isChecked = true

                                        tapInlineCardSwitch?.fadeVisibility(View.VISIBLE)
                                        Bugfender.d(
                                            CustomUtils.tagEvent,
                                            "Finished valid raw card data for:" + PaymentType.CARD
                                        )

                                    }
                                }
                            } else {
                            if (isCVCLengthMax == true)
                                cardNumber.toString().let {
                                    expiryDate?.let { it1 ->
                                        cvvNumber?.let { it2 ->
                                            cardBrandInString =
                                                PaymentDataSource?.getBinLookupResponse()?.scheme?.cardBrand?.rawValue

                                            doOnCurrencySupported { cardBrand ->
                                                onPaymentCardComplete.onPayCardCompleteAction(
                                                    true,
                                                    paymentTyper,
                                                    it,
                                                    it1,
                                                    it2,
                                                    null,
                                                    cardBrand ?: cardBrandInString,
                                                    savedCardsModel
                                                )
                                                if (tapCurrencyControlWidgetPaymentInline?.isVisible ==false){
                                                    tapInlineCardSwitch?.switchSaveCard?.isChecked = true
                                                    tapInlineCardSwitch?.fadeVisibility(View.VISIBLE)
                                                }
                                            }
                                        }
                                    }
                                }
                        }
                        allFieldsValid = true

                    } else {
                        if (cardInputUIStatus != null && cardInputUIStatus == CardInputUIStatus.NormalCard) {
                            if (PaymentDataSource.getCardHolderNameShowHide()) {
                                tapCardInputView.holderNameEnabled = true
                                tapCardInputView.setVisibilityOfHolderField(PaymentDataSource.getCardHolderNameShowHide())
                                onFocusChange(CardInputListener.FocusField.FOCUS_HOLDERNAME)
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
                        tapAlertView?.fadeVisibility(View.GONE, 500)
                        tapCardInputView.holderNameEnabled = false
                        separator1?.visibility = View.GONE
                        tapInlineCardSwitch?.visibility = View.GONE

                        if (s?.trim()?.length == 3 || s?.trim()?.length == 4) {
                            if (savedCardsModel != null) {
                                if (isCVCLengthMax == true)
                                    onPaymentCardComplete.onPayCardSwitchAction(
                                        true, PaymentType.SavedCard, savedCardsModel?.brand?.name
                                    )
                                // if(isCVCLengthMax == true) //check
                                cardNumber?.let {
                                    expiryDate?.let { it1 ->
                                        cardBrandInString = savedCardsModel?.brand?.name

                                        onPaymentCardComplete.onPayCardCompleteAction(
                                            true,
                                            PaymentType.SavedCard,
                                            it,
                                            it1,
                                            cvvNumber!!,
                                            cardHolderName,
                                            savedCardsModel?.brand?.name,
                                            savedCardsModel
                                        )
                                    }
                                }
                            } else {
                                cardBrandInString =
                                    PaymentDataSource?.getBinLookupResponse()?.cardBrand?.toString()
                                if (isCVCLengthMax == true)
                                    onPaymentCardComplete.onPayCardSwitchAction(
                                        true,
                                        PaymentType.CARD,
                                        PaymentDataSource?.getBinLookupResponse()?.cardBrand?.toString()
                                    )
                                if (isCVCLengthMax == true)
                                    cardNumber?.let {
                                        expiryDate?.let { it1 ->
                                            //TODO: show the currency widget
//                                        onPaymentCardComplete.onPaymentCompletedShowingCurrencyWidget(PaymentDataSource?.getBinLookupResponse()?.cardBrand?.toString().toString()
//                                        )
                                            onPaymentCardComplete.onPayCardCompleteAction(
                                                true,
                                                PaymentType.CARD,
                                                it,
                                                it1,
                                                cvvNumber!!,
                                                cardHolderName,
                                                PaymentDataSource?.getBinLookupResponse()?.cardBrand?.toString(),
                                                null
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

                    dismsisCurrencyWidget()
                    if (cardInputUIStatus == CardInputUIStatus.NormalCard)
                        onPaymentCardComplete.onPayCardSwitchAction(
                            false, PaymentType.CARD
                        ) else {
                        onPaymentCardComplete.onPayCardSwitchAction(
                            false, PaymentType.SavedCard
                        )

                    }
                    isCVCLengthMax = false


                }

            }
        })
    }


    fun isCurrencySelectedRelatedToSaudiReal(): Boolean {
        return PaymentDataSource.getSelectedCurrency()?.toUpperCase()?.toString() == SAUDI_CURRENCY
    }

    fun dismsisCurrencyWidget() {
        checkoutViewModel.dismisControlWidget()

    }

    fun isCardBrandOfTypeAmericanExpress(): Boolean {
        return PaymentDataSource?.getBinLookupResponse()?.cardBrand?.rawValue.toString() == AMERICAN_EXPRESS_VALUE
    }


    @RequiresApi(Build.VERSION_CODES.N)
    fun cardNumAfterTextChangeListener(charSequence: CharSequence?, textWatcher: TextWatcher) {
        val card = CardValidator.validate(charSequence.toString())
        // var card:DefinedCardBrand?=null
        // if(tapCardInputView.fullCardNumber!=null)
        //  card  = CardValidator.validate(tapCardInputView.fullCardNumber)

        if (charSequence != null) {
            baseLayoutManager.resetViewHolder()


            if (charSequence.length <= 2) {
                if (card.cardBrand != null)
                    logicTosetImageDynamic(card.cardBrand, charSequence.toString())

            }

            if (charSequence.length > 2) {
                callCardBinNumberApi(charSequence, textWatcher)

            } else {
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

                val binLookupResponse: BINLookupResponse? = PaymentDataSource.getBinLookupResponse()
                // println("binLookupResponse" + binLookupResponse)
                if (charSequence.length > 4) checkIfCardTypeExistInList(card.cardBrand)
                /***This a business logic required dont remove**/

                /**
                 * This is business condition based on user selection / settings **/
                if (PaymentDataSource.getCardType() != null && PaymentDataSource.getCardType() == CardType.ALL) {

                    if (charSequence.length == 8) // added length check to avoid flickering
                        doAfterSpecificTime(time = 1400L) { //delay added as response from api needs time
                            setTabLayoutBasedOnApiResponse(
                                PaymentDataSource.getBinLookupResponse(),
                                card
                            )

                        }
                } else {
                    checkAllowedCardTypes(binLookupResponse)
                    if (charSequence.length == 8)// added length check to avoid flickering
                        doAfterSpecificTime(time = 1400L) { //delay added as response from api needs time
                            setTabLayoutBasedOnApiResponse(
                                PaymentDataSource.getBinLookupResponse(),
                                card
                            )

                        }
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
            if (card != null) checkValidationState(card, charSequence.toString(), textWatcher)
        }

        //Check needed to reset the image to default instead of the brand last typed
        if (charSequence.toString().isEmpty()) {
            tapCardInputView.setSingleCardInput(
                CardBrandSingle.Unknown, null
            )
        }

    }

    private fun setTabLayoutBasedOnApiResponse(
        _binLookupResponse: BINLookupResponse?,
        cardBrand: DefinedCardBrand
    ) {

        if (_binLookupResponse?.cardBrand?.name.equals(
                _binLookupResponse?.scheme?.name?.replace("_", ""),
                ignoreCase = true
            )
        ) {
            // we will send card brand to validator
            _binLookupResponse?.cardBrand?.let { it1 ->
                tabLayout.selectTab(
                    it1,
                    true
                )

            }

            isDisabledBrandSelected =
                _disabledPaymentList?.any {
                    it.brand.equals(
                        _binLookupResponse?.cardBrand?.name,
                        ignoreCase = true
                    )
                }

            if (itemsCardsList.isNotEmpty()) {
                for (i in itemsCardsList.indices) {
                    //   println("binLookupResponse>>>"+binLookupResponse?.cardBrand?.name)
                    //    println("selectedImageURL>>>"+itemsCardsList[i].selectedImageURL)
                    if (_binLookupResponse?.cardBrand?.name?.let {
                            itemsCardsList[i].selectedImageURL.contains(
                                it
                            )
                        } == true) {
                        selectedImageURL = itemsCardsList[i].selectedImageURL

                        tapCardInputView.setSingleCardInput(
                            CardBrandSingle.fromCode(
                                _binLookupResponse?.cardBrand.toString()
                            ), selectedImageURL
                        )

                        tabLayout?.visibility = View.GONE
                        tapAlertView?.fadeVisibility(View.GONE, 500)

                    }
                }
            }

        } else {
            //we will send scheme
            schema = _binLookupResponse?.scheme


            _binLookupResponse?.scheme?.cardBrand?.let { it1 ->
                tabLayout.selectTab(it1, false)
            }
            isDisabledBrandSelected = _disabledPaymentList?.any {
                it.brand?.replace("_", "")
                    .equals(_binLookupResponse?.scheme?.cardBrand?.name, ignoreCase = true)
            }
            if (itemsCardsList.isNotEmpty()) {
                tapCardInputView.setSingleCardInput(
                    CardBrandSingle.fromCode(
                        _binLookupResponse?.cardBrand?.name.toString()
                    ), checkMadaLogoForVisaOrMasterCard(_binLookupResponse!!)?.selectedImageURL
                )
                tabLayout?.visibility = View.GONE
                tapAlertView?.fadeVisibility(View.GONE, 500)
            }


        }


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


    private fun checkValidationState(
        card: DefinedCardBrand,
        charSequence: String,
        textWatcher: TextWatcher
    ) {
        if (card.cardBrand != null)
            when (card.validationState) {

                CardValidationState.invalid -> {
                    println("cardBrand val" + card.cardBrand)
                    if (card.cardBrand != null)
                        tapCardInputView.setSingleCardInput(
                            CardBrandSingle.Unknown, null
                        )
                    // tabLayout.selectTab(card.cardBrand, false)
                    tapAlertView?.fadeVisibility(View.VISIBLE, 1000)
                    tapAlertView?.alertMessage?.text =
                        (LocalizationManager.getValue("Error", "Hints", "wrongCardNumber"))
                    cardNumValidation = false

                }
                CardValidationState.incomplete -> {
                    if (charSequence.isEmpty()) {
                        intertabLayout.visibility = View.VISIBLE
                        tabLayout.visibility = View.VISIBLE
                        acceptedCardText.visibility = View.VISIBLE
                    }
                    cardNumValidation = false
                }
                CardValidationState.valid -> {
                    if (schema != null) {
                        schema?.cardBrand?.let {
                            tabLayout.selectTab(it, true)
                            logicTosetImageDynamic(
                                checkMadaLogoForVisaOrMasterCard(
                                    PaymentDataSource.getBinLookupResponse()!!
                                )?.type, charSequence.toString()
                            )

                        }
                    } else {
                        logicTosetImageDynamic(card.cardBrand, charSequence.toString())
                        tabLayout.selectTab(card.cardBrand, true)
                    }

                    tapAlertView?.fadeVisibility(View.VISIBLE, 1000)
                    tapAlertView?.alertMessage?.text =
                        (LocalizationManager.getValue("Warning", "Hints", "missingExpiryCVV"))


                    intertabLayout.visibility = View.GONE
                    tabLayout.visibility = View.GONE
                    acceptedCardText.visibility = View.GONE
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

    override fun onCvcComplete() {
        checkoutViewModel.onPayCardCompleteAction(
            true,
            PaymentType.CARD,
            fullCardNumber,
            expiryDatePrev,
            cvvNumberPrev,
            cardHolderNamePrev,
            prevSetCardBrand?.toString(),
            null
        )
    }

    override fun cardFormHasFocus(hasFocus: Boolean) {

        checkoutViewModel.resetViewHolder()
    }

    override fun cvvFieldHasFocus(hasFocus: Boolean) {

        if (hasFocus) {
            tapAlertView?.fadeVisibility(View.GONE, 500)
        } else {
            if (cvvNumber != null)
                if (cvvNumber?.length!! < 3) {
                    tapAlertView?.fadeVisibility(View.VISIBLE)
                    val alertMessage: String =
                        LocalizationManager.getValue("Warning", "Hints", "missingCVV")
                    tapAlertView?.alertMessage?.text = alertMessage.replace("%i", "3")
                }

        }
    }

    override fun isCVCValid(isValid: Boolean) {
        println("isCVCValid ss" + isValid)
        isCVCLengthMax = isValid

    }


    override fun onExpirationComplete() {
        /*if (cardInputUIStatus == CardInputUIStatus.SavedCard) {
            onFocusChange(CardInputListener.FocusField.FOCUS_CVC)
        }*/
    }


    override fun onFocusChange(focusField: String) {
        lastFocusField = focusField

        println("lastFocusField>>>>" + lastFocusField)
        if (focusField == "focus_cardholder" && cardHolderName.isNullOrEmpty()) CustomUtils.showKeyboard(
            context
        )


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
     * @param enabledPaymentsList represents the images of payment methods.
     * */
    @RequiresApi(Build.VERSION_CODES.N)
    fun setDataFromAPI(
        enabledPaymentsList: List<PaymentOption>,
        disabledPaymentList: List<PaymentOption>
    ) {
        println("enabledPaymentsList>>" + enabledPaymentsList)
//        tabLayout.resetBehaviour()

        val itemsMobilesList = ArrayList<SectionTabItem>()
        val totalList: MutableList<PaymentOption> = ArrayList()
        itemsCardsList = ArrayList<SectionTabItem>()
        intertabLayout.removeAllTabs()
        _enabledPaymentsList =
            enabledPaymentsList.sortedBy { it.orderBy } as MutableList<PaymentOption>
        _disabledPaymentList =
            disabledPaymentList.sortedBy { it.orderBy } as MutableList<PaymentOption>

        PaymentDataSource.setBinLookupResponse(null)
        /**
         * Sorted cardpayment types based on orderBY*/
        decideTapSelection(
            _enabledPaymentsList as List<PaymentOption>,
            itemsMobilesList,
            itemsCardsList,
            _disabledPaymentList as List<PaymentOption>
        )
        /**
         * if there is only one payment method we will set visibility gone for tablayout
         * and set the payment method icon for inline input card
         * and set visibility  for separator after chips gone
         */

        hideTabLayoutWhenOnlyOnePayment(itemsCardsList, itemsMobilesList)
        tabLayout.addSection(itemsCardsList)


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
        enabledPaymentList: List<PaymentOption>,
        itemsMobilesList: ArrayList<SectionTabItem>,
        itemsCardsList: ArrayList<SectionTabItem>,
        disabledPaymentList: List<PaymentOption>
    ) {
        println("enabledPaymentList>>" + enabledPaymentList.size)
        println("disabledPaymentList>>" + disabledPaymentList.size)

        for (i in enabledPaymentList.indices) {
            when (CustomUtils.getCurrentTheme()) {
                ThemeMode.dark.name -> {
                    imageURL = enabledPaymentList[i].logos?.dark?.png?.toString().toString()
                }

                ThemeMode.dark_colored.name -> {
                    imageURL = enabledPaymentList[i].logos?.dark_colored?.png?.toString().toString()
                }
                ThemeMode.light.name -> {
                    imageURL = enabledPaymentList[i].logos?.light?.png?.toString().toString()
                    //  disabledImageURL = disabledPaymentList[0].logos?.light?.disabled?.png.toString()
                }
                ThemeMode.light_mono.name -> {
                    imageURL = enabledPaymentList[i].logos?.light_mono?.png?.toString().toString()
                }
            }
            // imageURL = imageURLApi[i].image.toString()
            paymentType = enabledPaymentList[i].paymentType
            cardBrandType = if (enabledPaymentList[i].brand == null) {
                "unknown"
            } else
                enabledPaymentList[i].brand.toString()



            itemsCardsList.add(
                SectionTabItem(
                    imageURL,
                    imageURL,
                    CardBrand.fromString(cardBrandType)
                )
            )
        }

        if (disabledPaymentList.isNotEmpty())
            for (i in disabledPaymentList.indices) {
                when (CustomUtils.getCurrentTheme()) {
                    ThemeMode.dark.name -> {
                        imageURL =
                            disabledPaymentList[i].logos?.dark?.disabled?.png?.toString().toString()
                    }

                    ThemeMode.dark_colored.name -> {
                        imageURL =
                            disabledPaymentList[i].logos?.dark_colored?.disabled?.png?.toString()
                                .toString()
                    }
                    ThemeMode.light.name -> {
                        imageURL = disabledPaymentList[i].logos?.light?.disabled?.png?.toString()
                            .toString()
                    }
                    ThemeMode.light_mono.name -> {
                        imageURL =
                            disabledPaymentList[i].logos?.light_mono?.disabled?.png?.toString()
                                .toString()
                    }
                }
                // imageURL = imageURLApi[i].image.toString()
                paymentType = disabledPaymentList[i].paymentType
                cardBrandType = if (disabledPaymentList[i].brand == null) {
                    "unknown"
                } else
                    disabledPaymentList[i].brand.toString()


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
            // closeButton?.visibility = View.VISIBLE  hiding for mobile view
        } else {
            //  closeButton?.visibility = View.GONE
            tapAlertView?.fadeVisibility(View.GONE, 500)
        }
    }


    fun TapBaseViewHolder.doOnCurrencySupported(onCurrencySupported: (cardBrand: String?) -> Unit) {
        if (isCardEnterdShouldBeDisabledPaymentOptions()) {
            onPaymentCardComplete.onPaymentCompletedShowingCurrencyWidget(
                cardBrandInString.toString()
            )
            /**
             * case activate PayButton
             */
            if (cardBrandInString == MADA_SCHEME && isCurrencySelectedRelatedToSaudiReal().not()) {
                onCurrencySupported.invoke(PaymentDataSource.getBinLookupResponse()?.cardBrand?.rawValue)
            }

        } else if (isCardEnterdEnabledPaymentOptionsAndHasMoreThanOneSupportedCurrency()) {
            onPaymentCardComplete.onPaymentCompletedShowingCurrencyWidget(
                cardBrandInString.toString()
            )
            onCurrencySupported.invoke(cardBrandInString)
        } else {
            onCurrencySupported.invoke(cardBrandInString)
        }
    }

    fun TapBaseViewHolder.checkMadaLogoForVisaOrMasterCard(
        binLookupResponse: BINLookupResponse?): SectionTabItem? {
        var selectedImage: SectionTabItem? = null
        if (isCurrencySelectedRelatedToSaudiReal()) {
            selectedImage = itemsCardsList.find {
                /**
                 * mada scheme cardBrand in case of SAUDi REAL
                 */
                it.selectedImageURL.contains(
                    binLookupResponse?.scheme?.cardBrand?.name?.toLowerCase().toString()
                )
            }
        } else {
            /**
             * visa , mastercard  brand  in case of SAUDI REAL
             */
            selectedImage = itemsCardsList.find {
                it.selectedImageURL.contains(
                    binLookupResponse?.cardBrand?.name?.toLowerCase().toString()
                )
            }
        }
        return selectedImage
    }

    fun isCardEnterdShouldBeDisabledPaymentOptions(): Boolean {

        val isCardEnterdDisabledPaymentOption = checkoutViewModel.getDisabledCardPaymentList(
            PaymentDataSource.getSelectedCurrency()?.toUpperCase().toString()
        )
            .any {
                it.brand == PaymentDataSource.getBinLookupResponse()?.scheme?.cardBrand?.rawValue
            }
        return isCardEnterdDisabledPaymentOption
    }

    private fun isCardEnterdEnabledPaymentOptionsAndHasMoreThanOneSupportedCurrency(): Boolean {

        val isCardEnterdHasMoreThanOneCurrency = checkoutViewModel.getEnabledCardPaymentList(
            PaymentDataSource.getSelectedCurrency()?.toUpperCase().toString()
        )
            .any {
                it.brand == PaymentDataSource.getBinLookupResponse()?.scheme?.cardBrand?.rawValue && it.getSupportedCurrencies().size > 1
            }
        return isCardEnterdHasMoreThanOneCurrency && (PaymentDataSource.getCurrency()?.isoCode?.toUpperCase() != PaymentDataSource.getSelectedCurrency()
            ?.toUpperCase().toString())
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
                //TODO we need this  was commented because we need localization to be updated by OSAMA Onfirebase to read them
//                CustomUtils.showDialog(
//                  /*  LocalizationManager.getValue(
//                        "alertUnsupportedCardTitle",
//                        "AlertBox"
//                    ),*/
//                    title = "Alert",
//                   // LocalizationManager.getValue("alertUnsupportedCardMessage", "AlertBox"),
//                    messageString = "Card Not supported",
//                    context = context,
//                    btnType = 1,
//                    baseLayoutManager =  baseLayoutManager,
//                    cardTypeDialog = true
 //              )

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
            CustomUtils.showDialog(
                title = "alert",
                messageString = "cardnot supported",
                context = context,
                btnType = 1,
                baseLayoutManager = baseLayoutManager,
                cardTypeDialog = true
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
        var loadUrlString: String? = ""
        when (CustomUtils.getCurrentTheme()) {
            ThemeMode.dark.name -> {
                loadUrlString = _savedCardsModel.logos?.dark?.png
            }
            ThemeMode.dark_colored.name -> {
                loadUrlString = _savedCardsModel.logos?.dark_colored?.png
            }
            ThemeMode.light.name -> {
                loadUrlString = _savedCardsModel.logos?.light?.png
            }
            ThemeMode.light_mono.name -> {
                loadUrlString = _savedCardsModel.logos?.light_mono?.png
            }
        }
        tapCardInputView.setSingleCardInput(
            CardBrandSingle.fromCode(
                company.tap.cardinputwidget2.CardBrand.fromCardNumber(_savedCardsModel.firstSix)
                    .toString()
            ), loadUrlString
        )
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

    fun logicTosetImageDynamic(card: CardBrand?, cardCharSeq: String) {
        for (i in itemsCardsList.indices) {

            if (itemsCardsList[i].selectedImageURL.contentEquals(
                    "dark"
                )
            ) {
                val iconStr = itemsCardsList[i].selectedImageURL.replace(
                    "https://tap-assets.b-cdn.net/payment-options/v2/dark/",
                    ""
                )
                if (iconStr.replace(".png", "").toLowerCase()
                        .contains(card?.name?.toLowerCase().toString())
                ) {
                    tapCardInputView.setSingleCardInput(
                        CardBrandSingle.fromCode(card?.name), itemsCardsList[i].selectedImageURL
                    )

                }
            } else {
                println("itemsCardsList[i] light" + itemsCardsList[i].selectedImageURL)
                val iconStr = itemsCardsList[i].selectedImageURL.replace(
                    "https://tap-assets.b-cdn.net/payment-options/v2/light/",
                    ""
                )
                if (iconStr.replace(".png", "").replace("_", "").toLowerCase()
                        .contains(card?.name?.toLowerCase().toString())
                ) {
                    tapCardInputView.setSingleCardInput(
                        CardBrandSingle.fromCode(card?.name), itemsCardsList[i].selectedImageURL
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


    fun hideViewONScanNFC() {
        intertabLayout.visibility = View.GONE
        tabLayout.visibility = View.GONE
        acceptedCardText.visibility = View.INVISIBLE
        tapCardInputView.onTouchView()

    }


}


