package company.tap.checkoutsdk


import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import company.tap.checkout.TapCheckOutSDK
import company.tap.checkout.internal.api.enums.Measurement
import company.tap.checkout.internal.api.models.*
import company.tap.checkout.open.CheckoutFragment
import company.tap.checkout.open.controller.SDKSession
import company.tap.checkout.open.enums.CardType
import company.tap.checkout.open.enums.TransactionMode
import company.tap.checkout.open.interfaces.SessionDelegate
import company.tap.checkout.open.models.*
import company.tap.checkout.open.models.Receipt
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapnetworkkit.exception.GoSellError
import company.tap.tapuilibrary.themekit.ThemeManager
import company.tap.tapuilibrary.uikit.models.DialogConfigurations
import company.tap.tapuilibrary.uikit.views.TapBottomSheetDialog.Companion.TAG
import java.math.BigDecimal
import java.util.*


class MainActivity : AppCompatActivity() , SessionDelegate{

   var sdkSession:SDKSession= SDKSession

    private val modalBottomSheet = CheckoutFragment()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /** Configures the theme manager by setting the provided custom theme file names
        - Parameter customTheme: Please pass the tap checkout theme object with the names of your custom theme files if needed. If not set, the normal and default TAP theme will be used
         */
        if (ThemeManager.currentTheme.isNotEmpty() && ThemeManager.currentTheme.contains("dark"))
         ThemeManager.loadTapTheme(resources, R.raw.defaultdarktheme, "darktheme")
        else if(ThemeManager.currentTheme.isNotEmpty() && !ThemeManager.currentTheme.contains("dark"))
        ThemeManager.loadTapTheme(resources, R.raw.defaultlighttheme, "lighttheme")
        else ThemeManager.loadTapTheme(resources, R.raw.defaultlighttheme, "lighttheme")


        //  setTheme(R.style.AppThemeBlack)
        /** Configures the localisation manager by setting the locale, adjusting the flipping and the localisation custom file if any
        - Parameter localiseFile: Please pass the name of the custom localisation file if needed. If not set, the normal and default TAP localisations will be used
         */
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        setLocale(this, LocalizationManager.getLocale(this).language)
        LocalizationManager.loadTapLocale(resources, R.raw.lang)
        initializeSDK()
         configureSDKSession()
        }

    private fun initializeSDK() {
       TapCheckOutSDK().init(
               this,
               "sk_test_kovrMB0mupFJXfNZWx6Etg5y",
               "company.tap.goSellSDKExample"
       )
       //TapCheckOutSDK().init(this,"sk_test_kovrMB0mupFJXfNZWx6Etg5y","")
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

        // Using static CustomerBuilder method available inside TAP TapCustomer Class you can populate TAP TapCustomer object and pass it to SDK
        sdkSession.setCustomer(setCustomer()) //** Required **


        // Set Total Amount. The Total amount will be recalculated according to provided Taxes and Shipping

        // Set Total Amount. The Total amount will be recalculated according to provided Taxes and Shipping
        sdkSession.setAmount(BigDecimal(22)) //** Required **


        // Set Payment Items array list
       // sdkSession.setPaymentItems(ArrayList()) // ** Optional ** you can pass empty array list
       sdkSession.setPaymentItems(getPaymentItems()) // ** Optional ** you can pass empty array list


        sdkSession.setPaymentType("CARD")  //** Merchant can pass paymentType

        // Set Taxes array list
        sdkSession.setTaxes(ArrayList()) // ** Optional ** you can pass empty array list


        // Set Shipping array list
        sdkSession.setShipping(ArrayList()) // ** Optional ** you can pass empty array list


        // Post URL

        // Post URL
        sdkSession.setPostURL("") // ** Optional **


        // Payment Description

        // Payment Description
        sdkSession.setPaymentDescription("") //** Optional **


        // Payment Extra Info

        // Payment Extra Info
        sdkSession.setPaymentMetadata(HashMap()) // ** Optional ** you can pass empty array hash map


        // Payment Reference

        // Payment Reference
        sdkSession.setPaymentReference(null) // ** Optional ** you can pass null


        // Payment Statement Descriptor

        // Payment Statement Descriptor
        sdkSession.setPaymentStatementDescriptor("") // ** Optional **


        // Enable or Disable Saving Card

        // Enable or Disable Saving Card
        sdkSession.isUserAllowedToSaveCard(true) //  ** Required ** you can pass boolean


        // Enable or Disable 3DSecure

        // Enable or Disable 3DSecure
        sdkSession.isRequires3DSecure(true)

        //Set Receipt Settings [SMS - Email ]

        //Set Receipt Settings [SMS - Email ]
        sdkSession.setReceiptSettings(
                Receipt(
                        false,
                        false
                )
        ) // ** Optional ** you can pass Receipt object or null


        // Set Authorize Action

        // Set Authorize Action
        sdkSession.setAuthorizeAction(null) // ** Optional ** you can pass AuthorizeAction object or null


        sdkSession.setDestination(null) // ** Optional ** you can pass Destinations object or null


        sdkSession.setMerchantID(null) // ** Optional ** you can pass merchant id or null


        sdkSession.setCardType(CardType.CREDIT) // ** Optional ** you can pass which cardType[CREDIT/DEBIT] you want.By default it loads all available cards for Merchant.

          sdkSession.setTransactionMode(TransactionMode.PURCHASE)
         sdkSession.setDefaultCardHolderName("TEST TAP"); // ** Optional ** you can pass default CardHolderName of the user .So you don't need to type it.
         sdkSession.isUserAllowedToEnableCardHolderName(false); // ** Optional ** you can enable/ disable  default CardHolderName .

    }

    fun openBottomSheet(view: View) {
        /// Configures the bottom sheet by creating one and assigning the correct delegates and datasources
        modalBottomSheet.arguments = getArguments()
        modalBottomSheet.show(supportFragmentManager, TAG)
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
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        modalBottomSheet.handleNFCResult(intent)

    }
    fun setCustomer(): TapCustomer { // test customer id cus_Kh1b4220191939i1KP2506448
        val tapCustomer: TapCustomer? = null
        //if (customer != null) customer.phone else Phone(965, 69045932)
        return TapCustomer(
                null, "firstname", "middlename",
                "lastname", "abcd@gmail.com",
                PhoneNumber("00965", "9090909090"), "description",
        )

    }

    private fun getPaymentItems(): ArrayList<PaymentItem>? {
        val items: ArrayList<PaymentItem> = ArrayList<PaymentItem>()
        items.add(PaymentItem("Items1",
                "Description for test item #1",
                Quantity(Measurement.UNITS, Measurement.MASS.name, BigDecimal.valueOf(22)), BigDecimal.valueOf(22),
                null, null))
        println("item are<<<<" + items)

        return items
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
            println("Payment Succeeded : acquirer id : " + charge?.acquirer?.id)
            println("Payment Succeeded : acquirer response code : " + charge?.acquirer?.response?.code)
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
            System.out.println("Payment Succeeded : topupWalletId : " + charge.topup?.walletId)
            System.out.println("Payment Succeeded : Id : " + charge.topup?.Id)
            System.out.println("Payment Succeeded : TopUpApp : " + charge.topup?.application?.amount)
        }

        println("##############################################################################")
        if (charge.expiry != null) {
            System.out.println("Payment Succeeded : expiry type :" + charge.expiry?.type)
            System.out.println("Payment Succeeded : expiry period :" + charge.expiry?.period)
        }
        Toast.makeText(this, charge.id, Toast.LENGTH_SHORT).show()
        modalBottomSheet.dismiss()
    }

    override fun paymentFailed(charge: Charge?) {
        println("Payment Failed : " + charge?.status)
        println("Payment Failed : " + charge?.description)
        println("Payment Failed : " + charge?.response?.message)

        modalBottomSheet.dismiss()
    }

    override fun authorizationSucceed(authorize: Authorize) {
        println("authorizationSucceed" + authorize)
    }

    override fun authorizationFailed(authorize: Authorize?) {
        println("authorizationFailed>>>>>" + authorize)
    }

    override fun cardSaved(charge: Charge) {
        println("cardSaved>>>>>" + charge)
    }

    override fun cardSavingFailed(charge: Charge) {
        println("cardSavingFailed>>>>>" + charge)
    }

    override fun cardTokenizedSuccessfully(token: Token) {
        println("cardTokenizedSuccessfully>>>>>" + token)
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
    }

    override fun sessionFailedToStart() {
        println("invalidCardDetails>>>>>")
    }

    override fun invalidCardDetails() {
        println("invalidCardDetails>>>>>")
    }

    override fun backendUnknownError(message: String?) {
        println("backendUnknownError>>>>>" + message)
    }

    override fun invalidTransactionMode() {
        println("invalidTransactionMode>>>>>")
    }

    override fun invalidCustomerID() {
        println("invalidCustomerID>>>>>")

    }

    override fun userEnabledSaveCardOption(saveCardEnabled: Boolean) {
       println("userEnabledSaveCardOption>>>>>" + saveCardEnabled)
    }

}




