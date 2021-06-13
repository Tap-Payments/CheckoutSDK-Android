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
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import company.tap.checkout.internal.api.models.PhoneNumber

import company.tap.checkout.open.CheckoutFragment


import company.tap.checkout.open.controller.SDKSession
import company.tap.checkout.open.enums.CardType
import company.tap.checkout.open.models.Customer
import company.tap.checkout.open.models.Receipt
import company.tap.checkout.open.models.TapCurrency
import company.tap.taplocalizationkit.LocalizationManager
import company.tap.tapuilibrary.themekit.ThemeManager
import company.tap.tapuilibrary.uikit.models.DialogConfigurations
import company.tap.tapuilibrary.uikit.views.TapBottomSheetDialog.Companion.TAG
import java.math.BigDecimal
import java.util.*


class MainActivity : AppCompatActivity() {

    var sdkSession:SDKSession= SDKSession()

    private val modalBottomSheet = CheckoutFragment()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (ThemeManager.currentTheme.isNotEmpty() && ThemeManager.currentTheme.contains("dark"))
         ThemeManager.loadTapTheme(resources, R.raw.defaultdarktheme, "darktheme")
        else if(ThemeManager.currentTheme.isNotEmpty() && !ThemeManager.currentTheme.contains("dark"))
        ThemeManager.loadTapTheme(resources, R.raw.defaultlighttheme, "lighttheme")
        else ThemeManager.loadTapTheme(resources, R.raw.defaultlighttheme, "lighttheme")


        //  setTheme(R.style.AppThemeBlack)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        setLocale(this, LocalizationManager.getLocale(this).language)
        LocalizationManager.loadTapLocale(resources, R.raw.lang)

         configureSDKSession()
        }

    private fun configureSDKSession() {
        // Instantiate SDK Session



        // pass your activity as a session delegate to listen to SDK internal payment process follow

        // pass your activity as a session delegate to listen to SDK internal payment process follow
        //sdkSession.addSessionDelegate(this) //** Required **


        // initiate PaymentDataSource

        // initiate PaymentDataSource
      // sdkSession.instantiatePaymentDataSource() //** Required **


        // set transaction currency associated to your account

        // set transaction currency associated to your account
        sdkSession.setTransactionCurrency(TapCurrency("KWD")) //** Required **


        // Using static CustomerBuilder method available inside TAP Customer Class you can populate TAP Customer object and pass it to SDK

        // Using static CustomerBuilder method available inside TAP Customer Class you can populate TAP Customer object and pass it to SDK
        sdkSession.setCustomer(setCustomer()) //** Required **


        // Set Total Amount. The Total amount will be recalculated according to provided Taxes and Shipping

        // Set Total Amount. The Total amount will be recalculated according to provided Taxes and Shipping
        sdkSession.setAmount(BigDecimal(1)) //** Required **


        // Set Payment Items array list

        // Set Payment Items array list
        sdkSession.setPaymentItems(ArrayList()) // ** Optional ** you can pass empty array list


//       sdkSession.setPaymentType("CARD");   //** Merchant can pass paymentType

        // Set Taxes array list


//       sdkSession.setPaymentType("CARD");   //** Merchant can pass paymentType

        // Set Taxes array list
        sdkSession.setTaxes(ArrayList()) // ** Optional ** you can pass empty array list


        // Set Shipping array list

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


         sdkSession.setDefaultCardHolderName("TEST TAP"); // ** Optional ** you can pass default CardHolderName of the user .So you don't need to type it.
         sdkSession.isUserAllowedToEnableCardHolderName(false); // ** Optional ** you can enable/ disable  default CardHolderName .

    }

    fun openBottomSheet(view: View) {

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
    fun setCustomer(): Customer { // test customer id cus_Kh1b4220191939i1KP2506448
        val customer: Customer? = null
        //if (customer != null) customer.phone else Phone(965, 69045932)
        return Customer(
            null,"firstname", "middlename",
            "lastname", "abcd@gmail.com",
            PhoneNumber("00965", "9090909090"), "description",
        )

    }

}




