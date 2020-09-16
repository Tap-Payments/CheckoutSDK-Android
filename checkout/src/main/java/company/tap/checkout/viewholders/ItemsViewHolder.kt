package company.tap.checkout.viewholders

import android.content.Context
import android.view.LayoutInflater
import company.tap.checkout.R
import company.tap.checkout.enums.SectionType
import company.tap.tapuilibrary.uikit.fragment.CurrencyViewFragment

/**
 *
 * Created by Mario Gamal on 7/23/20
 * Copyright © 2020 Tap Payments. All rights reserved.
 *
 */
class ItemsViewHolder(context: Context) : TapBaseViewHolder {
    override val view = LayoutInflater.from(context).inflate(R.layout.currency_fragment_layout, null)

    override val type = SectionType.SELECT

    var displayed: Boolean = true
    private lateinit var supportedCurrecnyList:ArrayList<String>


    init {
        bindViewComponents()
    }

    override fun bindViewComponents() {
            CurrencyViewFragment().currencyList = supportedCurrecnyList
    }
    /**
     * Sets data from API through LayoutManager
     * @param supportedCurrencyApi represents the supported currency for the Merchant.
     * */
    fun setDatafromAPI(supportedCurrencyApi: ArrayList<String>) {
        supportedCurrecnyList = supportedCurrencyApi
        bindViewComponents()
    }
}