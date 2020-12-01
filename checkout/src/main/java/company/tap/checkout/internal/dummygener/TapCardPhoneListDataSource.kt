package company.tap.checkout.internal.dummygener
import com.google.gson.annotations.SerializedName




data class TapCardPhoneListDataSource (

	@SerializedName("brand") val brand : String,
	@SerializedName("icon") val icon : String,
	@SerializedName("paymentType") val paymentType : String
)