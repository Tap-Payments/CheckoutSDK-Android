package company.tap.checkout.viewholders

import android.content.Context
import android.graphics.Color
import company.tap.checkout.R
import company.tap.checkout.enums.SectionType
import company.tap.checkout.utils.CurrentTheme
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

    init {
        bindViewComponents()
        setThemeToView(context)
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

    private fun setThemeToView(context: Context){

        ///we will set static value with current theme and when we use it we will check current theme first
        CurrentTheme.initAppTheme(R.raw.defaultlighttheme, context)
//        ThemeManager.loadTapTheme(context.resources,R.raw.defaultlighttheme )
        setThemeToBusinessName()
        setThemeToPaymentFor()
        setThemeToBusinessPlaceholder()

    }

    fun setThemeToBusinessName(){
        val textViewTheme = TextViewTheme()
        textViewTheme.textColor = Color.parseColor(ThemeManager.getValue("merchantHeaderView.titleLabelColor"))
        textViewTheme.textSize = ThemeManager.getFontSize("merchantHeaderView.titleLabelFont").toFloat()
        textViewTheme.font = ThemeManager.getFontName("merchantHeaderView.titleLabelFont")
//        view.businessName.setTheme(textViewTheme)
    }

    fun setThemeToPaymentFor(){
        val textViewTheme = TextViewTheme()
        textViewTheme.textColor = Color.parseColor(ThemeManager.getValue("merchantHeaderView.subTitleLabelColor"))
        textViewTheme.textSize = ThemeManager.getFontSize("merchantHeaderView.subTitleLabelFont").toFloat()
        textViewTheme.font = ThemeManager.getFontName("merchantHeaderView.subTitleLabelFont")
//        view.paymentFor.setTheme(textViewTheme)
    }

    fun setThemeToBusinessPlaceholder(){
        val textViewTheme = TextViewTheme()
        textViewTheme.textColor = Color.parseColor(ThemeManager.getValue("merchantHeaderView.merchantLogoPlaceHolderColor"))
        textViewTheme.textSize = ThemeManager.getFontSize("merchantHeaderView.merchantLogoPlaceHolderFont").toFloat()
        textViewTheme.font = ThemeManager.getFontName("merchantHeaderView.merchantLogoPlaceHolderFont")
//        view.businessPlaceholder.setTheme(textViewTheme)
    }


}