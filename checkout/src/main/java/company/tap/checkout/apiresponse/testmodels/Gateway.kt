package company.tap.cardbusinesskit.testmodels

import com.google.gson.annotations.SerializedName




data class Gateway (

	@SerializedName("merchant_id") val merchant_id : Int,
	@SerializedName("payment_methods") val payment_methods : String,
	@SerializedName("route") val route : List<Route>
)