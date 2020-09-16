package company.tap.checkout.viewmodels

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout

import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModel
import androidx.transition.*
import company.tap.cardbusinesskit.testmodels.DummyResp

import company.tap.checkout.enums.SectionType
import company.tap.checkout.interfaces.BaseLayoutManager
import company.tap.checkout.interfaces.OnCardSelectedActionListener
import company.tap.checkout.interfaces.onPaymentCardComplete
import company.tap.checkout.utils.AnimationEngine
import company.tap.checkout.utils.AnimationEngine.Type.*
import company.tap.checkout.viewholders.*
import kotlinx.android.synthetic.main.action_button_animation.view.*

/**
 *
 * Created by Mario Gamal on 6/14/20
 * Copyright © 2020 Tap Payments. All rights reserved.
 *
 */
class TapLayoutManager : ViewModel(),
    BaseLayoutManager, OnCardSelectedActionListener , onPaymentCardComplete {

    private lateinit var context: Context
    private lateinit var fragmentManager: FragmentManager
    private lateinit var sdkLayout: LinearLayout
    private lateinit var bottomSheetLayout: FrameLayout
    private lateinit var businessViewHolder: BusinessViewHolder
    private lateinit var amountViewHolder: AmountViewHolder
    private lateinit var itemsViewHolder: ItemsViewHolder
    private lateinit var cardViewHolder: CardViewHolder
    private lateinit var paymentInputViewHolder: PaymentInputViewHolder
    private lateinit var saveCardSwitchHolder: SwitchViewHolder
    private lateinit var goPayViewHolder: GoPayViewHolder
    private lateinit var tabAnimatedActionButtonViewHolder: TabAnimatedActionButtonViewHolder

    fun initLayoutManager(
        context: Context,
        fragmentManager: FragmentManager,
        sdkLayout: LinearLayout
    ) {
        this.context = context
        this.fragmentManager = fragmentManager
        this.sdkLayout = sdkLayout
        businessViewHolder =  BusinessViewHolder(context)
        amountViewHolder = AmountViewHolder(context)
        cardViewHolder = CardViewHolder(context, this)
        paymentInputViewHolder = PaymentInputViewHolder(context,this)
        saveCardSwitchHolder = SwitchViewHolder(context)
        itemsViewHolder = ItemsViewHolder(context)
        tabAnimatedActionButtonViewHolder = TabAnimatedActionButtonViewHolder(context)
        initAmountAction()
        initCardsGroup()
        initSwitchAction()
    }

    private fun initSwitchAction() {
       saveCardSwitchHolder.view.switchSaveMobile.visibility = View.GONE
    }

    private fun initAmountAction() {
        amountViewHolder.setOnItemsClickListener {
            if (!this::itemsViewHolder.isInitialized)
                itemsViewHolder = ItemsViewHolder(context)
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
            saveCardSwitchHolder,
            tabAnimatedActionButtonViewHolder
        )
    }

    private fun NMNM(){
        print("A7la nmnm dah kda kda")
    }

    fun setBottomSheetLayout(bottomSheetLayout: FrameLayout) {
        this.bottomSheetLayout = bottomSheetLayout
    }

    override fun displayGoPayLogin() {
        AnimationEngine.applyTransition(bottomSheetLayout, SLIDE)
        goPayViewHolder = GoPayViewHolder(context, bottomSheetLayout)
        removeViews(cardViewHolder, paymentInputViewHolder, saveCardSwitchHolder)
        addViews(goPayViewHolder)
    }

    override fun controlCurrency(display: Boolean) {
        TransitionManager.beginDelayedTransition(bottomSheetLayout)
        if (display) {
            addViews(itemsViewHolder)
            removeViews(
                cardViewHolder,
                paymentInputViewHolder,
                saveCardSwitchHolder,
                tabAnimatedActionButtonViewHolder
            )
        } else {
            removeViews(itemsViewHolder)
            addViews(
                cardViewHolder,
                paymentInputViewHolder,
                saveCardSwitchHolder,
                tabAnimatedActionButtonViewHolder
            )
        }
        itemsViewHolder.displayed = !display
        amountViewHolder.changeGroupAction(!display)
    }

    override fun displayOTP() {}

    override fun displayRedirect(url: String) {}

    override fun displaySaveCardOptions() {}

    override fun getDatafromAPI(dummyInitapiResponse: DummyResp) {
        println("dummy response value is ${dummyInitapiResponse}")
        businessViewHolder.setDatafromAPI(dummyInitapiResponse.merchant.logo,dummyInitapiResponse.merchant.name)
        amountViewHolder.setDatafromAPI(dummyInitapiResponse.order.original_amount.toString(),dummyInitapiResponse.order.trx_currency,dummyInitapiResponse.order.trx_currency,dummyInitapiResponse?.order.items.size.toString())
        cardViewHolder.setDatafromAPI(dummyInitapiResponse.payment_methods)
        paymentInputViewHolder.setDatafromAPI(dummyInitapiResponse.payment_methods.get(0).image)
        saveCardSwitchHolder.setDatafromAPI(dummyInitapiResponse.merchant.name,paymentInputViewHolder.selectedType)
        itemsViewHolder.setDatafromAPI(dummyInitapiResponse.payment_methods.get(0).supported_currencies)
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

    private fun activateActionButton(){
        tabAnimatedActionButtonViewHolder.activateButton()
    }

    private fun unActivateActionButton(){
        tabAnimatedActionButtonViewHolder.bindViewComponents()
        tabAnimatedActionButtonViewHolder.view.actionButton.isClickable = false
    }

    override fun onCardSelectedAction(isSelected: Boolean) {
        if (isSelected){
            activateActionButton()
            tabAnimatedActionButtonViewHolder.view.actionButton.setOnClickListener { tabAnimatedActionButtonViewHolder.setOnClickAction() }
        }
        else unActivateActionButton()
    }

    override fun onPaycardSwitchAction(isCompleted: Boolean) {
        if(isCompleted){
            saveCardSwitchHolder.view.switchSaveMerchant.isChecked = true
            saveCardSwitchHolder.view.switchGoPayCheckout.isChecked = true
            saveCardSwitchHolder.view.switchSaveMobile.isChecked = true
            saveCardSwitchHolder.view.switchesLayout.visibility = View.VISIBLE
            saveCardSwitchHolder.view.switchSaveMobile.visibility = View.VISIBLE

            activateActionButton()
        }else {
            saveCardSwitchHolder.view.switchSaveMerchant.isChecked = false
            saveCardSwitchHolder.view.switchGoPayCheckout.isChecked = false
            saveCardSwitchHolder.view.switchSaveMobile.isChecked = false
            saveCardSwitchHolder.view.switchSaveMobile.visibility = View.GONE
            saveCardSwitchHolder.view.switchesLayout.visibility = View.GONE
            unActivateActionButton()

        }

    }


}