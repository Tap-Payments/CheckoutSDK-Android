package company.tap.checkout.internal.api.requests

import androidx.annotation.Nullable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by AhlaamK on 3/27/22.

Copyright (c) 2022    Tap Payments.
All rights reserved.
 **/
class BinLookUpRequestModel (
        @SerializedName("bin")
        @Expose
        @Nullable var bin: String?
        )