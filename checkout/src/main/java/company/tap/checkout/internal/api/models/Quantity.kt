package company.tap.checkout.internal.api.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

/**
 * Created by AhlaamK on 6/13/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/

/**
 * The type Quantity.
 */
data class Quantity(
    @SerializedName("measurement_group") @Expose var measurementGroup: Measurement,

    @SerializedName("measurement_unit") @Expose val measurementUnit: MeasurementUnit,

    @SerializedName("value") @Expose val value: BigDecimal
)
