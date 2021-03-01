package company.tap.checkout.internal.dummygener
import com.google.gson.annotations.SerializedName




data class Currencies1 (

	@SerializedName("currencytitle") val currencytitle : String,
	@SerializedName("currencyicon") val currencyicon : String,
	@SerializedName("conversionrate") val conversionrate : Double
)