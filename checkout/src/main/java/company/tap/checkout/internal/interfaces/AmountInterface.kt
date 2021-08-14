package company.tap.checkout.internal.interfaces

import company.tap.checkout.internal.api.models.AmountedCurrency

interface AmountInterface {
    fun changeGroupAction(isOpen: Boolean)
}