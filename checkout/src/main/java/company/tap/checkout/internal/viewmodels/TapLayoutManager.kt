package company.tap.checkout.internal.viewmodels


import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModel
import androidx.transition.TransitionManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import company.tap.cardbusinesskit.testmodels.DummyResp
import company.tap.cardbusinesskit.testmodels.Items
import company.tap.checkout.R
import company.tap.checkout.internal.enums.SectionType
import company.tap.checkout.internal.interfaces.BaseLayoutManager
import company.tap.checkout.internal.interfaces.OnCardSelectedActionListener
import company.tap.checkout.internal.interfaces.onCardNFCCallListener
import company.tap.checkout.internal.interfaces.onPaymentCardComplete
import company.tap.checkout.internal.utils.AnimationEngine
import company.tap.checkout.internal.utils.AnimationEngine.Type.SLIDE
import company.tap.checkout.internal.viewholders.*
import company.tap.checkout.open.TapCheckoutFragment
import company.tap.checkout.open.controller.SessionManager
import company.tap.tapuilibrary.uikit.datasource.GoPayLoginDataSource
import company.tap.tapuilibrary.uikit.enums.GoPayLoginMethod
import company.tap.tapuilibrary.uikit.fragment.CardScannerFragment
import company.tap.tapuilibrary.uikit.fragment.CurrencyViewFragment
import company.tap.tapuilibrary.uikit.fragment.NFCFragment
import company.tap.tapuilibrary.uikit.interfaces.GoPayLoginInterface
import company.tap.tapuilibrary.uikit.interfaces.OpenOTPInterface
import company.tap.tapuilibrary.uikit.interfaces.OtpButtonConfirmationInterface
import company.tap.tapuilibrary.uikit.interfaces.TapBottomDialogInterface
import kotlinx.android.synthetic.main.checkout_sdk_layout.view.*
import kotlinx.android.synthetic.main.otpview_layout.view.*


/**
 *
 * Created by Mario Gamal on 6/14/20
 * Copyright © 2020 Tap Payments. All rights reserved.
 *
 */
class TapLayoutManager() : ViewModel(),
    BaseLayoutManager, OnCardSelectedActionListener, onPaymentCardComplete, onCardNFCCallListener ,
     OtpButtonConfirmationInterface {

    private lateinit var context: Context
    private lateinit var fragmentManager: FragmentManager
    private lateinit var sdkLayout: LinearLayout
    private lateinit var bottomSheetLayout: FrameLayout
    private lateinit var businessViewHolder: BusinessViewHolder
    private lateinit var cardScannerViewHolder: CardScannerViewHolder
    private lateinit var amountViewHolder: AmountViewHolder
    private lateinit var itemsViewHolder: ItemsViewHolder
    private lateinit var cardViewHolder: CardViewHolder
    private lateinit var paymentInputViewHolder: PaymentInputViewHolder
    private lateinit var saveCardSwitchHolder: SwitchViewHolder
    private lateinit var goPayViewHolder: GoPayViewHolder
    private lateinit var otpViewHolder: OTPViewHolder
    private lateinit var supportedCurrecnyList: ArrayList<String>
    private lateinit var itemList: List<Items>

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
        paymentInputViewHolder = PaymentInputViewHolder(context, this, this)
        saveCardSwitchHolder = SwitchViewHolder(context)
        itemsViewHolder = ItemsViewHolder(context, this)

        otpViewHolder = OTPViewHolder(context)

       goPayViewHolder= GoPayViewHolder(context,this)
        println("context = [${context}], fragmentManager = [${fragmentManager}], fragmentManager = [${fragmentManager}]")
        initAmountAction()
        initCardsGroup()
        initSwitchAction()
            otpViewHolder.view.otpView?.setOtpButtonConfirmationInterface(this)
      //  otpViewHolder.otpView?.setOTPInterface(this)
       // goPayViewHolder.goPayLoginInput?.setOpenOTPInterface(this)

    }

    private fun initSwitchAction() {
        saveCardSwitchHolder.view.switchSaveMobile.visibility = View.GONE
    }

    private fun initAmountAction() {
        amountViewHolder.setOnItemsClickListener {
            controlCurrency(itemsViewHolder.displayed)

        }
    }

    private fun initCardsGroup() {
        cardViewHolder.view.setOnClickListener {
            displayGoPayLogin()
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


    }

    private fun NMNM() {
        print("A7la nmnm dah kda kda")
    }

    fun setBottomSheetLayout(bottomSheetLayout: FrameLayout) {
        this.bottomSheetLayout = bottomSheetLayout
    }

    override fun displayGoPayLogin() {
       // if (this::bottomSheetLayout.isInitialized)
       // AnimationEngine.applyTransition(bottomSheetLayout, SLIDE)
        removeViews(
            cardViewHolder,
            paymentInputViewHolder,
            saveCardSwitchHolder
        )
       // if (this::bottomSheetLayout.isInitialized)
        addViews(goPayViewHolder)
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
            manager.beginTransaction().replace(R.id.sdkContainer,TapCheckoutFragment()).commit()

            transaction.addToBackStack(null)
            transaction.commit()


        }


        itemsViewHolder.displayed = !display
        amountViewHolder.changeGroupAction(!display)

    }

    override fun displayOTP() {
        addViews(otpViewHolder)
        println("display OTP is called")
        otpViewHolder.otpView.visibility = View.VISIBLE


    }

    override fun displayRedirect(url: String) {}

    override fun displaySaveCardOptions() {}

    override fun getDatafromAPI(dummyInitapiResponse: DummyResp) {

        println("dummy response value is ${dummyInitapiResponse}")
        businessViewHolder.setDatafromAPI(
            dummyInitapiResponse.merchant.logo,
            dummyInitapiResponse.merchant.name
        )
        amountViewHolder.setDatafromAPI(
            dummyInitapiResponse.order.original_amount.toString(),
            dummyInitapiResponse.order.trx_currency,
            dummyInitapiResponse.order.trx_currency,
            dummyInitapiResponse.order.items.size.toString()
        )
        cardViewHolder.setDatafromAPI(dummyInitapiResponse.payment_methods)
        paymentInputViewHolder.setDatafromAPI(dummyInitapiResponse.payment_methods.get(0).image)
        saveCardSwitchHolder.setDatafromAPI(
            dummyInitapiResponse.merchant.name,
            paymentInputViewHolder.selectedType
        )
        supportedCurrecnyList = dummyInitapiResponse.payment_methods.get(0).supported_currencies
        itemList = dummyInitapiResponse.order.items
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

    override fun onPaycardSwitchAction(isCompleted: Boolean) {
        if (isCompleted) {
            saveCardSwitchHolder.view.switchSaveMerchant.isChecked = true
            saveCardSwitchHolder.view.switchGoPayCheckout.isChecked = true
            saveCardSwitchHolder.view.switchSaveMobile.isChecked = true
            saveCardSwitchHolder.view.switchesLayout.visibility = View.VISIBLE
            saveCardSwitchHolder.view.switchSaveMobile.visibility = View.VISIBLE
            activateActionButton()
        } else {
            saveCardSwitchHolder.view.switchSaveMerchant.isChecked = false
            saveCardSwitchHolder.view.switchGoPayCheckout.isChecked = false
            saveCardSwitchHolder.view.switchSaveMobile.isChecked = false
            saveCardSwitchHolder.view.switchSaveMobile.visibility = View.GONE
            saveCardSwitchHolder.view.switchesLayout.visibility = View.GONE
            unActivateActionButton()

        }

    }

    // Override function to open NFC fragment and scan the card via NFC.
    override fun onClickNFC() {
        removeViews(
            businessViewHolder,
            amountViewHolder,
            cardViewHolder,
            saveCardSwitchHolder,
            paymentInputViewHolder

        )
        val manager: FragmentManager = fragmentManager
        val transaction = manager.beginTransaction()
        transaction.replace(R.id.fragment_container_nfc_lib, NFCFragment())
        transaction.addToBackStack(null)
        transaction.commit()

    }

    // Override function to open card Scanner and scan the card.
    override fun  onClickCardScanner() {
        println("are u reachinhg scanner")
       // cardScannerViewHolder  = CardScannerViewHolder(context)
       val manager: FragmentManager = fragmentManager
        val transaction = manager.beginTransaction()
        transaction.replace(R.id.fragment_container_card_lib, CardScannerFragment())
        transaction.addToBackStack(null)
        transaction.commit()
        removeViews(
            businessViewHolder,
            amountViewHolder,
            cardViewHolder,
            saveCardSwitchHolder,
            paymentInputViewHolder
        )

    }



    override fun onOtpButtonConfirmationClick(otpNumber: String): Boolean {
        //TODO dummy added
        Log.d("isValidOTP" ,(otpNumber == "111111").toString() )
        return otpNumber == "111111"
    }



}


