package company.tap.checkout.internal.api.models

import androidx.annotation.Nullable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by AhlaamK on 6/13/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
data class PhoneNumber(
    @SerializedName("country_code")
    @Expose
    @Nullable var countryCode: String?,
    @SerializedName("number")
    @Expose
    @Nullable val number: String?
) : Serializable
