package company.tap.checkout.internal.dummygener
import com.google.gson.annotations.SerializedName
import company.tap.cardbusinesskit.testmodels.Items
import company.tap.cardbusinesskit.testmodels.Shipping
import company.tap.cardbusinesskit.testmodels.Tax


data class Order (

	@SerializedName("trx_currency") val trx_currency : String,
	@SerializedName("original_amount") val original_amount : Int,
	@SerializedName("items") val items : List<Items>,
	@SerializedName("shipping") val shipping : Shipping,
	@SerializedName("tax") val tax : List<Tax>
)