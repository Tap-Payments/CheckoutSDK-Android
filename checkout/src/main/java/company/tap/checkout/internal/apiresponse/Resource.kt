package company.tap.checkout.internal.apiresponse

/**
 * Created by AhlaamK on 11/15/20.

Copyright (c) 2020    Tap Payments.
All rights reserved.
 **/
sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T) : Resource<T>(data)
    class Loading<T>() : Resource<T>()
    class Finished<T>() : Resource<T>()
    class Error<T>(message: String) : Resource<T>(message = message)
}