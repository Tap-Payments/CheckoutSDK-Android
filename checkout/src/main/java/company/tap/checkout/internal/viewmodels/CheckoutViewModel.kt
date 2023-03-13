package company.tap.checkout.internal.viewmodels

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ShapeDrawable
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.text.format.DateFormat
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.animation.*
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import androidx.annotation.RestrictTo
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isNotEmpty
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.transition.Fade
import androidx.transition.Scene
import androidx.transition.Transition
import androidx.transition.TransitionManager
import cards.pay.paycardsrecognizer.sdk.FrameManager
import cards.pay.paycardsrecognizer.sdk.ui.InlineViewCallback
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.android.gms.wallet.PaymentData
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.google.gson.JsonObject
import company.tap.cardinputwidget.CardBrandSingle
import company.tap.cardinputwidget.CardInputUIStatus
import company.tap.cardinputwidget.widget.CardInputListener
import company.tap.cardscanner.*
import company.tap.checkout.R
import company.tap.checkout.internal.PaymentDataProvider
import company.tap.checkout.internal.adapter.CardTypeAdapterUIKIT
import company.tap.checkout.internal.adapter.CurrencyTypeAdapter
import company.tap.checkout.internal.adapter.GoPayCardAdapterUIKIT
import company.tap.checkout.internal.adapter.ItemAdapter
import company.tap.checkout.internal.api.enums.ChargeStatus
import company.tap.checkout.internal.api.enums.PaymentType
import company.tap.checkout.internal.api.models.*
import company.tap.checkout.internal.api.requests.CreateTokenGPayRequest
import company.tap.checkout.internal.api.responses.DeleteCardResponse
import company.tap.checkout.internal.api.responses.InitResponseModel
import company.tap.checkout.internal.api.responses.MerchantData
import company.tap.checkout.internal.api.responses.PaymentOptionsResponse
import company.tap.checkout.internal.apiresponse.CardViewEvent
import company.tap.checkout.internal.apiresponse.CardViewModel
import company.tap.checkout.internal.apiresponse.testmodels.GoPaySavedCards
import company.tap.checkout.internal.apiresponse.testmodels.TapCardPhoneListDataSource
import company.tap.checkout.internal.enums.PaymentTypeEnum
import company.tap.checkout.internal.enums.SectionType
import company.tap.checkout.internal.enums.WebViewType
import company.tap.checkout.internal.interfaces.*
import company.tap.checkout.internal.utils.AmountCalculator.calculateExtraFeesAmount
import company.tap.checkout.internal.utils.CurrencyFormatter
import company.tap.checkout.internal.utils.CustomUtils
import company.tap.checkout.internal.utils.Utils
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
import company.tap.tapuilibrary.themekit.ThemeManager
import company.tap.tapuilibrary.themekit.theme.SeparatorViewTheme
import company.tap.tapuilibrary.uikit.datasource.LoyaltyHeaderDataSource
import company.tap.tapuilibrary.uikit.enums.ActionButtonState
import company.tap.tapuilibrary.uikit.enums.GoPayLoginMethod
import company.tap.tapuilibrary.uikit.fragment.NFCFragment
import company.tap.tapuilibrary.uikit.ktx.makeLinks
import company.tap.tapuilibrary.uikit.views.TabAnimatedActionButton
import kotlinx.android.synthetic.main.amountview_layout.view.*
import kotlinx.android.synthetic.main.businessview_layout.view.*
import kotlinx.android.synthetic.main.cardviewholder_layout1.view.*
import kotlinx.android.synthetic.main.gopaysavedcard_layout.view.*
import kotlinx.android.synthetic.main.itemviewholder_layout.view.*
import kotlinx.android.synthetic.main.loyalty_view_layout.view.*
import kotlinx.android.synthetic.main.otpview_layout.view.*
import kotlinx.android.synthetic.main.switch_layout.view.*
import mobi.foo.benefitinapp.data.Transaction
import mobi.foo.benefitinapp.listener.CheckoutListener
import mobi.foo.benefitinapp.utils.BenefitInAppCheckout
import org.json.JSONException
import org.json.JSONObject
import java.math.BigDecimal
import kotlin.properties.Delegates


/**
 *
 * Copyright © 2020 Tap Payments. All rights reserved.
 *
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
open class CheckoutViewModel : ViewModel(), BaseLayoutManager, OnCardSelectedActionListener,
    PaymentCardComplete, onCardNFCCallListener, OnCurrencyChangedActionListener, WebViewContract,
    TapTextRecognitionCallBack, TapScannerCallback, CheckoutListener {
    private var savedCardList = MutableLiveData<List<SavedCard>>()
    private var arrayListSavedCardSize = ArrayList<SavedCard>()
    private var supportedLoyalCards = MutableLiveData<List<LoyaltySupportedCurrency>>()
    private var paymentOptionsList = MutableLiveData<List<PaymentOption>>()
    private var goPayCardList = MutableLiveData<List<GoPaySavedCards>>()

    private var allCurrencies = MutableLiveData<List<SupportedCurrencies>>()
    private var selectedItemsDel by Delegates.notNull<Int>()
    private val isShaking = MutableLiveData<Boolean>()
    private var deleteCard: Boolean = false
    private var displayItemsOpen: Boolean = false
    private var displayOtpIsOpen: Boolean = false
    private var saveCardSwitchHolder: SwitchViewHolder? = null
   
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
    private lateinit var selectedCurrency: String
    private var fee: BigDecimal? = BigDecimal.ZERO
    val provider: IPaymentDataProvider = PaymentDataProvider()
    @JvmField
    var currentCurrency: String = ""

    @JvmField
    var currentCurrencySymbol: String = ""



    @JvmField
    var currentAmount: String = ""
    private lateinit var adapter: CardTypeAdapterUIKIT
    private lateinit var itemAdapter: ItemAdapter
    private lateinit var otpViewHolder: OTPViewHolder

    @SuppressLint("StaticFieldLeak")
    private lateinit var webFrameLayout: FrameLayout

    @SuppressLint("StaticFieldLeak")
    private lateinit var frameLayout: FrameLayout

    @SuppressLint("StaticFieldLeak")
    private lateinit var inLineCardLayout: FrameLayout

    @SuppressLint("StaticFieldLeak")
    private lateinit var sdkLayout: LinearLayout
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
    lateinit var tapCardPhoneListDataSource: ArrayList<TapCardPhoneListDataSource>
    lateinit var paymentOptionsResponse: PaymentOptionsResponse
    lateinit var initResponseModel: InitResponseModel
    lateinit var redirectURL: String
    lateinit var cardId: String
    var currencyOldRate: BigDecimal? = null
    var currentCalculatedAmount: BigDecimal? = null
    var lastSelectedCurrency: String? = null
    var loyatFlag: Boolean? = false
   @JvmField var isSavedCardSelected: Boolean? = false

    @JvmField var globalChargeResponse: Charge? =null

    @JvmField
    var selectedTotalAmount: String? = null

    @JvmField
    var selectedAmountPos: BigDecimal? = null

    @JvmField
    var selectedCurrencyPos: String? = null

    @JvmField
    var binLookupResponse1: BINLookupResponse? = null
    lateinit var paymentOptionsWorker: java.util.ArrayList<PaymentOption>

    private val googlePayButton: View? = null

    val appId: String = "4530082749"
    val merchantId: String = "00000101"
    val seceret: String = "3l5e0cstdim11skgwoha8x9vx9zo0kxxi4droryjp4eqd"
    val countrycode: String = "1001"
    val mcc: String = "4816"
    @JvmField
    var incrementalCount:Int = 0
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
        checkoutFragment: CheckoutFragment, headerLayout: LinearLayout
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

        val aScene: Scene? = Scene.getCurrentScene(sdkLayout)
        aScene?.setEnterAction {
            AnimationUtils.loadAnimation(context, R.anim.slide_down)
        }




        initializeScanner(this)
        initViewHolders()
        initAmountAction()
        initSwitchAction()
        initOtpActionButton()
        setAllSeparatorTheme()
        //  initLoyaltyView()

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
            loyaltyViewHolder.view.loyaltyView.constraintLayout?.visibility = View.VISIBLE

        } else
            loyaltyViewHolder.view.loyaltyView.constraintLayout?.visibility = View.GONE
    }

    private fun initializeScanner(checkoutViewModel: CheckoutViewModel) {
        textRecognitionML = TapTextRecognitionML(checkoutViewModel)
        textRecognitionML?.addTapScannerCallback(checkoutViewModel)
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
                        View.GONE
                    val payString: String = LocalizationManager.getValue("pay", "ActionButton")
                    saveCardSwitchHolder?.view?.cardSwitch?.payButton?.setButtonDataSource(
                        false,
                        "en",
                        if (::selectedAmount.isInitialized && ::selectedCurrency.isInitialized) {
                            payString+" "+currentCurrencySymbol+" "+selectedAmount
                        }else{ payString+" "+currentCurrencySymbol+" "+currentAmount},
                        Color.parseColor(ThemeManager.getValue("actionButton.Valid.paymentBackgroundColor")),
                        Color.parseColor(ThemeManager.getValue("actionButton.Valid.titleLabelColor")),
                    )
                    saveCardSwitchHolder?.view?.cardSwitch?.payButton?.visibility = View.VISIBLE
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
        otpViewHolder.view.otpView.otpViewActionButton.setDisplayMetrics(
            CustomUtils.getDeviceDisplayMetrics(
                context as Activity
            )
        )
        otpViewHolder.view.otpView.otpViewActionButton.changeButtonState(ActionButtonState.LOADING)
        when (PaymentDataSource.getTransactionMode()) {
            TransactionMode.PURCHASE -> sendChargeOTPCode(otpCode)
            TransactionMode.AUTHORIZE_CAPTURE -> sendAuthorizeOTPCode(otpCode)
            else -> sendChargeOTPCode(otpCode)
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
            null,
            otpCode
        )
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun initViewHolders() {
        businessViewHolder = BusinessViewHolder(context, this)
        amountViewHolder = AmountViewHolder(context, this)
        tabAnimatedActionButtonViewHolder = TabAnimatedActionButtonViewHolder(context)
        cardViewHolder = CardViewHolder(context, this)
        goPaySavedCardHolder = GoPaySavedCardHolder(context, this, this)
        saveCardSwitchHolder = SwitchViewHolder(context, this)
        loyaltyViewHolder = LoyaltyViewHolder(context, this, this)

        paymentInlineViewHolder = PaymentInlineViewHolder(
            context, this,
            this,
            this,
            saveCardSwitchHolder,
            this,
            cardViewModel, checkoutFragment, loyaltyViewHolder
        )

        itemsViewHolder = ItemsViewHolder(context, this)
        otpViewHolder = OTPViewHolder(context)
        otpViewHolder.otpView.visibility = View.GONE
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
        // nfcViewHolder = NFCViewHolder(context as Activity, context, this, fragmentManager)
        logicForLoyaltyProgram()
    }

    private fun initSwitchAction() {
        saveCardSwitchHolder?.view?.mainSwitch?.mainSwitch?.visibility = View.GONE
        //By Default switchGoPayCheckout will be hidden , will appear only when goPayLoggedin
        saveCardSwitchHolder?.view?.cardSwitch?.switchGoPayCheckout?.isChecked = false
        saveCardSwitchHolder?.view?.cardSwitch?.switchGoPayCheckout?.visibility = View.GONE
        saveCardSwitchHolder?.view?.cardSwitch?.saveGoPay?.visibility = View.GONE
        saveCardSwitchHolder?.view?.cardSwitch?.alertGoPaySignUp?.visibility = View.GONE
        saveCardSwitchHolder?.view?.cardSwitch?.switchSeparator?.visibility = View.GONE
        // saveCardSwitchHolder?.view?.mainSwitch?.mainSwitch?.visibility = View.VISIBLE
        //   saveCardSwitchHolder?.view?.mainSwitch?.mainSwitchLinear?.setBackgroundColor(Color.parseColor(ThemeManager.getValue("TapSwitchView.main.backgroundColor")))
    }

    private fun initAmountAction() {
        amountViewHolder.setOnItemsClickListener {}
        amountViewHolder.view.amount_section.mainKDAmountValue.visibility = View.GONE
    }


    override fun displayStartupLayout(enabledSections: ArrayList<SectionType>) {
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
                    cardViewHolder.cardInfoHeaderText?.visibility = View.GONE
                } else  if (PaymentDataSource.getPaymentDataType() == "CARD") {
                    addViews(
                        businessViewHolder,
                        paymentInlineViewHolder,saveCardSwitchHolder
                    )
                    cardViewHolder.cardInfoHeaderText.visibility = View.VISIBLE
                }else
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
        inLineCardLayout.visibility = View.GONE
        amountViewHolder.readyToScanVisibility(false)
        saveCardSwitchHolder?.view?.cardviewSwitch?.cardElevation = 0f
        SDKSession.activity?.let {
            CustomUtils.getDeviceDisplayMetrics(
                it
            )
        }?.let { saveCardSwitchHolder?.view?.cardSwitch?.payButton?.setDisplayMetrics(it) }

    }

    fun setBottomSheetLayout(bottomSheetLayout: FrameLayout) {
        this.bottomSheetLayout = bottomSheetLayout
    }

    override fun displayGoPayLogin() {
        setSlideAnimation()
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
            goPayViewsHolder.goPayLoginInput.visibility = View.VISIBLE
            saveCardSwitchHolder?.view?.cardSwitch?.switchGoPayCheckout?.visibility = View.VISIBLE
        }
        //TODO goPayAdapter.updateAdapterData(goPayCardList.value as List<GoPaySavedCards>)
        amountViewHolder.changeGroupAction(false)
        goPayViewsHolder.goPayopened = true
    }

    private fun setSlideAnimation() {
        if (this::bottomSheetLayout.isInitialized) {
            // AnimationUtils.loadAnimation(bottomSheetLayout.context,R.anim.slide_down)
            // AnimationEngine.applyTransition(bottomSheetLayout, AnimationEngine.Type.SLIDE, 1200)

        /*    val fadeIn = AlphaAnimation(0f, 1f)
            fadeIn.interpolator = DecelerateInterpolator() //add this
            fadeIn.duration = 1000

            val fadeOut = AlphaAnimation(1f, 0f)
            fadeOut.interpolator = AccelerateInterpolator() //and this
            fadeOut.startOffset = 1000
            fadeOut.duration = 1000

            val animation = AnimationSet(false) //change to false
            animation.addAnimation(fadeIn)
       //     animation.addAnimation(fadeOut)
            this.bottomSheetLayout.animation = animation*/
        }
    }

    override fun displayGoPay() {
        setSlideAnimation()
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
        saveCardSwitchHolder?.view?.cardSwitch?.payButton?.visibility = View.VISIBLE
        saveCardSwitchHolder?.goPayisLoggedin = true
        adapter.goPayOpenedfromMain(true)
        adapter.removeItems()
    }

    override fun controlCurrency(display: Boolean) {
        if (display) caseDisplayControlCurrency()
        else caseNotDisplayControlCurrency()

        displayItemsOpen = !display
        amountViewHolder.changeGroupAction(!display)
        // if (this::currentAmount.isInitialized)
        if (this::selectedAmount.isInitialized && this::selectedCurrency.isInitialized) {
            if (selectedAmount == currentAmount && selectedCurrency == currentCurrency) {
                amountViewHolder.view.amount_section.mainKDAmountValue.visibility = View.GONE

            } else {
                amountViewHolder.updateSelectedCurrency(
                    displayItemsOpen,
                    selectedAmount, selectedCurrency,
                    currentAmount, currentCurrency, currentCurrencySymbol
                )

            }
        }
        if (otpViewHolder.otpView.isVisible) {
            removeViews(otpViewHolder)
            saveCardSwitchHolder?.view?.cardSwitch?.payButton?.changeButtonState(ActionButtonState.RESET)
            saveCardSwitchHolder?.view?.cardSwitch?.payButton?.isClickable = true
            saveCardSwitchHolder?.view?.cardSwitch?.payButton?.stateListAnimator = null
            val payString: String = LocalizationManager.getValue("pay", "ActionButton")
            saveCardSwitchHolder?.view?.cardSwitch?.payButton?.setButtonDataSource(
                false,
                "en",
                if (::selectedAmount.isInitialized && ::selectedCurrency.isInitialized) {
                    payString+" "+currentCurrencySymbol+" "+selectedAmount
                }else{ payString+" "+currentCurrencySymbol+" "+currentAmount},
                Color.parseColor(ThemeManager.getValue("actionButton.Invalid.backgroundColor")),
                Color.parseColor(ThemeManager.getValue("actionButton.Invalid.titleLabelColor")),
            )
//            saveCardSwitchHolder?.view?.cardSwitch?.payButton?.isActivated = false
        }
        if (PaymentDataSource.getPaymentDataType() != null && PaymentDataSource.getPaymentDataType() == "WEB") {
            removeViews(paymentInlineViewHolder)
        }
        removeInlineScanner()
        removeNFCViewFragment()
        setSlideAnimation()
    }


    private fun caseDisplayControlCurrency() {
      /*  val newHeight = activity?.window?.decorView?.measuredHeight
        val viewGroupLayoutParams = bottomSheetLayout.layoutParams
        viewGroupLayoutParams.height = newHeight ?: 0
        bottomSheetLayout.layoutParams = viewGroupLayoutParams
*/
            removeViews(
                //  businessViewHolder,
                // amountViewHolder,
                cardViewHolder,
                paymentInlineViewHolder,
                )


        // removeAllViews()
        addViews(
            itemsViewHolder
        )



        // checkoutFragment?.isFullscreen =true

        /**
         * will be replaced by itemList coming from the API**/
        if (PaymentDataSource.getItems() != null) {
            itemsViewHolder.view.itemRecylerView.adapter = itemAdapter
            itemAdapter.updateAdapterData(itemList)
        }
        currencyAdapter.updateAdapterData(allCurrencies.value as List<SupportedCurrencies>)

        frameLayout.visibility = View.VISIBLE
        itemsViewHolder.itemsdisplayed = true

        removeViews(
            saveCardSwitchHolder,
            goPayViewsHolder,
            otpViewHolder,
           // itemsViewHolder
        )
        //Hide keyboard of any open
        CustomUtils.hideKeyboardFrom(paymentInlineViewHolder.view.context, paymentInlineViewHolder.view)
    }

    private fun caseNotDisplayControlCurrency() {
        if (goPayViewsHolder.goPayopened || itemsViewHolder.itemsdisplayed) setActionGoPayOpenedItemsDisplayed()
        else setActionNotGoPayOpenedNotItemsDisplayed()

    }

    private fun setActionGoPayOpenedItemsDisplayed() {
        removeViews(

            cardViewHolder,
            paymentInlineViewHolder,
            saveCardSwitchHolder,
            goPayViewsHolder,

            otpViewHolder,
            itemsViewHolder
        )
        if(:: webViewHolder.isInitialized){
            removeViews( webViewHolder)
        }
        addViews(
            cardViewHolder,
            paymentInlineViewHolder,
            saveCardSwitchHolder
        )
        paymentInlineViewHolder.resetPaymentCardView()
        //itemsViewHolder.resetView()
        //I comment   itemsViewHolder.setItemsRecylerView()
        //  itemsViewHolder?.view?.itemRecylerView?.adapter = itemAdapter
        frameLayout.visibility = View.GONE
    }

    private fun setActionNotGoPayOpenedNotItemsDisplayed() {
        saveCardSwitchHolder?.let {
            removeViews(
                cardViewHolder,
                paymentInlineViewHolder,
                it,
                itemsViewHolder
            )
        }

        if(::webViewHolder.isInitialized) removeViews(webViewHolder)

        saveCardSwitchHolder?.let {
            addViews(

                cardViewHolder,
                paymentInlineViewHolder,
                it
            )
        }
        saveCardSwitchHolder?.view?.cardSwitch?.payButton?.changeButtonState(ActionButtonState.IDLE)
        // itemsViewHolder.resetView()
        //  itemsViewHolder.setItemsRecylerView()
        //   itemsViewHolder.setCurrencyRecylerView()
        itemsViewHolder.view.mainCurrencyChip.chipsRecycler.adapter = currencyAdapter
        //  itemsViewHolder.view.itemRecylerView.adapter =itemAdapter
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

        amountViewHolder.changeGroupAction(false)
        amountViewHolder.view.amount_section.itemCountButton.text = LocalizationManager.getValue(
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
            cardViewHolder,
            paymentInlineViewHolder, saveCardSwitchHolder, amountViewHolder
        )
        addViews(amountViewHolder, otpViewHolder)

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
        println("mmmmmmmm" + amountViewHolder.view.amount_section.itemCountButton.text)

        removeViews(
            cardViewHolder,
            paymentInlineViewHolder, saveCardSwitchHolder, otpViewHolder, amountViewHolder
        )
        // bottomSheetDialog.dismissWithAnimation
        //start counter on open otpview
        otpViewHolder?.otpView?.startCounter()
        addViews(amountViewHolder, otpViewHolder)
        otpViewHolder.otpView.visibility = View.VISIBLE
        CustomUtils.showKeyboard(context)
        setOtpPhoneNumber(phoneNumber)
        otpViewHolder.otpView.changePhone.visibility = View.INVISIBLE
        otpViewHolder.otpView.timerText.setOnClickListener {
            resendOTPCode(chargeResponse)
            otpViewHolder.otpView.restartTimer()
        }
        amountViewHolder.changeGroupAction(false)
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
        saveCardSwitchHolder?.view?.mainSwitch?.switchSaveMobile?.visibility = View.GONE
        saveCardSwitchHolder?.view?.cardSwitch?.payButton?.visibility = View.GONE
        saveCardSwitchHolder?.view?.mainSwitch?.switchSaveMobile?.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) saveCardSwitchHolder?.view?.cardSwitch?.switchesLayout?.visibility =
                View.GONE
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

        println("redirectURL before display>>"+url)
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
            if(PaymentDataSource?.getWebViewType()!=null && PaymentDataSource.getWebViewType() ==WebViewType.REDIRECT){
                businessViewHolder.view.headerView.constraint.visibility = View.GONE
                businessViewHolder.view.topSeparatorLinear.visibility = View.GONE

                checkoutFragment.closeText.visibility = View.VISIBLE
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
                saveCardSwitchHolder?.view?.mainSwitch?.visibility = View.GONE
                saveCardSwitchHolder?.view?.cardSwitch?.payButton?.changeButtonState(ActionButtonState.LOADING)
                saveCardSwitchHolder?.view?.cardSwitch?.payButton?.visibility= View.GONE

                saveCardSwitchHolder?.view?.cardSwitch?.tapLogoImage?.visibility = View.GONE
                val bottomSheet: FrameLayout? =
                    bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet)
              //  BottomSheetBehavior.from(bottomSheet as View).state = BottomSheetBehavior.STATE_EXPANDED
                Handler(Looper.getMainLooper()).postDelayed({
                    fragmentManager.beginTransaction()
                        .replace(
                            R.id.webFrameLayout, WebFragment.newInstance(
                                redirectURL,
                                this, cardViewModel, authenticate
                            )
                        ).commitNow()
                    //  checkoutFragment.closeText.visibility = View.VISIBLE
                    webFrameLayout.visibility = View.VISIBLE


                }, 100)
                Handler(Looper.getMainLooper()).postDelayed({
                   // saveCardSwitchHolder?.view?.cardSwitch?.payButton?.visibility = View.INVISIBLE
                    removeViews(saveCardSwitchHolder)
                }, 2200)

            }else   if(PaymentDataSource?.getWebViewType()!=null && PaymentDataSource.getWebViewType() ==WebViewType.THREE_DS_WEBVIEW){
                  webViewHolder = WebViewHolder(context, fragmentManager,url,this,cardViewModel,authenticate)
                removeViews(
                    //  businessViewHolder,
                    //   amountViewHolder,
                    cardViewHolder,
                    saveCardSwitchHolder,
                    paymentInlineViewHolder,
                    otpViewHolder,
                    goPaySavedCardHolder,
                    goPayViewsHolder
                )
                //Added to hide the Items-Amount button when 3ds is opened within
                amountViewHolder?.view?.amount_section?.itemAmountLayout?.visibility = View.GONE
                addViews(webViewHolder)
                 Handler(Looper.getMainLooper()).postDelayed({

           webFrameLayout.visibility = View.VISIBLE
           }, 1800)


                //Stopped showing cancel button and poweredby for 3ds
             /*    saveCardSwitchHolder?.view?.cardSwitch?.payButton?.setButtonDataSource(
              true,
              LocalizationManager.getLocale(context).toString(),LocalizationManager.getValue("deleteSavedCardButtonCancel", "SavedCardTitle"),
                     Color.parseColor(ThemeManager.getValue("actionButton.Cancel.backgroundColor")),
                     Color.parseColor(ThemeManager.getValue("actionButton.Cancel.titleLabelColor")))

                saveCardSwitchHolder?.view?.cardSwitch?.payButton?.let {
                    setBorderedView(
                        it,
                        100.0f,
                        2.0f,
                        Color.parseColor(ThemeManager.getValue("actionButton.Cancel.borderColor")),
                        Color.parseColor(ThemeManager.getValue("actionButton.Cancel.backgroundColor")),
                        Color.parseColor(ThemeManager.getValue("actionButton.Cancel.backgroundColor"))
                    )
                }
          saveCardSwitchHolder?.view?.cardSwitch?.payButton?.changeButtonState(ActionButtonState.RESET)

          saveCardSwitchHolder?.view?.cardSwitch?.payButton?.setOnClickListener {
              this.dismissBottomSheet()

          }*/
            }




        }


       // removeViews(amountViewHolder, businessViewHolder)
        // tabAnimatedActionButtonViewHolder?.view?.actionButton?.visibility = View.INVISIBLE
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
            this,
            saveCardSwitchHolder,
            this,
            cardViewModel,
            checkoutFragment, loyaltyViewHolder
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
        //  println("if(::businessViewHolder.isInitialized getpay" + ::businessViewHolder.isInitialized)
        //  println("merchantData name>>" + merchantData?.name)
        //  println("merchantData logo>>" + merchantData?.logo)
        if (paymentOptionsResponse != null) {
            this.paymentOptionsResponse = paymentOptionsResponse
        }
        if (::businessViewHolder.isInitialized && PaymentDataSource.getTransactionMode() != TransactionMode.TOKENIZE_CARD) {
            businessViewHolder.setDataFromAPI(
                merchantData?.logo,
                merchantData?.name
            )
            if (merchantData?.verifiedApplication == true) {

            }
        }
        // println("PaymentOptionsResponse on get$paymentOptionsResponse")
        allCurrencies.value = paymentOptionsResponse?.supportedCurrencies
        savedCardList.value = paymentOptionsResponse?.cards
        currencyAdapter = CurrencyTypeAdapter(this)

        println("savedCardList on get" + savedCardList.value)
        println("paymentOptionsResponse?.supportedCurrencie on get" + paymentOptionsResponse?.supportedCurrencies)
        if (paymentOptionsResponse?.supportedCurrencies != null && ::amountViewHolder.isInitialized) {
            currentCurrency = paymentOptionsResponse.currency
            for (i in paymentOptionsResponse.supportedCurrencies.indices) {

                if (paymentOptionsResponse.supportedCurrencies[i].currency == currentCurrency) {
                    println("current amount value>>" + paymentOptionsResponse.supportedCurrencies[i].amount)
                    println("current currency value>>" + paymentOptionsResponse.supportedCurrencies[i].symbol)
                    currentAmount =
                        CurrencyFormatter.currencyFormat(paymentOptionsResponse.supportedCurrencies[i].amount.toString())
                    currentCurrency =
                        paymentOptionsResponse.supportedCurrencies[i].symbol.toString()
                    println("currentCurrency currency value>>" + currentCurrency)

                   /* if (currentCurrency.length == 2) {
                        currentCurrency =
                            paymentOptionsResponse.supportedCurrencies[i].currency.toString()
                    } else {
                        currentCurrency =
                            paymentOptionsResponse.supportedCurrencies[i].symbol.toString()
                        currentCurrencySymbol =
                            paymentOptionsResponse.supportedCurrencies[i].symbol.toString()
                    }*/
                    currentCurrency =
                        paymentOptionsResponse.supportedCurrencies[i].currency.toString()
                    currentCurrencySymbol =
                        paymentOptionsResponse.supportedCurrencies[i].symbol.toString()
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
            paymentOptionsResponse?.supportedCurrencies?.let {
                itemsViewHolder.setDataFromAPI(
                    it,
                    PaymentDataSource.getItems()
                )
            }
            //  itemsViewHolder.setItemsRecylerView()
            //  itemsViewHolder.setCurrencyRecylerView()


        }

        merchantData?.name?.let {
            saveCardSwitchHolder?.setDataFromAPI(
                it,
                paymentInlineViewHolder.selectedType
            )
        }
        paymentOptionsList.value = paymentOptionsResponse?.paymentOptions
        //println("paymentOptions value" + paymentOptionsResponse?.paymentOptions)
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
                cardViewHolder.view.mainChipgroup.chipsRecycler.addItemDecoration(divider)
                initAdaptersAction()

            }

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
        saveCardSwitchHolder?.view?.cardSwitch?.payButton?.setButtonDataSource(
            false,
            LocalizationManager.getLocale(context).toString(),
            if (::selectedAmount.isInitialized && ::selectedCurrency.isInitialized) {
                payString+" "+currentCurrencySymbol+" "+selectedAmount
            }else{ payString+" "+currentCurrencySymbol+" "+currentAmount},
            Color.parseColor(ThemeManager.getValue("actionButton.Invalid.backgroundColor")),
            Color.parseColor(ThemeManager.getValue("actionButton.Invalid.titleLabelColor")),
        )

    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun initAdaptersAction() {
        adapter = CardTypeAdapterUIKIT(this)
        goPayAdapter = GoPayCardAdapterUIKIT(this)
        itemAdapter = ItemAdapter()
        // adapter?.possiblyShowGooglePayButton()
        // val arrayList = ArrayList<String>()//Creating an empty arraylist
        //  arrayList.add("Google Pay")//Adding object in arraylist


        //adapter.updateAdapterGooglePay(arrayList)
        //  goPayAdapter.updateAdapterData(goPayCardList.value as List<GoPaySavedCards>)
        if (allCurrencies.value?.isNotEmpty() == true) {
            currencyAdapter.updateAdapterData(allCurrencies.value as List<SupportedCurrencies>)
        }
        if (savedCardList.value?.isNotEmpty() == true) {
            println("savedCardList.value" + PaymentDataSource.getCardType())
            if (PaymentDataSource.getCardType() != null && PaymentDataSource.getCardType() != CardType.ALL) {
                filterSavedCardTypes(savedCardList.value as List<SavedCard>)
            } else adapter.updateAdapterDataSavedCard(savedCardList.value as List<SavedCard>)
        } else {
            cardViewHolder.view.mainChipgroup.groupAction?.visibility = View.GONE
        }
        //  itemsViewHolder.view.itemRecylerView.adapter = itemAdapter
        itemsViewHolder.view.mainCurrencyChip.chipsRecycler.adapter = currencyAdapter
        if (PaymentDataSource.getItems() != null) {
            PaymentDataSource.getItems()?.let { itemAdapter.updateAdapterData(it) }
        }
        cardViewHolder.view.mainChipgroup.chipsRecycler.adapter = adapter
        // goPaySavedCardHolder.view.goPayLoginView.chipsRecycler.adapter = goPayAdapter
        cardViewHolder.view.mainChipgroup.groupAction?.visibility = View.VISIBLE
        cardViewHolder.view.mainChipgroup.groupAction?.setOnClickListener {
            setMainChipGroupActionListener()
            paymentInlineViewHolder.tapCardInputView?.clear()
            paymentInlineViewHolder.onFocusChange("")
            paymentInlineViewHolder.tapCardInputView.setSingleCardInput(
                CardBrandSingle.Unknown, null
            )
            CustomUtils.hideKeyboardFrom(context, paymentInlineViewHolder.view)
        }

        // filterCardTypes(paymentOptionsList.value as ArrayList<PaymentOption>)
        paymentOptionsWorker =
            java.util.ArrayList<PaymentOption>(paymentOptionsResponse.paymentOptions)

        // filterViewModels(PaymentDataSource.getCurrency()?.isoCode.toString())
        filterViewModels(currentCurrency)
        // filterModels(PaymentDataSource.getCurrency()?.isoCode.toString())
        //  filterCardTypes(PaymentDataSource.getCurrency()?.isoCode.toString(),paymentOptionsWorker)

        //    goPaySavedCardHolder.view.goPayLoginView.groupAction.setOnClickListener {
        //       setGoPayLoginViewGroupActionListener()
        //   }
        //paymentInlineViewHolder.tabLayout.setOnTouchListener { v, _ ->
        touchHandlingForCardView()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("ClickableViewAccessibility")
    private fun touchHandlingForCardView() {
        saveCardSwitchHolder?.view?.setOnTouchListener { v, _ ->
            CustomUtils.hideKeyboardFrom(context, paymentInlineViewHolder.view)
            paymentInlineViewHolder.resetView = true
            paymentInlineViewHolder.resetTouchView()
            //  paymentInlineViewHolder.onFocusChange("")

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

    @RequiresApi(Build.VERSION_CODES.N)
     fun filterCardTypes(list: ArrayList<PaymentOption>) {
        var filteredCardList: List<PaymentOption> =
            list.filter { items -> items.paymentType == PaymentType.CARD }

        println("filteredCardList value " + filteredCardList.size)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            paymentInlineViewHolder.setDataFromAPI(filteredCardList)
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
                LocalizationManager.getValue("edit", "Common")
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
                //saveCardSwitchHolder?.view?.cardSwitch?.payButton?.changeButtonState(ActionButtonState.LOADING)
                cardViewModel.processEvent(
                    CardViewEvent.DeleteSaveCardEvent,
                    this,
                    null,
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
                cardViewHolder.view.mainChipgroup.groupAction.visibility = View.VISIBLE
            }
        } else if (response == "NO") {
            adapter.updateShaking(false)
            deleteCard = false

        } else if (response == "OK") {
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
        println("delSelectedCard value is" + delSelectedCard.deleted)
        println("selectedItemsDel value is" +selectedItemsDel)
        if (delSelectedCard.deleted) {
            adapter.deleteSelectedCard(selectedItemsDel)
            adapter.updateAdapterDataSavedCard(arrayListSavedCardSize)
            adapter.updateShaking(false)
            deleteCard = false
            //saveCardSwitchHolder?.view?.cardSwitch?.payButton?.changeButtonState(ActionButtonState.SUCCESS)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun handleSuccessFailureResponseButton(
        response: String,
        authenticate: Authenticate?,
        chargeResponse: Charge?,
        tabAnimatedActionButton: TabAnimatedActionButton?
    ) {
        SessionManager.setActiveSession(false)
        println("response val>>"+response)
        println("tabAnimatedActionButton val>>"+tabAnimatedActionButton)
        println("tabAnimatedActionButton val>>"+tabAnimatedActionButton)
        /* if(chargeResponse?.status == null && response == "tokenized"){
             //todo replaced authorized with chargeresponse
             SDKSession.getListener()?.getStatusSDK(response,chargeResponse)
         }else{
             SDKSession.getListener()?.getStatusSDK(response,chargeResponse)
         }*/

        /***
         * This function is  working fine as expected in case when 3ds is false
         * i.e.  sdkSession.isRequires3DSecure(false) as no loading of url occurs direct response
         * from the API is available.
         * WRONG OTP scenario also handled here as similar to old sdk show user error button and
         * close the sdk.
         * **/

        tabAnimatedActionButton?.clearAnimation()
        if (::webFrameLayout.isInitialized) {
            if (fragmentManager.findFragmentById(R.id.webFrameLayout) != null)
                fragmentManager.beginTransaction()
                    .hide(fragmentManager.findFragmentById(R.id.webFrameLayout)!!)
                    .commit()
            webFrameLayout.visibility = View.GONE
        }

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
           // removeViews(businessViewHolder, amountViewHolder, saveCardSwitchHolder)
           // addViews(businessViewHolder, saveCardSwitchHolder)
            businessViewHolder.view.headerView.constraint.visibility = View.GONE
            businessViewHolder.view.topSeparatorLinear.visibility = View.GONE
            saveCardSwitchHolder?.view?.visibility = View.VISIBLE
            saveCardSwitchHolder?.view?.mainSwitch?.visibility = View.GONE
        }
        if(::checkoutFragment.isInitialized)
          checkoutFragment.closeText.visibility =View.GONE
        println("chargeResponse are>>>>"+chargeResponse?.status)
        println("saveCardSwitchHolder are>>>>"+saveCardSwitchHolder)
        if(response.contains("failure")){
            saveCardSwitchHolder?.view?.cardSwitch?.payButton?.setInValidBackground(false,Color.MAGENTA)
            saveCardSwitchHolder?.view?.cardSwitch?.payButton?.setButtonDataSource(
                false,
                "en",
                null,
                Color.parseColor(ThemeManager.getValue("actionButton.Invalid.backgroundColor")),
                Color.parseColor(ThemeManager.getValue("actionButton.Invalid.titleLabelColor")),
            )
            saveCardSwitchHolder?.view?.cardSwitch?.payButton?.changeButtonState(ActionButtonState.ERROR)
        }
        when (chargeResponse?.status) {
            ChargeStatus.CAPTURED, ChargeStatus.AUTHORIZED, ChargeStatus.VALID, ChargeStatus.IN_PROGRESS -> {

                saveCardSwitchHolder?.view?.cardSwitch?.payButton?.changeButtonState(
                    ActionButtonState.SUCCESS
                )
                tabAnimatedActionButton?.changeButtonState(ActionButtonState.SUCCESS)
            }
            ChargeStatus.CANCELLED, ChargeStatus.TIMEDOUT, ChargeStatus.FAILED, ChargeStatus.DECLINED, ChargeStatus.UNKNOWN,
            ChargeStatus.RESTRICTED, ChargeStatus.ABANDONED, ChargeStatus.VOID, ChargeStatus.INVALID -> {
                println("CANCELLED>>>"+tabAnimatedActionButton)
                println("CANCELLED 2>>>"+saveCardSwitchHolder?.view?.cardSwitch?.payButton)
                saveCardSwitchHolder?.view?.cardSwitch?.payButton?.setInValidBackground(false,Color.MAGENTA)
                tabAnimatedActionButton?.setInValidBackground(false,Color.MAGENTA)
                tabAnimatedActionButton?.setInValidBackground(false, Color.parseColor(ThemeManager.getValue("actionButton.Invalid.backgroundColor")))

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
                tabAnimatedActionButton?.setInValidBackground(false, Color.parseColor(ThemeManager.getValue("actionButton.Invalid.backgroundColor")))

                tabAnimatedActionButton?.setButtonDataSource(
                    false,
                    "en",
                    "",
                    Color.parseColor(ThemeManager.getValue("actionButton.Invalid.backgroundColor")),
                    Color.parseColor(ThemeManager.getValue("actionButton.Invalid.titleLabelColor")),
                )
                //tabAnimatedActionButton?.changeButtonState(ActionButtonState.LOADING)

                tabAnimatedActionButton?.changeButtonState(
                    ActionButtonState.ERROR
                )

                val payString: String = LocalizationManager.getValue("pay", "ActionButton")
               /* saveCardSwitchHolder?.view?.cardSwitch?.payButton?.setButtonDataSource(
                    false,
                    "en",
                   null,
                    Color.parseColor(ThemeManager.getValue("actionButton.Invalid.backgroundColor")),
                    Color.parseColor(ThemeManager.getValue("actionButton.Invalid.titleLabelColor")),
                )

                tabAnimatedActionButton?.changeButtonState(ActionButtonState.ERROR)
               tabAnimatedActionButton?.setButtonDataSource(
                    false,
                    "en",
                   "",
                    Color.parseColor(ThemeManager.getValue("actionButton.Invalid.backgroundColor")),
                    Color.parseColor(ThemeManager.getValue("actionButton.Invalid.titleLabelColor")),
                )*/
                tabAnimatedActionButton?.setInValidBackground(false, Color.parseColor(ThemeManager.getValue("actionButton.Valid.paymentBackgroundColor")))

            }
            else -> {

                if (response == "tokenized") {
                    saveCardSwitchHolder?.view?.cardSwitch?.payButton?.changeButtonState(
                        ActionButtonState.SUCCESS
                    )
                } else {
                    println("is this called>>>")
                    removeAllViews()
                    if (::businessViewHolder.isInitialized && saveCardSwitchHolder != null) {
                        addViews(businessViewHolder, saveCardSwitchHolder)
                        businessViewHolder.view.headerView.constraint.visibility = View.GONE
                        businessViewHolder.view.topSeparatorLinear.visibility = View.GONE
                        saveCardSwitchHolder?.view?.visibility = View.VISIBLE
                        saveCardSwitchHolder?.view?.mainSwitch?.visibility = View.GONE
                        saveCardSwitchHolder?.view?.cardSwitch?.payButton?.setInValidBackground(false,Color.RED)

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
                        saveCardSwitchHolder?.view?.cardSwitch?.payButton?.isEnabled = true
                        saveCardSwitchHolder?.view?.cardSwitch?.payButton?.isClickable = true
                    }
                }
            }
        }
        Handler().postDelayed({
            tabAnimatedActionButton?.onMorphAnimationReverted()
            tabAnimatedActionButton?.clearAnimation()
            SDKSession.sessionActive = false

        }, 4000)
        SessionManager.setActiveSession(false)
        tabAnimatedActionButton?.setOnClickListener {
            // if(::fragmentManager.isInitialized)
            tabAnimatedActionButton.changeButtonState(ActionButtonState.LOADING)
            SDKSession.startSDK(
                (tabAnimatedActionButton.context as AppCompatActivity).supportFragmentManager,
                tabAnimatedActionButton.context,
                tabAnimatedActionButton.context as AppCompatActivity
            )
        }
       Handler().postDelayed({
            if (::bottomSheetDialog.isInitialized)
                bottomSheetDialog.dismiss()

        }, 5000)


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
        unActivateActionButton()
    }

    private fun removeViews(vararg viewHolders: TapBaseViewHolder?) {

        viewHolders.forEach {
            Handler(Looper.getMainLooper()).postDelayed(Runnable {

                if (::context.isInitialized) {

                  /*  val fadeOut = AlphaAnimation(1f, 0f)
                    fadeOut.interpolator = AccelerateInterpolator() //and this
                    fadeOut.startOffset = 100*/
                  //  fadeOut.duration = 10
                   // val animation = AnimationSet(false) //change to false
                //    animation.addAnimation(fadeOut)
                    //     animation.addAnimation(fadeOut)
                  //  val animation = AnimationUtils.loadAnimation(context, R.anim.fade_out)
                   // it?.view?.startAnimation(animation)
                }

                if (::sdkLayout.isInitialized) {
                    bottomSheetLayout
                    sdkLayout.removeView(it?.view)
                }

            }, 0)

        }

    }


    private fun addViews(vararg viewHolders: TapBaseViewHolder?) {

        viewHolders.forEach {
            Handler(Looper.getMainLooper()).postDelayed(Runnable {

                if (::context.isInitialized) {


                    val fadeIn = AlphaAnimation(0f, 1f)
                    fadeIn.interpolator = DecelerateInterpolator() //add this
                    fadeIn.duration = 1000

                    val animation = AnimationSet(false) //change to false
                    animation.addAnimation(fadeIn)


                   // val animati1on = AnimationUtils.loadAnimation(context, R.anim.fade_in)
                    // animati1on.duration = 500L
                    it?.view?.startAnimation(animation)

                }

                if (::sdkLayout.isInitialized) {
                    sdkLayout.removeView(it?.view)
                    sdkLayout.addView(it?.view)
                }

                //   checkoutFragment?.scrollView?.fullScroll(View.FOCUS_DOWN)
                // bottomSheetDialog.behavior.setPeekHeight(sdkLayout.height)
                //  bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
                //bottomSheetDialog.behavior.saveFlags = BottomSheetBehavior.SAVE_FIT_TO_CONTENTS
                // checkoutFragment.scrollView?.fullScroll(View.FOCUS_DOWN)

                // AnimationEngine.applyTransition(sdkLayout,SLIDE,1500)
                bottomSheetLayout.startAnimation(AnimationUtils.loadAnimation(context,R.anim.slide_up))
            }, 0)
            BottomSheetBehavior.STATE_HALF_EXPANDED
        }



    }

    private fun unActivateActionButton() {
        val payString: String

        when (PaymentDataSource.getTransactionMode()) {
            TransactionMode.TOKENIZE_CARD -> payString = LocalizationManager.getValue(
                "pay",
                "ActionButton"
            )
            TransactionMode.SAVE_CARD -> payString = LocalizationManager.getValue(
                "savecard",
                "ActionButton"
            )
            else -> payString = LocalizationManager.getValue("pay", "ActionButton")
        }

        saveCardSwitchHolder?.view?.cardSwitch?.payButton?.setButtonDataSource(
            false,
            LocalizationManager.getLocale(context).toString(),
            if (::selectedAmount.isInitialized && ::selectedCurrency.isInitialized) {
                payString+" "+currentCurrencySymbol+" "+selectedAmount
            }else{ payString+" "+currentCurrencySymbol+" "+currentAmount},
            Color.parseColor(ThemeManager.getValue("actionButton.Invalid.backgroundColor")),
            Color.parseColor(ThemeManager.getValue("actionButton.Invalid.titleLabelColor")),
        )

        // saveCardSwitchHolder?.view?.cardSwitch?.payButton?.changeButtonState(ActionButtonState.IDLE)
           saveCardSwitchHolder?.view?.cardSwitch?.payButton?.isActivated = false
        saveCardSwitchHolder?.view?.cardSwitch?.payButton?.isClickable = false
    }


    fun resetCardSelection() {
        adapter.resetSelection()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCardSelectedAction(isSelected: Boolean, savedCardsModel: Any?) {

       // cardViewHolder.view.alpha = 0.95f
        /**
         * Clear card input text
         */
        println("focussss"+paymentInlineViewHolder.tapCardInputView.hasFocus())
        println("isSelected"+isSelected)
       // paymentInlineViewHolder.resetPaymentCardView()
        paymentInlineViewHolder.tapCardInputView.clear()
        paymentInlineViewHolder.clearCardInputAction()
       /* if (paymentInlineViewHolder.tapCardInputView.hasFocus()) {
            paymentInlineViewHolder.tapCardInputView.clear()
           // paymentInlineViewHolder.clearCardInputAction()
            //paymentInlineViewHolder.onFocusChange("")
           // CustomUtils.hideKeyboardFrom(context, paymentInlineViewHolder.view)
        }*/
        println("savedCardsModel" + savedCardsModel)
        unActivateActionButton()
        when (savedCardsModel) {
            is SavedCard -> {
              //  paymentInlineViewHolder.view.alpha = 1f
              paymentInlineViewHolder.setDataForSavedCard(
                    savedCardsModel,
                    CardInputUIStatus.SavedCard
                )
                setPayButtonAction(PaymentType.SavedCard, savedCardsModel)
                isSavedCardSelected = true
            }
            else -> {
                if (savedCardsModel != null) {
                    println("paymentType"+(savedCardsModel as PaymentOption).paymentType)
                    if ((savedCardsModel as PaymentOption).paymentType == PaymentType.WEB) {
                      //  paymentInlineViewHolder.view.alpha = 0.95f

                        PaymentDataSource.setWebViewType(WebViewType.REDIRECT)
                        activateActionButton()
                        setPayButtonAction(PaymentType.WEB, savedCardsModel)
                    } else if ((savedCardsModel as PaymentOption).paymentType == PaymentType.GOOGLE_PAY) {
                        removeViews(amountViewHolder,cardViewHolder,paymentInlineViewHolder)
                        checkoutFragment.checkOutActivity?.handleGooglePayApiCall()
                        activateActionButtonForGPay()
                        //setPayButtonAction(PaymentType.WEB, savedCardsModel)
                        PaymentDataSource.setWebViewType(WebViewType.REDIRECT)
                    }

                } else

                    displayGoPayLogin()
            }
        }
    }

    private fun activateActionButtonForGPay() {
       val payString:String = LocalizationManager.getValue("pay", "ActionButton")
        saveCardSwitchHolder?.view?.cardSwitch?.payButton?.isActivated
        saveCardSwitchHolder?.view?.cardSwitch?.payButton?.changeButtonState(ActionButtonState.LOADING)
        saveCardSwitchHolder?.view?.cardSwitch?.payButton?.setButtonDataSource(
            true,
            "en",
           "",
            Color.parseColor(ThemeManager.getValue("actionButton.Valid.paymentBackgroundColor")),
            Color.parseColor(ThemeManager.getValue("actionButton.Valid.titleLabelColor")),
        )

        saveCardSwitchHolder?.view?.cardSwitch?.showOnlyPayButton()

        saveCardSwitchHolder?.view?.cardSwitch?.payButton?.isActivated
        saveCardSwitchHolder?.view?.cardSwitch?.payButton?.changeButtonState(ActionButtonState.LOADING)
    }


    private fun activateActionButton() {
        val payString: String
        when (PaymentDataSource.getTransactionMode()) {
            TransactionMode.TOKENIZE_CARD -> payString = LocalizationManager.getValue(
                "pay",
                "ActionButton"
            )
            TransactionMode.SAVE_CARD -> payString = LocalizationManager.getValue(
                "savecard",
                "ActionButton"
            )
            else -> payString = LocalizationManager.getValue("pay", "ActionButton")
        }

        saveCardSwitchHolder?.view?.cardSwitch?.payButton?.setButtonDataSource(
            true,
            "en",
            if (::selectedAmount.isInitialized && ::selectedCurrency.isInitialized) {
                payString+" "+currentCurrencySymbol+" "+selectedAmount
            }else{ payString+" "+currentCurrencySymbol+" "+currentAmount},
            Color.parseColor(ThemeManager.getValue("actionButton.Valid.paymentBackgroundColor")),
            Color.parseColor(ThemeManager.getValue("actionButton.Valid.titleLabelColor")),
        )

        saveCardSwitchHolder?.view?.cardSwitch?.showOnlyPayButton()
        saveCardSwitchHolder?.view?.cardSwitch?.payButton?.isActivated
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("WrongConstant")
    private fun onClickRedirect(savedCardsModel: Any) {
        selectedPaymentOption = savedCardsModel as PaymentOption
        cardViewModel.processEvent(
            CardViewEvent.ChargeEvent,
            this,
            null,
            selectedPaymentOption,
            null,
            null,
            null
        )

        saveCardSwitchHolder?.view?.cardSwitch?.payButton?.changeButtonState(ActionButtonState.LOADING)
        saveCardSwitchHolder?.view?.mainSwitch?.mainTextSave?.visibility = View.INVISIBLE

        //Commented to try the flow of redirect
      /*  removeViews(
            //    businessViewHolder,
            //    amountViewHolder,
            cardViewHolder,
            paymentInlineViewHolder,
            tabAnimatedActionButtonViewHolder
        )*/


    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun onClickCardPayment() {
        println("onClickCardPayment")
        println("paymentInlineViewHolder val"+paymentInlineViewHolder.getCard())
        if(isSavedCardSelected == true){
            cardViewModel.processEvent(
                CardViewEvent.CreateTokenExistingCardEvent,
                this,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                paymentInlineViewHolder.getSavedCardData()
            )

        }else {
            cardViewModel.processEvent(
                CardViewEvent.CreateTokenEvent,
                this,
                null,
                null,
                null,
                paymentInlineViewHolder.getCard(),
                null
            )
        }

        setSlideAnimation()

        saveCardSwitchHolder?.view?.cardSwitch?.payButton?.changeButtonState(ActionButtonState.LOADING)
        saveCardSwitchHolder?.view?.mainSwitch?.visibility = View.GONE

        removeViews(
          //  businessViewHolder,
         //   amountViewHolder,
            cardViewHolder,
            paymentInlineViewHolder,
            tabAnimatedActionButtonViewHolder
        )


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
        maskedCardNumber: String,
        arrayListSavedCardSize: ArrayList<SavedCard>
    ) {
        this.cardId = cardId
        if (stopAnimation) {
            stopDeleteActionAnimation(itemId, maskedCardNumber,arrayListSavedCardSize)
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
        cardViewHolder.view.mainChipgroup.groupAction?.text = LocalizationManager.getValue(
            "GatewayHeader",
            "HorizontalHeaders",
            "rightTitle"
        )
        val title: String = LocalizationManager.getValue("deleteSavedCardTitle", "SavedCardTitle")
        val message: String = LocalizationManager.getValue(
            "deleteSavedCardMsg",
            "SavedCardTitle"
        )
        CustomUtils.showDialog(
            "$title",
            "$message $maskedCardNumber ?",
            context,
            4,
            this, null, null, false
        )
        selectedItemsDel = itemId
        deleteCard = true
        this.arrayListSavedCardSize = arrayListSavedCardSizes

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

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onGooglePayClicked(isClicked: Boolean) {
        println("onGooglePayClicked>>>" + isClicked)
        checkoutFragment.checkOutActivity?.handleGooglePayApiCall()

    }


    override fun onPayCardSwitchAction(isCompleted: Boolean, paymentType: PaymentType) {
        println("isCompleted???" + isCompleted)
        //todo add validations from api when cvv is valid the only  activate ActionButton
        if (isCompleted) {
            businessViewHolder.view?.headerView.constraint.visibility = View.VISIBLE
            saveCardSwitchHolder?.view?.mainSwitch?.visibility = View.GONE
            saveCardSwitchHolder?.view?.mainSwitch?.switchSaveMobile?.visibility = View.GONE
            saveCardSwitchHolder?.setSwitchToggleData(paymentType)
            loyaltyViewHolder.view.loyaltyView?.constraintLayout?.visibility = View.VISIBLE
            // loyatFlag = true
            //  initLoyaltyView() // Will be enabled when coming from API directly
            activateActionButton()
            paymentActionType = paymentType
        } else {
//            saveCardSwitchHolder11?.view?.mainSwitch?.visibility = View.GONE
            saveCardSwitchHolder?.view?.mainSwitch?.switchSaveMobile?.visibility = View.GONE
            saveCardSwitchHolder?.setSwitchToggleData(paymentType)
            unActivateActionButton()
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onPayCardCompleteAction(
        isCompleted: Boolean,
        paymentType: PaymentType,
        cardNumber: String,
        expiryDate: String,
        cvvNumber: String,holderName:String?
    ) {
        activateActionButton()
        setPayButtonAction(paymentType, null)


    }

    override fun onPaymentCardIsLoyaltyCard(isLoyaltyCard: Boolean) {
        //Todo logic for loyalty card
    }

    // Override function to open NFC fragment and scan the card via NFC.
    override fun onClickNFC() {
        setSlideAnimation()
        removeViews(
            //businessViewHolder,
            // amountViewHolder,
            cardViewHolder,
            saveCardSwitchHolder,
            paymentInlineViewHolder,
            otpViewHolder,
            goPaySavedCardHolder,
            goPayViewsHolder
        )
        //  addViews(businessViewHolder, amountViewHolder)
        frameLayout.visibility = View.VISIBLE
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
            cardViewHolder,
            saveCardSwitchHolder,
            paymentInlineViewHolder,
            otpViewHolder,
            goPaySavedCardHolder,
            goPayViewsHolder
        )
        amountViewHolder.readyToScanVisibility(scannerClicked)
        // addViews(businessViewHolder, amountViewHolder)
        inLineCardLayout.visibility = View.VISIBLE
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

        currencyOldRate = currencyRate
        lastSelectedCurrency = previousSelectedCurrency
        if (::unModifiedItemList.isInitialized)
            println("unModifiedItemList" + unModifiedItemList)
        if (::itemList.isInitialized) {
            for (i in itemList.indices) {
                itemList[i].amount = unModifiedItemList[i].amount?.times(currencyRate)
                //itemList[i].totalAmount = currencyOldRate?.div(currencyRate)
                itemList[i].totalAmount =
                    unModifiedItemList[i].getPlainAmount()?.times(currencyRate)

                // if( currencySelected !="KWD" && lastSelectedCurrency !="KWD"){
                /*   if( currencySelected !=PaymentDataSource.getCurrency()?.isoCode && lastSelectedCurrency !=PaymentDataSource.getCurrency()?.isoCode){
                     //   itemList[i].amountPerUnit = currencyOldRate?.div(currencyRate)!!
                        itemList[i].amountPerUnit = unModifiedItemList[i].amountPerUnit.times(currencyRate)
                            //itemList[i].totalAmount = currencyOldRate?.div(currencyRate)
                       itemList[i].totalAmount =  unModifiedItemList[i].totalAmount?.times(currencyRate)



                 //  }  else if(currencySelected == PaymentDataSource.getSelectedCurrency()){
                   }  else if(currencySelected == PaymentDataSource.getCurrency()?.isoCode){
                       // currentCalculatedAmount = itemList[i].amountPerUnit
                       // itemList[i].amountPerUnit = (currencyOldRate?.div(currencyRate)!!)
                        itemList[i].amountPerUnit = unModifiedItemList[i].amountPerUnit.div(currencyRate)
                       itemList[i].totalAmount = unModifiedItemList[i].totalAmount?.div(currencyRate)


                   }*/
                println("item per unit >>" + itemList[i].amount)


            }

            itemsViewHolder.view.itemRecylerView.adapter = itemAdapter
            itemAdapter.updateAdapterData(itemList)

        }

        //  itemList[i].amount = (list[i].amount.toLong())
        //  itemList[i].currency = currencySelected

        selectedAmount = CurrencyFormatter.currencyFormat(totalSelectedAmount.toString())
        selectedCurrency = currencySelected
        selectedTotalAmount = selectedAmount
        println("selectedAmount final>>" + selectedAmount)
        println("selectedCurrency final>>" + selectedCurrency.length)
        println("currentAmount final>>" + currentAmount)
        println("currentCurrency final>>" + currentCurrency)
        println("selectedCurrencySymbol final>>" + selectedCurrencySymbol)
        if (selectedCurrencySymbol.length == 2) {
            amountViewHolder.updateSelectedCurrency(
                displayItemsOpen,
                selectedAmount, selectedCurrency,
                currentAmount, currentCurrency,selectedCurrencySymbol
            )
          //  PaymentDataSource.setSelectedCurrency(selectedCurrency = selectedCurrency, null) // changed from null to symbol
            PaymentDataSource.setSelectedCurrency(selectedCurrency, selectedCurrencySymbol)
          //  PaymentDataSource.setSelectedCurrency(selectedCurrency = selectedCurrencySymbol, selectedCurrencySymbol) //commented

        } else {
            amountViewHolder.updateSelectedCurrency(
                displayItemsOpen,
                selectedAmount, selectedCurrency,
                currentAmount, currentCurrency, selectedCurrencySymbol
            )
            PaymentDataSource.setSelectedCurrency(selectedCurrency, selectedCurrencySymbol)

        }
        currentCurrencySymbol = selectedCurrencySymbol


        // PaymentDataSource.setSelectedCurrency(selectedCurrency = selectedCurrency)
        PaymentDataSource.setSelectedAmount(currencyRate)
        if (paymentInlineViewHolder.tapCardInputView.isNotEmpty()) {
            paymentInlineViewHolder.tapCardInputView.clear()
            paymentInlineViewHolder.tapAlertView?.visibility = View.GONE
            paymentInlineViewHolder.acceptedCardText.visibility = View.VISIBLE
            paymentInlineViewHolder.tabLayout.resetBehaviour()
        }

        adapter.resetSelection()

        if (::selectedCurrency.isInitialized) {
            filterViewModels(selectedCurrency)
        } else filterViewModels(currentCurrency)

    }

    @SuppressLint("ResourceType")
    override fun redirectLoadingFinished(
        done: Boolean,
        chargeResponse: Charge?,
        contextSDK: Context?
    ) {

        println("done val" + done + "chargeResponse status" + chargeResponse?.status)
        println("saveCardSwitchHolder val" + saveCardSwitchHolder)
        println("redirect val" + chargeResponse?.response)
        println("gatewayResponse val" + chargeResponse?.gatewayResponse)
        removeViews(businessViewHolder,saveCardSwitchHolder)
        if (::webFrameLayout.isInitialized) {
            if (fragmentManager.findFragmentById(R.id.webFrameLayout) != null)
                fragmentManager.beginTransaction()
                    .hide(fragmentManager.findFragmentById(R.id.webFrameLayout)!!)
                    .commit()
            webFrameLayout.visibility = View.GONE

        }
        removeViews(businessViewHolder,amountViewHolder,cardViewHolder,paymentInlineViewHolder)
        if(::webViewHolder.isInitialized)removeViews(webViewHolder)
         addViews(businessViewHolder,saveCardSwitchHolder)
        businessViewHolder.view.headerView.constraint.visibility = View.GONE
        businessViewHolder.view.topSeparatorLinear.visibility = View.GONE
        saveCardSwitchHolder?.view?.cardSwitch?.switchesLayout?.visibility= View.GONE
        saveCardSwitchHolder?.view?.cardSwitch?.payButton?.visibility= View.VISIBLE
        saveCardSwitchHolder?.view?.cardSwitch?.payButton?.changeButtonState(
            ActionButtonState.ERROR
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
        checkoutFragment.closeText.visibility =View.GONE
     /*   val payString: String = LocalizationManager.getValue("pay", "ActionButton")
        if(chargeResponse?.status== ChargeStatus.CAPTURED||chargeResponse?.status ==ChargeStatus.INITIATED){
        saveCardSwitchHolder?.view?.cardSwitch?.payButton?.setButtonDataSource(
            true,"en","",
            Color.parseColor(ThemeManager.getValue("actionButton.Valid.paymentBackgroundColor")),
            Color.parseColor(ThemeManager.getValue("actionButton.Valid.titleLabelColor")))
        saveCardSwitchHolder?.view?.cardSwitch?.payButton?.changeButtonState(ActionButtonState.LOADING)
        saveCardSwitchHolder?.view?.cardSwitch?.payButton?.changeButtonState(ActionButtonState.SUCCESS)
        }else {
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
        }*/
    /*  if (::bottomSheetDialog.isInitialized)
            bottomSheetDialog.dismiss()*/

        Handler().postDelayed({
            if (::bottomSheetDialog.isInitialized)
                bottomSheetDialog.dismiss()

        }, 3000)


    }


    @RequiresApi(Build.VERSION_CODES.N)
    private fun setPayButtonAction(paymentTypeEnum: PaymentType, savedCardsModel: Any?) {

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
                            PaymentType.CARD,
                            savedCardsModel,
                            PaymentDataProvider().getSelectedCurrency()
                        )

                    } else checkForExtraFees(
                        currentAmount,
                        currentCurrency,
                        PaymentType.CARD,
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
            }
        }
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
            if (paymentTypeEnum == PaymentType.CARD || paymentTypeEnum == PaymentType.SavedCard) {
                savedCardsModel as SavedCard
                if (savedCardsModel.paymentOptionIdentifier.toInt() == 3 || savedCardsModel.paymentOptionIdentifier.toInt() == 4) {
                    setDifferentPaymentsAction(PaymentType.SavedCard, savedCardsModel)
                }

            } else setDifferentPaymentsAction(paymentTypeEnum, savedCardsModel)
        } else setDifferentPaymentsAction(paymentTypeEnum, savedCardsModel)
    }


    @RequiresApi(Build.VERSION_CODES.N)
    private fun payActionSavedCard(savedCardsModel: SavedCard?) {
        println("payActionSavedCard" + savedCardsModel)
        saveCardSwitchHolder?.view?.cardSwitch?.payButton?.changeButtonState(
            ActionButtonState.LOADING
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

    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun startSavedCardPaymentProcess(savedCard: SavedCard?) {
        val createTokenSavedCard =
            CreateTokenSavedCard(savedCard?.id, PaymentDataSource.getCustomer().identifier)
        cardViewModel.processEvent(
            CardViewEvent.CreateTokenExistingCardEvent,
            this,
            null,
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
    }

    private fun setAllSeparatorTheme() {
        val separatorViewTheme = SeparatorViewTheme()
        separatorViewTheme.strokeColor =
            Color.parseColor(ThemeManager.getValue("tapSeparationLine.backgroundColor"))
        separatorViewTheme.strokeHeight = ThemeManager.getValue("tapSeparationLine.height")
        businessViewHolder.view.topSeparatorLinear.topSeparator.setTheme(separatorViewTheme)

        if (ThemeManager.currentTheme.isNotEmpty() && ThemeManager.currentTheme.contains("dark")){
            amountViewHolder.view.amountView_separator.visibility =View.GONE
        }else  amountViewHolder.view.amountView_separator.visibility =View.VISIBLE
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
            inLineCardLayout.visibility = View.GONE
            amountViewHolder.readyToScanVisibility(false)
            saveCardSwitchHolder?.view?.cardSwitch?.payButton?.changeButtonState(
                ActionButtonState.RESET
            )

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
            // businessViewHolder,
            /// amountViewHolder,
            cardViewHolder,
            paymentInlineViewHolder,
            saveCardSwitchHolder
        )

        if (card != null && card.cardNumber?.trim() != null && card.cardNumber.trim().length == 6) {
            callBinLookupApi(card.cardNumber.trim().substring(0, 6))
        }


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
        }, 300)



        // inlineCamerFragment.onDestroy()
        removeInlineScanner()


    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun setScannedCardDetails(card: TapCard) {
       /* if(CardUtils.isValidCardNumber(card.cardNumber)){
            paymentInlineViewHolder.tapCardInputView.onTouchView()
        }else {
            paymentInlineViewHolder.tapCardInputView.onTouchCardField()
        }*/
        //paymentInlineViewHolder.tapCardInputView.onTouchCardField()
        println("scanned card holder is${card.cardHolder}")
        println("scanned card number is${card.cardNumber}")

        paymentInlineViewHolder.tapCardInputView.setCardNumberMasked(paymentInlineViewHolder.maskCardNumber(card.cardNumber))
            val dateParts: List<String>? = card.expirationDate?.split("/")
            val month = dateParts?.get(0)?.toInt()
            val year = dateParts?.get(1)?.toInt()
            if (month != null) {
                if (year != null) {
                    paymentInlineViewHolder.tapCardInputView.setExpiryDate(month, year)
                }
            }


       // paymentInlineViewHolder.tapCardInputView.setCardHolderName(card.cardHolder)

    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun callBinLookupApi(binLookUpStr: String?) {
        cardViewModel.processEvent(
            CardViewEvent.RetreiveBinLookupEvent,
            CheckoutViewModel(), null, null, binLookUpStr, null, null
        )

    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun handleNFCScannedResult(emvCard: TapEmvCard) {
        println("emvCard>>" + emvCard)
        //   removeViews(amountViewHolder, businessViewHolder)
        addViews(
            //   businessViewHolder,
            //   amountViewHolder,
            cardViewHolder,
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
        paymentInlineViewHolder.tapCardInputView.onTouchView()
        paymentInlineViewHolder.tapCardInputView.setCardNumberMasked(paymentInlineViewHolder.maskCardNumber(emvCard.cardNumber))
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
        frameLayout.visibility = View.GONE
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
                        paymentInlineViewHolder.tapCardInputView.setExpiryDate(month, year.toInt())
                       // paymentInlineViewHolder.tapCardInputVie

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

        val cardPaymentOptions: java.util.ArrayList<PaymentOption> =
            filteredByPaymentTypeAndCurrencyAndSortedList(
                paymentOptionsWorker, PaymentType.CARD, currency
            )

        val googlePaymentOptions: java.util.ArrayList<PaymentOption> =
            filteredByPaymentTypeAndCurrencyAndSortedList(
                paymentOptionsWorker, PaymentType.GOOGLE_PAY, currency
            )
      //  println("googlePaymentOptions"+googlePaymentOptions)
        val hasWebPaymentOptions = webPaymentOptions.size > 0
        val hasCardPaymentOptions = cardPaymentOptions.size > 0
        val hasGooglePaymentOptions = googlePaymentOptions.size > 0
       // println("hasGooglePaymentOptions"+hasGooglePaymentOptions)

        //Added if else to update showing GooglePay button based on api
        if (hasGooglePaymentOptions && googlePaymentOptions.isNotEmpty()) {
            adapter.updateAdapterGooglePay(googlePaymentOptions)
            PaymentDataSource.setGoogleCardPay(googlePaymentOptions)
        }else {
            adapter.updateAdapterGooglePay(googlePaymentOptions)
        PaymentDataSource.setGoogleCardPay(googlePaymentOptions)
        }
        logicToHandlePaymentDataType(webPaymentOptions, cardPaymentOptions)

    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun logicToHandlePaymentDataType(
        webPaymentOptions: ArrayList<PaymentOption>,
        cardPaymentOptions: ArrayList<PaymentOption>

    ) {
        // println("webPaymentOptions in logic >>>>$webPaymentOptions")
        //  println("cardPaymentOptions in logic >>>>$cardPaymentOptions")

        if (PaymentDataSource.getPaymentDataType() != null && PaymentDataSource.getPaymentDataType() == "WEB" && PaymentDataSource.getTransactionMode() != TransactionMode.AUTHORIZE_CAPTURE) {
            adapter.updateAdapterDataSavedCard(ArrayList())
            adapter.updateAdapterData(webPaymentOptions)
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
                adapter.updateAdapterData(webPaymentOptions)
                if (!cardPaymentOptions.isEmpty()) {
                    paymentInlineViewHolder.setDataFromAPI(cardPaymentOptions)


                } else {
                    saveCardSwitchHolder?.mainTextSave?.visibility = View.GONE
                    removeViews(paymentInlineViewHolder)
                }

            }
        }
    }

    var title: String = LocalizationManager.getValue("title", "ExtraFees")

    @RequiresApi(Build.VERSION_CODES.N)
    private fun showExtraFees(
        totalAmount: String,
        extraFeesAmount: String,
        paymentType: PaymentType, savedCardsModel: Any?, selectedCurrency: String
    ) {
        val extraFeesPart1: String = LocalizationManager.getValue<String>(
            "message",
            "ExtraFees"
        ).replaceFirst("%@",extraFeesAmount+selectedCurrency)
        val extraFeesPart2: String = extraFeesPart1.replace("%@",totalAmount+selectedCurrency)
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
                PaymentDataSource.setWebViewType(WebViewType.THREE_DS_WEBVIEW)
                //Added to disable click when button loading
                amountViewHolder.view.amount_section?.itemAmountLayout?.isEnabled = false
                amountViewHolder.view.amount_section?.itemAmountLayout?.isClickable = false
                onClickCardPayment()
            }
            paymentType === PaymentType.SavedCard -> {
                if(isSavedCardSelected== true){
                    //Added to disable click when button loading
                    amountViewHolder.view.amount_section?.itemAmountLayout?.isEnabled = false
                    amountViewHolder.view.amount_section?.itemAmountLayout?.isClickable = false
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
        removeViews(businessViewHolder, asynchronousPaymentViewHolder)
        businessViewHolder.setDataFromAPI(
            PaymentDataSource.getMerchantData()?.logo,
            PaymentDataSource.getMerchantData()?.name
        )
        addViews(
            businessViewHolder,
            amountViewHolder,
            cardViewHolder,
            paymentInlineViewHolder,
            saveCardSwitchHolder
        )
        saveCardSwitchHolder?.view?.visibility = View.VISIBLE
        saveCardSwitchHolder?.view?.cardSwitch?.payButton?.changeButtonState(ActionButtonState.RESET)
        val payString: String = LocalizationManager.getValue("pay", "ActionButton")
        saveCardSwitchHolder?.view?.cardSwitch?.payButton?.setButtonDataSource(
            false,
            "en",
            if (::selectedAmount.isInitialized && ::selectedCurrency.isInitialized) {
                payString+" "+currentCurrencySymbol+" "+selectedAmount
            }else{ payString+" "+currentCurrencySymbol+" "+currentAmount},
            Color.parseColor(ThemeManager.getValue("actionButton.Invalid.backgroundColor")),
            Color.parseColor(ThemeManager.getValue("actionButton.Invalid.titleLabelColor")),
        )
    }

    fun showOnlyButtonView(
        status: ChargeStatus,
        checkOutActivity: CheckOutActivity?,
        _checkoutFragment: CheckoutFragment
    ) {
        println("checkoutFragment>>>." + _checkoutFragment)
        println("checkOutActivity>>>." + checkOutActivity)
        removeAllViews()
        addViews(saveCardSwitchHolder)
        val payString: String = LocalizationManager.getValue("pay", "ActionButton")
        saveCardSwitchHolder?.view?.cardSwitch?.payButton?.setButtonDataSource(
            false,
            "en",
            if (::selectedAmount.isInitialized && ::selectedCurrency.isInitialized) {
                payString+" "+currentCurrencySymbol+" "+selectedAmount
            }else{ payString+" "+currentCurrencySymbol+" "+currentAmount},
            Color.parseColor(ThemeManager.getValue("actionButton.Valid.paymentBackgroundColor")),
            Color.parseColor(ThemeManager.getValue("actionButton.Valid.titleLabelColor")),
        )


        when (status) {
            ChargeStatus.CAPTURED, ChargeStatus.AUTHORIZED, ChargeStatus.VALID, ChargeStatus.IN_PROGRESS -> {
                saveCardSwitchHolder?.view?.cardSwitch?.payButton?.setDisplayMetrics(
                    CustomUtils.getDeviceDisplayMetrics(
                        context as Activity
                    )
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
                        false,Color.YELLOW
                    )
                    tabAnimatedActionButton?.changeButtonState(ActionButtonState.LOADING)
                    saveCardSwitchHolder?.view?.cardSwitch?.payButton?.setInValidBackground(
                        false,Color.YELLOW
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
                        saveCardSwitchHolder?.view?.cardSwitch?.payButton?.setInValidBackground(false,
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
        Handler().postDelayed({
            checkOutActivity?.onBackPressed()

            if (::bottomSheetDialog.isInitialized)
                bottomSheetDialog.dismiss()
            _checkoutFragment.activity?.onBackPressed()

        }, 12000)
    }


    /**
     * handlePaymentSuccess handles the payment token obtained from GooglePay API
     * **/
    @RequiresApi(Build.VERSION_CODES.N)
    fun handlePaymentSuccess(paymentData: PaymentData) {
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
        saveCardSwitchHolder?.view?.cardSwitch?.switchesLayout?.visibility = View.GONE
        saveCardSwitchHolder?.view?.cardSwitch?.payButton?.visibility = View.VISIBLE
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

            val gson = Gson()
            val jsonToken = gson.fromJson(token, JsonObject::class.java)
            /**
             * At this stage, Passing the googlePaylaod to Tap Backend TokenAPI call followed by chargeAPI.
             * ***/
            /**
             * At this stage, Passing the googlePaylaod to Tap Backend TokenAPI call followed by chargeAPI.
             */
            val createTokenGPayRequest = CreateTokenGPayRequest("googlepay", jsonToken)
            CardViewModel().processEvent(
                CardViewEvent.CreateGoogleTokenEvent,
                this,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                context,
                createTokenGPayRequest
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

        println("incrementalCount val>>>>>"+incrementalCount)
        /*   if (card != null) {
               if(card.cardNumber!=null)
              handleScanSuccessResult(card)
              *//* Log.d("checkOutViewModel", "onRecognitionSuccess: " + card.cardNumber)
            Log.d("checkOutViewModel", "onRecognitionSuccess: " + card.expirationDate)
            Log.d("checkOutViewModel", "onRecognitionSuccess: " + card.cardHolder)*//*

        }*/
        if (card != null && incrementalCount==3) {

            if (card.cardNumber != null && card.cardHolder != null && card.expirationDate != null) {
                handleScanSuccessResult(card)
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
                loyaltyViewHolder.loyaltyView.linearLayout2?.visibility = View.VISIBLE
                loyaltyViewHolder.loyaltyView.linearLayout3?.visibility = View.VISIBLE

            } else {

                loyaltyViewHolder.loyaltyView.linearLayout2?.visibility = View.GONE
                loyaltyViewHolder.loyaltyView.linearLayout3?.visibility = View.GONE

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

    fun onButtonClicked() {
        println("onButtonClicked")
        BenefitInAppCheckout.newInstance(
            context as Activity,
            appId,
            "448544",
            merchantId,
            seceret,
            "20.0",
            "BH",
            "048",
            mcc,
            "Tap",
            "Manama",
            this
        )
    }


    override fun onTransactionSuccess(p0: Transaction?) {
        println("transaction is success $p0")

    }

    override fun onTransactionFail(p0: Transaction?) {
        println("onTransactionFail is  $p0")
    }

    fun dismissBottomSheet() {
        checkoutFragment?.dismissBottomSheetDialog()
    }




}














