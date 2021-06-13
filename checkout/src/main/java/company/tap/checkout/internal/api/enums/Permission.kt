package company.tap.checkout.internal.api.enums

/**
 * Created by AhlaamK on 6/13/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
/**
 * The enum Permission.
 */
enum class Permission(val permission: String) {
    /**
     * Pci permission.
     */
    PCI("pci"),

    /**
     * Merchant checkout permission.
     */
    MERCHANT_CHECKOUT("merchant_checkout"),

    /**
     * Threedsecure disabled permission.
     */
    THREEDSECURE_DISABLED("threeDSecure_disabled")
}