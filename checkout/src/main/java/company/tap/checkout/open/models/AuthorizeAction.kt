package company.tap.checkout.open.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import company.tap.checkout.internal.api.enums.AuthorizeActionType

/**
 * Created by AhlaamK on 6/13/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
class AuthorizeAction(void: AuthorizeActionType, i: Int) {
    @SerializedName("type")
    @Expose
    private var type: AuthorizeActionType? = AuthorizeActionType.VOID

    @SerializedName("time")
    @Expose
    private var timeInHours = 168

    /**
     * Gets default.
     *
     * @return the default
     */
   open fun getDefault() {
        return AuthorizeAction(AuthorizeActionType.VOID, 168)
    }

    /**
     * Gets type.
     *
     * @return the type
     */
    fun getType(): AuthorizeActionType? {
        return type
    }

    /**
     * Gets time in hours.
     *
     * @return the time in hours
     */
    fun getTimeInHours(): Int {
        return timeInHours
    }

    /**
     * Instantiates a new Authorize action.
     *
     * @param type        the type
     * @param timeInHours the time in hours
     */
    fun AuthorizeAction(type: AuthorizeActionType?, timeInHours: Int) {
        this.type = type
        this.timeInHours = timeInHours
    }
}