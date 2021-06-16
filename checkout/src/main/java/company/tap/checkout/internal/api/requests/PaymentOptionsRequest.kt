package company.tap.checkout.internal.api.requests

import androidx.annotation.RestrictTo
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import company.tap.checkout.internal.api.enums.PaymentType
import company.tap.checkout.internal.utils.AmountCalculator
import company.tap.checkout.open.enums.TransactionMode
import company.tap.checkout.open.models.PaymentItem
import company.tap.checkout.open.models.Shipping
import company.tap.checkout.open.models.TapCustomer
import company.tap.checkout.open.models.Tax
import company.tap.tapnetworkkit.interfaces.TapRequestBodyBase
import java.math.BigDecimal
import java.util.*

/**
 * Created by AhlaamK on 6/15/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
@RestrictTo(RestrictTo.Scope.LIBRARY)
class PaymentOptionsRequest(
    transactionMode: TransactionMode?,
    amount: Int,
    items: ArrayList<PaymentItem>?,
    shipping: ArrayList<Shipping>?,
    taxes: ArrayList<Tax?>?,
    currency: String?,
    customer: TapCustomer?,
    merchant_id: String?,
    payment_type: PaymentType
) : TapRequestBodyBase{
    @SerializedName("transaction_mode")
    @Expose
    private val transactionMode: TransactionMode? = null

    @SerializedName("items")
    @Expose
    private var items: ArrayList<PaymentItem>? = null

    @SerializedName("shipping")
    @Expose
    private val shipping: ArrayList<Shipping>? = null

    @SerializedName("taxes")
    @Expose
    private val taxes: ArrayList<Tax>? = null

    @SerializedName("customer")
    @Expose
    private val customer: String? = null

    @SerializedName("currency")
    @Expose
    private val currency: String? = null

    @SerializedName("total_amount")
    @Expose
    private var totalAmount: BigDecimal? = null

    @SerializedName("merchant_id")
    @Expose
    private val merchant_id: String? = null

    @SerializedName("payment_type")
    @Expose
    private val payment_type: String? = null
    /**
     * Gets transaction mode.
     *
     * @return the transaction mode
     */
    fun getTransactionMode(): TransactionMode? {
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
                "payment_type : " + payment_type + " /n "
    }

    /**
     * Instantiates a new Payment options request.
     *
     * @param transactionMode the transaction mode
     * @param amount          the amount
     * @param items           the items
     * @param shipping        the shipping
     * @param taxes           the taxes
     * @param currency        the currency
     * @param customer        the customer
     */
    init {
        if (items != null && items.size > 0) {
            this.items = items
            totalAmount = AmountCalculator.calculateTotalAmountOf(items, taxes, shipping)
        } else {
            this.items = null
            val plainAmount = amount ?: BigDecimal.ZERO
           // totalAmount =
            //    AmountCalculator.calculateTotalAmountOf(PaymentItem, taxes, shipping)
             //       ?.add(plainAmount)
        }
    }
}