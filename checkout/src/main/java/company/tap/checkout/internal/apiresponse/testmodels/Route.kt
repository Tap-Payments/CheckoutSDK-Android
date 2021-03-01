package company.tap.cardbusinesskit.testmodels

import com.google.gson.annotations.SerializedName
import company.tap.checkout.internal.apiresponse.testmodels.Issuer


data class Route (

    @SerializedName("payment_method") val payment_method : String,
    @SerializedName("issuer") val issuer : Issuer,
    @SerializedName("sequence") val sequence : List<String>
)