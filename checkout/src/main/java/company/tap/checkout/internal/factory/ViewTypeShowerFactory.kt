package company.tap.checkout.internal.factory

import company.tap.checkout.internal.enums.ViewsType

interface ViewTypeShowerFactory {
    fun createViewsFromType(viewType: ViewsType)
}