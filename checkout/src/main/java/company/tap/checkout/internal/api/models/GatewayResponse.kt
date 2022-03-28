package company.tap.checkout.internal.api.models

import com.google.gson.annotations.SerializedName

/**
 * Created by AhlaamK on 3/28/22.

Copyright (c) 2022    Tap Payments.
All rights reserved.
 **/
data class GatewayResponse ( @SerializedName("response")
                             val response: Response)