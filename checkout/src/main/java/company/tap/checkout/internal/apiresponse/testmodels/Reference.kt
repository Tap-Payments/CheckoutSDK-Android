package company.tap.checkout.internal.apiresponse.testmodels

import com.google.gson.annotations.SerializedName


data class Reference (

	@SerializedName("acquirer") val acquirer : Int,
	@SerializedName("gateway") val gateway : Int,
	@SerializedName("payment") val payment : Int,
	@SerializedName("track") val track : Int,
	@SerializedName("transaction") val transaction : Int,
	@SerializedName("order") val order : Int
)