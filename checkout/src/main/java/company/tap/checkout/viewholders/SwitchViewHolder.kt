package company.tap.checkout.viewholders

import android.content.Context
import company.tap.checkout.enums.SectionType
import company.tap.tapuilibrary.views.TapCardSwitch

/**
 *
 * Created by Mario Gamal on 7/29/20
 * Copyright © 2020 Tap Payments. All rights reserved.
 *
 */
class SwitchViewHolder(context: Context) : TapBaseViewHolder {

    override val view = TapCardSwitch(context)

    override val type = SectionType.SAVE_CARD

    init {
        bindViewComponents()
    }

    override fun bindViewComponents() {}
}