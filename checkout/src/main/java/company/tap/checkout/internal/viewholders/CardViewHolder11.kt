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
import company.tap.checkout.internal.interfaces.OnCardSelectedActionListener
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapuilibrary.themekit.ThemeManager
import company.tap.tapuilibrary.uikit.interfaces.TapActionButtonInterface
import kotlinx.android.synthetic.main.cardviewholder_layout1.view.*


/**
 *
 * Copyright © 2020 Tap Payments. All rights reserved.
 *
 */
class CardViewHolder11(private val context: Context, private val onCardSelectedActionListener: OnCardSelectedActionListener,private val tapActionButtonInterface: TapActionButtonInterface) : TapBaseViewHolder {

    override val view: View = LayoutInflater.from(context).inflate(R.layout.cardviewholder_layout1, null)
    override val type = SectionType.CARD
    private var savedCardsList: MutableList<SavedCards>? = null

    init { bindViewComponents() }

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
        val adapter = CardTypeAdapterUIKIT(onCardSelectedActionListener,tapActionButtonInterface )
        view.mainChipgroup.chipsRecycler.adapter = adapter
        savedCardsList?.let { adapter.updateAdapterData(it) }
        /**
         * set separator background
         */
        view.tapSeparatorViewLinear?.setBackgroundColor(Color.parseColor(ThemeManager.getValue("horizontalList.backgroundColor")))
    }

    /**
     * Sets data from API through LayoutManager
     * @param savedCardMethodsApi represents the list of payment methods available from API
     * */
    fun setDatafromAPI(savedCardMethodsApi: MutableList<SavedCards>) {
        savedCardsList = savedCardMethodsApi
        bindViewComponents()
    }
}