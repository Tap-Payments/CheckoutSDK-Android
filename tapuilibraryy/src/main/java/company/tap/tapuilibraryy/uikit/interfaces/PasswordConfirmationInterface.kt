package company.tap.tapuilibraryy.uikit.interfaces

/**
 *
 * Created by OlaMonir on 8/12/20
 * Copyright © 2020 Tap Payments. All rights reserved.
 *
 */

/**
 * Interface which have function take argument of
 * password number to send it to api from checkout sdk and
 * this function return if otp `is valid or not
 */
interface PasswordConfirmationInterface {
    fun onOtpButtonConfirmationClick(password: String):Boolean
}