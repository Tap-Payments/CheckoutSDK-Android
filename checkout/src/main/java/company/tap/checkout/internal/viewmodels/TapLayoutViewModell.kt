package company.tap.checkout.internal.viewmodels


import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.content.Context
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
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.transition.TransitionManager
import company.tap.checkout.R
import company.tap.checkout.internal.adapter.CardTypeAdapterUIKIT
import company.tap.checkout.internal.adapter.CurrencyTypeAdapter
import company.tap.checkout.internal.adapter.GoPayCardAdapterUIKIT
import company.tap.checkout.internal.dummygener.*
import company.tap.checkout.internal.enums.SectionType
import company.tap.checkout.internal.interfaces.*
import company.tap.checkout.internal.utils.AnimationEngine
import company.tap.checkout.internal.utils.AnimationEngine.Type.SLIDE
import company.tap.checkout.internal.utils.CurrencyFormatter
import company.tap.checkout.internal.utils.CustomUtils
import company.tap.checkout.internal.viewholders.*
import company.tap.checkout.internal.webview.WebFragment
import company.tap.checkout.internal.webview.WebViewContract
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapuilibrary.themekit.ThemeManager
import company.tap.tapuilibrary.uikit.datasource.TapSwitchDataSource
import company.tap.tapuilibrary.uikit.enums.ActionButtonState
import company.tap.tapuilibrary.uikit.enums.GoPayLoginMethod
import company.tap.tapuilibrary.uikit.fragment.CardScannerFragment
import company.tap.tapuilibrary.uikit.fragment.NFCFragment
import company.tap.tapuilibrary.uikit.interfaces.TapActionButtonInterface
import kotlinx.android.synthetic.main.cardviewholder_layout1.view.*
import kotlinx.android.synthetic.main.fragment_checkouttaps.view.*
import kotlinx.android.synthetic.main.gopaysavedcard_layout.view.*
import kotlinx.android.synthetic.main.switch_layout.view.*
import kotlin.properties.Delegates

/**
 *
 * Copyright © 2020 Tap Payments. All rights reserved.
 *
 */
class TapLayoutViewModell : ViewModel(), BaseLayouttManager, OnCardSelectedActionListener,
    onPaymentCardComplete, onCardNFCCallListener, OnCurrencyChangedActionListener, WebViewContract,
    TapActionButtonInterface {
    private lateinit var context: Context
    private lateinit var fragmentManager: FragmentManager
    private lateinit var sdkLayout: LinearLayout
    private lateinit var bottomSheetLayout: FrameLayout
    private lateinit var webFrameLayout: FrameLayout
    private lateinit var businessViewHolder: BusinessViewHolder
    private lateinit var amountViewHolder1: AmountViewHolder1
    private lateinit var itemsViewHolder1: ItemsViewHolder1
    private lateinit var cardViewHolder11: CardViewHolder11
    private lateinit var paymenttInputViewHolder: PaymenttInputViewHolder
    private lateinit var saveCardSwitchHolder11: SwitchViewHolder11
    private lateinit var goPayViewsHolder: GoPayViewsHolder
    private lateinit var otpViewHolder: OTPViewHolder
    private lateinit var goPaySavedCardHolder: GoPaySavedCardHolder

    //  private lateinit var allCurrencies: List<Currencies1>
    private lateinit var itemList: List<Items1>
    private lateinit var orderList: Order1

    //   private lateinit var currecnyList:List<Currencies1>
    private var savedCardList = MutableLiveData<List<SavedCards>>()
    private var goPayCardList = MutableLiveData<List<GoPaySavedCards>>()
    private var allCurrencies = MutableLiveData<List<Currencies1>>()
    private var displayItemsOpen: Boolean = false
    private val isShaking = MutableLiveData<Boolean>()
    private lateinit var selectedAmountCurrency: String
    private lateinit var currentAmountCurrency: String
    private lateinit var adapter: CardTypeAdapterUIKIT
    private lateinit var goPayAdapter: GoPayCardAdapterUIKIT
    private lateinit var currencyAdapter: CurrencyTypeAdapter
    private lateinit var frameLayout: FrameLayout
    private  var deleteCardisSelected: Boolean=false
    var selectedItemsDel by Delegates.notNull<Int>()


    @RequiresApi(Build.VERSION_CODES.N)
    fun initLayoutManager(
        context: Context,
        fragmentManager: FragmentManager,
        sdkLayout: LinearLayout,
        frameLayout: FrameLayout,
        webFrameLayout: FrameLayout
    ) {
        this.context = context
        this.fragmentManager = fragmentManager
        this.sdkLayout = sdkLayout
        this.frameLayout = frameLayout
        this.webFrameLayout = webFrameLayout
        businessViewHolder = BusinessViewHolder(context)
        amountViewHolder1 = AmountViewHolder1(context, this)
        initAmountAction()
        cardViewHolder11 = CardViewHolder11(context, this,this)
        goPaySavedCardHolder = GoPaySavedCardHolder(context, this, this)
        paymenttInputViewHolder = PaymenttInputViewHolder(context, this, this,this)
        saveCardSwitchHolder11 = SwitchViewHolder11(context)
        itemsViewHolder1 = ItemsViewHolder1(context, this, fragmentManager)
        otpViewHolder = OTPViewHolder(context)
        goPayViewsHolder = GoPayViewsHolder(context, this)
        initSwitchAction()
    }

    private fun initSwitchAction() {
        saveCardSwitchHolder11.view.mainSwitch.visibility = View.VISIBLE
        saveCardSwitchHolder11.view.mainSwitch.switchSaveMobile.visibility = View.GONE
    }

    private fun initAmountAction() {
        amountViewHolder1.setOnItemsClickListener{}
    }


    override fun displayStartupLayout(enabledSections: ArrayList<SectionType>) {
        print("is displayStartupLayout")
        //Todo based on api response logic for swicth case

        addViews(businessViewHolder,
            amountViewHolder1,
            cardViewHolder11,
            paymenttInputViewHolder,
            saveCardSwitchHolder11)

        saveCardSwitchHolder11.view.cardSwitch.visibility = View.VISIBLE
        saveCardSwitchHolder11.view.cardSwitch.payButton.visibility = View.VISIBLE
        saveCardSwitchHolder11.view.mainSwitch.mainSwitchLinear.setBackgroundColor(
            Color.parseColor(ThemeManager.getValue("TapSwitchView.main.backgroundColor"))
        )
    }


    fun setBottomSheetLayout(bottomSheetLayout: FrameLayout) {
        this.bottomSheetLayout = bottomSheetLayout

    }

    override fun displayGoPayLogin() {
        println("goPay Login reached")
        if (this::bottomSheetLayout.isInitialized)
            AnimationEngine.applyTransition(bottomSheetLayout, SLIDE)

        removeViews(businessViewHolder, amountViewHolder1 ,
            cardViewHolder11, paymenttInputViewHolder,
            saveCardSwitchHolder11, otpViewHolder,
            goPayViewsHolder)

        addViews(businessViewHolder
            ,amountViewHolder1, goPayViewsHolder)

        // goPayViewsHolder.goPayLoginInput.inputType = GoPayLoginMethod.PHONE
        // goPayViewsHolder.goPayLoginInput.visibility = View.VISIBLE
        // goPayLoginInput?.changeDataSource(GoPayLoginDataSource())
        if (goPayViewsHolder.goPayopened) {
            goPayViewsHolder.goPayLoginInput.inputType = GoPayLoginMethod.PHONE
            goPayViewsHolder.goPayLoginInput.visibility = View.VISIBLE
        }
        println("addedviews ae" + addViews())
        amountViewHolder1.changeGroupAction(false)
        goPayViewsHolder.goPayopened = true
    }

    override fun displayGoPay() {
        if (this::bottomSheetLayout.isInitialized)
            AnimationEngine.applyTransition(bottomSheetLayout, SLIDE)
        removeViews(
            businessViewHolder,
            amountViewHolder1,
            goPayViewsHolder,
            cardViewHolder11,
            paymenttInputViewHolder,
            saveCardSwitchHolder11,
            otpViewHolder
        )

        addViews(
            businessViewHolder,
            amountViewHolder1,
            goPaySavedCardHolder,
            cardViewHolder11,
            paymenttInputViewHolder,
            saveCardSwitchHolder11
        )

        cardViewHolder11.view.mainChipgroup.groupAction.visibility = View.INVISIBLE
        saveCardSwitchHolder11.view.cardSwitch.payButton.visibility = View.VISIBLE
        adapter.goPayOpenedfromMain(true)
        adapter.removeItems()
    }

    override fun controlCurrency(display: Boolean) {
        println("controlCurrency display " + display)

        if (this::bottomSheetLayout.isInitialized)
            AnimationEngine.applyTransition(
                bottomSheetLayout, SLIDE
            )
        if (display) {
            println("block1 display " + display)
            removeViews(
                businessViewHolder,
                amountViewHolder1,
                cardViewHolder11,
                paymenttInputViewHolder,
                saveCardSwitchHolder11, itemsViewHolder1
            )

            itemsViewHolder1.setView()
            addViews(businessViewHolder, amountViewHolder1)
            frameLayout.visibility = View.VISIBLE
            itemsViewHolder1.itemsdisplayed = true

        } else {
            if (goPayViewsHolder.goPayopened || itemsViewHolder1.itemsdisplayed) {

                removeViews(
                    businessViewHolder,
                    amountViewHolder1,
                    cardViewHolder11,
                    paymenttInputViewHolder,
                    saveCardSwitchHolder11,
                    goPayViewsHolder,
                    otpViewHolder,
                    itemsViewHolder1
                )
                addViews(
                    businessViewHolder,
                    amountViewHolder1,
                    cardViewHolder11,
                    paymenttInputViewHolder,
                    saveCardSwitchHolder11
                )
                itemsViewHolder1.resetView()
                frameLayout.visibility = View.GONE

            } else {


                removeViews(
                    businessViewHolder,
                    amountViewHolder1,
                    cardViewHolder11,
                    paymenttInputViewHolder,
                    saveCardSwitchHolder11,
                    itemsViewHolder1
                )
                addViews(
                    businessViewHolder,
                    amountViewHolder1,
                    cardViewHolder11,
                    paymenttInputViewHolder,
                    saveCardSwitchHolder11
                )

                itemsViewHolder1.resetView()
                frameLayout.visibility = View.GONE


            }
        }
        displayItemsOpen = !display
        amountViewHolder1.changeGroupAction(!display)
        if (this::currentAmountCurrency.isInitialized)
            amountViewHolder1.updateSelectedCurrency(
                displayItemsOpen,
                selectedAmountCurrency,
                currentAmountCurrency
            )
       
    }

    override fun displayOTPView(mobileNumber: String) {
        if (this::bottomSheetLayout.isInitialized) {
            AnimationEngine.applyTransition(
                bottomSheetLayout, SLIDE
            )
        }
        removeViews(cardViewHolder11,
            paymenttInputViewHolder, saveCardSwitchHolder11)
        addViews(otpViewHolder)
        otpViewHolder.otpView.visibility = View.VISIBLE
        otpViewHolder.otpView.mobileNumberText.text = mobileNumber
        otpViewHolder.otpView.changePhone.setOnClickListener {
            goPayViewsHolder.onChangePhoneClicked()
        }
        otpViewHolder.otpView.otpViewActionButton.setOnClickListener {
            goPayViewsHolder.onOtpButtonConfirmationClick(otpViewHolder.otpView.otpViewInput1.text.toString() + otpViewHolder.otpView.otpViewInput2.text.toString())
        }
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
            dummyInitapiResponse1.order1.original_amount.toString(),
            dummyInitapiResponse1.order1.trx_currency,
            dummyInitapiResponse1.order1.trx_currency,
            dummyInitapiResponse1.order1.items.size.toString()
        )

        cardViewHolder11.setDatafromAPI(dummyInitapiResponse1.savedCards as MutableList<SavedCards>)
        goPaySavedCardHolder.setDatafromAPI(dummyInitapiResponse1.goPaySavedCards)
        // println("dummy tapCardPhoneListDataSource" + dummyInitapiResponse1.tapCardPhoneListDataSources)
        paymenttInputViewHolder.setDatafromAPI(dummyInitapiResponse1.tapCardPhoneListDataSource)


        saveCardSwitchHolder11.setDatafromAPI(
            dummyInitapiResponse1.merchant1.name,
            paymenttInputViewHolder.selectedType
        )
        itemsViewHolder1.setDatafromAPI(
            dummyInitapiResponse1.currencies1 as ArrayList<Currencies1>,
            dummyInitapiResponse1.order1.items,
            fragmentManager
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

        adapter = CardTypeAdapterUIKIT(this,this)
        goPayAdapter = GoPayCardAdapterUIKIT(this)
        cardViewHolder11.view.mainChipgroup.chipsRecycler.adapter = adapter
        adapter.updateAdapterData(savedCardList.value as List<SavedCards>)
        goPayAdapter.updateAdapterData(goPayCardList.value as List<GoPaySavedCards>)
        currencyAdapter = CurrencyTypeAdapter(this)
//        currencyAdapter.initOnCurrencyChangedActionListener(this)
        //   currencyAdapter.updateAdapterData(allCurrencies.value as List<Currencies1>)
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
                goPayAdapter.updateShaking(true)
                cardViewHolder11.view.mainChipgroup.groupAction?.text =
                    LocalizationManager.getValue("close", "Common")
            }
        }

        goPaySavedCardHolder.view.goPayLoginView.groupAction.setOnClickListener {
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
            } else {
                isShaking.value = true
                adapter.updateShaking(isShaking.value ?: true)
                Log.d("isShaking.value", isShaking.value.toString())
                goPayAdapter.updateShaking(true)
                goPaySavedCardHolder.view.goPayLoginView.groupAction?.text =
                    LocalizationManager.getValue("close", "Common")
            }
        }
    }


    override fun didDialogueExecute(response: String) {
        println("response are$response")
        if (response == "YES") {
            if (deleteCardisSelected) {
                adapter.deleteSelectedCard(selectedItemsDel)
                adapter.updateShaking(false)
                deleteCardisSelected = false

            } else {
                removeViews(goPaySavedCardHolder)
                adapter.updateAdapterData(savedCardList.value as List<SavedCards>)
                goPayViewsHolder.goPayopened = false
                adapter.goPayOpenedfromMain(true)
                cardViewHolder11.view.mainChipgroup.groupAction.visibility = View.VISIBLE
            }
        } else if (response == "NO") {
            adapter.updateShaking(false)
            deleteCardisSelected = false

        }

    }

    private fun removeViews(vararg viewHolders: TapBaseViewHolder) {
        viewHolders.forEach {
            //   sdkLayout.removeView(it.view)
            Handler(Looper.getMainLooper()).postDelayed(Runnable {
                sdkLayout.removeView(it.view)
                val animation = AnimationUtils.loadAnimation(context, R.anim.fade_in)
                it.view.startAnimation(animation)

            }, 50)
        }

    }

    private fun addViews(vararg viewHolders: TapBaseViewHolder) {
        viewHolders.forEach {
            Handler(Looper.getMainLooper()).postDelayed(Runnable {
                sdkLayout.addView(it.view)
                //  val animation = AnimationUtils.loadAnimation(context, R.anim.fade_in)
                //   it.view.startAnimation(animation)
            }, 50)
            //   sdkLayout.addView(it.view)

        }
    }


    private fun unActivateActionButton() {
     //     tabAnimatedActionButtonViewHolder11.bindViewComponents()
//          tabAnimatedActionButtonViewHolder11.view.actionButton.isClickable = false
        saveCardSwitchHolder11.view.cardSwitch.payButton.setButtonDataSource(
            false,
            context.let { LocalizationManager.getLocale(it).language },
            LocalizationManager.getValue("pay", "ActionButton"),
            Color.parseColor(ThemeManager.getValue("actionButton.Invalid.backgroundColor")),
            Color.parseColor(ThemeManager.getValue("actionButton.Invalid.titleLabelColor"))
        )
        saveCardSwitchHolder11.view.cardSwitch.showOnlyPayButton()
        saveCardSwitchHolder11.view.cardSwitch.payButton.isActivated = false
    }


    override fun onCardSelectedAction(isSelected: Boolean, savedCardsModel: Any) {
        when ((savedCardsModel as SavedCards).chipType) {
            5 -> {
                // do action for saved card
            }
            1 -> {
                // redirect
                activateActionButton()
            }
            else -> {
                displayGoPayLogin()
                //goPayViewsHolder.goPayopened
            }
        }
    }


    private fun activateActionButton() {
        saveCardSwitchHolder11.view.cardSwitch.payButton.setButtonDataSource(
            true,
            context.let { LocalizationManager.getLocale(it).language },
            LocalizationManager.getValue("pay", "ActionButton"),
            Color.parseColor(ThemeManager.getValue("actionButton.Valid.paymentBackgroundColor")),
            Color.parseColor(ThemeManager.getValue("actionButton.Valid.titleLabelColor"))
        )
        saveCardSwitchHolder11.view.cardSwitch.showOnlyPayButton()
        saveCardSwitchHolder11.view.cardSwitch.payButton.isActivated
        saveCardSwitchHolder11.view.cardSwitch.payButton.setOnClickListener {
            onClickRedirect()
        }
    }

    @SuppressLint("WrongConstant")
    private fun onClickRedirect() {
        saveCardSwitchHolder11.view.cardSwitch.payButton.changeButtonState(ActionButtonState.LOADING)
        saveCardSwitchHolder11.view.cardSwitch.payButton.addChildView(
            saveCardSwitchHolder11.view.cardSwitch.payButton.getImageView(
                R.drawable.loader,
                1
            ) {
                if (this::bottomSheetLayout.isInitialized) {
                    AnimationEngine.applyTransition(
                        bottomSheetLayout, SLIDE
                    )
                }
                removeViews(cardViewHolder11)
                removeViews(businessViewHolder)
                removeViews(amountViewHolder1)
                removeViews(saveCardSwitchHolder11)
                removeViews(paymenttInputViewHolder)
                fragmentManager.beginTransaction().setTransition(200)
                    .replace(R.id.webFrameLayout, WebFragment.newInstance("https://www.google.com/",this))
                    .commit()
            })
    }

    private fun changeBottomSheetTransition() {
        bottomSheetLayout?.let { layout ->
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
            deleteCardisSelected = true

        } else { cardViewHolder11.view.mainChipgroup.groupAction?.text = LocalizationManager.getValue(
            "close",
            "Common"
        )
            deleteCardisSelected = false
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

    override fun onPaycardSwitchAction(isCompleted: Boolean) {
        saveCardSwitchHolder11.view.cardSwitch.setSwitchDataSource(getSwitchDataSource("fdsg"))
        if (isCompleted) {
            saveCardSwitchHolder11.view.mainSwitch.visibility = View.VISIBLE
            saveCardSwitchHolder11.view.mainSwitch.switchSaveMobile.visibility = View.VISIBLE
            saveCardSwitchHolder11.view.mainSwitch.mainSwitchLinear.setBackgroundColor(
                Color.parseColor(
                    ThemeManager.getValue("TapSwitchView.main.backgroundColor")
                )
            )
            saveCardSwitchHolder11.view.cardSwitch.visibility = View.VISIBLE
            activateActionButton()
        } else unActivateActionButton()
    }

    // Override function to open NFC fragment and scan the card via NFC.
    override fun onClickNFC() {
        if (this::bottomSheetLayout.isInitialized) {
            AnimationEngine.applyTransition(
                bottomSheetLayout, SLIDE
            )
        }
        removeViews(
            businessViewHolder,
            amountViewHolder1,
            cardViewHolder11,
            saveCardSwitchHolder11,
            paymenttInputViewHolder
        )
        addViews(businessViewHolder, amountViewHolder1)
        fragmentManager.beginTransaction().replace(R.id.fragment_container_nfc_lib, NFCFragment())
        amountViewHolder1.changeGroupAction(false)
    }


    // Override function to open card Scanner and scan the card.
    override fun onClickCardScanner() {
        if (this::bottomSheetLayout.isInitialized) {
            AnimationEngine.applyTransition(
                bottomSheetLayout, SLIDE
            )
        }
        println("are u reachinhg scanner")
        removeViews(
            businessViewHolder,
            amountViewHolder1,
            cardViewHolder11,
            saveCardSwitchHolder11,
            paymenttInputViewHolder
        )
        addViews(businessViewHolder, amountViewHolder1)
        fragmentManager.beginTransaction()
            .add(R.id.fragment_container_nfc_lib, CardScannerFragment()).commit()
        amountViewHolder1.changeGroupAction(false)
    }

    //Setting data to TapSwitchDataSource
    private fun getSwitchDataSource(switchText: String): TapSwitchDataSource {
        return TapSwitchDataSource(
            switchSave = switchText,
            switchSaveMerchantCheckout = "Save for [merchant_name] Checkouts",
            switchSavegoPayCheckout = LocalizationManager.getValue("goPayTextLabel", "GoPay"),
            savegoPayText = LocalizationManager.getValue("savegoPayLabel", "GoPay"),
            alertgoPaySignup = LocalizationManager.getValue("goPaySignupLabel", "GoPay")
        )
    }

    private fun configureSwitch() {
        saveCardSwitchHolder11.view.mainSwitch.switchSaveMobile?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                saveCardSwitchHolder11.view.cardSwitch.tapCardSwitchLinear.setBackgroundColor(
                    Color.parseColor(
                        ThemeManager.getValue("TapSwitchView.backgroundColor")
                    )
                )
                saveCardSwitchHolder11.view.cardSwitch.saveSwitchChip.cardElevation = 2.5f
                saveCardSwitchHolder11.view.cardSwitch.payButton.stateListAnimator = null
                saveCardSwitchHolder11.view.cardSwitch.payButton.setButtonDataSource(
                    true,
                    "en",
                    "Pay",
                    Color.parseColor(ThemeManager.getValue("actionButton.Valid.paymentBackgroundColor")),
                    Color.parseColor(ThemeManager.getValue("actionButton.Valid.titleLabelColor"))
                )
                saveCardSwitchHolder11.view.cardSwitch.switchesLayout?.visibility = View.VISIBLE
                saveCardSwitchHolder11.view.cardSwitch.switchSaveMerchant?.visibility = View.VISIBLE
                saveCardSwitchHolder11.view.cardSwitch.switchSaveMerchant?.isChecked = true
                saveCardSwitchHolder11.view.cardSwitch.switchGoPayCheckout?.isChecked = true
                saveCardSwitchHolder11.view.cardSwitch.switchGoPayCheckout?.visibility =
                    View.VISIBLE
                saveCardSwitchHolder11.view.cardSwitch.saveGoPay?.visibility = View.VISIBLE
                saveCardSwitchHolder11.view.cardSwitch.alertGoPaySignUp?.visibility = View.VISIBLE
                saveCardSwitchHolder11.view.cardSwitch.switchSeparator?.visibility = View.VISIBLE
            } else {
                saveCardSwitchHolder11.view.cardSwitch.tapCardSwitchLinear.setBackgroundColor(
                    Color.parseColor(
                        ThemeManager.getValue("TapSwitchView.main.backgroundColor")
                    )
                )
                saveCardSwitchHolder11.view.cardSwitch.saveSwitchChip.cardElevation = 0f
                saveCardSwitchHolder11.view.cardSwitch.payButton.stateListAnimator = null
                saveCardSwitchHolder11.view.cardSwitch.payButton.setButtonDataSource(
                    false,
                    "en",
                    "Pay",
                    Color.parseColor(ThemeManager.getValue("actionButton.Invalid.backgroundColor")),
                    Color.parseColor(ThemeManager.getValue("actionButton.Invalid.titleLabelColor"))
                )
                saveCardSwitchHolder11.view.cardSwitch.switchesLayout?.visibility = View.GONE
                saveCardSwitchHolder11.view.cardSwitch.switchSaveMerchant?.visibility = View.GONE
                saveCardSwitchHolder11.view.cardSwitch.switchSaveMerchant?.isChecked = false
                saveCardSwitchHolder11.view.cardSwitch.switchGoPayCheckout?.isChecked = false
                saveCardSwitchHolder11.view.cardSwitch.switchGoPayCheckout?.visibility = View.GONE
                saveCardSwitchHolder11.view.cardSwitch.saveGoPay?.visibility = View.GONE
                saveCardSwitchHolder11.view.cardSwitch.alertGoPaySignUp?.visibility = View.GONE
                saveCardSwitchHolder11.view.cardSwitch.switchSeparator?.visibility = View.GONE
            }
        }
    }

    override fun onCurrencyClicked(currencySelected: String, currencyRate: Double) {
        for (i in itemList.indices) {
            currentAmountCurrency = orderList.trx_currency + " " + CurrencyFormatter.currencyFormat(itemList[i].amount.toString())
            itemList[i].amount = (itemList[i].amount * currencyRate).toLong()
            itemList[i].currency = currencySelected
            selectedAmountCurrency = itemList[i].currency + " " + CurrencyFormatter.currencyFormat(itemList[i].amount.toString())
        }
        itemsViewHolder1.resetItemList(itemList)
        amountViewHolder1.updateSelectedCurrency(displayItemsOpen, selectedAmountCurrency, currentAmountCurrency)
    }

    @SuppressLint("ResourceType")
    override fun redirectLoadingFinished(done: Boolean) {
        Log.d("donedone", done.toString())
        if (done) {
            webFrameLayout.visibility = View.GONE
            removeAllViews()
            addViews(saveCardSwitchHolder11)
            saveCardSwitchHolder11.view.cardSwitch.showOnlyPayButton()
            saveCardSwitchHolder11.view.cardviewSwitch.visibility = View.GONE
            saveCardSwitchHolder11.view.cardSwitch.payButton.isActivated = true
            saveCardSwitchHolder11.view.cardSwitch.payButton.changeButtonState(ActionButtonState.SUCCESS)
            saveCardSwitchHolder11.view.cardSwitch.payButton.addChildView(
                saveCardSwitchHolder11.view.cardSwitch.payButton.getImageView(
                    R.drawable.success,
                    1
                ) {
                    removeAllViews()
                })
        }
    }

    private fun removeAllViews() {
        removeViews(
            businessViewHolder,
            amountViewHolder1,
            cardViewHolder11,
            paymenttInputViewHolder,
            saveCardSwitchHolder11,
            goPayViewsHolder,
            otpViewHolder
        )
    }

//    override fun onClickActionButton(redirectAction: Boolean) {
//
//    }

    override fun onEnterValidCardNumberActionListener() {
      println("Valid pay button")
    }

    override fun onEnterValidPhoneNumberActionListener() {
        TODO("Not yet implemented")
    }

    override fun onSelectPaymentOptionActionListener() {
        activateActionButton()
    }

    override fun onStateChanged(state: ActionButtonState) {}



}


