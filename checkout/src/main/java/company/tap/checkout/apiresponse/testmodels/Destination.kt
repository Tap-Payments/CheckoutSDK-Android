package company.tap.cardbusinesskit.testmodels

import com.google.gson.annotations.SerializedName




data class Destination (

	@SerializedName("id") val id : Int,
	@SerializedName("amount") val amount : Int,
	@SerializedName("currency") val currency : String,
	@SerializedName("description") val description : String,
	@SerializedName("reference") val reference : Int
)