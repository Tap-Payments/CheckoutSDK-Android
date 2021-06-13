package company.tap.checkout.internal.apiresponse.testmodels

import com.google.gson.annotations.SerializedName


data class Phone (

	@SerializedName("country_code") val country_code : Int,
	@SerializedName("number") val number : Int
)