package company.tap.cardbusinesskit.testmodels

import com.google.gson.annotations.SerializedName


data class Payment_methods (

    @SerializedName("id") val id : Int,
    @SerializedName("name") val name : String,
    @SerializedName("image") val image : String,
    @SerializedName("payment_type") val payment_type : String,
    @SerializedName("supported_card_brands") val supported_card_brands : List<String>,
    @SerializedName("extra_fees") val extra_fees : Int,
    @SerializedName("supported_currencies") val supported_currencies : ArrayList<String>,
    @SerializedName("order_by") val order_by : Int,
    @SerializedName("asynchronous") val asynchronous : Boolean,
    @SerializedName("threeDS") val threeDS : String,
    @SerializedName("supportedNetworks") val supportedNetworks : SupportedNetworks,
    @SerializedName("company.tap.cardbusinesskit.testmodels.MerchantCapabilities") val merchantCapabilities : MerchantCapabilities
)