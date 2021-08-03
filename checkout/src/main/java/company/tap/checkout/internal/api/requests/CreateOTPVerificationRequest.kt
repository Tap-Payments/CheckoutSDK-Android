package company.tap.checkout.internal.api.requests

import androidx.annotation.RestrictTo
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import company.tap.checkout.internal.api.enums.AuthenticationType

/**
 * Created by AhlaamK on 8/3/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
/**
 * The type Create otp verification request.
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
class CreateOTPVerificationRequest private constructor(id: AuthenticationType, otp: String) {
    @SerializedName("type")
    @Expose
    private val type: AuthenticationType = id

    @SerializedName("value")
    @Expose
    private val value: String = otp

    /**
     * The type Builder.
     */
   open class Builder(id: AuthenticationType, otp: String) {
        private val createOTPVerificationRequest: CreateOTPVerificationRequest = CreateOTPVerificationRequest(id, otp)

        /**
         * Build create otp verification request.
         *
         * @return the create otp verification request
         */
        fun build(): CreateOTPVerificationRequest {
            return createOTPVerificationRequest
        }

    }

}
