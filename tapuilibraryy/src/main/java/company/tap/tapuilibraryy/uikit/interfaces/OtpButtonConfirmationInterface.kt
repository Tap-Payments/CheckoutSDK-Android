package company.tap.tapuilibraryy.uikit.interfaces

/**
 *
 * Created by OlaMonir on 21/10/20
 * Copyright © 2020 Tap Payments. All rights reserved.
 *
 */

/**
 * Interface which have function take argument of
 * otp number to send it to api from checkout sdk and
 * this function return if otp is valid or not
 */
interface OtpButtonConfirmationInterface {
    fun onOtpButtonConfirmationClick(otpNumber: String): Boolean

}