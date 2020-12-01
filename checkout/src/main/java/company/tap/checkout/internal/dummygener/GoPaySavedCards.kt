package company.tap.checkout.internal.dummygener

import com.google.gson.annotations.SerializedName


data class GoPaySavedCards (

	@SerializedName("chip") val chip : Chip,
	@SerializedName("chipType") val chipType : Int,
	@SerializedName("paymentType") val paymentType : String
)