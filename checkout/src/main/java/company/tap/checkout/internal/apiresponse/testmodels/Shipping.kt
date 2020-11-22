package company.tap.cardbusinesskit.testmodels

import androidx.annotation.Nullable
import com.google.gson.annotations.SerializedName




data class Shipping (

	@Nullable @SerializedName("amount") val amount : Int,
	@Nullable @SerializedName("currency") val currency : String,
	@Nullable @SerializedName("description") val description : String,
	@Nullable @SerializedName("provider") val provider : String,
	@Nullable @SerializedName("shipping_note") val shipping_note : String,
	@Nullable @SerializedName("service") val service : String
)