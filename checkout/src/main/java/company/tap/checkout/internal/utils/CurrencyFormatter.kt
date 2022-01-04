package company.tap.checkout.internal.utils

import android.os.Build
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.util.*

/**
 * Created by AhlaamK on 12/8/20.

Copyright (c) 2020    Tap Payments.
All rights reserved.
 **/
object CurrencyFormatter {
    /**
     * Format string.
     *
     * @param amountedCurrency the amounted currency
     * @return the string
     */
    fun format(amountedCurrency: String): String? {
        return CurrencyFormatter.format(amountedCurrency, true)
    }

    /**
     * Format string.
     *
     * @param amountedCurrency the amounted currency
     * @param displayCurrency  the display currency
     * @return the string
     */
    fun format(amountedCurrency: String, displayCurrency: Boolean): String? {
        return CurrencyFormatter.format(amountedCurrency, Locale.US, displayCurrency)
    }

    /**
     * Format string.
     *
     * @param amountedCurrency the amounted currency
     * @param locale           the locale
     * @param displayCurrency  the display currency
     * @return the string
     */
    fun format(
        amountedCurrency: String,
        locale: Locale?,
        displayCurrency: Boolean
    ): String? {
        val formatter = NumberFormat.getCurrencyInstance(locale)
        val currency: Currency = Currency.getInstance(amountedCurrency)
        formatter.currency = currency
        return formatter.format(amountedCurrency)

    }

    /**
     * Gets localized currency symbol.
     *
     * @param currencyCode the currency code
     * @return the localized currency symbol
     */
    fun getLocalizedCurrencySymbol(currencyCode: String): String {
        return CurrencyFormatter.getLocalizedCurrencySymbol(currencyCode, Locale.US)
    }

    /**
     * Gets localized currency symbol.
     *
     * @param currencyCode the currency code
     * @param locale       the locale
     * @return the localized currency symbol
     */
    fun getLocalizedCurrencySymbol(currencyCode: String, locale: Locale?): String {
        return if (currencyCode.isEmpty()) {
            currencyCode
        } else {
            val currency = Currency.getInstance(currencyCode)
            var symbol = currency.getSymbol(locale)
            symbol = CurrencyFormatter.optionallyHardcodedCurrencySymbol(symbol)
            symbol
        }
    }

    /**
     * Gets localized currency name.
     *
     * @param currencyCode the currency code
     * @return the localized currency name
     */
    fun getLocalizedCurrencyName(currencyCode: String): String {
        return getLocalizedCurrencyName(currencyCode, Locale.US)
    }

    /**
     * Gets localized currency name.
     *
     * @param currencyCode the currency code
     * @param locale       the locale
     * @return the localized currency name
     */
    fun getLocalizedCurrencyName(currencyCode: String, locale: Locale): String {
        val currency = Currency.getInstance(currencyCode) ?: return currencyCode
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            currency.getDisplayName(locale)
        } else {
            currencyCode
        }
    }
    fun optionallyHardcodedCurrencySymbol(currencySymbol: String): String? {
        val hardcoded = hardcodedCurrencySymbols[currencySymbol]
        return hardcoded ?: currencySymbol
    }

    private val hardcodedCurrencySymbols: HashMap<String?, String?> =
        object : HashMap<String?, String?>() {
            init {
                put("KWD", "KD")
                put("د.ك.‏", "د.ك")
            }
        }
    fun currencyFormat(amount: String): String {
        println("amount formar" + amount)
        val df: DecimalFormat = DecimalFormat("###,###,##0.000")
        //val df: DecimalFormat = DecimalFormat("#,###,##0.00")
        df.decimalFormatSymbols = DecimalFormatSymbols(Locale.US)
        return  df.format(BigDecimal(amount))
    }



}