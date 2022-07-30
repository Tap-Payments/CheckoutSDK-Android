package company.tap.checkout.internal.utils

import android.app.Activity
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.android.gms.wallet.PaymentsClient
import com.google.android.gms.wallet.Wallet
import com.google.android.gms.wallet.WalletConstants
import company.tap.checkout.internal.api.models.PaymentOption
import company.tap.checkout.open.data_managers.PaymentDataSource
import company.tap.checkout.open.enums.SdkMode
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.math.BigDecimal
import java.util.*

/**
 * Created by AhlaamK on 4/10/22.

Copyright (c) 2022    Tap Payments.
All rights reserved.
 **/
/**
 * Contains helper static methods for dealing with the Payments API.
 *
 * Many of the parameters used in the code are optional and are set here merely to call out their
 * existence. Please consult the documentation to learn more and feel free to remove ones not
 * relevant to your implementation.
 */
object PaymentsUtil {
    val CENTS_IN_A_UNIT: BigDecimal = BigDecimal(100.0)

    /**
     * Create a Google Pay API base request object with properties used in all requests.
     *
     * @return Google Pay API base request object.
     * @throws JSONException
     */
    private val baseRequest = JSONObject().apply {
        put("apiVersion", PaymentDataSource.getGooglePaymentOptions()?.get(0)?.apiVersion)
        put("apiVersionMinor", PaymentDataSource.getGooglePaymentOptions()?.get(0)?.apiVersionMinor)
    }

    /**
     * Gateway Integration: Identify your gateway and your app's gateway merchant identifier.
     *
     *
     * The Google Pay API response will return an encrypted payment method capable of being charged
     * by a supported gateway after payer authorization.
     *
     *
     * TODO: Check with your gateway on the parameters to pass and modify them in Constants.java.
     *
     * @return Payment data tokenization for the CARD payment method.
     * @throws JSONException
     * @see [PaymentMethodTokenizationSpecification](https://developers.google.com/pay/api/android/reference/object.PaymentMethodTokenizationSpecification)
     */
    @Throws(JSONException::class)
      fun getGatewayTokenizationSpecification(): JSONObject? {
        //todo :25Jul22 replace value coming from API>>PENDING GATEWAY ID
        return object : JSONObject() {
            init {
                put("type", "PAYMENT_GATEWAY")
                put("parameters", object : JSONObject() {
                    init {
                        assert(PaymentDataSource.getGooglePaymentOptions() != null)
                      //  put("gateway", "tappayments")
                          put("gateway", PaymentDataSource.getGooglePaymentOptions()?.get(0)?.gatewayName)
                        put("gatewayMerchantId",
                            PaymentDataSource.getGooglePaymentOptions()?.get(0)?.gatewayMerchantId
                        )
                    }
                })
            }
        }
    }

    /**
     * `DIRECT` Integration: Decrypt a response directly on your servers. This configuration has
     * additional data security requirements from Google and additional PCI DSS compliance complexity.
     *
     *
     * Please refer to the documentation for more information about `DIRECT` integration. The
     * type of integration you use depends on your payment processor.
     *
     * @return Payment data tokenization for the CARD payment method.
     * @throws JSONException
     * @see [PaymentMethodTokenizationSpecification](https://developers.google.com/pay/api/android/reference/object.PaymentMethodTokenizationSpecification)
     */
    private fun directTokenizationSpecification(): JSONObject {
        if (Constants.DIRECT_TOKENIZATION_PUBLIC_KEY == "REPLACE_ME" ||
            (Constants.DIRECT_TOKENIZATION_PARAMETERS.isEmpty() ||
                    Constants.DIRECT_TOKENIZATION_PUBLIC_KEY.isEmpty())) {

            throw RuntimeException(
                "Please edit the Constants.java file to add protocol version & public key.")
        }

        return JSONObject().apply {
            put("type", "DIRECT")
            put("parameters", JSONObject(Constants.DIRECT_TOKENIZATION_PARAMETERS as Map<*, *>))
        }
    }

    /**
     * Card networks supported by your app and your gateway.
     *
     *
     * TODO: Confirm card networks supported by your app and gateway & update in Constants.java.
     *
     * @return Allowed card networks
     * @see [CardParameters](https://developers.google.com/pay/api/android/reference/object.CardParameters)
     */
    @get:RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private val allowedCardNetworks: JSONArray
        get() {
            // System.out.println("SUPPORTED_NETWORKS"+Constants.SUPPORTED_NETWORKS);
            var newlist: List<String> = ArrayList()

            var listWithoutDuplicates: ArrayList<String> = ArrayList()
            if (PaymentDataSource.getAvailablePaymentOptionsCardBrands() != null) {
                //  ArrayList<CardBrand> allowedCardBrands = PaymentDataManager.getInstance().getAvailablePaymentOptionsCardBrands();
                val allowedCardBrands: ArrayList<PaymentOption> ?=
                    PaymentDataSource.getAvailablePaymentOptionsCardBrands()
                var newValue: String? = null
                for (i in allowedCardBrands?.indices!!) {
                    if (allowedCardBrands[i] != null) {
                        newValue = allowedCardBrands[i]?.brand.toString().toUpperCase()
                    }
                    if (newValue!!.contains("AMERICANEXPRESS")) {
                        newValue = "AMEX"
                    }
                    newlist.plusElement(newValue)
                }
                val hashSet: LinkedHashSet<String?> = LinkedHashSet(newlist)
                listWithoutDuplicates = ArrayList(hashSet)
            }

            return  JSONArray(Constants.SUPPORTED_NETWORKS)
            //  return JSONArray(listWithoutDuplicates)
        }
    /**
     * Card authentication methods supported by your app and your gateway.
     *
     *
     * TODO: Confirm your processor supports Android device tokens on your supported card networks
     * and make updates in Constants.java.
     *
     * @return Allowed card authentication methods.
     * @see [CardParameters](https://developers.google.com/pay/api/android/reference/object.CardParameters)
     */
    private val allowedCardAuthMethods = JSONArray(Constants.SUPPORTED_METHODS)

    /**
     * Describe your app's support for the CARD payment method.
     *
     *
     * The provided properties are applicable to both an IsReadyToPayRequest and a
     * PaymentDataRequest.
     *
     * @return A CARD PaymentMethod object describing accepted cards.
     * @throws JSONException
     * @see [PaymentMethod](https://developers.google.com/pay/api/android/reference/object.PaymentMethod)
     */
    // Optionally, you can add billing address/phone number associated with a CARD payment method.
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Throws(JSONException::class)
    private  fun getBaseCardPaymentMethod(): JSONObject {
        val capCardBrandList: MutableList<String?> = ArrayList()
        val cardPaymentMethod = JSONObject()
        cardPaymentMethod.put("type", "CARD")
        // System.out.println("Payment data"+PaymentDataSource.getInstance().getGooglePaymentOptions().get(0).getSupportedCardBrands());
        val parameters = JSONObject()
        parameters.put(
            "allowedAuthMethods",
            JSONArray(
                PaymentDataSource.getGooglePaymentOptions()?.get(0)?.allowed_auth_methods
            )
        )
        // parameters.put("allowedAuthMethods", getAllowedCardAuthMethods());
        // parameters.put("allowedCardNetworks", getAllowedCardNetworks());

        var newValue: String? = null
        //Logic done to get cardbrands in capitals
        for (i in 0 until PaymentDataSource.getGooglePaymentOptions()?.get(0)?.supportedCardBrands?.size!!) {
            if (PaymentDataSource.getGooglePaymentOptions()?.get(0)?.supportedCardBrands?.get(i) != null
            ) {
                newValue = java.lang.String.valueOf(
                    PaymentDataSource.getGooglePaymentOptions()?.get(0)?.supportedCardBrands?.get(i)
                ).toUpperCase()
            }
            capCardBrandList.add(i, newValue)
        }
        parameters.put("allowedCardNetworks", JSONArray(capCardBrandList))
        cardPaymentMethod.put("parameters", parameters)
        return cardPaymentMethod
    }

    /**
     * Describe the expected returned payment data for the CARD payment method
     *
     * @return A CARD PaymentMethod describing accepted cards and optional fields.
     * @throws JSONException
     * @see [PaymentMethod](https://developers.google.com/pay/api/android/reference/object.PaymentMethod)
     */
    private fun cardPaymentMethod(): JSONObject {
        val cardPaymentMethod = getBaseCardPaymentMethod()
        cardPaymentMethod?.put("tokenizationSpecification", getGatewayTokenizationSpecification())


        return cardPaymentMethod
    }

    /**
     * An object describing accepted forms of payment by your app, used to determine a viewer's
     * readiness to pay.
     *
     * @return API version and payment methods supported by the app.
     * @see [IsReadyToPayRequest](https://developers.google.com/pay/api/android/reference/object.IsReadyToPayRequest)
     */
    fun isReadyToPayRequest(): JSONObject? {
        return try {
            baseRequest.apply {
                put("allowedPaymentMethods", JSONArray().put(getBaseCardPaymentMethod()))
            }

        } catch (e: JSONException) {
            null
        }
    }

    /**
     * Information about the merchant requesting payment information
     *
     * @return Information about the merchant.
     * @throws JSONException
     * @see [MerchantInfo](https://developers.google.com/pay/api/android/reference/object.MerchantInfo)
     */
    private val merchantInfo: JSONObject =
        JSONObject().put("merchantName", PaymentDataSource.getInitOptionsResponse()?.merchant?.name)

    /**
     * Creates an instance of [PaymentsClient] for use in an [Activity] using the
     * environment and theme set in [Constants].
     *
     * @param activity is the caller's activity.
     */
    fun createPaymentsClient(activity: Activity): PaymentsClient {
        var walletOptions: Wallet.WalletOptions? = null
        if (PaymentDataSource.getSDKMode() != null) {
            if (PaymentDataSource.getSDKMode() == SdkMode.SAND_BOX) {
                walletOptions =
                    Wallet.WalletOptions.Builder().setEnvironment(WalletConstants.ENVIRONMENT_TEST).build()
            } else if (PaymentDataSource.getSDKMode() == SdkMode.PRODUCTION
            ) {
                walletOptions =
                    Wallet.WalletOptions.Builder().setEnvironment(WalletConstants.ENVIRONMENT_PRODUCTION)
                        .build()
            } else walletOptions =
                Wallet.WalletOptions.Builder().setEnvironment(WalletConstants.ENVIRONMENT_TEST).build()
        }
        return Wallet.getPaymentsClient((activity), (walletOptions!!))
    }

    /**
     * Provide Google Pay API with a payment amount, currency, and amount status.
     *
     * @return information about the requested payment.
     * @throws JSONException
     * @see [TransactionInfo](https://developers.google.com/pay/api/android/reference/object.TransactionInfo)
     */
    @Throws(JSONException::class)
    private fun getTransactionInfo(price: String): JSONObject {
        return JSONObject().apply {
            put("totalPrice", price)
            put("totalPriceStatus", "FINAL")
            put("countryCode",PaymentDataSource.getGooglePaymentOptions()?.get(0)?.acquirerCountryCode)
            put("currencyCode", PaymentDataSource.getSelectedCurrency())
            put("checkoutOption", "COMPLETE_IMMEDIATE_PURCHASE")
        }
    }

    /**
     * An object describing information requested in a Google Pay payment sheet
     *
     * @return Payment data expected by your app.
     * @see [PaymentDataRequest](https://developers.google.com/pay/api/android/reference/object.PaymentDataRequest)
     */
    fun getPaymentDataRequest(priceCemts: Long): JSONObject? {
        return try {
            baseRequest.apply {
                put("allowedPaymentMethods", JSONArray().put(cardPaymentMethod()))
                put("transactionInfo", getTransactionInfo(priceCemts.toString()))
                put("merchantInfo", merchantInfo)

                // An optional shipping address requirement is a top-level property of the
                // PaymentDataRequest JSON object.

            }
        } catch (e: JSONException) {
            null
        }
    }
}


/**
 * Converts cents to a string format accepted by [PaymentsUtil.getPaymentDataRequest].
 *
 * @param cents value of the price.
 */
fun centsToString(cents: Long): String {
    return BigDecimal(cents)
        .toString()
}


