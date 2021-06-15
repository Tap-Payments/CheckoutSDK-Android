package company.tap.checkout.internal.api.enums

import com.google.gson.annotations.SerializedName
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
    @SerializedName("INITIATED")
    INITIATED,

    /**
     * In progress charge status.
     */
    @SerializedName("IN_PROGRESS")
    IN_PROGRESS,

    /**
     * Abandoned charge status.
     */
    @SerializedName("ABANDONED")
    ABANDONED,

    /**
     * Cancelled charge status.
     */
    @SerializedName("CANCELLED")
    CANCELLED,

    /**
     * Failed charge status.
     */
    @SerializedName("FAILED")
    FAILED,

    /**
     * Declined charge status.
     */
    @SerializedName("DECLINED")
    DECLINED,

    /**
     * Restricted charge status.
     */
    @SerializedName("RESTRICTED")
    RESTRICTED,

    /**
     * Captured charge status.
     */
    @SerializedName("CAPTURED")
    CAPTURED,

    /**
     * Void charge status.
     */
    @SerializedName("VOID")
    VOID,

    /**
     * Unknown charge status.
     */
    @SerializedName("UNKNOWN")
    UNKNOWN,

    /**
     * Authorized charge status.
     */
    @SerializedName("AUTHORIZED")
    AUTHORIZED,

    /**
     * Timedout charge status.
     */
    @SerializedName("TIMEDOUT")
    TIMEDOUT,

    /**
     * Valid charge status.
     */
    @SerializedName("VALID")
    VALID,

    /**
     * Invalid charge status.
     */
    @SerializedName("INVALID")
    INVALID
}
