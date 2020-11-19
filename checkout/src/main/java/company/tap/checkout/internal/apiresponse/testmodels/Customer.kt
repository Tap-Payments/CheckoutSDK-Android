package company.tap.cardbusinesskit.testmodels

import com.google.gson.annotations.SerializedName


data class Customer (

    @SerializedName("first_name") val first_name : String,
    @SerializedName("middle_name") val middle_name : String,
    @SerializedName("last_name") val last_name : String,
    @SerializedName("email") val email : String,
    @SerializedName("phone") val phone : Phone,
    @SerializedName("description") val description : String,
    @SerializedName("metadata") val metadata : Metadata?=null,
    @SerializedName("currency") val currency : String
)