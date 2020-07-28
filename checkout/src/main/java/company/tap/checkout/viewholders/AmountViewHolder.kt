package company.tap.checkout.viewholders

import android.content.Context
import company.tap.checkout.R
import company.tap.checkout.enums.SectionType
import company.tap.tapuilibrary.atoms.TapButton
import company.tap.tapuilibrary.datasource.AmountViewDataSource
import company.tap.tapuilibrary.views.TapAmountSectionView

/**
 *
 * Created by Mario Gamal on 7/22/20
 * Copyright © 2020 Tap Payments. All rights reserved.
 *
 */
class AmountViewHolder(context: Context) : TapBaseViewHolder {

    override val view = TapAmountSectionView(context, null)

    override val type = SectionType.AMOUNT_ITEMS

    init {
        bindViewComponents()
    }

    override fun bindViewComponents() {
        changeDataSource(
            AmountViewDataSource(
                selectedCurr = "SR1000,000.000",
                currentCurr = "KD1000,000.000",
                itemCount = "22 ITEMS"
            )
        )
    }

    fun changeDataSource(amountViewDataSource: AmountViewDataSource) {
        view.setAmountViewDataSource(amountViewDataSource)
    }

    fun changeGroupAction(isOpen: Boolean) {
        if (isOpen)
            changeDataSource(
                AmountViewDataSource(
                    selectedCurr = "SR1000,000.000",
                    currentCurr = "KD1000,000.000",
                    itemCount = "22 ITEMS"
                )
            )
        else
            changeDataSource(
                AmountViewDataSource(
                    selectedCurr = "SR1000,000.000",
                    currentCurr = "KD1000,000.000",
                    itemCount = "Close"
                )
            )
    }

    fun setOnItemsClickListener(onItemsClickListener: () -> Unit) {
        view.findViewById<TapButton>(R.id.textView_itemcount).setOnClickListener {
            onItemsClickListener()
        }
    }
}