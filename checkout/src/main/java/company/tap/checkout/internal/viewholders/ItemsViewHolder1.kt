package company.tap.checkout.internal.viewholders

import android.content.Context
import android.view.LayoutInflater
import androidx.fragment.app.FragmentManager
import company.tap.checkout.R
import company.tap.checkout.internal.adapter.CurrencyTypeAdapter
import company.tap.checkout.internal.adapter.ItemAdapter
import company.tap.checkout.internal.dummygener.Currencies1
import company.tap.checkout.internal.dummygener.Items1
import company.tap.checkout.internal.enums.SectionType
import company.tap.checkout.internal.fragment.CurrencyViewsFragment
import company.tap.checkout.internal.interfaces.OnCurrencyChangedActionListener

/**
 *
 * Copyright © 2020 Tap Payments. All rights reserved.
 *
 */
class ItemsViewHolder1(context11: Context, private val onCurrencyChangedActionListener: OnCurrencyChangedActionListener,fragmentManagere: FragmentManager) :
    TapBaseViewHolder {
    private  var fragmentManager: FragmentManager
    override val view = LayoutInflater.from(context11).inflate(
        R.layout.currency_fragment_layout,
        null
    )

    override val type = SectionType.SELECT

    var itemsdisplayed: Boolean = false
    private lateinit var supportedCurrecnyList: ArrayList<Currencies1>
    private lateinit var supportedItemList: List<Items1>


    init {
        bindViewComponents()
        this.fragmentManager = fragmentManagere
    }

    override fun bindViewComponents() {}

    /**
     * Sets data from API through LayoutManager
     * @param supportedCurrencyApi represents the supported currency for the Merchant.
     * @param supportItemListApi represents the supported currency for the Merchant.
     * */
    fun setDatafromAPI(supportedCurrencyApi: ArrayList<Currencies1>,supportItemListApi :List<Items1>,fragmentManager: FragmentManager) {
        supportedCurrecnyList = supportedCurrencyApi
        supportedItemList = supportItemListApi
       // bindViewComponents()
     //   val manager: FragmentManager = fragmentManager
     //   val transaction = manager.beginTransaction()
        /* transaction.add(
             R.id.currency_fragment_container,
             CurrencyViewFragment(supportedCurrecnyList, itemList)
         )*/
        println("supportedItemList curr list:$supportedItemList")
        this.fragmentManager = fragmentManager
    }

    fun resetView(){
        fragmentManager.beginTransaction().remove(CurrencyViewsFragment(ArrayList(), ArrayList(),onCurrencyChangedActionListener)).commit()
    }

    fun setView(){
        fragmentManager.beginTransaction()
            .replace(R.id.fragment_container_nfc_lib, CurrencyViewsFragment(supportedCurrecnyList, supportedItemList,onCurrencyChangedActionListener))
            .commit()
    }

    fun resetItemList(items1: List<Items1>){
        supportedItemList = items1
        setView()
        println("items1 is"+items1)
       /*  val adapter = CurrencyTypeAdapter(onCurrencyChangedActionListener)
        adapter.updateAdapterData(supportedCurrecnyList)
        adapter.notifyDataSetChanged()*/
     /* val itemAdapter = ItemAdapter(onCurrencyChangedActionListener)
        itemAdapter.adapterClearList()
        itemAdapter.updateAdapterData(items1)
        itemAdapter.notifyDataSetChanged()
*/

    }


}