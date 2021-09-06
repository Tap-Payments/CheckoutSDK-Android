package company.tap.checkoutsdk.utils

import android.text.TextUtils
import android.util.Patterns
import company.tap.checkoutsdk.activities.CustomerCreateActivity

/**
 * Created by AhlaamK on 9/6/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
object Validator {
    fun isValidEmail(target: CharSequence?): Boolean {
        val validation =
            !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
        println("isValidEmail :$validation")
        CustomerCreateActivity().EMAIL_IS_VALID = validation
        return validation
    }

    fun isValidPhoneNumber(target: CharSequence?): Boolean {
        val validation = Patterns.PHONE.matcher(target).matches()
        println("isValidPhoneNumber :$validation")
        CustomerCreateActivity().MOBILE_IS_VALID = validation
        return validation
    }

    fun isValidName(charSequence: CharSequence): Boolean {
        val validation = charSequence.length > 2
        println("isValidName : $validation")
        CustomerCreateActivity().NAME_IS_VALID = validation
        return validation
    }




}