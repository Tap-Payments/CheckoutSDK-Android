package company.tap.checkout.internal.api.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by AhlaamK on 6/13/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
data class PhoneNumber(
    @SerializedName("country_code") @Expose
     var countryCode: String? = null
,
    @SerializedName("number")
@Expose
 val number: String? = null
)
