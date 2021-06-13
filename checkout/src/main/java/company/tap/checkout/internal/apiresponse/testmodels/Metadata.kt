package company.tap.checkout.internal.apiresponse.testmodels

import androidx.annotation.Nullable
import com.google.gson.annotations.SerializedName

data class  Metadata (

	@Nullable @SerializedName("udf1") var udf1 : String,
	@Nullable @SerializedName("udf2") var udf2 : String
)