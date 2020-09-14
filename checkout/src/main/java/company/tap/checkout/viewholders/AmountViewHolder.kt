package company.tap.checkout.viewholders

import android.content.Context

import company.tap.checkout.R
import company.tap.checkout.enums.SectionType
import company.tap.checkout.utils.CurrentTheme
import company.tap.taplocalizationkit.LocalizationManager
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

    private var selectedCurren:String?=null
    private var currentCurren:String?=null
    private var itemCountt :String?=null
    private var originalAmount :String?=null
    init {
        bindViewComponents()
        CurrentTheme.initAppTheme(R.raw.defaultlighttheme, context)


    }

    override fun bindViewComponents() {
        view.setAmountViewDataSource(getAmountDataSourceFromAPIs())
    }

    private fun getAmountDataSourceFromAPIs(): AmountViewDataSource {
        return AmountViewDataSource(
            selectedCurr = selectedCurren +" "+ originalAmount,
            currentCurr = currentCurren,
            itemCount = itemCountt  + "  "+ LocalizationManager.getValue("items","Common")
        )
    }

    fun changeDataSource(amountViewDataSource: AmountViewDataSource) {
        view.setAmountViewDataSource(amountViewDataSource)
    }

    fun changeGroupAction(isOpen: Boolean) {
        if (isOpen)
            changeDataSource(
                AmountViewDataSource(
                    selectedCurr = selectedCurren +" "+  originalAmount,
                    currentCurr = currentCurren,
                    itemCount = itemCountt + "  "+ LocalizationManager.getValue("items","Common")
                )
            )
        else
            changeDataSource(
                AmountViewDataSource(
                    selectedCurr = selectedCurren +" "+ originalAmount,
                    currentCurr = currentCurren,
                    itemCount = LocalizationManager.getValue("close","Common")
                )
            )
    }


    fun setOnItemsClickListener(onItemsClickListener: () -> Unit) {
        view.findViewById<TapButton>(R.id.textView_itemcount).setOnClickListener {
            onItemsClickListener()
        }
    }


    /**
     * Sets data from API through LayoutManager
     * @param selectedCurrencyApi represents the currency set by the Merchant.
     * @param currentCurrencyApi represents the currency which by default.
     * @param itemCountApi represents the itemsCount for the Merchant.
     * @param originalAmountApi represents the default amount from API.
     * */
    fun setDatafromAPI(originalAmountApi:String,selectedCurrencyApi :String ,currentCurrencyApi :String, itemCountApi:String){
        selectedCurren = selectedCurrencyApi
        currentCurren = currentCurrencyApi
        itemCountt  = itemCountApi
        originalAmount = originalAmountApi
        bindViewComponents()
    }
}