package company.tap.checkout.internal.api.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import company.tap.checkout.internal.api.enums.URLStatus
import java.io.Serializable

/**
 * Created by AhlaamK on 6/14/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
data class TrackingURL(
        /**
     * The Status.
     */
    @SerializedName("status") @Expose
        var status: URLStatus? = URLStatus.PENDING,

        /**
     * The Url.
     */
    @SerializedName("url")
    @Expose
        var url: String? = null
) : Serializable
