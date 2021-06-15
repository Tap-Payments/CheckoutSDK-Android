package company.tap.checkout.internal.viewholders

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import company.tap.checkout.R
import company.tap.checkout.internal.enums.SectionType
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapuilibrary.themekit.ThemeManager
import company.tap.tapuilibrary.uikit.datasource.HeaderDataSource
import kotlinx.android.synthetic.main.businessview_layout.view.*


/**
 *
 * Copyright © 2020 Tap Payments. All rights reserved.
 *
 */
class BusinessViewHolder(private val context: Context) : TapBaseViewHolder {


    override val view: View =
        LayoutInflater.from(context).inflate(R.layout.businessview_layout, null)

    override val type = SectionType.BUSINESS

    private var merchantName: String? = null
    private var merchantLogo: String? = null

    init {
        bindViewComponents()
    }

    override fun bindViewComponents() {

        if (merchantName != null)
            view.headerView.setHeaderDataSource(getHeaderDataSourceFromAPI())
        /**
         * set separator background
         */
        view.topSeparatorLinear.setBackgroundColor((Color.parseColor(ThemeManager.getValue("merchantHeaderView.backgroundColor"))))
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
    fun setDatafromAPI(merchantLogoApi: String?, merchantNameApi: String?) {
        merchantLogo = merchantLogoApi
        merchantName = merchantNameApi
        bindViewComponents()
    }

}