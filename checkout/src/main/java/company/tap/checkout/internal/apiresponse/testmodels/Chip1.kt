package company.tap.checkout.internal.apiresponse.testmodels
import com.google.gson.annotations.SerializedName




data class Chip1 (

	@SerializedName("title") val title : String,
	@SerializedName("icon") val icon : String
)