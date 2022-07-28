package company.tap.checkoutsdk.activities


import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Color
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.crashlytics.FirebaseCrashlytics
import company.tap.checkout.TapCheckOutSDK
import company.tap.checkout.internal.api.enums.AmountModificatorType
import company.tap.checkout.internal.api.enums.ChargeStatus
import company.tap.checkout.internal.api.enums.Measurement
import company.tap.checkout.internal.api.models.*
import company.tap.checkout.open.CheckoutFragment
import company.tap.checkout.open.controller.SDKSession
import company.tap.checkout.open.enums.CardType
import company.tap.checkout.open.enums.SdkIdentifier
import company.tap.checkout.open.enums.SdkMode
import company.tap.checkout.open.interfaces.SessionDelegate
import company.tap.checkout.open.models.*
import company.tap.checkout.open.models.Receipt
import company.tap.checkout.open.models.Tax
import company.tap.checkoutsdk.BuildConfig
import company.tap.checkoutsdk.R
import company.tap.checkoutsdk.manager.SettingsManager
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapnetworkkit.exception.GoSellError
import company.tap.tapuilibrary.themekit.ThemeManager
import company.tap.tapuilibrary.uikit.enums.ActionButtonState
import company.tap.tapuilibrary.uikit.models.DialogConfigurations
import company.tap.tapuilibrary.uikit.views.TabAnimatedActionButton
import kotlinx.coroutines.*
import java.math.BigDecimal
import java.util.*


class MainActivity : AppCompatActivity(), SessionDelegate {
    private var settingsManager: SettingsManager? = null
    var sdkSession: SDKSession = SDKSession
    private val payButton by lazy { findViewById<TabAnimatedActionButton>(R.id.payButton) }

    private val modalBottomSheet = CheckoutFragment()
    var urlStrDark :String="https://gist.githubusercontent.com/AhlaamK-tap/2ca0cbeaf430c6d40baa4d0700024848/raw/2e23f76a6d323c9e154b63083e5a5a84f73a1994/darktheme.json"
    var urlStrLight :String="https://gist.githubusercontent.com/AhlaamK-tap/9862436dff3b3ca222243dad3705ec6a/raw/1f553408e0f1f7e0a1e15987f987b6033d64a90d/lighttheme.json"
    var urlLocalisation :String="https://gist.githubusercontent.com/AhlaamK-tap/4285f9b4e10fb9a5c51a58f5064d470e/raw/5769a9ddc5ea74020f406d729afba2b0cf29db6c/lang.json"

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Loading the theme and localization files prior to loading the view to avoid crashes

        initializeLanguage()
        settingsManager = SettingsManager

        initializeTheme()


        if(ThemeManager.currentTheme.isNotEmpty() && LocalizationManager.currentLocalized.toString().isNotEmpty()) {


            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

            setLocale(this, LocalizationManager.getLocale(this).language)
            // setLocale(this,"ar")
            println(
                "LocalizationManager.getLocale(this).language is " + LocalizationManager.getLocale(
                    this
                ).language
            )
            /* Handler().postDelayed({
            //doSomethingHere()
            setContentView(R.layout.activity_main)
            initializeSDK()
            configureSDKSession()
            initActionButton()

            if (modalBottomSheet.isHidden || modalBottomSheet.isDetached) {
                println("paybutton hidden")
                payButton.changeButtonState(ActionButtonState.IDLE)
            }

        }, 4000)*/
            setContentView(R.layout.activity_main)
            initializeSDK()
            configureSDKSession()
            initActionButton()

            if (modalBottomSheet.isHidden || modalBottomSheet.isDetached) {
                println("paybutton hidden")
                payButton.changeButtonState(ActionButtonState.IDLE)
            }


            /* if (ThemeManager.currentTheme.isNotEmpty() && (ThemeManager.currentTheme.contains("dark") || ThemeManager.currentTheme.contains("light"))){
                  setContentView(R.layout.activity_main)
                  initializeSDK()
                  configureSDKSession()
                  initActionButton()

                  if (modalBottomSheet.isHidden || modalBottomSheet.isDetached) {
                      println("paybutton hidden")
                      payButton.changeButtonState(ActionButtonState.IDLE)
                  }
              }*/

        }
    }

    private fun initializeTheme() {
        /**
         * Merchant select his choice if it needs Theme from Local Assets or Load it through a URL as below
         * */

        /** Configures the theme manager by setting the provided custom theme file names
        - Parameter customTheme: Please pass the tap checkout theme object with the names of your custom theme files if needed. If not set, the normal and default TAP theme will be used
         */
         if (ThemeManager.currentTheme.isNotEmpty() && ThemeManager.currentTheme.contains("dark"))
             ThemeManager.loadTapTheme(resources, R.raw.defaultdarktheme, "darktheme")
         else if (ThemeManager.currentTheme.isNotEmpty() && !ThemeManager.currentTheme.contains("dark"))
             ThemeManager.loadTapTheme(resources, R.raw.defaultlighttheme, "lighttheme")
         else ThemeManager.loadTapTheme(resources, R.raw.defaultlighttheme, "lighttheme")


        /** Configures the theme manager by passing the provided custom theme url
        - Parameter urlString: Please pass the themeUrL
         */

   /*  if (ThemeManager.currentTheme.isNotEmpty() && ThemeManager.currentTheme.contains("dark"))
            ThemeManager.loadTapTheme(this, urlStrDark)
        else if (ThemeManager.currentTheme.isNotEmpty() && !ThemeManager.currentTheme.contains("dark"))
            ThemeManager.loadTapTheme(this, urlStrLight)
        else ThemeManager.loadTapTheme(this, urlStrLight)
*/

    }


    private fun initializeLanguage(){
        /** Configures the LocalizationManager by setting the provided language  url
        - Parameter  languageUrl json: Please pass the taplanguage to be loaded dynamically  if needed. If not set, the normal and default language will be used
         */


       // LocalizationManager.loadTapLocale(this, urlLocalisation)

        /** Configures the localisation manager by setting the locale, adjusting the flipping and the localisation custom file if any
        - Parameter localiseFile: Please pass the name of the custom localisation file if needed. If not set, the normal and default TAP localisations will be used
         */

        LocalizationManager.loadTapLocale(resources, R.raw.lang)
    }

    private fun initializeSDK() {
        TapCheckOutSDK().init(
                this,
                "sk_test_kovrMB0mupFJXfNZWx6Etg5y",
                "sk_live_QglH8V7Fw6NPAom4qRcynDK2",
               "company.tap.goSellSDKExample"

        )



    }

    /** Configures the Checkout shared manager by setting the provided custom data gathered by the merchant
    - Parameter currency: Represents the original transaction currency stated by the merchant on checkout start
    - Parameter amount: Represents the original transaction amount stated by the merchant on checkout start
    - Parameter items: Represents the List of payment items if any. If no items are provided one will be created by default as PAY TO [MERCHANT NAME] -- Total value [Payment Item][company.tap.checkout.open.models.PaymentItem]
    - Parameter intentModel: The loaded Intent API response model
    - Parameter swipeDownToDismiss: If it is set then when the user swipes down the payment will close, otherwise, there will be a shown button to dismiss the screen. Default is false
    - Parameter paymentTypes: The allowed payment types including cards, apple pay, web and telecom
    - Parameter closeButtonStyle: Defines the required style of the sheet close button
    - Parameter showDragHandler: Decide to show the drag handler or not
    - Parameter transactionMode: Decide which transaction mode will be used in this call. Purchase, Authorization, Card Saving and Toknization. [TransactionMode][company.tap.checkout.open.enums.TransactionMode]
    - Parameter customer: Decides which customer is performing this transaction. It will help you as a merchant to define the payer afterwards. Please check [TapCustomer][company.tap.checkout.open.models.TapCustomer]
    - Parameter destinations: Decides which destination(s) this transaction's amount should split to. Please check [Destination][company.tap.checkout.open.models.Destination]
    - Parameter tapMerchantID: Optional. Useful when you have multiple Tap accounts and would like to do the `switch` on the fly within the single app.
    - Parameter taxes: Optional. List of Taxes you want to apply to the order if any.
    - Parameter shipping: Optional. List of Shipping you want to apply to the order if any.
     */

    private fun configureSDKSession() {
        // Instantiate SDK Session

        // pass your activity as a session delegate to listen to SDK internal payment process follow
        sdkSession.addSessionDelegate(this) //** Required **


        // initiate PaymentDataSource

        // initiate PaymentDataSource
        sdkSession.instantiatePaymentDataSource() //** Required **


        // set transaction currency associated to your account

        sdkSession.setTransactionCurrency(TapCurrency("KWD")) //** Required **


        // Using static CustomerBuilder method available inside TAP TapCustomer Class you can populate TAP TapCustomer object and pass it to SDK
      //  sdkSession.setCustomer(setCustomer()) //** Required **
        settingsManager?.getCustomer()?.let { sdkSession.setCustomer(it) } //** Required **


        // Set Total Amount. The Total amount will be recalculated according to provided Taxes and Shipping

        settingsManager?.getString("key_amount_name", "1")?.let { BigDecimal(it) }?.let {
            sdkSession.setAmount(
                    it
            )
        }//** Required **


        // Set Payment Items array list
        // sdkSession.setPaymentItems(ArrayList()) // ** Optional ** you can pass empty array list
        sdkSession.setPaymentItems(getPaymentItems()) // ** Optional ** you can pass empty array list


        sdkSession.setPaymentType("ALL")  //** Merchant can pass paymentType

        // Set Taxes array list
        sdkSession.setTaxes(ArrayList()) // ** Optional ** you can pass empty array list
         // sdkSession.setTaxes(settingsManager?.getTaxes()) // ** Optional ** you can pass empty array list


        // Set Shipping array list
        sdkSession.setShipping(ArrayList()) // ** Optional ** you can pass empty array list
       //   sdkSession.setShipping(settingsManager?.getShippingList()) // ** Optional ** you can pass empty array list

        // Post URL
        sdkSession.setPostURL("") // ** Optional **


        // Payment Description
        sdkSession.setPaymentDescription("") //** Optional **

        // Payment Extra Info
        sdkSession.setPaymentMetadata(HashMap()) // ** Optional ** you can pass empty array hash map

        // Payment Reference
        sdkSession.setPaymentReference(null) // ** Optional ** you can pass null

        // Payment Statement Descriptor
        sdkSession.setPaymentStatementDescriptor("") // ** Optional **


        // Enable or Disable Saving Card
        sdkSession.isUserAllowedToSaveCard(true) //  ** Required ** you can pass boolean


        // Enable or Disable 3DSecure
        sdkSession.isRequires3DSecure(true)

        //Set Receipt Settings [SMS - Email ]
        sdkSession.setReceiptSettings(
                Receipt(
                        false,
                        false
                )
        ) // ** Optional ** you can pass Receipt object or null


        // Set Authorize Action
        sdkSession.setAuthorizeAction(null) // ** Optional ** you can pass AuthorizeAction object or null


      //  sdkSession.setDestination(settingsManager?.getDestination()) // ** Optional ** you can pass Destinations object or null
       sdkSession.setDestination(null) // ** Optional ** you can pass Destinations object or null


        sdkSession.setMerchantID("1124340") // ** Optional ** you can pass merchant id or null


        sdkSession.setCardType(CardType.ALL) // ** Optional ** you can pass which cardType[CREDIT/DEBIT] you want.By default it loads all available cards for Merchant.

         settingsManager?.getTransactionsMode("key_sdk_transaction_mode")?.let {
            sdkSession.setTransactionMode(
                    it
            )
        }
       // sdkSession.setTransactionMode(TransactionMode.PURCHASE)

        sdkSession.setDefaultCardHolderName("TEST TAP"); // ** Optional ** you can pass default CardHolderName of the user .So you don't need to type it.
        sdkSession.isUserAllowedToEnableCardHolderName(false) // ** Optional ** you can enable/ disable  default CardHolderName .
        sdkSession.setSdkMode(SdkMode.SAND_BOX) //** Pass your SDK MODE
        settingsManager?.setPref(this)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun openBottomSheet(view: View) {
        /// Configures the bottom sheet by creating one and assigning the correct delegates and datasources
        modalBottomSheet.arguments = getArguments()
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)

        sdkSession.startSDK(supportFragmentManager, this, this)
        // modalBottomSheet.show(supportFragmentManager, TAG)
    }

    private fun getArguments(): Bundle {
        val arguments = Bundle()
        arguments.putFloatArray(DialogConfigurations.Corners, floatArrayOf(25f, 25f, 0f, 0f))
        arguments.putInt(DialogConfigurations.Color, Color.TRANSPARENT)
        arguments.putBoolean(DialogConfigurations.Cancelable, false)
        arguments.putFloat(DialogConfigurations.Dim, 0.75f)

        return arguments
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menumain, menu)
        return true
    }

    // actions on click menu items
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_dark -> {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            ThemeManager.loadTapTheme(resources, R.raw.defaultdarktheme, "defaultdarktheme")

            recreate()
            true
        }
        R.id.action_light -> {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            ThemeManager.loadTapTheme(resources, R.raw.defaultlighttheme, "defaultlighttheme")
            recreate()
            true
        }
        R.id.change_language -> {
            if (LocalizationManager.getLocale(this).language == "en") {
                LocalizationManager.setLocale(this, Locale("ar"))
                setLocale(this, "ar")
            } else if (LocalizationManager.getLocale(this).language == "ar") {
                LocalizationManager.setLocale(this, Locale("en"))
                setLocale(this, "en")
            }
            recreate()
            true
        }

        R.id.action_settings -> {
            val intent = Intent(this, SettingsActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)

            true

        }

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }


    private fun setLocale(activity: Activity, languageCode: String) {
        println("languageCode is$languageCode")
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val resources: Resources = activity.resources
        val config: Configuration = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    fun setCustomer(): TapCustomer { // test customer id cus_Kh1b4220191939i1KP2506448// cus_TS012520211349Za012907577 checkout
        val tapCustomer: TapCustomer? = null
        //if (customer != null) customer.phone else Phone(965, 69045932)
        return TapCustomer(
                "cus_TS012520211349Za012907577", "ahlaam", "middlename",
                "lastname", "abcd@gmail.com",
                PhoneNumber("00965", "66175090"), "description",
        )

    }

    private fun getPaymentItems(): ArrayList<PaymentItem>? {
        val items: ArrayList<PaymentItem> = ArrayList<PaymentItem>()
        items.add(
                PaymentItem(
                        "Items 1",
                        "Description for test item #1",
                        Quantity(Measurement.UNITS, Measurement.MASS.name, BigDecimal.valueOf(2)),
                        BigDecimal.valueOf(10),
                        AmountModificator(AmountModificatorType.FIXED, BigDecimal.ZERO),
                       null
                )
        )
        items.add(
                PaymentItem(
                        "Items 2",
                        "Description for test item #2",
                        Quantity(Measurement.UNITS, Measurement.MASS.name, BigDecimal.valueOf(3)),
                        BigDecimal.valueOf(12),
                        AmountModificator(AmountModificatorType.PERCENTAGE, BigDecimal.valueOf(30)),
                    settingsManager?.getTaxes()
                )
        )
        items.add(
                PaymentItem(
                        "Items 3",
                        "Description for test item #3",
                        Quantity(Measurement.UNITS, Measurement.MASS.name, BigDecimal.valueOf(4)),
                        BigDecimal.valueOf(14),
                        AmountModificator(AmountModificatorType.FIXED, BigDecimal.ZERO),
                        null
                )
        )
        println("item are<<<<" + items)

        return items
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun initActionButton() {
        payButton.setButtonDataSource(
                true,
                this.let { LocalizationManager.getLocale(it).language },
                LocalizationManager.getValue("pay", "ActionButton"),
                Color.parseColor(ThemeManager.getValue("actionButton.Valid.paymentBackgroundColor")),
                Color.parseColor(ThemeManager.getValue("actionButton.Valid.titleLabelColor"))
        )

        sdkSession.setButtonView(payButton, this, supportFragmentManager, this)


    }

    override fun paymentSucceed(charge: Charge) {
        println("Payment Succeeded : charge status : " + charge.status)
        println("Payment Succeeded : description : " + charge.description)
        println("Payment Succeeded : message : " + charge.response.message)
        println("##############################################################################")
        if (charge.card != null) {
            println("Payment Succeeded : first six : " + charge.card?.firstSix)
            System.out.println("Payment Succeeded : last four: " + charge.card?.lastFour)
            System.out.println("Payment Succeeded : card object : " + charge.card?.`object`)
            System.out.println("Payment Succeeded : brand : " + charge.card?.brand)
            //  System.out.println("Payment Succeeded : expiry : " + charge.getCard().getExpiry().getMonth()+"\n"+charge.getCard().getExpiry().getYear());
        }

        println("##############################################################################")
        if (charge.acquirer != null) {
            println("Payment Succeeded : acquirer id : " + charge.acquirer?.id)
            println("Payment Succeeded : acquirer response code : " + charge.acquirer?.response?.code)
            println("Payment Succeeded : acquirer response message: " + charge.acquirer?.response?.message)
        }
        println("##############################################################################")
        if (charge.source != null) {
            println("Payment Succeeded : source id: " + charge.source?.id)
            println("Payment Succeeded : source channel: " + charge.source?.channel)
            println("Payment Succeeded : source object: " + charge.source?.`object`)
            println("Payment Succeeded : source payment method: " + charge.source?.paymentMethod)
            println("Payment Succeeded : source payment type: " + charge.source.paymentType)
            println("Payment Succeeded : source type: " + charge.source.type)
        }
        println("##############################################################################")
        if (charge.topup != null) {
            println("Payment Succeeded : topupWalletId : " + charge.topup?.walletId)
            println("Payment Succeeded : Id : " + charge.topup?.Id)
            println("Payment Succeeded : TopUpApp : " + charge.topup?.application?.amount)
        }

        println("##############################################################################")
        if (charge.expiry != null) {
            println("Payment Succeeded : expiry type :" + charge.expiry?.type)
            println("Payment Succeeded : expiry period :" + charge.expiry?.period)
        }
        // Toast.makeText(this,"paymentSucceed"+charge.id, Toast.LENGTH_SHORT).show()

    }

    override fun paymentFailed(charge: Charge?) {
        println("Payment Failed : " + charge?.status)
        println("Payment Failed : " + charge?.description)
        println("Payment Failed : " + charge?.response?.message)
        //  Toast.makeText(this,"paymentFailed"+charge?.response?.message, Toast.LENGTH_SHORT).show()


    }

    override fun authorizationSucceed(authorize: Authorize) {
        println("Authorize Succeeded : " + authorize.status)
        println("Authorize Succeeded : " + authorize.response.message)

        if (authorize.card != null) {
            println("Payment Authorized Succeeded : first six : " + authorize.card?.firstSix)
            println("Payment Authorized Succeeded : last four: " + authorize.card?.lastFour)
            println("Payment Authorized Succeeded : card object : " + authorize.card?.`object`)
        }

        println("##############################################################################")
        if (authorize.acquirer != null) {
            println("Payment Authorized Succeeded : acquirer id : " + authorize.acquirer?.id)
            println("Payment Authorized Succeeded : acquirer response code : " + authorize.acquirer?.response?.code)
            println("Payment Authorized Succeeded : acquirer response message: " + authorize.acquirer?.response?.message)
        }
        println("##############################################################################")
        if (authorize.source != null) {
            println("Payment Authorized Succeeded : source id: " + authorize.source.id)
            println("Payment Authorized Succeeded : source channel: " + authorize.source.channel)
            println("Payment Authorized Succeeded : source object: " + authorize.source.`object`)
            println("Payment Authorized Succeeded : source payment method: " + authorize.source.paymentMethod)
            println("Payment Authorized Succeeded : source payment type: " + authorize.source.paymentType)
            println("Payment Authorized Succeeded : source type: " + authorize.source.type)
        }

        println("##############################################################################")
        if (authorize.expiry != null) {
            println("Payment Authorized Succeeded : expiry type :" + authorize.expiry?.type)
            println("Payment Authorized Succeeded : expiry period :" + authorize.expiry?.period)
        }
        Toast.makeText(this, "authorizationSucceed" + authorize.id, Toast.LENGTH_SHORT).show()


    }

    override fun authorizationFailed(authorize: Authorize?) {
        println("Authorize Failed : " + authorize?.status)
        println("Authorize Failed : " + authorize?.description)
        println("Authorize Failed : " + authorize?.response?.message)
        //  Toast.makeText(this, "authorizationFailed"+authorize?.response?.message, Toast.LENGTH_SHORT).show()


    }

    override fun cardSaved(charge: Charge) {
        // Cast charge object to SaveCard first to get all the Card info.
        if (charge is SaveCard) {
            println("Card Saved Succeeded : first six digits : " + charge.card?.firstSix.toString() + "  last four :" + charge.card?.lastFour)
        }
        println("Card Saved Succeeded : " + charge.status)
        println("Card Saved Succeeded : " + charge.card?.brand)
        println("Card Saved Succeeded : " + charge.description)
        println("Card Saved Succeeded : " + charge.response?.message)
        println("Card Saved Succeeded : " + (charge as SaveCard).card_issuer?.name)
        println("Card Saved Succeeded : " + charge.card_issuer?.id)
        Toast.makeText(this, "cardSaved" + charge.id, Toast.LENGTH_SHORT).show()

    }

    override fun cardSavingFailed(charge: Charge) {
        println("Card Saved Failed : " + charge.status)
        println("Card Saved Failed : " + charge.description)
        println("Card Saved Failed : " + charge.response.message)
    }

    override fun cardTokenizedSuccessfully(token: Token) {
        println("Card Tokenized Succeeded : ")
        println("Token card : " + token.card?.firstSix.toString() + " **** " + token.card?.lastFour)
        println("Token card : " + token.card?.fingerprint.toString() + " **** " + token.card?.funding)
        println("Token card : " + token.card?.id.toString() + " ****** " + token.card?.name)
        println("Token card : " + token.card?.address.toString() + " ****** " + token.card?.`object`)
        println("Token card : " + token.card?.expirationMonth.toString() + " ****** " + token.card?.expirationYear)
        Toast.makeText(this, "cardTokenizedSuccessfully" + token.id, Toast.LENGTH_SHORT).show()

    }

    override fun savedCardsList(cardsList: CardsList) {
        println("savedCardsList>>>>>" + cardsList)
    }

    override fun sdkError(goSellError: GoSellError?) {
        println("sdkError>>>>>" + goSellError)


    }

    override fun sessionIsStarting() {
        println("sessionIsStarting>>>>>")

    }

    override fun sessionHasStarted() {
        println("sessionHasStarted>>>>>")

    }

    override fun sessionCancelled() {
        println("sessionCancelled>>>>>")
        //CLose the bottomsheet and keep buttonto old state


    }

    override fun sessionFailedToStart() {
        println("invalidCardDetails>>>>>")

    }

    override fun invalidCardDetails() {
        println("invalidCardDetails>>>>>")
    }

    override fun backendUnknownError(message: GoSellError?) {
        println("backendUnknownError>>>>>" + message)
        //  payButton?.changeButtonState(ActionButtonState.ERROR)

    }

    override fun invalidTransactionMode() {
        println("invalidTransactionMode>>>>>")
    }

    override fun invalidCustomerID() {
        println("invalidCustomerID>>>>>")

    }


    override fun userEnabledSaveCardOption(saveCardEnabled: Boolean) {
        println("userEnabledSaveCardOption>>>>>$saveCardEnabled")
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun getStatusSDK(response :String ? ,charge: Charge?) {
    }

    override fun onResume() {
        super.onResume()
        if (settingsManager == null) {
            settingsManager?.setPref(this)
        }
    }




}




