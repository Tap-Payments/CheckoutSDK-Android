package company.tap.checkout.viewholders

import android.content.Context
import company.tap.checkout.enums.SectionType
import company.tap.tapuilibrary.datasource.HeaderDataSource
import company.tap.tapuilibrary.views.TapHeaderSectionView

/**
 *
 * Created by Mario Gamal on 6/14/20
 * Copyright © 2020 Tap Payments. All rights reserved.
 *
 */
class BusinessViewHolder(context: Context) : TapBaseViewHolder {

    override val view = TapHeaderSectionView(context, null)

    override val type = SectionType.BUSINESS

    init {
        bindViewComponents()
    }

    override fun bindViewComponents() {
        view.setHeaderDataSource(
            HeaderDataSource(
                businessName = "Tap payments",
                businessFor = "Payment For",
                businessImageResources = "https://www.gotapnow.com/web/tapimg.aspx?cst=1124340"
            )
        )
    }

}