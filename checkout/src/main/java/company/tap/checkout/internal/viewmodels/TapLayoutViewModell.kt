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
import company.tap.checkout.internal.adapter.CardTypeAdapterUIKIT
import company.tap.checkout.internal.adapter.CurrencyTypeAdapter
import company.tap.checkout.internal.adapter.GoPayCardAdapterUIKIT
import company.tap.checkout.internal.api.enums.ExtraFeesStatus
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
import company.tap.nfcreader.open.reader.TapEmvCard
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapuilibrary.themekit.ThemeManager
import company.tap.tapuilibrary.themekit.theme.SeparatorViewTheme
import company.tap.tapuilibrary.uikit.enums.ActionButtonState
import company.tap.tapuilibrary.uikit.enums.GoPayLoginMethod
import company.tap.tapuilibrary.uikit.fragment.NFCFragment
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
open class TapLayoutViewModel : ViewModel(), BaseLayouttManager, OnCardSelectedActionListener,
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
    private var saveCardSwitchHolder11: SwitchViewHolder11? = null

    private lateinit var paymentInputViewHolder: PaymenttInputViewHolder
    private lateinit var goPaySavedCardHolder: GoPaySavedCardHolder
    private lateinit var businessViewHolder: BusinessViewHolder
    private lateinit var amountViewHolder1: AmountViewHolder1
    private lateinit var currencyAdapter: CurrencyTypeAdapter
    private lateinit var goPayAdapter: GoPayCardAdapterUIKIT
    private lateinit var goPayViewsHolder: GoPayViewsHolder
    private lateinit var itemsViewHolder1: ItemsViewHolder1
    private lateinit var cardViewHolder11: CardViewHolder11
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var fragmentManager: FragmentManager
    private lateinit var bottomSheetLayout: FrameLayout
    private lateinit var selectedAmount: String
    private lateinit var selectedCurrency: String

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
    private lateinit var itemList: List<Items1>
    private lateinit var selectedPaymentOption: PaymentOption
    private lateinit var orderList: Order1

    @SuppressLint("StaticFieldLeak")
    private lateinit var context: Context
    private lateinit var cardViewModel: CardViewModel
    private  var otpTypeString: PaymentTypeEnum = PaymentTypeEnum.SAVEDCARD
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

    private fun initOtpActionButton() {
        otpViewHolder.otpView.otpViewActionButton.setOnClickListener {
            when (otpTypeString) {
                PaymentTypeEnum.GOPAY -> goPayViewsHolder.onOtpButtonConfirmationClick(otpViewHolder.otpView.otpViewInput1.text.toString() + otpViewHolder.otpView.otpViewInput2.text.toString())
                PaymentTypeEnum.SAVEDCARD -> confirmOTPCode(otpViewHolder.otpView.otpViewInput1.text.toString() + otpViewHolder.otpView.otpViewInput2.text.toString())
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
                    paymentInputViewHolder.tapMobileInputView.clearNumber()
                    CustomUtils.showDialog(
                        "Payment Done",
                        "Payment id 2e412321eqqweq32131",
                        context,
                        1,
                        this
                    )
                }
            }
        }

    }

    private fun confirmOTPCode(otpCode: String) {
        when(PaymentDataSource?.getTransactionMode()){
            TransactionMode.PURCHASE-> sendChargeOTPCode(otpCode)
            TransactionMode.AUTHORIZE_CAPTURE-> sendAuthorizeOTPCode(otpCode)
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

        // nfcViewHolder = NFCViewHolder(context as Activity, context, this, fragmentManager)
    }

    private fun initSwitchAction() {
        saveCardSwitchHolder11?.view?.mainSwitch?.mainSwitch?.visibility = View.VISIBLE
        // saveCardSwitchHolder11?.view?.mainSwitch?.mainSwitch?.visibility = View.VISIBLE
        //   saveCardSwitchHolder11?.view?.mainSwitch?.mainSwitchLinear?.setBackgroundColor(Color.parseColor(ThemeManager.getValue("TapSwitchView.main.backgroundColor")))
    }

    private fun initAmountAction() {
        amountViewHolder1.setOnItemsClickListener {}
        amountViewHolder1.view.amount_section.mainKDAmountValue.visibility = View.GONE
    }


    override fun displayStartupLayout(enabledSections: ArrayList<SectionType>) {
        //Todo based on api response logic for switch case
        when (PaymentDataSource?.getTransactionMode()) {

            company.tap.checkout.open.enums.TransactionMode.TOKENIZE_CARD -> {
                addViews(
                    businessViewHolder,
                    paymentInputViewHolder,
                    saveCardSwitchHolder11
                )

            }
            company.tap.checkout.open.enums.TransactionMode.SAVE_CARD -> {
                addViews(
                    businessViewHolder,
                    paymentInputViewHolder,
                    saveCardSwitchHolder11
                )
            }
            else -> {
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
        adapter.goPayOpenedfromMain(true)
        adapter.removeItems()
    }

    override fun controlCurrency(display: Boolean) {
        if (display) caseDisplayControlCurrency()
        else caseNotDisplayControlCurrency()

        displayItemsOpen = !display
        amountViewHolder1.changeGroupAction(!display)
        // if (this::currentAmount.isInitialized)
        if (currentAmount != null && this::selectedAmount.isInitialized && this::selectedCurrency.isInitialized)
            amountViewHolder1.updateSelectedCurrency(
                displayItemsOpen,
                selectedAmount, selectedCurrency,
                currentAmount, currentCurrency
            )
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
        addViews(businessViewHolder, amountViewHolder1, itemsViewHolder1)
        itemsViewHolder1.setDatafromAPI(
            allCurrencies.value as ArrayList<SupportedCurrencies>,
            null
        )

        //   itemsViewHolder1.setItemsRecylerView()
        itemsViewHolder1.setCurrencyRecylerView()
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
        // itemsViewHolder1.resetView()
        // itemsViewHolder1.setItemsRecylerView()
        itemsViewHolder1.setCurrencyRecylerView()
        frameLayout.visibility = View.GONE
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("SetTextI18n")
    override fun displayOTPView(mobileNumber: String, otpType: String, chargeResponse: Charge?) {
        setSlideAnimation()
        if (otpType == PaymentTypeEnum.GOPAY.name)
            displayOtpGoPay(mobileNumber)
        else if (otpType == PaymentTypeEnum.telecom.name) displayOtpTelecoms(mobileNumber)
        else displayOtpCharge(mobileNumber, chargeResponse)
    }

    private fun displayOtpGoPay(mobileNumber: String) {
        // otpTypeString = PaymentTypeEnum.GOPAY //temporray
        removeViews(
            cardViewHolder11,
            paymentInputViewHolder, saveCardSwitchHolder11
        )
        addViews(otpViewHolder)
        otpViewHolder.otpView.visibility = View.VISIBLE
        otpViewHolder.otpView.mobileNumberText.text = mobileNumber
        otpViewHolder.otpView.changePhone.setOnClickListener {
            goPayViewsHolder.onChangePhoneClicked()
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun displayOtpCharge(mobileNumber: String, chargeResponse: Charge?) {
        removeViews(
            cardViewHolder11,
            paymentInputViewHolder, saveCardSwitchHolder11, otpViewHolder
        )
        addViews(otpViewHolder)
        otpViewHolder.otpView.visibility = View.VISIBLE
        otpViewHolder.otpView.mobileNumberText.text = mobileNumber
        otpViewHolder.otpView.changePhone.visibility = View.INVISIBLE
        otpViewHolder.otpView.timerText.setOnClickListener {
            resendOTPCode(chargeResponse)

        }

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
        cardViewModel?.requestAuthenticateForAuthorizeTransaction(this, authorize)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun resendChargeOTPCode(charge: Charge?) {
        if (charge != null) {
            cardViewModel?.requestAuthenticateForChargeTransaction(this, charge)
        }
    }

    private fun displayOtpTelecoms(mobileNumber: String) {
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
        if (mobileNumber.length > 7)
            replaced = (mobileNumber.toString()).replaceRange(1, 6, "....")
        countryCodeReplaced =
            goPayViewsHolder.goPayLoginInput.countryCodePicker.selectedCountryCode.replace(
                "+",
                " "
            )
        otpViewHolder.otpView.mobileNumberTextNormalPay.text = "+$countryCodeReplaced$replaced"
    }


    override fun displayRedirect(url: String) {
        if(otpViewHolder.otpView.isVisible){
            removeViews(businessViewHolder, amountViewHolder1, otpViewHolder)
        }
        this.redirectURL = url
        println("redirectURL>>>" + redirectURL)
        if (::redirectURL.isInitialized && redirectURL != null && ::fragmentManager.isInitialized) {
            fragmentManager.beginTransaction()
                .replace(
                    R.id.webFrameLayout, WebFragment.newInstance(
                        redirectURL,
                        this, cardViewModel
                    )
                ).commitNow()

        }
    }

    override fun displaySaveCardOptions() {}

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
        if (::businessViewHolder.isInitialized && PaymentDataSource?.getTransactionMode() != company.tap.checkout.open.enums.TransactionMode.TOKENIZE_CARD) {
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
        if (::itemsViewHolder1.isInitialized)
            paymentOptionsResponse?.supportedCurrencies?.let {
                itemsViewHolder1.setDatafromAPI(
                    it,
                    null
                )
            }
        if (paymentOptionsResponse?.supportedCurrencies != null && ::amountViewHolder1.isInitialized)
            paymentOptionsResponse?.currency?.let {
                amountViewHolder1.setDatafromAPI(
                    paymentOptionsResponse?.supportedCurrencies[0].amount.toString(),
                    it,
                    "1"
                )
            }
        if (::itemsViewHolder1.isInitialized)
            paymentOptionsResponse?.supportedCurrencies?.let {
                itemsViewHolder1.setDatafromAPI(
                    it,
                    null
                )
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
            println("getCardType" + PaymentDataSource?.getCardType())
            if (PaymentDataSource?.getCardType() != null && PaymentDataSource?.getCardType() != CardType.ALL) {
                filterSavedCardTypes(savedCardList.value as List<SavedCard>)
            } else adapter.updateAdapterDataSavedCard(savedCardList.value as List<SavedCard>)
        }

        cardViewHolder11.view.mainChipgroup.chipsRecycler.adapter = adapter
        // goPaySavedCardHolder.view.goPayLoginView.chipsRecycler.adapter = goPayAdapter
        cardViewHolder11.view.mainChipgroup.groupAction?.visibility = View.VISIBLE
        cardViewHolder11.view.mainChipgroup.groupAction?.setOnClickListener {
            setMainChipGroupActionListener()
        }
        // filterCardTypes(paymentOptionsList.value as ArrayList<PaymentOption>)
        paymentOptionsWorker =
            java.util.ArrayList<PaymentOption>(paymentOptionsResponse?.paymentOptions)

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

        adapter?.updateAdapterDataSavedCard(filteredSavedCardList)
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
    override fun didDialogueExecute(response: String) {
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
                    PaymentDataSource?.getCustomer().identifier,
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
            bottomSheetDialog.dismissWithAnimation
            bottomSheetDialog.dismiss()
        }

    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun dialogueExecuteExtraFees(response: String, paymentType: PaymentType) {
        if (response == "YES") {
            when {
                paymentType === PaymentType.WEB -> {
                    //fireWebPaymentExtraFeesUserDecision(ExtraFeesStatus.ACCEPT_EXTRA_FEES)
                }
                paymentType === PaymentType.CARD -> {

                    fireCardPaymentExtraFeesUserDecision(ExtraFeesStatus.ACCEPT_EXTRA_FEES)
                }
                paymentType === PaymentType.SavedCard -> {

                    // fireSavedCardPaymentExtraFeesUserDecision(ExtraFeesStatus.ACCEPT_EXTRA_FEES)
                }
            }
        } else {
            when {
                paymentType === PaymentType.WEB -> {
                    // fireWebPaymentExtraFeesUserDecision(ExtraFeesStatus.REFUSE_EXTRA_FEES)
                }
                paymentType === PaymentType.CARD -> {
                    fireCardPaymentExtraFeesUserDecision(ExtraFeesStatus.REFUSE_EXTRA_FEES)
                }
                paymentType === PaymentType.SavedCard -> {
                    //  fireSavedCardPaymentExtraFeesUserDecision(ExtraFeesStatus.REFUSE_EXTRA_FEES)
                }
            }

        }
    }

    override fun deleteSelectedCardListener(delSelectedCard: DeleteCardResponse) {
        println("delSelectedCard value is" + delSelectedCard.deleted)
        //todo check why delSelectedCard.deleted is changing to false while its true from the API
        if (!delSelectedCard?.deleted) {
            adapter.deleteSelectedCard(selectedItemsDel)
            adapter.updateShaking(false)
            deleteCard = false
            //saveCardSwitchHolder11?.view?.cardSwitch?.payButton?.changeButtonState(ActionButtonState.SUCCESS)
        }
    }


    private fun removeViews(vararg viewHolders: TapBaseViewHolder?) {
        viewHolders.forEach {
            //   sdkLayout.removeView(it.view)
            Handler(Looper.getMainLooper()).postDelayed(Runnable {
                sdkLayout.removeView(it?.view)
//                val animation = AnimationUtils.loadAnimation(context, R.anim.fade_out)
//                it.view.startAnimation(animation)

            }, 0)
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
        saveCardSwitchHolder11?.view?.cardSwitch?.payButton?.changeButtonState(ActionButtonState.SUCCESS)
        saveCardSwitchHolder11?.view?.cardSwitch?.payButton?.setButtonDataSource(
            false,
            context.let { LocalizationManager.getLocale(it).language },
            LocalizationManager.getValue("pay", "ActionButton"),
            Color.parseColor(ThemeManager.getValue("actionButton.Invalid.backgroundColor")),
            Color.parseColor(ThemeManager.getValue("actionButton.Invalid.titleLabelColor"))
        )
        //saveCardSwitchHolder11.view.cardSwitch.showOnlyPayButton()
        saveCardSwitchHolder11?.view?.cardSwitch?.payButton?.isActivated = false
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCardSelectedAction(isSelected: Boolean, savedCardsModel: Any) {
        when (savedCardsModel) {
            is SavedCard -> {
                activateActionButton()
                setPayButtonAction(PaymentType.SavedCard, savedCardsModel)
            }
            else -> {
                if ((savedCardsModel as PaymentOption).paymentType == PaymentType.WEB) {
                    activateActionButton()
                    setPayButtonAction(PaymentType.WEB, savedCardsModel)
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
            selectedPaymentOption = savedCardsModel as PaymentOption
            cardViewModel.processEvent(
                CardViewEvent.ChargeEvent,
                this,
                selectedPaymentOption,
                null,
                null,
                null
            )

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
        val title: String = LocalizationManager.getValue("deletegoPayCard", "GoPay")
        //if(title.contains("?")) title.replace("?","")
        CustomUtils.showDialog(
            title.replace("?", "") + maskedCardNumber + " ?",
            LocalizationManager.getValue(
                "deleteMessage",
                "GoPay"
            ),
            context,
            2,
            this
        )
        selectedItemsDel = itemId
        deleteCard = true

    }

    override fun onGoPayLogoutClicked(isClicked: Boolean) {
        if (isClicked) CustomUtils.showDialog(
            LocalizationManager.getValue("goPaySignOut", "GoPay"), LocalizationManager.getValue(
                "goPaySaveCards",
                "GoPay"
            ), context, 2, this
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
        if (isCompleted) {
            saveCardSwitchHolder11?.view?.mainSwitch?.visibility = View.VISIBLE
            saveCardSwitchHolder11?.view?.mainSwitch?.switchSaveMobile?.visibility = View.VISIBLE
            saveCardSwitchHolder11?.setSwitchToggleData(paymentType)
            activateActionButton()
            paymentActionType = paymentType
        } else unActivateActionButton()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onPayCardCompleteAction(
        isCompleted: Boolean,
        paymentType: PaymentType,
        cardNumber: String,
        expiryDate: String,
        cvvNumber: String
    ) {
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
        currentCurrency = paymentOptionsResponse.currency
        currentAmount =
            CurrencyFormatter.currencyFormat(paymentOptionsResponse.supportedCurrencies[0].amount.toString())
        //  itemList[i].amount = (list[i].amount.toLong())
        //  itemList[i].currency = currencySelected
        selectedAmount = CurrencyFormatter.currencyFormat(currencyRate.toString())
        selectedCurrency = currencySelected
        // itemsViewHolder1.setResetItemsRecylerView(list)
        amountViewHolder1.updateSelectedCurrency(
            displayItemsOpen,
            selectedAmount, selectedCurrency,
            currentAmount, currentCurrency
        )
        PaymentDataSource.setSelectedCurrency(selectedCurrency = selectedCurrency)
        PaymentDataSource.setSelectedAmount(currencyRate)
        filterViewModels(currencySelected)
    }

    @SuppressLint("ResourceType")
    override fun redirectLoadingFinished(done: Boolean) {
        if (done) {
            if (::webFrameLayout.isInitialized)
                webFrameLayout.visibility = View.GONE
            removeAllViews()
            //  addViews(saveCardSwitchHolder11)
            /*          saveCardSwitchHolder11?.view?.cardSwitch?.showOnlyPayButton()
                      saveCardSwitchHolder11?.view?.cardviewSwitch?.visibility = View.GONE
                      saveCardSwitchHolder11?.view?.cardSwitch?.payButton?.isActivated = true
                      saveCardSwitchHolder11?.view?.cardSwitch?.payButton?.changeButtonState(ActionButtonState.SUCCESS)
                      saveCardSwitchHolder11?.view?.cardSwitch?.payButton?.getImageView(
                              R.drawable.success,
                              1
                      ) {
                          removeAllViews()
                          bottomSheetDialog.dismiss()
                      }?.let {
                          saveCardSwitchHolder11?.view?.cardSwitch?.payButton?.addChildView(
                                  it
                          )
                      }*/
        }
    }

    override fun directLoadingFinished(done: Boolean) {
        if (done) {
            if (::webFrameLayout.isInitialized)
                webFrameLayout.visibility = View.VISIBLE
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
                    payActionSavedCard(savedCardsModel as SavedCard)
                }
                PaymentType.WEB -> {
                    if (savedCardsModel != null) {
                        onClickRedirect(savedCardsModel)
                    }
                }
                PaymentType.CARD -> {
                    activateActionButton()
                    showExtraFees(
                        currentAmount,
                        currentCurrency,
                        paymentTypeEnum
                    )
//                    onClickCardPayment(savedCardsModel)
                }
                PaymentType.telecom -> {
                }
            }
        }
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

            /*  CustomUtils.showDialog(
                      "Payment Done",
                      "Payment id 2e41232132131",
                      context,
                      1,
                      this
              )*/
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
        if(::businessViewHolder.isInitialized&&::amountViewHolder1.isInitialized&& ::cardViewHolder11.isInitialized&& ::paymentInputViewHolder.isInitialized&&
                ::goPayViewsHolder.isInitialized  && ::otpViewHolder.isInitialized )
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
                fragmentManager?.beginTransaction()
                    .remove(fragmentManager?.findFragmentById(R.id.inline_container)!!)
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
        paymentInputViewHolder.tapCardInputView.setCardNumber(card.cardNumber)
        // paymentInputViewHolder.tapCardInputView.cardHolder.setText(card.cardHolderName)
        val dateParts: List<String>? = card.expirationDate?.split("/")
        val month = dateParts?.get(0)?.toInt()
        val year = dateParts?.get(1)?.toInt()
        if (month != null) {
            if (year != null) {
                paymentInputViewHolder.tapCardInputView.setExpiryDate(month, year)
            }
        }

    }

    fun handleNFCScannedResult(emvCard: TapEmvCard) {
        removeViews(amountViewHolder1, businessViewHolder)
        addViews(
            businessViewHolder,
            amountViewHolder1,
            cardViewHolder11,
            paymentInputViewHolder,
            saveCardSwitchHolder11
        )
        paymentInputViewHolder.tapCardInputView.setCardNumber(emvCard.cardNumber)
        convertDateString(emvCard.expireDate.toString())
        removeNFCViewFragment()
    }

    private fun removeNFCViewFragment() {
        if (isNFCOpened)
            if (fragmentManager.findFragmentById(R.id.webFrameLayout) != null)
                fragmentManager?.beginTransaction()
                    .remove(fragmentManager?.findFragmentById(R.id.webFrameLayout)!!)
                    .commit()
        isNFCOpened = false
        webFrameLayout?.visibility = View.GONE
    }


    private fun convertDateString(date: String) {
        val dateParts: List<String>? = date?.split(" ")
        val month = dateParts?.get(2)?.toInt()
        val year = dateParts?.get(5)?.toInt()
        if (month != null) {
            if (year != null) paymentInputViewHolder.tapCardInputView.setExpiryDate(month, year)
        }
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
                list.filter { items -> items.paymentType == paymentType && items.getSupportedCurrencies()?.contains(
                    currencyFilter
                ) == true } as ArrayList<PaymentOption>

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
        paymentOptionsWorker =
            java.util.ArrayList<PaymentOption>(paymentOptionsResponse.paymentOptions)
        val savedCardsWorker: java.util.ArrayList<SavedCard> =
            java.util.ArrayList<SavedCard>(paymentOptionsResponse.cards)
        // val viewModelResult: java.util.ArrayList<PaymentOptionViewModel> = java.util.ArrayList<PaymentOptionViewModel>()

        val webPaymentOptions: java.util.ArrayList<PaymentOption> =
            filteredByPaymentTypeAndCurrencyAndSortedList(
                paymentOptionsWorker, PaymentType.WEB, currency
            )
        val cardPaymentOptions: java.util.ArrayList<PaymentOption> =
            filteredByPaymentTypeAndCurrencyAndSortedList(
                paymentOptionsWorker, PaymentType.CARD, currency
            )
        val hasWebPaymentOptions = webPaymentOptions.size > 0
        val hasCardPaymentOptions = cardPaymentOptions.size > 0
        val hasOtherPaymentOptions = hasWebPaymentOptions || hasCardPaymentOptions
        adapter.updateAdapterData(webPaymentOptions)
        paymentInputViewHolder.setDataFromAPI(cardPaymentOptions)
    }

    var title = "Confirm Extra charges"

    private fun showExtraFees(
         amount: String,
        extraFeesAmount: String,
        paymentType: PaymentType
    ) {
//        if (calculateExtraFeesAmount(
//                (savedCardsModel as PaymentOption).extraFees,
//                allCurrencies.value as ArrayList<SupportedCurrencies>,
//                null
//            )?.compareTo(
//                BigDecimal.ZERO
//            )!! > 0
//        ) {
            var localizedMessage =
                "You will be charged an additional fee of $extraFeesAmount for this type of payment, totaling an amount of $amount"
            CustomUtils.showDialog(title, localizedMessage, context, 3, this, paymentType)

//        }else{    onClickCardPayment()  }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    open fun fireCardPaymentExtraFeesUserDecision(
        userChoice: ExtraFeesStatus?,
        ) {
        when (userChoice) {
            ExtraFeesStatus.ACCEPT_EXTRA_FEES, ExtraFeesStatus.NO_EXTRA_FEES -> {
                "continue"
                onClickCardPayment()
            }
            ExtraFeesStatus.REFUSE_EXTRA_FEES -> {

//                unActivateActionButton()
            }
        }
    }

}












