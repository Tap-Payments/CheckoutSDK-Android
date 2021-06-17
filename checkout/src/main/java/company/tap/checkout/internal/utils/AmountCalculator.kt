package company.tap.checkout.internal.utils

import company.tap.checkout.internal.api.enums.AmountModificatorType
import company.tap.checkout.open.models.PaymentItem
import company.tap.checkout.open.models.Shipping
import company.tap.checkout.open.models.Tax
import java.math.BigDecimal
import java.util.*


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
    fun calculateTaxesOn(amount: BigDecimal, taxes: ArrayList<Tax?>?): BigDecimal? {
        var result = BigDecimal.ZERO
        if (taxes == null) {
            return result
        }
        for (tax in taxes) {
            when (tax?.amount?.getType()) {
                AmountModificatorType.PERCENTAGE -> {
                    result = result.add(amount.multiply(tax.amount.normalizedValue))
                    result = result.add(tax.amount.value)
                }
                AmountModificatorType.FIXED -> result = result.add(tax.amount.value)
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
    fun calculateTotalAmountOf(
        items: ArrayList<PaymentItem>,
        taxes: ArrayList<Tax?>?,
        shippings: ArrayList<Shipping>?
    ): BigDecimal? {
        var itemsPlainAmount = BigDecimal.ZERO
        var itemsDiscountAmount = BigDecimal.ZERO
        var itemsTaxesAmount = BigDecimal.ZERO
     /*for (item in items) {
            itemsPlainAmount = itemsPlainAmount.add(item.getPlainAmount())
            itemsDiscountAmount = itemsDiscountAmount.add(item.getDiscountAmount())
            itemsTaxesAmount = itemsTaxesAmount.add(item.getTaxesAmount())
        }*/
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

}