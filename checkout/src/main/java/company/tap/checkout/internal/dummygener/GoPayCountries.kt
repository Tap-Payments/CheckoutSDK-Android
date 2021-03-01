package company.tap.checkout.internal.dummygener
import com.google.gson.annotations.SerializedName



data class GoPayCountries (

	@SerializedName("nameAR") val nameAR : String,
	@SerializedName("nameEN") val nameEN : String,
	@SerializedName("code") val code : Int,
	@SerializedName("phoneLength") val phoneLength : Int
)