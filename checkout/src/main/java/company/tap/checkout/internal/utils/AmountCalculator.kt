package company.tap.checkout.internal.utils

import company.tap.checkout.internal.api.enums.AmountModificatorType
import company.tap.checkout.internal.api.models.AmountedCurrency
import company.tap.checkout.internal.api.models.ExtraFee
import company.tap.checkout.internal.api.models.SupportedCurrencies
import company.tap.checkout.open.data_managers.PaymentDataSource
import company.tap.checkout.open.models.PaymentItem
import company.tap.checkout.open.models.Shipping
import company.tap.checkout.open.models.Tax
import java.math.BigDecimal
import java.math.MathContext


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
                    result = result.add(tax.amount.getValue())
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
    open fun calculateTotalAmountOf(items: List<PaymentItem>, taxes: java.util.ArrayList<Tax>?, shippings: java.util.ArrayList<Shipping>?): BigDecimal? {
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
     * Calculate extra fees amount big decimal.
     *
     * @param fees                the fees
     * @param supportedCurrencies the supported currencies
     * @param currency            the currency
     * @return the big decimal
     */
    fun calculateExtraFeesAmount(fees: java.util.ArrayList<ExtraFee>?, supportedCurrencies: java.util.ArrayList<SupportedCurrencies>?, currency: AmountedCurrency?): BigDecimal? {
        var result:BigDecimal = BigDecimal.ZERO
        if (fees != null) {
            for (fee in fees) {
                var increase:BigDecimal? = BigDecimal.ZERO
                when (fee.getType()) {
                    AmountModificatorType.FIXED -> {
                        val amountedCurrency: SupportedCurrencies? = fee?.currency?.let { AmountCalculator.getAmountedCurrency(supportedCurrencies, it) }
                        /***
                         * Based on JS Lib added check for Settlement currency equals the amounted currency.
                         */
                        if (PaymentDataSource.getPaymentOptionsResponse()?.settlement_currency != null) increase = if (PaymentDataSource.getPaymentOptionsResponse()?.settlement_currency.equals(currency?.currency)) {
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
                            val rate: BigDecimal? = amountedCurrency?.amount?.divide(currency?.amount, MathContext.DECIMAL64)
                            fee.getValue()?.multiply(rate)
                        }
                    }
                    AmountModificatorType.PERCENTAGE -> {

                        // increase = currency.getAmount().multiply(fee.getNormalizedValue());
                        /***
                         * Increase i.e change in currency changed as per JS Lib FORMULA: extra_fee =  {amount} / (1 - fee.value / 100) - {amount};
                         */
                        increase = currency?.amount?.divide(BigDecimal.valueOf(1).subtract(fee.getValue()?.divide(BigDecimal.valueOf(100))), MathContext.DECIMAL64)?.subtract(currency.amount)
                        // System.out.println("increase = " + increase );
                        /**
                         * Applying Min and Max values based on the calculated extra fees.
                         */
                        if (fee.minimum_fee != null && fee.minimum_fee !== 0.0 || fee.maximum_fee != null && fee.maximum_fee !== 0.0) {
                            if (java.lang.Double.valueOf(increase.toString()) > fee?.minimum_fee!! && java.lang.Double.valueOf(increase.toString()) < fee.maximum_fee!!) {
                                increase = increase
                            } else if (java.lang.Double.valueOf(increase.toString()) < fee.maximum_fee!!) {
                                increase = fee.minimum_fee?.let { BigDecimal.valueOf(it) }
                            } else if (java.lang.Double.valueOf(increase.toString()) > fee.maximum_fee!!) {
                                increase = fee.maximum_fee?.let { BigDecimal.valueOf(it) }
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

}