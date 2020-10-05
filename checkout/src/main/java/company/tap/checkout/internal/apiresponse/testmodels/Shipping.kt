package company.tap.cardbusinesskit.testmodels

import com.google.gson.annotations.SerializedName




data class Shipping (

	@SerializedName("amount") val amount : Int,
	@SerializedName("currency") val currency : String,
	@SerializedName("description") val description : String,
	@SerializedName("provider") val provider : String,
	@SerializedName("shipping_note") val shipping_note : String,
	@SerializedName("service") val service : String
)