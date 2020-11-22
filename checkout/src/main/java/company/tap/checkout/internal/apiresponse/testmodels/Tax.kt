package company.tap.cardbusinesskit.testmodels

import androidx.annotation.Nullable
import com.google.gson.annotations.SerializedName




data class Tax (

	@Nullable @SerializedName("description") val description : String,
	@Nullable @SerializedName("name") val name : String,
	@Nullable @SerializedName("rate") val rate : Rate
)