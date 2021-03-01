package company.tap.checkout.internal.apiresponse.testmodels

import androidx.annotation.Nullable
import com.google.gson.annotations.SerializedName
import company.tap.checkout.internal.apiresponse.testmodels.Rate


data class Tax (

	@Nullable @SerializedName("description") val description : String,
	@Nullable @SerializedName("name") val name : String,
	@Nullable @SerializedName("rate") val rate : Rate
)