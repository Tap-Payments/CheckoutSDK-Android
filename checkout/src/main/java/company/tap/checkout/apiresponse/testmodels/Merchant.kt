package company.tap.cardbusinesskit.testmodels

import com.google.gson.annotations.SerializedName


data class Merchant (

    @SerializedName("id") val id : String,
    @SerializedName("name") val name : String,
    @SerializedName("country_code") val country_code : Int,
    @SerializedName("currency") val currency : String,
    @SerializedName("contact_info") val contact_info : Contact_info,
    @SerializedName("logo") val logo : String,
    @SerializedName("description") val description : String,
    @SerializedName("background_url") val background_url : String
)