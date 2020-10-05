package company.tap.cardbusinesskit.testmodels

import com.google.gson.annotations.SerializedName




data class Contact_info (

	@SerializedName("type") val type : String,
	@SerializedName("key") val key : String,
	@SerializedName("value") val value : String,
	@SerializedName("image") val image : String,
	@SerializedName("background_color") val background_color : String
)