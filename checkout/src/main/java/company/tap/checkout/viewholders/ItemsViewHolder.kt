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
class ItemsViewHolder(context11: Context, private val itemClickedListener : ItemClickedListener) : TapBaseViewHolder {
    private val context1:Context?=context11
    override val view = LayoutInflater.from(context1).inflate(
        R.layout.currency_fragment_layout,
        null,false
    )

    override val type = SectionType.SELECT

    var displayed: Boolean = true
    private lateinit var supportedCurrecnyList:ArrayList<String>


    init {

        bindViewComponents()
    }

    override fun bindViewComponents() {
        if (this::supportedCurrecnyList.isInitialized) {
            itemClickedListener.onItemClicked()
            //CurrencyViewFragment(supportedCurrecnyList)

          /*  val appCompatActivity = context1 as AppCompatActivity

            val fm1: FragmentManager = appCompatActivity.supportFragmentManager
            val currencyViewFragment: Fragment = CurrencyViewFragment(supportedCurrecnyList)
            //fm.beginTransaction().replace(R.id.fragment_container_nfc, nfcfrag).commit()
           // fm1.executePendingTransactions()
            fm1.beginTransaction().replace(R.id.currency_fragment_container,currencyViewFragment).commitNow()*/
          //  fm1.executePendingTransactions()

        }
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