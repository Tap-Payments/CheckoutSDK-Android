package company.tap.checkout.open.models

import company.tap.checkout.internal.utils.LocaleCurrencies
import company.tap.checkout.open.exceptions.CurrencyException

/**
 * Created by AhlaamK on 6/13/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
/**
 * The type Tap currency.
 */
class TapCurrency(isoCode: String) {
    /**
     * Gets iso code.
     *
     * @return the iso code
     */
    val isoCode: String

    /**
     * Instantiates a new Tap currency.
     *
     * @param isoCode the iso code
     * @throws CurrencyException the currency exception
     */
    init {
        if (isoCode.isEmpty()) {
            this.isoCode = isoCode
        } else {
            val code = isoCode.toLowerCase()
            if (!LocaleCurrencies.checkUserCurrency(code)) {
                throw CurrencyException.getUnknown(code)
            }
            this.isoCode = code
        }
    }
}
