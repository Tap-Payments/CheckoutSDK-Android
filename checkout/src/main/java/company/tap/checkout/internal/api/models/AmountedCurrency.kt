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
abstract class AmountedCurrency @JvmOverloads constructor(
    currency: String,
    amount: BigDecimal,
    symbol: String = CurrencyFormatter.getLocalizedCurrencySymbol(
        currency
    )
) :
    Serializable, Comparable<AmountedCurrency?> {
    @SerializedName("currency")
    @Expose
    private val currency: String = currency

    @SerializedName("symbol")
    @Expose
    private var symbol: String

    @SerializedName("amount")
    @Expose
    private val amount: BigDecimal = amount

    /**
     * Gets currency.
     *
     * @return the currency
     */
    fun getCurrency(): String {
        return currency
    }

    /**
     * Gets symbol.
     *
     * @return the symbol
     */
    fun getSymbol(): String {
        return symbol
    }

    /**
     * Sets symbol.
     *
     * @param symbol the symbol
     */
    fun setSymbol(symbol: String) {
        this.symbol = symbol
    }

    /**
     * Gets amount.
     *
     * @return the amount
     */
    fun getAmount(): BigDecimal {
        return amount
    }

    override operator fun compareTo(other: AmountedCurrency?): Int {
        val thisDisplayName: String = CurrencyFormatter.getLocalizedCurrencyName(getCurrency())
        val oDisplayName: String = CurrencyFormatter.getLocalizedCurrencyName(other.getCurrency())
        return thisDisplayName.compareTo(oDisplayName, ignoreCase = true)
    }
    /**
     * Instantiates a new Amounted currency.
     *
     * @param currency the currency
     * @param amount   the amount
     * @param symbol   the symbol
     */
    /**
     * Instantiates a new Amounted currency.
     *
     * @param currency the currency
     * @param amount   the amount
     */
    init {
        this.symbol = symbol
    }
}
