package company.tap.checkout.internal.dummygener
import com.google.gson.annotations.SerializedName
import company.tap.checkout.internal.enums.PaymentTypeEnum
import company.tap.tapcardvalidator_android.CardBrand


data class TapCardPhoneListDataSources (

	@SerializedName("brand") val brand : CardBrand,
	@SerializedName("icon") val icon : String,
	@SerializedName("paymentType") val paymentType : PaymentTypeEnum
)