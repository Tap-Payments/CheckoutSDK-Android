package company.tap.checkout.internal.apiresponse.testmodels
import com.google.gson.annotations.SerializedName




data class SavedCards (

	@SerializedName("chip") val chip1 : Chip1,
	@SerializedName("paymentType") val paymentType : String,
	@SerializedName("chipType") val chipType : Int,
	@SerializedName("currencies") val currencies : List<String>
)