package company.tap.checkout.internal.api.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import company.tap.checkout.internal.utils.CurrencyFormatter
import java.io.Serializable
import java.math.BigDecimal

/**
 * Created by AhlaamK on 6/13/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
data class AmountedCurrency(
    @SerializedName("currency") @Expose
    private var currency: String,

    @SerializedName("symbol")
    @Expose
    private val symbol: String,

    @SerializedName("amount")
    @Expose
    private var amount: BigDecimal
) : Serializable, Comparable<AmountedCurrency> {

    override fun compareTo(other: AmountedCurrency): Int {
        val thisDisplayName: String = CurrencyFormatter.getLocalizedCurrencyName(currency)
        val oDisplayName: String = CurrencyFormatter.getLocalizedCurrencyName(other.currency)

        return thisDisplayName.compareTo(oDisplayName, ignoreCase = true)
    }

}



