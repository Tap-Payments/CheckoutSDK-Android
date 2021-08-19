package company.tap.checkout.internal.viewholders

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import company.tap.checkout.R
import company.tap.checkout.internal.api.models.Charge
import company.tap.checkout.internal.enums.SectionType
import company.tap.tapuilibrary.uikit.organisms.FawryPaymentView

/**
 * Created by AhlaamK on 8/19/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
class AsynchronousPaymentViewHolder (  context: Context) : TapBaseViewHolder {

    private lateinit var fawryView :FawryPaymentView
    override val view: View =
        LayoutInflater.from(context).inflate(R.layout.asynchronous_layout, null)

    override val type = SectionType.BUSINESS




    init {
        bindViewComponents()
    }

    override fun bindViewComponents() {
       fawryView = view.findViewById(R.id.fawry_payment_view)


    }

    fun setDataFromAPI(chargeResponse: Charge){
        println("chargeResponse>>> asynch"+chargeResponse)
    }



}
