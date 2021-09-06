package company.tap.checkoutsdk.manager

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import company.tap.checkout.internal.api.enums.AmountModificatorType
import company.tap.checkout.internal.api.enums.AuthorizeActionType
import company.tap.checkout.internal.api.enums.Measurement
import company.tap.checkout.internal.api.models.AmountModificator
import company.tap.checkout.internal.api.models.PhoneNumber
import company.tap.checkout.internal.api.models.Quantity
import company.tap.checkout.open.enums.SdkMode
import company.tap.checkout.open.enums.TransactionMode
import company.tap.checkout.open.models.*
import company.tap.checkoutsdk.CustomerViewModel
import java.math.BigDecimal
import java.util.*

/**
 * Created by AhlaamK on 9/6/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
@SuppressLint("StaticFieldLeak")
object SettingsManager {
    private var pref: SharedPreferences? = null
    private var context: Context? = null
    fun setPref(ctx: Context?) {
        context = ctx
        if (pref == null) pref = PreferenceManager.getDefaultSharedPreferences(ctx)
    }

    fun saveCustomer(
        name: String,
        middle: String,
        last: String,
        email: String,
        sdn: String,
        mobile: String,
        ctx: Context
    ) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(ctx)
        val gson = Gson()
        val response = preferences.getString("customer", "")
        var customersList: ArrayList<CustomerViewModel?>? = gson.fromJson(
            response,
            object : TypeToken<List<CustomerViewModel?>?>() {}.getType()
        )
        if (customersList == null) customersList = ArrayList<CustomerViewModel?>()
        customersList.add(last.let {

            CustomerViewModel(null, name, middle, it, email, sdn, mobile)

        })
        val data: String = gson.toJson(customersList)
        writeCustomersToPreferences(data, preferences)
    }

    fun editCustomer(
        oldCustomer: CustomerViewModel?,
        newCustomer: CustomerViewModel,
        ctx: Context?
    ) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(ctx)
        val gson = Gson()
        val response = preferences.getString("customer", "")
        val customersList: ArrayList<CustomerViewModel> = gson.fromJson(
            response,
            object : TypeToken<List<CustomerViewModel?>?>() {}.getType()
        )
        if (customersList != null) {
            val customerRef: String? = customersList[0].getRef()
            customersList.clear()
            newCustomer.getMiddleName()?.let {
                CustomerViewModel(
                    customerRef,
                    newCustomer.getName(),
                    it,
                    newCustomer.getLastName()!!,
                    newCustomer.getEmail(),
                    newCustomer.getSdn()!!,
                    newCustomer.getMobile()
                )
            }?.let {
                customersList.add(
                    it
                )
            }
            val data: String = gson.toJson(customersList)
            writeCustomersToPreferences(data, preferences)
        } else {
            if (ctx != null) {
                newCustomer?.getSdn()?.let {
                    saveCustomer(
                        newCustomer.getName(),
                        newCustomer?.getMiddleName()!!,
                        newCustomer?.getLastName()!!,
                        newCustomer.getEmail(),
                        it,
                        newCustomer.getMobile(), ctx
                    )
                }
            }
        }
    }

    private fun writeCustomersToPreferences(data: String, preferences: SharedPreferences) {
        val editor = preferences.edit()
        editor.putString("customer", data)
        editor.commit()
    }

    fun getRegisteredCustomers(ctx: Context?): List<CustomerViewModel> {
        val preferences = PreferenceManager.getDefaultSharedPreferences(ctx)
        val gson = Gson()
        val response = preferences.getString("customer", "")
        val customersList: ArrayList<CustomerViewModel>? = gson.fromJson(
            response,
            object : TypeToken<List<CustomerViewModel?>?>() {}.getType()
        )
        return customersList ?: ArrayList<CustomerViewModel>()
    }


    //////////////////////////////////////   Get Payment Settings ////////////////////////////////
    fun getCustomer(): TapCustomer? {
        val customer: TapCustomer
        val gson = Gson()
        val response = pref!!.getString("customer", "")
        println(" get customer: $response")
        val customersList = gson.fromJson<ArrayList<CustomerViewModel>>(
            response,
            object : TypeToken<List<CustomerViewModel?>?>() {}.type
        )

        // check if customer id is in pref.
        customer = if (customersList != null) {
            System.out.println("preparing data source with customer ref :" + customersList[0].getRef())
            return TapCustomer(
                customersList[0].getRef(),
                customersList[0].getName(),
                customersList[0].getMiddleName(),
                customersList[0].getLastName(),
                customersList[0].getEmail(),
                PhoneNumber(customersList[0].getSdn(), customersList[0].getMobile()),
                "meta"
            )
        } else {
            println(" paymentResultDataManager.getCustomerRef(context) null")
            //65562630
            return TapCustomer(
                "cus_TS012520211349Za012907577", "ahlaam", "middlename",
                "lastname", "abcd@gmail.com",
                PhoneNumber("00965", "69045932"), "description",
            )
        }
        //  65562630

    }

    private fun getPaymentItems(): ArrayList<PaymentItem>? {
        val items: ArrayList<PaymentItem> = ArrayList<PaymentItem>()
        items.add(
            PaymentItem(
                "Items1",
                "Description for test item #1",
                Quantity(Measurement.UNITS, Measurement.MASS.name, BigDecimal.valueOf(1)),
                BigDecimal.valueOf(
                    1
                ),
                AmountModificator(AmountModificatorType.FIXED, BigDecimal.ZERO),
                null
            )
        )
        return items
    }
    fun getTaxes(): ArrayList<Tax> {
        val taxes: ArrayList<Tax> = ArrayList<Tax>()
        taxes.add(
            Tax(
                "Test tax #1",
                "Test tax #1 description",
                AmountModificator(AmountModificatorType.FIXED, BigDecimal.TEN)
            )
        )
        return taxes
    }

    fun getShippingList(): ArrayList<Shipping> {
        val shipping: ArrayList<Shipping> = ArrayList<Shipping>()
        shipping.add(
            Shipping(
                "Test shipping #1",
                "Test shipping description #1",
                BigDecimal.ONE
            )
        )
        return shipping
    }

    fun getPostURL(): String {
//        Base URL
        return "https://tap.company"
    }

    fun getPaymentDescription(): String {
        return "Test payment description."
    }

    fun getPaymentMetaData(): HashMap<String, String> {
        val paymentMetadata = HashMap<String, String>()
        paymentMetadata["metadata_key_1"] = "metadata value 1"
        return paymentMetadata
    }

    fun getPaymentReference(): Reference {
        return Reference(
            "acquirer_1",
            "gateway_1",
            "payment_1",
            "track_1",
            "transaction_1",
            "order_1"
        )
    }

    fun getPaymentStatementDescriptor(): String {
        return "Test payment statement descriptor."
    }

    fun getReceipt(): Receipt {
        return Receipt(true, true)
    }

    fun getAuthorizeAction(): AuthorizeAction {
        return AuthorizeAction(AuthorizeActionType.VOID, 10)
    }

    fun getDestination(): Destinations {
        val destinations: ArrayList<Destination> = ArrayList<Destination>()
        destinations.add(
            Destination(
                "1014",  /// destination unique identifier
                BigDecimal(10),  // Amount to be transferred to the destination account
                //  TapCurrency("kwd"),  //currency code (three digit ISO format)
                "kwd",  //currency code (three digit ISO format)
                "please deduct 10 kd for this account",  //Description about the transfer
                "" //Merchant reference number to the destination
            )
        )
        return Destinations(
            BigDecimal(10),  // total amount, transferred to the destination account
            // TapCurrency("kwd"),  // transfer currency code
            "kwd",  // transfer currency code
            1,  //number of destinations trabsfer involved
            destinations
        ) //List of destinations object
    }
    ////////////////////////////////////////////////// Specific Settings ////////////////////////////
    /**
     * Session Data Source
     */
    fun getSDKOperationMode(key: String?): SdkMode {
        val op_mode = pref!!.getString(key, SdkMode.SAND_BOX.name)
        return if (op_mode == SdkMode.SAND_BOX.name) SdkMode.SAND_BOX else SdkMode.PRODUCTION
    }

    /**
     * get Transaction mode
     * @param key
     * @return
     */
    fun getTransactionsMode(key: String): TransactionMode {
        val trx_mode = pref?.getString(key, TransactionMode.PURCHASE.name)
        println("trx_mode value" + trx_mode)
        if (trx_mode.equals(
                TransactionMode.PURCHASE.name,
                ignoreCase = true
            )
        ) return TransactionMode.PURCHASE
        if (trx_mode.equals(
                TransactionMode.AUTHORIZE_CAPTURE.name,
                ignoreCase = true
            )
        ) return TransactionMode.AUTHORIZE_CAPTURE
         if (trx_mode.equals(
                 TransactionMode.TOKENIZE_CARD.name,
                 ignoreCase = true
             )
        ) TransactionMode.TOKENIZE_CARD
        return if (trx_mode.equals(
                TransactionMode.SAVE_CARD.name,
                ignoreCase = true
            )
        ) TransactionMode.SAVE_CARD
        else TransactionMode.PURCHASE
    }

    /**
     * get transaction currency
     * @param key
     * @return
     */
    fun getTransactionCurrency(key: String?): TapCurrency {
        val trx_curr = pref!!.getString(key, "kwd")
        Log.d("Settings Manager", "trx_curr :" + trx_curr!!.trim { it <= ' ' })
        return if (trx_curr != null && !"".equals(
                trx_curr.trim { it <= ' ' },
                ignoreCase = true
            )
        ) TapCurrency(trx_curr) else TapCurrency("kwd")
    }

    //////////////////////////////////////////////////  General ////////////////////////////////
    /**
     * Get Font name saved in session or return default
     * @param key
     * @param defaultFont
     * @return
     */
    fun getFont(key: String?, defaultFont: String?): String? {
        println("pref: " + pref!!.getString(key, defaultFont))
        return pref!!.getString(key, defaultFont)
    }


    /**
     *
     * @param key
     * @return
     */
    fun getBoolean(key: String?, defaultValue: Boolean): Boolean {
        return pref!!.getBoolean(key, defaultValue)
    }

    /**
     *
     * @param key
     * @param defaultValue
     * @return
     */
    @SuppressLint("StaticFieldLeak")
    fun getString(key: String?, defaultValue: String?): String? {
        return pref!!.getString(key, defaultValue)
    }

    fun getInt(key: String?, defaultValue: Int): Int? {
        return pref?.getInt(key, defaultValue)
    }


    //Set topup object

      fun getTopUp(): TopUp? {
        return TopUp(
            null,
            "wal_7nTwK44211030uxtI115c6T710",
            null,
            null,
            BigDecimal.valueOf(30),
            "kwd",
            null,
            null,
            null,
            TopUpApplication(BigDecimal.valueOf(40), "kwd"),
            null,
            TopupPost("wwww.google.com"),
            null
        )
    }

}
