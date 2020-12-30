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
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
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
import company.tap.checkout.internal.dummygener.*
import company.tap.checkout.internal.enums.PaymentTypeEnum
import company.tap.checkout.internal.enums.SectionType
import company.tap.checkout.internal.interfaces.*
import company.tap.checkout.internal.utils.AnimationEngine
import company.tap.checkout.internal.utils.AnimationEngine.Type.SLIDE
import company.tap.checkout.internal.utils.CurrencyFormatter
import company.tap.checkout.internal.utils.CustomUtils
import company.tap.checkout.internal.viewholders.*
import company.tap.checkout.internal.webview.WebFragment
import company.tap.checkout.internal.webview.WebViewContract
import company.tap.nfcreader.open.reader.TapEmvCard
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapuilibrary.themekit.ThemeManager
import company.tap.tapuilibrary.themekit.theme.SeparatorViewTheme
import company.tap.tapuilibrary.uikit.enums.ActionButtonState
import company.tap.tapuilibrary.uikit.enums.GoPayLoginMethod
import company.tap.tapuilibrary.uikit.fragment.NFCFragment
import company.tap.tapuilibrary.uikit.views.TapNFCView
import kotlinx.android.synthetic.main.amountview_layout.view.*
import kotlinx.android.synthetic.main.businessview_layout.view.*
import kotlinx.android.synthetic.main.cardviewholder_layout1.view.*
import kotlinx.android.synthetic.main.fragment_checkouttaps.view.*
import kotlinx.android.synthetic.main.gopaysavedcard_layout.view.*
import kotlinx.android.synthetic.main.switch_layout.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.properties.Delegates


/**
 *
 * Copyright © 2020 Tap Payments. All rights reserved.
 *
 */
class TapLayoutViewModel : ViewModel(), BaseLayouttManager, OnCardSelectedActionListener,
    PaymentCardComplete, onCardNFCCallListener, OnCurrencyChangedActionListener, WebViewContract,
    TapTextRecognitionCallBack {
    private var savedCardList = MutableLiveData<List<SavedCards>>()
    private var goPayCardList = MutableLiveData<List<GoPaySavedCards>>()
    private var allCurrencies = MutableLiveData<List<Currencies1>>()
    private var selectedItemsDel by Delegates.notNull<Int>()
    private val isShaking = MutableLiveData<Boolean>()
    private var deleteCard: Boolean=false
    private var displayItemsOpen: Boolean = false
    private var saveCardSwitchHolder11: SwitchViewHolder11 ? = null

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
    private lateinit var currentCurrency: String
    private lateinit var currentAmount: String
    private lateinit var adapter: CardTypeAdapterUIKIT
    private lateinit var otpViewHolder: OTPViewHolder
    private lateinit var webFrameLayout: FrameLayout
    private lateinit var frameLayout: FrameLayout
    private lateinit var inLineCardLayout: FrameLayout
    private lateinit var sdkLayout: LinearLayout
    private lateinit var itemList: List<Items1>
    private lateinit var orderList: Order1
    private lateinit var context: Context
    private lateinit var otpTypeString: PaymentTypeEnum
    private lateinit var paymentActionType: PaymentTypeEnum
    private val nfcFragment = NFCFragment()
    private val inlineViewFragment = InlineViewFragment()
    private var isInlineOpened = false
    private var textRecognitionML: TapTextRecognitionML? = null
    private lateinit var intent:Intent


    private lateinit var  inlineViewCallback: InlineViewCallback


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
        intent: Intent) {
        this.context = context
        this.fragmentManager = fragmentManager
        this.sdkLayout = sdkLayout
        this.frameLayout = frameLayout
        this.webFrameLayout = webFrameLayout
        this.bottomSheetDialog = bottomSheetDialog
        this.inLineCardLayout = inLineCardLayout
        this.inlineViewCallback = inlineViewCallback
        this.intent = intent


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
         when(otpTypeString) {
             PaymentTypeEnum.GOPAY -> goPayViewsHolder.onOtpButtonConfirmationClick(otpViewHolder.otpView.otpViewInput1.text.toString() + otpViewHolder.otpView.otpViewInput2.text.toString())
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
                 saveCardSwitchHolder11?.view?.mainSwitch?.switchSaveMobile?.visibility = View.GONE
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

    @RequiresApi(Build.VERSION_CODES.N)
    fun initViewHolders(){
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
            this
        )
        itemsViewHolder1 = ItemsViewHolder1(context, this)
        paymentInputViewHolder = PaymenttInputViewHolder(
            context,
            this,
            this,
            saveCardSwitchHolder11,
            this
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
        amountViewHolder1.setOnItemsClickListener{}
        amountViewHolder1.view.amount_section.mainKDAmountValue.visibility= View.GONE

    }


    override fun displayStartupLayout(enabledSections: ArrayList<SectionType>) {
        //Todo based on api response logic for swicth case
        addViews(
            businessViewHolder,
            amountViewHolder1,
            cardViewHolder11,
            paymentInputViewHolder,
            saveCardSwitchHolder11
        )
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
        goPayAdapter.updateAdapterData(goPayCardList.value as List<GoPaySavedCards>)
        amountViewHolder1.changeGroupAction(false)
        goPayViewsHolder.goPayopened = true
    }

  private  fun setSlideAnimation(){
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
        if (display) {
//            removeAllViews()
            removeViews(
                businessViewHolder,
                amountViewHolder1,
                cardViewHolder11,
                paymentInputViewHolder,
                saveCardSwitchHolder11,
                goPayViewsHolder,
                otpViewHolder,
                itemsViewHolder1,

                )
            addViews(businessViewHolder, amountViewHolder1, itemsViewHolder1)
            itemsViewHolder1.setItemsRecylerView()
            itemsViewHolder1.setCurrencyRecylerView()
            frameLayout.visibility = View.VISIBLE
            itemsViewHolder1.itemsdisplayed = true
        } else {
            if (goPayViewsHolder.goPayopened || itemsViewHolder1.itemsdisplayed) {
                removeViews(
                    businessViewHolder,
                    amountViewHolder1,
                    cardViewHolder11,
                    paymentInputViewHolder,
                    saveCardSwitchHolder11,
                    goPayViewsHolder,
                    otpViewHolder,
                    itemsViewHolder1,

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

            } else {


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
                itemsViewHolder1.setItemsRecylerView()
              //  itemsViewHolder1.setCurrencyRecylerView()
                frameLayout.visibility = View.GONE


            }
        }
        displayItemsOpen = !display
        amountViewHolder1.changeGroupAction(!display)
        if (this::currentAmount.isInitialized)
            amountViewHolder1.updateSelectedCurrency(
                displayItemsOpen,
                selectedAmount, selectedCurrency,
                currentAmount, currentCurrency
            )
        removeInlineScanner()
    }

    @SuppressLint("SetTextI18n")
    override fun displayOTPView(mobileNumber: String, otpType: String) {
        setSlideAnimation()
        if(otpType== PaymentTypeEnum.GOPAY.name)
            displayOtpGoPay(mobileNumber)
            else if(otpType == PaymentTypeEnum.telecom.name) displayOtpTelecoms(mobileNumber)
    }

    private fun displayOtpGoPay(mobileNumber: String){
        otpTypeString = PaymentTypeEnum.GOPAY
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

    private fun displayOtpTelecoms(mobileNumber: String){
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
            if(isChecked) saveCardSwitchHolder11?.view?.cardSwitch?.switchesLayout?.visibility = View.GONE
        }
        saveCardSwitchHolder11?.setSwitchToggleData(PaymentTypeEnum.telecom)
        otpViewHolder.setMobileOtpView()
        var replaced = ""
        var countryCodeReplaced = ""
        if (mobileNumber.length > 7)
            replaced = (mobileNumber.toString()).replaceRange(1, 6, "....")
        countryCodeReplaced = goPayViewsHolder.goPayLoginInput.countryCodePicker.selectedCountryCode.replace(
            "+",
            " "
        )

        otpViewHolder.otpView.mobileNumberTextNormalPay.text = "+$countryCodeReplaced$replaced"
    }


    override fun displayRedirect(url: String) {
        Toast.makeText(context, "url redirecting" + url, Toast.LENGTH_SHORT).show()
    }

    override fun displaySaveCardOptions() {}


    @RequiresApi(Build.VERSION_CODES.N)
    override fun getDatafromAPI(dummyInitapiResponse1: JsonResponseDummy1) {
        println("dummyAPIS response new value is ${dummyInitapiResponse1}")
        businessViewHolder.setDatafromAPI(
            dummyInitapiResponse1.merchant1.logo,
            dummyInitapiResponse1.merchant1.name
        )
        amountViewHolder1.setDatafromAPI(
            dummyInitapiResponse1.order1.original_amount,
            dummyInitapiResponse1.order1.trx_currency,
            dummyInitapiResponse1.order1.items.size.toString()
        )

        cardViewHolder11.setDatafromAPI(dummyInitapiResponse1.savedCards as MutableList<SavedCards>)
       // goPaySavedCardHolder.setDatafromAPI(dummyInitapiResponse1.goPaySavedCards)
        // println("dummy tapCardPhoneListDataSource" + dummyInitapiResponse1.tapCardPhoneListDataSources)
        paymentInputViewHolder.setDatafromAPI(dummyInitapiResponse1.tapCardPhoneListDataSource)


        saveCardSwitchHolder11?.setDatafromAPI(
            dummyInitapiResponse1.merchant1.name,
            paymentInputViewHolder.selectedType
        )
        itemsViewHolder1.setDatafromAPI(
            dummyInitapiResponse1.currencies1 as ArrayList<Currencies1>,
            dummyInitapiResponse1.order1.items
        )

        allCurrencies.value = dummyInitapiResponse1.currencies1
        itemList = dummyInitapiResponse1.order1.items
        savedCardList.value = dummyInitapiResponse1.savedCards
        goPayCardList.value = dummyInitapiResponse1.goPaySavedCards
        orderList = dummyInitapiResponse1.order1

        /**
         * Setting divider for items
         */
        val divider = DividerItemDecoration(
            context,
            DividerItemDecoration.HORIZONTAL
        )
        divider.setDrawable(ShapeDrawable().apply {
            intrinsicWidth = 10
            paint.color = Color.TRANSPARENT
        }) // note: currently (support version 28.0.0), we can not use tranparent color here, if we use transparent, we still see a small divider line. So if we want to display transparent space, we can set color = background color or we can create a custom ItemDecoration instead of DividerItemDecoration.
        cardViewHolder11.view.mainChipgroup.chipsRecycler.addItemDecoration(divider)

        adapter = CardTypeAdapterUIKIT(this)
        goPayAdapter = GoPayCardAdapterUIKIT(this)
        cardViewHolder11.view.mainChipgroup.chipsRecycler.adapter = adapter
        adapter.updateAdapterData(savedCardList.value as List<SavedCards>)
        goPayAdapter.updateAdapterData(goPayCardList.value as List<GoPaySavedCards>)
        goPaySavedCardHolder.view.goPayLoginView.chipsRecycler.adapter = goPayAdapter
        currencyAdapter = CurrencyTypeAdapter(this)
//        currencyAdapter.initOnCurrencyChangedActionListener(this)
           currencyAdapter.updateAdapterData(allCurrencies.value as List<Currencies1>)
        cardViewHolder11.view.mainChipgroup.groupAction?.visibility = View.VISIBLE
        cardViewHolder11.view.mainChipgroup.groupAction?.setOnClickListener {
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

        goPaySavedCardHolder.view.goPayLoginView.groupAction.setOnClickListener {
            if (goPaySavedCardHolder.view.goPayLoginView.groupAction?.text == LocalizationManager.getValue(
                    "close",
                    "Common"
                )) {
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
    }


    @SuppressLint("WrongConstant")
    override fun didDialogueExecute(response: String) {
        println("response are$response")
        if (response == "YES") {
            if (deleteCard) {
                adapter.deleteSelectedCard(selectedItemsDel)
                adapter.updateShaking(false)
                deleteCard = false

            } else {
                removeViews(goPaySavedCardHolder)
                adapter.updateAdapterData(savedCardList.value as List<SavedCards>)
                goPayViewsHolder.goPayopened = false
                adapter.goPayOpenedfromMain(true)
                cardViewHolder11.view.mainChipgroup.groupAction.visibility = View.VISIBLE
            }
        } else if (response == "NO") {
            adapter.updateShaking(false)
            deleteCard = false

        } else if (response == "OK"){

            bottomSheetDialog.dismissWithAnimation
            bottomSheetDialog.dismiss()
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
                sdkLayout.addView(it?.view)
//                  val animation = AnimationUtils.loadAnimation(context, R.anim.fade_in)
//                   it.view.startAnimation(animation)
            }, 0)
            //   sdkLayout.addView(it.view)

        }
    }


    private fun unActivateActionButton() {
     //     tabAnimatedActionButtonViewHolder11.bindViewComponents()
//          tabAnimatedActionButtonViewHolder11.view.actionButton.isClickable = false

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


    override fun onCardSelectedAction(isSelected: Boolean, savedCardsModel: Any) {
        when ((savedCardsModel as SavedCards).chipType) {
            5 -> {
                // do action for saved card
                activateActionButton()
                setPayButtonAction(PaymentTypeEnum.SAVEDCARD)

            }
            1 -> {
                // redirect
                activateActionButton()
                setPayButtonAction(PaymentTypeEnum.REDIRECT)

//                saveCardSwitchHolder11?.view?.cardSwitch?.payButton?.setOnClickListener{
//                    onClickRedirect()
//                }
            }
            else -> {
                displayGoPayLogin()
                //goPayViewsHolder.goPayopened
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

    @SuppressLint("WrongConstant")
    private fun onClickRedirect() {
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
            fragmentManager.beginTransaction().setTransition(200)
                .replace(
                    R.id.webFrameLayout, WebFragment.newInstance(
                        "https://www.google.com/",
                        this
                    )
                )
                .commit()
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

    override fun onDeleteIconClicked(stopAnimation: Boolean, itemId: Int) {
        println("delete icon is clicked:$stopAnimation" + "itemId is" + itemId)
        if (stopAnimation) {
            isShaking.value = false
            cardViewHolder11.view.mainChipgroup.groupAction?.text = LocalizationManager.getValue(
                "GatewayHeader",
                "HorizontalHeaders",
                "rightTitle"
            )
            CustomUtils.showDialog(
                LocalizationManager.getValue("deletegoPayCard", "GoPay"),
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

        } else { cardViewHolder11.view.mainChipgroup.groupAction?.text = LocalizationManager.getValue(
            "close",
            "Common"
        )
            deleteCard = false
        }
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

    override fun onPayCardSwitchAction(isCompleted: Boolean, paymentType: PaymentTypeEnum) {
        if (isCompleted) {
            saveCardSwitchHolder11?.view?.mainSwitch?.visibility = View.VISIBLE
            saveCardSwitchHolder11?.view?.mainSwitch?.switchSaveMobile?.visibility = View.VISIBLE
            saveCardSwitchHolder11?.setSwitchToggleData(paymentType)

            activateActionButton()


            paymentActionType = paymentType


        }else unActivateActionButton()
    }

    override fun onPayCardCompleteAction(
        isCompleted: Boolean,
        paymentType: PaymentTypeEnum,
        cardNumber: String,
        expiryDate: String,
        cvvNumber: String
    ) {
        setPayButtonAction(paymentType)


    }

    // Override function to open NFC fragment and scan the card via NFC.
    override fun onClickNFC() {
       setSlideAnimation()
        println("ssnfc")
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
        amountViewHolder1.changeGroupAction(false)
    }


    // Override function to open card Scanner and scan the card.
    override fun onClickCardScanner() {
        setSlideAnimation()
        println("are u reachinhg scanner")
        //bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        //bottomSheetDialog.behavior.isFitToContents = false

        removeViews(
            businessViewHolder,
            amountViewHolder1,
            cardViewHolder11,
            saveCardSwitchHolder11,
            paymentInputViewHolder
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
    }


    override fun onCurrencyClicked(currencySelected: String, currencyRate: Double) {
        for (i in itemList.indices) {
            currentCurrency = orderList.trx_currency
            currentAmount= CurrencyFormatter.currencyFormat(itemList[i].amount.toString())
            itemList[i].amount = (itemList[i].amount * currencyRate).toLong()
            itemList[i].currency = currencySelected
            selectedAmount= CurrencyFormatter.currencyFormat(itemList[i].amount.toString())
            selectedCurrency = itemList[i].currency

        }
       // itemsViewHolder1.resetItemList(itemList)
        itemsViewHolder1.setResetItemsRecylerView(itemList)
        amountViewHolder1.updateSelectedCurrency(
            displayItemsOpen,
            selectedAmount, selectedCurrency,
            currentAmount, currentCurrency
        )
    }

    @SuppressLint("ResourceType")
    override fun redirectLoadingFinished(done: Boolean) {
        if (done) {
            webFrameLayout.visibility = View.GONE
            removeAllViews()
            addViews(saveCardSwitchHolder11)
            saveCardSwitchHolder11?.view?.cardSwitch?.showOnlyPayButton()
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
            }
        }
    }



      private fun setPayButtonAction(paymentTypeEnum: PaymentTypeEnum){
        /**
         * payment from onSelectPaymentOptionActionListener
         */

        saveCardSwitchHolder11?.view?.cardSwitch?.payButton?.setOnClickListener {
                when(paymentTypeEnum) {
                    PaymentTypeEnum.SAVEDCARD -> {
                        Log.d("PaymentTypeEnum", paymentTypeEnum.toString())
                        saveCardSwitchHolder11?.view?.cardSwitch?.payButton?.changeButtonState(
                            ActionButtonState.LOADING
                        )

                        saveCardSwitchHolder11?.view?.cardSwitch?.payButton?.getImageView(
                            R.drawable.loader,
                            1
                        ) {
                            CustomUtils.showDialog(
                                "Payment Done",
                                "Payment id 2e41232132131",
                                context,
                                1,
                                this
                            )
                        }?.let { it1 ->
                            saveCardSwitchHolder11?.view?.cardSwitch?.payButton?.addChildView(
                                it1
                            )
                        }
                    }
                    PaymentTypeEnum.REDIRECT -> {
                        onClickRedirect()
                    }
                    PaymentTypeEnum.card -> {
                        activateActionButton()
                        onClickRedirect()
                    }
                    PaymentTypeEnum.telecom -> {


                    }
                }
        }

    }



//    override fun onStateChanged(state: ActionButtonState) {}

    private fun removeAllViews() {
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


     fun removeInlineScanner() {
        if (isInlineOpened) {
            if (fragmentManager.findFragmentById(R.id.inline_container) != null)
                fragmentManager?.beginTransaction()
                .remove(fragmentManager?.findFragmentById(R.id.inline_container)!!)
                .commit()
            isInlineOpened = false
            inLineCardLayout.visibility = View.GONE
        }
    }




    fun handleScanSuccessResult(card: Card){
        removeInlineScanner()
        removeViews(amountViewHolder1, businessViewHolder)
        addViews(
            businessViewHolder,
            amountViewHolder1,
            cardViewHolder11,
            paymentInputViewHolder,
            saveCardSwitchHolder11
        )
        if (card != null) {
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
        }

    fun handleNFCScannedResult(emvCard: TapEmvCard){
        removeViews(amountViewHolder1, businessViewHolder)
        addViews(
            businessViewHolder,
            amountViewHolder1,
            cardViewHolder11,
            paymentInputViewHolder,
            saveCardSwitchHolder11
        )

        println("scanned card is$emvCard")
        paymentInputViewHolder.tapCardInputView.setCardNumber(emvCard.cardNumber)
        // paymentInputViewHolder.tapCardInputView.cardHolder.setText(card.cardHolderName)


        if (fragmentManager.findFragmentById(R.id.webFrameLayout) != null)
            fragmentManager?.beginTransaction()
                .remove(fragmentManager?.findFragmentById(R.id.webFrameLayout)!!)
                .commit()
        webFrameLayout?.visibility= View.GONE
    }



    }






