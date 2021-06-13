package company.tap.checkout.internal.apiresponse.testmodels

import com.google.gson.annotations.SerializedName


data class Redirect (

	@SerializedName("url") val url : String
)