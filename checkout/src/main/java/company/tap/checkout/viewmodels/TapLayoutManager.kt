package company.tap.checkout.viewmodels

import android.content.Context
import android.widget.FrameLayout
import android.widget.LinearLayout

import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModel
import androidx.transition.*
import company.tap.checkout.apiresponse.DummyResponse
import company.tap.checkout.enums.SectionType
import company.tap.checkout.interfaces.BaseLayoutManager
import company.tap.checkout.interfaces.OnCardSelectedActionListener
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
    BaseLayoutManager, OnCardSelectedActionListener {

    private lateinit var context: Context
    private lateinit var fragmentManager: FragmentManager
    private lateinit var sdkLayout: LinearLayout
    private lateinit var bottomSheetLayout: FrameLayout
    private lateinit var businessViewHolder: BusinessViewHolder
    private lateinit var amountViewHolder: AmountViewHolder
    private lateinit var itemsViewHolder: ItemsViewHolder
    private lateinit var cardViewHolder: CardViewHolder
    private lateinit var paymentInputViewHolder: PaymentInputViewHolder
    private lateinit var saveCardSwitch: SwitchViewHolder
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
        paymentInputViewHolder = PaymentInputViewHolder(context)
        saveCardSwitch = SwitchViewHolder(context)
        tabAnimatedActionButtonViewHolder = TabAnimatedActionButtonViewHolder(context)
        initAmountAction()
        initCardsGroup()
    }

    private fun initAmountAction() {
        amountViewHolder.setOnItemsClickListener {
            if (!this::itemsViewHolder.isInitialized)
                itemsViewHolder = ItemsViewHolder(context)
            controlCurrency(itemsViewHolder.displayed)
        }
    }

    private fun initCardsGroup() {
        cardViewHolder.view.groupAction.setOnClickListener {
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
            saveCardSwitch,
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
        removeViews(cardViewHolder, paymentInputViewHolder, saveCardSwitch)
        addViews(goPayViewHolder)
    }

    override fun controlCurrency(display: Boolean) {
        TransitionManager.beginDelayedTransition(bottomSheetLayout)
        if (display) {
            addViews(itemsViewHolder)
            removeViews(
                cardViewHolder,
                paymentInputViewHolder,
                saveCardSwitch,
                tabAnimatedActionButtonViewHolder
            )
        } else {
            removeViews(itemsViewHolder)
            addViews(
                cardViewHolder,
                paymentInputViewHolder,
                saveCardSwitch,
                tabAnimatedActionButtonViewHolder
            )
        }
        itemsViewHolder.displayed = !display
        amountViewHolder.changeGroupAction(!display)
    }

    override fun displayOTP() {}

    override fun displayRedirect(url: String) {}

    override fun displaySaveCardOptions() {}

    override fun getDatafromAPI(dummyInitapiResponse: DummyResponse) {
        println("dummy response value is ${dummyInitapiResponse?.data?.merchant?.logo}")
        businessViewHolder.setDatafromAPI(dummyInitapiResponse?.data?.merchant?.logo,dummyInitapiResponse?.data?.merchant.name)

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


}