package company.tap.cardbusinesskit.testmodels

import com.google.gson.annotations.SerializedName


data class Route (

    @SerializedName("payment_method") val payment_method : String,
    @SerializedName("issuer") val issuer : Issuer,
    @SerializedName("sequence") val sequence : List<String>
)