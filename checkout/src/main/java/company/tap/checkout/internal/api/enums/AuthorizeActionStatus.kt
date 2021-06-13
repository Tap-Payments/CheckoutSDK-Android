package company.tap.checkout.internal.apiresponse.enums

/**
 * Created by AhlaamK on 6/13/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/


/**
 * The enum Authorize action status.
 */
enum class AuthorizeActionStatus {
    /**
     * Pending authorize action status.
     */
    PENDING,

    /**
     * Scheduled authorize action status.
     */
    SCHEDULED,

    /**
     * Captured authorize action status.
     */
    CAPTURED,

    /**
     * Failed authorize action status.
     */
    FAILED,

    /**
     * Declined authorize action status.
     */
    DECLINED,

    /**
     * Void authorize action status.
     */
    VOID
}
