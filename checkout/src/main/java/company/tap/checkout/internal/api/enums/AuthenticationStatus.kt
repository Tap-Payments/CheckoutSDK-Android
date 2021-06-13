package company.tap.checkout.internal.api.enums



/**
 * Created by AhlaamK on 6/13/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
enum class AuthenticationStatus(val authenticationStatus: String) {
    /**
     * Initiated authentication status.
     */
    INITIATED("INITIATED"),
    /**
     * Authenticated authentication status.
     */
    AUTHENTICATED("AUTHENTICATED"),
    /**
     * Not authenticated authentication status.
     */
    NOT_AUTHENTICATED("NOT_AUTHENTICATED"),
    /**
     * Abandoned authentication status.
     */
    ABANDONED("ABANDONED"),
    /**
     * Failed authentication status.
     */
    FAILED("FAILED")

}