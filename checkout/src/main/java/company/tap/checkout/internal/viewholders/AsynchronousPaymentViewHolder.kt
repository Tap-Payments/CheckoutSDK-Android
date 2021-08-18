package company.tap.checkout.internal.viewholders

import android.content.Context

import android.view.LayoutInflater
import android.view.View
import company.tap.checkout.R
import company.tap.checkout.internal.enums.SectionType


/**
 * Created by AhlaamK on 8/18/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
class AsynchronousPaymentViewHolder (private val context: Context) : TapBaseViewHolder {


    override val view: View =
        LayoutInflater.from(context).inflate(R.layout.asynchronous_layout, null)

    override val type = SectionType.BUSINESS

    private var merchantName: String? = null
    private var merchantLogo: String? = null


    init {
        bindViewComponents()
    }

    override fun bindViewComponents() {


    }



}