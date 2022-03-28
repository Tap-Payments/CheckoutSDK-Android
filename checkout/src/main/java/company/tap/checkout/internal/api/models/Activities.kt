package company.tap.checkout.internal.api.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import company.tap.checkout.internal.api.enums.ChargeStatus

/**
 * Created by AhlaamK on 3/28/22.

Copyright (c) 2022    Tap Payments.
All rights reserved.
 **/
data class Activities(
        @SerializedName("id") var id: String,

        @SerializedName("object") @Expose
        private var `object`: String,

        @SerializedName("created")
        @Expose
        var created: Long?,

        @SerializedName("status")
        @Expose
        var status: ChargeStatus?,

        @SerializedName("currency") @Expose
        private var currency: String?,

        @SerializedName("amount") @Expose
        var amount: Double = 0.0,

        @SerializedName("remarks") @Expose
        var remarks: String


)
