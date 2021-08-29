package company.tap.checkout.internal.apiresponse.testmodels

import com.google.gson.annotations.SerializedName


data class GoPaySavedCards (

	@SerializedName("chip") val chip1 : Chip1,
	@SerializedName("chipType") val chipType : Int,
	@SerializedName("paymentType") val paymentType : String
)