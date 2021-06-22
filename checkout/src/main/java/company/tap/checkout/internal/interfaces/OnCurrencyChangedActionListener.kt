package company.tap.checkout.internal.interfaces

import java.math.BigDecimal


/**
 * Created by AhlaamK on 12/9/20.

Copyright (c) 2020    Tap Payments.
All rights reserved.
 **/
interface OnCurrencyChangedActionListener {
    fun onCurrencyClicked(currencySelected: String, currencyRate: Double)
}