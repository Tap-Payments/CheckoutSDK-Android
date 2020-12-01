package company.tap.checkout.internal.viewmodels


import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ShapeDrawable
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
import company.tap.cardbusinesskit.testmodels.Items
import company.tap.checkout.R
import company.tap.checkout.internal.dummygener.Items1
import company.tap.checkout.internal.dummygener.JsonResponseDummy
import company.tap.checkout.internal.dummygener.SavedCards
import company.tap.checkout.internal.enums.SectionType
import company.tap.checkout.internal.interfaces.BaseLayoutManager
import company.tap.checkout.internal.interfaces.OnCardSelectedActionListener
import company.tap.checkout.internal.interfaces.onCardNFCCallListener
import company.tap.checkout.internal.interfaces.onPaymentCardComplete
import company.tap.checkout.internal.viewholders.*
import company.tap.checkout.open.TapCheckoutFragment
import company.tap.tapuilibrary.themekit.ThemeManager
import company.tap.tapuilibrary.uikit.adapters.CardTypeAdapter
import company.tap.tapuilibrary.uikit.datasource.TapSwitchDataSource
import company.tap.tapuilibrary.uikit.fragment.CardScannerFragment
import company.tap.tapuilibrary.uikit.fragment.CurrencyViewFragment
import company.tap.tapuilibrary.uikit.fragment.NFCFragment
import kotlinx.android.synthetic.main.cardviewholder_layout.view.*
import kotlinx.android.synthetic.main.switch_layout.view.*


/**
 *
 * Created by Mario Gamal on 6/14/20
 * Copyright © 2020 Tap Payments. All rights reserved.
 *
 */
class TapLayoutManager : ViewModel(),
    BaseLayoutManager, OnCardSelectedActionListener, onPaymentCardComplete, onCardNFCCallListener,
    company.tap.tapuilibrary.uikit.interfaces.OnCardSelectedActionListener {

    private lateinit var context: Context
    private lateinit var fragmentManager: FragmentManager
    private lateinit var sdkLayout: LinearLayout
    private lateinit var bottomSheetLayout: FrameLayout
    private lateinit var businessViewHolder: BusinessViewHolder
    private lateinit var cardScannerViewHolder: CardScannerViewHolder
    private lateinit var amountViewHolder: AmountViewHolder
    private lateinit var itemsViewHolder: ItemsViewHolder
    private lateinit var cardViewHolder: CardViewHolder
    private lateinit var goPayLoginHolder: GoPayLoginHolder
    private lateinit var paymentInputViewHolder: PaymentInputViewHolder
    private lateinit var saveCardSwitchHolder: SwitchViewHolder
    private lateinit var goPayViewHolder: GoPayViewHolder
    private lateinit var otpViewHolder: OTPViewHolder
    private lateinit var supportedCurrecnyList: ArrayList<String>
    private lateinit var itemList: List<Items1>
    private lateinit var savedcardList: List<SavedCards>

    fun initLayoutManager(
        context: Context,
        fragmentManager: FragmentManager,
        sdkLayout: LinearLayout
    ) {
        this.context = context
        this.fragmentManager = fragmentManager
        this.sdkLayout = sdkLayout
        businessViewHolder = BusinessViewHolder(context)
        amountViewHolder = AmountViewHolder(context)
        cardViewHolder = CardViewHolder(context, this, this)
        goPayLoginHolder = GoPayLoginHolder(context, this, this)
        paymentInputViewHolder = PaymentInputViewHolder(context, this, this)
        saveCardSwitchHolder = SwitchViewHolder(context)
        itemsViewHolder = ItemsViewHolder(context, this)

        otpViewHolder = OTPViewHolder(context)

       goPayViewHolder= GoPayViewHolder(context,this)
        println("context = [${context}], fragmentManager = [${fragmentManager}], fragmentManager = [${fragmentManager}]")
        initAmountAction()
        initCardsGroup()
        initSwitchAction()

      //  otpViewHolder.otpView?.setOTPInterface(this)
       // goPayViewHolder.goPayLoginInput?.setOpenOTPInterface(this)

    }

    private fun initSwitchAction() {
        saveCardSwitchHolder.view.mainSwitch.visibility = View.VISIBLE
       saveCardSwitchHolder.view.mainSwitch.switchSaveMobile.visibility = View.GONE
      //  saveCardSwitchHolder.view.cardSwitch.switchSaveMobile.visibility = View.GONE
    }

    private fun initAmountAction() {
        amountViewHolder.setOnItemsClickListener {
            controlCurrency(itemsViewHolder.displayed)

        }
    }

    private fun initCardsGroup() {
        cardViewHolder.view.setOnClickListener {
           // displayGoPayLogin()
           println("init caleed ")
        }
    }

    override fun displayStartupLayout(enabledSections: ArrayList<SectionType>) {
        //Todo based on api response logic for swicth case
            addViews(
                businessViewHolder,
                amountViewHolder,
                cardViewHolder,
                paymentInputViewHolder,
                saveCardSwitchHolder
            )

        saveCardSwitchHolder.view.cardSwitch.visibility = View.VISIBLE
        saveCardSwitchHolder.view.cardSwitch.payButton.visibility = View.VISIBLE
        saveCardSwitchHolder.view.mainSwitch.mainSwitchLinear.setBackgroundColor(Color.parseColor(ThemeManager.getValue("TapSwitchView.main.backgroundColor")))

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
        cardViewHolder.view.mainChipgroup.chipsRecycler.addItemDecoration(divider)



        cardViewHolder.view.mainChipgroup.chipsRecycler.adapter = CardTypeAdapter(savedcardList as ArrayList<SavedCards>, this, false)

        cardViewHolder.view.mainChipgroup.groupAction?.visibility = View.VISIBLE

        cardViewHolder.view.mainChipgroup.groupAction?.setOnClickListener {
            if (cardViewHolder.view.mainChipgroup.groupAction?.text == "Close"){
                cardViewHolder.view.mainChipgroup.chipsRecycler.adapter = CardTypeAdapter(savedcardList as ArrayList<SavedCards>, this, false)
                cardViewHolder.view.mainChipgroup.groupAction?.text = "Edit"

            }else{
                cardViewHolder.view.mainChipgroup.chipsRecycler.adapter = CardTypeAdapter(savedcardList as ArrayList<SavedCards>, this, true)
                cardViewHolder.view.mainChipgroup.groupAction?.text = "Close"
            }

        }
    }



    fun setBottomSheetLayout(bottomSheetLayout: FrameLayout) {
        this.bottomSheetLayout = bottomSheetLayout
    }

    override fun displayGoPayLogin() {
       // if (this::bottomSheetLayout.isInitialized)
       // AnimationEngine.applyTransition(bottomSheetLayout, SLIDE)
      //  removeViews(
       //cardViewHolder,
      //      paymentInputViewHolder,
      //      saveCardSwitchHolder
      //  )
       // if (this::bottomSheetLayout.isInitialized)
       // addViews(goPayViewHolder)
        //addViews(goPayLoginHolder)
    }

    override fun displayGoPay() {
        removeViews(  businessViewHolder,
            amountViewHolder,
            cardViewHolder,
            paymentInputViewHolder,
            saveCardSwitchHolder,
        otpViewHolder)
        addViews(
            businessViewHolder,
            amountViewHolder,
            goPayLoginHolder,
            cardViewHolder,
            paymentInputViewHolder,
            saveCardSwitchHolder
        )

        cardViewHolder.view.mainChipgroup.groupAction.text = ""
        saveCardSwitchHolder.view.cardSwitch.payButton.visibility = View.VISIBLE

    }

    override fun controlCurrency(display: Boolean) {
        val manager: FragmentManager = fragmentManager
        val transaction = manager.beginTransaction()
       if (this::bottomSheetLayout.isInitialized)
            TransitionManager.beginDelayedTransition(bottomSheetLayout)
        if (display) {
            removeViews(
                cardViewHolder,
                paymentInputViewHolder,
                saveCardSwitchHolder
            )
        //   itemsViewHolder.setDatafromAPI(supportedCurrecnyList,itemList,fragmentManager)
          if (supportedCurrecnyList.size != 0) {

              transaction.add(
                    R.id.sdkContainer,
                    CurrencyViewFragment(supportedCurrecnyList, itemList)
                )
                transaction.addToBackStack(null)
                transaction.commit()
             //  addViews(itemsViewHolder)
              itemsViewHolder.displayed = false

          }

        } else {
            removeViews(businessViewHolder,amountViewHolder,itemsViewHolder)


            supportedCurrecnyList.clear()
            manager.beginTransaction().replace(R.id.sdkContainer, TapCheckoutFragment()).commit()

            transaction.addToBackStack(null)
            transaction.commit()


        }


        itemsViewHolder.displayed = !display
        amountViewHolder.changeGroupAction(!display)

    }

    override fun displayOTP(otpMobile: String) {
        addViews(otpViewHolder)
        println("display OTP is called"+otpMobile)
        otpViewHolder.otpView.visibility = View.VISIBLE
        otpViewHolder.otpView.mobileNumberText.text=otpMobile
        otpViewHolder.otpView.changePhone.setOnClickListener {
            goPayViewHolder.onChangePhoneClicked()
            removeViews(otpViewHolder)
       }

    }

    override fun displayRedirect(url: String) {}

    override fun displaySaveCardOptions() {

    }

    override fun getDatafromAPI(dummyInitapiResponse: JsonResponseDummy) {

        println("dummy response value is ${dummyInitapiResponse}")
        businessViewHolder.setDatafromAPI(
            dummyInitapiResponse.merchant1.logo,
            dummyInitapiResponse.merchant1.name
        )
        amountViewHolder.setDatafromAPI(
            dummyInitapiResponse.order1.original_amount.toString(),
            dummyInitapiResponse.order1.trx_currency,
            dummyInitapiResponse.order1.trx_currency,
            dummyInitapiResponse.order1.items?.size.toString()
        )
        cardViewHolder.setDatafromAPI(dummyInitapiResponse.savedCards)
        goPayLoginHolder.setDatafromAPI(dummyInitapiResponse.savedCards)
        println("dummy values"+dummyInitapiResponse.tapCardPhoneListDataSource)
        paymentInputViewHolder.setDatafromAPI(dummyInitapiResponse.tapCardPhoneListDataSource)


        saveCardSwitchHolder.setDatafromAPI(
            dummyInitapiResponse.merchant1.name,
            paymentInputViewHolder.selectedType
        )
        supportedCurrecnyList = dummyInitapiResponse.currencies as ArrayList<String>
        itemList = dummyInitapiResponse.order1.items
        savedcardList = dummyInitapiResponse.savedCards
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
       // tabAnimatedActionButtonViewHolder.activateButton(context)
    }

    private fun unActivateActionButton() {
      //  tabAnimatedActionButtonViewHolder.bindViewComponents()
      //  tabAnimatedActionButtonViewHolder.view.actionButton.isClickable = false
    }

    override fun onCardSelectedAction(isSelected: Boolean) {
        if (isSelected) {
           // activateActionButton()
          //  tabAnimatedActionButtonViewHolder.view.actionButton.setOnClickListener { tabAnimatedActionButtonViewHolder.setOnClickAction() }
        } else unActivateActionButton()
    }

    override fun onDeleteIconClicked(stopAnimation: Boolean, itemId: Int) {
        println("onDeleteIconClicked is onDeleteIconClicked")
        if (stopAnimation){
            stopShakingCards(cardViewHolder.view.mainChipgroup.chipsRecycler)
            cardViewHolder.view.mainChipgroup.groupAction?.text = "Edit"
        }else cardViewHolder.view.mainChipgroup.groupAction?.text = "Close"
    }

    override fun onPaycardSwitchAction(isCompleted: Boolean) {
      saveCardSwitchHolder.view.cardSwitch.setSwitchDataSource(getSwitchDataSource("fdsg"))

        if (isCompleted) {
             saveCardSwitchHolder.view.mainSwitch.visibility = View.VISIBLE
             saveCardSwitchHolder.view.mainSwitch.switchSaveMobile.visibility = View.VISIBLE
            saveCardSwitchHolder.view.mainSwitch.mainSwitchLinear.setBackgroundColor(Color.parseColor(ThemeManager.getValue("TapSwitchView.main.backgroundColor")))
            saveCardSwitchHolder.view.cardSwitch.visibility = View.VISIBLE
           /* saveCardSwitchHolder.view.cardSwitch.switchesLayout?.visibility = View.VISIBLE
            saveCardSwitchHolder.view.cardSwitch.switchSaveMerchant.isChecked = true
            saveCardSwitchHolder.view.cardSwitch.switchGoPayCheckout.isChecked = true
            saveCardSwitchHolder.view.cardSwitch.switchSaveMobile.isChecked = true
            saveCardSwitchHolder.view.cardSwitch.switchesLayout.visibility = View.VISIBLE
            saveCardSwitchHolder.view.cardSwitch.switchSaveMobile.visibility = View.VISIBLE*/
            activateActionButton()
           // configureSwitch()
        } else {
           /* saveCardSwitchHolder.view.cardSwitch.switchSaveMerchant.isChecked = false
            saveCardSwitchHolder.view.cardSwitch.switchGoPayCheckout.isChecked = false
            saveCardSwitchHolder.view.cardSwitch.switchSaveMobile.isChecked = false
            saveCardSwitchHolder.view.cardSwitch.switchSaveMobile.visibility = View.GONE
            saveCardSwitchHolder.view.cardSwitch.switchesLayout.visibility = View.GONE*/
            unActivateActionButton()

        }

    }

    // Override function to open NFC fragment and scan the card via NFC.
    override fun onClickNFC() {
        removeViews(
            cardViewHolder,
            saveCardSwitchHolder,
            paymentInputViewHolder
        )
        val manager: FragmentManager = fragmentManager
        val transaction = manager.beginTransaction()
        transaction.add(
            R.id.sdkContainer,
            NFCFragment()
        )
        transaction.addToBackStack(null)
        transaction.commit()
        amountViewHolder.changeGroupAction(true)


    }

    // Override function to open card Scanner and scan the card.
    override fun  onClickCardScanner() {
        println("are u reachinhg scanner")
       // cardScannerViewHolder  = CardScannerViewHolder(context)
        removeViews(
            cardViewHolder,
            saveCardSwitchHolder,
            paymentInputViewHolder
        )
       val manager: FragmentManager = fragmentManager
        val transaction = manager.beginTransaction()
        transaction.add(
                R.id.sdkContainer,
            CardScannerFragment()
        )
        transaction.addToBackStack(null)
        transaction.commit()

        amountViewHolder.changeGroupAction(true)


    }



    //Setting data to TapSwitchDataSource
    private fun getSwitchDataSource(switchText: String): TapSwitchDataSource {
        println("switch data source "+switchText)
        return TapSwitchDataSource(
            switchSave = switchText,
            switchSaveMerchantCheckout = "Save for [merchant_name] Checkouts",
            switchSavegoPayCheckout = "By enabling goPay, your mobile number will be saved with Tap Payments to get faster and more secure checkouts in multiple apps and websites.",
            savegoPayText = "Save for goPay Checkouts",
            alertgoPaySignup = "Please check your email or SMS’s in order to complete the goPay Checkout signup process."
        )
    }

    private fun configureSwitch() {

        saveCardSwitchHolder.view.mainSwitch.switchSaveMobile?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                saveCardSwitchHolder.view.cardSwitch.tapCardSwitchLinear.setBackgroundColor(Color.parseColor(ThemeManager.getValue("TapSwitchView.backgroundColor")))
              //  saveCardSwitchHolder.view.cardSwitch.cardElevation = 2.5f
                 saveCardSwitchHolder.view.cardSwitch.saveSwitchChip.cardElevation = 2.5f

                saveCardSwitchHolder.view.cardSwitch.payButton.stateListAnimator = null
                saveCardSwitchHolder.view.cardSwitch.payButton.setButtonDataSource(
                    true,
                  "en",
                    "Pay",
                    Color.parseColor(ThemeManager.getValue("actionButton.Valid.paymentBackgroundColor")),
                    Color.parseColor(ThemeManager.getValue("actionButton.Valid.titleLabelColor"))
                )
                saveCardSwitchHolder.view.cardSwitch.switchesLayout?.visibility = View.VISIBLE
                saveCardSwitchHolder.view.cardSwitch.switchSaveMerchant?.visibility = View.VISIBLE
                saveCardSwitchHolder.view.cardSwitch.switchSaveMerchant?.isChecked = true
                saveCardSwitchHolder.view.cardSwitch.switchGoPayCheckout?.isChecked = true
                saveCardSwitchHolder.view.cardSwitch.switchGoPayCheckout?.visibility = View.VISIBLE
                saveCardSwitchHolder.view.cardSwitch.saveGoPay?.visibility = View.VISIBLE
                saveCardSwitchHolder.view.cardSwitch.alertGoPaySignUp?.visibility = View.VISIBLE
                saveCardSwitchHolder.view.cardSwitch.switchSeparator?.visibility = View.VISIBLE
            } else {
                saveCardSwitchHolder.view.cardSwitch.tapCardSwitchLinear.setBackgroundColor(Color.parseColor(ThemeManager.getValue("TapSwitchView.main.backgroundColor")))
                saveCardSwitchHolder.view.cardSwitch.saveSwitchChip.cardElevation = 0f

                saveCardSwitchHolder.view.cardSwitch.payButton.stateListAnimator = null
                saveCardSwitchHolder.view.cardSwitch.payButton.setButtonDataSource(
                    false,
                    "en" ,
                    "Pay",
                    Color.parseColor(ThemeManager.getValue("actionButton.Invalid.backgroundColor")),
                    Color.parseColor(ThemeManager.getValue("actionButton.Invalid.titleLabelColor"))
                )

                saveCardSwitchHolder.view.cardSwitch.switchesLayout?.visibility = View.GONE
                saveCardSwitchHolder.view.cardSwitch.switchSaveMerchant?.visibility = View.GONE
                saveCardSwitchHolder.view.cardSwitch.switchSaveMerchant?.isChecked = false
                saveCardSwitchHolder.view.cardSwitch.switchGoPayCheckout?.isChecked = false
                saveCardSwitchHolder.view.cardSwitch.switchGoPayCheckout?.visibility = View.GONE
                saveCardSwitchHolder.view.cardSwitch.saveGoPay?.visibility = View.GONE
                saveCardSwitchHolder.view.cardSwitch.alertGoPaySignUp?.visibility = View.GONE
                saveCardSwitchHolder.view.cardSwitch.switchSeparator?.visibility = View.GONE
            }
        }

    }
    private fun stopShakingCards(chipsView: RecyclerView) {
        chipsView.adapter = CardTypeAdapter(savedcardList as ArrayList<SavedCards>, this, false)
    }

}


