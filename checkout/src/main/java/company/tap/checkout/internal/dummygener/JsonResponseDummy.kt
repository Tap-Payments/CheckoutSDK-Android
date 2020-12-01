package company.tap.checkout.internal.dummygener


import com.google.gson.annotations.SerializedName



data class JsonResponseDummy (

	@SerializedName("merchant") val merchant : Merchant,
	@SerializedName("order") val order : Order,
	@SerializedName("currencies") val currencies : List<String>,
	@SerializedName("goPayCountries") val goPayCountries : List<GoPayCountries>,
	@SerializedName("goPaySavedCards") val goPaySavedCards : List<GoPaySavedCards>,
	@SerializedName("savedCards") val savedCards : List<SavedCards>,
	@SerializedName("tapCardPhoneListDataSource") val tapCardPhoneListDataSource : List<TapCardPhoneListDataSource>
)