package company.tap.checkout.internal.dummygener
import com.google.gson.annotations.SerializedName




data class Discount1 (

	@SerializedName("type") val type : String,
	@SerializedName("value") val value : Int
)