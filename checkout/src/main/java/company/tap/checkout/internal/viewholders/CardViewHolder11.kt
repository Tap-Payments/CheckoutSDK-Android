package company.tap.checkout.internal.viewholders

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import company.tap.checkout.R
import company.tap.checkout.internal.adapter.CardTypeAdapterUIKIT


import company.tap.checkout.internal.dummygener.SavedCards
import company.tap.checkout.internal.enums.SectionType
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapuilibrary.themekit.ThemeManager

import company.tap.tapuilibrary.uikit.interfaces.OnCardSelectedActionListener
import kotlinx.android.synthetic.main.cardviewholder_layout.view.*

/**
 *
 * Created by Mario Gamal on 7/26/20
 * Copyright © 2020 Tap Payments. All rights reserved.
 *
 */
class CardViewHolder11(
    private val context: Context,
    private val onCardSelectedActionListener: OnCardSelectedActionListener?=null

) : TapBaseViewHolder {

  override val view: View = LayoutInflater.from(context).inflate(R.layout.cardviewholder_layout1, null)


    override val type = SectionType.CARD


    private var savedCardsList: List<SavedCards>? = null


    init {
       // bindViewComponents()

    }

    override fun bindViewComponents() {

        view.mainChipgroup.groupName.text = LocalizationManager.getValue(
            "GatewayHeader",
            "HorizontalHeaders",
            "leftTitle"
        )
        view.mainChipgroup.groupAction.text = LocalizationManager.getValue(
            "GatewayHeader",
            "HorizontalHeaders",
            "rightTitle"
        )
        view.mainChipgroup.chipsRecycler.layoutManager = LinearLayoutManager(
            context,
            RecyclerView.HORIZONTAL,
            false
        )
       /* view.mainChipgroup.chipsRecycler.adapter =
            savedCardsList?.let { CardTypeAdapter(it as ArrayList<SavedCards>, onCardSelectedActionListener,false) } */
        println("savedCardsList before cardviewholder ${savedCardsList}")
        view.mainChipgroup.chipsRecycler.adapter =
            savedCardsList?.let { CardTypeAdapterUIKIT(it,onCardSelectedActionListener,false) }
          //  savedCardsList?.let { CardTypeAdapterUIKIT(it as ArrayList<SavedCards>, onCardSelectedActionListener,false) }
        println("savedCardsList in cardviewholder ${savedCardsList}")


        /**
         * set separator background
         */
        view.tapSeparatorViewLinear?.setBackgroundColor(Color.parseColor(ThemeManager.getValue("horizontalList.backgroundColor")))


    }


    /**
     * Sets data from API through LayoutManager
     * @param savedCardMethodsApi represents the list of payment methods available from API
     * */
    fun setDatafromAPI(savedCardMethodsApi: List<SavedCards>) {
        savedCardsList = savedCardMethodsApi
        bindViewComponents()
    }
}