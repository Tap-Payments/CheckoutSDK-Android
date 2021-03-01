package company.tap.checkout.internal.apiresponse.testmodels

import com.google.gson.annotations.SerializedName




data class Receipt (

	@SerializedName("email") val email : Boolean,
	@SerializedName("sms") val sms : Boolean
)