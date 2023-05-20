package company.tap.tapuilibraryy.uikit.interfaces

/**
 *
 * Created by OlaMonir on 21/10/20
 * Copyright © 2020 Tap Payments. All rights reserved.
 *
 */

interface OpenOTPInterface {

    fun getPhoneNumber(phoneNumber : String , countryCode :String, maskedValue : String)
    fun onChangePhoneClicked()

}