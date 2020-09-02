package company.tap.cardbusinesskit.testmodels

import com.google.gson.annotations.SerializedName


data class Items (

    @SerializedName("amount") val amount : Int,
    @SerializedName("currency") val currency : String,
    @SerializedName("description") val description : String,
    @SerializedName("discount") val discount : Discount,
    @SerializedName("image") val image : String,
    @SerializedName("sku") val sku : String,
    @SerializedName("name") val name : String,
    @SerializedName("quantity") val quantity : Int
)