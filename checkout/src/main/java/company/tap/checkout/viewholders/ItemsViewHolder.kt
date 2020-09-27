package company.tap.checkout.viewholders

import android.content.Context
import android.net.sip.SipManager.newInstance
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.replace
import company.tap.checkout.R
import company.tap.checkout.enums.SectionType
import company.tap.checkout.interfaces.BaseLayoutManager
import company.tap.checkout.interfaces.ItemClickedListener
import company.tap.tapuilibrary.uikit.adapters.context
import company.tap.tapuilibrary.uikit.fragment.CardScannerFragment
import company.tap.tapuilibrary.uikit.fragment.CurrencyViewFragment
import java.lang.reflect.Array.newInstance

/**
 *
 * Created by Mario Gamal on 7/23/20
 * Copyright © 2020 Tap Payments. All rights reserved.
 *
 */
class ItemsViewHolder(context11: Context, private val baseLayoutManager: BaseLayoutManager ) : TapBaseViewHolder {
    private val context1:Context?=context11
    override val view = LayoutInflater.from(context11).inflate(
        R.layout.currency_fragment_layout,
        null,false
    )

    override val type = SectionType.SELECT

    var displayed: Boolean = true
    private lateinit var supportedCurrecnyList:ArrayList<String>


    init {

        bindViewComponents()
        if (this::supportedCurrecnyList.isInitialized) {
            CurrencyViewFragment().currencyList=supportedCurrecnyList
        }

    }

    override fun bindViewComponents() {
        if (this::supportedCurrecnyList.isInitialized) {
            baseLayoutManager.controlCurrency(true)
        }
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