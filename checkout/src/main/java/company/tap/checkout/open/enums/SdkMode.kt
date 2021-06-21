package company.tap.checkout.open.enums

import com.google.gson.annotations.SerializedName

/**
 * Created by AhlaamK on 6/14/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
enum class SdkMode {
    /**
     * Sandbox is for testing purposes
     */
    @SerializedName("Sandbox")
    SAND_BOX,
    /**
     * Production is for live
     */
    @SerializedName("Production")
    PRODUCTION
}