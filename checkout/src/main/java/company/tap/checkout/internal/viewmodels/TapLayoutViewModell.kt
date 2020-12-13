package company.tap.checkout.internal.viewmodels


import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ShapeDrawable
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.DividerItemDecoration
import company.tap.checkout.R
import company.tap.checkout.internal.adapter.CardTypeAdapterUIKIT
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
import company.tap.tapuilibrary.uikit.enums.GoPayLoginMethod
import company.tap.tapuilibrary.uikit.fragment.CardScannerFragment
import company.tap.tapuilibrary.uikit.fragment.NFCFragment
import company.tap.tapuilibrary.uikit.interfaces.OtpButtonConfirmationInterface
import kotlinx.android.synthetic.main.businessview_layout.view.*
import kotlinx.android.synthetic.main.cardviewholder_layout.view.*
import kotlinx.android.synthetic.main.gopaysavedcard_layout.view.*
import kotlinx.android.synthetic.main.switch_layout.view.*


/**
 *
 * Copyright © 2020 Tap Payments. All rights reserved.
 *
 */
class TapLayoutViewModell : ViewModel(), BaseLayouttManager, OnCardSelectedActionListener,
    onPaymentCardComplete, onCardNFCCallListener, OnCurrencyChangedActionListener, WebViewContract {
    private lateinit var context: Context
    private lateinit var fragmentManager: FragmentManager
    private lateinit var sdkLayout: LinearLayout
    private lateinit var bottomSheetLayout: FrameLayout
    private lateinit var businessViewHolder: BusinessViewHolder
    private lateinit var amountViewHolder1: AmountViewHolder1
    private lateinit var itemsViewHolder1: ItemsViewHolder1
    private lateinit var cardViewHolder11: CardViewHolder11
    private lateinit var paymenttInputViewHolder: PaymenttInputViewHolder
    private lateinit var saveCardSwitchHolder11: SwitchViewHolder11
    private lateinit var goPayViewsHolder: GoPayViewsHolder
    private lateinit var otpViewHolder: OTPViewHolder
    private lateinit var goPaySavedCardHolder: GoPaySavedCardHolder
    private lateinit var allCurrencies: List<Currencies1>
    private lateinit var itemList: List<Items1>
    private lateinit var orderList: Order1
    private var savedCardList = MutableLiveData<List<SavedCards>>()
    private var goPayCardList = MutableLiveData<List<GoPaySavedCards>>()
    private var displayItemsOpen: Boolean = false
    private val isShaking = MutableLiveData<Boolean>()
    private lateinit var selectedAmountCurrency: String
    private lateinit var currentAmountCurrency: String
    private lateinit var adapter: CardTypeAdapterUIKIT
    private lateinit var goPayAdapter: GoPayCardAdapterUIKIT
    private lateinit var frameLayout: FrameLayout

    @RequiresApi(Build.VERSION_CODES.N)
    fun initLayoutManager(
        context: Context,
        fragmentManager: FragmentManager,
        sdkLayout: LinearLayout,
        frameLayout: FrameLayout
    ) {
        this.context = context
        this.fragmentManager = fragmentManager
        this.sdkLayout = sdkLayout
        this.frameLayout = frameLayout
        businessViewHolder = BusinessViewHolder(context)
        amountViewHolder1 = AmountViewHolder1(context, this)
        initAmountAction()
        cardViewHolder11 = CardViewHolder11(context, this)
        goPaySavedCardHolder = GoPaySavedCardHolder(context, this, this)
        paymenttInputViewHolder = PaymenttInputViewHolder(context, this, this)
        saveCardSwitchHolder11 = SwitchViewHolder11(context)
        itemsViewHolder1 = ItemsViewHolder1(context, this, fragmentManager)
        otpViewHolder = OTPViewHolder(context)
        goPayViewsHolder = GoPayViewsHolder(context, this)
//        initCardsGroup()
        initSwitchAction()
    }

    fun initWebFragment(){

    }

    private fun initSwitchAction() {
        saveCardSwitchHolder11.view.mainSwitch.visibility = View.VISIBLE
        saveCardSwitchHolder11.view.mainSwitch.switchSaveMobile.visibility = View.GONE
        //  saveCardSwitchHolder.view.cardSwitch.switchSaveMobile.visibility = View.GONE
    }

    private fun initAmountAction() {
        amountViewHolder1.setOnItemsClickListener {
            //itemsViewHolder1.resetView()
        }
    }


    override fun displayStartupLayout(enabledSections: ArrayList<SectionType>) {
        print("is displayStartupLayout")
        //Todo based on api response logic for swicth case

        addViews(
            businessViewHolder,
            amountViewHolder1,
            cardViewHolder11,
            paymenttInputViewHolder,
            saveCardSwitchHolder11
        )
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
        if (this::bottomSheetLayout.isInitialized) {
            AnimationEngine.applyTransition(
                bottomSheetLayout, SLIDE
            )
        }
        removeViews(
            businessViewHolder,
            amountViewHolder1,
            cardViewHolder11,
            paymenttInputViewHolder,
            saveCardSwitchHolder11,
            goPayViewsHolder,
            otpViewHolder
        )

        addViews(
            businessViewHolder,
            amountViewHolder1,
            goPayViewsHolder
        )
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
                println("block2 display " + display)
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
                println("block3 display " + display)

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
        removeViews(cardViewHolder11, paymenttInputViewHolder, saveCardSwitchHolder11)
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

        allCurrencies = dummyInitapiResponse1.currencies1
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
            removeViews(goPaySavedCardHolder)
            adapter.updateAdapterData(savedCardList.value as List<SavedCards>)
            goPayViewsHolder.goPayopened = false
            adapter.goPayOpenedfromMain(true)
            cardViewHolder11.view.mainChipgroup.groupAction.visibility = View.VISIBLE

        } else if (response == "NO") {

        }

    }

    private fun removeViews(vararg viewHolders: TapBaseViewHolder) {
        viewHolders.forEach {
            sdkLayout.removeView(it.view)
        }
    }

    private fun addViews(vararg viewHolders: TapBaseViewHolder) {
        viewHolders.forEach {
            sdkLayout.addView(it.view)
        }
    }

    private fun activateActionButton() {
        // tabAnimatedActionButtonViewHolder11.activateButton(context)
    }

    private fun unActivateActionButton() {
//          tabAnimatedActionButtonViewHolder11.bindViewComponents()
//          tabAnimatedActionButtonViewHolder11.view.actionButton.isClickable = false
    }


    override fun onCardSelectedAction(isSelected: Boolean, savedCardsModel: Any) {
        when ((savedCardsModel as SavedCards).chipType) {
            5 -> {
                // do action for saved card
            }
            1 -> {
                // redirect
                onClickRedirect()
            }
            else -> {
                displayGoPayLogin()
                //goPayViewsHolder.goPayopened
            }
        }
    }


    override fun onDeleteIconClicked(stopAnimation: Boolean, itemId: Int) {
        if (stopAnimation) {
            isShaking.value = false
            cardViewHolder11.view.mainChipgroup.groupAction?.text = LocalizationManager.getValue("GatewayHeader", "HorizontalHeaders", "rightTitle")
        } else cardViewHolder11.view.mainChipgroup.groupAction?.text = LocalizationManager.getValue("close", "Common")
    }

    override fun onGoPayLogoutClicked(isClicked: Boolean) {
        if (isClicked) CustomUtils.showDialog("Are you sure you would like to sign out", "The goPayCards will be hidden from the page and you will need to login again to use any of them", context, "twobtns", this)
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
            saveCardSwitchHolder11.view.mainSwitch.mainSwitchLinear.setBackgroundColor(Color.parseColor(ThemeManager.getValue("TapSwitchView.main.backgroundColor")))
            saveCardSwitchHolder11.view.cardSwitch.visibility = View.VISIBLE
            activateActionButton()
        } else unActivateActionButton()
    }

    // Override function to open NFC fragment and scan the card via NFC.
    override fun onClickNFC() {
        removeViews(businessViewHolder, amountViewHolder1, cardViewHolder11, saveCardSwitchHolder11, paymenttInputViewHolder)
        addViews(businessViewHolder, amountViewHolder1)
        fragmentManager.beginTransaction().replace(R.id.fragment_container_nfc_lib, NFCFragment())
        amountViewHolder1.changeGroupAction(false)
    }
    private fun onClickRedirect() {
        removeViews(businessViewHolder, amountViewHolder1, cardViewHolder11, saveCardSwitchHolder11, paymenttInputViewHolder)
        addViews(businessViewHolder)
        businessViewHolder.view.headerView.showOnlyTopLinear()
//        businessViewHolder.view.headerView.topLinear.visibility =View.VISIBLE
        fragmentManager.beginTransaction().replace(R.id.webFrameLayout, WebFragment(this)).commit()
    }

    // Override function to open card Scanner and scan the card.
    override fun onClickCardScanner() {
        println("are u reachinhg scanner")
        removeViews(businessViewHolder, amountViewHolder1, cardViewHolder11, saveCardSwitchHolder11, paymenttInputViewHolder)
        addViews(businessViewHolder, amountViewHolder1)
        fragmentManager.beginTransaction().add(R.id.fragment_container_nfc_lib, CardScannerFragment()).commit()
        amountViewHolder1.changeGroupAction(false)
    }

    //Setting data to TapSwitchDataSource
    private fun getSwitchDataSource(switchText: String): TapSwitchDataSource {
        return TapSwitchDataSource(
            switchSave = switchText,
            switchSaveMerchantCheckout = "Save for [merchant_name] Checkouts",
            switchSavegoPayCheckout = "By enabling goPay, your mobile number will be saved with Tap Payments to get faster and more secure checkouts in multiple apps and websites.",
            savegoPayText = "Save for goPay Checkouts",
            alertgoPaySignup = "Please check your email or SMS’s in order to complete the goPay Checkout signup process."
        )
    }

    private fun configureSwitch() {
        saveCardSwitchHolder11.view.mainSwitch.switchSaveMobile?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                saveCardSwitchHolder11.view.cardSwitch.tapCardSwitchLinear.setBackgroundColor(Color.parseColor(ThemeManager.getValue("TapSwitchView.backgroundColor")))
                saveCardSwitchHolder11.view.cardSwitch.saveSwitchChip.cardElevation = 2.5f
                saveCardSwitchHolder11.view.cardSwitch.payButton.stateListAnimator = null
                saveCardSwitchHolder11.view.cardSwitch.payButton.setButtonDataSource(true, "en", "Pay", Color.parseColor(ThemeManager.getValue("actionButton.Valid.paymentBackgroundColor")), Color.parseColor(ThemeManager.getValue("actionButton.Valid.titleLabelColor")))
                saveCardSwitchHolder11.view.cardSwitch.switchesLayout?.visibility = View.VISIBLE
                saveCardSwitchHolder11.view.cardSwitch.switchSaveMerchant?.visibility = View.VISIBLE
                saveCardSwitchHolder11.view.cardSwitch.switchSaveMerchant?.isChecked = true
                saveCardSwitchHolder11.view.cardSwitch.switchGoPayCheckout?.isChecked = true
                saveCardSwitchHolder11.view.cardSwitch.switchGoPayCheckout?.visibility = View.VISIBLE
                saveCardSwitchHolder11.view.cardSwitch.saveGoPay?.visibility = View.VISIBLE
                saveCardSwitchHolder11.view.cardSwitch.alertGoPaySignUp?.visibility = View.VISIBLE
                saveCardSwitchHolder11.view.cardSwitch.switchSeparator?.visibility = View.VISIBLE
            } else {
                saveCardSwitchHolder11.view.cardSwitch.tapCardSwitchLinear.setBackgroundColor(Color.parseColor(ThemeManager.getValue("TapSwitchView.main.backgroundColor")))
                saveCardSwitchHolder11.view.cardSwitch.saveSwitchChip.cardElevation = 0f
                saveCardSwitchHolder11.view.cardSwitch.payButton.stateListAnimator = null
                saveCardSwitchHolder11.view.cardSwitch.payButton.setButtonDataSource(false, "en", "Pay", Color.parseColor(ThemeManager.getValue("actionButton.Invalid.backgroundColor")), Color.parseColor(ThemeManager.getValue("actionButton.Invalid.titleLabelColor")))
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

    override fun redirectLoadingFinished(done: Boolean) {
    }

}


