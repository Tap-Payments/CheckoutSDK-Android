package company.tap.cardbusinesskit.testmodels

import com.google.gson.annotations.SerializedName


data class Metadata (

	@SerializedName("udf1") val udf1 : String,
	@SerializedName("udf2") val udf2 : String
)