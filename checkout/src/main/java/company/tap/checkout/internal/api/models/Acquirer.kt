package company.tap.checkout.internal.api.models

import com.google.gson.annotations.SerializedName

/**
 * Created by AhlaamK on 6/13/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
data class Acquirer(
    @SerializedName("id") var id : String,
    @SerializedName("response") var response : Response,
)
