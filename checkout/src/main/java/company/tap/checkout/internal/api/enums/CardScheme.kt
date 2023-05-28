package company.tap.checkout.internal.api.enums

import com.google.gson.annotations.SerializedName
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
    @SerializedName("KNET")
    KNET,

    /**
     * Visa card scheme.
     */
    @SerializedName("VISA")
    VISA,

    /**
     * Mastercard card scheme.
     */
    @SerializedName("MASTERCARD")
    MASTERCARD,

    /**
     * American express card scheme.
     */
    @SerializedName("AMERICAN_EXPRESS")
    AMERICAN_EXPRESS,

    /**
     * Mada card scheme.
     */
    @SerializedName("MADA")
    MADA,

    /**
     * Benefit card scheme.
     */
    @SerializedName("BENEFIT")
    BENEFIT,

    /**
     * Sadad card scheme.
     */
    @SerializedName("SADAD")
    SADAD,

    /**
     * Fawry card scheme.
     */
    @SerializedName("FAWRY")
    FAWRY,

    /**
     * Naps card scheme.
     */
    @SerializedName("NAPS")
    NAPS,

    /**
     * Oman net card scheme.
     */
    @SerializedName("OMANNET")
    OMAN_NET,

    /**
     * Oman net card scheme.
     */
    @SerializedName("MEEZA")
    MEEZA;

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
            MEEZA -> CardBrand.meeza
            else -> CardBrand.unknown
        }
}