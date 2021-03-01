package company.tap.cardbusinesskit.testmodels

import com.google.gson.annotations.SerializedName
import company.tap.checkout.internal.apiresponse.testmodels.Destination


data class Destinations (

	@SerializedName("amount") val amount : Int,
	@SerializedName("currency") val currency : String,
	@SerializedName("count") val count : Int,
	@SerializedName("destination") val destination : List<Destination>
)