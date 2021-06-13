package company.tap.checkout.open.models

import androidx.annotation.Nullable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

/**
 * Created by AhlaamK on 6/13/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/

/**
* The type Shipping.
*/
data class Shipping(
    @SerializedName("name") private var name: String,
    @SerializedName("description") @Nullable val description: String,
    @SerializedName("amount") val amount: BigDecimal
)
