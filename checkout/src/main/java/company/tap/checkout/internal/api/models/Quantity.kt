package company.tap.checkout.internal.api.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import company.tap.checkout.internal.api.enums.Measurement
import company.tap.checkout.internal.interfaces.MeasurementUnit
import java.io.Serializable
import java.math.BigDecimal

/**
 * Created by AhlaamK on 6/13/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/

/**
 * The type Quantity.
 */
class Quantity(
    @SerializedName("measurement_group") @Expose var measurementGroup: Measurement,

    @SerializedName("measurement_unit") @Expose var measurementUnit: String,

    @SerializedName("value") @Expose var value: BigDecimal?
) : Serializable {
    /**
     * Instantiates a new Quantity.
     *
     * @param measurementUnit the measurement unit
     * @param value           the value
     */
    open fun Quantity(measurementUnit: MeasurementUnit, value: BigDecimal?) {
        measurementGroup = measurementUnit.getMeasurementGroup()
        this.measurementUnit = measurementUnit.toString()
        this.value = value
    }

}
