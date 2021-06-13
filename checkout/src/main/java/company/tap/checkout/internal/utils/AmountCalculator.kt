package company.tap.checkout.internal.utils


import company.tap.checkout.internal.api.models.AmountedCurrency
import company.tap.checkout.open.models.PaymentItem
import company.tap.checkout.open.models.Shipping
import company.tap.checkout.open.models.Tax
import java.math.BigDecimal
import java.math.MathContext
import java.util.*

/**
 * The type Amount calculator.
 */
object AmountCalculator {
    /**
     * Calculate total amount of big decimal.
     *
     * @param item the item
     * @return the big decimal
     */
    fun calculateTotalAmountOf(item: PaymentItem): BigDecimal {
        return item.getPlainAmount().subtract(item.getDiscountAmount()).add(item.getTaxesAmount())
    }

    /**
     * Calculate total amount of big decimal.
     *
     * @param items     the items
     * @param taxes     the taxes
     * @param shippings the shippings
     * @return the big decimal
     */
    fun calculateTotalAmountOf(
        items: ArrayList<PaymentItem>,
        taxes: ArrayList<Tax>?,
        shippings: ArrayList<Shipping>?
    ): BigDecimal {
        var itemsPlainAmount = BigDecimal.ZERO
        var itemsDiscountAmount = BigDecimal.ZERO
        var itemsTaxesAmount = BigDecimal.ZERO
        for (item in items) {
            itemsPlainAmount = itemsPlainAmount.add(item.getPlainAmount())
            itemsDiscountAmount = itemsDiscountAmount.add(item.getDiscountAmount())
            itemsTaxesAmount = itemsTaxesAmount.add(item.getTaxesAmount())
        }
        val discountedAmount = itemsPlainAmount.subtract(itemsDiscountAmount)
        var shippingAmount = BigDecimal.ZERO
        if (shippings != null) {
            for (shipping in shippings) {
                shippingAmount = shippingAmount.add(shipping.amount)
            }
        }
        val taxesAmount = calculateTaxesOn(discountedAmount.add(shippingAmount), taxes)
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
    fun calculateTaxesOn(amount: BigDecimal, taxes: ArrayList<Tax>?): BigDecimal {
        var result = BigDecimal.ZERO
        if (taxes == null) {
            return result
        }
        for (tax in taxes) {
            when (tax.getAmount().getType()) {
                PERCENTAGE -> {
                    result = result.add(amount.multiply(tax.getAmount().getNormalizedValue()))
                    result = result.add(tax.getAmount().getValue())
                }
                FIXED -> result = result.add(tax.getAmount().getValue())
            }
        }
        return result
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
        fees: ArrayList<ExtraFee>?,
        supportedCurrencies: ArrayList<AmountedCurrency>,
        currency: AmountedCurrency
    ): BigDecimal {
        var result = BigDecimal.ZERO
        if (fees != null) {
            for (fee in fees) {
                var increase = BigDecimal.ZERO
                when (fee.getType()) {
                    FIXED -> {
                        val amountedCurrency: AmountedCurrency? =
                            getAmountedCurrency(supportedCurrencies, fee.getCurrency())
                        /***
                         * Based on JS Lib added check for Settlement currency equals the amounted currency.
                         */
                        if (PaymentDataManager.getInstance().getPaymentOptionsDataManager()
                                .getPaymentOptionsResponse().getSettlement_currency() != null
                        ) increase =
                            if (PaymentDataManager.getInstance().getPaymentOptionsDataManager()
                                    .getPaymentOptionsResponse().getSettlement_currency()
                                    .equals(currency.getCurrency())
                            ) {
                                return fee.getValue()
                            } else {
                                /***
                                 * Based on JS Lib Condition for extra fees calculation changed.
                                 * var rate =  settlement_currency.amount / current_currency.amount
                                 * extra_fee = fee.value * rate;
                                 *
                                 */
                                //                      increase = currency.getAmount().multiply(fee.getValue()).divide(amountedCurrency.getAmount());
                                // increase = currency.getAmount().multiply(fee.getValue()).divide(amountedCurrency.getAmount(), MathContext.DECIMAL64); /// handling issue of  quotient has a non terminating decimal expansion
                                val rate: BigDecimal = amountedCurrency.getAmount()
                                    .divide(currency.getAmount(), MathContext.DECIMAL64)
                                fee.getValue().multiply(rate)
                            }
                    }
                    PERCENTAGE -> {

                        // increase = currency.getAmount().multiply(fee.getNormalizedValue());
                        /***
                         * Increase i.e change in currency changed as per JS Lib FORMULA: extra_fee =  {amount} / (1 - fee.value / 100) - {amount};
                         */
                        increase = currency.getAmount().divide(
                            BigDecimal.valueOf(1).subtract(
                                fee.getValue().divide(
                                    BigDecimal.valueOf(100)
                                )
                            ), MathContext.DECIMAL64
                        )
                            .subtract(currency.getAmount())
                        // System.out.println("increase = " + increase );
                        /**
                         * Applying Min and Max values based on the calculated extra fees.
                         */
                        if (fee.getMinimum_fee() != null && fee.getMinimum_fee() !== 0 || fee.getMaximum_fee() != null && fee.getMaximum_fee() !== 0) {
                            if (java.lang.Double.valueOf(increase.toString()) > fee.getMinimum_fee() && java.lang.Double.valueOf(
                                    increase.toString()
                                ) < fee.getMaximum_fee()
                            ) {
                                increase = increase
                            } else if (java.lang.Double.valueOf(increase.toString()) < fee.getMinimum_fee()) {
                                increase = BigDecimal.valueOf(fee.getMinimum_fee())
                            } else if (java.lang.Double.valueOf(increase.toString()) > fee.getMaximum_fee()) {
                                increase = BigDecimal.valueOf(fee.getMaximum_fee())
                            }
                        }
                    }
                }
                result = result.add(increase)
            }
        }
        return result
    }

    private fun getAmountedCurrency(
        amountedCurrencies: ArrayList<AmountedCurrency>,
        currency: String
    ): AmountedCurrency? {
        for (amountedCurrency in amountedCurrencies) {
            if (amountedCurrency.getCurrency() == currency) {
                return amountedCurrency
            }
        }
        return null
    }
}