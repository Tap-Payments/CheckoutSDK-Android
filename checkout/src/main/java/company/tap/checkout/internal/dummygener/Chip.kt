package company.tap.checkout.internal.dummygener
import com.google.gson.annotations.SerializedName




data class Chip (

	@SerializedName("title") val title : String,
	@SerializedName("icon") val icon : String
)