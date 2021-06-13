package company.tap.checkout.internal.api.enums

import java.io.Serializable

/**
 * Created by AhlaamK on 6/13/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
/**
 * The enum Charge status.
 */
enum class ChargeStatus : Serializable {
    /**
     * Initiated charge status.
     */
    INITIATED,

    /**
     * In progress charge status.
     */
    IN_PROGRESS,

    /**
     * Abandoned charge status.
     */
    ABANDONED,

    /**
     * Cancelled charge status.
     */
    CANCELLED,

    /**
     * Failed charge status.
     */
    FAILED,

    /**
     * Declined charge status.
     */
    DECLINED,

    /**
     * Restricted charge status.
     */
    RESTRICTED,

    /**
     * Captured charge status.
     */
    CAPTURED,

    /**
     * Void charge status.
     */
    VOID,

    /**
     * Unknown charge status.
     */
    UNKNOWN,

    /**
     * Authorized charge status.
     */
    AUTHORIZED,

    /**
     * Timedout charge status.
     */
    TIMEDOUT,

    /**
     * Valid charge status.
     */
    VALID,

    /**
     * Invalid charge status.
     */
    INVALID
}
