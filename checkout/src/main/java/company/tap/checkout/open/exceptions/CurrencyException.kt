package company.tap.checkout.open.exceptions

/**
 * Created by AhlaamK on 6/13/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/

/**
 * The type Currency exception.
 */
class CurrencyException private constructor(exceptionMessage: String) :
    ExceptionInInitializerError(exceptionMessage) {
    companion object {
        /**
         * Gets unknown.
         *
         * @param code the code
         * @return the unknown
         */
        fun getUnknown(code: String): CurrencyException {
            return CurrencyException("Unknown currency: $code")
        }
    }
}

