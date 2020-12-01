package company.tap.checkout.internal.dummygener
import com.google.gson.annotations.SerializedName




data class SavedCards (

	@SerializedName("chip") val chip : Chip,
	@SerializedName("paymentType") val paymentType : String,
	@SerializedName("chipType") val chipType : Int,
	@SerializedName("currencies") val currencies : List<String>
)