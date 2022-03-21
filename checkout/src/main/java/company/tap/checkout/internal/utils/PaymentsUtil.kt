package company.tap.checkout.internal.utils

import android.app.Activity
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.android.gms.wallet.PaymentsClient
import com.google.android.gms.wallet.Wallet
import com.google.android.gms.wallet.Wallet.WalletOptions
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
 * Created by AhlaamK on 3/20/22.

Copyright (c) 2022    Tap Payments.
All rights reserved.
 **/
/**
* Contains helper static methods for dealing with the Payments API.
*
* <p>Many of the parameters used in the code are optional and are set here merely to call out their
* existence. Please consult the documentation to learn more and feel free to remove ones not
* relevant to your implementation.
*/
class PaymentsUtil {

    val CENTS_IN_A_UNIT: BigDecimal = BigDecimal(100.0)

    /**
     * Create a Google Pay API base request object with properties used in all requests.
     *
     * @return Google Pay API base request object.
     * @throws JSONException
     */
    @get:Throws(JSONException::class)
    private val baseRequest: JSONObject
         get() = JSONObject().put("apiVersion", 2).put("apiVersionMinor", 0)

    /**
     * Creates an instance of [PaymentsClient] for use in an [Activity] using the
     * environment and theme set in [Constants].
     *
     * @param activity is the caller's activity.
     */
    fun createPaymentsClient(activity: Activity): PaymentsClient {
        var walletOptions: WalletOptions? = null
        if (PaymentDataSource.getSDKMode() != null) {
            if (PaymentDataSource.getSDKMode() == SdkMode.SAND_BOX) {
                walletOptions =
                    WalletOptions.Builder().setEnvironment(WalletConstants.ENVIRONMENT_TEST).build()
            } else if (PaymentDataSource.getSDKMode() == SdkMode.PRODUCTION
            ) {
                walletOptions =
                    WalletOptions.Builder().setEnvironment(WalletConstants.ENVIRONMENT_PRODUCTION)
                        .build()
            } else walletOptions =
                WalletOptions.Builder().setEnvironment(WalletConstants.ENVIRONMENT_TEST).build()
        }
        return Wallet.getPaymentsClient((activity), (walletOptions)!!)
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
    @get:Throws(JSONException::class)
    private val gatewayTokenizationSpecification: JSONObject
        private get() = object : JSONObject() {
            init {
                put("type", "PAYMENT_GATEWAY")
                put("parameters", object : JSONObject() {
                    init {
                        put("gateway", "tappayments")
                        put("gatewayMerchantId", "googletest")
                    }
                })
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
    @get:Throws(JSONException::class, RuntimeException::class)
    private val directTokenizationSpecification: JSONObject?
        private get() {
            if ((Constants.DIRECT_TOKENIZATION_PARAMETERS.isEmpty()
                        || Constants.DIRECT_TOKENIZATION_PUBLIC_KEY.isEmpty()
                        || (Constants.DIRECT_TOKENIZATION_PUBLIC_KEY == null
                        ) || (Constants.DIRECT_TOKENIZATION_PUBLIC_KEY === "REPLACE_ME"))
            ) {
                throw RuntimeException(
                    "Please edit the Constants.java file to add protocol version & public key."
                )
            }
            val tokenizationSpecification: JSONObject = JSONObject()
            tokenizationSpecification.put("type", "DIRECT")
            val parameters: JSONObject = JSONObject(Constants.DIRECT_TOKENIZATION_PARAMETERS as Map<*, *>)
            tokenizationSpecification.put("parameters", parameters)
            return tokenizationSpecification
        }//  ArrayList<CardBrand> allowedCardBrands = PaymentDataManager.getInstance().getAvailablePaymentOptionsCardBrands();

    //  return new JSONArray(Constants.SUPPORTED_NETWORKS);
// System.out.println("SUPPORTED_NETWORKS"+Constants.SUPPORTED_NETWORKS);

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

            //  return new JSONArray(Constants.SUPPORTED_NETWORKS);
            return JSONArray(listWithoutDuplicates)
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
    private val allowedCardAuthMethods: JSONArray?
        private get() {
            return JSONArray(Constants.SUPPORTED_METHODS)
        }// Optionally, you can add billing address/phone number associated with a CARD payment method.
    //  parameters.put("billingAddressRequired", true);

    //  JSONObject billingAddressParameters = new JSONObject();
    //  billingAddressParameters.put("format", "FULL");

    //  parameters.put("billingAddressParameters", billingAddressParameters);

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
    @get:Throws(JSONException::class)
    @get:RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private val baseCardPaymentMethod: JSONObject
        private get() {
            val cardPaymentMethod: JSONObject = JSONObject()
            cardPaymentMethod.put("type", "CARD")
            val parameters: JSONObject = JSONObject()
            parameters.put("allowedAuthMethods", allowedCardAuthMethods)
            parameters.put("allowedCardNetworks", allowedCardNetworks)
            // Optionally, you can add billing address/phone number associated with a CARD payment method.
            //  parameters.put("billingAddressRequired", true);

            //  JSONObject billingAddressParameters = new JSONObject();
            //  billingAddressParameters.put("format", "FULL");

            //  parameters.put("billingAddressParameters", billingAddressParameters);
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
    @get:Throws(JSONException::class)
    private val cardPaymentMethod: JSONObject
        private get() {
            val cardPaymentMethod: JSONObject = baseCardPaymentMethod
            cardPaymentMethod.put("tokenizationSpecification", gatewayTokenizationSpecification)
            return cardPaymentMethod
        }

    /**
     * An object describing accepted forms of payment by your app, used to determine a viewer's
     * readiness to pay.
     *
     * @return API version and payment methods supported by the app.
     * @see [IsReadyToPayRequest](https://developers.google.com/pay/api/android/reference/object.IsReadyToPayRequest)
     */
    @get:RequiresApi(api = Build.VERSION_CODES.N)
    val isReadyToPayRequest: Optional<JSONObject>
        get() {
            try {
                val isReadyToPayRequest: JSONObject = baseRequest
                isReadyToPayRequest.put(
                    "allowedPaymentMethods", JSONArray().put(baseCardPaymentMethod)
                )
                return Optional.of(isReadyToPayRequest)
            } catch (e: JSONException) {
                return Optional.empty()
            }
        }

    /**
     * Provide Google Pay API with a payment amount, currency, and amount status.
     *
     * @return information about the requested payment.
     * @throws JSONException
     * @see [TransactionInfo](https://developers.google.com/pay/api/android/reference/object.TransactionInfo)
     */
    @Throws(JSONException::class)
    private fun  getTransactionInfo(price: String): JSONObject{
        val transactionInfo: JSONObject = JSONObject()
        transactionInfo.put("totalPrice", price)
        transactionInfo.put("totalPriceStatus", "FINAL")
        transactionInfo.put("countryCode", Constants.COUNTRY_CODE)
        transactionInfo.put("currencyCode", Constants.CURRENCY_CODE)
        transactionInfo.put("checkoutOption", "COMPLETE_IMMEDIATE_PURCHASE")
        return transactionInfo
    }

    /**
     * Information about the merchant requesting payment information
     *
     * @return Information about the merchant.
     * @throws JSONException
     * @see [MerchantInfo](https://developers.google.com/pay/api/android/reference/object.MerchantInfo)
     */
    @get:Throws(JSONException::class)
     val merchantInfo: JSONObject
        get() {
            return JSONObject().put("merchantName", "Example Merchant")
        }

    /**
     * An object describing information requested in a Google Pay payment sheet
     *
     * @return Payment data expected by your app.
     * @see [PaymentDataRequest](https://developers.google.com/pay/api/android/reference/object.PaymentDataRequest)
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    fun getPaymentDataRequest(priceCents: Long): Optional<JSONObject> {
        val price: String = centsToString(priceCents)
        try {
            val paymentDataRequest: JSONObject = baseRequest
            paymentDataRequest.put(
                "allowedPaymentMethods", JSONArray().put(cardPaymentMethod)
            )
            paymentDataRequest.put("transactionInfo", getTransactionInfo(price))
            paymentDataRequest.put("merchantInfo", merchantInfo)

            /* An optional shipping address requirement is a top-level property of the PaymentDataRequest
      JSON object. */
            // paymentDataRequest.put("shippingAddressRequired", true);

            // JSONObject shippingAddressParameters = new JSONObject();
            // shippingAddressParameters.put("phoneNumberRequired", false);

            //  JSONArray allowedCountryCodes = new JSONArray(Constants.SHIPPING_SUPPORTED_COUNTRIES);

            // shippingAddressParameters.put("allowedCountryCodes", allowedCountryCodes);
            //  paymentDataRequest.put("shippingAddressParameters", shippingAddressParameters);
            return Optional.of(paymentDataRequest)
        } catch (e: JSONException) {
            return Optional.empty()
        }
    }

    /**
     * Converts cents to a string format accepted by [PaymentsUtil.getPaymentDataRequest].
     *
     * @param cents value of the price in cents.
     */
    fun centsToString(cents: Long): String {
        return BigDecimal(cents)
            .toString()
    }
}
