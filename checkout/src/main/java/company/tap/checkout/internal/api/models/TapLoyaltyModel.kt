package company.tap.checkout.internal.api.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class TapLoyaltyModel(
    @SerializedName("id") @Expose
    var id: String? = null,


    @SerializedName("bankName") @Expose
    var bankName: String? = null,

 @SerializedName("bankLogo") @Expose
    var bankLogo: String? = null,

    @SerializedName("loyaltyProgramName") @Expose
    var loyaltyProgramName: String? = null,

    @SerializedName("loyaltyPointsName") @Expose
    var loyaltyPointsName: String? = null,

    @SerializedName("transactionsCount") @Expose
    var transactionsCount: String? = null,


    @SerializedName("termsConditionsLink") @Expose
    var termsConditionsLink: String? = null,

    @SerializedName("supportedCurrencies") @Expose
    var supportedCurrencies: ArrayList<LoyaltySupportedCurrency>? = null,
 )

data class LoyaltySupportedCurrency(
    @SerializedName("rate") @Expose
    var rate: Int? = null,
    @SerializedName("currency") @Expose
    var currency: String? = null,
    @SerializedName("balance") @Expose
    var balance: Int? = null,
)