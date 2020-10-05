package company.tap.checkout.viewholders

import android.content.Context
import android.view.LayoutInflater
import company.tap.checkout.R
import company.tap.checkout.enums.SectionType
import kotlinx.android.synthetic.main.cardscanner_fragment_layout.view.*


/**
 * Created by AhlaamK on 9/29/20.

Copyright (c) 2020    Tap Payments.
All rights reserved.
 **/
class CardScannerViewHolder(private val context: Context) : TapBaseViewHolder {
    override val view = LayoutInflater.from(context).inflate(
        R.layout.cardscanner_fragment_layout,
        null
    )
    override val type: SectionType
        get() = SectionType.FRAGMENT

    override fun bindViewComponents() {


    }
}