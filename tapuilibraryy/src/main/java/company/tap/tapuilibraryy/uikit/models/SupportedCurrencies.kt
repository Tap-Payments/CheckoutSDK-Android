import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

/**
 * Created by AhlaamK on 6/20/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
data class SupportedCurrencies(
    @SerializedName("symbol")
    @Expose
    var symbol: String? = null,
    @SerializedName("name")
    @Expose
    var name: String? = null,
    @SerializedName("flag")
    @Expose
    var flag: String? = null,
    @SerializedName("decimal_digit")
    @Expose
    var decimal_digit: Int? = null,
    @SerializedName("selected")
    @Expose
    var selected: Boolean? = false,
    @SerializedName("currency")
    @Expose
    var currency: String? = null,
    @SerializedName("rate")
    @Expose
    var rate: Double? = null,
    @SerializedName("amount")
    @Expose
    var amount: BigDecimal,
    @SerializedName("logos")
    @Expose
    var logos: LogosFlag? = null,

    @SerializedName("order_by")
    @Expose
    var orderBy: Int? = 0,
    var isSelectedCurrency: Boolean? = false
)
