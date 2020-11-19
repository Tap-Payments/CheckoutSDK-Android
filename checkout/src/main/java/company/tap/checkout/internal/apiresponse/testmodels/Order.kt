package company.tap.cardbusinesskit.testmodels

import com.google.gson.annotations.SerializedName


data class Order (

    @SerializedName("trx_currency") val trx_currency : String,
    @SerializedName("original_amount") val original_amount : Int,
    @SerializedName("items") val items : List<Items>,
    @SerializedName("shipping") val shipping : Shipping?=null,
    @SerializedName("tax") val tax : List<Tax>?=null
)