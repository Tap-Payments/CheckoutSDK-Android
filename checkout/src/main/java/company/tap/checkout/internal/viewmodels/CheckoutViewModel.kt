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
import android.view.animation.AnimationUtils
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
import company.tap.checkout.internal.interfaces.*
import company.tap.checkout.internal.utils.AmountCalculator.calculateExtraFeesAmount
import company.tap.checkout.internal.utils.AnimationEngine
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
    TapTextRecognitionCallBack , TapScannerCallback {
    private var savedCardList = MutableLiveData<List<SavedCard>>()
    private var supportedLoyalCards = MutableLiveData<List<LoyaltySupportedCurrency>>()
    private var paymentOptionsList = MutableLiveData<List<PaymentOption>>()
    private var goPayCardList = MutableLiveData<List<GoPaySavedCards>>()

    private var allCurrencies = MutableLiveData<List<SupportedCurrencies>>()
    private var selectedItemsDel by Delegates.notNull<Int>()
    private val isShaking = MutableLiveData<Boolean>()
    private var deleteCard: Boolean = false
    private var displayItemsOpen: Boolean = false
    private var displayOtpIsOpen: Boolean = false
    private  var saveCardSwitchHolder: SwitchViewHolder?=null
    private lateinit var paymentInputViewHolder: PaymentInputViewHolder
    private lateinit var goPaySavedCardHolder: GoPaySavedCardHolder
    private lateinit var businessViewHolder: BusinessViewHolder
    private lateinit var amountViewHolder: AmountViewHolder
    private lateinit var currencyAdapter: CurrencyTypeAdapter
    private lateinit var goPayAdapter: GoPayCardAdapterUIKIT
    private lateinit var goPayViewsHolder: GoPayViewsHolder
    private lateinit var itemsViewHolder: ItemsViewHolder
    private lateinit var cardViewHolder: CardViewHolder
    private lateinit var asynchronousPaymentViewHolder: AsynchronousPaymentViewHolder
    private lateinit var loyaltyViewHolder: LoyaltyViewHolder

    private  var tabAnimatedActionButtonViewHolder: TabAnimatedActionButtonViewHolder?=null
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var fragmentManager: FragmentManager
    @SuppressLint("StaticFieldLeak")
    private lateinit var bottomSheetLayout: FrameLayout
    @Nullable
    internal lateinit var selectedAmount: String
    @Nullable
    private lateinit var selectedCurrency: String
    private var fee : BigDecimal?= BigDecimal.ZERO
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
    var currencyOldRate:BigDecimal?=null
    var currentCalculatedAmount:BigDecimal?=null
    var lastSelectedCurrency:String?=null
    var loyatFlag:Boolean?=false

    @JvmField
    var selectedTotalAmount : String? = null

    @JvmField
    var selectedAmountPos: BigDecimal? = null

    @JvmField
    var selectedCurrencyPos: String? = null

    @JvmField
    var binLookupResponse1: BINLookupResponse? = null
    lateinit var paymentOptionsWorker: java.util.ArrayList<PaymentOption>

    private val googlePayButton: View? = null



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
            checkoutFragment: CheckoutFragment,headerLayout: LinearLayout
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

        val aScene: Scene ?= Scene.getCurrentScene(sdkLayout)
        aScene?.setEnterAction {
            AnimationUtils.loadAnimation(context,R.anim.slide_down)
        }




        initializeScanner(this)
        initViewHolders()
        initAmountAction()
        initSwitchAction()
        initOtpActionButton()
        setAllSeparatorTheme()
        initLoyaltyView()

    }

    private fun initLoyaltyView() {
        if(loyatFlag== true){
            removeViews(saveCardSwitchHolder)
            addViews(loyaltyViewHolder,saveCardSwitchHolder)
            loyaltyViewHolder.view.loyaltyView.constraintLayout?.visibility = View.VISIBLE

        }else
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
                            paymentInputViewHolder,
                            cardViewHolder,
                            saveCardSwitchHolder,
                            otpViewHolder
                    )
                    addViews(
                            businessViewHolder,
                            amountViewHolder,
                            cardViewHolder,
                            paymentInputViewHolder,
                            saveCardSwitchHolder
                    )
                    saveCardSwitchHolder?.view?.mainSwitch?.switchSaveMobile?.visibility =
                        View.GONE
                    saveCardSwitchHolder?.view?.cardSwitch?.payButton?.setButtonDataSource(
                            false,
                            context.let { LocalizationManager.getLocale(it).language },
                            LocalizationManager.getValue("pay", "ActionButton"),
                            Color.parseColor(ThemeManager.getValue("actionButton.Invalid.backgroundColor")),
                            Color.parseColor(ThemeManager.getValue("actionButton.Invalid.titleLabelColor"))
                    )
                    saveCardSwitchHolder?.view?.cardSwitch?.payButton?.visibility = View.VISIBLE
                    saveCardSwitchHolder?.view?.cardSwitch?.payButton?.changeButtonState(
                            ActionButtonState.IDLE
                    )
                    paymentInputViewHolder.tapMobileInputView.clearNumber()

                }
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun confirmOTPCode(otpCode: String) {
        otpViewHolder.view.otpView.otpViewActionButton.setDisplayMetrics(CustomUtils.getDeviceDisplayMetrics(context as Activity))
        otpViewHolder.view.otpView.otpViewActionButton.changeButtonState(ActionButtonState.LOADING)
        when(PaymentDataSource.getTransactionMode())
        {
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
        businessViewHolder = BusinessViewHolder(context)
        amountViewHolder = AmountViewHolder(context, this)
        tabAnimatedActionButtonViewHolder = TabAnimatedActionButtonViewHolder(context)
        cardViewHolder = CardViewHolder(context, this)
        goPaySavedCardHolder = GoPaySavedCardHolder(context, this, this)
        saveCardSwitchHolder = SwitchViewHolder(context)
        paymentInputViewHolder = PaymentInputViewHolder(
                context,
                this,
                this,
                saveCardSwitchHolder,
                this,
                cardViewModel,checkoutFragment
        )

        itemsViewHolder = ItemsViewHolder(context, this)
        otpViewHolder = OTPViewHolder(context)
        otpViewHolder.otpView.visibility=View.GONE
        otpViewHolder.otpView.requestFocus()
        otpViewHolder.otpView.otpViewInput1.cursorColor = Color.parseColor(ThemeManager.getValue("TapOtpView.OtpController.activeBottomColor"))
        otpViewHolder.otpView.otpViewInput1.setLineColorInactive(Color.parseColor(ThemeManager.getValue("TapOtpView.OtpController.bottomLineColor")) )
        otpViewHolder.otpView.otpViewInput1.setLineColorActive(Color.parseColor(ThemeManager.getValue("TapOtpView.OtpController.activeBottomColor")) )
        otpViewHolder.otpView.otpViewInput1.isCursorVisible = true
        goPayViewsHolder = GoPayViewsHolder(context, this, otpViewHolder)
        asynchronousPaymentViewHolder = AsynchronousPaymentViewHolder(context, this)
        loyaltyViewHolder = LoyaltyViewHolder(context,this,this)
        // nfcViewHolder = NFCViewHolder(context as Activity, context, this, fragmentManager)
        logicForLoyaltyProgram()
    }

    private fun initSwitchAction() {
        saveCardSwitchHolder?.view?.mainSwitch?.mainSwitch?.visibility = View.VISIBLE
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
                        paymentInputViewHolder,
                        saveCardSwitchHolder
                )

            }
            TransactionMode.SAVE_CARD -> {
                addViews(
                        businessViewHolder,
                        paymentInputViewHolder,
                        saveCardSwitchHolder
                )
            }
            else -> {

                if(PaymentDataSource.getPaymentDataType()=="WEB"){
                    addViews(
                            businessViewHolder,
                            amountViewHolder,
                            cardViewHolder,
                            saveCardSwitchHolder
                    )
                }else
                addViews(
                        businessViewHolder,
                        amountViewHolder,
                        cardViewHolder,
                        paymentInputViewHolder,
                        saveCardSwitchHolder
                )
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
                    cardViewHolder, paymentInputViewHolder,
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
        if (this::bottomSheetLayout.isInitialized)
           // AnimationUtils.loadAnimation(bottomSheetLayout.context,R.anim.slide_down)
            AnimationEngine.applyTransition(bottomSheetLayout, AnimationEngine.Type.SLIDE , 1000)
    }

    override fun displayGoPay() {
        setSlideAnimation()
        saveCardSwitchHolder?.let {
            removeViews(
                    businessViewHolder,
                    amountViewHolder,
                    goPayViewsHolder,
                    cardViewHolder,
                    paymentInputViewHolder,
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
                    paymentInputViewHolder,
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

            } else{
                amountViewHolder.updateSelectedCurrency(
                        displayItemsOpen,
                        selectedAmount, selectedCurrency,
                        currentAmount, currentCurrency,currentCurrencySymbol
                )

            }
        }
        if(otpViewHolder.otpView.isVisible){
            removeViews(otpViewHolder)
            saveCardSwitchHolder?.view?.cardSwitch?.payButton?.changeButtonState(ActionButtonState.RESET)
            saveCardSwitchHolder?.view?.cardSwitch?.payButton?.isClickable = true
            saveCardSwitchHolder?.view?.cardSwitch?.payButton?.stateListAnimator=null
            saveCardSwitchHolder?.view?.cardSwitch?.payButton?.setButtonDataSource(
                    false,
                    context.let { LocalizationManager.getLocale(it).language },
                    LocalizationManager.getValue("pay", "ActionButton"),
                    Color.parseColor(ThemeManager.getValue("actionButton.Valid.paymentBackgroundColor")),
                    Color.parseColor(ThemeManager.getValue("actionButton.Valid.titleLabelColor"))
            )
//            saveCardSwitchHolder?.view?.cardSwitch?.payButton?.isActivated = false
        }
       if(PaymentDataSource.getPaymentDataType()!=null && PaymentDataSource.getPaymentDataType() == "WEB"){
            removeViews(paymentInputViewHolder)
        }
        removeInlineScanner()
        removeNFCViewFragment()
        setSlideAnimation()
    }


    private fun caseDisplayControlCurrency() {
        removeViews(
              //  businessViewHolder,
           // amountViewHolder,
                cardViewHolder,
                paymentInputViewHolder,
                saveCardSwitchHolder,
                goPayViewsHolder,
                otpViewHolder,
                itemsViewHolder
        )

       // removeAllViews()
        addViews(
            itemsViewHolder)



       // checkoutFragment?.isFullscreen =true

        /**
         * will be replaced by itemList coming from the API**/
        if(PaymentDataSource.getItems()!=null){
            itemsViewHolder.view.itemRecylerView.adapter = itemAdapter
            itemAdapter.updateAdapterData(itemList)
        }
        currencyAdapter.updateAdapterData(allCurrencies.value as List<SupportedCurrencies>)

        frameLayout.visibility = View.VISIBLE
        itemsViewHolder.itemsdisplayed = true
    }

    private fun caseNotDisplayControlCurrency() {
        if (goPayViewsHolder.goPayopened || itemsViewHolder.itemsdisplayed) setActionGoPayOpenedItemsDisplayed()
        else setActionNotGoPayOpenedNotItemsDisplayed()

    }

    private fun setActionGoPayOpenedItemsDisplayed() {
        removeViews(

                cardViewHolder,
                paymentInputViewHolder,
                saveCardSwitchHolder,
                goPayViewsHolder,
                otpViewHolder,
                itemsViewHolder
        )
        addViews(
            cardViewHolder,
                paymentInputViewHolder,
                saveCardSwitchHolder
        )
        //itemsViewHolder.resetView()
     //I comment   itemsViewHolder.setItemsRecylerView()
      //  itemsViewHolder?.view?.itemRecylerView?.adapter = itemAdapter
        frameLayout.visibility = View.GONE
    }

    private fun setActionNotGoPayOpenedNotItemsDisplayed() {
        saveCardSwitchHolder?.let {
            removeViews(

                    cardViewHolder,
                    paymentInputViewHolder,
                    it,
                    itemsViewHolder
            )
        }
        saveCardSwitchHolder?.let {
            addViews(

                    cardViewHolder,
                    paymentInputViewHolder,
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
        amountViewHolder.view.amount_section.itemCountButton.text =  LocalizationManager.getValue(
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
                paymentInputViewHolder, saveCardSwitchHolder, amountViewHolder
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
                paymentInputViewHolder, saveCardSwitchHolder, otpViewHolder, amountViewHolder
        )
       // bottomSheetDialog.dismissWithAnimation

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
                paymentInputViewHolder,
                cardViewHolder,
                saveCardSwitchHolder,
                otpViewHolder
        )
        addViews(
                businessViewHolder,
                amountViewHolder,
                cardViewHolder,
                paymentInputViewHolder,
                saveCardSwitchHolder,
                otpViewHolder
        )
        //Added check change listener to handle showing of extra save options
        saveCardSwitchHolder?.view?.mainSwitch?.switchSaveMobile?.visibility = View.VISIBLE
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


    override fun displayRedirect(url: String, authenticate: Charge?) {
        this.redirectURL = url


        if (::redirectURL.isInitialized && ::fragmentManager.isInitialized) {
            if (otpViewHolder.otpView.isVisible) {
                removeViews(
                       // businessViewHolder,

                        cardViewHolder,
                        paymentInputViewHolder,
                        otpViewHolder
                )
            }
            setSlideAnimation()
            businessViewHolder.view.headerView.constraint.visibility= View.GONE
            businessViewHolder.view.topSeparatorLinear.visibility= View.GONE
            checkoutFragment.closeText.visibility =  View.GONE

            // removeViews(amountViewHolder)
            val bottomSheet: FrameLayout? = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet)
            BottomSheetBehavior.from(bottomSheet as View).state = BottomSheetBehavior.STATE_EXPANDED
            Handler(Looper.getMainLooper()).postDelayed({
                fragmentManager.beginTransaction()
                        .replace(
                                R.id.webFrameLayout, WebFragment.newInstance(
                                redirectURL,
                                this, cardViewModel, authenticate
                        )
                        ).commitNow()
                checkoutFragment.closeText.visibility =  View.VISIBLE

            }, 1500)

        }
        saveCardSwitchHolder?.view?.visibility = View.VISIBLE
        webFrameLayout.visibility =View.VISIBLE
         removeViews(amountViewHolder,businessViewHolder)
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
        paymentInputViewHolder = PaymentInputViewHolder(
            context,
            this,
            this,
            saveCardSwitchHolder,
            this,
            cardViewModel,
            checkoutFragment
        )
      //  paymentInputViewHolder.tabLayout.setUnselectedAlphaLevel(1.0f)
        if (::paymentInputViewHolder.isInitialized)
            paymentInputViewHolder.setCurrentBinData(binLookupResponse)

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
                    currentCurrency = paymentOptionsResponse.supportedCurrencies[i].symbol.toString()
                    println("currentCurrency currency value>>" + currentCurrency)

                    if(currentCurrency.length ==2){
                        currentCurrency= paymentOptionsResponse.supportedCurrencies[i].currency.toString()
                    }else {
                        currentCurrency= paymentOptionsResponse.supportedCurrencies[i].symbol.toString()
                        currentCurrencySymbol =  paymentOptionsResponse.supportedCurrencies[i].symbol.toString()
                    }
                   currencyAdapter.updateSelectedPosition(i)
                }

            }
            amountViewHolder.setDataFromAPI(
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
         //  unModifiedItemList = itemList
            unModifiedItemList = itemList.map{it.copy()}
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
                    paymentInputViewHolder.selectedType
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

        PaymentDataSource.setSelectedCurrency(currentCurrency,null)
        PaymentDataSource.setSelectedAmount(currentAmount.toBigDecimal())

        if (::loyaltyViewHolder.isInitialized){
            val objectMapper = ObjectMapper()
            val tapLoyaltyModel: TapLoyaltyModel =
                objectMapper.readValue(context.resources.openRawResource(R.raw.loyalty), TapLoyaltyModel::class.java)
            supportedLoyalCards.value = tapLoyaltyModel.supportedCurrencies as List<LoyaltySupportedCurrency>
            println("tapLoyaltyModel>>>"+tapLoyaltyModel.bankLogo)
            tapLoyaltyModel.bankLogo?.let { tapLoyaltyModel.bankName?.let { it1 ->
                loyaltyViewHolder.setDataFromAPI(it,
                    it1,tapLoyaltyModel,supportedLoyalCards.value as List<LoyaltySupportedCurrency>
                )
            } }

        }
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
        if(PaymentDataSource.getItems()!=null){
    PaymentDataSource.getItems()?.let { itemAdapter.updateAdapterData(it) }
      }
        cardViewHolder.view.mainChipgroup.chipsRecycler.adapter = adapter
        // goPaySavedCardHolder.view.goPayLoginView.chipsRecycler.adapter = goPayAdapter
        cardViewHolder.view.mainChipgroup.groupAction?.visibility = View.VISIBLE
        cardViewHolder.view.mainChipgroup.groupAction?.setOnClickListener {
            setMainChipGroupActionListener()
            paymentInputViewHolder.clearCardInputAction()
            paymentInputViewHolder.onFocusChange("")
            CustomUtils.hideKeyboardFrom(context,paymentInputViewHolder.view)
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
        //paymentInputViewHolder.tabLayout.setOnTouchListener { v, _ ->
        touchHandlingForCardView()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("ClickableViewAccessibility")
    private fun touchHandlingForCardView() {
        saveCardSwitchHolder?.view?.setOnTouchListener { v, _ ->
            CustomUtils.hideKeyboardFrom(context, paymentInputViewHolder.view)
            paymentInputViewHolder.resetView = true
            paymentInputViewHolder.resetTouchView()
          //  paymentInputViewHolder.onFocusChange("")

            true
        }
        cardViewHolder.view.cardLinearLayout.setOnTouchListener { v, _ ->
            CustomUtils.hideKeyboardFrom(context, paymentInputViewHolder.view)
            paymentInputViewHolder.resetView = true
            paymentInputViewHolder.resetTouchView()

            true
        }
        amountViewHolder.view.setOnTouchListener { v, _ ->
            CustomUtils.hideKeyboardFrom(context, paymentInputViewHolder.view)
            paymentInputViewHolder.resetView = true
            paymentInputViewHolder.resetTouchView()

            true
        }
        paymentInputViewHolder.tapAlertView?.setOnTouchListener { v, _ ->
            CustomUtils.hideKeyboardFrom(context, paymentInputViewHolder.view)
            paymentInputViewHolder.resetView = true
            paymentInputViewHolder.resetTouchView()

            true
        }
        paymentInputViewHolder.tabLayout.setOnTouchListener { v, _ ->
            CustomUtils.hideKeyboardFrom(context, paymentInputViewHolder.view)
            paymentInputViewHolder.resetView = true
            paymentInputViewHolder.resetTouchView()

            true
        }
        businessViewHolder.view.setOnTouchListener { v, _ ->
            CustomUtils.hideKeyboardFrom(context, paymentInputViewHolder.view)
            paymentInputViewHolder.resetView = true
            paymentInputViewHolder.resetTouchView()

            true
        }
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
        if (delSelectedCard.deleted) {
            adapter.deleteSelectedCard(selectedItemsDel)
            adapter.updateShaking(false)
            deleteCard = false
            //saveCardSwitchHolder?.view?.cardSwitch?.payButton?.changeButtonState(ActionButtonState.SUCCESS)
        }
    }

    override fun handleSuccessFailureResponseButton(
        response: String,
        authenticate: Authenticate?,
        chargeResponse: Charge?,
        tabAnimatedActionButton: TabAnimatedActionButton?
    ) {
        SessionManager.setActiveSession(false)
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

        if(::otpViewHolder.isInitialized)
        if (otpViewHolder.otpView.isVisible) {
            removeViews(
                    businessViewHolder,
                    amountViewHolder,
                    cardViewHolder,
                    paymentInputViewHolder,
                    otpViewHolder
            )
        }
        if(saveCardSwitchHolder!=null){
            removeViews(businessViewHolder,amountViewHolder,saveCardSwitchHolder)
            addViews(businessViewHolder,saveCardSwitchHolder)
            businessViewHolder.view.headerView.constraint.visibility= View.GONE
            businessViewHolder.view.topSeparatorLinear.visibility= View.GONE
            saveCardSwitchHolder?.view?.visibility = View.VISIBLE
            saveCardSwitchHolder?.view?.mainSwitch?.visibility = View.GONE
        }


        when(chargeResponse?.status){
            ChargeStatus.CAPTURED, ChargeStatus.AUTHORIZED, ChargeStatus.VALID, ChargeStatus.IN_PROGRESS -> {
                saveCardSwitchHolder?.view?.cardSwitch?.payButton?.changeButtonState(
                        ActionButtonState.SUCCESS
                )
                tabAnimatedActionButton?.changeButtonState(ActionButtonState.SUCCESS)
            }
            ChargeStatus.CANCELLED, ChargeStatus.TIMEDOUT, ChargeStatus.FAILED, ChargeStatus.DECLINED, ChargeStatus.UNKNOWN,
            ChargeStatus.RESTRICTED, ChargeStatus.ABANDONED, ChargeStatus.VOID, ChargeStatus.INVALID -> {
                saveCardSwitchHolder?.view?.cardSwitch?.payButton?.changeButtonState(
                        ActionButtonState.ERROR
                )

                tabAnimatedActionButton?.changeButtonState(ActionButtonState.ERROR)
            }else->{

            if(response == "tokenized"){
                saveCardSwitchHolder?.view?.cardSwitch?.payButton?.changeButtonState(
                        ActionButtonState.SUCCESS
                )
            }else{
                saveCardSwitchHolder?.view?.cardSwitch?.payButton?.changeButtonState(
                        ActionButtonState.ERROR
                )
                saveCardSwitchHolder?.view?.cardSwitch?.payButton?.isEnabled =true
                saveCardSwitchHolder?.view?.cardSwitch?.payButton?.isClickable =true
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
            SDKSession.startSDK((tabAnimatedActionButton.context as AppCompatActivity).supportFragmentManager,tabAnimatedActionButton.context,tabAnimatedActionButton.context as AppCompatActivity)
        }
        Handler().postDelayed({
            if (::bottomSheetDialog.isInitialized)
                bottomSheetDialog.dismiss()

        }, 15000)


    }



    override fun displayAsynchronousPaymentView(chargeResponse: Charge) {
        if(chargeResponse!=null){
            saveCardSwitchHolder?.view?.cardSwitch?.payButton?.changeButtonState(ActionButtonState.LOADING)
            removeViews(
                    businessViewHolder,
                    amountViewHolder,
                    cardViewHolder,
                    paymentInputViewHolder,
                    saveCardSwitchHolder
            )
            businessViewHolder.setDataFromAPI(
                    selectedPaymentOption.image,
                    selectedPaymentOption.brand?.name
            )
            addViews(businessViewHolder, asynchronousPaymentViewHolder)
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
                if (::sdkLayout.isInitialized){
                    sdkLayout.removeView(it?.view)

                }

                if (::context.isInitialized) {
                    val animation = AnimationUtils.loadAnimation(context, R.anim.fade_out)
                    it?.view?.startAnimation(animation)
                }




            }, 0)

        }

    }


    private fun addViews(vararg viewHolders: TapBaseViewHolder?) {
        viewHolders.forEach {
            Handler(Looper.getMainLooper()).postDelayed(Runnable {

               // checkoutFragment?.scrollView?.post(Runnable { checkoutFragment?.scrollView?.scrollBy(0,sdkLayout.height) })
              //  val animati1on = AnimationUtils.loadAnimation(context, R.anim.slide_down)
                if (::context.isInitialized) {
                 //   val animation = AnimationUtils.loadAnimation(context, R.anim.slide_down)
                 //   it?.view?.startAnimation(animation)
                    val animati1on= AnimationUtils.loadAnimation(context, R.anim.fade_in)
                   // animati1on.duration = 500L
                    it?.view?.startAnimation(animati1on)
                   // AnimationEngine.applyTransition(it?.view as ViewGroup,AnimationEngine.Type.FADE_IN,500)

                }

                if (::sdkLayout.isInitialized){
                    sdkLayout.removeView(it?.view)
                    sdkLayout.addView(it?.view)
                }

                //   checkoutFragment?.scrollView?.fullScroll(View.FOCUS_DOWN)
               // bottomSheetDialog.behavior.setPeekHeight(sdkLayout.height)
              //  bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
                //bottomSheetDialog.behavior.saveFlags = BottomSheetBehavior.SAVE_FIT_TO_CONTENTS
              // checkoutFragment.scrollView?.fullScroll(View.FOCUS_DOWN)

               // AnimationEngine.applyTransition(sdkLayout,SLIDE,1500)

            }, 0)
        }

    }

    private fun unActivateActionButton() {
        val payButtonText: String

        when(PaymentDataSource.getTransactionMode()){
            TransactionMode.TOKENIZE_CARD -> payButtonText = LocalizationManager.getValue(
                    "pay",
                    "ActionButton"
            )
            TransactionMode.SAVE_CARD -> payButtonText = LocalizationManager.getValue(
                    "savecard",
                    "ActionButton"
            )
            else->payButtonText =LocalizationManager.getValue("pay", "ActionButton")
        }
        saveCardSwitchHolder?.view?.cardSwitch?.payButton?.setButtonDataSource(
                false,
                context.let { LocalizationManager.getLocale(it).language },
                payButtonText,
                Color.parseColor(ThemeManager.getValue("actionButton.Invalid.backgroundColor")),
                Color.parseColor(ThemeManager.getValue("actionButton.Invalid.titleLabelColor"))
        )
    
       // saveCardSwitchHolder?.view?.cardSwitch?.payButton?.changeButtonState(ActionButtonState.IDLE)
        saveCardSwitchHolder?.view?.cardSwitch?.payButton?.isActivated = false
    }


    fun resetCardSelection(){
        adapter.resetSelection()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCardSelectedAction(isSelected: Boolean, savedCardsModel: Any?) {

        /**
         * Clear card input text
         */

        if(paymentInputViewHolder.tapCardInputView.hasFocus()){
            paymentInputViewHolder.clearCardInputAction()
            paymentInputViewHolder.onFocusChange("")
            CustomUtils.hideKeyboardFrom(context,paymentInputViewHolder.view)
        }

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
        var payButtonText: String
        when(PaymentDataSource.getTransactionMode()){
            TransactionMode.TOKENIZE_CARD -> payButtonText = LocalizationManager.getValue(
                    "pay",
                    "ActionButton"
            )
            TransactionMode.SAVE_CARD -> payButtonText = LocalizationManager.getValue(
                    "savecard",
                    "ActionButton"
            )
            else->payButtonText =LocalizationManager.getValue("pay", "ActionButton")
        }
        saveCardSwitchHolder?.view?.cardSwitch?.payButton?.setButtonDataSource(
                true,
                context.let { LocalizationManager.getLocale(it).language },
                payButtonText,
                Color.parseColor(ThemeManager.getValue("actionButton.Valid.paymentBackgroundColor")),
                Color.parseColor(ThemeManager.getValue("actionButton.Valid.titleLabelColor"))
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

        removeViews(
            //    businessViewHolder,
            //    amountViewHolder,
                cardViewHolder,
                paymentInputViewHolder,
                tabAnimatedActionButtonViewHolder
        )


    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun onClickCardPayment() {
        cardViewModel.processEvent(
                CardViewEvent.CreateTokenEvent,
                this,
                null,
                null,
                null,
                paymentInputViewHolder.getCard(),
                null
        )
        setSlideAnimation()

        saveCardSwitchHolder?.view?.cardSwitch?.payButton?.changeButtonState(ActionButtonState.LOADING)
        saveCardSwitchHolder?.view?.mainSwitch?.visibility = View.GONE

        removeViews(
                businessViewHolder,
                amountViewHolder,
                cardViewHolder,
                paymentInputViewHolder,
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
            maskedCardNumber: String
    ) {
        this.cardId = cardId
        if (stopAnimation) {
            stopDeleteActionAnimation(itemId, maskedCardNumber)
        } else {
            cardViewHolder.view.mainChipgroup.groupAction?.text = LocalizationManager.getValue(
                    "close",
                    "Common"
            )
            deleteCard = false
        }

    }

    private fun stopDeleteActionAnimation(itemId: Int, maskedCardNumber: String) {
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
        if (isCompleted) {
            businessViewHolder.view?.headerView.constraint.visibility = View.VISIBLE
            saveCardSwitchHolder?.view?.mainSwitch?.visibility = View.VISIBLE
            saveCardSwitchHolder?.view?.mainSwitch?.switchSaveMobile?.visibility = View.VISIBLE
            saveCardSwitchHolder?.setSwitchToggleData(paymentType)
            loyaltyViewHolder.view.loyaltyView?.constraintLayout?.visibility = View.VISIBLE
            loyatFlag = true
            initLoyaltyView()
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
            cvvNumber: String
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
                paymentInputViewHolder,
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
        val bottomSheet: FrameLayout? = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet)
        BottomSheetBehavior.from(bottomSheet as View).state = BottomSheetBehavior.STATE_EXPANDED
        checkSelectedAmountInitiated()
    }


    // Override function to open card Scanner and scan the card.
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onClickCardScanner(scannerClicked :Boolean) {
       // setSlideAnimation()
        removeViews(
               //businessViewHolder,
              //  amountViewHolder,
                cardViewHolder,
                saveCardSwitchHolder,
                paymentInputViewHolder,
                otpViewHolder,
                goPaySavedCardHolder,
                goPayViewsHolder
        )
        amountViewHolder.readyToScanVisibility(scannerClicked)
       // addViews(businessViewHolder, amountViewHolder)

        FrameManager.getInstance().frameColor = Color.WHITE
        // Use
      //  val bottomSheet: FrameLayout? = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet)
      //  BottomSheetBehavior.from(bottomSheet as View).state = BottomSheetBehavior.STATE_EXPANDED
     //   bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        fragmentManager
            .beginTransaction()
            .replace(R.id.inline_container,inlineCamerFragment)
            .commit()

        isInlineOpened = true
        inLineCardLayout.visibility = View.VISIBLE
        amountViewHolder.changeGroupAction(false)

        checkSelectedAmountInitiated()
    }

    private fun checkSelectedAmountInitiated() {
        if (this::selectedAmount.isInitialized && this::selectedCurrency.isInitialized) {
            amountViewHolder.updateSelectedCurrency(
                    displayItemsOpen,
                    selectedAmount, selectedCurrency,
                    currentAmount, currentCurrency,currentCurrencySymbol
            )
        }
    }


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCurrencyClicked(currencySelected: String, currencyRate: BigDecimal, totalSelectedAmount: BigDecimal, previousSelectedCurrency: String,selectedCurrencySymbol:String) {
        currencyOldRate = currencyRate
        lastSelectedCurrency = previousSelectedCurrency
        if(::unModifiedItemList.isInitialized)
        println("unModifiedItemList"+unModifiedItemList)
        if (::itemList.isInitialized) {
            for (i in itemList.indices) {
                itemList[i].amount = unModifiedItemList[i].amount?.times(currencyRate)
                //itemList[i].totalAmount = currencyOldRate?.div(currencyRate)
                itemList[i].totalAmount =  unModifiedItemList[i].getPlainAmount()?.times(currencyRate)

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
        println("selectedCurrencySymbol final>>" + selectedCurrencySymbol.length)
        if(selectedCurrencySymbol.length == 2){
            amountViewHolder.updateSelectedCurrency(
                displayItemsOpen,
                selectedAmount, selectedCurrency,
                currentAmount, currentCurrency
            )
            PaymentDataSource.setSelectedCurrency(selectedCurrency = selectedCurrency,null)

        }else{
            amountViewHolder.updateSelectedCurrency(
                displayItemsOpen,
                selectedAmount, selectedCurrency,
                currentAmount, currentCurrency ,selectedCurrencySymbol
            )
            PaymentDataSource.setSelectedCurrency(selectedCurrency, selectedCurrencySymbol)

        }
        currentCurrencySymbol = selectedCurrencySymbol


           // PaymentDataSource.setSelectedCurrency(selectedCurrency = selectedCurrency)
            PaymentDataSource.setSelectedAmount(currencyRate)
    if(paymentInputViewHolder.tapCardInputView.isNotEmpty()){
        paymentInputViewHolder.tapCardInputView.clear()
        paymentInputViewHolder.tapAlertView?.visibility = View.GONE
        paymentInputViewHolder.tabLayout.resetBehaviour()
    }

        adapter.resetSelection()

    if(::selectedCurrency.isInitialized){
                filterViewModels(selectedCurrency)
        }else   filterViewModels(currentCurrency)

    }

    @SuppressLint("ResourceType")
    override fun redirectLoadingFinished(
            done: Boolean,
            chargeResponse: Charge?,
            contextSDK: Context?
    ) {
        println("done val" + done + "chargeResponse status" + chargeResponse?.status)
        println("saveCardSwitchHolder val" + saveCardSwitchHolder)
        if (::webFrameLayout.isInitialized) {
            if (fragmentManager.findFragmentById(R.id.webFrameLayout) != null)
                fragmentManager.beginTransaction()
                    .hide(fragmentManager.findFragmentById(R.id.webFrameLayout)!!)
                    .commit()
            webFrameLayout.visibility = View.GONE

        }

       if (::bottomSheetDialog.isInitialized)
                    bottomSheetDialog.dismiss()

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
        var paymentOption:PaymentOption? = null


        if(paymentTypeEnum == PaymentType.WEB ){
           savedCardsModel as PaymentOption
           extraFees = savedCardsModel.extraFees
            fee = calculateExtraFeesAmount(savedCardsModel as PaymentOption, amountedCurrency)

       } else{
           for (i in paymentOptionsResponse.paymentOptions.indices) {
               if (paymentOptionsResponse.paymentOptions[i].paymentType == paymentTypeEnum) {
                   extraFees = paymentOptionsResponse.paymentOptions[i].extraFees
                   paymentOption = paymentOptionsResponse.paymentOptions[i]
               }
           }
           fee = calculateExtraFeesAmount(paymentOption, amountedCurrency)

       }
        val totalAmount :String
        if(selectedTotalAmount!=null){
           totalAmount  = CurrencyFormatter.currencyFormat(fee?.add(
                     selectedTotalAmount?.toDouble()?.let { BigDecimal.valueOf(it) }).toString())
        }else {
            totalAmount  = CurrencyFormatter.currencyFormat(fee?.add(
                    amountedCurrency?.amount?.toDouble()?.let { BigDecimal.valueOf(it) }).toString())
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
        saveCardSwitchHolder?.view?.cardSwitch?.payButton?.changeButtonState(
                ActionButtonState.LOADING
        )
        saveCardSwitchHolder?.view?.cardSwitch?.payButton?.getImageView(
                R.drawable.loader,
                1
        ) {

            startSavedCardPaymentProcess(savedCardsModel as SavedCard)
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
        if (::businessViewHolder.isInitialized && ::amountViewHolder.isInitialized && ::cardViewHolder.isInitialized && ::paymentInputViewHolder.isInitialized &&
            ::goPayViewsHolder.isInitialized && ::otpViewHolder.isInitialized
        )
            removeViews(
                    businessViewHolder,
                    amountViewHolder,
                    cardViewHolder,
                    paymentInputViewHolder,
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
        amountViewHolder.view.separator.setTheme(separatorViewTheme)
        cardViewHolder.view.tapSeparatorViewLinear1.separator_1.setTheme(separatorViewTheme)
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
        if (card != null) {
            if (card.cardNumber != null && card.cardHolder != null && card.expirationDate != null) {
               onReadSuccess(card)
            }
        }


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
            inLineCardLayout.visibility = View.GONE
            amountViewHolder.readyToScanVisibility(false)

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
                paymentInputViewHolder,
                saveCardSwitchHolder
        )

    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun handleScanSuccessResult(card: TapCard) {
        removeInlineScanner()
      //  removeViews(amountViewHolder, businessViewHolder)
        addViews(
               // businessViewHolder,
               /// amountViewHolder,
                cardViewHolder,
                paymentInputViewHolder,
                saveCardSwitchHolder
        )

        if(card!=null && card.cardNumber?.trim()!=null && card.cardNumber.trim().length==6){
            callBinLookupApi(card.cardNumber.trim().substring(0, 6))
        }


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


        paymentInputViewHolder.tapCardInputView.setCardHolderName(card.cardHolder)
       // inlineCamerFragment.onDestroy()


    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun setScannedCardDetails(card: TapCard) {
        println("scanned card holder is${card.cardHolder}")
        println("scanned card number is${card.cardNumber}")
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
                paymentInputViewHolder,
                saveCardSwitchHolder
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
        convertDateString(emvCard)


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
        println("dateparts"+dateParts?.length)
        if (dateParts?.contains("/") == true) {
            if (dateParts.length <= 3) {
                return
            } else {
                if (dateParts.length >= 5 || dateParts.length >= 4) {
                    val month = (dateParts).substring(0, 1).toInt()

                     val year = (dateParts).substring(2, 4)

                  if(year.contains("/")){
                      println("retuu>>"+year)
                      return
                  }else{
                      paymentInputViewHolder.tapCardInputView.setExpiryDate(month, year.toInt())

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
        val hasWebPaymentOptions = webPaymentOptions.size > 0
        val hasCardPaymentOptions = cardPaymentOptions.size > 0
        val hasGooglePaymentOptions = googlePaymentOptions.size > 0
        if(hasGooglePaymentOptions){
            adapter.updateAdapterGooglePay(googlePaymentOptions)
            PaymentDataSource.setGoogleCardPay(googlePaymentOptions)
        }
        logicToHandlePaymentDataType(webPaymentOptions, cardPaymentOptions )

    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun logicToHandlePaymentDataType(
        webPaymentOptions: ArrayList<PaymentOption>,
        cardPaymentOptions: ArrayList<PaymentOption>

    ) {
       // println("webPaymentOptions in logic >>>>$webPaymentOptions")
      //  println("cardPaymentOptions in logic >>>>$cardPaymentOptions")

         if(PaymentDataSource.getPaymentDataType()!=null && PaymentDataSource.getPaymentDataType() == "WEB" && PaymentDataSource.getTransactionMode() !=TransactionMode.AUTHORIZE_CAPTURE ){
            adapter.updateAdapterDataSavedCard(ArrayList())
            adapter.updateAdapterData(webPaymentOptions)
            saveCardSwitchHolder?.view?.cardSwitch?.showOnlyPayButton()
        }else if(PaymentDataSource.getPaymentDataType() !=null && PaymentDataSource.getPaymentDataType() == "CARD"){
            adapter.updateAdapterData(ArrayList())
            paymentInputViewHolder.setDataFromAPI(cardPaymentOptions)
            saveCardSwitchHolder?.view?.cardSwitch?.showOnlyPayButton()
        }else{
            /**
             * If TransactionMode is AUTHORIZE_CAPTURE then don't show webpayment options
             * **/
             if(PaymentDataSource.getTransactionMode() ==TransactionMode.AUTHORIZE_CAPTURE){
                 adapter.updateAdapterData(ArrayList())
                 paymentInputViewHolder.setDataFromAPI(cardPaymentOptions)
             }else{
                adapter.updateAdapterData(webPaymentOptions)
                 if(!cardPaymentOptions.isEmpty()){
                     paymentInputViewHolder.setDataFromAPI(cardPaymentOptions)

                 }else {
                     saveCardSwitchHolder?.mainTextSave?.visibility = View.GONE
                     removeViews(paymentInputViewHolder)
                 }

             }
        }
    }

    var title :String= LocalizationManager.getValue("extraFeesAlertTitle", "ExtraFees")

    @RequiresApi(Build.VERSION_CODES.N)
    private fun showExtraFees(
            totalAmount: String,
            extraFeesAmount: String,
            paymentType: PaymentType, savedCardsModel: Any?, selectedCurrency: String
    ) {
        val extraFeesPart1:String = LocalizationManager.getValue(
                "extraFeesAlertMessagePart1",
                "ExtraFees"
        )
        val extraFeesPart2:String = LocalizationManager.getValue(
                "extraFeesAlertMessagePart2",
                "ExtraFees"
        )
       // val leftToRight = "\u200F"
        val localizedMessage =
           // extraFeesPart1 +" "+extraFeesAmount+PaymentDataProvider().getSelectedCurrency()?.currency +extraFeesPart2+" "+ totalAmount+ PaymentDataProvider().getSelectedCurrency()?.currency
            "$extraFeesPart1 $extraFeesAmount$selectedCurrency$extraFeesPart2 $totalAmount$selectedCurrency"
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
            saveCardSwitchHolder?.view?.cardSwitch?.payButton?.changeButtonState(ActionButtonState.LOADING)
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
    open fun calculateExtraFeesAmount(
            paymentOption: PaymentOption?,
            amountedCurrency: AmountedCurrency?
    ): BigDecimal? {
        return if (paymentOption != null) {

            // val amount = PaymentDataProvider().getSelectedCurrency()
            var extraFees: java.util.ArrayList<ExtraFee>? = paymentOption.extraFees
            if (extraFees == null) extraFees = java.util.ArrayList()
            val supportedCurrencies: java.util.ArrayList<SupportedCurrencies>? = PaymentDataProvider().getSupportedCurrencies()
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
    fun closeAsynchView(){
        removeViews(businessViewHolder, asynchronousPaymentViewHolder)
        businessViewHolder.setDataFromAPI(
                PaymentDataSource.getMerchantData()?.logo,
                PaymentDataSource.getMerchantData()?.name
        )
        addViews(
                businessViewHolder,
                amountViewHolder,
                cardViewHolder,
                paymentInputViewHolder,
                saveCardSwitchHolder
        )
        saveCardSwitchHolder?.view?.visibility= View.VISIBLE
        saveCardSwitchHolder?.view?.cardSwitch?.payButton?.changeButtonState(ActionButtonState.RESET)
        saveCardSwitchHolder?.view?.cardSwitch?.payButton?.setButtonDataSource(
                true,
                context.let { LocalizationManager.getLocale(it).language },
                LocalizationManager.getValue("pay", "ActionButton"),
                Color.parseColor(ThemeManager.getValue("actionButton.Valid.paymentBackgroundColor")),
                Color.parseColor(ThemeManager.getValue("actionButton.Valid.titleLabelColor"))
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
        saveCardSwitchHolder?.view?.cardSwitch?.payButton?.setButtonDataSource(
                true,
                context.let { LocalizationManager.getLocale(it).language },
                LocalizationManager.getValue("pay", "ActionButton"),
                Color.parseColor(ThemeManager.getValue("actionButton.Valid.paymentBackgroundColor")),
                Color.parseColor(ThemeManager.getValue("actionButton.Valid.titleLabelColor"))
        )


        when(status){
            ChargeStatus.CAPTURED, ChargeStatus.AUTHORIZED, ChargeStatus.VALID, ChargeStatus.IN_PROGRESS -> {
                saveCardSwitchHolder?.view?.cardSwitch?.payButton?.setDisplayMetrics(CustomUtils.getDeviceDisplayMetrics(context as Activity))
                Handler().postDelayed({
                    saveCardSwitchHolder?.view?.cardSwitch?.payButton?.changeButtonState(
                            ActionButtonState.SUCCESS
                    )
                }, 500)
            }
            ChargeStatus.CANCELLED, ChargeStatus.TIMEDOUT, ChargeStatus.FAILED, ChargeStatus.DECLINED, ChargeStatus.UNKNOWN,
            ChargeStatus.RESTRICTED, ChargeStatus.ABANDONED, ChargeStatus.VOID, ChargeStatus.INVALID -> {
                Handler().postDelayed({
                    saveCardSwitchHolder?.view?.cardSwitch?.payButton?.changeButtonState(
                            ActionButtonState.ERROR
                    )
                }, 500)
                tabAnimatedActionButton?.changeButtonState(ActionButtonState.ERROR)
            }else->{

            if(status.equals("tokenized")){
                Handler().postDelayed({
                    saveCardSwitchHolder?.view?.cardSwitch?.payButton?.changeButtonState(
                            ActionButtonState.SUCCESS
                    )
                }, 500)

            }else{
                Handler().postDelayed({
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

      }, 8000)
    }


    /**
     * handlePaymentSuccess handles the payment token obtained from GooglePay API
     * **/
    @RequiresApi(Build.VERSION_CODES.N)
    fun handlePaymentSuccess(paymentData: PaymentData) {
        val paymentInformation = paymentData.toJson() ?: return

        try {
            // Token will be null if PaymentDataRequest was not constructed using fromJson(String).
            val paymentMethodData = JSONObject(paymentInformation).getJSONObject("paymentMethodData")

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
            CardViewModel().processEvent(CardViewEvent.CreateGoogleTokenEvent,this,null,null,null,null,null,null,null,null,null,null,context,createTokenGPayRequest)
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
        if (card != null) {
            if(card.cardNumber!=null)
           handleScanSuccessResult(card)
           /* Log.d("checkOutViewModel", "onRecognitionSuccess: " + card.cardNumber)
            Log.d("checkOutViewModel", "onRecognitionSuccess: " + card.expirationDate)
            Log.d("checkOutViewModel", "onRecognitionSuccess: " + card.cardHolder)*/

        }
    }

    override fun onReadFailure(error: String?) {
        handleScanFailedResult(error)
    }

    private fun logicForLoyaltyProgram(){

        loyaltyViewHolder.loyaltyView.switchLoyalty?.setOnCheckedChangeListener { buttonView, isChecked ->
            loyaltyViewHolder.loyaltyView.switchTheme()

            if(isChecked){
                loyaltyViewHolder.loyaltyView.linearLayout2?.visibility = View.VISIBLE
                loyaltyViewHolder.loyaltyView.linearLayout3?.visibility = View.VISIBLE

            }else {

                loyaltyViewHolder.loyaltyView.linearLayout2?.visibility = View.GONE
                loyaltyViewHolder.loyaltyView.linearLayout3?.visibility = View.GONE

            }
        }

        loyaltyViewHolder.loyaltyView.setLoyaltyHeaderDataSource(LoyaltyHeaderDataSource("ADCB","https://is4-ssl.mzstatic.com/image/thumb/Purple112/v4/05/33/67/05336718-a6f6-8ca1-1ea0-0644f5071ce9/AppIcon-0-0-1x_U007emarketing-0-0-0-5-0-0-sRGB-0-0-0-GLES2_U002c0-512MB-85-220-0-0.png/1200x600wa.png"))

        loyaltyViewHolder.loyaltyView.textViewClickable?.makeLinks(
            Pair("(T&C)", View.OnClickListener {
                val url = "https://www.adcb.com/en/tools-resources/adcb-privacy-policy/"
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(url)
               context.startActivity(intent)

            }))
    }

}














