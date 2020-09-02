package company.tap.cardbusinesskit.testmodels

import com.google.gson.annotations.SerializedName




data class Issuer (

	@SerializedName("card_type") val card_type : String,
	@SerializedName("country") val country : String
)