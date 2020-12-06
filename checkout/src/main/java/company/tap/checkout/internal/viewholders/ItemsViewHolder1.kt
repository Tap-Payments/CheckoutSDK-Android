package company.tap.checkout.internal.viewholders

import android.content.ClipData
import android.content.Context
import android.view.LayoutInflater
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.add
import company.tap.cardbusinesskit.testmodels.Items
import company.tap.checkout.R
import company.tap.checkout.internal.dummygener.Currencies1
import company.tap.checkout.internal.dummygener.Items1
import company.tap.checkout.internal.enums.SectionType
import company.tap.checkout.internal.interfaces.BaseLayoutManager
import company.tap.tapuilibrary.uikit.fragment.CurrencyViewFragment
import company.tap.tapuilibrary.uikit.fragment.CurrencyViewsFragment

/**
 *
 * Created by Mario Gamal on 7/23/20
 * Modified by AhlaamK
 * Copyright © 2020 Tap Payments. All rights reserved.
 *
 */
class ItemsViewHolder1(context11: Context, private val baseLayoutManager: BaseLayoutManager) :
    TapBaseViewHolder {
    private val context1: Context? = context11
    private lateinit var fragmentManager: FragmentManager
    override val view = LayoutInflater.from(context11).inflate(
        R.layout.currency_fragment_layout,
        null
    )

    override val type = SectionType.SELECT

    var displayed: Boolean = true
    private lateinit var supportedCurrecnyList: ArrayList<String>
    private lateinit var supportedCurrecny: ArrayList<Currencies1>
    private lateinit var supportedItemList: List<Items1>


    init {

        bindViewComponents()


    }

    override fun bindViewComponents() {

    }

    /**
     * Sets data from API through LayoutManager
     * @param supportedCurrencyApi represents the supported currency for the Merchant.
     * @param supportItemListApi represents the supported currency for the Merchant.
     * */
    fun setDatafromAPI(supportedCurrencyApi: ArrayList<String>,supportItemListApi :List<Items1>,fragmentManager: FragmentManager) {
        supportedCurrecnyList = supportedCurrencyApi
        supportedItemList = supportItemListApi
       // bindViewComponents()
        val manager: FragmentManager = fragmentManager
        val transaction = manager.beginTransaction()
        /* transaction.add(
             R.id.currency_fragment_container,
             CurrencyViewFragment(supportedCurrecnyList, itemList)
         )*/
        transaction.replace(R.id.sdkContainer,
            CurrencyViewsFragment(supportedCurrecny, supportedItemList))


        transaction.addToBackStack(null)
        transaction.commit()
        println("supported curr list:$supportedCurrecnyList")

    }

    fun resetView(){
        val manager: FragmentManager = fragmentManager
        val transaction = manager.beginTransaction()
        transaction.remove(CurrencyViewFragment(supportedCurrecny, supportedItemList))
        transaction.addToBackStack(null)
        transaction.commit()
    }

}