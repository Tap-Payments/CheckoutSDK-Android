package company.tap.checkout.internal.viewmodels



import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ShapeDrawable
import android.transition.TransitionManager
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.*
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Slide

import company.tap.checkout.R
import company.tap.checkout.internal.dummygener.Currencies1
import company.tap.checkout.internal.dummygener.Items1
import company.tap.checkout.internal.dummygener.JsonResponseDummy1
import company.tap.checkout.internal.dummygener.SavedCards
import company.tap.checkout.internal.enums.SectionType
import company.tap.checkout.internal.interfaces.*
import company.tap.checkout.internal.viewholders.*
import company.tap.tapuilibrary.themekit.ThemeManager
import company.tap.tapuilibrary.uikit.adapters.CardTypeAdapterUIKIT
import company.tap.tapuilibrary.uikit.animation.AnimationEngine
import company.tap.tapuilibrary.uikit.datasource.TapSwitchDataSource
import company.tap.tapuilibrary.uikit.fragment.CardScannerFragment
import company.tap.tapuilibrary.uikit.fragment.CurrencyViewsFragment
import company.tap.tapuilibrary.uikit.fragment.NFCFragment
import company.tap.tapuilibrary.uikit.interfaces.OnCardSelectedActionListener
import kotlinx.android.synthetic.main.amountview_layout.view.*

import kotlinx.android.synthetic.main.cardviewholder_layout.view.*
import kotlinx.android.synthetic.main.switch_layout.view.*
import java.util.*


/**
 *
 * Created by Mario Gamal on 6/14/20
 * Copyright © 2020 Tap Payments. All rights reserved.
 *
 */
class TapLayoutViewModell : ViewModel(),
    BaseLayoutManager, OnCardSelectedActionListener, onPaymentCardComplete, onCardNFCCallListener
    {

    private lateinit var context: Context
    private lateinit var fragmentManager: FragmentManager
    private lateinit var sdkLayout: LinearLayout
    private lateinit var bottomSheetLayout: FrameLayout
    private lateinit var businessViewHolder: BusinessViewHolder
    private lateinit var amountViewHolder1: AmountViewHolder1
    private lateinit var itemsViewHolder1: ItemsViewHolder1
    private lateinit var cardViewHolder11: CardViewHolder11
  //  private lateinit var goPayLoginHolder1: GoPayLoginHolder1
  //  private lateinit var paymentInputViewHolder1: PaymentInputViewHolder1
    private lateinit var paymentInputViewHolder11: PaymentInputViewHolder11
  //  private lateinit var saveCardSwitchHolder1: SwitchViewHolder1
    private lateinit var saveCardSwitchHolder11: SwitchViewHolder11
    private lateinit var goPayViewHolder1: GoPayViewHolder1
    private lateinit var otpViewHolder: OTPViewHolder
 private lateinit var goPaySavedCardHolder: GoPaySavedCardHolder
    private lateinit var allCurrencies:List<Currencies1>
    private lateinit var itemList: List<Items1>
    private lateinit var savedcardList: List<SavedCards>

    fun initLayoutManager(
        context: Context,
        fragmentManager: FragmentManager,
        sdkLayout: LinearLayout,

    ) {
        println("again called fragmentManager = [${fragmentManager}]")

        this.context = context
        this.fragmentManager = fragmentManager
        this.sdkLayout = sdkLayout
        businessViewHolder = BusinessViewHolder(context)
      //  amountViewHolder = AmountViewHolder(context)
        amountViewHolder1 = AmountViewHolder1(context)
        initAmountAction()

        cardViewHolder11 = CardViewHolder11(context,this)

       // goPayLoginHolder1 = GoPayLoginHolder1(context, this, this)
        goPaySavedCardHolder = GoPaySavedCardHolder(context, this, this)
       // paymentInputViewHolder1 = PaymentInputViewHolder1(context, this, this)
        paymentInputViewHolder11 = PaymentInputViewHolder11(context, this, this)
       // saveCardSwitchHolder1 = SwitchViewHolder1(context)
        saveCardSwitchHolder11 = SwitchViewHolder11(context)
       // itemsViewHolder = ItemsViewHolder(context, this)
        itemsViewHolder1 = ItemsViewHolder1(context, this)

        otpViewHolder = OTPViewHolder(context)

        goPayViewHolder1= GoPayViewHolder1(context, this)
       // println("context = [${context}], fragmentManager = [${fragmentManager}], dummyInitApiResponse11 = [${dummyInitApiResponse11}]")
        initCardsGroup()
        initSwitchAction()
     //   if (dummyInitapiResponse1 != null) {
         //  getDatafromAPI(dummyInitApiResponse11)
     //   }
      //  displayStartupLayout(enabledSections)
      //  otpViewHolder.otpView?.setOTPInterface(this)
       // goPayViewHolder.goPayLoginInput?.setOpenOTPInterface(this)

    }

    private fun initSwitchAction() {
        saveCardSwitchHolder11.view.mainSwitch.visibility = View.VISIBLE
        saveCardSwitchHolder11.view.mainSwitch.switchSaveMobile.visibility = View.GONE
      //  saveCardSwitchHolder.view.cardSwitch.switchSaveMobile.visibility = View.GONE
    }

    private fun initAmountAction() {
        amountViewHolder1.view.amount_section?.itemCount?.setOnClickListener {
            controlCurrency(itemsViewHolder1.displayed)
            println("amount clicked")

        }
    }

    private fun initCardsGroup() {
       // cardViewHolder = CardViewHolder(context,this )

        cardViewHolder11.view.setOnClickListener {
           // displayGoPayLogin()
           println("init caleed ")
        }
    }

    override fun displayStartupLayout(enabledSections: ArrayList<SectionType>) {
        print("is displayStartupLayout")
        //Todo based on api response logic for swicth case

        addViews(
                businessViewHolder,
            amountViewHolder1,
            cardViewHolder11,
            paymentInputViewHolder11,
            saveCardSwitchHolder11
        )

        saveCardSwitchHolder11.view.cardSwitch.visibility = View.VISIBLE
        saveCardSwitchHolder11.view.cardSwitch.payButton.visibility = View.VISIBLE
        saveCardSwitchHolder11.view.mainSwitch.mainSwitchLinear.setBackgroundColor(
                Color.parseColor(
                        ThemeManager.getValue(
                                "TapSwitchView.main.backgroundColor"
                        )
                )
        )

    }



    fun setBottomSheetLayout(bottomSheetLayout: FrameLayout) {
        this.bottomSheetLayout = bottomSheetLayout
    }

    override fun displayGoPayLogin() {
        println("goPay Login reached")
        if (this::bottomSheetLayout.isInitialized) {
            AnimationEngine.applyTransition(bottomSheetLayout,

            )
        }
        removeViews(
            businessViewHolder,
            amountViewHolder1,
            cardViewHolder11,
            paymentInputViewHolder11,
            saveCardSwitchHolder11,
        )
       // if (this::bottomSheetLayout.isInitialized)
       // addViews(goPayViewHolder)
        addViews( businessViewHolder,
            amountViewHolder1,goPayViewHolder1)


    }

    override fun displayGoPay() {

        removeViews(
            businessViewHolder,
            amountViewHolder1,
            goPaySavedCardHolder,
            cardViewHolder11,
            paymentInputViewHolder11,
            saveCardSwitchHolder11
        )

        addViews(
            businessViewHolder,
            amountViewHolder1,
            goPaySavedCardHolder,
            cardViewHolder11,
            paymentInputViewHolder11,
            saveCardSwitchHolder11
        )

        cardViewHolder11.view.mainChipgroup.groupAction.text = ""
        saveCardSwitchHolder11.view.cardSwitch.payButton.visibility = View.VISIBLE

    }

    override fun controlCurrency(display: Boolean) {
        val manager: FragmentManager = fragmentManager
        val transaction = manager.beginTransaction()
      if (this::bottomSheetLayout.isInitialized)
           TransitionManager.beginDelayedTransition(bottomSheetLayout)
        if (display) {

            println("allCurrencies inside"+allCurrencies)
            // itemsViewHolder.setDatafromAPI(CurrecnyLists as ArrayList<Currencies1>,itemList,fragmentManager)

            if (allCurrencies.isNotEmpty()) {
                transaction.add(
                    R.id.sdkContainer,
                    CurrencyViewsFragment(allCurrencies  as ArrayList<Currencies1>, itemList)
                )
                transaction.addToBackStack(null)
                transaction.commit()
            //   addViews(itemsViewHolder)
                itemsViewHolder1.displayed = false


          }
            removeViews(
                cardViewHolder11,
                paymentInputViewHolder11,
                saveCardSwitchHolder11
            )

          }

        else {
            removeViews(businessViewHolder, amountViewHolder1, itemsViewHolder1)


          //  supportedCurrecnyList.clear()
          //  manager.beginTransaction().replace(R.id.sdkContainer, TapCheckoutFragment()).commit()

           // transaction.addToBackStack(null)
          //  transaction.commit()


        }


        itemsViewHolder1.displayed = !display
        amountViewHolder1.changeGroupAction(!display)

    }

    override fun displayOTP(otpMobile: String) {
        removeViews(businessViewHolder,amountViewHolder1,otpViewHolder)
        addViews(businessViewHolder,amountViewHolder1,otpViewHolder)
        println("display OTP is called" + otpMobile)
        otpViewHolder.otpView.visibility = View.VISIBLE
        otpViewHolder.otpView.mobileNumberText.text=otpMobile
        otpViewHolder.otpView.changePhone.setOnClickListener {
            goPayViewHolder1.onChangePhoneClicked()

       }

    }

    override fun displayRedirect(url: String) {

    }

    override fun displaySaveCardOptions() {

    }



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

        cardViewHolder11.setDatafromAPI(dummyInitapiResponse1.savedCards)
        goPaySavedCardHolder.setDatafromAPI(dummyInitapiResponse1.goPaySavedCards)
        println("dummy tapCardPhoneListDataSource" + dummyInitapiResponse1.tapCardPhoneListDataSource)
        paymentInputViewHolder11.setDatafromAPI(dummyInitapiResponse1.tapCardPhoneListDataSource)


        saveCardSwitchHolder11.setDatafromAPI(
            dummyInitapiResponse1.merchant1.name,
            paymentInputViewHolder11.selectedType
        )

        allCurrencies = dummyInitapiResponse1.currencies1
        itemList = dummyInitapiResponse1.order1.items
        savedcardList = dummyInitapiResponse1.savedCards
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



        cardViewHolder11.view.mainChipgroup.chipsRecycler.adapter = CardTypeAdapterUIKIT(
                savedcardList as ArrayList<SavedCards>,
                this,
                false
        )

        cardViewHolder11.view.mainChipgroup.groupAction?.visibility = View.VISIBLE

        cardViewHolder11.view.mainChipgroup.groupAction?.setOnClickListener {
            if (cardViewHolder11.view.mainChipgroup.groupAction?.text == "Close"){
                cardViewHolder11.view.mainChipgroup.chipsRecycler.adapter =  CardTypeAdapterUIKIT(
                        savedcardList as ArrayList<SavedCards>,
                        this,
                        false
                )
                cardViewHolder11.view.mainChipgroup.groupAction?.text = "Edit"

            }else{
                cardViewHolder11.view.mainChipgroup.chipsRecycler.adapter = CardTypeAdapterUIKIT(
                        savedcardList as ArrayList<SavedCards>,
                        this,
                        true
                )
                cardViewHolder11.view.mainChipgroup.groupAction?.text = "Close"
            }

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
      //  tabAnimatedActionButtonViewHolder11.bindViewComponents()
      //  tabAnimatedActionButtonViewHolder11.view.actionButton.isClickable = false
    }


    override fun onCardSelectedAction(isSelected: Boolean, typeCardView: String) {
        println("onCardSelectedAction called"+isSelected)
        if (isSelected) {
           if(typeCardView == "3"){
               displayGoPayLogin()
           }
            // activateActionButton()
            //  tabAnimatedActionButtonViewHolder11.view.actionButton.setOnClickListener { tabAnimatedActionButtonViewHolder11.setOnClickAction() }
        } else unActivateActionButton()
    }

    override fun onDeleteIconClicked(stopAnimation: Boolean, itemId: Int) {
        println("onDeleteIconClicked is onDeleteIconClicked")
       if (stopAnimation){
            stopShakingCards(cardViewHolder11.view.mainChipgroup.chipsRecycler)
           cardViewHolder11.view.mainChipgroup.groupAction?.text = "Edit"
        }else cardViewHolder11.view.mainChipgroup.groupAction?.text = "Close"
    }

    override fun onPaycardSwitchAction(isCompleted: Boolean) {
        saveCardSwitchHolder11.view.cardSwitch.setSwitchDataSource(getSwitchDataSource("fdsg"))

        if (isCompleted) {
            saveCardSwitchHolder11.view.mainSwitch.visibility = View.VISIBLE
            saveCardSwitchHolder11.view.mainSwitch.switchSaveMobile.visibility = View.VISIBLE
            saveCardSwitchHolder11.view.mainSwitch.mainSwitchLinear.setBackgroundColor(
                Color.parseColor(
                    ThemeManager.getValue(
                        "TapSwitchView.main.backgroundColor"
                    )
                )
            )
            saveCardSwitchHolder11.view.cardSwitch.visibility = View.VISIBLE
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
            cardViewHolder11,
            saveCardSwitchHolder11,
            paymentInputViewHolder11
        )
        val manager: FragmentManager = fragmentManager
        val transaction = manager.beginTransaction()
        transaction.add(
            R.id.sdkContainer,
            NFCFragment()
        )
        transaction.addToBackStack(null)
        transaction.commit()
        amountViewHolder1.changeGroupAction(true)


    }

    // Override function to open card Scanner and scan the card.
    override fun  onClickCardScanner() {
        println("are u reachinhg scanner")
       // cardScannerViewHolder  = CardScannerViewHolder(context)
        removeViews(
            cardViewHolder11,
            saveCardSwitchHolder11,
            paymentInputViewHolder11
        )
       val manager: FragmentManager = fragmentManager
        val transaction = manager.beginTransaction()
        transaction.add(
            R.id.sdkContainer,
            CardScannerFragment()
        )
        transaction.addToBackStack(null)
        transaction.commit()

        amountViewHolder1.changeGroupAction(true)


    }



    //Setting data to TapSwitchDataSource
    private fun getSwitchDataSource(switchText: String): TapSwitchDataSource {
        println("switch data source " + switchText)
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
                saveCardSwitchHolder11.view.cardSwitch.tapCardSwitchLinear.setBackgroundColor(
                    Color.parseColor(
                        ThemeManager.getValue(
                            "TapSwitchView.backgroundColor"
                        )
                    )
                )
              //  saveCardSwitchHolder.view.cardSwitch.cardElevation = 2.5f
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
                saveCardSwitchHolder11.view.cardSwitch.switchGoPayCheckout?.visibility = View.VISIBLE
                saveCardSwitchHolder11.view.cardSwitch.saveGoPay?.visibility = View.VISIBLE
                saveCardSwitchHolder11.view.cardSwitch.alertGoPaySignUp?.visibility = View.VISIBLE
                saveCardSwitchHolder11.view.cardSwitch.switchSeparator?.visibility = View.VISIBLE
            } else {
                saveCardSwitchHolder11.view.cardSwitch.tapCardSwitchLinear.setBackgroundColor(
                    Color.parseColor(
                        ThemeManager.getValue(
                            "TapSwitchView.main.backgroundColor"
                        )
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
    private fun stopShakingCards(chipsView: RecyclerView) {
        chipsView.adapter = CardTypeAdapterUIKIT(savedcardList as ArrayList<SavedCards>, this, false)
    }




}


