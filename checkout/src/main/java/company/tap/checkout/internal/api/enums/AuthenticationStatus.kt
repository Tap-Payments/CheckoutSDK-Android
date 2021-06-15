package company.tap.checkout.internal.api.enums

import com.google.gson.annotations.SerializedName


/**
 * Created by AhlaamK on 6/13/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
enum class AuthenticationStatus(val authenticationStatus: String) {
    /**
     * Initiated authentication status.
     */
    @SerializedName("INITIATED")
    INITIATED("INITIATED"),
    /**
     * Authenticated authentication status.
     */
    @SerializedName("AUTHENTICATED")
    AUTHENTICATED("AUTHENTICATED"),
    /**
     * Not authenticated authentication status.
     */
    @SerializedName("NOT_AUTHENTICATED")
    NOT_AUTHENTICATED("NOT_AUTHENTICATED"),
    /**
     * Abandoned authentication status.
     */
    @SerializedName("ABANDONED")
    ABANDONED("ABANDONED"),
    /**
     * Failed authentication status.
     */
    @SerializedName("FAILED")
    FAILED("FAILED")

}