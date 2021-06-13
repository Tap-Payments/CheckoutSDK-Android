package company.tap.checkout.internal.api.enums

import company.tap.tapcardvalidator_android.CardBrand

/**
 * Created by AhlaamK on 6/13/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
/**
 * The enum Card scheme.
 */
enum class CardScheme {
    /**
     * Knet card scheme.
     */
    KNET,

    /**
     * Visa card scheme.
     */
    VISA,

    /**
     * Mastercard card scheme.
     */
    MASTERCARD,

    /**
     * American express card scheme.
     */
    AMERICAN_EXPRESS,

    /**
     * Mada card scheme.
     */
    MADA,

    /**
     * Benefit card scheme.
     */
    BENEFIT,

    /**
     * Sadad card scheme.
     */
    SADAD,

    /**
     * Fawry card scheme.
     */
    FAWRY,

    /**
     * Naps card scheme.
     */
    NAPS,

    /**
     * Oman net card scheme.
     */
    OMAN_NET;

    /**
     * Gets card brand.
     *
     * @return the card brand
     */
    val cardBrand: CardBrand
        get() = when (this) {
            KNET -> CardBrand.knet
            VISA -> CardBrand.visa
            MASTERCARD -> CardBrand.masterCard
            AMERICAN_EXPRESS -> CardBrand.americanExpress
            MADA -> CardBrand.mada
            BENEFIT -> CardBrand.benefit
            SADAD -> CardBrand.sadad
            FAWRY -> CardBrand.fawry
            NAPS -> CardBrand.naps
            OMAN_NET -> CardBrand.omanNet
            else -> CardBrand.unknown
        }
}