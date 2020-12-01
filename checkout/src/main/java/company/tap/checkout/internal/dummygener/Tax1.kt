package company.tap.checkout.internal.dummygener
import com.google.gson.annotations.SerializedName


data class Tax1 (

	@SerializedName("description") val description : String,
	@SerializedName("name") val name : String,
	@SerializedName("rate") val rate1 : Rate1
)