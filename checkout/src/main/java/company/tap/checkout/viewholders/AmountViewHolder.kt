package company.tap.checkout.viewholders

import android.content.Context
import android.graphics.Color
import company.tap.checkout.R
import company.tap.checkout.enums.SectionType
import company.tap.checkout.utils.CurrentTheme
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapuilibrary.atoms.TapButton
import company.tap.tapuilibrary.datasource.AmountViewDataSource
import company.tap.tapuilibrary.datasource.HeaderDataSource
import company.tap.tapuilibrary.views.TapAmountSectionView


import company.tap.thememanager.manager.ThemeManager
import company.tap.thememanager.theme.ButtonTheme
import company.tap.thememanager.theme.TextViewTheme

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
        setThemeToSelectedCurrency()
        setThemeToCurrentCurrency()
        setThemeToItemCount()

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

    fun setThemeToSelectedCurrency(){
        val textViewTheme = TextViewTheme()
        textViewTheme.textColor = Color.parseColor(ThemeManager.getValue("amountSectionView.convertedAmountLabelColor"))
//        textViewTheme.textSize = ThemeManager.getFontSize("amountSectionView.convertedAmountLabelFont").toFloat()
        textViewTheme.font = ThemeManager.getFontName("amountSectionView.convertedAmountLabelFont")
//        view.selectedCurrency.setTheme(textViewTheme)
    }

    fun setThemeToCurrentCurrency(){
        val textViewTheme = TextViewTheme()
        textViewTheme.textColor = Color.parseColor(ThemeManager.getValue("amountSectionView.originalAmountLabelColor"))
     //   textViewTheme.textSize = ThemeManager.getFontSize("amountSectionView.originalAmountLabelFont").toFloat()
        textViewTheme.font = ThemeManager.getFontName("amountSectionView.originalAmountLabelFont")
//        view.currentCurrency.setTheme(textViewTheme)
    }
    fun setThemeToItemCount(){
        val buttonTheme = ButtonTheme()
        buttonTheme.textColor = Color.parseColor(ThemeManager.getValue("amountSectionView.originalAmountLabelColor"))
    //    buttonTheme.textSize = ThemeManager.getFontSize("amountSectionView.itemsLabelFont").toFloat()
//        buttonTheme. = ThemeManager.getFontName("amountSectionView.originalAmountLabelFont")
//        view.itemCount.setTheme(buttonTheme)
    }


    /*
     "amountSectionView": {
        "originalAmountLabelFont" : "Roboto-Regular,20",
        "originalAmountLabelColor" : "greyishBrown",

        "convertedAmountLabelFont" : "Roboto-Light,14",
        "convertedAmountLabelColor" : "brownGrey",

        "itemsLabelFont" : "Roboto-Light,9",
        "itemsLabelColor" : "greyishBrown",

        "itemsNumberButtonBackgroundColor" : "whiteTwo",
        "itemsNumberButtonBorder" : {
            "color" : "veryLightPinkTwo",
            "width" : 1
        },
        "itemsNumberButtonCorner" : 10,

        "backgroundColor" : "white"
    },
     */




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