package company.tap.checkout.internal.viewholders

import android.content.Context
import android.view.LayoutInflater
import company.tap.checkout.R
import company.tap.checkout.internal.enums.SectionType


/**

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