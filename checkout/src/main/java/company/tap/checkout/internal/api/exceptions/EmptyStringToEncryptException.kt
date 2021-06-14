package company.tap.checkout.internal.api.exceptions

/**
 * Created by AhlaamK on 6/14/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
object EmptyStringToEncryptException : IllegalArgumentException() {
    val MESSAGE = "Parameter jsonString cannot be empty"

    /**
     * Instantiates a new Empty string to encrypt exception.
     */
    fun EmptyStringToEncryptException() {
        (MESSAGE)
    }
}