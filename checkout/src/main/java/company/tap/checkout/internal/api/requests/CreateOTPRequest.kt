package company.tap.checkout.internal.api.requests

import androidx.annotation.RestrictTo
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by AhlaamK on 8/3/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
/**
 * The type Create otp request.
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
open class CreateOTPRequest private constructor(@field:Expose @field:SerializedName("id") private val id: String) {
    /**
     * The type Builder.
     */
    class Builder(id: String) {
        private val createOTPRequest: CreateOTPRequest = CreateOTPRequest(id)
        /**
         * Build create otp request.
         *
         * @return the create otp request
         */
        fun build(): CreateOTPRequest {
            return createOTPRequest
        }

    }
}
