package company.tap.checkout.internal.apiresponse.testmodels

import com.google.gson.annotations.SerializedName


data class Post (

	@SerializedName("status") val status : String,
	@SerializedName("url") val url : String
)