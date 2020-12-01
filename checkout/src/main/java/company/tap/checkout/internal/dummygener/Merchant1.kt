package company.tap.checkout.internal.dummygener
import com.google.gson.annotations.SerializedName




data class Merchant1 (

	@SerializedName("name") val name : String,
	@SerializedName("logo") val logo : String
)