package company.tap.cardbusinesskit.testmodels

import com.google.gson.annotations.SerializedName


data class Discount (

	@SerializedName("type") val type : String,
	@SerializedName("value") val value : Int
)