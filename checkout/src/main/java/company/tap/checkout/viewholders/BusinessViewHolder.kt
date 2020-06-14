package company.tap.checkout.viewholders

import android.content.Context
import android.view.View
import company.tap.checkout.R
import company.tap.tapuilibrary.TapHeader

/**
 *
 * Created by Mario Gamal on 6/14/20
 * Copyright © 2020 Tap Payments. All rights reserved.
 *
 */
class BusinessViewHolder(context: Context?): TapBaseViewHolder {

    override val view = TapHeader(context, null)

    override val type: ViewHolderType
        get() = ViewHolderType.BUSINESS

    init {
        bindViewComponents()
    }

    override fun bindViewComponents() {
        view.businessName.text = "Tap Payments"
        view.paymentFor.text = "Payment For"
        view.businessIcon.setImageResource(R.drawable.tap_logo)
    }

}