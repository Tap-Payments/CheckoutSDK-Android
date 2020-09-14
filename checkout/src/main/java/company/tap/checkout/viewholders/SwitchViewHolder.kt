package company.tap.checkout.viewholders

import android.content.Context
import company.tap.checkout.enums.SectionType
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapuilibrary.uikit.datasource.TapSwitchDataSource
import company.tap.tapuilibrary.uikit.views.TapCardSwitch

/**
 *
 * Created by Mario Gamal on 7/29/20
 * Copyright © 2020 Tap Payments. All rights reserved.
 *
 */
class SwitchViewHolder(private val context: Context) : TapBaseViewHolder {

    override val view = TapCardSwitch(context)

    override val type = SectionType.SAVE_CARD

    private var merchantName: String? = null
    private var paymentName: PaymentInputViewHolder.PaymentType? = null
    private var switchString: String? = null
    private var goPayString: String? = null
    private var savegoPayString: String? = null
    private var alertgoPaySignupString: String? = null
    init {
        bindViewComponents()
    }

    override fun bindViewComponents() {
        setSwitchLocals()

    }
    // Function is responsible for setting the data to switch based on user selection
    private fun setSwitchLocals() {
        if (merchantName != null && paymentName!= null){
            if(paymentName?.name == "CARD"){
                switchString = LocalizationManager.getValue("cardSaveLabel", "TapCardInputKit")
                view.setSwitchDataSource(getSwitchDataSourceFromAPI(
                    switchString,
                    goPayString,
                    savegoPayString,
                    alertgoPaySignupString
                ))

            }else if( paymentName?.name == "MOBILE") {
                switchString = LocalizationManager.getValue("mobileUseLabel","TapMobileInput")
                view.setSwitchDataSource(getSwitchDataSourceFromAPI(
                    switchString,
                    goPayString,
                    savegoPayString,
                    alertgoPaySignupString
                ))
            }else {
                goPayString = LocalizationManager.getValue("goPayTextLabel","GoPay")
                savegoPayString = LocalizationManager.getValue("savegoPayLabel","GoPay")
                alertgoPaySignupString = LocalizationManager.getValue("goPaySignupLabel","GoPay")

                view.setSwitchDataSource(getSwitchDataSourceFromAPI(null,goPayString,savegoPayString,alertgoPaySignupString))
            }

        }
    }

    private fun getSwitchDataSourceFromAPI(
        switchString: String?,
        goPayString: String?,
        savegoPayString: String?,
        alertgoPaySignupString: String?
    ): TapSwitchDataSource {
        return TapSwitchDataSource(
    switchSave = switchString,
    switchSaveMerchantCheckout = "Save for $merchantName Checkouts",
    switchSavegoPayCheckout = goPayString,
    savegoPayText = savegoPayString,
    alertgoPaySignup = alertgoPaySignupString
)
    }

    /**
     * Sets data from API through LayoutManager
     * @param merchantNameApi represents the merchant name.
     * @param paymentType represents the type is card or mobile payment.Based on it will decide the
     * text of switches.
     * */
    fun setDatafromAPI(merchantNameApi: String, paymentType: PaymentInputViewHolder.PaymentType) {
        merchantName = merchantNameApi
        paymentName = paymentType
        bindViewComponents()
    }



}

