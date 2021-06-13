package company.tap.checkout.internal.apiresponse.testmodels

import androidx.annotation.Nullable
import com.google.gson.annotations.SerializedName


data class Customer (

    @Nullable @SerializedName("first_name") val first_name : String,
    @Nullable @SerializedName("middle_name") val middle_name : String,
    @Nullable @SerializedName("last_name") val last_name : String,
    @Nullable @SerializedName("email") val email : String,
    @Nullable @SerializedName("phone") val phone : Phone,
    @Nullable @SerializedName("description") val description : String,
    @Nullable @SerializedName("metadata") val metadata : Metadata,
    @Nullable @SerializedName("currency") val currency : String
)