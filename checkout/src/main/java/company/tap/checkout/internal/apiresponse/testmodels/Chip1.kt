package company.tap.checkout.internal.apiresponse.testmodels
import com.google.gson.annotations.SerializedName



/**
 * Dummy till we get them from BE*/
data class Chip1 (

	@SerializedName("title") val title : String,
	@SerializedName("icon") val icon : String
)