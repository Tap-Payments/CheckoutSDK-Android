package company.tap.checkout.internal.api.models

import androidx.annotation.Nullable
import androidx.annotation.RestrictTo
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by AhlaamK on 6/13/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
@RestrictTo(RestrictTo.Scope.LIBRARY)
data class Merchant(
    @SerializedName("id")
    @Expose
    @Nullable var id: String
)
