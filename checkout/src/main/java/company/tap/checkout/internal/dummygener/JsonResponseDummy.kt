package company.tap.checkout.internal.dummygener


import com.google.gson.annotations.SerializedName



data class JsonResponseDummy (

	@SerializedName("merchant") val merchant1 : Merchant1,
	@SerializedName("order") val order1 : Order1,
	@SerializedName("currencies") val currencies : List<String>,
	@SerializedName("goPayCountries") val goPayCountries : List<GoPayCountries>,
	@SerializedName("goPaySavedCards") val goPaySavedCards : List<GoPaySavedCards>,
	@SerializedName("savedCards") val savedCards : List<SavedCards>,
	@SerializedName("tapCardPhoneListDataSource") val tapCardPhoneListDataSource : List<TapCardPhoneListDataSource>
)