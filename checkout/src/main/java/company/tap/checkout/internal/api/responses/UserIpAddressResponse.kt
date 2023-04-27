package company.tap.checkout.internal.api.responses

import com.google.gson.annotations.SerializedName

data class UserIpAddressResponse(

    @SerializedName("country_code") var countryCode: String? = null,
    @SerializedName("country_name") var countryName: String? = null,
    @SerializedName("city") var city: String? = null,
    @SerializedName("postal") var postal: String? = null,
    @SerializedName("latitude") var latitude: Double? = null,
    @SerializedName("longitude") var longitude: Double? = null,
    @SerializedName("IPv4") var IPv4: String? = null,
    @SerializedName("state") var state: String? = null

)