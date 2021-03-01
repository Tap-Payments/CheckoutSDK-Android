package company.tap.checkout.internal.apiresponse.testmodels

import com.google.gson.annotations.SerializedName




data class Issuer (

	@SerializedName("card_type") val card_type : String,
	@SerializedName("country") val country : String
)