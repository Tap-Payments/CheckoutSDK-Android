package company.tap.checkout.internal.viewholders

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import company.tap.checkout.R
import company.tap.checkout.internal.enums.SectionType
import company.tap.checkout.internal.utils.getDimensionsInDp
import company.tap.checkout.internal.utils.isRTL
import company.tap.checkout.internal.utils.setMargins
import company.tap.checkout.internal.viewmodels.CheckoutViewModel
import company.tap.checkout.open.data_managers.PaymentDataSource
import company.tap.checkout.open.enums.TransactionMode
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapuilibrary.themekit.ThemeManager
import company.tap.tapuilibrary.themekit.theme.ImageViewTheme
import company.tap.tapuilibrary.uikit.atoms.TapImageView
import company.tap.tapuilibrary.uikit.datasource.HeaderDataSource
import company.tap.tapuilibrary.uikit.ktx.setTopBorders
import kotlinx.android.synthetic.main.businessview_layout.view.*


/**
 *
 * Copyright © 2020 Tap Payments. All rights reserved.
 *
 */
class BusinessViewHolder(context: Context, private val checkoutViewModel: CheckoutViewModel) :
    TapBaseViewHolder {


    override val view: View =
        LayoutInflater.from(context).inflate(R.layout.businessview_layout, null)

    override val type = SectionType.BUSINESS

    private var merchantName: String? = null
    private var merchantLogo: String? = null
    private var tapCloseIcon: TapImageView = view.findViewById(R.id.tapCloseIcon)


    init {
        bindViewComponents()
    }

    override fun bindViewComponents() {
        val imagetheme = ImageViewTheme()
        imagetheme.imageResource = (R.drawable.merchant_new_logo)
        view.headerView.businessIcon.setTheme(imagetheme)




        view.headerView.tapCloseIcon.layoutParams.height =
            view.headerView.context.getDimensionsInDp(30)
        view.headerView.tapCloseIcon.layoutParams.width =
            view.headerView.context.getDimensionsInDp(30)
        view.headerView.tapChipIcon.layoutParams.width = view.headerView.context.getDimensionsInDp(40)
        view.headerView.tapChipIcon.layoutParams.height = view.headerView.context.getDimensionsInDp(40)
        view.headerView.businessIcon.layoutParams.height = view.headerView.context.getDimensionsInDp(40)
        view.headerView.businessIcon.layoutParams.width = view.headerView.context.getDimensionsInDp(40)

        view.headerView.draggerView.layoutParams.width = view.headerView.context.getDimensionsInDp(65)

        view.headerView.tapChipIcon.setMargins(if (view.headerView.tapChipIcon.isRTL()) 0 else 20, 0, if (view.headerView.tapChipIcon.isRTL()) 20 else 20, 0)



        setTopBorders(
            view.headerView.tapCloseIcon,
            40f,// corner raduis
            0.0f,
            Color.parseColor(ThemeManager.getValue("merchantHeaderView.backgroundColor")),// stroke color
            Color.parseColor(ThemeManager.getValue("merchantHeaderView.backgroundColor")),// tint color
            Color.parseColor(ThemeManager.getValue("merchantHeaderView.backgroundColor"))
        )//
        if (PaymentDataSource.getTransactionMode() == TransactionMode.SAVE_CARD || PaymentDataSource.getTransactionMode() == TransactionMode.TOKENIZE_CARD) {

            view.headerView.setHeaderDataSource(HeaderDataSource("Enter Card Details", null, null))
            view.headerView.businessIcon.visibility = View.VISIBLE
            //view.headerView.tapChipIcon.visibility= View.GONE
        } else
            if (merchantName != null) {
                view.headerView.setHeaderDataSource(getHeaderDataSourceFromAPI())
                if (merchantName.equals("fawry")) {
                    view.headerView.paymentFor.visibility = View.GONE
                }
                view.headerView.businessIcon.visibility = View.VISIBLE
                view.headerView.constraint.visibility = View.VISIBLE

                tapCloseIcon.setOnClickListener {
                    checkoutViewModel.dismissBottomSheet()
                }

            }

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
    fun setDataFromAPI(merchantLogoApi: String?, merchantNameApi: String?) {
        merchantLogo = merchantLogoApi
        merchantName = merchantNameApi
        bindViewComponents()
    }

}