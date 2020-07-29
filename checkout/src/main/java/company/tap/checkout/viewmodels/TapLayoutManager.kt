package company.tap.checkout.viewmodels

import android.content.Context
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModel
import androidx.transition.TransitionManager
import company.tap.checkout.enums.SectionType
import company.tap.checkout.interfaces.BaseLayoutManager
import company.tap.checkout.viewholders.*
import company.tap.tapuilibrary.datasource.AmountViewDataSource

/**
 *
 * Created by Mario Gamal on 6/14/20
 * Copyright © 2020 Tap Payments. All rights reserved.
 *
 */
class TapLayoutManager : ViewModel(),
    BaseLayoutManager {

    private lateinit var context: Context
    private lateinit var fragmentManager: FragmentManager
    private lateinit var sdkLayout: LinearLayout
    private lateinit var bottomSheetLayout: FrameLayout
    private lateinit var businessViewHolder: BusinessViewHolder
    private lateinit var amountViewHolder: AmountViewHolder
    private lateinit var itemsViewHolder: ItemsViewHolder
    private lateinit var cardViewHolder: CardViewHolder
    private lateinit var paymentInputViewHolder: PaymentInputViewHolder

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
        cardViewHolder = CardViewHolder(context)
        paymentInputViewHolder = PaymentInputViewHolder(context)
        initAmountAction()
    }

    private fun initAmountAction() {
        amountViewHolder.setOnItemsClickListener {
            if (!this::itemsViewHolder.isInitialized)
                itemsViewHolder = ItemsViewHolder(context)
            controlCurrency(itemsViewHolder.displayed)
        }
    }

    override fun displayStartupLayout(enabledSections: ArrayList<SectionType>) {
        sdkLayout.addView(businessViewHolder.view)
        sdkLayout.addView(amountViewHolder.view)
        sdkLayout.addView(cardViewHolder.view)
        sdkLayout.addView(paymentInputViewHolder.view)
    }

    fun setBottomSheetLayout(bottomSheetLayout: FrameLayout) {
        this.bottomSheetLayout = bottomSheetLayout
    }

    override fun displayGoPayLogin() {
        TODO("Not yet implemented")
    }

    override fun controlCurrency(display: Boolean) {
        TransitionManager.beginDelayedTransition(bottomSheetLayout)
        if (display)
            sdkLayout.addView(itemsViewHolder.view)
        else
            sdkLayout.removeView(itemsViewHolder.view)
        itemsViewHolder.displayed = !display
        amountViewHolder.changeGroupAction(!display)
    }


    override fun displayOTP() {
        TODO("Not yet implemented")
    }

    override fun displayRedirect(url: String) {
        TODO("Not yet implemented")
    }

    override fun displaySaveCardOptions() {
        TODO("Not yet implemented")
    }
}