package company.tap.checkout.viewholders

import android.content.Context
import android.view.LayoutInflater
import company.tap.checkout.R
import company.tap.checkout.enums.SectionType
import company.tap.checkout.interfaces.BaseLayoutManager

/**
 *
 * Created by Mario Gamal on 7/23/20
 * Modified by AhlaamK
 * Copyright © 2020 Tap Payments. All rights reserved.
 *
 */
class ItemsViewHolder(context11: Context, private val baseLayoutManager: BaseLayoutManager) :
    TapBaseViewHolder {
    private val context1: Context? = context11
    override val view = LayoutInflater.from(context11).inflate(
        R.layout.currency_fragment_layout,
        null
    )

    override val type = SectionType.SELECT

    var displayed: Boolean = true
    private lateinit var supportedCurrecnyList: ArrayList<String>


    init {

        bindViewComponents()

    }

    override fun bindViewComponents() {

    }

    /**
     * Sets data from API through LayoutManager
     * @param supportedCurrencyApi represents the supported currency for the Merchant.
     * */
    fun setDatafromAPI(supportedCurrencyApi: ArrayList<String>) {
        supportedCurrecnyList = supportedCurrencyApi
        bindViewComponents()
        println("supported curr list:$supportedCurrecnyList")

    }
}