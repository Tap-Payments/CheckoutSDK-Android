package company.tap.checkout.internal.api.requests

import androidx.annotation.Nullable
import androidx.annotation.RestrictTo
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import company.tap.checkout.internal.utils.AmountCalculator
import company.tap.checkout.open.enums.TransactionMode
import company.tap.checkout.open.models.*
import java.math.BigDecimal
import java.util.*

/**
 * Created by AhlaamK on 6/15/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
@RestrictTo(RestrictTo.Scope.LIBRARY)
open class PaymentOptionsRequest(
    transactionMode: TransactionMode?,
    amount: BigDecimal?,
    shipping: ArrayList<Shipping>?,
    taxes: ArrayList<Tax>?,
    destinations:Destinations?,
    currency: String?,
    customer: String?,
    merchant_id: String?,
    payment_type: String,
    topup :TopUp?,
    orderObject: OrderObject?

) {
    @SerializedName("transaction_mode")
    @Expose
    private val transactionMode: TransactionMode = transactionMode ?: TransactionMode.PURCHASE

    @SerializedName("items")
    @Expose
    private var items: ArrayList<PaymentItem>? = null

    @SerializedName("shipping")
    @Expose
    private val shipping: ArrayList<Shipping>? = shipping

    @SerializedName("taxes")
    @Expose
    private val taxes: ArrayList<Tax>? = taxes


    @SerializedName("destinations")
    @Expose
    private val destinations: Destinations? = destinations

    @SerializedName("customer")
    @Expose
    private val customer: String? = customer

    @SerializedName("currency")
    @Expose
    private val currency: String? = currency

    @SerializedName("total_amount")
    @Expose
    private var totalAmount: BigDecimal? = null

    @SerializedName("merchant_id")
    @Expose
    @Nullable private val merchant_id: String? = merchant_id

    @SerializedName("payment_type")
    @Expose
    private val payment_type: String = "ALL"

    @SerializedName("topup")
    @Expose
    private val topup: TopUp? = null

    @SerializedName("order")
    @Expose
    private val orderObject: OrderObject? = orderObject
    /**
     * Gets transaction mode.
     *
     * @return the transaction mode
     */
    fun getTransactionMode(): TransactionMode {
        return transactionMode
    }

    /**
     * Get payment option request info string.
     *
     * @return the string
     */
    fun getPaymentOptionRequestInfo(): String {
        return "trx_mode : " + transactionMode + " /n " +
                "shipping : " + shipping + " /n " +
                "taxes : " + taxes + " /n " +
                "currency : " + currency + " /n " +
                "customer : " + customer + " /n " +
                "total_amout : " + totalAmount + " /n " +
                "merchant_id : " + merchant_id + " /n " +
                "payment_type : " + payment_type + " /n "+
                "topup : " + topup + " /n "+
                "orderObject : " + orderObject + " /n "
    }

    /**
     * Instantiates a new Payment options request.
     *
     * @param transactionMode the transaction mode
     * @param amount          the amount
     * @param shipping        the shipping
     * @param taxes           the taxes
     * @param currency        the currency
     * @param topUp        the topup
     * @param orderObject        the orderObject
     */
    init {


            totalAmount = AmountCalculator.calculateTotalAmountOf( null ,taxes, shipping)
            if (orderObject != null) {
                totalAmount =
                    totalAmount?.add(AmountCalculator.calculateTotalAmountOfObject(orderObject))
            }
         else {
            if (orderObject != null) {
                totalAmount = BigDecimal.ZERO
                totalAmount =
                    totalAmount?.add(AmountCalculator.calculateTotalAmountOfObject(orderObject))
            }
        }
    }
}
