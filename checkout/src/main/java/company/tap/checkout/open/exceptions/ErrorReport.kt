package company.tap.checkout.open.exceptions

/**
 * Created by AhlaamK on 6/17/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/

class ErrorReport : Exception {
    constructor() : super()
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
    constructor(cause: Throwable) : super(cause)
}