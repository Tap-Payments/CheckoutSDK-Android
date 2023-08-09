package company.tap.checkout.internal.utils

import company.tap.checkout.internal.api.enums.AmountModificatorType
import company.tap.checkout.internal.api.models.AmountedCurrency
import company.tap.checkout.internal.api.models.ExtraFee
import SupportedCurrencies
import company.tap.checkout.open.data_managers.PaymentDataSource
import company.tap.checkout.open.models.*
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode


/**
 * The type Amount calculator.
 */
object AmountCalculator {
    /**
     * Calculate taxes on big decimal.
     *
     * @param amount the amount
     * @param taxes  the taxes
     * @return the big decimal
     */
    fun calculateTaxesOn(amount: BigDecimal, taxes: java.util.ArrayList<Tax>?): BigDecimal? {
        var result = BigDecimal.ZERO
        if (taxes == null) {
            return result
        }
        for (tax in taxes) {
            when (tax?.amount?.getType()) {
                AmountModificatorType.PERCENTAGE -> {
                    result = result.add(amount.multiply(tax.amount.getNormalizedValue()))

                }
                AmountModificatorType.FIXED -> result = result.add(tax.amount.getValue())
            }
        }
        return result
    }

    /**
     * Calculate total amount of big decimal.
     *
     * @param items     the items
     * @param taxes     the taxes
     * @param shippings the shippings
     * @return the big decimal
     */
    open fun calculateTotalAmountOf(
        items: List<PaymentItem>?,
        taxes: java.util.ArrayList<Tax>?,
        shippings: Shipping?
    ): BigDecimal? {
        var itemsPlainAmount = BigDecimal.ZERO
        var itemsDiscountAmount = BigDecimal.ZERO
        var itemsTaxesAmount = BigDecimal.ZERO
        if (items != null) {
            for (item in items) {
                itemsPlainAmount = itemsPlainAmount.add(item.getPlainAmount())
                itemsDiscountAmount = itemsDiscountAmount.add(item.getDiscountAmount())
                itemsTaxesAmount = itemsTaxesAmount.add(item.getTaxesAmount())
            }
        }
        val discountedAmount = itemsPlainAmount.subtract(itemsDiscountAmount)
        var shippingAmount = BigDecimal.ZERO
        if (shippings != null) {
         //   for (shipping in shippings) {
             //   shippingAmount = shippingAmount.add(shipping.amount)
           // }
        }
        val taxesAmount = calculateTaxesOn(discountedAmount.add(shippingAmount), taxes)
        val totalTaxesAmount = itemsTaxesAmount.add(taxesAmount)
        return discountedAmount.add(shippingAmount).add(totalTaxesAmount)
    }

    /**
     * Calculate extra fees amount big decimal.
     *
     * @param fees                the fees
     * @param supportedCurrencies the supported currencies
     * @param currency            the currency
     * @return the big decimal
     */
    fun calculateExtraFeesAmount(
        fees: java.util.ArrayList<ExtraFee>?,
        supportedCurrencies: java.util.ArrayList<SupportedCurrencies>?,
        currency: AmountedCurrency?
    ): BigDecimal? {
        var result:BigDecimal = BigDecimal.ZERO

        if (fees != null) {
            for (fee in fees) {
                var increase: BigDecimal? = BigDecimal.ZERO

                when (fee.getType()) {
                    AmountModificatorType.FIXED -> {
                        val amountedCurrency: SupportedCurrencies? = fee?.currency?.let {
                            AmountCalculator.getAmountedCurrency(
                                supportedCurrencies,
                                it
                            )
                        }
                        /***
                         * Based on JS Lib added check for Settlement currency equals the amounted currency.
                         */
                        if (PaymentDataSource.getPaymentOptionsResponse()?.settlement_currency != null) increase =
                            if (PaymentDataSource.getPaymentOptionsResponse()?.settlement_currency.equals(
                                    currency?.currency
                                )
                            ) {
                                return fee.getValue()
                            } else {
                                /***
                                 * Based on JS Lib Condition for extra fees calculation changed.
                                 * var rate =  settlement_currency.amount / current_currency.amount
                                 * extra_fee = fee.value * rate;
                                 *
                                 */

                                val rate: BigDecimal? = amountedCurrency?.amount?.divide(
                                    currency?.amount,
                                    MathContext.DECIMAL64
                                )
                             return  fee.getValue()?.multiply(rate)
                            }
                        break
                    }
                    AmountModificatorType.PERCENTAGE -> {
                        /***
                         * Increase i.e change in currency changed as per JS Lib FORMULA: extra_fee =  {amount} / (1 - fee.value / 100) - {amount};
                         */
                        increase = (currency?.amount?.divide(
                            BigDecimal.valueOf(1).subtract(
                                fee.getValue()?.divide(
                                    BigDecimal.valueOf(
                                        100
                                    )
                                )
                            ),
                            MathContext.DECIMAL64
                        ))?.subtract(currency.amount)
                        //println("increase = " + increase?.toDouble())
                        /**
                         * Applying Min and Max values based on the calculated extra fees.
                         */
                        //  if (fee.minimum_fee != null && fee.minimum_fee != 0.00 || fee.maximum_fee != null && fee.maximum_fee != 0.00) {
                        if (increase?.toDouble()!! > fee.minimum_fee && increase?.toDouble() < fee.maximum_fee) {
                            increase = increase
                        } else if (increase?.toDouble() < fee.minimum_fee && fee.minimum_fee != 0.0) {
                            increase = BigDecimal.valueOf(fee.minimum_fee)
                        } else if (increase?.toDouble() > fee.maximum_fee && fee.maximum_fee != 0.0) {
                            increase = BigDecimal.valueOf(fee.maximum_fee)
                        } else if (increase.toDouble() > 0) {
                            increase = increase


                        }


                    }

                }


               result = result.add(increase)


            }
        }
        return result?.setScale(3, RoundingMode.HALF_UP)
    }

    private fun getAmountedCurrency(
            amountedCurrencies: ArrayList<SupportedCurrencies>?,
            currency: String
        ): SupportedCurrencies? {
        if (amountedCurrencies != null) {
            for (amountedCurrency in amountedCurrencies) {
                if (amountedCurrency?.currency.equals(currency)) {
                    return amountedCurrency
                }
            }
        }
        return null
    }

    /**
     * Calculate total amount of big decimal.
     *
     * @param orderObject the orderObject
     * @return the big decimal
     */
    fun calculateTotalAmountOfObject(orderObject: OrderObject): BigDecimal? {
        return orderObject.getAmount()
    }


    /**
     * Calculate total amount of big decimal.
     *
     * @param orderObjects     the orderObjects
     * @param taxes     the taxes
     * @param shippings the shippings
     * @return the big decimal
     */
    fun calculateTotalAmountOfOrder(
        items: java.util.ArrayList<ItemsModel>?,
        taxes: java.util.ArrayList<TaxObject>?,
        shippings: ShippingObject?,
        orderObjects: OrderObject
    ): BigDecimal? {
        var itemsPlainAmount = BigDecimal.ZERO
        val itemsDiscountAmount = BigDecimal.ZERO
        var itemsTaxesAmount = BigDecimal.ZERO
        if (items != null) {
            for (item in items) {
                itemsPlainAmount = itemsPlainAmount.add(item.getPlainAmount())
            }
        }

        //  itemsPlainAmount    = itemsPlainAmount.add(orderObjects.getAmount());
        itemsTaxesAmount = itemsTaxesAmount.add(orderObjects.getTaxesAmount())
        val discountedAmount = itemsPlainAmount.subtract(itemsDiscountAmount)
        var shippingAmount = BigDecimal.ZERO
        if (shippings != null) {
            //for (shipping in shippings) {
               // shippingAmount = shippingAmount.add(shipping.getAmount())
          //  }
            shippingAmount = shippingAmount.add(shippings.getAmount())

        }
        val taxesAmount =
            calculateTaxesOnOrder(discountedAmount.add(shippingAmount), taxes)
        val totalTaxesAmount = itemsTaxesAmount.add(taxesAmount)
        return discountedAmount.add(shippingAmount).add(totalTaxesAmount)
    }

    /**
     * Calculate taxes on big decimal.
     *
     * @param amount the amount
     * @param taxes  the taxes
     * @return the big decimal
     */
    fun calculateTaxesOnOrder(
        amount: BigDecimal,
        taxes: java.util.ArrayList<TaxObject>?
    ): BigDecimal {
        var result = BigDecimal.ZERO
        if (taxes == null) {
            return result
        }
        for (tax in taxes) {
            when (tax?.getAmount()?.getType()) {
                AmountModificatorType.PERCENTAGE -> {
                    result = result.add(amount.multiply(tax.getAmount().getNormalizedValue()))
                    result = result.add(tax.getAmount().getValue())
                }
                AmountModificatorType.FIXED -> result = result.add(tax.getAmount().getValue())
            }
        }
        return result
    }

    /**
     * Calculate taxes on big decimal.
     *
     * @param amount the amount
     * @param taxes  the taxes
     * @return the big decimal
     */
    fun calculateTaxesOnItems(
        amount: BigDecimal,
        taxes: java.util.ArrayList<TaxObject>?
    ): BigDecimal? {
        var result = BigDecimal.ZERO
        if (taxes == null) {
            return result
        }
        for (tax in taxes) {
            when (tax.amount.getType()) {
                AmountModificatorType.PERCENTAGE -> {
                    result = result.add(amount.multiply(tax.amount.getNormalizedValue()))
                    result = result.add(tax.amount.getValue())
                }
                AmountModificatorType.FIXED -> result = result.add(tax.amount.getValue())
            }
        }
        return result
    }


}