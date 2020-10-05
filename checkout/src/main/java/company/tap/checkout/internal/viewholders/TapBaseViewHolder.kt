package company.tap.checkout.viewholders

import android.view.View
import company.tap.checkout.enums.SectionType

/**
 *
 * Created by Mario Gamal on 6/14/20
 * Copyright © 2020 Tap Payments. All rights reserved.
 *
 */
interface TapBaseViewHolder {
    val view: View
    val type: SectionType
    fun bindViewComponents()
}