package company.tap.checkout.internal.api.responses

import com.google.gson.annotations.SerializedName

data class UserLocalCurrencyModel(

    @SerializedName("code") var code: String? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("symbol") var symbol: String? = null,
    @SerializedName("country_code") var country_code: String? = null,
    @SerializedName("decimal_digits") var decimal_digits: Double? = null,

    )