package company.tap.checkout.internal.viewholders

import android.content.Context
import android.view.LayoutInflater
import android.view.View

import company.tap.checkout.R
import company.tap.checkout.internal.enums.SectionType
import company.tap.checkout.internal.utils.CurrencyFormatter
import company.tap.checkout.internal.utils.CurrentTheme
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapuilibrary.uikit.atoms.TapButton
import company.tap.tapuilibrary.uikit.datasource.AmountViewDataSource
import company.tap.tapuilibrary.uikit.views.TapAmountSectionView
import kotlinx.android.synthetic.main.amountview_layout.view.*


/**
 *
 * Created by Mario Gamal on 7/22/20
 * Copyright © 2020 Tap Payments. All rights reserved.
 *
 */
class AmountViewHolder1(context: Context) : TapBaseViewHolder {

  // override val view = TapAmountSectionView(context, null)
    override val view: View = LayoutInflater.from(context).inflate(R.layout.amountview_layout, null)

    override val type = SectionType.AMOUNT_ITEMS

    private var selectedCurren:String?=null
    private var currentCurren:String?=null
    private var itemCountt :String?=null
    private var originalAmount :String=""
    init {
        bindViewComponents()
        CurrentTheme.initAppTheme(R.raw.defaultlighttheme, context)


    }

    override fun bindViewComponents() {
        view.amount_section.setAmountViewDataSource(getAmountDataSourceFromAPIs())
    }

    private fun getAmountDataSourceFromAPIs(): AmountViewDataSource {
        return AmountViewDataSource(
            selectedCurr = selectedCurren +" "+ originalAmount,
            currentCurr = currentCurren,
            itemCount = itemCountt  + "  "+ LocalizationManager.getValue("items","Common")
        )
    }

    fun changeDataSource(amountViewDataSource: AmountViewDataSource) {
        view.amount_section.setAmountViewDataSource(amountViewDataSource)
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
        originalAmount =CurrencyFormatter.currencyFormat(originalAmountApi)

        bindViewComponents()
    }
}