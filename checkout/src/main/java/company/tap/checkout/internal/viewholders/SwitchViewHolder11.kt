package company.tap.checkout.internal.viewholders

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import company.tap.checkout.R
import company.tap.checkout.internal.enums.PaymentActionType
import company.tap.checkout.internal.enums.SectionType
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapuilibrary.themekit.ThemeManager
import company.tap.tapuilibrary.uikit.atoms.TapTextView
import company.tap.tapuilibrary.uikit.datasource.TapSwitchDataSource
import company.tap.tapuilibrary.uikit.ktx.setBorderedView
import company.tap.tapuilibrary.uikit.ktx.setBottomBorders
import kotlinx.android.synthetic.main.switch_layout.view.*

/**
 *
 * Copyright © 2020 Tap Payments. All rights reserved.
 *
 */
class SwitchViewHolder11(private val context: Context) : TapBaseViewHolder  {

    override val view: View = LayoutInflater.from(context).inflate(R.layout.switch_layout, null)

    override val type = SectionType.SAVE_CARD

    private var merchantName: String? = null
    private var paymentName: PaymentActionType? = null
    private var switchString: String? = null
    private var goPayString: String? = null
    private var savegoPayString: String? = null
    private var alertgoPaySignupString: String? = null
    lateinit var mobileString:String
     var mainTextSave:TapTextView
    init {
        bindViewComponents()
        mainTextSave = view.findViewById(R.id.mainTextSave)
    }

    override fun bindViewComponents() {
        configureSwitch()

    }
    // Function / Logic is responsible for sett ing the data to switch based on user selection
    fun setSwitchLocals(payName:PaymentActionType) {
        goPayString = LocalizationManager.getValue("goPayTextLabel","GoPay")
        savegoPayString = LocalizationManager.getValue("savegoPayLabel","GoPay")
        alertgoPaySignupString = LocalizationManager.getValue("goPaySignupLabel","GoPay")
        println("payname in switch"+payName.name)
        if(payName.name == "CARD"){
            switchString =LocalizationManager.getValue("cardUseNFCLabel","TapCardInputKit")
            switchString?.let { getMainSwitchDataSource(it) }?.let {
                view.mainSwitch.setSwitchDataSource(it) }
            view.cardSwitch.setSwitchDataSource(getTapSwitchDataSourceFromAPI(switchString, goPayString, savegoPayString, alertgoPaySignupString))

        }else if( payName.name == "MOBILE") {
            mobileString = LocalizationManager.getValue("mobileUseLabel","TapMobileInput")
            view.mainSwitch.setSwitchDataSource(getMainSwitchDataSource(mobileString))
            view.cardSwitch.setSwitchDataSource(getTapSwitchDataSourceFromAPI(mobileString, goPayString, savegoPayString, alertgoPaySignupString))

        }
    }

    private fun getTapSwitchDataSourceFromAPI(switchString2: String?, goPayString: String?, savegoPayString: String?, alertgoPaySignupString: String?): TapSwitchDataSource {
        return TapSwitchDataSource(
            switchSave = switchString2,
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
    fun setDatafromAPI(merchantNameApi: String, paymentType: PaymentActionType) {
        merchantName = merchantNameApi
        paymentName = paymentType
        // bindViewComponents()
        paymentName?.let {
            setSwitchLocals(it)
        }
    }

    /**
     * We will change tap card switch background if main switch checked or not
     */
    private fun configureSwitch() {
//        switch_pay_demo.tapCardSwitchLinear.setBackgroundColor(Color.parseColor(ThemeManager.getValue("TapSwitchView.main.backgroundColor")))
//                switch_pay_demo.tapCardSwitchLinear.setBackgroundColor(Color.parseColor(ThemeManager.getValue("TapSwitchView.backgroundColor")))

        setBottomBorders(
            view.cardviewSwitch,
            30f,// corner raduis
            0.0f,
            Color.parseColor(ThemeManager.getValue("TapSwitchView.backgroundColor")),// stroke color
            Color.parseColor(ThemeManager.getValue("TapSwitchView.backgroundColor")),// tint color
            Color.parseColor(ThemeManager.getValue("TapSwitchView.backgroundColor"))
        )//
        setBottomBorders(
            view.cardviewSwitch,
            30f,// corner raduis
            0.0f,
            Color.parseColor(ThemeManager.getValue("TapSwitchView.backgroundColor")),// stroke color
            Color.parseColor(ThemeManager.getValue("TapSwitchView.backgroundColor")),// tint color
            Color.parseColor(ThemeManager.getValue("TapSwitchView.backgroundColor"))
        )//
        view.mainSwitch.switchSaveMobile?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (ThemeManager.currentTheme.isNotEmpty() && ThemeManager.currentTheme.contains("dark")) {
                    view.cardSwitch.tapCardSwitchLinear.setBackgroundResource(R.drawable.ic_blurbackgroundblack)
                } else {
                    view.cardSwitch.tapCardSwitchLinear.setBackgroundResource(R.drawable.ic_blurbackground)
                }
                view.cardviewSwitch.cardElevation = 2.5f
                setBorderedView(
                    view.mainSwitch.card,
                    40f,// corner raduis
                    0.0f,
                    Color.parseColor(ThemeManager.getValue("TapSwitchView.main.backgroundColor")),// stroke color
                    Color.parseColor(ThemeManager.getValue("TapSwitchView.main.backgroundColor")),// tint color
                    Color.parseColor(ThemeManager.getValue("TapSwitchView.main.backgroundColor"))
                )//

                Color.parseColor(ThemeManager.getValue("TapSwitchView.main.backgroundColor"))
               /* view.cardSwitch.payButton.stateListAnimator = null
                view.cardSwitch.payButton.isActivated
                view.cardSwitch.payButton.setButtonDataSource(
                    true,
                    context?.let { LocalizationManager.getLocale(it).language },
                    "Pay",
                    Color.parseColor(ThemeManager.getValue("actionButton.Valid.paymentBackgroundColor")),
                    Color.parseColor(ThemeManager.getValue("actionButton.Valid.titleLabelColor"))
                )*/
                view.cardSwitch.switchesLayout?.visibility = View.VISIBLE
                view.cardSwitch.switchSaveMerchant?.visibility = View.VISIBLE
                view.cardSwitch.switchSaveMerchant?.isChecked = true
                view.cardSwitch.switchGoPayCheckout?.isChecked = true
                view.cardSwitch.switchGoPayCheckout?.visibility = View.VISIBLE
                view.cardSwitch.saveGoPay?.visibility = View.VISIBLE
                view.cardSwitch.alertGoPaySignUp?.visibility = View.VISIBLE
                view.cardSwitch.switchSeparator?.visibility = View.VISIBLE

            } else {
                setBorderedView(
                    view.mainSwitch.card,
                    0f,// corner raduis
                    0.0f,
                    Color.parseColor(ThemeManager.getValue("TapSwitchView.main.backgroundColor")),// stroke color
                    Color.parseColor(ThemeManager.getValue("TapSwitchView.main.backgroundColor")),// tint color
                    Color.parseColor(ThemeManager.getValue("TapSwitchView.main.backgroundColor"))
                )//
                view.cardSwitch.tapCardSwitchLinear.setBackgroundColor(
                    Color.parseColor(
                        ThemeManager.getValue(
                            "TapSwitchView.main.backgroundColor"
                        )
                    )
                )
                view.cardviewSwitch.cardElevation = 0f

               /* view.cardSwitch.payButton.stateListAnimator = null
                view.cardSwitch.payButton.setButtonDataSource(
                    false,
                    context?.let { LocalizationManager.getLocale(it).language },
                    LocalizationManager.getValue("pay", "ActionButton"),
                    Color.parseColor(ThemeManager.getValue("actionButton.Invalid.backgroundColor")),
                    Color.parseColor(ThemeManager.getValue("actionButton.Invalid.titleLabelColor"))
                )
*/

                view.cardSwitch.switchesLayout?.visibility = View.GONE
                view.cardSwitch.switchSaveMerchant?.visibility = View.GONE
                view.cardSwitch.switchSaveMerchant?.isChecked = false
                view.cardSwitch.switchGoPayCheckout?.isChecked = false
                view.cardSwitch.switchGoPayCheckout?.visibility = View.GONE
                view.cardSwitch.saveGoPay?.visibility = View.GONE
                view.cardSwitch.alertGoPaySignUp?.visibility = View.GONE
                view.cardSwitch.switchSeparator?.visibility = View.GONE

            }
        }
        view.cardSwitch.switchSaveMerchant?.setOnCheckedChangeListener { _, _ ->
 if (!view.cardSwitch.switchSaveMerchant?.isChecked!! && !view.cardSwitch.switchGoPayCheckout?.isChecked!!) {
                view.mainSwitch.switchSaveMobile?.isChecked = false
                view.cardSwitch.switchesLayout?.visibility = View.GONE
                view.cardSwitch.switchSaveMerchant?.visibility = View.GONE
                view.cardSwitch.switchSaveMerchant?.isChecked = false
                view.cardSwitch.switchGoPayCheckout?.isChecked = false
                view.cardSwitch.switchGoPayCheckout?.visibility = View.GONE
                view.cardSwitch.saveGoPay?.visibility = View.GONE
                view.cardSwitch.alertGoPaySignUp?.visibility = View.GONE
                view.cardSwitch.switchSeparator?.visibility = View.GONE
            }
        }


        view.cardSwitch.switchGoPayCheckout?.setOnCheckedChangeListener { _, _ ->
            if (!view.cardSwitch.switchSaveMerchant?.isChecked!! && !view.cardSwitch.switchGoPayCheckout?.isChecked!!) {
                 view.mainSwitch.switchSaveMobile?.isChecked = false
                view.cardSwitch.switchesLayout?.visibility = View.GONE
                view.cardSwitch.switchSaveMerchant?.visibility = View.GONE
                view.cardSwitch.switchSaveMerchant?.isChecked = false
                view.cardSwitch.switchGoPayCheckout?.isChecked = false
                view.cardSwitch.switchGoPayCheckout?.visibility = View.GONE
                view.cardSwitch.saveGoPay?.visibility = View.GONE
                view.cardSwitch.alertGoPaySignUp?.visibility = View.GONE
                view.cardSwitch.switchSeparator?.visibility = View.GONE
            }
        }


    }
    //Setting data to TapMainSwitchDataSource
    private fun getMainSwitchDataSource(switchText: String): TapSwitchDataSource {
        return TapSwitchDataSource(
            switchSave = switchText
        )
    }

    fun setSwitchToggleData(paymentType: PaymentActionType){
        if(paymentType==PaymentActionType.CARD) {
            view.mainSwitch.setSwitchDataSource(getMainSwitchDataSource(LocalizationManager.getValue("cardSaveLabel", "TapCardInputKit")))
        }else{
            view.mainSwitch.setSwitchDataSource(getMainSwitchDataSource(LocalizationManager.getValue("mobileSaveLabel","TapMobileInput")))

        }



    }

}

