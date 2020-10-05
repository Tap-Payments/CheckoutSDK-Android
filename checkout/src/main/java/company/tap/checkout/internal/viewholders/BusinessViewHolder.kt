package company.tap.checkout.internal.viewholders

import android.content.Context
import company.tap.checkout.internal.enums.SectionType
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapuilibrary.uikit.datasource.HeaderDataSource
import company.tap.tapuilibrary.uikit.views.TapHeaderSectionView


/**
 *
 * Created by Mario Gamal on 6/14/20
 * Copyright © 2020 Tap Payments. All rights reserved.
 *
 */
class BusinessViewHolder(context: Context) : TapBaseViewHolder {

    override val view = TapHeaderSectionView(context, null)

    override val type = SectionType.BUSINESS

    private var merchantName: String? = null
    private var merchantLogo: String? = null

    init {
        bindViewComponents()

    }

    override fun bindViewComponents() {

        if (merchantName != null)
            view.setHeaderDataSource(getHeaderDataSourceFromAPI())
    }

    private fun getHeaderDataSourceFromAPI(): HeaderDataSource {
        return HeaderDataSource(
            businessName = merchantName,
            businessFor = LocalizationManager.getValue("paymentFor", "TapMerchantSection"),
            businessImageResources = merchantLogo
        )
    }


    /**
     * Sets data from API through LayoutManager
     * @param merchantLogoApi represents the merchant Logo from the Url
     * @param merchantNameApi represents the merchant business name
     * */
    fun setDatafromAPI(merchantLogoApi: String, merchantNameApi: String) {
        merchantLogo = null
        merchantName = merchantNameApi
        bindViewComponents()
    }

}