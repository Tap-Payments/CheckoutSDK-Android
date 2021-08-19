package company.tap.checkout.internal.viewmodels

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ShapeDrawable
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.annotation.RestrictTo
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.transition.Fade
import androidx.transition.Transition
import androidx.transition.TransitionManager
import cards.pay.paycardsrecognizer.sdk.Card
import cards.pay.paycardsrecognizer.sdk.FrameManager
import cards.pay.paycardsrecognizer.sdk.ui.InlineViewCallback
import cards.pay.paycardsrecognizer.sdk.ui.InlineViewFragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import company.tap.cardscanner.TapCard
import company.tap.cardscanner.TapTextRecognitionCallBack
import company.tap.cardscanner.TapTextRecognitionML
import company.tap.checkout.R
import company.tap.checkout.internal.PaymentDataProvider
import company.tap.checkout.internal.adapter.CardTypeAdapterUIKIT
import company.tap.checkout.internal.adapter.CurrencyTypeAdapter
import company.tap.checkout.internal.adapter.GoPayCardAdapterUIKIT
import company.tap.checkout.internal.api.enums.ChargeStatus
import company.tap.checkout.internal.api.enums.PaymentType
import company.tap.checkout.internal.api.models.*
import company.tap.checkout.internal.api.responses.DeleteCardResponse
import company.tap.checkout.internal.api.responses.PaymentOptionsResponse
import company.tap.checkout.internal.api.responses.SDKSettings
import company.tap.checkout.internal.apiresponse.CardViewEvent
import company.tap.checkout.internal.apiresponse.CardViewModel
import company.tap.checkout.internal.dummygener.*
import company.tap.checkout.internal.enums.PaymentTypeEnum
import company.tap.checkout.internal.enums.SectionType
import company.tap.checkout.internal.interfaces.*
import company.tap.checkout.internal.utils.*
import company.tap.checkout.internal.utils.AmountCalculator.calculateExtraFeesAmount
import company.tap.checkout.internal.utils.AnimationEngine.Type.SLIDE
import company.tap.checkout.internal.viewholders.*
import company.tap.checkout.internal.webview.WebFragment
import company.tap.checkout.internal.webview.WebViewContract
import company.tap.checkout.open.controller.SDKSession
import company.tap.checkout.open.data_managers.PaymentDataSource
import company.tap.checkout.open.enums.CardType
import company.tap.checkout.open.enums.TransactionMode
import company.tap.checkout.open.models.PaymentItem
import company.tap.nfcreader.open.reader.TapEmvCard
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapuilibrary.themekit.ThemeManager
import company.tap.tapuilibrary.themekit.theme.SeparatorViewTheme
import company.tap.tapuilibrary.uikit.enums.ActionButtonState
import company.tap.tapuilibrary.uikit.enums.GoPayLoginMethod
import company.tap.tapuilibrary.uikit.fragment.NFCFragment
import kotlinx.android.synthetic.main.action_button_animation.view.*
import kotlinx.android.synthetic.main.amountview_layout.view.*
import kotlinx.android.synthetic.main.businessview_layout.view.*
import kotlinx.android.synthetic.main.cardviewholder_layout1.view.*
import kotlinx.android.synthetic.main.gopaysavedcard_layout.view.*
import kotlinx.android.synthetic.main.switch_layout.view.*
import java.math.BigDecimal
import kotlin.properties.Delegates


/**
 *
 * Copyright © 2020 Tap Payments. All rights reserved.
 *
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
open class CheckoutViewModel : ViewModel(), BaseLayouttManager, OnCardSelectedActionListener,
    PaymentCardComplete, onCardNFCCallListener, OnCurrencyChangedActionListener, WebViewContract,
    TapTextRecognitionCallBack {
    private var savedCardList = MutableLiveData<List<SavedCard>>()
    private var paymentOptionsList = MutableLiveData<List<PaymentOption>>()
    private var goPayCardList = MutableLiveData<List<GoPaySavedCards>>()

    //private var allCurrencies = MutableLiveData<List<Currencies1>>()
    private var allCurrencies = MutableLiveData<List<SupportedCurrencies>>()
    private var selectedItemsDel by Delegates.notNull<Int>()
    private val isShaking = MutableLiveData<Boolean>()
    private var deleteCard: Boolean = false
    private var displayItemsOpen: Boolean = false
    private var displayOtpIsOpen: Boolean = false
    private  var saveCardSwitchHolder11: SwitchViewHolder11?=null
    private lateinit var paymentInputViewHolder: PaymenttInputViewHolder
    private lateinit var goPaySavedCardHolder: GoPaySavedCardHolder
    private lateinit var businessViewHolder: BusinessViewHolder
    private lateinit var amountViewHolder1: AmountViewHolder1
    private lateinit var currencyAdapter: CurrencyTypeAdapter
    private lateinit var goPayAdapter: GoPayCardAdapterUIKIT
    private lateinit var goPayViewsHolder: GoPayViewsHolder
    private lateinit var itemsViewHolder1: ItemsViewHolder1
    private lateinit var cardViewHolder11: CardViewHolder11
    private lateinit var asynchronousPaymentViewHolder: AsynchronousPaymentViewHolder
    private lateinit var tabAnimatedActionButtonViewHolder11: TabAnimatedActionButtonViewHolder11
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var fragmentManager: FragmentManager
    private lateinit var bottomSheetLayout: FrameLayout
    private lateinit var selectedAmount: String
    private lateinit var selectedCurrency: String
    private var fee : BigDecimal?= BigDecimal.ZERO
    @JvmField
    var currentCurrency: String = ""

    @JvmField
    var currentAmount: String = ""
    private lateinit var adapter: CardTypeAdapterUIKIT
    private lateinit var otpViewHolder: OTPViewHolder
    private lateinit var webFrameLayout: FrameLayout
    private lateinit var frameLayout: FrameLayout
    private lateinit var inLineCardLayout: FrameLayout
    private lateinit var sdkLayout: LinearLayout
    private lateinit var itemList: List<PaymentItem>
    private lateinit var selectedPaymentOption: PaymentOption
    private lateinit var orderList: Order1

    @SuppressLint("StaticFieldLeak")
    private lateinit var context: Context
    private lateinit var cardViewModel: CardViewModel
    private var otpTypeString: PaymentTypeEnum = PaymentTypeEnum.SAVEDCARD
    private lateinit var paymentActionType: PaymentType
    private val nfcFragment = NFCFragment()
    private val inlineViewFragment = InlineViewFragment()
    private var isInlineOpened = false
    private var isNFCOpened = false
    private var textRecognitionML: TapTextRecognitionML? = null
    private lateinit var intent: Intent
    private lateinit var inlineViewCallback: InlineViewCallback
    lateinit var tapCardPhoneListDataSource: ArrayList<TapCardPhoneListDataSource>
    lateinit var paymentOptionsResponse: PaymentOptionsResponse
    lateinit var redirectURL: String
    lateinit var cardId: String
    private var sdkSession: SDKSession = SDKSession


    @JvmField
    var selectedAmountPos: BigDecimal? = null

    @JvmField
    var selectedCurrencyPos: String? = null

    @JvmField
    var binLookupResponse1: BINLookupResponse? = null
    lateinit var paymentOptionsWorker: java.util.ArrayList<PaymentOption>

    @RequiresApi(Build.VERSION_CODES.N)
    fun initLayoutManager(
        bottomSheetDialog: BottomSheetDialog,
        context: Context,
        fragmentManager: FragmentManager,
        sdkLayout: LinearLayout,
        frameLayout: FrameLayout,
        webFrameLayout: FrameLayout,
        inLineCardLayout: FrameLayout,
        inlineViewCallback: InlineViewCallback,
        intent: Intent,
        cardViewModel: CardViewModel
    ) {
        this.context = context
        this.fragmentManager = fragmentManager
        this.sdkLayout = sdkLayout
        this.frameLayout = frameLayout
        this.webFrameLayout = webFrameLayout
        this.bottomSheetDialog = bottomSheetDialog
        this.inLineCardLayout = inLineCardLayout
        this.inlineViewCallback = inlineViewCallback
        this.intent = intent
        this.cardViewModel = cardViewModel

        textRecognitionML = TapTextRecognitionML(this)
        inlineViewFragment.setCallBackListener(inlineViewCallback)
        initViewHolders()
        initAmountAction()
        initSwitchAction()
        initOtpActionButton()
        setAllSeparatorTheme()

    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun initOtpActionButton() {
        otpViewHolder.otpView.otpViewActionButton.setOnClickListener {
            when (otpTypeString) {
                PaymentTypeEnum.GOPAY ->
                    goPayViewsHolder.onOtpButtonConfirmationClick(otpViewHolder.otpView.otpViewInput1.text.toString())


                PaymentTypeEnum.SAVEDCARD -> confirmOTPCode(otpViewHolder.otpView.otpViewInput1.text.toString())
                else -> {
                    removeViews(
                        businessViewHolder,
                        amountViewHolder1,
                        paymentInputViewHolder,
                        cardViewHolder11,
                        saveCardSwitchHolder11,
                        otpViewHolder
                    )
                    addViews(
                        businessViewHolder,
                        amountViewHolder1,
                        cardViewHolder11,
                        paymentInputViewHolder,
                        saveCardSwitchHolder11
                    )
                    saveCardSwitchHolder11?.view?.mainSwitch?.switchSaveMobile?.visibility =
                        View.GONE
                    saveCardSwitchHolder11?.view?.cardSwitch?.payButton?.setButtonDataSource(
                        false,
                        context.let { LocalizationManager.getLocale(it).language },
                        LocalizationManager.getValue("pay", "ActionButton"),
                        Color.parseColor(ThemeManager.getValue("actionButton.Invalid.backgroundColor")),
                        Color.parseColor(ThemeManager.getValue("actionButton.Invalid.titleLabelColor"))
                    )
                    saveCardSwitchHolder11?.view?.cardSwitch?.payButton?.visibility = View.VISIBLE
                    saveCardSwitchHolder11?.view?.cardSwitch?.payButton?.changeButtonState(
                        ActionButtonState.IDLE
                    )
                    paymentInputViewHolder.tapMobileInputView.clearNumber()

                }
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun confirmOTPCode(otpCode: String) {
        otpViewHolder.otpView.otpViewActionButton.changeButtonState(ActionButtonState.LOADING)
        when(PaymentDataSource.getTransactionMode()){
            TransactionMode.PURCHASE -> sendChargeOTPCode(otpCode)
            TransactionMode.AUTHORIZE_CAPTURE -> sendAuthorizeOTPCode(otpCode)
            else->sendChargeOTPCode(otpCode)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun sendAuthorizeOTPCode(otpCode: String) {
        cardViewModel.processEvent(
            CardViewEvent.AuthenticateAuthorizeTransaction,
            this,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            otpCode
        )
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun sendChargeOTPCode(otpCode: String) {
        cardViewModel.processEvent(
            CardViewEvent.AuthenticateChargeTransaction,
            this,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            otpCode
        )
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun initViewHolders() {
        businessViewHolder = BusinessViewHolder(context)
        amountViewHolder1 = AmountViewHolder1(context, this)
        cardViewHolder11 = CardViewHolder11(context, this)
        goPaySavedCardHolder = GoPaySavedCardHolder(context, this, this)
        saveCardSwitchHolder11 = SwitchViewHolder11(context)
        paymentInputViewHolder = PaymenttInputViewHolder(
            context,
            this,
            this,
            saveCardSwitchHolder11,
            this,
            cardViewModel
        )
        itemsViewHolder1 = ItemsViewHolder1(context, this)
        otpViewHolder = OTPViewHolder(context)
        goPayViewsHolder = GoPayViewsHolder(context, this, otpViewHolder)
        asynchronousPaymentViewHolder = AsynchronousPaymentViewHolder(context)
        tabAnimatedActionButtonViewHolder11 = TabAnimatedActionButtonViewHolder11(context)
        // nfcViewHolder = NFCViewHolder(context as Activity, context, this, fragmentManager)
    }

    private fun initSwitchAction() {
        saveCardSwitchHolder11?.view?.mainSwitch?.mainSwitch?.visibility = View.VISIBLE
        //By Default switchGoPayCheckout will be hidden , will appear only when goPayLoggedin
        saveCardSwitchHolder11?.view?.cardSwitch?.switchGoPayCheckout?.isChecked = false
        saveCardSwitchHolder11?.view?.cardSwitch?.switchGoPayCheckout?.visibility = View.GONE
        saveCardSwitchHolder11?.view?.cardSwitch?.saveGoPay?.visibility = View.GONE
        saveCardSwitchHolder11?.view?.cardSwitch?.alertGoPaySignUp?.visibility = View.GONE
        saveCardSwitchHolder11?.view?.cardSwitch?.switchSeparator?.visibility = View.GONE
        // saveCardSwitchHolder11?.view?.mainSwitch?.mainSwitch?.visibility = View.VISIBLE
        //   saveCardSwitchHolder11?.view?.mainSwitch?.mainSwitchLinear?.setBackgroundColor(Color.parseColor(ThemeManager.getValue("TapSwitchView.main.backgroundColor")))
    }

    private fun initAmountAction() {
        amountViewHolder1.setOnItemsClickListener {}
        amountViewHolder1.view.amount_section.mainKDAmountValue.visibility = View.GONE
    }


    override fun displayStartupLayout(enabledSections: ArrayList<SectionType>) {
        //Todo based on api response logic for switch case
        when (PaymentDataSource.getTransactionMode()) {

            TransactionMode.TOKENIZE_CARD -> {
                addViews(
                    businessViewHolder,
                    paymentInputViewHolder,
                    saveCardSwitchHolder11
                )

            }
            TransactionMode.SAVE_CARD -> {
                addViews(
                    businessViewHolder,
                    paymentInputViewHolder,
                    saveCardSwitchHolder11
                )
            }
            else -> {

                if(PaymentDataSource.getPaymentDataType()=="WEB"){
                    addViews(
                        businessViewHolder,
                        amountViewHolder1,
                        cardViewHolder11,
                        saveCardSwitchHolder11
                    )
                }else
                addViews(
                    businessViewHolder,
                    amountViewHolder1,
                    cardViewHolder11,
                    paymentInputViewHolder,
                    saveCardSwitchHolder11
                )
            }
        }

        saveCardSwitchHolder11?.view?.mainSwitch?.mainSwitchLinear?.setBackgroundColor(
            Color.parseColor(
                ThemeManager.getValue(
                    "TapSwitchView.main.backgroundColor"
                )
            )
        )
        inLineCardLayout.visibility = View.GONE
        saveCardSwitchHolder11?.view?.cardviewSwitch?.cardElevation = 0f


    }

    fun setBottomSheetLayout(bottomSheetLayout: FrameLayout) {
        this.bottomSheetLayout = bottomSheetLayout
    }

    override fun displayGoPayLogin() {
        setSlideAnimation()
        saveCardSwitchHolder11?.let {
            removeViews(
                businessViewHolder, amountViewHolder1,
                cardViewHolder11, paymentInputViewHolder,
                it, otpViewHolder,
                goPayViewsHolder
            )
        }

        addViews(businessViewHolder, amountViewHolder1, goPayViewsHolder)
        if (goPayViewsHolder.goPayopened) {
            goPayViewsHolder.goPayLoginInput.inputType = GoPayLoginMethod.PHONE
            goPayViewsHolder.goPayLoginInput.visibility = View.VISIBLE
            saveCardSwitchHolder11?.view?.cardSwitch?.switchGoPayCheckout?.visibility = View.VISIBLE
        }
        //TODO goPayAdapter.updateAdapterData(goPayCardList.value as List<GoPaySavedCards>)
        amountViewHolder1.changeGroupAction(false)
        goPayViewsHolder.goPayopened = true
    }

    private fun setSlideAnimation() {
        if (this::bottomSheetLayout.isInitialized)
            AnimationEngine.applyTransition(bottomSheetLayout, SLIDE)
    }

    override fun displayGoPay() {
        setSlideAnimation()
        saveCardSwitchHolder11?.let {
            removeViews(
                businessViewHolder,
                amountViewHolder1,
                goPayViewsHolder,
                cardViewHolder11,
                paymentInputViewHolder,
                it,
                otpViewHolder
            )
        }

        saveCardSwitchHolder11?.let {
            addViews(
                businessViewHolder,
                amountViewHolder1,
                goPaySavedCardHolder,
                cardViewHolder11,
                paymentInputViewHolder,
                it
            )
        }

        cardViewHolder11.view.mainChipgroup.groupAction.visibility = View.INVISIBLE
        saveCardSwitchHolder11?.view?.cardSwitch?.payButton?.visibility = View.VISIBLE
        saveCardSwitchHolder11?.goPayisLoggedin = true
        adapter.goPayOpenedfromMain(true)
        adapter.removeItems()
    }

    override fun controlCurrency(display: Boolean) {
        if (display) caseDisplayControlCurrency()
        else caseNotDisplayControlCurrency()

        displayItemsOpen = !display
        amountViewHolder1.changeGroupAction(!display)
        // if (this::currentAmount.isInitialized)
        if (this::selectedAmount.isInitialized && this::selectedCurrency.isInitialized) {
            if (selectedAmount == currentAmount && selectedCurrency == currentCurrency) {
                amountViewHolder1.view.amount_section.mainKDAmountValue.visibility = View.GONE

            } else
                amountViewHolder1.updateSelectedCurrency(
                    displayItemsOpen,
                    selectedAmount, selectedCurrency,
                    currentAmount, currentCurrency
                )
        }
        if(otpViewHolder.otpView.isVisible){
            removeViews(otpViewHolder)
            saveCardSwitchHolder11?.view?.cardSwitch?.payButton?.changeButtonState(ActionButtonState.IDLE)
            saveCardSwitchHolder11?.view?.cardSwitch?.payButton?.isClickable = true
            saveCardSwitchHolder11?.view?.cardSwitch?.payButton?.stateListAnimator=null
            saveCardSwitchHolder11?.view?.cardSwitch?.payButton?.setButtonDataSource(
                false,
                context.let { LocalizationManager.getLocale(it).language },
                LocalizationManager.getValue("pay", "ActionButton"),
                Color.parseColor(ThemeManager.getValue("actionButton.Valid.paymentBackgroundColor")),
                Color.parseColor(ThemeManager.getValue("actionButton.Valid.titleLabelColor"))
            )
//            saveCardSwitchHolder11?.view?.cardSwitch?.payButton?.isActivated = false
        }
       if(PaymentDataSource.getPaymentDataType()!=null && PaymentDataSource.getPaymentDataType() == "WEB"){
            removeViews(paymentInputViewHolder)
        }
        removeInlineScanner()
        removeNFCViewFragment()
    }


    private fun caseDisplayControlCurrency() {
        removeViews(
            businessViewHolder,
            amountViewHolder1,
            cardViewHolder11,
            paymentInputViewHolder,
            saveCardSwitchHolder11,
            goPayViewsHolder,
            otpViewHolder,
            itemsViewHolder1
        )
        removeAllViews()
        addViews(businessViewHolder, amountViewHolder1, itemsViewHolder1)
        /*itemsViewHolder1.setDatafromAPI(
            allCurrencies.value as ArrayList<SupportedCurrencies>,
            itemList
        )*/


        //  itemsViewHolder1.setItemsRecylerView()


        frameLayout.visibility = View.VISIBLE
        itemsViewHolder1.itemsdisplayed = true
    }

    private fun caseNotDisplayControlCurrency() {
        if (goPayViewsHolder.goPayopened || itemsViewHolder1.itemsdisplayed) setActionGoPayOpenedItemsDisplayed()
        else setActionNotGoPayOpenedNotItemsDisplayed()
    }

    private fun setActionGoPayOpenedItemsDisplayed() {
        removeViews(
            businessViewHolder,
            amountViewHolder1,
            cardViewHolder11,
            paymentInputViewHolder,
            saveCardSwitchHolder11,
            goPayViewsHolder,
            otpViewHolder,
            itemsViewHolder1
        )
        addViews(
            businessViewHolder,
            amountViewHolder1,
            cardViewHolder11,
            paymentInputViewHolder,
            saveCardSwitchHolder11
        )
        //itemsViewHolder1.resetView()
        itemsViewHolder1.setItemsRecylerView()
        //itemsViewHolder1.setCurrencyRecylerView()
        frameLayout.visibility = View.GONE
    }

    private fun setActionNotGoPayOpenedNotItemsDisplayed() {
        saveCardSwitchHolder11?.let {
            removeViews(
                businessViewHolder,
                amountViewHolder1,
                cardViewHolder11,
                paymentInputViewHolder,
                it,
                itemsViewHolder1
            )
        }
        saveCardSwitchHolder11?.let {
            addViews(
                businessViewHolder,
                amountViewHolder1,
                cardViewHolder11,
                paymentInputViewHolder,
                it
            )
        }
        saveCardSwitchHolder11?.view?.cardSwitch?.payButton?.changeButtonState(ActionButtonState.IDLE)
        // itemsViewHolder1.resetView()
        itemsViewHolder1.setItemsRecylerView()
        itemsViewHolder1.setCurrencyRecylerView()
        frameLayout.visibility = View.GONE
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("SetTextI18n")
    override fun displayOTPView(
        phoneNumber: PhoneNumber?,
        otpType: String,
        chargeResponse: Charge?
    ) {
        setSlideAnimation()

        amountViewHolder1.changeGroupAction(false)
        amountViewHolder1.view.amount_section.itemCountButton.text =  LocalizationManager.getValue(
            "close",
            "Common"
        )


        displayOtpIsOpen = true
        displayItemsOpen = false
        when (otpType) {
            PaymentTypeEnum.GOPAY.name -> {
                displayOtpGoPay(phoneNumber)
            }
            PaymentTypeEnum.telecom.name -> {
                displayOtpTelecoms(phoneNumber)
            }
            PaymentTypeEnum.SAVEDCARD.name -> {
                displayOtpCharge(phoneNumber, chargeResponse)
            }
            else -> {
                displayOtpCharge(phoneNumber, chargeResponse)
            }
        }
    }

    private fun displayOtpGoPay(phoneNumber: PhoneNumber?) {
        // otpTypeString = PaymentTypeEnum.GOPAY //temporray
        removeViews(
            cardViewHolder11,
            paymentInputViewHolder, saveCardSwitchHolder11, amountViewHolder1
        )
        addViews(amountViewHolder1, otpViewHolder)

        otpViewHolder.otpView.visibility = View.VISIBLE
        setOtpPhoneNumber(phoneNumber)
        otpViewHolder.otpView.changePhone.setOnClickListener {
            goPayViewsHolder.onChangePhoneClicked()
        }
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.N)
    private fun displayOtpCharge(phoneNumber: PhoneNumber?, chargeResponse: Charge?) {
        // otpTypeString = PaymentTypeEnum.GOPAY //temporray
        println("mmmmmmmm" + amountViewHolder1.view.amount_section.itemCountButton.text)

        removeViews(
            cardViewHolder11,
            paymentInputViewHolder, saveCardSwitchHolder11, otpViewHolder, amountViewHolder1
        )
        bottomSheetDialog.dismissWithAnimation

        addViews(amountViewHolder1, otpViewHolder)
        otpViewHolder.otpView.visibility = View.VISIBLE
        setOtpPhoneNumber(phoneNumber)
        otpViewHolder.otpView.changePhone.visibility = View.INVISIBLE
        otpViewHolder.otpView.timerText.setOnClickListener {
            resendOTPCode(chargeResponse)
        }
        amountViewHolder1.changeGroupAction(false)
    }

    private fun setOtpPhoneNumber(phoneNumber: PhoneNumber?) {

        var replaced = ""
        var countryCodeReplaced = ""
        countryCodeReplaced = phoneNumber?.countryCode?.replace("0", "").toString()
        if (phoneNumber?.number?.length!! > 7)
            replaced = (phoneNumber.number?.toString()).replaceRange(1, 6, "••••")
        otpViewHolder.otpView.mobileNumberText.text = "+$countryCodeReplaced $replaced"
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun resendOTPCode(chargeResponse: Charge?) {
        if (chargeResponse != null) {
            if (chargeResponse is Authorize) resendAuthorizeOTPCode(chargeResponse as Authorize?)
            else resendChargeOTPCode(chargeResponse as Charge?)
        }

    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun resendAuthorizeOTPCode(authorize: Authorize?) {
        cardViewModel.requestAuthenticateForAuthorizeTransaction(this, authorize)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun resendChargeOTPCode(charge: Charge?) {
        if (charge != null) {
            cardViewModel.requestAuthenticateForChargeTransaction(this, charge)
        }
    }

    private fun displayOtpTelecoms(phoneNumber: PhoneNumber?) {
        otpTypeString = PaymentTypeEnum.telecom
        removeViews(
            businessViewHolder,
            amountViewHolder1,
            paymentInputViewHolder,
            cardViewHolder11,
            saveCardSwitchHolder11,
            otpViewHolder
        )
        addViews(
            businessViewHolder,
            amountViewHolder1,
            cardViewHolder11,
            paymentInputViewHolder,
            saveCardSwitchHolder11,
            otpViewHolder
        )
        //Added check change listener to handle showing of extra save options
        saveCardSwitchHolder11?.view?.mainSwitch?.switchSaveMobile?.visibility = View.VISIBLE
        saveCardSwitchHolder11?.view?.cardSwitch?.payButton?.visibility = View.GONE
        saveCardSwitchHolder11?.view?.mainSwitch?.switchSaveMobile?.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) saveCardSwitchHolder11?.view?.cardSwitch?.switchesLayout?.visibility =
                View.GONE
        }
        saveCardSwitchHolder11?.setSwitchToggleData(PaymentType.telecom)
        otpViewHolder.setMobileOtpView()
        var replaced = ""
        var countryCodeReplaced = ""
        if (phoneNumber?.number?.length!! > 7)
            replaced = (phoneNumber.number.toString()).replaceRange(1, 6, "....")
        countryCodeReplaced =
            goPayViewsHolder.goPayLoginInput.countryCodePicker.selectedCountryCode.replace(
                "+",
                " "
            )
        otpViewHolder.otpView.mobileNumberTextNormalPay.text = "+$countryCodeReplaced$replaced"
    }


    override fun displayRedirect(url: String) {
        this.redirectURL = url
        if (::redirectURL.isInitialized && ::fragmentManager.isInitialized) {
            if (otpViewHolder.otpView.isVisible) {
                removeViews(
                    businessViewHolder,
                    amountViewHolder1,
                    cardViewHolder11,
                    paymentInputViewHolder,
                    otpViewHolder
                )
            }
           // setSlideAnimation()
            Handler(Looper.getMainLooper()).postDelayed({
                fragmentManager.beginTransaction()
                    .replace(
                        R.id.webFrameLayout, WebFragment.newInstance(
                            redirectURL,
                            this, cardViewModel
                        )
                    ).commitNow()

            }, 5000)

        }
        saveCardSwitchHolder11?.view?.visibility = View.GONE
    }

    override fun displaySaveCardOptions() {

    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun setBinLookupData(
        binLookupResponse: BINLookupResponse,
        context: Context,
        cardViewModel: CardViewModel
    ) {
        paymentInputViewHolder = PaymenttInputViewHolder(
            context,
            this,
            this,
            saveCardSwitchHolder11,
            this,
            cardViewModel
        )
        paymentInputViewHolder.tabLayout.setUnselectedAlphaLevel(0.3f)
        if (::paymentInputViewHolder.isInitialized)
            paymentInputViewHolder.setCurrentBinData(binLookupResponse)

    }


    override fun getDatasfromAPIs(
        sdkSettings: SDKSettings?,
        paymentOptionsResponse: PaymentOptionsResponse?
    ) {
        println("if(::businessViewHolder.isInitialized getpay" + ::businessViewHolder.isInitialized)
        if (paymentOptionsResponse != null) {
            this.paymentOptionsResponse = paymentOptionsResponse
        }
        if (::businessViewHolder.isInitialized && PaymentDataSource.getTransactionMode() != TransactionMode.TOKENIZE_CARD) {
            businessViewHolder.setDatafromAPI(
                sdkSettings?.data?.merchant?.logo,
                sdkSettings?.data?.merchant?.name
            )
            if (sdkSettings?.data?.verified_application == true) {

            }
        }
        // println("PaymentOptionsResponse on get$paymentOptionsResponse")
        allCurrencies.value = paymentOptionsResponse?.supportedCurrencies
        savedCardList.value = paymentOptionsResponse?.cards
        println("savedCardList on get" + savedCardList.value)
        println("paymentOptionsResponse?.supportedCurrencie on get" + paymentOptionsResponse?.supportedCurrencies)
        if (paymentOptionsResponse?.supportedCurrencies != null && ::amountViewHolder1.isInitialized) {
            currentCurrency = paymentOptionsResponse.currency
            for (i in paymentOptionsResponse.supportedCurrencies.indices) {
                if (paymentOptionsResponse.supportedCurrencies[i].currency == currentCurrency) {
                    println("current amount value>>" + paymentOptionsResponse.supportedCurrencies[i].amount)
                    currentAmount =
                        CurrencyFormatter.currencyFormat(paymentOptionsResponse.supportedCurrencies[i].amount.toString())

                }
            }
            amountViewHolder1.setDatafromAPI(
                currentAmount,
                currentCurrency,
                PaymentDataSource.getItems()?.size.toString()
            )
        }
        /**
         * <<<<<<< This items list is going to come from API response later now for loading the view we are taking the List from
         * PaymentDatasource.getItems()  >>>>>>>> */
        if (PaymentDataSource.getItems() != null) {
            itemList = PaymentDataSource.getItems()!!
        }


        if (::itemsViewHolder1.isInitialized) {
            paymentOptionsResponse?.supportedCurrencies?.let {
                itemsViewHolder1.setDatafromAPI(
                    it,
                    PaymentDataSource.getItems()
                )
            }
            itemsViewHolder1.setItemsRecylerView()
            itemsViewHolder1.setCurrencyRecylerView()
        }
        sdkSettings?.data?.merchant?.name?.let {
            saveCardSwitchHolder11?.setDataFromAPI(
                it,
                paymentInputViewHolder.selectedType
            )
        }
        paymentOptionsList.value = paymentOptionsResponse?.paymentOptions
        println("paymentOptions value" + paymentOptionsResponse?.paymentOptions)
        if (::context.isInitialized)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                val divider = DividerItemDecoration(
                    context,
                    DividerItemDecoration.HORIZONTAL
                )
                divider.setDrawable(ShapeDrawable().apply {
                    intrinsicWidth = 10
                    paint.color = Color.TRANSPARENT
                }) // note: currently (support version 28.0.0), we can not use tranparent color here, if we use transparent, we still see a small divider line. So if we want to display transparent space, we can set color = background color or we can create a custom ItemDecoration instead of DividerItemDecoration.
                cardViewHolder11.view.mainChipgroup.chipsRecycler.addItemDecoration(divider)
                initAdaptersAction()

            }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun initAdaptersAction() {
        adapter = CardTypeAdapterUIKIT(this)
        goPayAdapter = GoPayCardAdapterUIKIT(this)
        //  goPayAdapter.updateAdapterData(goPayCardList.value as List<GoPaySavedCards>)
        currencyAdapter = CurrencyTypeAdapter(this)
        if (allCurrencies.value?.isNotEmpty() == true) {
            currencyAdapter.updateAdapterData(allCurrencies.value as List<SupportedCurrencies>)
        }
        if (savedCardList.value?.isNotEmpty() == true) {
            println("getCardType" + PaymentDataSource.getCardType())
            if (PaymentDataSource.getCardType() != null && PaymentDataSource.getCardType() != CardType.ALL) {
                filterSavedCardTypes(savedCardList.value as List<SavedCard>)
            } else adapter.updateAdapterDataSavedCard(savedCardList.value as List<SavedCard>)
        } else {
            cardViewHolder11.view.mainChipgroup.groupAction?.visibility = View.GONE
        }

        cardViewHolder11.view.mainChipgroup.chipsRecycler.adapter = adapter
        // goPaySavedCardHolder.view.goPayLoginView.chipsRecycler.adapter = goPayAdapter
        cardViewHolder11.view.mainChipgroup.groupAction?.visibility = View.VISIBLE
        cardViewHolder11.view.mainChipgroup.groupAction?.setOnClickListener {
            setMainChipGroupActionListener()
        }
        // filterCardTypes(paymentOptionsList.value as ArrayList<PaymentOption>)
        paymentOptionsWorker =
            java.util.ArrayList<PaymentOption>(paymentOptionsResponse.paymentOptions)

        filterViewModels(PaymentDataSource.getCurrency()?.isoCode.toString())
        // filterModels(PaymentDataSource.getCurrency()?.isoCode.toString())
        //  filterCardTypes(PaymentDataSource.getCurrency()?.isoCode.toString(),paymentOptionsWorker)

        //    goPaySavedCardHolder.view.goPayLoginView.groupAction.setOnClickListener {
        //       setGoPayLoginViewGroupActionListener()
        //   }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun filterCardTypes(list: ArrayList<PaymentOption>) {
        var filteredCardList: List<PaymentOption> =
            list.filter { items -> items.paymentType == PaymentType.CARD }

        println("filteredCardList value " + filteredCardList.size)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            paymentInputViewHolder.setDataFromAPI(filteredCardList)
        }
    }

    private fun filterSavedCardTypes(savedCardList: List<SavedCard>) {
        val filteredSavedCardList: List<SavedCard> =
            savedCardList.filter { items ->
                items.funding == PaymentDataSource?.getCardType().toString()
            }

        adapter.updateAdapterDataSavedCard(filteredSavedCardList)
    }


    private fun setMainChipGroupActionListener() {
        if (cardViewHolder11.view.mainChipgroup.groupAction?.text == LocalizationManager.getValue(
                "close",
                "Common"
            )
        ) {
            isShaking.value = false
            adapter.updateShaking(isShaking.value ?: false)
            goPayAdapter.updateShaking(isShaking.value ?: false)
            cardViewHolder11.view.mainChipgroup.groupAction?.text =
                LocalizationManager.getValue("edit", "Common")
        } else {
            isShaking.value = true
            adapter.updateShaking(isShaking.value ?: true)
            goPayAdapter.updateShaking(isShaking.value ?: true)
            goPayAdapter.updateShaking(false)
            cardViewHolder11.view.mainChipgroup.groupAction?.text =
                LocalizationManager.getValue("close", "Common")
        }
    }

    private fun setGoPayLoginViewGroupActionListener() {
        if (goPaySavedCardHolder.view.goPayLoginView.groupAction?.text == LocalizationManager.getValue(
                "close",
                "Common"
            )
        ) {
            isShaking.value = false
            adapter.updateShaking(isShaking.value ?: false)
            goPayAdapter.updateShaking(isShaking.value ?: false)
            goPaySavedCardHolder.view.goPayLoginView.groupAction?.text =
                LocalizationManager.getValue("edit", "Common")
            goPayAdapter.updateSignOut(goPayCardList.value as List<GoPaySavedCards>, false)

        } else {
            isShaking.value = true
            adapter.updateShaking(isShaking.value ?: true)
            Log.d("isShaking.value", isShaking.value.toString())
            goPayAdapter.updateShaking(false)
            goPaySavedCardHolder.view.goPayLoginView.groupAction?.text =
                LocalizationManager.getValue("close", "Common")
            goPayAdapter.updateSignOut(goPayCardList.value as List<GoPaySavedCards>, true)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("WrongConstant")
    override fun didDialogueExecute(response: String, cardTypeDialog: Boolean?) {
        println("response are$response")
        if (response == "YES") {
            if (deleteCard) {
                //saveCardSwitchHolder11?.view?.cardSwitch?.payButton?.changeButtonState(ActionButtonState.LOADING)
                cardViewModel.processEvent(
                    CardViewEvent.DeleteSaveCardEvent,
                    this,
                    null,
                    null,
                    null,
                    null,
                    PaymentDataSource.getCustomer().identifier,
                    cardId
                )

            } else {
                println("else block is calle are")
                removeViews(goPaySavedCardHolder)
                //  adapter.updateAdapterDataSavedCard(savedCardList.value as List<SavedCard>)
                goPayViewsHolder.goPayopened = false
                adapter.goPayOpenedfromMain(true)
                adapter.updateShaking(false)
                cardViewHolder11.view.mainChipgroup.groupAction.visibility = View.VISIBLE
            }
        } else if (response == "NO") {
            adapter.updateShaking(false)
            deleteCard = false

        } else if (response == "OK") {
            if (cardTypeDialog == true) {
                paymentInputViewHolder.tapCardInputView.clear()
                paymentInputViewHolder.tabLayout.resetBehaviour()
            } else {
                bottomSheetDialog.dismissWithAnimation
                bottomSheetDialog.dismiss()
            }
        }
    }


    override fun deleteSelectedCardListener(delSelectedCard: DeleteCardResponse) {
        println("delSelectedCard value is" + delSelectedCard.deleted)
        //todo check why delSelectedCard.deleted is changing to false while its true from the API
        if (!delSelectedCard.deleted) {
            adapter.deleteSelectedCard(selectedItemsDel)
            adapter.updateShaking(false)
            deleteCard = false
            //saveCardSwitchHolder11?.view?.cardSwitch?.payButton?.changeButtonState(ActionButtonState.SUCCESS)
        }
    }

    override fun handleSuccessFailureResponseButton(
        response: String,
        authenticate: Authenticate,
        status: ChargeStatus
    ) {
        when (response) {
            "success" -> {
                setSlideAnimation()
                if (::webFrameLayout.isInitialized)
                    webFrameLayout.visibility = View.GONE
                //  webFrameLayout.fadeVisibility(View.GONE)
                setSlideAnimation()
                saveCardSwitchHolder11?.view?.visibility = View.VISIBLE
                saveCardSwitchHolder11?.view?.cardSwitch?.payButton?.changeButtonState(
                    ActionButtonState.SUCCESS
                )
                Handler().postDelayed({
                    if (::bottomSheetDialog.isInitialized)
                        bottomSheetDialog.dismiss()
                }, 8000)


            }
            else -> {
                if (::webFrameLayout.isInitialized)
                    webFrameLayout.visibility = View.GONE
                //  webFrameLayout.fadeVisibility(View.GONE)
                setSlideAnimation()
                saveCardSwitchHolder11?.view?.visibility=View.VISIBLE
                saveCardSwitchHolder11?.view?.cardSwitch?.payButton?.changeButtonState(
                    ActionButtonState.ERROR
                )
                Handler().postDelayed({
                    if (::bottomSheetDialog.isInitialized)
                        bottomSheetDialog.dismiss()
                }, 8000)

                // saveCardSwitchHolder11?.view?.cardSwitch?.showOnlyPayButton()
            }
        }
    }


    override fun displayAsynchronousPaymentView(chargeResponse: Charge) {
        if(chargeResponse!=null){
            removeViews(businessViewHolder,amountViewHolder1,cardViewHolder11,paymentInputViewHolder,saveCardSwitchHolder11)
            addViews(asynchronousPaymentViewHolder)
            asynchronousPaymentViewHolder.setDataFromAPI(chargeResponse)
        }
    }


    private fun removeViews(vararg viewHolders: TapBaseViewHolder?) {

        viewHolders.forEach {
            Handler(Looper.getMainLooper()).postDelayed(Runnable {
                sdkLayout.removeView(it?.view)
                val animation = AnimationUtils.loadAnimation(context, R.anim.fade_out)
                it?.view?.startAnimation(animation)

            }, 1000)
        }

    }

    private fun addViews(vararg viewHolders: TapBaseViewHolder?) {
        viewHolders.forEach {
            Handler(Looper.getMainLooper()).postDelayed(Runnable {
                if (::sdkLayout.isInitialized)
                    sdkLayout.addView(it?.view)
                val animation = AnimationUtils.loadAnimation(context, R.anim.fade_in)
                it?.view?.startAnimation(animation)
            }, 0)
        }
    }

    private fun unActivateActionButton() {
        saveCardSwitchHolder11?.view?.cardSwitch?.payButton?.changeButtonState(ActionButtonState.IDLE)
        saveCardSwitchHolder11?.view?.cardSwitch?.payButton?.isActivated = false
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCardSelectedAction(isSelected: Boolean, savedCardsModel: Any?) {
        when (savedCardsModel) {
            is SavedCard -> {
                activateActionButton()
                setPayButtonAction(PaymentType.SavedCard, savedCardsModel)
            }
            else -> {
                if (savedCardsModel != null) {
                    if ((savedCardsModel as PaymentOption).paymentType == PaymentType.WEB) {
                        activateActionButton()
                        setPayButtonAction(PaymentType.WEB, savedCardsModel)
                    }
                } else
                    displayGoPayLogin()

            }
        }
    }


    private fun activateActionButton() {
        saveCardSwitchHolder11?.view?.cardSwitch?.payButton?.setButtonDataSource(
            true,
            context.let { LocalizationManager.getLocale(it).language },
            LocalizationManager.getValue("pay", "ActionButton"),
            Color.parseColor(ThemeManager.getValue("actionButton.Valid.paymentBackgroundColor")),
            Color.parseColor(ThemeManager.getValue("actionButton.Valid.titleLabelColor"))
        )
        saveCardSwitchHolder11?.view?.cardSwitch?.showOnlyPayButton()
        saveCardSwitchHolder11?.view?.cardSwitch?.payButton?.isActivated
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("WrongConstant")
    private fun onClickRedirect(savedCardsModel: Any) {
        selectedPaymentOption = savedCardsModel as PaymentOption
        cardViewModel.processEvent(
            CardViewEvent.ChargeEvent,
            this,
            selectedPaymentOption,
            null,
            null,
            null
        )
             saveCardSwitchHolder11?.view?.cardSwitch?.payButton?.changeButtonState(
                 ActionButtonState.LOADING
             )

        removeViews(businessViewHolder)
        removeViews(amountViewHolder1)
        removeViews(cardViewHolder11)
        removeViews(paymentInputViewHolder)
        setSlideAnimation()
        saveCardSwitchHolder11?.view?.visibility = View.GONE
        saveCardSwitchHolder11?.view?.cardSwitch?.payButton?.getImageView(
            R.drawable.loader,
            1
        ) {





        }?.let {
            saveCardSwitchHolder11?.view?.cardSwitch?.payButton?.addChildView(
                it
            )
        }

    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun onClickCardPayment() {
        saveCardSwitchHolder11?.view?.cardSwitch?.payButton?.changeButtonState(ActionButtonState.LOADING)
        saveCardSwitchHolder11?.view?.cardSwitch?.payButton?.getImageView(
            R.drawable.loader,
            1
        ) {
            setSlideAnimation()
            removeViews(cardViewHolder11)
            removeViews(businessViewHolder)
            removeViews(amountViewHolder1)
            removeViews(saveCardSwitchHolder11)
            removeViews(paymentInputViewHolder)

            cardViewModel.processEvent(
                CardViewEvent.CreateTokenEvent,
                this,
                null,
                null,
                paymentInputViewHolder.getCard(),
                null
            )
            // cardViewModel.processEvent(CardViewEvent.ChargeEvent, this, null,null,null,null)

        }?.let {
            saveCardSwitchHolder11?.view?.cardSwitch?.payButton?.addChildView(
                it
            )
        }
    }

    private fun changeBottomSheetTransition() {
        bottomSheetLayout.let { layout ->
            layout.post {
                TransitionManager.beginDelayedTransition(layout)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onDeleteIconClicked(
        stopAnimation: Boolean,
        itemId: Int,
        cardId: String,
        maskedCardNumber: String
    ) {
        this.cardId = cardId
        if (stopAnimation) {
            stopDeleteActionAnimation(itemId, maskedCardNumber)
        } else {
            cardViewHolder11.view.mainChipgroup.groupAction?.text = LocalizationManager.getValue(
                "close",
                "Common"
            )
            deleteCard = false
        }

    }

    private fun stopDeleteActionAnimation(itemId: Int, maskedCardNumber: String) {
        isShaking.value = false
        cardViewHolder11.view.mainChipgroup.groupAction?.text = LocalizationManager.getValue(
            "GatewayHeader",
            "HorizontalHeaders",
            "rightTitle"
        )
        val title: String = LocalizationManager.getValue("deleteSavedCardTitle", "SavedCardTitle")
        CustomUtils.showDialog(
            "$title$maskedCardNumber ?",
            LocalizationManager.getValue(
                "deleteMessage",
                "GoPay"
            ),
            context,
            2,
            this, null, null, false
        )
        selectedItemsDel = itemId
        deleteCard = true

    }

    override fun onGoPayLogoutClicked(isClicked: Boolean) {
        if (isClicked) CustomUtils.showDialog(
            LocalizationManager.getValue("goPaySignOut", "GoPay"), LocalizationManager.getValue(
                "goPaySaveCards",
                "GoPay"
            ), context, 2, this, null, null, false
        )
    }

    override fun onEditClicked(isClicked: Boolean) {
        if (isClicked) {
            adapter.updateShaking(true)
            goPayAdapter.updateShaking(true)
        } else {
            adapter.updateShaking(false)
            goPayAdapter.updateShaking(false)
        }

    }

    override fun onPayCardSwitchAction(isCompleted: Boolean, paymentType: PaymentType) {
        println("isCompleted???" + isCompleted)
        if (isCompleted) {
            saveCardSwitchHolder11?.view?.mainSwitch?.visibility = View.VISIBLE
            saveCardSwitchHolder11?.view?.mainSwitch?.switchSaveMobile?.visibility = View.VISIBLE
            saveCardSwitchHolder11?.setSwitchToggleData(paymentType)
            activateActionButton()
            paymentActionType = paymentType
        } else {
            saveCardSwitchHolder11?.view?.mainSwitch?.visibility = View.GONE
            saveCardSwitchHolder11?.view?.mainSwitch?.switchSaveMobile?.visibility = View.GONE
           saveCardSwitchHolder11?.setSwitchToggleData(paymentType)
            unActivateActionButton()
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onPayCardCompleteAction(
        isCompleted: Boolean,
        paymentType: PaymentType,
        cardNumber: String,
        expiryDate: String,
        cvvNumber: String
    ) {
        activateActionButton()
        setPayButtonAction(paymentType, null)


    }

    // Override function to open NFC fragment and scan the card via NFC.
    override fun onClickNFC() {
        setSlideAnimation()
        removeViews(
            businessViewHolder,
            amountViewHolder1,
            cardViewHolder11,

            saveCardSwitchHolder11,
            paymentInputViewHolder,
            otpViewHolder,
            goPaySavedCardHolder,
            goPayViewsHolder
        )
        addViews(businessViewHolder, amountViewHolder1)
        fragmentManager.beginTransaction().remove(InlineViewFragment()).replace(
            R.id.webFrameLayout,
            nfcFragment
        ).commit()
        webFrameLayout.visibility = View.VISIBLE
        isNFCOpened = true
        amountViewHolder1.changeGroupAction(false)
        checkSelectedAmountInitiated()
    }


    // Override function to open card Scanner and scan the card.
    override fun onClickCardScanner() {
        setSlideAnimation()
        removeViews(
            businessViewHolder,
            amountViewHolder1,
            cardViewHolder11,
            saveCardSwitchHolder11,
            paymentInputViewHolder,
            otpViewHolder,
            goPaySavedCardHolder,
            goPayViewsHolder
        )
        addViews(businessViewHolder, amountViewHolder1)
        inLineCardLayout.visibility = View.VISIBLE
        FrameManager.getInstance().frameColor = Color.WHITE
        fragmentManager
            .beginTransaction()
            .replace(R.id.inline_container, inlineViewFragment)
            .commit()
        isInlineOpened = true
        amountViewHolder1.changeGroupAction(false)
        checkSelectedAmountInitiated()
    }

    private fun checkSelectedAmountInitiated() {
        if (this::selectedAmount.isInitialized && this::selectedCurrency.isInitialized) {
            amountViewHolder1.updateSelectedCurrency(
                displayItemsOpen,
                selectedAmount, selectedCurrency,
                currentAmount, currentCurrency
            )
        }
    }


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCurrencyClicked(currencySelected: String, currencyRate: BigDecimal) {
        if (::itemList.isInitialized) {
            for (i in itemList.indices) {
                println("item per unit start >>" + itemList[i].amountPerUnit)
                itemList[i].amountPerUnit = (itemList[i].getAmountPerUnit()?.times(currencyRate))
                // itemList[i].currency = currencySelected
                // selectedAmount = CurrencyFormatter.currencyFormat(currencyRate.toString())
                // selectedCurrency = currencySelected
                println("item per unit >>" + itemList[i].amountPerUnit)

            }
            itemsViewHolder1.setResetItemsRecylerView(itemList)

        }

        //  itemList[i].amount = (list[i].amount.toLong())
        //  itemList[i].currency = currencySelected
        selectedAmount = CurrencyFormatter.currencyFormat(currencyRate.toString())
        selectedCurrency = currencySelected

        amountViewHolder1.updateSelectedCurrency(
            displayItemsOpen,
            selectedAmount, selectedCurrency,
            currentAmount, currentCurrency
        )
            PaymentDataSource.setSelectedCurrency(selectedCurrency = selectedCurrency)
            PaymentDataSource.setSelectedAmount(currencyRate)

    if(::selectedCurrency.isInitialized){
                filterViewModels(selectedCurrency)
        }else   filterViewModels(currentCurrency)

    }

    @SuppressLint("ResourceType")
    override fun redirectLoadingFinished(done: Boolean) {
        if (::webFrameLayout.isInitialized)
            webFrameLayout.visibility = View.GONE
        removeAllViews()
        addViews(tabAnimatedActionButtonViewHolder11)
        if (done)
            tabAnimatedActionButtonViewHolder11.view.actionButton.changeButtonState(ActionButtonState.SUCCESS)
        else
            tabAnimatedActionButtonViewHolder11.view.actionButton.changeButtonState(ActionButtonState.ERROR)

        setSlideAnimation()
        removeAllViews()
    }

    override fun directLoadingFinished(done: Boolean) {
        println("directLoadingFinished>" + done)
        if (done) {
            if (::webFrameLayout.isInitialized)
                webFrameLayout.visibility = View.VISIBLE
           // removeViews(businessViewHolder,amountViewHolder1,cardViewHolder11,paymentInputViewHolder)
          //  saveCardSwitchHolder11?.view?.visibility = View.GONE
           // addViews(saveCardSwitchHolder11)
           // saveCardSwitchHolder11?.view?.cardSwitch?.payButton?.changeButtonState(ActionButtonState.SUCCESS)
        }


    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun setPayButtonAction(paymentTypeEnum: PaymentType, savedCardsModel: Any?) {
        /**
         * payment from onSelectPaymentOptionActionListener
         */
        saveCardSwitchHolder11?.view?.cardSwitch?.payButton?.setOnClickListener {
            when (paymentTypeEnum) {
                PaymentType.SavedCard -> {
                    /**
                     * Note PaymentType savedcard is changed to Card for saved card payments for extra fees and payment options*/
                    if (::selectedAmount.isInitialized && ::selectedCurrency.isInitialized) {
                        checkForExtraFees(
                            selectedAmount,
                            selectedCurrency,
                            PaymentType.CARD,
                            savedCardsModel
                        )
                    } else checkForExtraFees(
                        currentAmount,
                        currentCurrency,
                        PaymentType.CARD,
                        savedCardsModel
                    )

                    // setDifferentPaymentsAction(paymentTypeEnum,savedCardsModel)
                }
                PaymentType.WEB -> {
                    if (savedCardsModel != null) {
                        if (::selectedAmount.isInitialized && ::selectedCurrency.isInitialized) {
                            checkForExtraFees(
                                selectedAmount,
                                selectedCurrency,
                                paymentTypeEnum, savedCardsModel
                            )
                        } else checkForExtraFees(
                            currentAmount,
                            currentCurrency,
                            paymentTypeEnum,
                            savedCardsModel
                        )
                    }
                }
                PaymentType.CARD -> {
                    //activateActionButton()
                    if (::selectedAmount.isInitialized && ::selectedCurrency.isInitialized) {
                        checkForExtraFees(
                            selectedAmount,
                            selectedCurrency,
                            paymentTypeEnum, savedCardsModel
                        )
                    } else checkForExtraFees(
                        currentAmount,
                        currentCurrency,
                        paymentTypeEnum,
                        savedCardsModel
                    )


                }
                PaymentType.telecom -> {
                    checkForExtraFees(
                        currentAmount,
                        currentCurrency,
                        paymentTypeEnum, savedCardsModel
                    )
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun checkForExtraFees(
        selectedAmount: String,
        selectedCurrency: String,
        paymentTypeEnum: PaymentType,
        savedCardsModel: Any?
    ) {
        var extraFees: java.util.ArrayList<ExtraFee>? = null
        var paymentOption:PaymentOption? = null

        if(paymentTypeEnum == PaymentType.WEB ){
           savedCardsModel as PaymentOption
           extraFees = savedCardsModel?.extraFees
            fee = calculateExtraFeesAmount(savedCardsModel as PaymentOption)

       } else{
           for (i in paymentOptionsResponse.paymentOptions.indices) {
               if (paymentOptionsResponse.paymentOptions[i].paymentType == paymentTypeEnum) {
                   extraFees = paymentOptionsResponse.paymentOptions[i].extraFees
                   paymentOption = paymentOptionsResponse.paymentOptions[i]
               }
           }
           fee = calculateExtraFeesAmount(paymentOption)

       }
        val totalAmount = fee?.add(PaymentDataProvider()?.getSelectedCurrency()?.amount)
       if (calculateExtraFeesAmount(
               extraFees,
               paymentOptionsResponse.supportedCurrencies,
               PaymentDataProvider().getSelectedCurrency()
           )!! > BigDecimal.ZERO
        ) {
            showExtraFees(totalAmount.toString(), fee.toString(), paymentTypeEnum, savedCardsModel)
        }else if(savedCardsModel!=null) {
            if(paymentTypeEnum==PaymentType.CARD|| paymentTypeEnum==PaymentType.SavedCard){
                savedCardsModel as SavedCard
                if (savedCardsModel.paymentOptionIdentifier.toInt() == 3 || savedCardsModel.paymentOptionIdentifier.toInt() == 4) {
                    setDifferentPaymentsAction(PaymentType.SavedCard, savedCardsModel)
            }

           } else setDifferentPaymentsAction(paymentTypeEnum, savedCardsModel)
       }else setDifferentPaymentsAction(paymentTypeEnum, savedCardsModel)
    }


    @RequiresApi(Build.VERSION_CODES.N)
    private fun payActionSavedCard(savedCardsModel: SavedCard?) {
        saveCardSwitchHolder11?.view?.cardSwitch?.payButton?.changeButtonState(
            ActionButtonState.LOADING
        )
        saveCardSwitchHolder11?.view?.cardSwitch?.payButton?.getImageView(
            R.drawable.loader,
            1
        ) {

            startSavedCardPaymentProcess(savedCardsModel as SavedCard)
        }?.let { it1 ->
            saveCardSwitchHolder11?.view?.cardSwitch?.payButton?.addChildView(
                it1
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun startSavedCardPaymentProcess(savedCard: SavedCard?) {
        val createTokenSavedCard =
            CreateTokenSavedCard(savedCard?.id, PaymentDataSource?.getCustomer().identifier)
        cardViewModel?.processEvent(
            CardViewEvent.CreateTokenExistingCardEvent,
            this,
            null,
            null,
            null,
            null,
            null,
            null,
            createTokenSavedCard
        )
    }


//    override fun onStateChanged(state: ActionButtonState) {}

    private fun removeAllViews() {
        if (::businessViewHolder.isInitialized && ::amountViewHolder1.isInitialized && ::cardViewHolder11.isInitialized && ::paymentInputViewHolder.isInitialized &&
            ::goPayViewsHolder.isInitialized && ::otpViewHolder.isInitialized
        )
            removeViews(
                businessViewHolder,
                amountViewHolder1,
                cardViewHolder11,
                paymentInputViewHolder,
                saveCardSwitchHolder11,
                goPayViewsHolder,
                otpViewHolder
            )
    }

    private fun setAllSeparatorTheme() {
        val separatorViewTheme = SeparatorViewTheme()
        separatorViewTheme.strokeColor =
            Color.parseColor(ThemeManager.getValue("tapSeparationLine.backgroundColor"))
        separatorViewTheme.strokeHeight = ThemeManager.getValue("tapSeparationLine.height")
        businessViewHolder.view.topSeparatorLinear.topSeparator.setTheme(separatorViewTheme)
        amountViewHolder1.view.separator.setTheme(separatorViewTheme)
        cardViewHolder11.view.tapSeparatorViewLinear1.separator_1.setTheme(separatorViewTheme)
        goPaySavedCardHolder.view.tapSeparatorViewLinear.separator_.setTheme(separatorViewTheme)
        /**
         * set separator background
         */
        businessViewHolder.view.topSeparatorLinear.setBackgroundColor(
            (Color.parseColor(
                ThemeManager.getValue(
                    "merchantHeaderView.backgroundColor"
                )
            ))
        )
    }

    override fun onRecognitionSuccess(card: TapCard?) {
//to be added for unembossed cards
    }

    override fun onRecognitionFailure(error: String?) {

    }


    private fun removeInlineScanner() {
        if (isInlineOpened) {
            if (fragmentManager.findFragmentById(R.id.inline_container) != null)
                fragmentManager.beginTransaction()
                    .remove(fragmentManager.findFragmentById(R.id.inline_container)!!)
                    .commit()
            isInlineOpened = false
            inLineCardLayout.visibility = View.GONE
        }

    }

    fun handleScanFailedResult() {
        println("handleScanFailedResult card is")
        removeInlineScanner()
        removeViews(amountViewHolder1, businessViewHolder)
        addViews(
            businessViewHolder,
            amountViewHolder1,
            cardViewHolder11,
            paymentInputViewHolder,
            saveCardSwitchHolder11
        )

    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun handleScanSuccessResult(card: Card) {
        removeInlineScanner()
        removeViews(amountViewHolder1, businessViewHolder)
        addViews(
            businessViewHolder,
            amountViewHolder1,
            cardViewHolder11,
            paymentInputViewHolder,
            saveCardSwitchHolder11
        )
        println("scanned card is$card")
        callBinLookupApi(card.cardNumber?.substring(0, 6))

        Handler().postDelayed({
            val binLookupResponse: BINLookupResponse? = PaymentDataSource.getBinLookupResponse()
            if (PaymentDataSource.getCardType() != null && PaymentDataSource.getCardType() == CardType.ALL) {
                setScannedCardDetails(card)

            } else {
                if (binLookupResponse != null) {
                    paymentInputViewHolder.checkAllowedCardTypes(binLookupResponse)
                    setScannedCardDetails(card)
                }

            }
        }, 300)


        // paymentInputViewHolder.tapCardInputView.cardHolder.setText(card.cardHolderName)


    }

    private fun setScannedCardDetails(card: Card) {
        paymentInputViewHolder.tapCardInputView.setCardNumber(card.cardNumber)
        val dateParts: List<String>? = card.expirationDate?.split("/")
        val month = dateParts?.get(0)?.toInt()
        val year = dateParts?.get(1)?.toInt()
        if (month != null) {
            if (year != null) {
                paymentInputViewHolder.tapCardInputView.setExpiryDate(month, year)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun callBinLookupApi(binLookUpStr: String?) {
        cardViewModel.processEvent(
            CardViewEvent.RetreiveBinLookupEvent,
            CheckoutViewModel(), null, binLookUpStr, null, null
        )

    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun handleNFCScannedResult(emvCard: TapEmvCard) {
        removeViews(amountViewHolder1, businessViewHolder)
        addViews(
            businessViewHolder,
            amountViewHolder1,
            cardViewHolder11,
            paymentInputViewHolder,
            saveCardSwitchHolder11
        )

        callBinLookupApi(emvCard.cardNumber?.substring(0, 6))

        Handler().postDelayed({
            val binLookupResponse: BINLookupResponse? = PaymentDataSource.getBinLookupResponse()
            if (PaymentDataSource.getCardType() != null && PaymentDataSource.getCardType() == CardType.ALL) {
                setNfcCardDetails(emvCard)

            } else {
                if (binLookupResponse != null) {
                    paymentInputViewHolder.checkAllowedCardTypes(binLookupResponse)
                    setNfcCardDetails(emvCard)
                }

            }
        }, 300)

        removeNFCViewFragment()
    }

    private fun setNfcCardDetails(emvCard: TapEmvCard) {
        paymentInputViewHolder.tapCardInputView.setCardNumber(emvCard.cardNumber)
        convertDateString(emvCard.expireDate.toString())

    }

    private fun removeNFCViewFragment() {
        if (isNFCOpened)
            if (fragmentManager.findFragmentById(R.id.webFrameLayout) != null)
                fragmentManager.beginTransaction()
                    .remove(fragmentManager.findFragmentById(R.id.webFrameLayout)!!)
                    .commit()
        isNFCOpened = false
        webFrameLayout.visibility = View.GONE
    }


    private fun convertDateString(date: String) {
        val dateParts: List<String> = date.split(" ")
        val month = dateParts[2].toInt()
        val year = dateParts[5].toInt()
        paymentInputViewHolder.tapCardInputView.setExpiryDate(month, year)
    }

    private fun filteredByPaymentTypeAndCurrencyAndSortedList(
        list: java.util.ArrayList<PaymentOption>, paymentType: PaymentType, currency: String
    ): java.util.ArrayList<PaymentOption> {
        var currencyFilter: String? = currency
        val filters: java.util.ArrayList<Utils.List.Filter<PaymentOption>> =
            java.util.ArrayList<Utils.List.Filter<PaymentOption>>()

        /**
         * if trx currency not included inside supported currencies <i.e Merchant pass transaction currency that he is not allowed for>
         * set currency to first supported currency
        </i.e> */
        var trxCurrencySupported = false
        for (amountedCurrency in paymentOptionsResponse.supportedCurrencies) {
            if (amountedCurrency.currency == currencyFilter) {
                trxCurrencySupported = true
                break
            }
        }
        if (!trxCurrencySupported) currencyFilter =
            paymentOptionsResponse.supportedCurrencies[0].currency


        if (currencyFilter != null) {
            this.getCurrenciesFilter<PaymentOption>(currencyFilter)?.let { filters.add(it) }
        }
        //  filters.add(getPaymentOptionsFilter(paymentType))
        //  val filter: CompoundFilter<PaymentOption> = CompoundFilter(filters)
        //  val filtered: ArrayList<PaymentOption> = Utils.List.filter(list)

        var filtered: ArrayList<PaymentOption> =
            list.filter { items ->
                items.paymentType == paymentType && items.getSupportedCurrencies()?.contains(
                    currencyFilter
                ) == true
            } as ArrayList<PaymentOption>

        return list.filter { items ->
            items.paymentType == paymentType && items.getSupportedCurrencies()?.contains(
                currencyFilter
            ) == true
        } as ArrayList<PaymentOption>
    }

    private fun <E : CurrenciesSupport?> getCurrenciesFilter(
        currency: String
    ): Utils.List.Filter<E>? {
        return object : Utils.List.Filter<E> {
            override fun isIncluded(`object`: E): Boolean {
                return `object`?.getSupportedCurrencies()!!.contains(currency)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun filterViewModels(currency: String) {
        if (paymentOptionsResponse.paymentOptions != null)
            paymentOptionsWorker =
                java.util.ArrayList<PaymentOption>(paymentOptionsResponse.paymentOptions)
        if (paymentOptionsResponse.cards != null) {
            val savedCardsWorker: java.util.ArrayList<SavedCard> =
                java.util.ArrayList<SavedCard>(paymentOptionsResponse.cards)
        }
        val webPaymentOptions: java.util.ArrayList<PaymentOption> =
            filteredByPaymentTypeAndCurrencyAndSortedList(
                paymentOptionsWorker, PaymentType.WEB, currency
            )
        println("webPaymentOptions>>>>$webPaymentOptions")
        val cardPaymentOptions: java.util.ArrayList<PaymentOption> =
            filteredByPaymentTypeAndCurrencyAndSortedList(
                paymentOptionsWorker, PaymentType.CARD, currency
            )
        val hasWebPaymentOptions = webPaymentOptions.size > 0
        val hasCardPaymentOptions = cardPaymentOptions.size > 0
        logicToHandlePaymentDataType(webPaymentOptions, cardPaymentOptions)

    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun logicToHandlePaymentDataType(
        webPaymentOptions: ArrayList<PaymentOption>,
        cardPaymentOptions: ArrayList<PaymentOption>
    ) {
        if(PaymentDataSource.getPaymentDataType()!=null && PaymentDataSource.getPaymentDataType() == "WEB"){
            adapter.updateAdapterDataSavedCard(ArrayList())
            adapter.updateAdapterData(webPaymentOptions)
            saveCardSwitchHolder11?.view?.cardSwitch?.showOnlyPayButton()
        }else if(PaymentDataSource.getPaymentDataType() !=null && PaymentDataSource.getPaymentDataType() == "CARD"){
            adapter.updateAdapterData(ArrayList())
            paymentInputViewHolder.setDataFromAPI(cardPaymentOptions)
            saveCardSwitchHolder11?.view?.cardSwitch?.showOnlyPayButton()
        }else{
            adapter.updateAdapterData(webPaymentOptions)
            paymentInputViewHolder.setDataFromAPI(cardPaymentOptions)
        }
    }

    var title :String= LocalizationManager.getValue("extraFeesAlertTitle", "ExtraFees")

    @RequiresApi(Build.VERSION_CODES.N)
    private fun showExtraFees(
        totalAmount: String,
        extraFeesAmount: String,
        paymentType: PaymentType, savedCardsModel: Any?
    ) {

        val localizedMessage =
            "You will be charged an additional fee of $extraFeesAmount${PaymentDataProvider().getSelectedCurrency()?.currency} for this type of payment, totaling an amount of $totalAmount${PaymentDataProvider().getSelectedCurrency()?.currency}"
        CustomUtils.showDialog(
            title,
            localizedMessage,
            context,
            3,
            this,
            paymentType,
            savedCardsModel, true
        )


    }


    @RequiresApi(Build.VERSION_CODES.N)
    override fun dialogueExecuteExtraFees(
        response: String,
        paymentType: PaymentType,
        savedCardsModel: Any?
    ) {
        if (response == "YES") {
            println("savedCardsModel>>>>" + savedCardsModel)
            if(savedCardsModel!=null) {
                if(paymentType == PaymentType.CARD){
                    savedCardsModel as SavedCard
                    if (savedCardsModel.paymentOptionIdentifier.toInt() == 3 || savedCardsModel.paymentOptionIdentifier.toInt() == 4) {
                        setDifferentPaymentsAction(PaymentType.SavedCard, savedCardsModel)
                    }
                }
                else setDifferentPaymentsAction(paymentType, savedCardsModel)
            }else setDifferentPaymentsAction(paymentType, savedCardsModel)
        } else {
            when {
                paymentType === PaymentType.WEB -> {
                    // fireWebPaymentExtraFeesUserDecision(ExtraFeesStatus.REFUSE_EXTRA_FEES)
                }
                paymentType === PaymentType.CARD -> {
//                    fireCardPaymentExtraFeesUserDecision(ExtraFeesStatus.REFUSE_EXTRA_FEES)
                }
                paymentType === PaymentType.SavedCard -> {
                    //  fireSavedCardPaymentExtraFeesUserDecision(ExtraFeesStatus.REFUSE_EXTRA_FEES)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun setDifferentPaymentsAction(
        paymentType: PaymentType,
        savedCardsModel: Any?
    ) {
        when {
            paymentType === PaymentType.WEB -> {
                if (savedCardsModel != null) {
                    onClickRedirect(savedCardsModel)
                }
            }
            paymentType === PaymentType.CARD -> {
                onClickCardPayment()
            }
            paymentType === PaymentType.SavedCard -> {

                payActionSavedCard(savedCardsModel as SavedCard)
            }
        }
    }

    /**
     * Calculate extra fees amount big decimal.
     *
     * @param paymentOption the payment option
     * @return the big decimal
     */
    open fun calculateExtraFeesAmount(paymentOption: PaymentOption?): BigDecimal? {
        return if (paymentOption != null) {

            val amount = PaymentDataProvider().getSelectedCurrency()
            var extraFees: java.util.ArrayList<ExtraFee>? = paymentOption.extraFees
            if (extraFees == null) extraFees = java.util.ArrayList()
            val supportedCurrencies: java.util.ArrayList<SupportedCurrencies>? = PaymentDataProvider().getSupportedCurrencies()
            calculateExtraFeesAmount(extraFees, supportedCurrencies, amount)
        } else BigDecimal.ZERO
    }
    fun View.fadeVisibility(visibility: Int, duration: Long = 3000) {
        val transition: Transition = Fade()
        transition.duration = duration
        transition.addTarget(this)
        TransitionManager.beginDelayedTransition(this.parent as ViewGroup, transition)
        this.visibility = visibility
    }
}














