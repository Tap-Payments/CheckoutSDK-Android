package company.tap.cardbusinesskit.testmodels

import com.google.gson.annotations.SerializedName


data class Phone (

	@SerializedName("country_code") val country_code : Int,
	@SerializedName("number") val number : Int
)