package company.tap.checkout.viewholders

import android.content.Context
import android.graphics.Color
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import company.tap.checkout.adapters.CardAdapter
import company.tap.checkout.enums.SectionType
import company.tap.tapuilibrary.atoms.TapChipGroup

/**
 *
 * Created by Mario Gamal on 7/26/20
 * Copyright © 2020 Tap Payments. All rights reserved.
 *
 */
class CardViewHolder(private val context: Context) : TapBaseViewHolder {

    override val view = TapChipGroup(context, null)

    override val type = SectionType.CARD

    private val paymentsList: ArrayList<Int> = arrayListOf(1, 2, 3, 4, 5, 6)

    init {
        bindViewComponents()
    }

    override fun bindViewComponents() {
        view.setBackgroundColor(Color.parseColor("#f4f4f4"))
        view.groupName.text = "Select"
        view.groupAction.text = "Edit"
        view.chipsRecycler.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        view.chipsRecycler.adapter = CardAdapter(paymentsList)
    }
}