package company.tap.checkout.internal.viewholders

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import company.tap.checkout.R
import company.tap.checkout.internal.enums.SectionType
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapuilibrary.uikit.datasource.TapSwitchDataSource
import company.tap.tapuilibrary.uikit.views.TapCardSwitch
import kotlinx.android.synthetic.main.switch_layout.view.*

/**
 *
 * Created by Mario Gamal on 7/29/20
 * Copyright © 2020 Tap Payments. All rights reserved.
 *
 */
class SwitchViewHolder(private val context: Context) : TapBaseViewHolder  {

  //  override val view = TapCardSwitch(context)
    override val view: View = LayoutInflater.from(context).inflate(R.layout.switch_layout, null)

    override val type = SectionType.SAVE_CARD

    private var merchantName: String? = null
    private var paymentName: PaymentInputViewHolder.PaymentType? = null
    private var switchString: String? = null
    private var goPayString: String? = null
    private var savegoPayString: String? = null
    private var alertgoPaySignupString: String? = null
    private var actionButton = TabAnimatedActionButtonViewHolder(context)

    init {
        bindViewComponents()
    }

    override fun bindViewComponents() {
      //  setSwitchLocals()

    }
    // Function / Logic is responsible for sett ing the data to switch based on user selection
    fun setSwitchLocals(payName:PaymentInputViewHolder.PaymentType) {
        //TODO Check why view is not being refreshed though string is updated
        //Has to be in Else condition only, temporarily kept here for data purpose
        goPayString = LocalizationManager.getValue("goPayTextLabel","GoPay")
        savegoPayString = LocalizationManager.getValue("savegoPayLabel","GoPay")
        alertgoPaySignupString = LocalizationManager.getValue("goPaySignupLabel","GoPay")
        if(payName.name == "CARD"){
            switchString = LocalizationManager.getValue("cardSaveLabel", "TapCardInputKit")
           view.mainSwitch.setSwitchDataSource(getSwitchDataSourceFromAPI(
               switchString,
                goPayString,
                savegoPayString,
                alertgoPaySignupString
            ))

        }else if( payName.name == "MOBILE") {
            switchString = LocalizationManager.getValue("mobileUseLabel","TapMobileInput")
            view.mainSwitch.setSwitchDataSource(getSwitchDataSourceFromAPI(
               switchString,
                goPayString,
                savegoPayString,
                alertgoPaySignupString
            ))

        }else {
            goPayString = LocalizationManager.getValue("goPayTextLabel","GoPay")
            savegoPayString = LocalizationManager.getValue("savegoPayLabel","GoPay")
            alertgoPaySignupString = LocalizationManager.getValue("goPaySignupLabel","GoPay")
            view.mainSwitch.setSwitchDataSource(getSwitchDataSourceFromAPI(switchString,goPayString,savegoPayString,alertgoPaySignupString))
        }
        view.cardSwitch.saveTextView.text = switchString
        print("switch string $switchString")
        actionButton.activateButton(context)
    }

    private fun getSwitchDataSourceFromAPI(
        switchString: String?,
        goPayString: String?,
        savegoPayString: String?,
        alertgoPaySignupString: String?
    ): TapSwitchDataSource {
        return TapSwitchDataSource(
    switchSave = switchString,
    switchSaveMerchantCheckout = "Save for   $merchantName   Checkouts",
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
       // bindViewComponents()
       paymentName?.let {
           setSwitchLocals(it)
       }
    }


}

