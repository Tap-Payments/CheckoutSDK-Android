package company.tap.checkoutsdk.activities


import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.crashlytics.FirebaseCrashlytics
import company.tap.checkout.TapCheckOutSDK
import company.tap.checkout.internal.api.enums.AmountModificatorType
import company.tap.checkout.internal.api.models.*
import company.tap.checkout.open.CheckoutFragment
import company.tap.checkout.open.controller.SDKSession
import company.tap.checkout.open.enums.CardType
import company.tap.checkout.open.enums.Category
import company.tap.checkout.open.enums.SdkMode
import company.tap.checkout.open.interfaces.SessionDelegate
import company.tap.checkout.open.models.*
import company.tap.checkout.open.models.Receipt
import company.tap.checkoutsdk.R
import company.tap.checkoutsdk.manager.SettingsManager
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapnetworkkit.exception.GoSellError
import company.tap.tapuilibrary.themekit.ThemeManager
import company.tap.tapuilibrary.uikit.enums.ActionButtonState
import company.tap.tapuilibrary.uikit.models.DialogConfigurations
import company.tap.tapuilibrary.uikit.views.TabAnimatedActionButton
import mobi.foo.benefitinapp.data.Transaction
import mobi.foo.benefitinapp.listener.CheckoutListener
import java.math.BigDecimal
import java.util.*


class MainActivity : AppCompatActivity(), SessionDelegate, CheckoutListener {
    private var settingsManager: SettingsManager? = null
    var sdkSession: SDKSession = SDKSession
    private val payButton by lazy { findViewById<TabAnimatedActionButton>(R.id.payButton) }
    private val linearLayout by lazy { findViewById<LinearLayout>(R.id.linearLayout) }

    private val modalBottomSheet = CheckoutFragment()
    var urlStrDark: String =
        "https://gist.githubusercontent.com/AhlaamK-tap/2ca0cbeaf430c6d40baa4d0700024848/raw/b5b9862e3600b6905bd530ed1dac2e030eb0a96f/darktheme.json"
    var urlStrLight: String =
        "https://gist.githubusercontent.com/AhlaamK-tap/9862436dff3b3ca222243dad3705ec6a/raw/aadbb3c3531f8c58aa706ff1ea1cf9bfae691f6b/lighttheme.json"
    var urlLocalisation: String =
        "https://gist.githubusercontent.com/AhlaamK-tap/4285f9b4e10fb9a5c51a58f5064d470e/raw/5769a9ddc5ea74020f406d729afba2b0cf29db6c/lang.json"
    var itemsList = ArrayList<ItemsModel>()
    val REQUEST_ID_MULTIPLE_PERMISSIONS = 7

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settingsManager = SettingsManager
        settingsManager?.setPref(this)
        //Loading the theme and localization files prior to loading the view to avoid crashes
        checkAndroidVersion()
        initializeLanguage()

       // ThemeManager.loadTapTheme(this, urlStrLight,"lighttheme")
        initializeTheme()

        //displayMertrc()

        if (ThemeManager.currentTheme.isNotEmpty() && LocalizationManager.currentLocalized.toString()
                .isNotEmpty()
        ) {


            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

           LocalizationManager.setLocale(this, Locale( settingsManager?.getSDKLanguage("sdk_language")))
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
            initializeBottomSheet()


        }
    }

    private fun displayMertrc() {
        val displaymetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displaymetrics)
        val ht = displaymetrics.heightPixels
        var wt: Int = displaymetrics.widthPixels

        if (resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK == Configuration.SCREENLAYOUT_SIZE_LARGE) {
            Toast.makeText(this, "Large screen", Toast.LENGTH_LONG).show()
        } else if (resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK == Configuration.SCREENLAYOUT_SIZE_NORMAL) {
            Toast.makeText(this, "Normal sized screen", Toast.LENGTH_LONG)
                .show()
        } else if (resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK == Configuration.SCREENLAYOUT_SIZE_SMALL) {
            Toast.makeText(this, "Small sized screen", Toast.LENGTH_LONG)
                .show()
        } else {
            Toast.makeText(
                this,
                "Screen size is neither large, normal or small",
                Toast.LENGTH_LONG
            ).show()
        }


        // Determine density
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        val density = metrics.densityDpi

        if (density == DisplayMetrics.DENSITY_HIGH) {
            Toast.makeText(
                this,
                "DENSITY_HIGH... Density is $density",
                Toast.LENGTH_LONG
            ).show()
        } else if (density == DisplayMetrics.DENSITY_MEDIUM) {
            Toast.makeText(
                this,
                "DENSITY_MEDIUM... Density is $density",
                Toast.LENGTH_LONG
            ).show()
        } else if (density == DisplayMetrics.DENSITY_LOW) {
            Toast.makeText(
                this,
                "DENSITY_LOW... Density is $density",
                Toast.LENGTH_LONG
            ).show()
        } else {
            Toast.makeText(
                this, "Density is neither HIGH, MEDIUM OR LOW.  Density is "
                        + density.toString(), Toast.LENGTH_LONG
            )
                .show()
        }
        // These are deprecated
        val display = (getSystemService(Context.WINDOW_SERVICE) as WindowManager)
            .defaultDisplay
        val width = display.width
        val height = display.height

        println("width" + width + "////height" + height + "///model" + android.os.Build.MANUFACTURER + "///ht metrci" + ht + "///wt metric" + wt)
    }

    private fun initializeBottomSheet() {
        modalBottomSheet.arguments = getArguments()
        //  modalBottomSheet.showCloseImage = settingsManager?.getBoolean("showImageKey",false) == true
        if (modalBottomSheet.isHidden || modalBottomSheet.isDetached) {
            println("paybutton hidden")
            payButton.changeButtonState(ActionButtonState.RESET)
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




 /* if (ThemeManager.currentTheme.isNotEmpty() && ThemeManager.currentTheme.contains("dark"))
                ThemeManager.loadTapTheme(this, urlStrDark,"darktheme")
            else if (ThemeManager.currentTheme.isNotEmpty() && !ThemeManager.currentTheme.contains("dark"))
                ThemeManager.loadTapTheme(this, urlStrLight,"lighttheme")
            else ThemeManager.loadTapTheme(this, urlStrLight,"lighttheme")
*/


   }


   private fun initializeLanguage() {
       /** Configures the LocalizationManager by setting the provided language  url
       - Parameter  languageUrl json: Please pass the taplanguage to be loaded dynamically  if needed. If not set, the normal and default language will be used
        */


       // LocalizationManager.loadTapLocale(this, urlLocalisation)

       /** Configures the localisation manager by setting the locale, adjusting the flipping and the localisation custom file if any
       - Parameter localiseFile: Please pass the name of the custom localisation file if needed. If not set, the normal and default TAP localisations will be used
        */

       LocalizationManager.loadTapLocale(resources, R.raw.lang)
       settingsManager?.getSDKLanguage("sdk_language")?.let { Locale(it) }?.let {
           LocalizationManager.setLocale(this,
               it
           )
       }
       settingsManager?.getSDKLanguage("sdk_language")?.let { setLocale(this, it) }
       println("sdk_language init>>>"+ settingsManager?.getSDKLanguage("sdk_language"))
   }

   private fun initializeSDK() {
       TapCheckOutSDK().init(
           this,
           settingsManager?.getString("key_test_name", "sk_test_kovrMB0mupFJXfNZWx6Etg5y"),
           settingsManager?.getString("key_live_name", "sk_live_QglH8V7Fw6NPAom4qRcynDK2"),
           settingsManager?.getString("key_package_name", "company.tap.goSellSDKExample"),
          Locale(settingsManager?.getSDKLanguage("sdk_language"))
       )
       /*  settingsManager?.getString("key_test_name","sk_test_2kGVSuR6bKAXLF4rDe0wa9QU"),
    settingsManager?.getString("key_live_name","sk_live_QglH8V7Fw6NPAom4qRcynDK2"),
    settingsManager?.getString("key_package_name","resources.gosell.io")
 )*/

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
       sdkSession.showCloseImage = settingsManager?.getBoolean("showImageKey", false)


       // initiate PaymentDataSource
       sdkSession.instantiatePaymentDataSource() //** Required **


       // set transaction currency associated to your account
       //  if(settingsManager.getString("key_sdk_transaction_currency","KWD"))
       settingsManager?.getString("key_sdk_transaction_currency", "KWD")
           ?.let { TapCurrency(it) }
           ?.let { sdkSession.setTransactionCurrency(it) } //** Required **
       // sdkSession.setTransactionCurrency(TapCurrency("KWD"))

       // Using static CustomerBuilder method available inside TAP TapCustomer Class you can populate TAP TapCustomer object and pass it to SDK
       // sdkSession.setCustomer(setCustomer()) //** Required **
       settingsManager?.getCustomer()?.let { sdkSession.setCustomer(it) } //** Required **


       // Set Total Amount. The Total amount will be recalculated according to provided Taxes and Shipping
       // settingsManager?.getString("key_amount_name", "1")?.let { BigDecimal(it) }?.let {
       //   sdkSession.setAmount(
       //           it
       //    )
       // }//** Required **
       sdkSession.setAmount(
           BigDecimal.valueOf(60)
       )

       // Set Payment ItemsModel array list
       //  sdkSession.setPaymentItems(ArrayList()) // ** Optional ** you can pass empty array list
       sdkSession.setPaymentItems(settingsManager?.getDynamicPaymentItems()) // ** Optional ** you can pass empty array list
       //  sdkSession.setPaymentItems(getPaymentItems()) // ** Optional ** you can pass empty array list


       sdkSession.setPaymentType("ALL")  //** Merchant can pass paymentType


       // Set Taxes array list
       //  sdkSession.setTaxes(ArrayList()) // ** Optional ** you can pass empty array list
       // sdkSession.setTaxes(settingsManager?.getTaxes()) // ** Optional ** you can pass empty array list


       // Set Shipping array list
       // sdkSession.setShipping(ArrayList()) // ** Optional ** you can pass empty array list
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


       sdkSession.setMerchantID(
           settingsManager?.getString(
               "key_merchant_id",
               "1124340"
           )
       ) // ** Optional ** you can pass merchant id or null
       // sdkSession.setMerchantID("599424") // ** Optional ** you can pass merchant id or null
       // sdkSession.setMerchantID("1124340") // ** Optional ** you can pass merchant id or null


       sdkSession.setCardType(CardType.ALL) // ** Optional ** you can pass which cardType[CREDIT/DEBIT] you want.By default it loads all available cards for Merchant.

       settingsManager?.getTransactionsMode("key_sdk_transaction_mode")?.let {
           sdkSession.setTransactionMode(
               it
           )
       }
       // sdkSession.setTransactionMode(TransactionMode.PURCHASE)

       sdkSession.setDefaultCardHolderName(
           settingsManager?.getString(
               "key_default_holder_name",
               "Tap Test"
           )
       ); // ** Optional ** you can pass default CardHolderName of the user .So you don't need to type it.
       settingsManager?.getBoolean("enableHolderName", false)
           ?.let { sdkSession.isUserAllowedToEnableCardHolderName(it) } // ** Optional ** you can enable/ disable  default CardHolderName .

       sdkSession.setSdkMode(SdkMode.SAND_BOX) //** Pass your SDK MODE

       //    sdkSession.setCardType(CardType.CREDIT); // ** Optional ** you can pass which cardType[CREDIT/DEBIT] you want.By default it loads all available cards for Merchant.
       val useShippingEnableKey = settingsManager?.getBoolean("useShippingEnableKey", false)
       val enableLoyaltyProgram = settingsManager?.getBoolean("enableLoyaltyProgram", false)

       println("useShippingEnableKey value>>>$useShippingEnableKey")
       if (useShippingEnableKey == true) {
           sdkSession.setOrderObject(getOrder())
       } else {
           sdkSession.setOrderObject(getOrderWithoutAdd())
       }

       sdkSession.enableLoyalty = enableLoyaltyProgram
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
       val metrics = DisplayMetrics()
       windowManager.defaultDisplay.getMetrics(metrics)
       val displayMetrics = metrics.densityDpi
       if (displayMetrics == DisplayMetrics.DENSITY_450 || displayMetrics == DisplayMetrics.DENSITY_420 || displayMetrics == DisplayMetrics.DENSITY_400 || displayMetrics == DisplayMetrics.DENSITY_440 || displayMetrics == DisplayMetrics.DENSITY_XXHIGH) {

           arguments.putFloatArray(DialogConfigurations.Corners, floatArrayOf(25f, 25f, 0f, 0f))
       } else if (displayMetrics == DisplayMetrics.DENSITY_260 || displayMetrics == DisplayMetrics.DENSITY_280 || displayMetrics == DisplayMetrics.DENSITY_300 || displayMetrics == DisplayMetrics.DENSITY_XHIGH || displayMetrics == DisplayMetrics.DENSITY_340 || displayMetrics == DisplayMetrics.DENSITY_360) {
           arguments.putFloatArray(DialogConfigurations.Corners, floatArrayOf(5f, 5f, 0f, 0f))
       }
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

       R.id.action_settings -> {
           val intent = Intent(this, SettingsActivity::class.java)
           intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_CLEAR_TASK)
          finish()
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
           "", "ahlaam", "middlename",
           "lastname", "abcd@gmail.com",
           PhoneNumber("00965", "66175090"), "description",
       )

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

       payButton.setOnClickListener {
           payButton.clearAnimation()
           sdkSession.setButtonView(payButton, this, supportFragmentManager, this)

       }


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

   @RequiresApi(Build.VERSION_CODES.N)
   override fun sdkError(goSellError: GoSellError?) {
       println("sdkError>>>>>" + goSellError)
       Toast.makeText(this, "sdkError" + goSellError?.errorBody, Toast.LENGTH_SHORT).show()
       payButton?.setOnClickListener {
           sdkSession.setButtonView(payButton, this, supportFragmentManager, this)
       }
       /* payButton?.changeButtonState(ActionButtonState.ERROR)
        payButton.setButtonDataSource(
            true,
            this.let { LocalizationManager.getLocale(it).language },
            LocalizationManager.getValue("pay", "ActionButton"),
            Color.parseColor(ThemeManager.getValue("actionButton.Valid.paymentBackgroundColor")),
            Color.parseColor(ThemeManager.getValue("actionButton.Valid.titleLabelColor"))
        )
        payButton?.setOnClickListener {
            sdkSession.setButtonView(payButton, this, supportFragmentManager, this)
        }*/

   }

   override fun sessionIsStarting() {
       Log.e("MainActivity", "sessionIsStarting: " )

   }

   override fun sessionHasStarted() {
       Log.e("MainActivity", "sessionHasStarted>>>>>")

   }

   override fun sessionCancelled() {
       Log.e("MainActivity", "sessionCancelled>>>>>")
       //CLose the bottomsheet and keep buttonto old state

       Toast.makeText(this, "Your sessionCancelled>>!", Toast.LENGTH_SHORT).show()

   }

   override fun sessionFailedToStart() {
       Toast.makeText(this, "sessionFailedToStart>> it already active!", Toast.LENGTH_SHORT).show()

   }

   override fun invalidCardDetails() {
       Log.e("MainActivity", "invalidCardDetails>>>>>")
   }

   @RequiresApi(Build.VERSION_CODES.N)
   override fun backendUnknownError(message: GoSellError?) {
       println("backendUnknownError>>>>>" + message)
       payButton?.changeButtonState(ActionButtonState.RESET)
       payButton.setButtonDataSource(
           true,
           this.let { LocalizationManager.getLocale(it).language },
           LocalizationManager.getValue("pay", "ActionButton"),
           Color.parseColor(ThemeManager.getValue("actionButton.Valid.paymentBackgroundColor")),
           Color.parseColor(ThemeManager.getValue("actionButton.Valid.titleLabelColor"))
       )
       payButton?.setOnClickListener {
           sdkSession.setButtonView(payButton, this, supportFragmentManager, this)
       }

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

/*    @RequiresApi(Build.VERSION_CODES.N)
   override fun getStatusSDK(response :String ? ,charge: Charge?) {
     *//*  if(charge==null) {

           payButton.changeButtonState(ActionButtonState.RESET)
           SessionManager.setActiveSession(false)
           payButton.setButtonDataSource(
               true,
               this.let { LocalizationManager.getLocale(it).language },
               LocalizationManager.getValue("pay", "ActionButton"),
               Color.parseColor(ThemeManager.getValue("actionButton.Valid.paymentBackgroundColor")),
               Color.parseColor(ThemeManager.getValue("actionButton.Valid.titleLabelColor"))
           )
           payButton.setOnClickListener {
               payButton.changeButtonState(ActionButtonState.LOADING)
               SDKSession.startSDK(supportFragmentManager, this, this)
           }
       }*//*
   }*/

   override fun onResume() {
       super.onResume()
       if (settingsManager == null) {
           settingsManager?.setPref(this)
       }


   }

   private fun getOrderItemsList(): ArrayList<ItemsModel
           > {
       itemsList = ArrayList<ItemsModel>()
       itemsList.add(
           ItemsModel(
               "",
               "abcjuice",
               BigDecimal.valueOf(12),
               "kwd",
               BigDecimal.valueOf(3),
               Category.DIGITAL_GOODS,
               AmountModificator(AmountModificatorType.FIXED, BigDecimal.valueOf(1)),
               Vendor("id", "name"),
               "ff",
               true,
               "items1",
               "aac1",
               "descr1",
               "image1",
               ReferenceItem("GTU", "SKI"),
               ItemDimensions(
                   "KG",
                   0.5, "IN", 1.0, 1.0, 1.0
               ),
               "",
               MetaData("udf1", "udf2")
           )
       )
       return itemsList
   }

   //Set Order object
   private fun getOrder(): OrderObject {
       return OrderObject(
           BigDecimal.valueOf(38),
           settingsManager?.getString("key_sdk_transaction_currency", "KWD").toString(),
           settingsManager?.getCustomer(),
           settingsManager?.getDynamicPaymentItems(),
           ArrayList(
               setOf(
                   TaxObject(
                       "Tax1",
                       "tax described",
                       AmountModificator(AmountModificatorType.FIXED, BigDecimal.valueOf(0))
                   )
               )
           ),
           settingsManager?.getShippingObject(),
           settingsManager?.getString("key_merchant_id", "1124340")?.let { Merchant(it) },
           MetaData("udf1", "udf2"),
           ReferId("")
       )
   }

   private fun getOrderWithoutAdd(): OrderObject {
       return OrderObject(
           BigDecimal.valueOf(38),
           settingsManager?.getString("key_sdk_transaction_currency", "KWD").toString(),
           settingsManager?.getCustomer(),
           settingsManager?.getDynamicPaymentItems(),
           ArrayList(
               setOf(
                   TaxObject(
                       "Tax1",
                       "tax described",
                       AmountModificator(AmountModificatorType.FIXED, BigDecimal.valueOf(0))
                   )
               )
           ),
           null,
           settingsManager?.getString("key_merchant_id", "1124340")?.let { Merchant(it) },
           MetaData("udf1", "udf2"),
           ReferId("")
       )
   }

   private fun checkAndroidVersion() {
       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
           checkAndRequestPermissions()
       } else {
           // code for lollipop and pre-lollipop devices
       }
   }


   private fun checkAndRequestPermissions(): Boolean {
       val camera = ContextCompat.checkSelfPermission(
           this,
           Manifest.permission.CAMERA
       )
       val wtite =
           ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
       val read =
           ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
       val listPermissionsNeeded: MutableList<String> = ArrayList()
       if (wtite != PackageManager.PERMISSION_GRANTED) {
           listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
       }
       if (camera != PackageManager.PERMISSION_GRANTED) {
           listPermissionsNeeded.add(Manifest.permission.CAMERA)
       }
       if (read != PackageManager.PERMISSION_GRANTED) {
           listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE)
       }
       if (!listPermissionsNeeded.isEmpty()) {
           ActivityCompat.requestPermissions(
               this,
               listPermissionsNeeded.toTypedArray(),
               REQUEST_ID_MULTIPLE_PERMISSIONS
           )
           return false
       }
       return true
   }

   override fun onTransactionSuccess(p0: Transaction?) {
       Toast.makeText(this, "onTransactionSuccess" + p0, Toast.LENGTH_SHORT).show()
   }

   override fun onTransactionFail(p0: Transaction?) {
       Toast.makeText(this, "onTransactionFail" + p0, Toast.LENGTH_SHORT).show()

   }

}




