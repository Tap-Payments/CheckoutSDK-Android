package company.tap.checkout.viewholders

import android.content.Context
import android.graphics.Color
import company.tap.checkout.R
import company.tap.checkout.enums.SectionType
import company.tap.checkout.utils.CurrentTheme
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapuilibrary.datasource.HeaderDataSource
import company.tap.tapuilibrary.views.TapHeaderSectionView
import company.tap.thememanager.manager.ThemeManager
import company.tap.thememanager.theme.TextViewTheme


/**
 *
 * Created by Mario Gamal on 6/14/20
 * Copyright © 2020 Tap Payments. All rights reserved.
 *
 */
class BusinessViewHolder(context: Context) : TapBaseViewHolder {

    override val view = TapHeaderSectionView(context, null)

    override val type = SectionType.BUSINESS

    var merchantName:String?=null
    var merchantLogo:String?=null

    init {
        bindViewComponents()
        setThemeToView(context)
    }

    override fun bindViewComponents() {

        if(merchantName!=null)
        view.setHeaderDataSource(getHeaderDataSourceFromAPI())
    }

    private fun getHeaderDataSourceFromAPI():HeaderDataSource{
        //ToDO add Local string from localization Payment For
        return HeaderDataSource(
                businessName = merchantName,
                businessFor = "Payment For",
                businessImageResources = merchantLogo
            )
    }

 // TODO remove all the themes  from viewHolder
    private fun setThemeToView(context: Context){
        ///we will set static value with current theme and when we use it we will check current theme first
        CurrentTheme.initAppTheme(R.raw.defaultlighttheme, context)
    }



    /**
     * Sets data from API through LayoutManager
     * @param merchantLogoApi represents the merchant Logo from the Url
     * @param merchantNameApi represents the merchant business name
     * */
    fun setDatafromAPI(merchantLogoApi :String ,merchantNameApi :String){
        merchantLogo = merchantLogoApi
        merchantName = merchantNameApi
        bindViewComponents()
    }

}