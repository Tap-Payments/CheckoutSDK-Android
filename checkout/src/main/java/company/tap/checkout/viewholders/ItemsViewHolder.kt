package company.tap.checkout.viewholders

import android.content.Context
import android.view.LayoutInflater
import androidx.fragment.app.FragmentManager
import company.tap.checkout.R
import company.tap.checkout.enums.SectionType

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

    init {
        bindViewComponents()
    }

    override fun bindViewComponents() {
    }
}