package company.tap.checkout.internal.viewholders

import android.content.Context
import android.view.LayoutInflater
import android.view.View

import company.tap.checkout.R
import company.tap.checkout.internal.enums.SectionType
import company.tap.checkout.internal.interfaces.BaseLayouttManager
import company.tap.checkout.internal.utils.CurrencyFormatter
import company.tap.checkout.internal.utils.CurrentTheme
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapuilibrary.uikit.atoms.TapButton
import company.tap.tapuilibrary.uikit.atoms.TapTextView
import company.tap.tapuilibrary.uikit.datasource.AmountViewDataSource
import kotlinx.android.synthetic.main.amountview_layout.view.*


/**
 *
 * Copyright © 2020 Tap Payments. All rights reserved.
 *
 */
class AmountViewHolder1(context: Context,private val baseLayouttManager: BaseLayouttManager?=null) : TapBaseViewHolder {


    override val view: View = LayoutInflater.from(context).inflate(R.layout.amountview_layout, null)

    override val type = SectionType.AMOUNT_ITEMS

    private var itemCountt :String?=null
    lateinit var originalAmount :String
    private var isOpenedList :Boolean=true
    private var transactionCurrency :String?=null
    init {
       // bindViewComponents()
    }

    override fun bindViewComponents() {
        view.amount_section.setAmountViewDataSource(getAmountDataSourceFromAPIs())
    }

    private fun getAmountDataSourceFromAPIs(): AmountViewDataSource {
        return AmountViewDataSource(
            selectedCurr = originalAmount ,
            selectedCurrText = transactionCurrency,
            itemCount = itemCountt  + "  "+ LocalizationManager.getValue("items","Common")
        )
    }

    fun changeDataSource(amountViewDataSource: AmountViewDataSource) {
        view.amount_section.setAmountViewDataSource(amountViewDataSource)
    }

    fun changeGroupAction(isOpen: Boolean) {
        println("isOpen in groupaction"+isOpen)
        isOpenedList = isOpen
        if (isOpen)
            changeDataSource(
                AmountViewDataSource(
                    selectedCurr = originalAmount ,
                    selectedCurrText = transactionCurrency,
                    itemCount = itemCountt + "  "+ LocalizationManager.getValue("items","Common")))
        else
            if(::originalAmount.isInitialized)
            changeDataSource(
                AmountViewDataSource(
                    selectedCurr = originalAmount ,
                    selectedCurrText = transactionCurrency,
                    itemCount = LocalizationManager.getValue("close","Common")))
    }


    fun setOnItemsClickListener(onItemsClickListener: () -> Unit) {
        view.amount_section.itemCountButton.setOnClickListener {
            onItemsClickListener()
            baseLayouttManager?.controlCurrency(isOpenedList)
        }
    }

    fun updateSelectedCurrency(isOpen: Boolean, selectedAmount: String,selectedCurrency: String,currentAmount:String, currentCurrency:String) {
        isOpenedList = isOpen
        view.amount_section.mainKDAmountValue.visibility = View.VISIBLE
        if (isOpen)
            changeDataSource(
                AmountViewDataSource(
                    selectedCurr = selectedAmount ,
                    selectedCurrText = selectedCurrency,
                    currentCurr = currentAmount,
                    currentCurrText = currentCurrency,
                    itemCount = itemCountt + "  "+ LocalizationManager.getValue("items","Common")))
        else
            changeDataSource(
                AmountViewDataSource(
                    selectedCurr = selectedAmount ,
                    selectedCurrText = selectedCurrency,
                    currentCurr = currentAmount,
                    currentCurrText = currentCurrency,
                    itemCount = LocalizationManager.getValue("close","Common")))
    }



    /**
     * Sets data from API through LayoutManager
     * @param selectedCurrencyApi represents the currency set by the Merchant.
     * @param transactionCurrencyApi represents the currency which by default.
     * @param itemCountApi represents the itemsCount for the Merchant.
     * @param originalAmountApi represents the default amount from API.
     * */
    fun setDatafromAPI(originalAmountApi:String,transactionCurrencyApi :String, itemCountApi:String){
        itemCountt  = itemCountApi
        originalAmount =CurrencyFormatter.currencyFormat(originalAmountApi)
        transactionCurrency= transactionCurrencyApi
        bindViewComponents()
    }
}