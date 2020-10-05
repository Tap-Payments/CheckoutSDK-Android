package company.tap.checkout.open.controller

/**
 * Created by AhlaamK on 10/5/20.

Copyright (c) 2020    Tap Payments.
All rights reserved.
 **/
//Session Manager is responsible for starting the sdk session as active or inactive
class SessionManager {
    companion object {
        private var sessionStatus = false

        fun isSessionEnabled(): Boolean {
            return sessionStatus
        }

        fun setActiveSession(status: Boolean) {
            sessionStatus = status
        }
    }
}