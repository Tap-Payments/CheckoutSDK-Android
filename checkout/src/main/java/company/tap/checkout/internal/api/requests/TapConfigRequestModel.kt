package company.tap.checkout.internal.api.requests

import androidx.annotation.Nullable
import androidx.annotation.RestrictTo
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by AhlaamK on 3/26/22.

Copyright (c) 2022    Tap Payments.
All rights reserved.
 **/
@RestrictTo(RestrictTo.Scope.LIBRARY)
open class TapConfigRequestModel(
    @SerializedName("gateway")
    @Expose
    @Nullable var gateway: Gateway?) {

}
@RestrictTo(RestrictTo.Scope.LIBRARY)
data class Gateway(
    @SerializedName("publicKey")
    @Expose
    @Nullable var publicKey: String,
    @SerializedName("merchantId")
    @Expose
    @Nullable var merchantId: String,
    @SerializedName("config")
    @Expose
    @Nullable var config: Config,

): Serializable

@RestrictTo(RestrictTo.Scope.LIBRARY)
data class Config(
    @SerializedName("application")
    @Expose
    @Nullable var application: String,


    ): Serializable