package company.tap.checkout.internal.api.models

import androidx.annotation.Nullable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import java.io.Serializable

/**
 * Created by AhlaamK on 6/13/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
@kotlinx.serialization.Serializable
data class PhoneNumber(
    @SerialName("country_code")
    @Expose
    @Nullable var countryCode: String?,
    @SerialName("number")
    @Expose
    @Nullable val number: String?
) : Serializable
