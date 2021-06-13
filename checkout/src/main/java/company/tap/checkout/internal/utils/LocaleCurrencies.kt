package company.tap.checkout.internal.utils

import androidx.annotation.RestrictTo
import java.util.*

/**
 * Created by AhlaamK on 6/13/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/

/**
 * The type Locale currencies.
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
class internal
object LocaleCurrencies {
    /**
     * Gets iso codes array.
     *
     * @return the iso codes array
     */
    var isoCodesArray: Set<String>? = null
        get() {
            if (field == null) field = localizedArray
            return field
        }
        private set

    /**
     * Check user currency boolean.
     *
     * @param userProvidedCurrency the user provided currency
     * @return the boolean
     */
    fun checkUserCurrency(userProvidedCurrency: String): Boolean {
        return isIsoCodeContainsUserCurrency(userProvidedCurrency.toLowerCase())
    }

    private fun isIsoCodeContainsUserCurrency(userProvidedCurrency: String): Boolean {
        return isoCodesArray!!.contains(userProvidedCurrency)
    }

    // Locale not found
    private val localizedArray: Set<String>
        get() {
            val currenciesList: MutableSet<String> = HashSet()
            val locs = Locale.getAvailableLocales()
            for (loc in locs) {
                try {
                    val currency = Currency.getInstance(loc)
                    if (currency != null) {
                        currenciesList.add(currency.currencyCode.toLowerCase())
                    }
                } catch (exc: Exception) {
                    // Locale not found
                }
            }
            return currenciesList
        }
}
