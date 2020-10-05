package company.tap.cardbusinesskit.testmodels

import com.google.gson.annotations.SerializedName


data class Post (

	@SerializedName("status") val status : String,
	@SerializedName("url") val url : String
)