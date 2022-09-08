package company.tap.checkout.open.models

import androidx.annotation.Nullable
import androidx.annotation.RestrictTo
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by AhlaamK on 6/13/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/

data class Merchant(
    @SerializedName("id")
    @Expose
    @Nullable var id: String?
):Serializable
