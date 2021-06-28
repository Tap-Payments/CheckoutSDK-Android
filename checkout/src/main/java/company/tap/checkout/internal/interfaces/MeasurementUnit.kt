package company.tap.checkout.internal.interfaces

import company.tap.checkout.internal.api.enums.Measurement

/**
 * Created by AhlaamK on 6/13/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
interface MeasurementUnit {
    /**
     * Gets measurement group.
     *
     * @return the measurement group
     */
    fun getMeasurementGroup(): Measurement
}