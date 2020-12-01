package company.tap.checkout.internal.dummygener
import com.google.gson.annotations.SerializedName



data class Order1 (

	@SerializedName("trx_currency") val trx_currency : String,
	@SerializedName("original_amount") val original_amount : Int,
	@SerializedName("items") val items : List<Items1>,
	@SerializedName("shipping") val shipping : Shipping1,
	@SerializedName("tax") val tax : List<Tax1>
)