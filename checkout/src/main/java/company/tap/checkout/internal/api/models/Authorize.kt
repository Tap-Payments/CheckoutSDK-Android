package company.tap.checkout.internal.api.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by AhlaamK on 6/14/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
data class Authorize(
    @SerializedName("auto") @Expose
    private var autorizeAction: AuthorizeActionResponse
): Charge()
