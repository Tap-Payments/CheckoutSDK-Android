package company.tap.checkout.internal.utils

import android.content.Context
import company.tap.checkout.internal.api.models.PaymentOption
import company.tap.checkout.internal.viewmodels.CheckoutViewModel
import company.tap.checkout.open.CheckoutFragment


fun CheckoutViewModel.getAssetName(paymentOptionOb: PaymentOption?, context: Context): String {
    var lang: String = "en"
    var theme: String = "light"
    lang = CustomUtils.getCurrentLocale(context)

    theme = CustomUtils.getCurrentTheme()

    val assetToLoad: String = paymentOptionOb?.buttonStyle?.titleAssets.toString()
    println(
        "<<<assetToLoad>>>" + assetToLoad.replace("{theme}", theme)
            .replace("{lang}", lang) + ".png"
    )
    return assetToLoad.replace("{theme}", theme).replace("{lang}", lang) + ".png"
}