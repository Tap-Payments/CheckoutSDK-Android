package company.tap.checkout.internal.interfaces

import java.util.*

/**
 * Created by AhlaamK on 6/14/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
interface CurrenciesSupport {
    /**
     * Gets supported currencies.
     *
     * @return the supported currencies
     */
    fun getSupportedCurrencies(): ArrayList<String>?
}