package company.tap.checkout.open.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import company.tap.checkout.internal.api.enums.AmountModificatorType
import company.tap.checkout.internal.api.models.AmountModificator
import company.tap.checkout.internal.api.models.Quantity
import company.tap.checkout.internal.utils.AmountCalculator
import java.math.BigDecimal
import java.util.*

/**
 * Created by AhlaamK on 6/13/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
class PaymentItem(
    nestedName: String,
    nestedDescription: String?,
    nestedQuantity: Quantity,
    nestedAmountPerUnit: BigDecimal,
    nestedDiscount: AmountModificator?,
    nestedTaxes: ArrayList<Tax>?
) {
    @SerializedName("name")
    @Expose
    private var name: String? = null


    @SerializedName("description")
    @Expose
    private var description: String? = null

    @SerializedName("quantity")
    @Expose
    private var quantity: Quantity? = null

    @SerializedName("amount_per_unit")
    @Expose
    private var amountPerUnit: BigDecimal? = null

    @SerializedName("discount")
    @Expose
    private var discount: AmountModificator? = null

    @SerializedName("taxes")
    @Expose
    private var taxes: ArrayList<Tax>? = null

    @SerializedName("total_amount")
    @Expose
    private var totalAmount: BigDecimal? = null

    /**
     * Constructor is private to prevent access from client app, it must be through inner Builder class only
     */
    private fun PaymentItem(
        name: String,
        description: String?,
        quantity: Quantity,
        amountPerUnit: BigDecimal,
        discount: AmountModificator?,
        taxes: ArrayList<Tax>?
    ) {
        this.name = name
        this.description = description
        this.quantity = quantity
        this.amountPerUnit = amountPerUnit
        this.discount = discount
        this.taxes = taxes
        //totalAmount = AmountCalculator.calculateTotalAmountOf(this,this,this)
        println("calculated total amount : $totalAmount")
    }

    /**
     * Gets amount per unit.
     *
     * @return the amount per unit
     */
    private fun getAmountPerUnit(): BigDecimal? {
        return amountPerUnit
    }

    /**
     * Gets quantity.
     *
     * @return the quantity
     */
    private fun getQuantity(): Quantity? {
        return quantity
    }

    /**
     * Gets discount.
     *
     * @return the discount
     */
    private fun getDiscount(): AmountModificator? {
        return discount
    }

    /**
     * Gets plain amount.
     *
     * @return the plain amount
     */
    fun getPlainAmount(): BigDecimal {
        println("  #### getPlainAmount : " + getAmountPerUnit())
        println("  #### this.getQuantity().getValue() : " + getQuantity()?.value)
        println("  #### result : " + getAmountPerUnit()!!.multiply(getQuantity()?.value))
        return getAmountPerUnit()!!.multiply(getQuantity()?.value)
    }

    /**
     * Gets discount amount.
     *
     * @return the discount amount
     */
    fun getDiscountAmount(): BigDecimal? {
        return if (getDiscount() == null) {
            BigDecimal.ZERO
        } else when (getDiscount()?.getType()) {
            AmountModificatorType.PERCENTAGE -> getPlainAmount().multiply(getDiscount()?.value)
            AmountModificatorType.FIXED -> getDiscount()?.value
            else -> BigDecimal.ZERO
        }
    }

    /**
     * Gets taxes amount.
     *
     * @return the taxes amount
     */
    fun getTaxesAmount(): BigDecimal? {
        val taxationAmount = getPlainAmount().subtract(getDiscountAmount())
       // return AmountCalculator.calculateTaxesOn(taxationAmount, taxes)
        return null
    }

    /**
     * The type Payment item builder.
     */
    ////////////////////////// ############################ Start of Builder Region ########################### ///////////////////////
    class PaymentItemBuilder(
        private val nestedName: String,
        quantity: Quantity,
        amountPerUnit: BigDecimal
    ) {
        private var nestedDescription: String? = null
        private val nestedQuantity: Quantity
        private val nestedAmountPerUnit: BigDecimal
        private var nestedDiscount: AmountModificator? = null
        private var nestedTaxes: ArrayList<Tax>? = null
        private var nestedTotalAmount: BigDecimal? = null

        /**
         * Description payment item builder.
         *
         * @param innerDescription the inner description
         * @return the payment item builder
         */
        fun description(innerDescription: String?): PaymentItemBuilder {
            nestedDescription = innerDescription
            return this
        }

        /**
         * Discount payment item builder.
         *
         * @param innerDiscount the inner discount
         * @return the payment item builder
         */
        fun discount(innerDiscount: AmountModificator?): PaymentItemBuilder {
            nestedDiscount = innerDiscount
            return this
        }

        /**
         * Taxes payment item builder.
         *
         * @param innerTaxes the inner taxes
         * @return the payment item builder
         */
        fun taxes(innerTaxes: ArrayList<Tax>?): PaymentItemBuilder {
            nestedTaxes = innerTaxes
            return this
        }

        /**
         * Total amount payment item builder.
         *
         * @param innerTotalAmount the inner total amount
         * @return the payment item builder
         */
        fun totalAmount(innerTotalAmount: BigDecimal?): PaymentItemBuilder {
            nestedTotalAmount = innerTotalAmount
            return this
        }

        /**
         * Build payment item.
         *
         * @return the payment item
         */
        fun build(): PaymentItem {
            return PaymentItem(
                nestedName, nestedDescription, nestedQuantity, nestedAmountPerUnit,
                nestedDiscount, nestedTaxes
            )
        }

        /**
         * public constructor with only required data
         *
         * @param name          the name
         * @param quantity      the quantity
         * @param amountPerUnit the amount per unit
         */
        init {
            nestedQuantity = quantity
            nestedAmountPerUnit = amountPerUnit
        }
    }
    ////////////////////////// ############################ End of Builder Region ########################### ///////////////////////


}

