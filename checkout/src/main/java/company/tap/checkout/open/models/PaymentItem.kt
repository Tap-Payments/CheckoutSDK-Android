package company.tap.checkout.open.models

import androidx.annotation.Nullable
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
data class PaymentItem(var name: String,
                  @Nullable var description: String,
                 var quantity: Quantity,
                 var amountPerUnit: BigDecimal,
                  @Nullable var discount: AmountModificator?,
                  @Nullable var taxes: ArrayList<Tax>?, var totalAmount: BigDecimal? = null){




    init {
        this.name = name
        this.description = description
        this.quantity = quantity
        this.amountPerUnit = amountPerUnit
        this.discount = discount
        this.taxes = taxes
        totalAmount = AmountCalculator.calculateTotalAmountOf(listOf(this),null,null)

        println("calculated total amount : " + totalAmount)
    }

    /**
     * Gets amount per unit.
     *
     * @return the amount per unit
     */
    @JvmName("getAmountPerUnit1")
    fun getAmountPerUnit(): BigDecimal? {
        return amountPerUnit
    }

    /**
     * Gets quantity.
     *
     * @return the quantity
     */
    @JvmName("getQuantity1")
    fun getQuantity(): Quantity? {
        return quantity
    }

    /**
     * Gets discount.
     *
     * @return the discount
     */
    @JvmName("getDiscount1")
    fun getDiscount(): AmountModificator? {
        return discount
    }

    /**
     * Gets plain amount.
     *
     * @return the plain amount
     */
    fun getPlainAmount(): BigDecimal? {
        println("  #### getPlainAmount : " + getAmountPerUnit())
        System.out.println("  #### this.getQuantity().getValue() : " + getQuantity()?.value)
        println("  #### result : " + getAmountPerUnit()!!.multiply(getQuantity()?.value))
        return getAmountPerUnit()?.multiply(getQuantity()?.value)
    }

    /**
     * Gets discount amount.
     *
     * @return the discount amount
     */
    fun getDiscountAmount(): BigDecimal? {
        return if (getDiscount() == null) {
            BigDecimal.ZERO
        } else when (getDiscount()!!.getType()) {
            AmountModificatorType.PERCENTAGE -> getPlainAmount()?.multiply(getDiscount()!!.getNormalizedValue())
            AmountModificatorType.FIXED -> getDiscount()?.getValue()
            else -> BigDecimal.ZERO
        }
    }

    /**
     * Gets taxes amount.
     *
     * @return the taxes amount
     */
    fun getTaxesAmount(): BigDecimal? {
        val taxationAmount = getPlainAmount()?.subtract(getDiscountAmount())
        return taxationAmount?.let { AmountCalculator.calculateTaxesOn(it, taxes) }
    }


    class PaymentItemBuilder
    /**
     * public constructor with only required data
     *
     * @param name          the name
     * @param quantity      the quantity
     * @param amountPerUnit the amount per unit
     */(private var nestedName: String,
        private var nestedQuantity: Quantity,
        private var nestedAmountPerUnit: BigDecimal) {
        private var nestedDescription: String? = null
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
            return PaymentItem(nestedName, nestedDescription!!, nestedQuantity, nestedAmountPerUnit,
                    nestedDiscount, nestedTaxes)
        }
    }


}