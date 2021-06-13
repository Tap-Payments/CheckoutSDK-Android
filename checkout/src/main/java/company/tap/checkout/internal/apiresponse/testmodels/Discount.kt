package company.tap.checkout.internal.apiresponse.testmodels

import com.google.gson.annotations.SerializedName


data class Discount (

	@SerializedName("type") var type : String,
	@SerializedName("value") var value : Int
)