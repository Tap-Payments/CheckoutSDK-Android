package company.tap.checkout.internal.apiresponse.testmodels
import com.google.gson.annotations.SerializedName
import company.tap.checkout.internal.enums.PaymentTypeEnum



data class TapCardPhoneListDataSource (

	@SerializedName("brand") val brand : String,
	@SerializedName("icon") val icon : String,
	@SerializedName("paymentType") val paymentType : PaymentTypeEnum
)