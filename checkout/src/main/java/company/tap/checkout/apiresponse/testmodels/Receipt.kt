package company.tap.cardbusinesskit.testmodels

import com.google.gson.annotations.SerializedName




data class Receipt (

	@SerializedName("email") val email : Boolean,
	@SerializedName("sms") val sms : Boolean
)