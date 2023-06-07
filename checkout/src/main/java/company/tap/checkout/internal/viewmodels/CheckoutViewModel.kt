package company.tap.checkout.internal.viewmodels

import SupportedCurrencies
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ShapeDrawable
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.text.format.DateFormat
import android.util.Log
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import androidx.annotation.RestrictTo
import androidx.core.view.isNotEmpty
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.transition.*
import cards.pay.paycardsrecognizer.sdk.FrameManager
import cards.pay.paycardsrecognizer.sdk.ui.InlineViewCallback
import com.bugfender.sdk.Bugfender
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.android.gms.wallet.PaymentData
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.google.gson.JsonObject
import company.tap.cardinputwidget2.CardBrandSingle
import company.tap.cardinputwidget2.CardInputUIStatus
import company.tap.cardinputwidget2.widget.CardInputListener
import company.tap.cardscanner.*
import company.tap.checkout.R
import company.tap.checkout.internal.PaymentDataProvider
import company.tap.checkout.internal.adapter.CardAdapterUIKIT
import company.tap.checkout.internal.adapter.CurrencyTypeAdapter
import company.tap.checkout.internal.adapter.GoPayCardAdapterUIKIT
import company.tap.checkout.internal.adapter.ItemAdapter
import company.tap.checkout.internal.api.enums.ChargeStatus
import company.tap.checkout.internal.api.enums.PaymentType
import company.tap.checkout.internal.api.models.*
import company.tap.checkout.internal.api.requests.CreateTokenGPayRequest
import company.tap.checkout.internal.api.responses.DeleteCardResponse
import company.tap.checkout.internal.api.responses.MerchantData
import company.tap.checkout.internal.api.responses.PaymentOptionsResponse
import company.tap.checkout.internal.apiresponse.CardViewEvent
import company.tap.checkout.internal.apiresponse.CardViewModel
import company.tap.checkout.internal.apiresponse.testmodels.GoPaySavedCards
import company.tap.checkout.internal.cache.SharedPrefManager
import company.tap.checkout.internal.cache.UserSupportedLocaleForTransactions
import company.tap.checkout.internal.enums.PaymentTypeEnum
import company.tap.checkout.internal.enums.SectionType
import company.tap.checkout.internal.enums.ThemeMode
import company.tap.checkout.internal.enums.WebViewType
import company.tap.checkout.internal.interfaces.*
import company.tap.checkout.internal.utils.*
import company.tap.checkout.internal.utils.AmountCalculator.calculateExtraFeesAmount
import company.tap.checkout.internal.viewholders.*
import company.tap.checkout.internal.webview.WebFragment
import company.tap.checkout.internal.webview.WebViewContract
import company.tap.checkout.open.CheckOutActivity
import company.tap.checkout.open.CheckoutFragment
import company.tap.checkout.open.controller.SDKSession
import company.tap.checkout.open.controller.SDKSession.tabAnimatedActionButton
import company.tap.checkout.open.controller.SessionManager
import company.tap.checkout.open.data_managers.PaymentDataSource
import company.tap.checkout.open.enums.CardType
import company.tap.checkout.open.enums.TransactionMode
import company.tap.checkout.open.models.ItemsModel
import company.tap.nfcreader.open.reader.TapEmvCard
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapuilibraryy.themekit.ThemeManager
import company.tap.tapuilibraryy.themekit.theme.SeparatorViewTheme
import company.tap.tapuilibraryy.uikit.AppColorTheme
import company.tap.tapuilibraryy.uikit.animation.MorphingAnimation
import company.tap.tapuilibraryy.uikit.datasource.LoyaltyHeaderDataSource
import company.tap.tapuilibraryy.uikit.enums.ActionButtonState
import company.tap.tapuilibraryy.uikit.enums.GoPayLoginMethod
import company.tap.tapuilibraryy.uikit.fragment.NFCFragment
import company.tap.tapuilibraryy.uikit.interfaces.TabAnimatedButtonListener
import company.tap.tapuilibraryy.uikit.ktx.loadAppThemManagerFromPath
import company.tap.tapuilibraryy.uikit.ktx.makeLinks
import company.tap.tapuilibraryy.uikit.ktx.setTopBorders
import company.tap.tapuilibraryy.uikit.utils.MetricsUtil
import company.tap.tapuilibraryy.uikit.views.TabAnimatedActionButton
import company.tap.tapuilibraryy.uikit.views.TapBrandView
import kotlinx.android.synthetic.main.amountview_layout.view.*
import kotlinx.android.synthetic.main.businessview_layout.view.*
import kotlinx.android.synthetic.main.cardviewholder_layout1.view.*
import kotlinx.android.synthetic.main.fragment_checkouttaps.view.*
import kotlinx.android.synthetic.main.gopaysavedcard_layout.view.*
import kotlinx.android.synthetic.main.itemviewholder_layout.view.*
import kotlinx.android.synthetic.main.loyalty_view_layout.view.*
import kotlinx.android.synthetic.main.otpview_layout.view.*
import kotlinx.android.synthetic.main.switch_layout.view.*
import mobi.foo.benefitinapp.data.Transaction
import mobi.foo.benefitinapp.listener.CheckoutListener
import org.json.JSONException
import org.json.JSONObject
import java.math.BigDecimal
import java.util.*
import kotlin.collections.ArrayList
import kotlin.properties.Delegates


/**
 *
 * Copyright © 2020 Tap Payments. All rights reserved.
 *
 */

@RestrictTo(RestrictTo.Scope.LIBRARY)
open class CheckoutViewModel : ViewModel(), BaseLayoutManager, OnCardSelectedActionListener,
    PaymentCardComplete, onCardNFCCallListener, OnCurrencyChangedActionListener, WebViewContract,
    TapTextRecognitionCallBack, TapScannerCallback, CheckoutListener, TabAnimatedButtonListener,
    MorphingAnimation.OnAnimationEndListener {
    private var savedCardList: MutableList<SavedCard>? = mutableListOf()
    private var arrayListSavedCardSize = ArrayList<SavedCard>()
    private var disabledPaymentOptionList = arrayListOf<PaymentOption>()
    private var supportedLoyalCards = MutableLiveData<List<LoyaltySupportedCurrency>>()
    private var goPayCardList = MutableLiveData<List<GoPaySavedCards>>()
    private var selectedViewToBeDeletedFromCardViewHolder: ViewGroup? = null
    private var viewToBeBlurCardViewHolder: View? = null
    var newColorVal: Int? = null

    private var allCurrencies = MutableLiveData<List<SupportedCurrencies>>()
    private var selectedItemsDel by Delegates.notNull<Int>()
    private var cardIDToBeDeleted: Int? = 0

    private val isShaking = MutableLiveData<Boolean>()
    val localCurrencyReturned = MutableLiveData<Boolean>()
    val powerdByTapAnimationFinished = MutableLiveData<Boolean>()
    val isItemsAreOpend = MutableLiveData<Boolean>()

    private var deleteCard: Boolean = false
    private var displayItemsOpen: Boolean = false
    private var displayOtpIsOpen: Boolean = false
    private var saveCardSwitchHolder: SwitchViewHolder? = null

    private lateinit var title: String
    private lateinit var paymentInlineViewHolder: PaymentInlineViewHolder
    private lateinit var goPaySavedCardHolder: GoPaySavedCardHolder
    private lateinit var businessViewHolder: BusinessViewHolder
    private lateinit var amountViewHolder: AmountViewHolder
    private lateinit var currencyAdapter: CurrencyTypeAdapter
    private lateinit var goPayAdapter: GoPayCardAdapterUIKIT
    private lateinit var goPayViewsHolder: GoPayViewsHolder
    private lateinit var itemsViewHolder: ItemsViewHolder
    private lateinit var cardViewHolder: CardViewHolder
    private lateinit var webViewHolder: WebViewHolder
    private lateinit var asynchronousPaymentViewHolder: AsynchronousPaymentViewHolder
    private lateinit var loyaltyViewHolder: LoyaltyViewHolder

    private var tabAnimatedActionButtonViewHolder: TabAnimatedActionButtonViewHolder? = null
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var fragmentManager: FragmentManager

    @SuppressLint("StaticFieldLeak")
    private lateinit var bottomSheetLayout: FrameLayout

    @Nullable
    internal lateinit var selectedAmount: String

    @Nullable
    lateinit var selectedCurrency: String

    companion object {
        var currencySelectedForCheck: String = ""
    }

    private var fee: BigDecimal? = BigDecimal.ZERO
    private var webPaymentOptions: java.util.ArrayList<PaymentOption> = ArrayList()

    @JvmField
    var currentCurrency: String = ""

    @JvmField
    var currentCurrencySymbol: String = ""


    @JvmField
    var finalCurrencySymbol: String = ""

    @JvmField
    var currentAmount: String = ""
    private lateinit var adapter: CardAdapterUIKIT
    private lateinit var itemAdapter: ItemAdapter
    private lateinit var otpViewHolder: OTPViewHolder

    @SuppressLint("StaticFieldLeak")
    private lateinit var webFrameLayout: FrameLayout

    @SuppressLint("StaticFieldLeak")
    private lateinit var frameLayout: FrameLayout

    @SuppressLint("StaticFieldLeak")
    private lateinit var inLineCardLayout: FrameLayout

    @SuppressLint("StaticFieldLeak")
    private lateinit var headerLayout: LinearLayout
    private lateinit var background: LinearLayout

    @SuppressLint("StaticFieldLeak")
    private lateinit var sdkLayout: LinearLayout

    @SuppressLint("StaticFieldLeak")
    private lateinit var topHeaderView: TapBrandView


    private lateinit var checkoutFragment: CheckoutFragment
    private lateinit var itemList: List<ItemsModel>
    private lateinit var unModifiedItemList: List<ItemsModel>
    private lateinit var selectedPaymentOption: PaymentOption

    @SuppressLint("StaticFieldLeak")
    private lateinit var context: Context
    private lateinit var cardViewModel: CardViewModel
    private var otpTypeString: PaymentTypeEnum = PaymentTypeEnum.SAVEDCARD
    private lateinit var paymentActionType: PaymentType
    private val nfcFragment = NFCFragment()


    //  private val inlineViewFragment = InlineViewFragment()
    private val inlineCamerFragment = CameraFragment()
    private var isInlineOpened = false

    @JvmField
    var isNFCOpened = false
    private var textRecognitionML: TapTextRecognitionML? = null
    private lateinit var intent: Intent
    private lateinit var inlineViewCallback: InlineViewCallback
    lateinit var paymentOptionsResponse: PaymentOptionsResponse
    lateinit var redirectURL: String
    lateinit var cardId: String
    var currencyOldRate: BigDecimal? = null
    var lastSelectedCurrency: String? = null

    @JvmField
    var isSavedCardSelected: Boolean? = false

    @JvmField
    var globalChargeResponse: Charge? = null

    @JvmField
    var selectedTotalAmount: String? = null

    @JvmField
    var selectedAmountPos: BigDecimal? = null

    @JvmField
    var selectedCurrencyPos: String? = null


    val appId: String = "4530082749"
    val merchantId: String = "00000101"
    val seceret: String = "3l5e0cstdim11skgwoha8x9vx9zo0kxxi4droryjp4eqd"
    val countrycode: String = "1001"
    val mcc: String = "4816"


    private var colorBackGround: String? = null
    private var intColorArray: IntArray? = null
    private var startColor: String? = null
    private var endColor: String? = null
    private var middleColor: String? = null
    private var image: ImageView? = null
    var savedCardsBasedCurr: java.util.ArrayList<SavedCard> = java.util.ArrayList<SavedCard>()

    @JvmField
    var incrementalCount: Int = 0

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
        cardViewModel: CardViewModel,
        checkoutFragment: CheckoutFragment,
        headerLayout: LinearLayout,
        topHeaderView: TapBrandView?,
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
        this.checkoutFragment = checkoutFragment
        this.headerLayout = headerLayout
        if (topHeaderView != null) {
            this.topHeaderView = topHeaderView
        }

        initializeScanner(this)
        initViewHolders()
        initAmountAction()
        initSwitchAction()
        initOtpActionButton()
        setAllSeparatorTheme()
        saveCardSwitchHolder?.view?.cardSwitch?.payButton?.setTabAnimatedInterface(object :
            TabAnimatedButtonListener {
            override fun onTabAnimatedStarted() {
                headerLayout.deepForEach { isEnabled = false }
            }

            override fun onTabAnimatedEnded() {
                doAfterSpecificTime {
                    headerLayout.deepForEach { isEnabled = true }
                }

            }

        })


    }

    private fun poweredByTapAnimationEnds() {
        powerdByTapAnimationFinished.value = true
    }

    init {
        powerdByTapAnimationFinished.value = false
        isItemsAreOpend.value = false
    }

    private fun initLoyaltyView() {
        if (SDKSession.enableLoyalty == true) {
            /*removeViews(saveCardSwitchHolder)
            addViews(loyaltyViewHolder,saveCardSwitchHolder)*/
            removeAllViews()
            addViews(
                businessViewHolder,
                amountViewHolder,
                cardViewHolder,
                paymentInlineViewHolder,
                loyaltyViewHolder,
                saveCardSwitchHolder
            )
            loyaltyViewHolder.view.loyaltyView.constraintLayout?.visibility = VISIBLE

        } else
            loyaltyViewHolder.view.loyaltyView.constraintLayout?.visibility = GONE
    }

    private fun initializeScanner(checkoutViewModel: CheckoutViewModel) {
        textRecognitionML = TapTextRecognitionML(checkoutViewModel)
        textRecognitionML?.addTapScannerCallback(checkoutViewModel)
    }

    override fun onTabAnimatedStarted() {
        context.showToast("animation Started")
    }

    override fun onTabAnimatedEnded() {
        context.showToast("animation Ended")
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun initOtpActionButton() {

        otpViewHolder.otpView.otpViewActionButton.setOnClickListener {
            //Added to hide keyboard if open
            CustomUtils.hideKeyboardFrom(context as Activity, otpViewHolder.view)
            when (otpTypeString) {
                PaymentTypeEnum.GOPAY ->
                    goPayViewsHolder.onOtpButtonConfirmationClick(otpViewHolder.otpView.otpViewInput1.text.toString())


                PaymentTypeEnum.SAVEDCARD -> confirmOTPCode(otpViewHolder.otpView.otpViewInput1.text.toString())
                else -> {

                    removeViews(
                        businessViewHolder,
                        amountViewHolder,
                        paymentInlineViewHolder,
                        cardViewHolder,
                        saveCardSwitchHolder,
                        otpViewHolder
                    )
                    addViews(
                        businessViewHolder,
                        amountViewHolder,
                        cardViewHolder,
                        paymentInlineViewHolder,
                        saveCardSwitchHolder
                    )
                    saveCardSwitchHolder?.view?.mainSwitch?.switchSaveMobile?.visibility =
                        GONE
                    val payString: String = LocalizationManager.getValue("pay", "ActionButton")
                    val nowString: String = LocalizationManager.getValue("now", "ActionButton")
                    saveCardSwitchHolder?.view?.cardSwitch?.payButton?.setButtonDataSource(
                        false,
                        "en",
                        if (::selectedAmount.isInitialized && ::selectedCurrency.isInitialized) {
                            payString + " " + nowString
                        } else {
                            payString + " " + nowString
                        },
                        Color.parseColor(ThemeManager.getValue("actionButton.Valid.paymentBackgroundColor")),
                        Color.parseColor(ThemeManager.getValue("actionButton.Valid.titleLabelColor")),
                    )
                    saveCardSwitchHolder?.view?.cardSwitch?.payButton?.visibility = VISIBLE
                    saveCardSwitchHolder?.view?.cardSwitch?.payButton?.changeButtonState(
                        ActionButtonState.IDLE
                    )
                    paymentInlineViewHolder.tapMobileInputView.clearNumber()

                }
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun confirmOTPCode(otpCode: String) {
        otpViewHolder.view.otpView.otpViewActionButton.setDisplayMetricsTheme(
            CustomUtils.getDeviceDisplayMetrics(
                context as Activity
            ), CustomUtils.getCurrentTheme()
        )
        otpViewHolder.view.otpView.otpViewActionButton.changeButtonState(ActionButtonState.LOADING)
        val viewsToDisable = mutableListOf<View>(
            cardViewHolder.view.mainChipgroup.chipsRecycler as View,
            paymentInlineViewHolder.tapCardInputView.backArrow as View,
            amountViewHolder.view.amount_section.constraint,
            amountViewHolder.view.amount_section.tapChipAmount,
            cardViewHolder.view,
            cardViewHolder.view.mainChipgroup,
            paymentInlineViewHolder.tapCardInputView,
            paymentInlineViewHolder.view,
            paymentInlineViewHolder.nfcButton as View,
            paymentInlineViewHolder.scannerButton as View,
            saveCardSwitchHolder?.view as View,
            saveCardSwitchHolder?.view?.cardSwitch?.payButton as View
        )
        viewsToDisable.disableViews()
        when (PaymentDataSource.getTransactionMode()) {
            TransactionMode.PURCHASE -> sendChargeOTPCode(otpCode)
            TransactionMode.AUTHORIZE_CAPTURE -> sendAuthorizeOTPCode(otpCode)
            else -> sendChargeOTPCode(otpCode)
        }

    }


    @RequiresApi(Build.VERSION_CODES.N)
    private fun sendAuthorizeOTPCode(otpCode: String) {
        cardViewModel.processEvent(
            event = CardViewEvent.AuthenticateAuthorizeTransaction,
            viewModel = this,
            otpString = otpCode
        )
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun sendChargeOTPCode(otpCode: String) {
        cardViewModel.processEvent(
            event = CardViewEvent.AuthenticateChargeTransaction,
            viewModel = this,
            otpString = otpCode
        )
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun initViewHolders() {
        businessViewHolder = BusinessViewHolder(context, this)
        amountViewHolder = AmountViewHolder(context, this, this)
        tabAnimatedActionButtonViewHolder = TabAnimatedActionButtonViewHolder(context)
        cardViewHolder = CardViewHolder(context, this)
        goPaySavedCardHolder = GoPaySavedCardHolder(context, this, this)
        saveCardSwitchHolder = SwitchViewHolder(context, this)

        loyaltyViewHolder = LoyaltyViewHolder(context, this, this)


        headerLayout.let { it1 ->
            setTopBorders(
                view = it1,
                cornerRadius = 35f,
                strokeWidth = 0.0f,
                strokeColor = loadAppThemManagerFromPath(AppColorTheme.PoweredByTapBackgroundColor),// stroke color
                tintColor = loadAppThemManagerFromPath(AppColorTheme.PoweredByTapBackgroundColor),// tint color
                shadowColor = loadAppThemManagerFromPath(AppColorTheme.PoweredByTapBackgroundColor)
            )

        }//
        paymentInlineViewHolder = PaymentInlineViewHolder(
            context, this,
            this,
            saveCardSwitchHolder,
            this,
            cardViewModel
        )

        itemsViewHolder = ItemsViewHolder(context, this)

        otpViewHolder = OTPViewHolder(context)
        otpViewHolder.otpView.visibility = GONE
        otpViewHolder.otpView.requestFocus()
        otpViewHolder.otpView.otpViewInput1.cursorColor =
            Color.parseColor(ThemeManager.getValue("TapOtpView.OtpController.activeBottomColor"))
        otpViewHolder.otpView.otpViewInput1.setLineColorInactive(
            Color.parseColor(
                ThemeManager.getValue(
                    "TapOtpView.OtpController.bottomLineColor"
                )
            )
        )
        otpViewHolder.otpView.otpViewInput1.setLineColorActive(
            Color.parseColor(
                ThemeManager.getValue(
                    "TapOtpView.OtpController.activeBottomColor"
                )
            )
        )
        otpViewHolder.otpView.otpViewInput1.isCursorVisible = true
        goPayViewsHolder = GoPayViewsHolder(context, this, otpViewHolder)
        asynchronousPaymentViewHolder = AsynchronousPaymentViewHolder(context, this)
        logicForLoyaltyProgram()

        amountViewHolder.view.amount_section.tapChipPopup.setOnClickListener {
            removePaymentInlineShrinkageAndDimmed()
            amountViewHolder.view.amount_section.tapChipPopup.slideFromLeftToRight()
            with(SharedPrefManager.getUserSupportedLocaleForTransactions(context)!!) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    submitNewLocalCurrency(
                        currencySelected = currency.toString(),
                        currencyRate = rate?.toBigDecimal()!!,
                        totalSelectedAmount = amount,
                        selectedCurrencySymbol = symbol ?: "",
                        isSortingList = false
                    )
                }
            }
            unActivateActionButton()

        }
        newColorVal = Color.parseColor(ThemeManager.getValue("horizontalList.backgroundColor"))


    }


    private fun showCountryFlag(): String? {
        val currency = SharedPrefManager.getUserSupportedLocaleForTransactions(context)
        if (ThemeManager.currentTheme.contains("dark")) {
            return currency?.logos?.dark?.png
        } else {
            return currency?.logos?.light?.png
        }
    }

    private fun initSwitchAction() {
        saveCardSwitchHolder?.view?.mainSwitch?.mainSwitch?.visibility = GONE
        saveCardSwitchHolder?.view?.cardSwitch?.switchGoPayCheckout?.isChecked = false
        saveCardSwitchHolder?.view?.cardSwitch?.switchGoPayCheckout?.visibility = GONE
        saveCardSwitchHolder?.view?.cardSwitch?.saveGoPay?.visibility = GONE
        saveCardSwitchHolder?.view?.cardSwitch?.alertGoPaySignUp?.visibility = GONE
        saveCardSwitchHolder?.view?.cardSwitch?.switchSeparator?.visibility = GONE
    }

    private fun initAmountAction() {
        amountViewHolder.setOnItemsClickListener()
        amountViewHolder.view.amount_section.mainKDAmountValue.visibility = GONE
    }


    override fun displayStartupLayout(enabledSections: ArrayList<SectionType>) {
        println("PaymentDataSource.getPaymentDataType()" + PaymentDataSource.getPaymentDataType())
        //Todo based on api response logic for switch case
        when (PaymentDataSource.getTransactionMode()) {

            TransactionMode.TOKENIZE_CARD -> {
                addViews(
                    businessViewHolder,
                    paymentInlineViewHolder,
                    saveCardSwitchHolder
                )

            }
            TransactionMode.SAVE_CARD -> {
                addViews(
                    businessViewHolder,
                    paymentInlineViewHolder,
                    saveCardSwitchHolder
                )
            }
            else -> {


                if (PaymentDataSource.getPaymentDataType() == "WEB") {
                    addViews(
                        businessViewHolder,
                        amountViewHolder,
                        cardViewHolder,
                        saveCardSwitchHolder
                    )
                    cardViewHolder.cardInfoHeaderText?.visibility = GONE
                } else if (PaymentDataSource.getPaymentDataType() == "CARD") {

                    addViews(
                        businessViewHolder,
                        amountViewHolder,
                        cardViewHolder,
                        paymentInlineViewHolder, saveCardSwitchHolder
                    )
                    cardViewHolder.cardInfoHeaderText.visibility = VISIBLE
                } else
                //Checkimg to be removed once loyalty enabled form api level onluy else will be there
                    if (SDKSession.enableLoyalty == true) {
                        addViews(
                            businessViewHolder,
                            amountViewHolder,
                            cardViewHolder,
                            paymentInlineViewHolder,
                            loyaltyViewHolder,
                            saveCardSwitchHolder
                        )
                    } else {
                        addViews(
                            businessViewHolder,
                            amountViewHolder,
                            cardViewHolder,
                            paymentInlineViewHolder,
                            saveCardSwitchHolder
                        )
                    }


            }
        }

        saveCardSwitchHolder?.view?.mainSwitch?.mainSwitchLinear?.setBackgroundColor(
            Color.parseColor(
                ThemeManager.getValue(
                    "TapSwitchView.main.backgroundColor"
                )
            )
        )
        inLineCardLayout.visibility = GONE
        amountViewHolder.readyToScanVisibility(false)
        saveCardSwitchHolder?.view?.cardviewSwitch?.cardElevation = 0f
        SDKSession.activity?.let {
            CustomUtils.getDeviceDisplayMetrics(
                it
            )
        }?.let {
            saveCardSwitchHolder?.view?.cardSwitch?.payButton?.setDisplayMetricsTheme(
                it,
                CustomUtils.getCurrentTheme()
            )
        }


    }

    fun setBottomSheetLayout(bottomSheetLayout: FrameLayout) {
        this.bottomSheetLayout = bottomSheetLayout
        bottomSheetLayout.clipToOutline = true
    }

    override fun displayGoPayLogin() {
        saveCardSwitchHolder?.let {
            removeViews(
                businessViewHolder, amountViewHolder,
                cardViewHolder, paymentInlineViewHolder,
                it, otpViewHolder,
                goPayViewsHolder
            )
        }

        addViews(businessViewHolder, amountViewHolder, goPayViewsHolder)
        if (goPayViewsHolder.goPayopened) {
            goPayViewsHolder.goPayLoginInput.inputType = GoPayLoginMethod.PHONE
            goPayViewsHolder.goPayLoginInput.visibility = VISIBLE
            saveCardSwitchHolder?.view?.cardSwitch?.switchGoPayCheckout?.visibility = VISIBLE
        }
        //TODO goPayAdapter.updateAdapterData(goPayCardList.value as List<GoPaySavedCards>)
        amountViewHolder.changeGroupAction(false)
        goPayViewsHolder.goPayopened = true
    }


    override fun displayGoPay() {

        saveCardSwitchHolder?.let {
            removeViews(
                businessViewHolder,
                amountViewHolder,
                goPayViewsHolder,
                cardViewHolder,
                paymentInlineViewHolder,
                it,
                otpViewHolder
            )
        }

        saveCardSwitchHolder?.let {
            addViews(
                businessViewHolder,
                amountViewHolder,
                goPaySavedCardHolder,
                cardViewHolder,
                paymentInlineViewHolder,
                it
            )
        }

        cardViewHolder.view.mainChipgroup.groupAction.visibility = View.INVISIBLE
        saveCardSwitchHolder?.view?.cardSwitch?.payButton?.visibility = VISIBLE
        saveCardSwitchHolder?.goPayisLoggedin = true
        adapter.goPayOpenedfromMain(true)
        adapter.removeItems()
    }

    override fun controlCurrency(display: Boolean) {
        isItemsAreOpend.value = display
        if (display) caseDisplayControlCurrency()
        else caseNotDisplayControlCurrency()

        displayItemsOpen = !display
        /**
         * un comment after fix change currencies with disabled payment option
         */
     //   amountViewHolder.changeGroupAction(!display)
        if (this::selectedAmount.isInitialized && this::selectedCurrency.isInitialized) {
            if (selectedAmount == currentAmount && selectedCurrency == currentCurrency) {
                amountViewHolder.view.amount_section.mainKDAmountValue.visibility = GONE


            } else {
                amountViewHolder.updateSelectedCurrency(
                    displayItemsOpen,
                    selectedAmount, selectedCurrency,
                    currentAmount, finalCurrencySymbol, currentCurrencySymbol
                )

            }

        }
        if (otpViewHolder.otpView.isVisible) {
            removeViews(otpViewHolder)
            saveCardSwitchHolder?.view?.cardSwitch?.payButton?.changeButtonState(ActionButtonState.RESET)
            saveCardSwitchHolder?.view?.cardSwitch?.payButton?.isClickable = true
            saveCardSwitchHolder?.view?.cardSwitch?.payButton?.stateListAnimator = null
            val payString: String = LocalizationManager.getValue("pay", "ActionButton")
            val nowString: String = LocalizationManager.getValue("now", "ActionButton")
            saveCardSwitchHolder?.view?.cardSwitch?.payButton?.setButtonDataSource(
                false,
                "en",
                if (::selectedAmount.isInitialized && ::selectedCurrency.isInitialized) {
                    payString + " " + nowString
                } else {
                    payString + " " + nowString
                },
                Color.parseColor(ThemeManager.getValue("actionButton.Invalid.backgroundColor")),
                Color.parseColor(ThemeManager.getValue("actionButton.Invalid.titleLabelColor")),
            )
            saveCardSwitchHolder?.view?.cardSwitch?.payButton?.isActivated = false
        }
        if (PaymentDataSource.getPaymentDataType() != null && PaymentDataSource.getPaymentDataType() == "WEB") {
            removeViews(paymentInlineViewHolder)
        }
        removeInlineScanner()
        removeNFCViewFragment()


    }

    override fun reOpenSDKState() {
        //todo add back to sdk functionality

        removeViews(otpViewHolder, amountViewHolder, paymentInlineViewHolder, saveCardSwitchHolder)
        doAfterSpecificTime(time = 500L) {
            with(cardViewHolder.view.mainChipgroup) {
                mutableListOf<View>(
                    chipsRecycler,
                    groupAction,
                    groupName
                ).addFadeInAnimationToViews()
            }

        }

        businessViewHolder.setDataFromAPI(
            PaymentDataSource.getMerchantData()?.logo,
            PaymentDataSource.getMerchantData()?.name
        )
        addViews(
            amountViewHolder,
            paymentInlineViewHolder,
            saveCardSwitchHolder
        )
        amountViewHolder.view.amount_section.itemAmountLayout?.visibility = VISIBLE
        amountViewHolder.view.amount_section.itemPopupLayout?.visibility = VISIBLE
        amountViewHolder.view.amount_section.tapChipAmount?.visibility = VISIBLE
        checkSelectedAmountInitiated()
        amountViewHolder.changeGroupAction(false)
        amountViewHolder.setOnItemsClickListener()
        amountViewHolder.view.amount_section.flagImageView?.visibility = VISIBLE


        saveCardSwitchHolder?.view?.visibility = VISIBLE
        saveCardSwitchHolder?.view?.cardSwitch?.payButton?.changeButtonState(ActionButtonState.RESET)
        val payString: String = LocalizationManager.getValue("pay", "ActionButton")
        val nowString: String = LocalizationManager.getValue("pay", "ActionButton")
        saveCardSwitchHolder?.view?.cardSwitch?.payButton?.setButtonDataSource(
            false,
            "en",
            if (::selectedAmount.isInitialized && ::selectedCurrency.isInitialized) {
                payString + " " + nowString
            } else {
                payString + " " + nowString
            },
            Color.parseColor(ThemeManager.getValue("actionButton.Invalid.backgroundColor")),
            Color.parseColor(ThemeManager.getValue("actionButton.Invalid.titleLabelColor")),
        )

    }


    fun addTitlePaymentAndFlag() {
        addDataToAmountView()
        if (displayItemsOpen) {

        }
        amountViewHolder.view.amount_section.tapChipPopup.slidefromRightToLeft()
        amountViewHolder.view.amount_section.itemPopupLayout.applyGlowingEffect(getCurrencyColors())

    }

    fun addDataToAmountView() {
        val currencyAlert: String = LocalizationManager.getValue("currencyAlert", "Common")
        amountViewHolder.view.amount_section.popupTextView.text =
            currencyAlert + " " + checkoutFragment.getSimIsoCountryCurrency()
        Glide.with(context).load(showCountryFlag())
            .into(amountViewHolder.view.amount_section.flagImageView);
        amountViewHolder.view.amount_section.tapChipAmount.bringToFront()

    }

    fun removevisibiltyCurrency() {
        amountViewHolder.view.amount_section.tapChipPopup.visibility = GONE
    }

    fun getCurrencyColors(): Pair<Int, Int> {
        var pair: Pair<Int, Int>? = null
        if (ThemeManager.currentTheme.contains("dark")) {
            /**
             * dark theme colors
             */
            pair = Pair(Color.parseColor("#211F1F"), Color.parseColor("#343232"))
        } else {
            /**
             * light theme colors
             */

            pair = Pair(Color.parseColor("#F4F4F4"), Color.parseColor("#E1E1E1"))
        }
        return pair
    }


    /**
     * case displaying @TODO:EGP /
     */

    private fun caseDisplayControlCurrency() {


        removeViews(
            cardViewHolder,
            paymentInlineViewHolder,
            goPayViewsHolder,
            otpViewHolder,
        )
        saveCardSwitchHolder?.view?.visibility = View.GONE
        addViews(
            itemsViewHolder
        )


        /**
         * will be replaced by itemList coming from the API**/
        if (PaymentDataSource.getItems() != null) {
            itemsViewHolder.view.itemRecylerView.adapter = itemAdapter
            itemAdapter.updateAdapterData(itemList)
        }
        currencyAdapter.updateAdapterData(allCurrencies.value as List<SupportedCurrencies>)

        frameLayout.visibility = VISIBLE
        itemsViewHolder.itemsdisplayed = true


        //Hide keyboard of any open
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            CustomUtils.hideKeyboardFrom(
                paymentInlineViewHolder.view.context,
                paymentInlineViewHolder.view
            )
        }
    }

    private fun caseNotDisplayControlCurrency() {

        if (goPayViewsHolder.goPayopened || itemsViewHolder.itemsdisplayed) setActionGoPayOpenedItemsDisplayed()
        else setActionNotGoPayOpenedNotItemsDisplayed()

        itemAdapter.resetViewToInitialValue()

    }


    @RequiresApi(Build.VERSION_CODES.N)
    private fun setActionGoPayOpenedItemsDisplayed() {

        removeViews(
            cardViewHolder,
            paymentInlineViewHolder,
            goPayViewsHolder,
            otpViewHolder,
            itemsViewHolder
        )
        if (::webViewHolder.isInitialized) {
            removeViews(webViewHolder)
        }
        addViews(
            cardViewHolder,
            paymentInlineViewHolder,
            saveCardSwitchHolder, afterAddingViews = {
                saveCardSwitchHolder?.view?.visibility = VISIBLE
            })


        paymentInlineViewHolder.resetPaymentCardView()
        frameLayout.visibility = GONE
    }

    private fun setActionNotGoPayOpenedNotItemsDisplayed() {
        val originalHeight: Int = sdkLayout.height
        saveCardSwitchHolder?.let {
            removeViews(
                cardViewHolder,
                paymentInlineViewHolder,
                it,
                itemsViewHolder
            )
        }

        if (::webViewHolder.isInitialized) removeViews(webViewHolder)

        saveCardSwitchHolder?.let {
            addViews(
                cardViewHolder,
                paymentInlineViewHolder,
                it
            )
        }

        saveCardSwitchHolder?.view?.cardSwitch?.payButton?.changeButtonState(ActionButtonState.IDLE)
        saveCardSwitchHolder?.view?.cardSwitch?.payButton?.isEnabled = true
        saveCardSwitchHolder?.view?.cardSwitch?.payButton?.isClickable = true
        saveCardSwitchHolder?.view?.cardSwitch?.payButton?.isActivated = true
        itemsViewHolder.view.mainCurrencyChip.chipsRecycler.adapter = currencyAdapter
        frameLayout.visibility = GONE

    }

    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("SetTextI18n")
    override fun displayOTPView(
        phoneNumber: PhoneNumber?,
        otpType: String,
        chargeResponse: Charge?
    ) {


        // amountViewHolder.changeGroupAction(false)
        amountViewHolder.view.amount_section.itemCountButton.text = LocalizationManager.getValue(
            "close",
            "Common"
        )
        // amountViewHolder.view.amount_section.itemAmountLayout?.visibility = View.GONE
        //  amountViewHolder.view.amount_section.itemPopupLayout?.visibility = View.GONE
        amountViewHolder.view.amount_section.flagImageView?.visibility = GONE
        amountViewHolder.view.amount_section.popupTextView.text = LocalizationManager.getValue(
            "close",
            "Common"
        )


        //  amountViewHolder.view.amount_section.itemCountButton?.visibility = View.GONE

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
            cardViewHolder,
            paymentInlineViewHolder, saveCardSwitchHolder, amountViewHolder
        )
        addViews(amountViewHolder, otpViewHolder)

        otpViewHolder.otpView.visibility = VISIBLE
        setOtpPhoneNumber(phoneNumber)
        otpViewHolder.otpView.changePhone.setOnClickListener {
            goPayViewsHolder.onChangePhoneClicked()
        }
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.N)
    private fun displayOtpCharge(phoneNumber: PhoneNumber?, chargeResponse: Charge?) {
        // otpTypeString = PaymentTypeEnum.GOPAY //temporray
        println("mmmmmmmm" + amountViewHolder?.originalAmount)
        /*if(::selectedAmount.isInitialized && ::selectedCurrency.isInitialized) {
            amountViewHolder.updateSelectedCurrency(
                false,
                selectedAmount,
                selectedCurrency,
                currentAmount,
                currentCurrency
            )
            println("selectedAmount>>>>" + selectedAmount)
        }*/
        removeViews(
            paymentInlineViewHolder, otpViewHolder
        )

        saveCardSwitchHolder?.view?.mainSwitch?.visibility = GONE
        saveCardSwitchHolder?.view?.cardSwitch?.payButton?.changeButtonState(ActionButtonState.LOADING)

        //start counter on open otpview
        otpViewHolder?.otpView?.startCounter()
        amountViewHolder?.view?.amountView_separator?.visibility = View.GONE
        //Replaced blur with below
        otpViewHolder?.otpView?.otpLinearLayout.setBackgroundColor(
            Color.parseColor(
                ThemeManager.getValue(
                    "amountSectionView.backgroundColor"
                )
            )
        )
        // otpViewHolder?.otpView?.otpLinearLayout.setBackgroundColor(Color.parseColor(ThemeManager.getValue("TapOtpView.backgroundColor")))
        doAfterSpecificTime(time = 700L) {
            removeViews(cardViewHolder)
            addViews(otpViewHolder)
            otpViewHolder.otpView.visibility = VISIBLE

        }
        doAfterSpecificTime(time = 800L) { CustomUtils.showKeyboard(context) }
        //Added to hide the Items-Amount button when OTP is opened
        // amountViewHolder.view.amount_section.itemAmountLayout?.visibility = View.GONE
        amountViewHolder.view.amount_section.tapChipAmount?.visibility = GONE

        setOtpPhoneNumber(phoneNumber)
        otpViewHolder.otpView.changePhone.visibility = View.INVISIBLE
        otpViewHolder.otpView.timerText.setOnClickListener {
            resendOTPCode(chargeResponse)
            otpViewHolder.otpView.restartTimer()
        }
        amountViewHolder.view.amount_section.mainKDAmountValue.visibility = GONE
        saveCardSwitchHolder?.view?.cardSwitch?.payButton?.visibility = GONE
    }

    private fun setOtpPhoneNumber(phoneNumber: PhoneNumber?) {

        var replaced = ""
        var countryCodeReplaced = ""
        countryCodeReplaced = phoneNumber?.countryCode?.replace("0", "").toString()
        if (phoneNumber?.number?.length!! > 7)
            replaced = (phoneNumber.number.toString()).replaceRange(1, 6, "••••")
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
            amountViewHolder,
            paymentInlineViewHolder,
            cardViewHolder,
            saveCardSwitchHolder,
            otpViewHolder
        )
        addViews(
            businessViewHolder,
            amountViewHolder,
            cardViewHolder,
            paymentInlineViewHolder,
            saveCardSwitchHolder,
            otpViewHolder
        )
        //Added check change listener to handle showing of extra save options
        saveCardSwitchHolder?.view?.mainSwitch?.switchSaveMobile?.visibility = GONE
        saveCardSwitchHolder?.view?.cardSwitch?.payButton?.visibility = GONE
        saveCardSwitchHolder?.view?.mainSwitch?.switchSaveMobile?.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) saveCardSwitchHolder?.view?.cardSwitch?.switchesLayout?.visibility =
                GONE
        }
        saveCardSwitchHolder?.setSwitchToggleData(PaymentType.telecom)
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


    @RequiresApi(Build.VERSION_CODES.P)
    override fun displayRedirect(url: String, authenticate: Charge?) {
        this.redirectURL = url

        println("redirectURL before display>>" + url)
        if (::redirectURL.isInitialized && ::fragmentManager.isInitialized) {
            if (otpViewHolder.otpView.isVisible) {
                removeViews(
                    // businessViewHolder,
                    cardViewHolder,
                    paymentInlineViewHolder,
                    otpViewHolder
                )
            }
            // setSlideAnimation()
            if (PaymentDataSource?.getWebViewType() != null && PaymentDataSource.getWebViewType() == WebViewType.REDIRECT) {

                //Stopped showing closetext as requested
                // checkoutFragment.closeText.visibility = View.VISIBLE
                doAfterSpecificTime {
                    removeViews(
                        paymentInlineViewHolder,
                        otpViewHolder,
                        goPaySavedCardHolder,
                        goPayViewsHolder,
                        amountViewHolder,
                        cardViewHolder,
                        paymentInlineViewHolder,
                        tabAnimatedActionButtonViewHolder
                    )

                }


                val fragment = WebFragment.newInstance(
                    redirectURL,
                    this,
                    cardViewModel,
                    authenticate,
                    this,
                    isFirstTimeLoading = true,
                    onLoadedWebView = {
                        saveCardSwitchHolder?.view?.cardSwitch?.payButton?.addFadeOutAnimation(
                            durationTime = 5,
                            isGone = true
                        )

                        showWebView()
                    }
                )
                if (!fragmentManager.isDestroyed)
                    fragmentManager.beginTransaction()
                        .replace(R.id.webFrameLayout, fragment, "webFragment")
                        .addToBackStack("webFragment")
                        .commit()


            } else if (PaymentDataSource?.getWebViewType() != null && PaymentDataSource.getWebViewType() == WebViewType.THREE_DS_WEBVIEW) {

                webViewHolder = WebViewHolder(
                    context,
                    url,
                    this,
                    cardViewModel,
                    authenticate,
                    this,
                    bottomSheetLayout,
                    sdkLayout,
                    saveCardSwitchHolder,
                    paymentInlineViewHolder,
                    cardViewHolder
                )
                removeViews(
                    //  businessViewHolder,
                    // amountViewHolder,
                    //  cardViewHolder,
                    //  saveCardSwitchHolder,
                    // paymentInlineViewHolder,
                    otpViewHolder,
                    goPaySavedCardHolder,
                    goPayViewsHolder
                )

                addViews(webViewHolder)


                //            saveCardSwitchHolder?.view?.mainSwitch?.visibility = GONE
                saveCardSwitchHolder?.view?.cardSwitch?.payButton?.changeButtonState(
                    ActionButtonState.LOADING
                )
                saveCardSwitchHolder?.view?.cardSwitch?.payButton?.visibility = VISIBLE
                saveCardSwitchHolder?.view?.cardSwitch?.tapLogoImage?.visibility = GONE
                // checkoutFragment.closeText.visibility = GONE


            }
        }
    }

    private fun showWebView() {
        animateBS(
            fromView = bottomSheetLayout,
            toView = sdkLayout,
            transitionAnimation = 1000L,
            changeHeight = {
                webFrameLayout.visibility = VISIBLE
            })
    }


    override fun displaySaveCardOptions() {

    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun setBinLookupData(
        binLookupResponse: BINLookupResponse,
        context: Context,
        cardViewModel: CardViewModel
    ) {
        paymentInlineViewHolder = PaymentInlineViewHolder(
            context,
            this,
            this,
            saveCardSwitchHolder,
            this,
            cardViewModel,
        )
        //  paymentInlineViewHolder.tabLayout.setUnselectedAlphaLevel(1.0f)
        if (::paymentInlineViewHolder.isInitialized)
            paymentInlineViewHolder.setCurrentBinData(binLookupResponse)

    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun getDatasfromAPIs(
        merchantData: MerchantData?,
        paymentOptionsResponse: PaymentOptionsResponse?
    ) {
        if (paymentOptionsResponse != null) {
            this.paymentOptionsResponse = paymentOptionsResponse
        }
        Log.e("paymentOptions", this.paymentOptionsResponse.toString())
        if (::businessViewHolder.isInitialized && PaymentDataSource.getTransactionMode() != TransactionMode.TOKENIZE_CARD) {
            businessViewHolder.setDataFromAPI(
                merchantData?.logo,
                merchantData?.name
            )
            // TODO check
            if (merchantData?.verifiedApplication == true) {

            }
        }
        // println("PaymentOptionsResponse on get$paymentOptionsResponse")
        allCurrencies.value =
            (paymentOptionsResponse?.supportedCurrencies as List<SupportedCurrencies>).sortedBy { it.orderBy }

        cacheUserLocalCurrency()




        savedCardList = paymentOptionsResponse.cards
        currencyAdapter = CurrencyTypeAdapter(this)
        if (::amountViewHolder.isInitialized) {
            currentCurrency = paymentOptionsResponse.currency
            val sortedList: List<SupportedCurrencies> =
                (paymentOptionsResponse.supportedCurrencies).sortedBy { it.orderBy }
            for (i in sortedList.indices) {

                if (sortedList[i].currency == currentCurrency) {
                    currentAmount =
                        CurrencyFormatter.currencyFormat(sortedList[i].amount.toString())
                    currentCurrency =
                        sortedList[i].symbol.toString()

                    currentCurrency =
                        sortedList[i].currency.toString()
                    currentCurrencySymbol =
                        sortedList[i].symbol.toString()
                    finalCurrencySymbol =
                        sortedList[i].symbol.toString()
                    currencySelectedForCheck = currentCurrency

                    currencyAdapter.updateSelectedPosition(i)
                }

            }
            /**
             *Note replacing all currency to be currency symbol as per ali 17jan23
             * **/
            amountViewHolder.setDataFromAPI(
                currentAmount,
                currentCurrencySymbol,
                PaymentDataSource.getItems()?.size.toString()
            )
        }
        /**
         * <<<<<<< This items list is going to come from API response later now for loading the view we are taking the List from
         * PaymentDatasource.getItems()  >>>>>>>> */
        if (PaymentDataSource.getItems() != null) {
            itemList = PaymentDataSource.getItems()!!
            //  unModifiedItemList = itemList
            unModifiedItemList = itemList.map { it.copy() }
        }




        if (::itemsViewHolder.isInitialized) {
            paymentOptionsResponse.supportedCurrencies.let {
                itemsViewHolder.setDataFromAPI(
                    it,
                    PaymentDataSource.getItems()
                )
            }

        }

        merchantData?.name?.let {
            saveCardSwitchHolder?.setDataFromAPI(
                it,
                paymentInlineViewHolder.selectedType
            )
        }
        val divider = DividerItemDecoration(
            context,
            DividerItemDecoration.HORIZONTAL
        )
        divider.setDrawable(ShapeDrawable().apply {
            intrinsicWidth = 10
            paint.color = Color.TRANSPARENT
        })
        cardViewHolder.view.mainChipgroup.chipsRecycler.addItemDecoration(divider)
        initAdaptersAction()


        //PaymentDataSource.setSelectedCurrency(currentCurrency, null)
        PaymentDataSource.setSelectedCurrency(currentCurrency, currentCurrencySymbol)
        PaymentDataSource.setSelectedAmount(currentAmount.toBigDecimal())

        if (::loyaltyViewHolder.isInitialized) {
            val objectMapper = ObjectMapper()
            val tapLoyaltyModel: TapLoyaltyModel =
                objectMapper.readValue(
                    context.resources.openRawResource(R.raw.loyalty),
                    TapLoyaltyModel::class.java
                )
            supportedLoyalCards.value =
                tapLoyaltyModel.supportedCurrencies as List<LoyaltySupportedCurrency>
            //println("tapLoyaltyModel>>>" + tapLoyaltyModel.bankLogo)
            tapLoyaltyModel.bankLogo?.let {
                tapLoyaltyModel.bankName?.let { it1 ->
                    loyaltyViewHolder.setDataFromAPI(
                        it,
                        it1,
                        tapLoyaltyModel,
                        supportedLoyalCards.value as List<LoyaltySupportedCurrency>
                    )
                }
            }

        }
        val payString: String = LocalizationManager.getValue("pay", "ActionButton")
        val nowString: String = LocalizationManager.getValue("now", "ActionButton")
        saveCardSwitchHolder?.view?.cardSwitch?.payButton?.setButtonDataSource(
            false,
            LocalizationManager.getLocale(context).toString(),
            if (::selectedAmount.isInitialized && ::selectedCurrency.isInitialized) {
                payString + " " + nowString
            } else {
                payString + " " + nowString
            },
            Color.parseColor(ThemeManager.getValue("actionButton.Invalid.backgroundColor")),
            Color.parseColor(ThemeManager.getValue("actionButton.Invalid.titleLabelColor")),
        )

    }

    fun cacheUserLocalCurrency(): Boolean {
        val suportedCurrencyForUser = allCurrencies.value?.find {
            it.symbol == SharedPrefManager.getUserLocalCurrency(context)?.symbol
        }
        return SharedPrefManager.saveModelLocally(
            context = context,
            dataToBeSaved = suportedCurrencyForUser,
            keyValueToBeSaved = UserSupportedLocaleForTransactions
        )
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun initAdaptersAction() {
        adapter = CardAdapterUIKIT(this)
        goPayAdapter = GoPayCardAdapterUIKIT(this)
        itemAdapter = ItemAdapter(bottomSheetLayout, headerLayout, sdkLayout)

        if (allCurrencies.value?.isNotEmpty() == true) {
            currencyAdapter.updateAdapterData(allCurrencies.value as List<SupportedCurrencies>)
        }
        if (savedCardList?.isNotEmpty() == true) {
            if (PaymentDataSource.getCardType() != null && PaymentDataSource.getCardType() != CardType.ALL) {
                filterSavedCardTypes(savedCardList as List<SavedCard>)
            } else adapter.updateAdapterDataSavedCard(savedCardList as List<SavedCard>)
        } else {
            cardViewHolder.view.mainChipgroup.groupAction?.visibility = GONE
        }
        //itemsViewHolder.view.itemRecylerView.adapter = itemAdapter
        itemsViewHolder.view.mainCurrencyChip.chipsRecycler.adapter = currencyAdapter
        if (PaymentDataSource.getItems() != null) {
            PaymentDataSource.getItems()?.let { itemAdapter.updateAdapterData(it) }
        }
        cardViewHolder.view.mainChipgroup.chipsRecycler.adapter = adapter
        (cardViewHolder.view.mainChipgroup.chipsRecycler?.itemAnimator as SimpleItemAnimator).supportsChangeAnimations =
            false



        filterViewModels(currentCurrency)
        touchHandlingForCardView()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("ClickableViewAccessibility")
    private fun touchHandlingForCardView() {
        saveCardSwitchHolder?.view?.setOnTouchListener { v, _ ->
            CustomUtils.hideKeyboardFrom(context, paymentInlineViewHolder.view)
            paymentInlineViewHolder.resetView = true
            paymentInlineViewHolder.resetTouchView()

            true
        }
        cardViewHolder.view.cardLinearLayout.setOnTouchListener { v, _ ->
            CustomUtils.hideKeyboardFrom(context, paymentInlineViewHolder.view)
            paymentInlineViewHolder.resetView = true
            paymentInlineViewHolder.resetTouchView()

            true
        }
        amountViewHolder.view.setOnTouchListener { v, _ ->
            CustomUtils.hideKeyboardFrom(context, paymentInlineViewHolder.view)
            paymentInlineViewHolder.resetView = true
            paymentInlineViewHolder.resetTouchView()

            true
        }
        paymentInlineViewHolder.tapAlertView?.setOnTouchListener { v, _ ->
            CustomUtils.hideKeyboardFrom(context, paymentInlineViewHolder.view)
            paymentInlineViewHolder.resetView = true
            paymentInlineViewHolder.resetTouchView()

            true
        }
        paymentInlineViewHolder.tabLayout.setOnTouchListener { v, _ ->
            CustomUtils.hideKeyboardFrom(context, paymentInlineViewHolder.view)
            paymentInlineViewHolder.resetView = true
            paymentInlineViewHolder.resetTouchView()

            true
        }
        businessViewHolder.view.setOnTouchListener { v, _ ->
            CustomUtils.hideKeyboardFrom(context, paymentInlineViewHolder.view)
            paymentInlineViewHolder.resetView = true
            paymentInlineViewHolder.resetTouchView()

            true
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
        if (cardViewHolder.view.mainChipgroup.groupAction?.text == LocalizationManager.getValue(
                "close",
                "Common"
            )
        ) {
            isShaking.value = false
            adapter.updateShaking(isShaking.value ?: false)
            goPayAdapter.updateShaking(isShaking.value ?: false)
            cardViewHolder.view.mainChipgroup.groupAction?.text =
                LocalizationManager.getValue("availableInOtherCurrencies", "Common")
        } else {
            isShaking.value = true
            adapter.updateShaking(isShaking.value ?: true)
            goPayAdapter.updateShaking(isShaking.value ?: true)
            goPayAdapter.updateShaking(false)
            cardViewHolder.view.mainChipgroup.groupAction?.text =
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
                LocalizationManager.getValue("availableInOtherCurrencies", "Common")
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

    /**
     * Function ensures to clear the fields or unselect the not needed when
     * delete is clicked on Long press**/
    fun savedCardLongClickDelete() {
        paymentInlineViewHolder.tapCardInputView?.clear()
        paymentInlineViewHolder.onFocusChange("")
        paymentInlineViewHolder.tapCardInputView.setSingleCardInput(
            CardBrandSingle.Unknown, null
        )
        adapter.updateShaking(false)
        CustomUtils.hideKeyboardFrom(context, paymentInlineViewHolder.view)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("WrongConstant")
    override fun didDialogueExecute(response: String, cardTypeDialog: Boolean?) {
        // println("response are$response")
        if (response == "YES") {
            if (deleteCard) {
                selectedViewToBeDeletedFromCardViewHolder?.addLoaderWithBlurryToView(invokeAfterLoad = {
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
                }, viewToBeBLur = selectedViewToBeDeletedFromCardViewHolder)


            } else {
                // println("else block is calle are")
                removeViews(goPaySavedCardHolder)
                //  adapter.updateAdapterDataSavedCard(savedCardList.value as List<SavedCard>)
                goPayViewsHolder.goPayopened = false
                adapter.goPayOpenedfromMain(true)
                adapter.updateShaking(false)
                //cardViewHolder.view.mainChipgroup.groupAction.visibility = View.VISIBLE
            }
        } else if (response == "NO") {
            adapter.updateShaking(false)
            deleteCard = false
            sdkLayout.deepForEach { isEnabled = true }

        } else if (response == "DONE") {
            if (cardTypeDialog == true) {
                paymentInlineViewHolder.tapCardInputView.clear()
                paymentInlineViewHolder.tabLayout.resetBehaviour()
                paymentInlineViewHolder.resetPaymentCardView()
            } else {
                bottomSheetDialog.dismissWithAnimation
                bottomSheetDialog.dismiss()
            }
        }
    }


    override fun deleteSelectedCardListener(delSelectedCard: DeleteCardResponse) {
        if (delSelectedCard.deleted) {
            savedCardsBasedCurr?.removeAt(selectedItemsDel)
            savedCardsBasedCurr?.let { adapter.updateAdapterDataSavedCard(it) }
            cardViewHolder.view.mainChipgroup.chipsRecycler.adapter = adapter
            deleteCard = false
            adapter.updateShaking(false)
            if (savedCardList.isNullOrEmpty()) cardViewHolder.view.mainChipgroup?.groupAction?.visibility =
                GONE

            sdkLayout.deepForEach { isEnabled = true }

        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun handleSuccessFailureResponseButton(
        response: String,
        authenticate: Authenticate?,
        chargeResponse: Charge?,
        tabAnimatedActionButton: TabAnimatedActionButton?,
        contextSDK: Context?
    ) {

        SessionManager.setActiveSession(false)
        println("response val>>" + response)
        println("tabAnimatedActionButton val>>" + tabAnimatedActionButton)
        println("save val>>" + saveCardSwitchHolder)

        /* if(chargeResponse?.status == null && response == "tokenized"){
             //todo replaced authorized with chargeresponse
             SDKSession.getListener()?.getStatusSDK(response,chargeResponse)
         }else{
             SDKSession.getListener()?.getStatusSDK(response,chargeResponse)
         }*/

        /*saveCardSwitchHolder?.view?.cardSwitch?.payButton?.setDisplayMetricsTheme(
            CustomUtils.getDeviceDisplayMetrics(
                contextSDK as Activity
            )
        )*/

        /***
         * This function is  working fine as expected in case when 3ds is false
         * i.e.  sdkSession.isRequires3DSecure(false) as no loading of url occurs direct response
         * from the API is available.
         * WRONG OTP scenario also handled here as similar to old sdk show user error button and
         * close the sdk.
         * **/


        if (::otpViewHolder.isInitialized)
            if (otpViewHolder.otpView.isVisible) {
                removeViews(
                    businessViewHolder,
                    amountViewHolder,
                    cardViewHolder,
                    paymentInlineViewHolder,
                    otpViewHolder
                )
            }
        if (saveCardSwitchHolder != null) {
            saveCardSwitchHolder?.view?.cardSwitch?.payButton?.visibility = INVISIBLE
            saveCardSwitchHolder?.view?.visibility = VISIBLE
        }
//        if (::checkoutFragment.isInitialized)
//            checkoutFragment.closeText.visibility = GONE
        println("chargeResponse are>>>>" + chargeResponse?.status)
        if (response.contains("failure") || response.contains("Cancelled Google Pay") && chargeResponse == null) {

            if (::webFrameLayout.isInitialized) {
                provideBackgroundtoBsLayout(8300)
                showAnimatedButtonRegardingWebViewDismiss(
                    viewToFadeOut = webFrameLayout,
                    isSuccess = false
                )
            } else {
                saveCardSwitchHolder?.view?.cardSwitch?.payButton?.visibility = VISIBLE
                saveCardSwitchHolder?.view?.cardSwitch?.payButton?.changeButtonState(
                    ActionButtonState.ERROR
                )
            }
        }
        when (chargeResponse?.status) {
            ChargeStatus.CAPTURED, ChargeStatus.AUTHORIZED, ChargeStatus.VALID, ChargeStatus.IN_PROGRESS -> {
                if (::webViewHolder.isInitialized) {
                    showAnimatedButtonRegardingWebViewDismiss(viewToFadeOut = webViewHolder.view)
                } else if (::webFrameLayout.isInitialized && webFrameLayout.isVisible) {
                    provideBackgroundtoBsLayout(8000)
                    showAnimatedButtonRegardingWebViewDismiss(viewToFadeOut = webFrameLayout)
                } else {
                    saveCardSwitchHolder?.view?.cardSwitch?.payButton?.visibility = VISIBLE
                    saveCardSwitchHolder?.view?.cardSwitch?.payButton?.changeButtonState(
                        ActionButtonState.SUCCESS
                    )
                }

            }
            ChargeStatus.CANCELLED, ChargeStatus.TIMEDOUT, ChargeStatus.FAILED, ChargeStatus.DECLINED, ChargeStatus.UNKNOWN,
            ChargeStatus.RESTRICTED, ChargeStatus.ABANDONED, ChargeStatus.VOID, ChargeStatus.INVALID -> {

                if (::webFrameLayout.isInitialized) {
                    provideBackgroundtoBsLayout(8300)
                    showAnimatedButtonRegardingWebViewDismiss(
                        viewToFadeOut = webFrameLayout,
                        isSuccess = false
                    )
                }

            }
            else -> {

                if (response == "tokenized") {
                    saveCardSwitchHolder?.view?.cardSwitch?.payButton?.changeButtonState(
                        ActionButtonState.SUCCESS
                    )
                } else {
                    println("is this called>>>")
                    // removeAllViews()
                    saveCardSwitchHolder?.view?.cardSwitch?.payButton?.visibility = VISIBLE
                    saveCardSwitchHolder?.view?.cardSwitch?.payButton?.changeButtonState(
                        ActionButtonState.ERROR
                    )


                }
            }
        }


        SessionManager.setActiveSession(false)
        doAfterSpecificTime(4500)
        {
            if (::bottomSheetDialog.isInitialized)
                bottomSheetDialog.dismiss()
        }


    }


    private fun showAnimatedButtonRegardingWebViewDismiss(
        viewToFadeOut: View,
        isSuccess: Boolean = true,
        tranisitionDuration: Long = 500L
    ) {

        viewToFadeOut.addFadeOutAnimation {
            animateBS(
                fromView = bottomSheetLayout,
                toView = sdkLayout,
                transitionAnimation = tranisitionDuration,
                changeHeight = {},
                onTransitionEnd = {
                    doAfterSpecificTime {
                        saveCardSwitchHolder?.view?.cardSwitch?.payButton?.visibility = VISIBLE
                        if (isSuccess) {
                            saveCardSwitchHolder?.view?.cardSwitch?.payButton?.changeButtonState(
                                ActionButtonState.SUCCESS
                            )
                        } else {
                            saveCardSwitchHolder?.view?.cardSwitch?.payButton?.changeButtonState(
                                ActionButtonState.ERROR
                            )
                        }
                        provideBackgroundtoBsLayout()
                    }

                },
            )
        }

    }


    override fun displayAsynchronousPaymentView(chargeResponse: Charge) {
        if (chargeResponse != null) {
            saveCardSwitchHolder?.view?.cardSwitch?.payButton?.changeButtonState(ActionButtonState.LOADING)

            removeViews(
                // businessViewHolder,
                amountViewHolder,
                cardViewHolder,
                paymentInlineViewHolder,
                saveCardSwitchHolder
            )
            /*  businessViewHolder.setDataFromAPI(
                  selectedPaymentOption.image,
                  selectedPaymentOption.brand?.name
              )*/
            addViews(asynchronousPaymentViewHolder)


            asynchronousPaymentViewHolder.setDataFromAPI(chargeResponse)
        }
    }

    override fun resetViewHolder() {
        adapter.resetSelection()
        cardViewHolder?.cardInfoHeaderText.text =
            LocalizationManager.getValue("cardSectionTitleOr", "TapCardInputKit")
        unActivateActionButton()
    }

    private fun removeViews(vararg viewHolders: TapBaseViewHolder?, onRemoveEnd: () -> Unit = {}) {
        animateBS(
            fromView = bottomSheetLayout,
            toView = sdkLayout,
            transitionAnimation = 100L,
            changeHeight = {
                viewHolders.forEach {
                    if (::sdkLayout.isInitialized) {
                        if (it is PaymentInlineViewHolder) {
                            removePaymentInlineShrinkageAndDimmed()
                        }
                        sdkLayout.removeView(it?.view)
                        provideBackgroundtoBsLayout()
                        onRemoveEnd.invoke()
                    }
                }

            })
    }


    private fun addViews(
        vararg viewHolders: TapBaseViewHolder?,
        afterAddingViews: () -> Unit = {}
    ) {

        animateBS(fromView = bottomSheetLayout, toView = sdkLayout, changeHeight = {
            viewHolders.forEach {
                if (::sdkLayout.isInitialized) {
                    sdkLayout.removeView(it?.view)
                    provideBackgroundtoBsLayout()
                    sdkLayout.addView(it?.view)


                }
            }
            afterAddingViews.invoke()
        })

    }


    fun provideBackgroundtoBsLayout(levelDuration: Int = 7500) {
        /**
         * needed to be enhanced according to the bottomSheetAnimation .
         *
         */
        bottomSheetLayout.background = context.resources.getDrawable(R.drawable.bkgd_level)
        bottomSheetLayout.backgroundTintList =
            ColorStateList.valueOf(loadAppThemManagerFromPath(AppColorTheme.GlobalValuesColor))
        bottomSheetLayout.background.level = levelDuration
    }


    fun unActivateActionButton() {
        val payNowString: String

        when (PaymentDataSource.getTransactionMode()) {
            TransactionMode.TOKENIZE_CARD -> payNowString = LocalizationManager.getValue(
                "pay",
                "ActionButton"
            )
            //TODO stopped since checkout won't have SAVE_CARD -so not available in Localization
            /*TransactionMode.SAVE_CARD -> payString = LocalizationManager.getValue(
                "savecard",
                "ActionButton"
            )*/
            TransactionMode.SAVE_CARD -> payNowString = "SAVE CARD"
            else -> {
                val payString: String = LocalizationManager.getValue("pay", "ActionButton")
                val nowString: String = LocalizationManager.getValue("now", "ActionButton")
                payNowString = payString + " " + nowString
            }
        }

        saveCardSwitchHolder?.view?.cardSwitch?.payButton?.setButtonDataSource(
            false,
            LocalizationManager.getLocale(context).toString(),
            if (::selectedAmount.isInitialized && ::selectedCurrency.isInitialized) {
                payNowString
            } else {
                payNowString
            },
            Color.parseColor(ThemeManager.getValue("actionButton.Invalid.backgroundColor")),
            Color.parseColor(ThemeManager.getValue("actionButton.Invalid.titleLabelColor")),
        )

        // saveCardSwitchHolder?.view?.cardSwitch?.payButton?.changeButtonState(ActionButtonState.IDLE)
        saveCardSwitchHolder?.view?.cardSwitch?.payButton?.isActivated = false
        saveCardSwitchHolder?.view?.cardSwitch?.payButton?.isClickable = false
        saveCardSwitchHolder?.view?.cardSwitch?.payButton?.isEnabled = false
    }


    fun resetCardSelection() {
        adapter.resetSelection()
    }

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCardSelectedAction(isSelected: Boolean, savedCardsModel: Any?) {
        dismisControlWidget()
        unActivateActionButton()

        when (savedCardsModel) {
            is SavedCard -> {
                Bugfender.d(
                    CustomUtils.tagEvent,
                    "Saved card selected :" + savedCardsModel.lastFour + "&" + savedCardsModel.id
                )
                /**
                 * Clear card input text auto focus
                 */
                paymentInlineViewHolder.tapCardInputView.clear()
                paymentInlineViewHolder.clearCardInputAction()

                paymentInlineViewHolder.setDataForSavedCard(
                    savedCardsModel,
                    CardInputUIStatus.SavedCard
                )

                cardViewHolder.view.cardInfoHeaderText.visibility = VISIBLE
                cardViewHolder.view.cardInfoHeaderText.text =
                    LocalizationManager.getValue("savedCardSectionTitle", "TapCardInputKit")

                isSavedCardSelected = true
                Bugfender.d(
                    CustomUtils.tagEvent,
                    "Payment scheme selected: title :" + savedCardsModel.brand + "& ID :" + savedCardsModel.paymentOptionIdentifier
                )
                unActivateActionButton()
            }
            else -> {
                if (savedCardsModel != null) {
                    if ((savedCardsModel as PaymentOption).paymentType == PaymentType.WEB) {

                        showShrinkageForPaymentInline()
                        PaymentDataSource.setWebViewType(WebViewType.REDIRECT)
                        activateActionButton((savedCardsModel as PaymentOption))
                        setPayButtonAction(PaymentType.WEB, savedCardsModel)
                    } else if ((savedCardsModel as PaymentOption).paymentType == PaymentType.GOOGLE_PAY) {
                        showShrinkageForPaymentInline()
                        activateActionButton((savedCardsModel as PaymentOption))
                        setPayButtonAction(PaymentType.GOOGLE_PAY, savedCardsModel)
                        PaymentDataSource.setWebViewType(WebViewType.THREE_DS_WEBVIEW)
                    }
                    /**
                     * needed to reset these setOnclick listener after refactoring
                     * the payment Inline :S :S to avaid multible onClickListener .
                     */
                    paymentInlineViewHolder.intertabLayout.getTabAt(0)?.view?.setOnClickListener {
                        performResetToPaymentInline()
                    }
                    paymentInlineViewHolder.tabLayout.getChildAt(0).setOnClickListener {
                        performResetToPaymentInline()
                    }
                    paymentInlineViewHolder.mainLinear?.setOnTouchListener { view, motionEvent ->
                        performResetToPaymentInline()
                        return@setOnTouchListener true
                    }
                } else

                    displayGoPayLogin()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onDisabledChipSelected(paymentOption: PaymentOption, position: Int) {
        if (paymentOption.isPaymentOptionEnabled) {
            onCardSelectedAction(true, paymentOption)

        } else {
            unActivateActionButton()
            resetViewToPaymentInline()
            showControlWidget()
            with(paymentOption) {
                val supportedCurrenciesRelatedToDisabledChip = mutableListOf<SupportedCurrencies>()
                allCurrencies.value?.forEachIndexed { index, supportedCurrencies ->
                    this.getSupportedCurrencies().forEachIndexed { index, s ->
                        if (s.toUpperCase() == supportedCurrencies.currency!!?.toUpperCase()) {
                            supportedCurrenciesRelatedToDisabledChip.add(supportedCurrencies)
                        }
                    }
                }
                cardViewHolder.view.mainChipgroup.tapCurrencyControlWidget.setCurrencyWidgetDescription(
                    this.displayName
                )
                cardViewHolder.view.mainChipgroup.tapCurrencyControlWidget.setSupportedCurrunciesForControlWidget(
                    supportedCurrenciesRelatedToDisabledChip
                )

                when (CustomUtils.getCurrentTheme()) {
                    ThemeMode.dark.name -> {
                        Glide.with(context).load(this.logos?.dark?.png)
                            .into(cardViewHolder.view.mainChipgroup.tapCurrencyControlWidget.currencyWidgetLogo)
                    }
                    ThemeMode.dark_colored.name -> {
                        Glide.with(context).load(this.logos?.dark_colored?.png)
                            .into(cardViewHolder.view.mainChipgroup.tapCurrencyControlWidget.currencyWidgetLogo)

                    }
                    ThemeMode.light.name -> {
                        Glide.with(context).load(this.logos?.light?.png)
                            .into(cardViewHolder.view.mainChipgroup.tapCurrencyControlWidget.currencyWidgetLogo)

                    }
                    ThemeMode.light_mono.name -> {
                        Glide.with(context).load(this.logos?.light_mono?.png)
                            .into(cardViewHolder.view.mainChipgroup.tapCurrencyControlWidget.currencyWidgetLogo)

                    }

                    else -> {}
                }


            }

            with(cardViewHolder.view.mainChipgroup.tapCurrencyControlWidget) {
                confitmButton.setOnClickListener {
                    dismisControlWidget()
                    submitNewLocalCurrency(
                        currencySelected = getSelectedSupportedCurrency().currency.toString(),
                        currencyRate = getSelectedSupportedCurrency().rate?.toBigDecimal()!!,
                        totalSelectedAmount = getSelectedSupportedCurrency().amount,
                        selectedCurrencySymbol = getSelectedSupportedCurrency().symbol ?: "",
                        isSortingList = false,
                        position = position
                    )
                    onCardSelectedAction(true, paymentOption)

//                    disabledPaymentOptionList.forEachIndexed { index, paymentOption ->
//                        paymentOption.isPaymentOptionEnabled =
//                            this.getSelectedSupportedCurrency().currency in paymentOption.getSupportedCurrencies()
//
//                    }
//                    adapter.updateDisabledPaymentOptions(disabledPaymentOptionList, position)


//                    disabledPaymentOptionList = getListOfDisabledChipsAccordingToSelectedCurrency()
//                    disabledPaymentOptionList.forEachIndexed { index, paymentOption ->
//                        paymentOption.isPaymentOptionEnabled =
//                            paymentOption.getSupportedCurrencies().contains(currency)
//                    }
//                    if (isSorting) adapter.updateDisabledPaymentOptions(disabledPaymentOptionList.sortedBy { !it.isPaymentOptionEnabled })
//                    else adapter.updateDisabledPaymentOptions(disabledPaymentOptionList)
                }
            }

        }

    }


    fun showControlWidget() {
        cardViewHolder.view.mainChipgroup.tapCurrencyControlWidget.fadeVisibility(
            View.VISIBLE,
            duration = 1000
        )
    }

    fun dismisControlWidget() {
        cardViewHolder.view.mainChipgroup.tapCurrencyControlWidget.visibility = View.GONE
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun performResetToPaymentInline() {
        resetViewToPaymentInline()
        resetCardSelection()
        dismisControlWidget()
        unActivateActionButton()
        if (paymentInlineViewHolder.cvvNumber?.length == 3) {
            with(paymentInlineViewHolder) {
                if (this.savedCardsModel != null) {
                    onPayCardCompleteAction(
                        true,
                        PaymentType.SavedCard,
                        cardNumber,
                        expiryDate,
                        cvvNumber!!,
                        cardHolderName,
                        this.savedCardsModel?.brand?.name,
                        this.savedCardsModel
                    )
                } else {
                    onPayCardCompleteAction(
                        true,
                        PaymentType.CARD,
                        cardNumber,
                        expiryDate,
                        cvvNumber!!,
                        cardHolderName,
                        cardBrandInString,
                        this.savedCardsModel
                    )
                }

            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(Build.VERSION_CODES.N)
    private fun showShrinkageForPaymentInline() {
        paymentInlineViewHolder.mainLinear?.addShrinkAnimation(
            xDirection = 0.9f,
            yDirection = 0.9f,
            isDimmed = true
        )
        cardViewHolder.cardInfoHeaderText.addShrinkAnimation(
            xDirection = 0.9f,
            yDirection = 1f,
            isDimmed = true
        )
        paymentInlineViewHolder.tapCardInputView.hideCursor()
        paymentInlineViewHolder.mainLinear?.deepForEach {
            isClickable = false
        }

    }


    @RequiresApi(Build.VERSION_CODES.N)
    private fun resetViewToPaymentInline() {
        paymentInlineViewHolder.tapCardInputView.showCursor()
        cardViewHolder.cardInfoHeaderText.addShrinkAnimation(
            xDirection = 1f,
            yDirection = 1f,
            isDimmed = false
        )
        paymentInlineViewHolder.mainLinear?.addShrinkAnimation(
            xDirection = 1f,
            yDirection = 1f,
            isDimmed = false
        )
    }


    private fun activateActionButton(
        paymentOptObject: PaymentOption? = null,
        cardBrandString: String? = null
    ) {
        val payStringButton: String

        provideBackgroundtoBsLayout()
        when (PaymentDataSource.getTransactionMode()) {
            TransactionMode.TOKENIZE_CARD -> payStringButton = LocalizationManager.getValue(
                "pay",
                "ActionButton"
            )
            //TODO stopped since checkout won't have SAVE_CARD -so not available in Localization
            /* TransactionMode.SAVE_CARD -> payString = LocalizationManager.getValue(
                 "savecard",
                 "ActionButton"
             )*/  TransactionMode.SAVE_CARD -> payStringButton = "SAVE CARD"
            else -> {
                val payString: String = LocalizationManager.getValue("pay", "ActionButton")
                val nowString: String = LocalizationManager.getValue("now", "ActionButton")

                payStringButton = payString + " " + nowString
            }
        }

        println("cardBrandString before " + cardBrandString)
        println("paymentOptObject before " + paymentOptObject)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            logicTogetButtonStyle(paymentOptObject, payStringButton, cardBrandString)
        }


    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun logicTogetButtonStyle(
        paymentOptObject: PaymentOption?,
        payString: String,
        cardBrandString: String?
    ) {

        var selectedPayOpt: PaymentOption? = null

        if (cardBrandString != null && cardBrandString != "Unknown") {
            selectedPayOpt = logicTogetPayOptions(cardBrandString)

        } else selectedPayOpt = paymentOptObject

        //  println("selectedPayOpt are"+selectedPayOpt)
        var bgArrayList: ArrayList<String>? = arrayListOf()
        when (CustomUtils.getCurrentTheme()) {
            ThemeMode.dark.name -> {
                bgArrayList = selectedPayOpt?.buttonStyle?.background?.darkModel?.backgroundColors
                if (selectedPayOpt?.buttonStyle?.background?.darkModel?.backgroundColors?.size == 1) {
                    colorBackGround =
                        selectedPayOpt.buttonStyle?.background?.darkModel?.backgroundColors?.get(0)
                }
                intColorArray = null
            }
            ThemeMode.dark_colored.name -> {
                bgArrayList =
                    selectedPayOpt?.buttonStyle?.background?.darkColorModel?.backgroundColors

                if (bgArrayList?.size == 1) {
                    colorBackGround = bgArrayList[0]
                    intColorArray = null
                } else {

                    if (bgArrayList?.size == 2) {
                        startColor = bgArrayList.get(0).replace("0x", "#")
                        endColor = bgArrayList.get(1).replace("0x", "#")

                        intColorArray =
                            intArrayOf(Color.parseColor(startColor), Color.parseColor(endColor))
                        colorBackGround = "0"

                    } else if (bgArrayList?.size == 3) {
                        startColor = bgArrayList[2].replace("0x", "#")

                        middleColor = bgArrayList[1].replace("0x", "#")
                        endColor = bgArrayList[0].replace("0x", "#")

                        intColorArray = intArrayOf(
                            Color.parseColor(startColor),
                            Color.parseColor(middleColor),
                            Color.parseColor(endColor)
                        )
                        colorBackGround = "0"

                    }
                }
            }
            ThemeMode.light.name -> {
                bgArrayList = selectedPayOpt?.buttonStyle?.background?.lightModel?.backgroundColors

                if (bgArrayList?.size == 1) {
                    colorBackGround = bgArrayList[0]
                    intColorArray = null
                } else {

                    if (bgArrayList?.size == 2) {
                        startColor = bgArrayList.get(0).replace("0x", "#")
                        endColor = bgArrayList.get(1).replace("0x", "#")

                        intColorArray =
                            intArrayOf(Color.parseColor(startColor), Color.parseColor(endColor))
                        colorBackGround = "0"

                    } else if (bgArrayList?.size == 3) {
                        startColor = bgArrayList[2].replace("0x", "#")

                        middleColor = bgArrayList[1].replace("0x", "#")
                        endColor = bgArrayList[0].replace("0x", "#")

                        intColorArray = intArrayOf(
                            Color.parseColor(startColor),
                            Color.parseColor(middleColor),
                            Color.parseColor(endColor)
                        )
                        colorBackGround = "0"

                    }
                }
            }
            ThemeMode.light_mono.name -> {
                bgArrayList =
                    selectedPayOpt?.buttonStyle?.background?.lightMonoModel?.backgroundColors
                if (selectedPayOpt?.buttonStyle?.background?.lightMonoModel?.backgroundColors?.size == 1) {
                    colorBackGround =
                        selectedPayOpt.buttonStyle?.background?.lightMonoModel?.backgroundColors?.get(
                            0
                        )
                }
                intColorArray = null
            }
        }





        saveCardSwitchHolder?.view?.cardSwitch?.payButton?.removeAllViewsInLayout()


        if (bgArrayList?.isNotEmpty() == true) {
            saveCardSwitchHolder?.view?.cardSwitch?.payButton?.setButtonDataSource(
                true,
                LocalizationManager.getLocale(context).language,
                payString,
                if (colorBackGround.equals("0") || colorBackGround == null) 0 else Color.parseColor(
                    colorBackGround
                ),
                Color.parseColor(ThemeManager.getValue("actionButton.Valid.titleLabelColor")),
                intColorArray
            )
        }


        saveCardSwitchHolder?.view?.cardSwitch?.payButton?.clearFocus()
        saveCardSwitchHolder?.view?.cardSwitch?.payButton?.clearAnimation()

        image = ImageView(context)
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        /**
         *work around condition for small logo of knet , set margins according to design figma*/
        if (CustomUtils.getCurrentLocale(context).contains("ar")) {

            params.setMargins(
                MetricsUtil.convertDpToPixel(100f, context).toInt(),
                MetricsUtil.convertDpToPixel(5f, context).toInt(),
                MetricsUtil.convertDpToPixel(100f, context).toInt(),
                MetricsUtil.convertDpToPixel(5f, context).toInt()
            )

        } else {
            if (selectedPayOpt?.brand?.contains("KNET") == true) params.setMargins(
                MetricsUtil.convertDpToPixel(
                    80f,
                    context
                ).toInt(),
                MetricsUtil.convertDpToPixel(5f, context).toInt(),
                MetricsUtil.convertDpToPixel(80f, context).toInt(),
                MetricsUtil.convertDpToPixel(5f, context).toInt()
            )
            else
                params.setMargins(
                    MetricsUtil.convertDpToPixel(108f, context).toInt(),
                    MetricsUtil.convertDpToPixel(10f, context).toInt(),
                    MetricsUtil.convertDpToPixel(108f, context).toInt(),
                    MetricsUtil.convertDpToPixel(10f, context).toInt()
                )


        }

        image?.layoutParams = params
        Glide.with(context)
            .load(
                getAssetName(
                    selectedPayOpt
                )
            ).fitCenter()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(image!!)


        //  println("asset size"+getAssetName(selectedPaymentOption))
        saveCardSwitchHolder?.view?.cardSwitch?.payButton?.addChildView(image!!)

        saveCardSwitchHolder?.view?.cardSwitch?.payButton?.isActivated = true
        saveCardSwitchHolder?.view?.cardSwitch?.payButton?.isClickable = true
        saveCardSwitchHolder?.view?.cardSwitch?.payButton?.isEnabled = true

    }

    private fun logicTogetPayOptions(cardBrandString: String?): PaymentOption? {
        //  println("cardBrandString inn>>>>"+cardBrandString)
        var selectedPayOption: PaymentOption? = null
        var slectedBrand: String? = null
        for (i in 0 until paymentOptionsResponse.paymentOptions.size) {
            slectedBrand = paymentOptionsResponse.paymentOptions[i].brand?.replace("_", "")
            if (slectedBrand == cardBrandString?.replace("_", "")?.toUpperCase()) {
                selectedPayOption = paymentOptionsResponse.paymentOptions[i]
            }
        }
        // println("selectedPayOption>>"+selectedPayOption)

        return selectedPayOption
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("WrongConstant")
    private fun onClickRedirect(savedCardsModel: Any) {
        /**
         * on Click Redirect for Knet Redirection
         */

        amountViewHolder.view.amount_section?.tapChipPopup?.slideFromLeftToRight()
        val viewsToDisable = mutableListOf<View>(
            cardViewHolder.view.mainChipgroup.chipsRecycler as View,
            paymentInlineViewHolder.tapCardInputView.backArrow as View,
            amountViewHolder.view.amount_section.constraint,
            amountViewHolder.view.amount_section.tapChipAmount,
            cardViewHolder.view,
            cardViewHolder.view.mainChipgroup,
            paymentInlineViewHolder.tapCardInputView,
            paymentInlineViewHolder.view,
            paymentInlineViewHolder.nfcButton as View,
            paymentInlineViewHolder.scannerButton as View,
            saveCardSwitchHolder?.view as View,
            saveCardSwitchHolder?.view?.cardSwitch?.payButton as View
        )

        viewsToDisable.disableViews()
        saveCardSwitchHolder?.view?.cardSwitch?.payButton?.changeButtonState(ActionButtonState.LOADING)

        doAfterSpecificTime {
            selectedPaymentOption = savedCardsModel as PaymentOption
            cardViewModel.processEvent(
                CardViewEvent.ChargeEvent,
                this,
                selectedPaymentOption,
                null,
                null,
                null
            )

            when (CustomUtils.getCurrentTheme()) {
                ThemeMode.dark.name -> {
                    saveCardSwitchHolder?.view?.cardSwitch?.payButton?.setInValidBackground(
                        backgroundColor = Color.parseColor(
                            savedCardsModel.buttonStyle?.background?.darkModel?.baseColor
                        )
                    )
                }
                ThemeMode.light.name -> {
                    saveCardSwitchHolder?.view?.cardSwitch?.payButton?.setInValidBackground(
                        backgroundColor = Color.parseColor(savedCardsModel.buttonStyle?.background?.lightModel?.baseColor)
                    )
                }
                ThemeMode.light_mono.name -> {
                    saveCardSwitchHolder?.view?.cardSwitch?.payButton?.setInValidBackground(
                        backgroundColor = Color.parseColor(savedCardsModel.buttonStyle?.background?.lightMonoModel?.baseColor)
                    )
                }
                ThemeMode.dark_colored.name -> {
                    saveCardSwitchHolder?.view?.cardSwitch?.payButton?.setInValidBackground(
                        backgroundColor = Color.parseColor(savedCardsModel.buttonStyle?.background?.darkColorModel?.baseColor)
                    )
                }
            }


            saveCardSwitchHolder?.view?.mainSwitch?.mainTextSave?.visibility = View.INVISIBLE

            mutableListOf(
                amountViewHolder.view,
                cardViewHolder.view,
                paymentInlineViewHolder.view,
                tabAnimatedActionButtonViewHolder!!.view
            ).addFadeOutAnimationToViews {
                translateHeightAnimationForWebViews()
            }

        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun onClickCardPayment(savedCardsModel: Any?) {
        removeVisibilityOfSwitch()
        PaymentDataSource.setWebViewType(WebViewType.THREE_DS_WEBVIEW)
        amountViewHolder.view.amount_section?.tapChipPopup?.slideFromLeftToRight()
        saveCardSwitchHolder?.view?.cardSwitch?.payButton?.changeButtonState(ActionButtonState.LOADING)

        doAfterSpecificTime {
            savedCardsModel as PaymentOption
            CustomUtils.hideKeyboardFrom(context, paymentInlineViewHolder.view)
            if (ThemeManager.currentTheme.isNotEmpty() && ThemeManager.currentTheme.contains("dark")) {
                saveCardSwitchHolder?.view?.cardSwitch?.payButton?.setInValidBackground(
                    backgroundColor = Color.parseColor(
                        savedCardsModel.buttonStyle?.background?.darkModel?.baseColor
                    )
                )
            } else saveCardSwitchHolder?.view?.cardSwitch?.payButton?.setInValidBackground(
                backgroundColor = Color.parseColor(savedCardsModel.buttonStyle?.background?.lightModel?.baseColor)
            )
            if (paymentInlineViewHolder.cardInputUIStatus == CardInputUIStatus.NormalCard) {
                paymentInlineViewHolder.paymentInputContainer.applyBluryToView()
                with(cardViewHolder.view.mainChipgroup) {
                    val viewsToFadeOut = mutableListOf<View>(
                        chipsRecycler,
                        groupAction,
                        groupName,
                    )
                    if (paymentInlineViewHolder.cardInputUIStatus == CardInputUIStatus.NormalCard) {
                        viewsToFadeOut.add(amountViewHolder.view)
                    }
                    doAfterSpecificTime(time = 100L) {

                        viewsToFadeOut.addFadeOutAnimationToViews(onAnimationStart = {
                            paymentInlineViewHolder.paymentInputContainer.applyBluryToView()
                        }, durationTime = 500, onAnimationEnd = {
                            animateBS(
                                fromView = bottomSheetLayout,
                                toView = sdkLayout,
                                transitionAnimation = 500, changeHeight = {})
                        }, headerLayout = headerLayout)

                        if (isSavedCardSelected == true) {
                            cardViewModel.processEvent(
                                event = CardViewEvent.CreateTokenExistingCardEvent,
                                viewModel = this@CheckoutViewModel,
                                createTokenWithExistingCardRequest = paymentInlineViewHolder.getSavedCardData()
                            )

                        } else {
                            cardViewModel.processEvent(
                                event = CardViewEvent.CreateTokenEvent,
                                viewModel = this@CheckoutViewModel,
                                cardDataRequest = paymentInlineViewHolder.getCard(),
                                saveCardValue = paymentInlineViewHolder.tapInlineCardSwitch?.switchSaveCard?.isChecked
                            )
                        }
                    }

                }

            }

        }


    }

    private fun removeVisibilityOfSwitch() {
        saveCardSwitchHolder?.view?.mainSwitch?.visibility = GONE
        paymentInlineViewHolder?.tapInlineCardSwitch?.addFadeOutAnimation()
        animateBS(
            fromView = bottomSheetLayout,
            toView = sdkLayout,
            transitionAnimation = 500, changeHeight = {})
    }


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onDeleteIconClicked(
        stopAnimation: Boolean,
        itemId: Int,
        cardId: String,
        maskedCardNumber: String,
        arrayListSavedCardSize: ArrayList<SavedCard>,
        selectedViewToBeDeleted: ViewGroup,
        viewtoBeBlur: View,
        position: Int
    ) {
        savedCardLongClickDelete()
        resetViewToPaymentInline()
        dismisControlWidget()
        println("stopAnimation" + stopAnimation)
        this.cardId = cardId
        selectedViewToBeDeletedFromCardViewHolder = selectedViewToBeDeleted
        viewToBeBlurCardViewHolder = viewtoBeBlur
        selectedItemsDel = itemId
        cardIDToBeDeleted = position
        if (stopAnimation) {
            stopDeleteActionAnimation(itemId, maskedCardNumber, arrayListSavedCardSize)
        } else {
            cardViewHolder.view.mainChipgroup.groupAction?.text = LocalizationManager.getValue(
                "close",
                "Common"
            )
            deleteCard = false
        }

    }

    private fun stopDeleteActionAnimation(
        itemId: Int,
        maskedCardNumber: String,
        arrayListSavedCardSizes: ArrayList<SavedCard>
    ) {
        isShaking.value = false
        cardViewHolder.view.mainChipgroup.groupAction?.text =
            LocalizationManager.getValue("availableInOtherCurrencies", "Common")
        val title: String = LocalizationManager.getValue("title", "DeleteCard")
        val message: String = LocalizationManager.getValue(
            "message",
            "DeleteCard"
        )
        CustomUtils.showDialog(
            "$title",
            "${message.replace("%@", maskedCardNumber)} ",
            context,
            4,
            this, null, null, false
        )

        selectedItemsDel = itemId
        deleteCard = true
        this.arrayListSavedCardSize = arrayListSavedCardSizes
        sdkLayout.deepForEach { isEnabled = false }

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

    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onGooglePayClicked(isClicked: Boolean) {
        println("onGooglePayClicked>>>" + isClicked)
        // checkoutFragment.checkOutActivity?.handleGooglePayApiCall(savedCardsModel as PaymentOption)

    }

    override fun removePaymentInlineShrinkageAndDimmed() {
        resetViewToPaymentInline()
        dismisControlWidget()
    }


    override fun onPayCardSwitchAction(
        isCompleted: Boolean,
        paymentType: PaymentType,
        cardBrandString: String?
    ) {
        println("isCompleted???" + isCompleted)
        //todo add validations from api when cvv is valid the only  activate ActionButton
        if (isCompleted) {
            businessViewHolder.view?.headerView.constraint.visibility = VISIBLE
            saveCardSwitchHolder?.view?.mainSwitch?.visibility = GONE
            saveCardSwitchHolder?.view?.mainSwitch?.switchSaveMobile?.visibility = GONE
            saveCardSwitchHolder?.setSwitchToggleData(paymentType)
            loyaltyViewHolder.view.loyaltyView?.constraintLayout?.visibility = VISIBLE
            // loyatFlag = true
            /**
             * @TODO:  Will be enabled when coming from API directly
             */
            //  initLoyaltyView() // Will be enabled when coming from API directly
            activateActionButton(cardBrandString = cardBrandString)
            paymentActionType = paymentType
        } else {
            saveCardSwitchHolder?.view?.mainSwitch?.switchSaveMobile?.visibility = GONE
            saveCardSwitchHolder?.setSwitchToggleData(paymentType)
            unActivateActionButton()
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onPayCardCompleteAction(
        isCompleted: Boolean,
        paymentType: PaymentType,
        cardNumber: String?,
        expiryDate: String?,
        cvvNumber: String?, holderName: String?, cardBrandString: String?, savedCardsModel: Any?

    ) {
        println("isCompleted aaa" + isCompleted)
        println("cardBrandString aaa" + cardBrandString)
        println("cardNumber aaa" + cardNumber)
        println("cardInput status>>" + paymentInlineViewHolder.cardInputUIStatus)
        println("paymentTypeEnum status>>" + paymentType)
        println("savedCardsModel status>>" + savedCardsModel)
        if (cardBrandString == null) return

        if (isCompleted) {
            activateActionButton(cardBrandString = cardBrandString)
            CustomUtils.hideKeyboardFrom(context, paymentInlineViewHolder.view)
        }
        if (savedCardsModel != null) {
            setPayButtonAction(paymentType, savedCardsModel)
        } else {
            val typedCardModel = logicTogetPayOptions(cardBrandString)
            setPayButtonAction(paymentType, typedCardModel)
        }

    }

    override fun onPaymentCardIsLoyaltyCard(isLoyaltyCard: Boolean) {
        //Todo logic for loyalty card
    }

    // Override function to open NFC fragment and scan the card via NFC.
    override fun onClickNFC() {

        removeViews(
            //businessViewHolder,
            // amountViewHolder,
            // cardViewHolder,
            paymentInlineViewHolder,
            saveCardSwitchHolder,
            otpViewHolder,
            goPaySavedCardHolder,
            goPayViewsHolder
        )
        frameLayout.visibility = VISIBLE
        //  addViews(businessViewHolder, amountViewHolder)

        cardViewHolder.view.visibility = GONE
        fragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container_nfc_lib, nfcFragment)
            .commit()
        /* fragmentManager.beginTransaction().remove(InlineViewFragment()).replace(
             R.id.fragment_container_nfc_lib,
             nfcFragment
         ).commit()*/
        isNFCOpened = true
        checkoutFragment.isNfcOpened = true
        amountViewHolder.changeGroupAction(false)
        val bottomSheet: FrameLayout? =
            bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet)
        BottomSheetBehavior.from(bottomSheet as View).state = BottomSheetBehavior.STATE_EXPANDED
        checkSelectedAmountInitiated()
    }


    // Override function to open card Scanner and scan the card.
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onClickCardScanner(scannerClicked: Boolean) {
        //setSlideAnimation()
        removeViews(
            //businessViewHolder,
            // amountViewHolder,
            //cardViewHolder,
            saveCardSwitchHolder,
            paymentInlineViewHolder,
            otpViewHolder,
            goPaySavedCardHolder,
            goPayViewsHolder
        )
        amountViewHolder.readyToScanVisibility(scannerClicked)
        // addViews(businessViewHolder, amountViewHolder)
        inLineCardLayout.visibility = VISIBLE
        cardViewHolder.view.visibility = GONE
        FrameManager.getInstance().frameColor = Color.WHITE
        // Use
        //  val bottomSheet: FrameLayout? = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet)
        //  BottomSheetBehavior.from(bottomSheet as View).state = BottomSheetBehavior.STATE_EXPANDED
        //   bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED

        fragmentManager
            .beginTransaction()
            .replace(R.id.inline_container, inlineCamerFragment)
            .commit()

        isInlineOpened = true
        checkoutFragment.isScannerOpened = true

        amountViewHolder.changeGroupAction(false)
        val bottomSheet: FrameLayout? =
            bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet)
        BottomSheetBehavior.from(bottomSheet as View).state = BottomSheetBehavior.STATE_EXPANDED
        checkSelectedAmountInitiated()
    }

    private fun checkSelectedAmountInitiated() {
        if (this::selectedAmount.isInitialized && this::selectedCurrency.isInitialized) {
            amountViewHolder.updateSelectedCurrency(
                displayItemsOpen,
                selectedAmount, selectedCurrency,
                currentAmount, currentCurrency, currentCurrencySymbol
            )
        }
    }


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCurrencyClicked(
        currencySelected: String,
        currencyRate: BigDecimal,
        totalSelectedAmount: BigDecimal,
        previousSelectedCurrency: String,
        selectedCurrencySymbol: String
    ) {


        /**
         * need to be refactored to one function
         */

        submitNewLocalCurrency(
            currencySelected = currencySelected.toString(),
            currencyRate = currencyRate,
            totalSelectedAmount = totalSelectedAmount,
            selectedCurrencySymbol = selectedCurrencySymbol ?: "",
            isSortingList = false
        )

//        currencyOldRate = currencyRate
//        lastSelectedCurrency = previousSelectedCurrency
//        if (::unModifiedItemList.isInitialized)
//            println("unModifiedItemList" + unModifiedItemList)
//        if (::itemList.isInitialized) {
//            for (i in itemList.indices) {
//                itemList[i].amount = unModifiedItemList[i].amount?.times(currencyRate)
//                //itemList[i].totalAmount = currencyOldRate?.div(currencyRate)
//                itemList[i].totalAmount =
//                    unModifiedItemList[i].getPlainAmount()?.times(currencyRate)
//
//
//                println("item per unit >>" + itemList[i].amount)
//
//
//            }
//
//            itemsViewHolder.view.itemRecylerView.adapter = itemAdapter
//            itemAdapter.updateAdapterData(itemList)
//
//        }
//
//        //  itemList[i].amount = (list[i].amount.toLong())
//        //  itemList[i].currency = currencySelected
//
//        selectedAmount = CurrencyFormatter.currencyFormat(totalSelectedAmount.toString())
//        selectedCurrency = currencySelected
//        currencySelectedForCheck = currencySelected
//        selectedTotalAmount = selectedAmount
//
//        /**
//         * Logic based on currency synbol , actual currency code etc note currentCurrency and finalCurrencySymbol passed in
//         * if-else separately ***/
//
//        if (selectedCurrencySymbol.length == 2) {
//            amountViewHolder.updateSelectedCurrency(
//                displayItemsOpen,
//                selectedAmount, selectedCurrency,
//                currentAmount, currentCurrency, selectedCurrencySymbol
//            )
//            //  PaymentDataSource.setSelectedCurrency(selectedCurrency = selectedCurrency, null) // changed from null to symbol
//            PaymentDataSource.setSelectedCurrency(selectedCurrency, selectedCurrencySymbol)
//            //  PaymentDataSource.setSelectedCurrency(selectedCurrency = selectedCurrencySymbol, selectedCurrencySymbol) //commented
//
//        } else {
//            amountViewHolder.updateSelectedCurrency(
//                displayItemsOpen,
//                selectedAmount, selectedCurrency,
//                currentAmount, finalCurrencySymbol, selectedCurrencySymbol
//            )
//            PaymentDataSource.setSelectedCurrency(selectedCurrency, selectedCurrencySymbol)
//
//        }
//        currentCurrencySymbol = selectedCurrencySymbol
//
//
//        // PaymentDataSource.setSelectedCurrency(selectedCurrency = selectedCurrency)
//        PaymentDataSource.setSelectedAmount(currencyRate)
//        if (paymentInlineViewHolder.tapCardInputView.isNotEmpty()) {
//            paymentInlineViewHolder.tapCardInputView.clear()
//            paymentInlineViewHolder.tapAlertView?.fadeVisibility(GONE, 500)
//            paymentInlineViewHolder.acceptedCardText.visibility = VISIBLE
//            paymentInlineViewHolder.tabLayout.resetBehaviour()
//        }

        adapter.resetSelection()
//
//        if (::selectedCurrency.isInitialized) {
//            println("selectedCurrency he" + selectedCurrency)
//            Bugfender.d(CustomUtils.tagEvent, "Currency changed to : " + selectedCurrencySymbol)
//            filterViewModels(selectedCurrency)
//        } else {
//            filterViewModels(currentCurrency)
//            //Bugfender.d("Currency changed to : "+currentCurrency ,CustomUtils.tagEvent)
//
//        }

    }


    @RequiresApi(Build.VERSION_CODES.N)
    fun submitNewLocalCurrency(
        currencySelected: String,
        currencyRate: BigDecimal,
        totalSelectedAmount: BigDecimal,
        selectedCurrencySymbol: String,
        isClickFromDisabledViews: Boolean = false,
        position: Int? = null,
        isSortingList: Boolean? = false
    ) {

        /**
         * need to be refactored
         */
        currencyOldRate = currencyRate
        if (::unModifiedItemList.isInitialized)
            println("unModifiedItemList" + unModifiedItemList)
        if (::itemList.isInitialized) {
            for (i in itemList.indices) {
                itemList[i].amount = unModifiedItemList[i].amount?.times(currencyRate)
                //itemList[i].totalAmount = currencyOldRate?.div(currencyRate)
                itemList[i].totalAmount =
                    unModifiedItemList[i].getPlainAmount()?.times(currencyRate)


            }

            itemsViewHolder.view.itemRecylerView.adapter = itemAdapter
            itemAdapter.updateAdapterData(itemList)

        }

        selectedAmount = CurrencyFormatter.currencyFormat(totalSelectedAmount.toString())
        selectedCurrency = currencySelected
        selectedTotalAmount = selectedAmount


        /**
         * Why this check present ??!!  :
         * Ans- This is logic to check symbol of currency was 2 digit or 3 like kwd or kd/
         */
        if (selectedCurrencySymbol.length == 2) {
            amountViewHolder.updateSelectedCurrency(
                displayItemsOpen,
                selectedAmount,
                selectedCurrency,
                currentAmount,
                currentCurrency,
                selectedCurrencySymbol,
                isChangingCurrencyFromOutside = true
            )
            //  PaymentDataSource.setSelectedCurrency(selectedCurrency = selectedCurrency, null) // changed from null to symbol
            PaymentDataSource.setSelectedCurrency(selectedCurrency, selectedCurrencySymbol)
            //  PaymentDataSource.setSelectedCurrency(selectedCurrency = selectedCurrencySymbol, selectedCurrencySymbol) //commented

        } else {
            amountViewHolder.updateSelectedCurrency(
                true,
                selectedAmount,
                selectedCurrency,
                currentAmount,
                finalCurrencySymbol,
                selectedCurrencySymbol,
                isChangingCurrencyFromOutside = true
            )
            PaymentDataSource.setSelectedCurrency(selectedCurrency, selectedCurrencySymbol)

        }
        currentCurrencySymbol = selectedCurrencySymbol

        val sortedList: List<SupportedCurrencies> =
            (paymentOptionsResponse.supportedCurrencies).sortedBy { it.orderBy }
        sortedList.forEachIndexed { index, supportedCurrencies ->
            /**
             * here we update selected currency flag to be rounded corner
             */
            if (supportedCurrencies.currency == selectedCurrency) {
                currencyAdapter.updateSelectedPosition(index)
            }
            /**
             * here we update currency selected check to avoid it's appearance when selected currency is same
             */
            currencySelectedForCheck = selectedCurrency
        }


        adapter.resetSelection()

        if (!isClickFromDisabledViews) {
            filterPaymentChipsAccordingToCurrency(
                selectedCurrency,
                position = position,
                isSorting = isSortingList
            )
        }
    }


    @SuppressLint("ResourceType")
    override fun redirectLoadingFinished(
        done: Boolean,
        charge: Charge?,
        contextSDK: Context?
    ) {
        //  checkoutFragment.dismiss()
        println("done val" + done + "chargeResponse status" + PaymentDataSource.getChargeOrAuthorize())
        println("saveCardSwitchHolder val" + saveCardSwitchHolder)
        println("redirect val" + charge?.response)
        println("gatewayResponse val" + charge?.gatewayResponse)

        //  saveCardSwitchHolder?.view?.layoutParams= ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT)

        businessViewHolder = contextSDK?.let { BusinessViewHolder(it, this) }!!
        // saveCardSwitchHolder = contextSDK.let { SwitchViewHolder(it,this) }
        removeViews(businessViewHolder)



        if (::amountViewHolder.isInitialized && ::cardViewHolder.isInitialized && ::cardViewHolder.isInitialized && ::paymentInlineViewHolder.isInitialized)
            removeViews(
                businessViewHolder,
                amountViewHolder,
                cardViewHolder,
                paymentInlineViewHolder
            )
    }

    override fun resultObtained(done: Boolean, contextSDK: Context?) {

    }


    @RequiresApi(Build.VERSION_CODES.N)
    private fun setPayButtonAction(
        paymentTypeEnum: PaymentType? = null,
        savedCardsModel: Any? = null
    ) {
        println("setPayButtonAction >>" + saveCardSwitchHolder?.view?.cardSwitch?.payButton)
        println("paymentTypeEnum >>" + paymentTypeEnum)
        /**
         * payment from onSelectPaymentOptionActionListener
         */


        saveCardSwitchHolder?.view?.cardSwitch?.payButton?.setOnClickListener {
            when (paymentTypeEnum) {
                PaymentType.SavedCard -> {
                    /**
                     * Note PaymentType savedcard is changed to Card for saved card payments for extra fees and payment options*/
                    if (::selectedAmount.isInitialized && ::selectedCurrency.isInitialized) {
                        checkForExtraFees(
                            selectedAmount,
                            selectedCurrency,
                            PaymentType.SavedCard,
                            savedCardsModel,
                            PaymentDataProvider().getSelectedCurrency()
                        )

                    } else checkForExtraFees(
                        currentAmount,
                        currentCurrency,
                        PaymentType.SavedCard,
                        savedCardsModel,
                        PaymentDataProvider().getSelectedCurrency()
                    )

                    // setDifferentPaymentsAction(paymentTypeEnum,savedCardsModel)
                }
                PaymentType.WEB -> {
                    PaymentDataSource.setWebViewType(WebViewType.REDIRECT)
                    if (savedCardsModel != null) {
                        if (::selectedAmount.isInitialized && ::selectedCurrency.isInitialized) {
                            checkForExtraFees(
                                selectedAmount,
                                selectedCurrency,
                                paymentTypeEnum,
                                savedCardsModel,
                                PaymentDataProvider().getSelectedCurrency()
                            )
                        } else checkForExtraFees(
                            currentAmount,
                            currentCurrency,
                            paymentTypeEnum,
                            savedCardsModel,
                            PaymentDataProvider().getSelectedCurrency()
                        )
                    }
                }
                PaymentType.CARD -> {
                    //activateActionButton()
                    if (::selectedAmount.isInitialized && ::selectedCurrency.isInitialized) {
                        checkForExtraFees(
                            selectedAmount,
                            selectedCurrency,
                            paymentTypeEnum,
                            savedCardsModel,
                            PaymentDataProvider().getSelectedCurrency()
                        )
                    } else checkForExtraFees(
                        currentAmount,
                        currentCurrency,
                        paymentTypeEnum,
                        savedCardsModel,
                        PaymentDataProvider().getSelectedCurrency()
                    )


                }
                PaymentType.telecom -> {
                    checkForExtraFees(
                        currentAmount,
                        currentCurrency,
                        paymentTypeEnum,
                        savedCardsModel,
                        PaymentDataProvider().getSelectedCurrency()
                    )
                }
                PaymentType.GOOGLE_PAY -> {
                    checkoutFragment.checkOutActivity?.handleGooglePayApiCall(savedCardsModel as PaymentOption)

                }

            }
            //  false
        }

    }

    fun changeButtonToLoading() {
        saveCardSwitchHolder?.view?.cardSwitch?.payButton?.changeButtonState(ActionButtonState.LOADING)

    }


    @RequiresApi(Build.VERSION_CODES.N)
    private fun checkForExtraFees(
        selectedAmount: String,
        selectedCurrency: String,
        paymentTypeEnum: PaymentType,
        savedCardsModel: Any?,
        amountedCurrency: AmountedCurrency?
    ) {
        var extraFees: java.util.ArrayList<ExtraFee>? = null
        var paymentOption: PaymentOption? = null



        if (paymentTypeEnum == PaymentType.WEB) {
            savedCardsModel as PaymentOption
            extraFees = savedCardsModel.extraFees
            fee = calculateExtraFeesAmount(savedCardsModel as PaymentOption, amountedCurrency)

        } else {
            for (i in paymentOptionsResponse.paymentOptions.indices) {
                if (paymentOptionsResponse.paymentOptions[i].paymentType == paymentTypeEnum) {
                    extraFees = paymentOptionsResponse.paymentOptions[i].extraFees
                    paymentOption = paymentOptionsResponse.paymentOptions[i]
                }
            }
            fee = calculateExtraFeesAmount(paymentOption, amountedCurrency)

        }
        val totalAmount: String
        if (selectedTotalAmount != null) {
            println("selectedTotalAmount is" + selectedTotalAmount)
            if (selectedAmount.contains(",")) {
                totalAmount = CurrencyFormatter.currencyFormat(
                    fee?.add(BigDecimal(selectedAmount.replace(",", "").toDouble())).toString()
                )

            } else
                totalAmount = CurrencyFormatter.currencyFormat(
                    fee?.add(
                        selectedTotalAmount?.toDouble()?.let { BigDecimal.valueOf(it) }).toString()
                )
        } else {
            totalAmount = CurrencyFormatter.currencyFormat(
                fee?.add(
                    amountedCurrency?.amount?.toDouble()?.let { BigDecimal.valueOf(it) }).toString()
            )
        }

        if (calculateExtraFeesAmount(
                extraFees,
                paymentOptionsResponse.supportedCurrencies,
                PaymentDataProvider().getSelectedCurrency()
            )!! > BigDecimal.ZERO
        ) {
            showExtraFees(
                totalAmount.toString(),
                fee.toString(),
                paymentTypeEnum,
                savedCardsModel,
                selectedCurrency
            )
        } else if (savedCardsModel != null) {

            println("savedCardsModel after fees" + savedCardsModel)
            println("savedCardsModel after fees" + paymentTypeEnum)

            if (paymentTypeEnum == PaymentType.CARD || paymentTypeEnum == PaymentType.SavedCard) {
                if (paymentInlineViewHolder.cardInputUIStatus == CardInputUIStatus.SavedCard) {
                    savedCardsModel as SavedCard
                    println("savedCardsModel after fees" + savedCardsModel.paymentOptionIdentifier.toInt())
                    if (savedCardsModel.paymentOptionIdentifier.toInt() == 3 || savedCardsModel.paymentOptionIdentifier.toInt() == 4) {
                        setDifferentPaymentsAction(PaymentType.SavedCard, savedCardsModel)
                    } else setDifferentPaymentsAction(PaymentType.SavedCard, savedCardsModel)

                } else {
                    //    savedCardsModel as PaymentOption
                    setDifferentPaymentsAction(PaymentType.CARD, savedCardsModel)

                }

            } else setDifferentPaymentsAction(paymentTypeEnum, savedCardsModel)
        } else setDifferentPaymentsAction(paymentTypeEnum, savedCardsModel)
    }


    @RequiresApi(Build.VERSION_CODES.N)
    private fun payActionSavedCard(savedCardsModel: SavedCard?) {
        println("payActionSavedCard??????" + savedCardsModel)
        var selectdSavedCard: PaymentOption? = null
        selectdSavedCard = logicTogetPayOptions(savedCardsModel?.brand?.name)
        if (ThemeManager.currentTheme.isNotEmpty() && ThemeManager.currentTheme.contains("dark")) {
            saveCardSwitchHolder?.view?.cardSwitch?.payButton?.setInValidBackground(
                backgroundColor = Color.parseColor(
                    selectdSavedCard?.buttonStyle?.background?.darkModel?.baseColor
                )
            )

        } else
            saveCardSwitchHolder?.view?.cardSwitch?.payButton?.setInValidBackground(
                backgroundColor = Color.parseColor(selectdSavedCard?.buttonStyle?.background?.lightModel?.baseColor)
            )

        startSavedCardPaymentProcess(savedCardsModel as SavedCard)

        saveCardSwitchHolder?.view?.cardSwitch?.payButton?.getImageView(
            R.drawable.loader,
            8
        ) {


        }?.let { it1 ->
            saveCardSwitchHolder?.view?.cardSwitch?.payButton?.addChildView(
                it1
            )
        }

        PaymentDataSource.setWebViewType(WebViewType.THREE_DS_WEBVIEW)
        amountViewHolder.view.amount_section?.tapChipPopup?.slideFromLeftToRight()
        saveCardSwitchHolder?.view?.cardSwitch?.payButton?.changeButtonState(ActionButtonState.LOADING)

        doAfterSpecificTime {

            CustomUtils.hideKeyboardFrom(context, paymentInlineViewHolder.view)

            saveCardSwitchHolder?.view?.mainSwitch?.visibility = GONE
            if (paymentInlineViewHolder.cardInputUIStatus == CardInputUIStatus.SavedCard) {
                cardViewHolder.view.cardInfoHeaderText.visibility = VISIBLE
                cardViewHolder.view.cardInfoHeaderText.text =
                    LocalizationManager.getValue("savedCardSectionTitle", "TapCardInputKit")

                with(cardViewHolder.view.mainChipgroup) {
                    val viewsToFadeOut = mutableListOf<View>(chipsRecycler, groupAction, groupName)
                    cardViewHolder.view.cardInfoHeaderText?.let { viewsToFadeOut.add(it) }
                    viewsToFadeOut.add(amountViewHolder.view)
                    paymentInlineViewHolder.paymentInputContainer.applyBluryToView()

                    doAfterSpecificTime(time = 100L) {
                        viewsToFadeOut.addFadeOutAnimationToViews(
                            durationTime = 500L, onAnimationStart = {
                            }, onAnimationEnd = {
                                translateHeightAnimationForWebViews()
                            })

                    }

                }

            }
        }

    }

    private fun translateHeightAnimationForWebViews(): Unit {
        animateBS(
            fromView = bottomSheetLayout,
            toView = sdkLayout,
            transitionAnimation = 300L,
            changeHeight = {})
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun startSavedCardPaymentProcess(savedCard: SavedCard?) {
        val createTokenSavedCard =
            CreateTokenSavedCard(savedCard?.id, PaymentDataSource.getCustomer().identifier)
        cardViewModel.processEvent(
            event = CardViewEvent.CreateTokenExistingCardEvent,
            viewModel = this,
            createTokenWithExistingCardRequest = createTokenSavedCard
        )
    }

    private fun removeAllViews() {
        if (::businessViewHolder.isInitialized && ::amountViewHolder.isInitialized && ::cardViewHolder.isInitialized && ::paymentInlineViewHolder.isInitialized &&
            ::goPayViewsHolder.isInitialized && ::otpViewHolder.isInitialized
        )
            removeViews(
                businessViewHolder,
                amountViewHolder,
                cardViewHolder,
                paymentInlineViewHolder,
                saveCardSwitchHolder,
                goPayViewsHolder,
                otpViewHolder,
                tabAnimatedActionButtonViewHolder
            )

        if (::webViewHolder.isInitialized) removeViews(webViewHolder)
    }

    private fun setAllSeparatorTheme() {

        val borderColor: String = ThemeManager.getValue("poweredByTap.backgroundColor")
        var borderOpacityVal: String? = null
        var newBorderColor: String? = null
        borderOpacityVal = borderColor.substring(borderColor.length - 2)
        newBorderColor = "#" + borderOpacityVal + borderColor.substring(0, borderColor.length - 2)
            .replace("#", "")

        /**
         * Set the stroke width from json itself for business header*/
        val separatorViewTheme = SeparatorViewTheme()
        separatorViewTheme.strokeColor =
            Color.parseColor(ThemeManager.getValue("tapSeparationLine.backgroundColor"))
        separatorViewTheme.strokeHeight = ThemeManager.getValue("tapSeparationLine.height")
        businessViewHolder.view.topSeparatorLinear.topSeparator.setTheme(separatorViewTheme)

        if (ThemeManager.currentTheme.isNotEmpty() && ThemeManager.currentTheme.contains("dark")) {
            amountViewHolder.view.amountView_separator.visibility = GONE
        } else amountViewHolder.view.amountView_separator.visibility = VISIBLE
        amountViewHolder.view.amountView_separator.setTheme(separatorViewTheme)
        //  cardViewHolder.view.tapSeparatorViewLinear1.separator_1.setTheme(separatorViewTheme)
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

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onRecognitionSuccess(card: TapCard?) {
//to be added for unembossed cards
        /* if (card != null) {
             if (card.cardNumber != null && card.cardHolder != null && card.expirationDate != null) {
                onReadSuccess(card)
             }
         }*/

        println("are you called here!!")


    }

    override fun onRecognitionFailure(error: String?) {

    }


    private fun removeInlineScanner() {
        if (isInlineOpened) {
            if (fragmentManager.findFragmentById(R.id.inline_container) != null) {
                fragmentManager.beginTransaction()
                    .remove(fragmentManager?.findFragmentById(R.id.inline_container)!!)
                    .commit()
            }
            // inlineCamerFragment.onDestroy()
            isInlineOpened = false
            checkoutFragment.isScannerOpened = false
            inLineCardLayout.visibility = GONE
            cardViewHolder.view.visibility = VISIBLE
            amountViewHolder.readyToScanVisibility(false)
            saveCardSwitchHolder?.view?.cardSwitch?.payButton?.changeButtonState(
                ActionButtonState.RESET
            )
            incrementalCount = 0
        }

    }

    fun handleScanFailedResult(error: String?) {
        println("handleScanFailedResult card is")
        removeInlineScanner()
        //  removeViews(amountViewHolder, businessViewHolder)
        addViews(
            // businessViewHolder,
            //  amountViewHolder,
            cardViewHolder,
            paymentInlineViewHolder,
            saveCardSwitchHolder
        )

    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun handleScanSuccessResult(card: TapCard) {

        //  removeViews(amountViewHolder, businessViewHolder)
        addViews(
            //   businessViewHolder,
            //   amountViewHolder,
            // cardViewHolder,
            paymentInlineViewHolder,
            saveCardSwitchHolder
        )
        callBinLookupApi(card.cardNumber?.trim()?.substring(0, 6))


        Handler().postDelayed({
            val binLookupResponse: BINLookupResponse? = PaymentDataSource.getBinLookupResponse()
            if (PaymentDataSource.getCardType() != null && PaymentDataSource.getCardType() == CardType.ALL) {
                setScannedCardDetails(card)

            } else {
                if (binLookupResponse != null) {
                    paymentInlineViewHolder.checkAllowedCardTypes(binLookupResponse)
                    setScannedCardDetails(card)
                }

            }
        }, 1000)


        // inlineCamerFragment.onDestroy()
        removeInlineScanner()


    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun setScannedCardDetails(card: TapCard) {


        println("scanned card holder is${card.cardHolder}")
        println("scanned card number is${card.cardNumber}")

        /* paymentInlineViewHolder.tapCardInputView.setCardNumberMasked(
             paymentInlineViewHolder.maskCardNumber(
                 card.cardNumber
             )
         )*/
        paymentInlineViewHolder.hideViewONScanNFC()
        val dateParts: List<String>? = card.expirationDate?.split("/")
        val month = dateParts?.get(0)?.toInt()
        val year = dateParts?.get(1)?.toInt()
        if (month != null) {
            if (year != null) {
                paymentInlineViewHolder.setCardScanData(card, month, year)
            }
        }

        // paymentInlineViewHolder.tapCardInputView.setCardHolderName(card.cardHolder)

    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun callBinLookupApi(binLookUpStr: String?) {
        cardViewModel.processEvent(
            event = CardViewEvent.RetreiveBinLookupEvent,
            viewModel = CheckoutViewModel(), binValue = binLookUpStr
        )

    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun handleNFCScannedResult(emvCard: TapEmvCard) {
        println("emvCard>>" + emvCard)
        //   removeViews(amountViewHolder, businessViewHolder)
        addViews(
            //   businessViewHolder,
            //   amountViewHolder,
            // cardViewHolder,
            paymentInlineViewHolder,
            saveCardSwitchHolder
        )
        callBinLookupApi(emvCard.cardNumber?.substring(0, 6))

        Handler().postDelayed({
            val binLookupResponse: BINLookupResponse? = PaymentDataSource.getBinLookupResponse()
            if (PaymentDataSource.getCardType() != null && PaymentDataSource.getCardType() == CardType.ALL) {
                setNfcCardDetails(emvCard)

            } else {
                if (binLookupResponse != null) {
                    paymentInlineViewHolder.checkAllowedCardTypes(binLookupResponse)
                    setNfcCardDetails(emvCard)
                }

            }
        }, 300)

        removeNFCViewFragment()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun setNfcCardDetails(emvCard: TapEmvCard) {
        // auto slide added on scan to prevent overlap
        paymentInlineViewHolder.hideViewONScanNFC()
        convertDateString(emvCard)
        paymentInlineViewHolder.onFocusChange(CardInputListener.FocusField.FOCUS_CVC)


    }

    private fun removeNFCViewFragment() {
        if (isNFCOpened)
            if (fragmentManager.findFragmentById(R.id.fragment_container_nfc_lib) != null)
                fragmentManager.beginTransaction()
                    .remove(fragmentManager.findFragmentById(R.id.fragment_container_nfc_lib)!!)
                    .commit()
        isNFCOpened = false
        checkoutFragment.isNfcOpened = false
        //  webFrameLayout.visibility = View.GONE
        frameLayout.visibility = GONE
        cardViewHolder.view.visibility = VISIBLE
    }


    private fun convertDateString(emvCard: TapEmvCard) {
        //  println("emvCard.getExpireDate()"+emvCard.getExpireDate())
        val dateParts: CharSequence? = DateFormat.format("M/y", emvCard.getExpireDate())
        println("dateparts" + dateParts?.length)
        if (dateParts?.contains("/") == true) {
            if (dateParts.length <= 3) {
                return
            } else {
                if (dateParts.length >= 5 || dateParts.length >= 4) {
                    val month = (dateParts).substring(0, 1).toInt()

                    val year = (dateParts).substring(2, 4)

                    if (year.contains("/")) {
                        println("retuu>>" + year)
                        return
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            paymentInlineViewHolder.setNFCCardData(emvCard, month, year.toInt())
                        }


                    }

                }

            }


        }

    }

    private fun filteredByPaymentTypeAndCurrencyAndSortedList(
        list: java.util.ArrayList<PaymentOption>, paymentType: PaymentType, currency: String
    ): java.util.ArrayList<PaymentOption> {
        var currencyFilter: String? = currency.toUpperCase()
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

        return list.filter { items ->
            items.paymentType == paymentType && items.getSupportedCurrencies()?.contains(
                currencyFilter
            ) == true
        } as ArrayList<PaymentOption>
    }

    private fun <E : CurrenciesSupport> getCurrenciesFilter(
        currency: String
    ): Utils.List.Filter<E> {
        return object : Utils.List.Filter<E> {
            override fun isIncluded(`object`: E): Boolean {
                return `object`.getSupportedCurrencies()!!.contains(currency)
            }
        }
    }

    private fun <E> filterByCurrenciesAndSortList(
        list: ArrayList<E>,
        currency: String
    ): ArrayList<SavedCard> where E : CurrenciesSupport?, E : Comparable<E>? {
        /**
         * Stopped generic for now to work the functionality**/
        return list.filter { items ->
            items?.getSupportedCurrencies()?.contains(
                currency
            ) == true
        } as ArrayList<SavedCard>

    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun filterViewModels(currency: String) {
        savedCardsBasedCurr = filterByCurrenciesAndSortList(paymentOptionsResponse.cards, currency)
        filterPaymentChipsAccordingToCurrency(currency, isSorting = true)
        val hasSavedCards: Boolean = savedCardsBasedCurr.size > 0
        if (hasSavedCards) {
            adapter.updateAdapterDataSavedCard(savedCardsBasedCurr)
        }
    }

    private fun filterPaymentChipsAccordingToCurrency(
        currency: String,
        isSorting: Boolean? = false,
        position: Int? = null
    ) {
        disabledPaymentOptionList = getListOfDisabledChipsAccordingToSelectedCurrency()
        disabledPaymentOptionList.forEachIndexed { index, paymentOption ->
            paymentOption.isPaymentOptionEnabled =
                paymentOption.getSupportedCurrencies().contains(currency)
        }
     adapter.updateDisabledPaymentOptions(disabledPaymentOptionList, position = position)

    }

    fun getListOfDisabledChipsAccordingToSelectedCurrency(): ArrayList<PaymentOption> {
        return paymentOptionsResponse.paymentOptions.sortedBy { it.orderBy }.filter {
            it.paymentType == PaymentType.WEB || it.paymentType == PaymentType.GOOGLE_PAY
        } as ArrayList<PaymentOption>

    }


    @RequiresApi(Build.VERSION_CODES.N)
    private fun logicToHandlePaymentDataType(
        webPaymentOptions: ArrayList<PaymentOption>? = null,
        cardPaymentOptions: ArrayList<PaymentOption>

    ) {
        cardViewHolder.view.mainChipgroup?.groupName?.visibility = VISIBLE
        // println("webPaymentOptions in logic >>>>$webPaymentOptions")
        println("cardPaymentOptions in logic >>>>$cardPaymentOptions")
        println("payment data in logic >>>>" + PaymentDataSource.getPaymentDataType())


        if (PaymentDataSource.getPaymentDataType() != null && PaymentDataSource.getPaymentDataType() == "WEB" && PaymentDataSource.getTransactionMode() != TransactionMode.AUTHORIZE_CAPTURE) {
            adapter.updateAdapterDataSavedCard(ArrayList())
            if (webPaymentOptions != null) {
                adapter.updateAdapterData(webPaymentOptions)
            }
            saveCardSwitchHolder?.view?.cardSwitch?.showOnlyPayButton()
        } else if (PaymentDataSource.getPaymentDataType() != null && PaymentDataSource.getPaymentDataType() == "CARD") {
            adapter.updateAdapterData(ArrayList())
            paymentInlineViewHolder.setDataFromAPI(cardPaymentOptions)
            saveCardSwitchHolder?.view?.cardSwitch?.showOnlyPayButton()
        } else {
            /**
             * If TransactionMode is AUTHORIZE_CAPTURE then don't show webpayment options
             * **/
            if (PaymentDataSource.getTransactionMode() == TransactionMode.AUTHORIZE_CAPTURE) {
                adapter.updateAdapterData(ArrayList())
                paymentInlineViewHolder.setDataFromAPI(cardPaymentOptions)
            } else {
                if (webPaymentOptions != null) {
                    adapter.updateAdapterData(webPaymentOptions)
                }
                if (cardPaymentOptions.isNotEmpty()) {
                    paymentInlineViewHolder.setDataFromAPI(cardPaymentOptions)


                } else {
                    saveCardSwitchHolder?.mainTextSave?.visibility = GONE
                    removeViews(paymentInlineViewHolder)
                }

            }
        }
        if (LocalizationManager.currentLocalized.length() != 0) {
            title = LocalizationManager.getValue("title", "ExtraFees")

        }
    }


    @RequiresApi(Build.VERSION_CODES.N)
    private fun showExtraFees(
        totalAmount: String,
        extraFeesAmount: String,
        paymentType: PaymentType, savedCardsModel: Any?, selectedCurrency: String
    ) {

        val extraFeesPart1: String = LocalizationManager.getValue<String>(
            "message",
            "ExtraFees"
        ).replaceFirst("%@", extraFeesAmount + selectedCurrency)
        val extraFeesPart2: String = extraFeesPart1.replace("%@", totalAmount + selectedCurrency)
        // val leftToRight = "\u200F"
        //  val localizedMessage =
        // extraFeesPart1 +" "+extraFeesAmount+PaymentDataProvider().getSelectedCurrency()?.currency +extraFeesPart2+" "+ totalAmount+ PaymentDataProvider().getSelectedCurrency()?.currency
        //    "$extraFeesPart1 $extraFeesAmount$selectedCurrency$extraFeesPart2 $totalAmount$selectedCurrency"
        CustomUtils.showDialog(
            title,
            extraFeesPart2,
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
            saveCardSwitchHolder?.view?.cardSwitch?.payButton?.changeButtonState(ActionButtonState.LOADING)

            println("savedCardsModel>>>>" + savedCardsModel)
            if (savedCardsModel != null) {
                if (paymentType == PaymentType.CARD) {
                    savedCardsModel as SavedCard
                    if (savedCardsModel.paymentOptionIdentifier.toInt() == 3 || savedCardsModel.paymentOptionIdentifier.toInt() == 4) {
                        setDifferentPaymentsAction(PaymentType.SavedCard, savedCardsModel)
                    }
                } else setDifferentPaymentsAction(paymentType, savedCardsModel)
            } else setDifferentPaymentsAction(paymentType, savedCardsModel)
        } else {
            fee = BigDecimal.ZERO

            when {
                paymentType === PaymentType.WEB -> {
                    // fireWebPaymentExtraFeesUserDecision(ExtraFeesStatus.REFUSE_EXTRA_FEES)
                    selectedCurrencyPos = null
                    selectedAmountPos = null


                }
                paymentType === PaymentType.CARD -> {
                    selectedCurrencyPos = null
                    selectedAmountPos = null
//                    fireCardPaymentExtraFeesUserDecision(ExtraFeesStatus.REFUSE_EXTRA_FEES)
                }
                paymentType === PaymentType.SavedCard -> {
                    selectedCurrencyPos = null
                    selectedAmountPos = null
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
                    PaymentDataSource.setWebViewType(WebViewType.REDIRECT)
                    //Added to disable click when button loading
                    amountViewHolder.view.amount_section?.itemAmountLayout?.isEnabled = false
                    amountViewHolder.view.amount_section?.itemAmountLayout?.isClickable = false
                    onClickRedirect(savedCardsModel)
                }
            }
            paymentType === PaymentType.CARD -> {
                println("savedCardsModel fro card" + savedCardsModel)

                //Added to disable click when button loading
                amountViewHolder.view.amount_section?.itemAmountLayout?.isEnabled = false
                amountViewHolder.view.amount_section?.itemAmountLayout?.isClickable = false

                if (savedCardsModel is PaymentOption) onClickCardPayment(savedCardsModel)


            }
            paymentType === PaymentType.SavedCard -> {
                println("SavedCard fro card" + savedCardsModel)
                if (isSavedCardSelected == true) {
                    PaymentDataSource.setWebViewType(WebViewType.THREE_DS_WEBVIEW)
                    //Added to disable click when button loading
                    amountViewHolder.view.amount_section?.itemAmountLayout?.isEnabled = false
                    amountViewHolder.view.amount_section?.itemAmountLayout?.isClickable = false
                    if (savedCardsModel != null)
                        payActionSavedCard(savedCardsModel as SavedCard)

                }
            }
        }
    }

    /**
     * Calculate extra fees amount big decimal.
     *
     * @param paymentOption the payment option
     * @return the big decimal
     */
    open fun calculateExtraFeesAmount(
        paymentOption: PaymentOption?,
        amountedCurrency: AmountedCurrency?
    ): BigDecimal? {
        return if (paymentOption != null) {

            // val amount = PaymentDataProvider().getSelectedCurrency()
            var extraFees: java.util.ArrayList<ExtraFee>? = paymentOption.extraFees
            if (extraFees == null) extraFees = java.util.ArrayList()
            val supportedCurrencies: java.util.ArrayList<SupportedCurrencies>? =
                PaymentDataProvider().getSupportedCurrencies()
            calculateExtraFeesAmount(extraFees, supportedCurrencies, amountedCurrency)
        } else BigDecimal.ZERO
    }

    fun View.fadeVisibility(visibility: Int, duration: Long = 3000) {
        val transition: Transition = Fade()
        transition.duration = duration
        transition.addTarget(this)
        TransitionManager.beginDelayedTransition(this.parent as ViewGroup, transition)
        this.visibility = visibility
    }

    fun closeAsynchView() {
        checkoutFragment?.dismissBottomSheetDialog()
    }

    fun showOnlyButtonView(
        status: ChargeStatus,
        checkOutActivity: CheckOutActivity?,
        _checkoutFragment: CheckoutFragment
    ) {
        removeAllViews()
        addViews(saveCardSwitchHolder)
        val payString: String = LocalizationManager.getValue("pay", "ActionButton")
        val nowString: String = LocalizationManager.getValue("now", "ActionButton")
        saveCardSwitchHolder?.view?.cardSwitch?.payButton?.setButtonDataSource(
            false,
            "en",
            if (::selectedAmount.isInitialized && ::selectedCurrency.isInitialized) {
                payString + " " + nowString
            } else {
                payString + " " + nowString
            },
            Color.parseColor(ThemeManager.getValue("actionButton.Valid.paymentBackgroundColor")),
            Color.parseColor(ThemeManager.getValue("actionButton.Valid.titleLabelColor")),
        )


        when (status) {
            ChargeStatus.CAPTURED, ChargeStatus.AUTHORIZED, ChargeStatus.VALID, ChargeStatus.IN_PROGRESS -> {
                saveCardSwitchHolder?.view?.cardSwitch?.payButton?.setDisplayMetricsTheme(
                    CustomUtils.getDeviceDisplayMetrics(
                        context as Activity
                    ), CustomUtils.getCurrentTheme()
                )
                Handler().postDelayed({
                    saveCardSwitchHolder?.view?.cardSwitch?.payButton?.changeButtonState(
                        ActionButtonState.SUCCESS
                    )
                }, 500)
            }
            ChargeStatus.CANCELLED, ChargeStatus.TIMEDOUT, ChargeStatus.FAILED, ChargeStatus.DECLINED, ChargeStatus.UNKNOWN,
            ChargeStatus.RESTRICTED, ChargeStatus.ABANDONED, ChargeStatus.VOID, ChargeStatus.INVALID -> {
                Handler().postDelayed({
                    tabAnimatedActionButton?.setInValidBackground(
                        false, Color.YELLOW
                    )
                    tabAnimatedActionButton?.changeButtonState(ActionButtonState.LOADING)
                    saveCardSwitchHolder?.view?.cardSwitch?.payButton?.setInValidBackground(
                        false, Color.YELLOW
                    )

                    saveCardSwitchHolder?.view?.cardSwitch?.payButton?.setButtonDataSource(
                        false,
                        "en",
                        null,
                        Color.parseColor(ThemeManager.getValue("actionButton.Invalid.backgroundColor")),
                        Color.parseColor(ThemeManager.getValue("actionButton.Invalid.titleLabelColor")),

                        )
                    saveCardSwitchHolder?.view?.cardSwitch?.payButton?.changeButtonState(
                        ActionButtonState.ERROR
                    )
                }, 500)
                tabAnimatedActionButton?.changeButtonState(ActionButtonState.ERROR)
            }
            else -> {

                if (status.equals("tokenized")) {
                    Handler().postDelayed({
                        saveCardSwitchHolder?.view?.cardSwitch?.payButton?.changeButtonState(
                            ActionButtonState.SUCCESS
                        )
                    }, 500)

                } else {
                    Handler().postDelayed({
                        saveCardSwitchHolder?.view?.cardSwitch?.payButton?.setInValidBackground(
                            false,
                            Color.BLUE
                        )
                        saveCardSwitchHolder?.view?.cardSwitch?.payButton?.setButtonDataSource(
                            false,
                            "en",
                            null,
                            Color.parseColor(ThemeManager.getValue("actionButton.Invalid.backgroundColor")),
                            Color.parseColor(ThemeManager.getValue("actionButton.Invalid.titleLabelColor")),

                            )
                        saveCardSwitchHolder?.view?.cardSwitch?.payButton?.changeButtonState(
                            ActionButtonState.ERROR
                        )
                    }, 500)

                }
            }
        }
    }


    /**
     * handlePaymentSuccess handles the payment token obtained from GooglePay API
     * **/
    @RequiresApi(Build.VERSION_CODES.N)
    fun handlePaymentSuccess(paymentData: PaymentData, selectedPaymentOption: PaymentOption) {
        removeViews(
            //businessViewHolder,
            amountViewHolder,
            cardViewHolder,
            // saveCardSwitchHolder,
            paymentInlineViewHolder,
            otpViewHolder,
            goPaySavedCardHolder,
            goPayViewsHolder
        )
        saveCardSwitchHolder?.view?.cardSwitch?.switchesLayout?.visibility = GONE
        saveCardSwitchHolder?.view?.cardSwitch?.payButton?.visibility = VISIBLE
        saveCardSwitchHolder?.view?.cardSwitch?.payButton?.changeButtonState(ActionButtonState.LOADING)

        val paymentInformation = paymentData.toJson() ?: return

        try {
            // Token will be null if PaymentDataRequest was not constructed using fromJson(String).
            val paymentMethodData =
                JSONObject(paymentInformation).getJSONObject("paymentMethodData")

            // If the gateway is set to "example", no payment information is returned - instead, the
            // token will only consist of "examplePaymentMethodToken".
            val tokenizationData = paymentMethodData.getJSONObject("tokenizationData")

            val token = tokenizationData.getString("token")
            Bugfender.d(CustomUtils.tagEvent, "Google pay raw token :" + token)
            val gson = Gson()
            val jsonToken = gson.fromJson(token, JsonObject::class.java)


            /**
             * At this stage, Passing the googlePaylaod to Tap Backend TokenAPI call followed by chargeAPI.
             * ***/
            val createTokenGPayRequest = CreateTokenGPayRequest("googlepay", jsonToken)
            CardViewModel().processEvent(
                event = CardViewEvent.CreateGoogleTokenEvent,
                viewModel = this,
                selectedPaymentOption = selectedPaymentOption,
                context = context,
                createTokenGPayRequest = createTokenGPayRequest
            )
            // Logging token string.
            /* Log.e("GooglePaymentToken", paymentMethodData
                     .getJSONObject("tokenizationData")
                     .getString("token"))
    */
        } catch (e: JSONException) {
            Log.e("handlePaymentSuccess", "Error: " + e.toString())
        }

    }

    /**
     * handleError handles the payment response obtained from GooglePay API
     * **/
    fun handleError(statusCode: Int) {
        Log.e("loadPaymentData failed", String.format("Error code: %d", statusCode))
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onReadSuccess(card: TapCard?) {
        incrementalCount += 1

        println("incrementalCount val>>>>>" + incrementalCount)
        /*   if (card != null) {
               if(card.cardNumber!=null)
              handleScanSuccessResult(card)
              *//* Log.d("checkOutViewModel", "onRecognitionSuccess: " + card.cardNumber)
            Log.d("checkOutViewModel", "onRecognitionSuccess: " + card.expirationDate)
            Log.d("checkOutViewModel", "onRecognitionSuccess: " + card.cardHolder)*//*

        }*/
        if (card != null && incrementalCount == 3) {

            if (card.cardNumber != null && card.cardHolder != null && card.expirationDate != null) {
                handleScanSuccessResult(card)
                incrementalCount = 0
            }
            return
        }
    }

    override fun onReadFailure(error: String?) {
        handleScanFailedResult(error)
    }

    private fun logicForLoyaltyProgram() {

        loyaltyViewHolder.loyaltyView.switchLoyalty?.setOnCheckedChangeListener { buttonView, isChecked ->
            loyaltyViewHolder.loyaltyView.switchTheme()

            if (isChecked) {
                loyaltyViewHolder.loyaltyView.linearLayout2?.visibility = VISIBLE
                loyaltyViewHolder.loyaltyView.linearLayout3?.visibility = VISIBLE

            } else {

                loyaltyViewHolder.loyaltyView.linearLayout2?.visibility = GONE
                loyaltyViewHolder.loyaltyView.linearLayout3?.visibility = GONE

            }
        }

        loyaltyViewHolder.loyaltyView.setLoyaltyHeaderDataSource(
            LoyaltyHeaderDataSource(
                "ADCB",
                "https://is4-ssl.mzstatic.com/image/thumb/Purple112/v4/05/33/67/05336718-a6f6-8ca1-1ea0-0644f5071ce9/AppIcon-0-0-1x_U007emarketing-0-0-0-5-0-0-sRGB-0-0-0-GLES2_U002c0-512MB-85-220-0-0.png/1200x600wa.png"
            )
        )

        loyaltyViewHolder.loyaltyView.textViewClickable?.makeLinks(
            Pair("(T&C)", View.OnClickListener {
                val url = "https://www.adcb.com/en/tools-resources/adcb-privacy-policy/"
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(url)
                context.startActivity(intent)

            })
        )
    }


    override fun onTransactionSuccess(p0: Transaction?) {
        println("transaction is success $p0")

    }

    override fun onTransactionFail(p0: Transaction?) {
        println("onTransactionFail is  $p0")
    }

    fun dismissBottomSheet() {
        checkoutFragment.dismissBottomSheetDialog()
    }

    fun setTitleNormalCard() {
        cardViewHolder.view.cardInfoHeaderText.text =
            LocalizationManager.getValue("cardSectionTitleOr", "TapCardInputKit")
    }

    fun getAssetName(paymentOptionOb: PaymentOption?): String {
        println("paymentOptionOb" + paymentOptionOb)
        var lang: String = "en"
        var theme: String = "light"
        if (CustomUtils.getCurrentLocale(context) != null) {
            lang = CustomUtils.getCurrentLocale(context)
        } else lang = "en"

        theme = CustomUtils.getCurrentTheme()

        val assetToLoad: String = paymentOptionOb?.buttonStyle?.titleAssets.toString()
        println(
            "<<<assetToLoad>>>" + assetToLoad.replace("{theme}", theme)
                .replace("{lang}", lang) + ".png"
        )
        return assetToLoad.replace("{theme}", theme).replace("{lang}", lang) + ".png"
    }

    override fun onMorphAnimationEnd() {
        context.showToast("end")
    }

    override fun onMorphAnimationReverted() {
        context.showToast("revert")
    }

    override fun onMorphAnimationStarted() {
        context.showToast("start")
    }


}













