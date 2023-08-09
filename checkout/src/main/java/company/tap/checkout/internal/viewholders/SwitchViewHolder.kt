package company.tap.checkout.internal.viewholders

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import company.tap.checkout.R
import company.tap.checkout.internal.api.enums.PaymentType
import company.tap.checkout.internal.enums.PaymentTypeEnum
import company.tap.checkout.internal.enums.SectionType
import company.tap.checkout.internal.viewmodels.CheckoutViewModel
import company.tap.nfcreader.open.utils.TapNfcUtils
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapuilibrary.themekit.ThemeManager
import company.tap.tapuilibrary.uikit.atoms.TapTextViewNew
import company.tap.tapuilibrary.uikit.datasource.TapSwitchDataSource
import company.tap.tapuilibrary.uikit.ktx.setBorderedView
import company.tap.tapuilibrary.uikit.ktx.setBottomBorders
import kotlinx.android.synthetic.main.switch_layout.view.*

/**
 *
 * Copyright © 2020 Tap Payments. All rights reserved.
 *
 */
class SwitchViewHolder(private val context: Context , private val checkoutViewModel: CheckoutViewModel) : TapBaseViewHolder {

    override val view: View = LayoutInflater.from(context).inflate(R.layout.switch_layout, null)

    override val type = SectionType.SAVE_CARD

    private var merchantName: String? = null
    private var paymentName: PaymentTypeEnum? = null
    private var switchString: String? = null
    private var goPayString: String? = null
    private var savegoPayString: String? = null
    private var alertgoPaySignupString: String? = null
    lateinit var mobileString: String


    @JvmField
    var goPayisLoggedin: Boolean = false
    var mainTextSave: TapTextViewNew

    init {
        bindViewComponents()
        mainTextSave = view.findViewById(R.id.mainTextSave)
    }

    override fun bindViewComponents() {
        configureSwitch()
    }

    // Function / Logic is responsible for sett ing the data to switch based on user selection
    fun setSwitchLocals(payName: PaymentTypeEnum) {

        goPayString = LocalizationManager.getValue( "goPay", "TapSwitchView", "notes")
        savegoPayString =  LocalizationManager.getValue("goPay", "TapSwitchView", "notes")
        alertgoPaySignupString = LocalizationManager.getValue("goPay", "TapSwitchView", "notes")

        println("payname in switch" + payName.name)
        if (payName.name == "card") {
            if (TapNfcUtils.isNfcAvailable(context)) {
                switchString = LocalizationManager.getValue("mainCards", "TapSwitchView","titleEmpty")

            } else {
                //Logic applied to  stop showing NFC on non supported devices
                if (LocalizationManager.getLocale(context).language == "en") {
                    switchString =
                        LocalizationManager.getValue<String?>("mainCards", "TapSwitchView","titleEmpty")
                            ?.replace(" or NFC", "")

                } else {
                    switchString =
                        LocalizationManager.getValue<String?>("mainCards", "TapSwitchView","titleEmpty")
                            ?.replace("الــ NFC", "")

                }
            }

            switchString?.let { getMainSwitchDataSource(it) }?.let {
                view.mainSwitch.setSwitchDataSource(it)
            }
            view.cardSwitch.setSwitchDataSource(
                getTapSwitchDataSourceFromAPI(
                    switchString,
                    goPayString,
                    savegoPayString,
                    alertgoPaySignupString
                )
            )

        } else if (payName.name == "telecom") {
            mobileString = LocalizationManager.getValue("mainTelecom", "TapSwitchView","titleValid")
            view.mainSwitch.setSwitchDataSource(getMainSwitchDataSource(mobileString))
            view.cardSwitch.setSwitchDataSource(
                getTapSwitchDataSourceFromAPI(
                    mobileString,
                    goPayString,
                    savegoPayString,
                    alertgoPaySignupString
                )
            )

        }
    }

    private fun getTapSwitchDataSourceFromAPI(
        switchString2: String?,
        goPayString: String?,
        savegoPayString: String?,
        alertgoPaySignupString: String?
    ): TapSwitchDataSource {
        val saveForString: String = LocalizationManager.getValue("cardSaveForTapLabel", "TapCardInputKit")
        val saveForStringArabic: String = LocalizationManager.getValue("cardSaveForTapLabel", "TapCardInputKit")
        val checkoutsString: String = LocalizationManager.getValue("cardSaveForTapLabel", "TapCardInputKit")
        val switchSaveMerchantCheckout: String
        if (LocalizationManager.getLocale(context).language == "ar") {
            switchSaveMerchantCheckout = "$saveForStringArabic $merchantName"
        } else {
            switchSaveMerchantCheckout = "$saveForString$merchantName  $checkoutsString"
        }

        return TapSwitchDataSource(
            switchSave = switchString2,
            switchSaveMerchantCheckout = switchSaveMerchantCheckout,
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
    fun setDataFromAPI(merchantNameApi: String, paymentType: PaymentTypeEnum) {
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
        view.mainSwitch.mainSwitchLinear.setBackgroundColor(Color.parseColor(ThemeManager.getValue("TapSwitchView.main.backgroundColor")))
        view.mainSwitch.mainTextSave.visibility = View.VISIBLE
        view.cardviewSwitch.cardElevation = 0f
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
        )

        /**
         * Logic for Main switch
         * **/
        view.mainSwitch.switchSaveMobile?.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                view.cardSwitch.payButton.isActivated
                val payString: String = LocalizationManager.getValue("pay", "ActionButton")
                val nowString: String = LocalizationManager.getValue("pay", "ActionButton")
                view?.cardSwitch?.payButton?.setButtonDataSource(
                    false,
                    "en",
                    payString+" "+nowString,
                    Color.parseColor(ThemeManager.getValue("actionButton.Valid.paymentBackgroundColor")),
                    Color.parseColor(ThemeManager.getValue("actionButton.Valid.titleLabelColor")),
                )
                /**
                 * Here we will check if there is saving options if NOT ----> We will just activate action button
                 * view.cardSwitch.payButton.isActivated
                 * if YES -----> we will set Logic of function mainSwitchCheckedAction()
                 */
                mainSwitchCheckedAction()
            } else mainSwitchUncheckedAction()
        }

        /**
         * Logic for save merchant switch
         * **/
        view.cardSwitch.switchSaveMerchant?.setOnCheckedChangeListener { _, _ ->
            switchMerchantCheckoutChangeCheckedAction()
        }
        /**
         * Logic for save goPay Checkout switch
         * **/
        if (goPayisLoggedin) {
            view.cardSwitch.switchGoPayCheckout.visibility = View.VISIBLE
            view.cardSwitch.switchGoPayCheckout?.setOnCheckedChangeListener { _, _ ->
                switchGoPayCheckoutChangeCheckedAction()
            }
        }


    }

    //Setting data to TapMainSwitchDataSource
    private fun getMainSwitchDataSource(switchText: String): TapSwitchDataSource {
        return TapSwitchDataSource(
            switchSave = switchText
        )
    }

    fun setSwitchToggleData(paymentType: PaymentType) {
        if (paymentType == PaymentType.CARD) {
            view.mainSwitch.setSwitchDataSource(
                getMainSwitchDataSource(
                    LocalizationManager.getValue(
                        "mainCards",
                        "TapSwitchView","titleValid"
                    )
                )
            )
        } else {
            view.mainSwitch.setSwitchDataSource(
                getMainSwitchDataSource(
                    LocalizationManager.getValue(
                        "mainTelecom",
                        "TapSwitchView","titleValid"
                    )
                )
            )

        }

    }

    private fun mainSwitchUncheckedAction() {
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

    private fun mainSwitchCheckedAction() {
        if (ThemeManager.currentTheme.isNotEmpty() && ThemeManager.currentTheme.contains("dark")) {
            view.cardSwitch.tapCardSwitchLinear.setBackgroundResource(R.drawable.ic_blurbackgroundblack)
        } else {
            view.cardSwitch.tapCardSwitchLinear.setBackgroundResource(R.drawable.blurbackground)
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
        view.cardSwitch.payButton.stateListAnimator = null
        view.cardSwitch.payButton.isActivated
        val payString: String = LocalizationManager.getValue("pay", "ActionButton")
        val nowString: String = LocalizationManager.getValue("now", "ActionButton")
        view?.cardSwitch?.payButton?.setButtonDataSource(
            false,
            "en",
            payString+" "+nowString,
            Color.parseColor(ThemeManager.getValue("actionButton.Valid.paymentBackgroundColor")),
            Color.parseColor(ThemeManager.getValue("actionButton.Valid.titleLabelColor")),
        )
        view.cardSwitch.switchesLayout?.visibility = View.VISIBLE
        view.cardSwitch.switchSaveMerchant?.visibility = View.VISIBLE
        view.cardSwitch.switchSaveMerchant?.isChecked = true
        /**
         * Here we will check if goPay is Loggedin if NOT ----> We will just hide the switches
         *
         * if YES -----> we will show the switches also
         * Please NOTE : @paramgoPayisLoggedin is false for now will update when api is added
         */
        if (goPayisLoggedin) {
            view.cardSwitch.switchGoPayCheckout?.isChecked = true
            view.cardSwitch.switchGoPayCheckout?.visibility = View.VISIBLE
            view.cardSwitch.saveGoPay?.visibility = View.VISIBLE
            view.cardSwitch.alertGoPaySignUp?.visibility = View.VISIBLE
            view.cardSwitch.switchSeparator?.visibility = View.VISIBLE
        }

    }

    private fun switchGoPayCheckoutChangeCheckedAction() {
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

    private fun switchMerchantCheckoutChangeCheckedAction() {
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

