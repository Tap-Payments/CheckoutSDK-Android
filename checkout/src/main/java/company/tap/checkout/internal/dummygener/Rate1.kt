package company.tap.checkout.internal.dummygener
import com.google.gson.annotations.SerializedName




data class Rate1 (

	@SerializedName("type") val type : String,
	@SerializedName("value") val value : Int
)