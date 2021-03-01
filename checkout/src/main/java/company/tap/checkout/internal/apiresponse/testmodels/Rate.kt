package company.tap.checkout.internal.apiresponse.testmodels

import com.google.gson.annotations.SerializedName


data class Rate (

	@SerializedName("type") val type : String,
	@SerializedName("value") val value : Int
)