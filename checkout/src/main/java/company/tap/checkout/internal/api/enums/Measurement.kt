package company.tap.checkout.internal.api.enums


/**
 * Created by AhlaamK on 6/13/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
enum class Measurement(val unit: String) {
    /**
     * Area measurement.
     */
    AREA("area"),
    /**
     * Duration measurement.
     */
    DURATION("duration"),

    /**
     * Electric charge measurement.
     */
    ELECTRIC_CHARGE("electric_charge"),

    /**
     * Electric current measurement.
     */
    ELECTRIC_CURRENT("electric_current"),
    /**
     * Energy measurement.
     */
    ENERGY("energy"),
    /**
     * Length measurement.
     */
    LENGTH("length"),
    /**
     * Mass measurement.
     */
    MASS("mass"),
    /**
     * Power measurement.
     */
    POWER("power"),
    /**
     * Volume measurement.
     */
    VOLUME("volume"),

    /**
     * Units measurement.
     */
    UNITS("units")

}