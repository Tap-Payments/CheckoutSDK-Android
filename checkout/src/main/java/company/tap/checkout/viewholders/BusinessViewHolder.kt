package company.tap.checkout.viewholders

import android.content.Context
import company.tap.checkout.R
import company.tap.checkout.enums.SectionType
import company.tap.checkout.utils.CurrentTheme
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
        setThemeToView(context)
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

    // TODO remove all the themes  from viewHolder
    private fun setThemeToView(context: Context) {

        ///we will set static value with current theme and when we use it we will check current theme first
        CurrentTheme.initAppTheme(R.raw.defaultlighttheme, context)
//        ThemeManager.loadTapTheme(context.resources,R.raw.defaultlighttheme )

    }

    fun setThemeToPaymentFor(){
        val textViewTheme = TextViewTheme()
        textViewTheme.textColor = Color.parseColor(ThemeManager.getValue("merchantHeaderView.subTitleLabelColor"))
//        textViewTheme.textSize = ThemeManager.getFontSize("merchantHeaderView.subTitleLabelFont").toFloat()
        textViewTheme.font = ThemeManager.getFontName("merchantHeaderView.subTitleLabelFont")
//        view.paymentFor.setTheme(textViewTheme)
    }

    fun setThemeToBusinessPlaceholder(){
        val textViewTheme = TextViewTheme()
        textViewTheme.textColor = Color.parseColor(ThemeManager.getValue("merchantHeaderView.merchantLogoPlaceHolderColor"))
    //    textViewTheme.textSize = ThemeManager.getFontSize("merchantHeaderView.merchantLogoPlaceHolderFont").toFloat()
        textViewTheme.font = ThemeManager.getFontName("merchantHeaderView.merchantLogoPlaceHolderFont")
//        view.businessPlaceholder.setTheme(textViewTheme)
    }

    /**
     * Sets data from API through LayoutManager
     * @param merchantLogoApi represents the merchant Logo from the Url
     * @param merchantNameApi represents the merchant business name
     * */
    fun setDatafromAPI(merchantLogoApi: String, merchantNameApi: String) {
        merchantLogo = merchantLogoApi
        merchantName = merchantNameApi
        bindViewComponents()
    }

}