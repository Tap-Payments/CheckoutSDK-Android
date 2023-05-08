package company.tap.tapuilibrary.uikit.interfaces

import company.tap.tapuilibrary.uikit.enums.ActionButtonState

/**
 *
 * Copyright © 2020 Tap Payments. All rights reserved.
 *
 */
interface TapActionButtonInterface {
    fun onStateChanged(state: ActionButtonState)
    fun onSelectPaymentOptionActionListener()
    fun onEnterValidPhoneNumberActionListener()
    fun onEnterValidCardNumberActionListener()
}