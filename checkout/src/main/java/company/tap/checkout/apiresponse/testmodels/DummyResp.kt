package company.tap.cardbusinesskit.testmodels

import com.google.gson.annotations.SerializedName


data class DummyResp (

    @SerializedName("action") val action : String,
    @SerializedName("customer") val customer : Customer,
    @SerializedName("order") val order : Order,
    @SerializedName("gateway") val gateway : Gateway,
    @SerializedName("receipt") val receipt : Receipt,
    @SerializedName("description") val description : String,
    @SerializedName("metadata") val metadata : Metadata,
    @SerializedName("reference") val reference : Reference,
    @SerializedName("statement_descriptor") val statement_descriptor : String,
    @SerializedName("destinations") val destinations : Destinations,
    @SerializedName("post") val post : Post,
    @SerializedName("redirect") val redirect : Redirect,
    @SerializedName("merchant") val merchant : Merchant,
    @SerializedName("payment_methods") val payment_methods : List<Payment_methods>
)