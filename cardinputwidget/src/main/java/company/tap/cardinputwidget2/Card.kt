package company.tap.cardinputwidget

import androidx.annotation.IntRange
import androidx.annotation.Size
import company.tap.cardinputwidget.utils.CardUtils

/**
 * A representation of a Card API object.
 */
data class Card(

    /**
     * @return the [number] of this card
     */
    var number: String?,

    /**
     * @return the [cvc] for this card
     */
    val cvc: String?,

    /**
     * @return Two-digit number representing the card’s expiration month.
     *
     */
    @get:IntRange(from = 1, to = 12)
    val expMonth: Int?,

    /**
     * @return Four-digit number representing the card’s expiration year.
     *
     */
    val expYear: Int?,

    /**
     * @return Cardholder name.
     */
    val name: String?,

    /**
     * @return Address line 1 (Street address/PO Box/Company name).
     */
    val addressLine1: String? =null,

    /**
     * @return If address_line1 was provided, results of the check: `pass`, `fail`, `unavailable`,
     * or `unchecked`.
     */
    val addressLine1Check: String? = null,

    /**
     * @return Address line 2 (Apartment/Suite/Unit/Building).
     */
    val addressLine2: String?= null,

    /**
     * @return City/District/Suburb/Town/Village.
     */
    val addressCity: String?= null,

    /**
     * @return State/County/Province/Region.
     */
    val addressState: String?= null,

    /**
     * @return ZIP or postal code.
     */
    val addressZip: String?= null,

    /**
     * @return If `address_zip` was provided, results of the check: `pass`, `fail`, `unavailable`,
     * or `unchecked`.
     */
    val addressZipCheck: String?= null,

    /**
     * @return Billing address country, if provided when creating card.
     */
    val addressCountry: String?= null,

    /**
     * @return The last four digits of the card.
     */
    @Size(4)
    val last4: String?,

    /**
     * @return Card brand. Can be `"American Express"`, `"Diners Club"`, `"Discover"`, `"JCB"`,
     * `"MasterCard"`, `"UnionPay"`, `"Visa"`, or `"Unknown"`.
     */
    val brand: CardBrand,


    /**
     * @return Uniquely identifies this particular card number. You can use this attribute to
     * check whether two customers who’ve signed up with you are using the same card number,
     * for example.
     */
    val fingerprint: String?= null,

    /**
     * @return Two-letter ISO code representing the country of the card. You could use this
     * attribute to get a sense of the international breakdown of cards you’ve collected.
     */
    val country: String?= null,

    /**
     * @return Three-letter ISO code for currency. Only
     * applicable on accounts (not customers or recipients). The card can be used as a transfer
     * destination for funds in this currency.
     */
    val currency: String?= null,

    /**
     * @return The ID of the customer that this card belongs to.
     */
    val customerId: String?= null,

    /**
     * @return If a CVC was provided, results of the check: `pass`, `fail`, `unavailable`,
     * or `unchecked`.
     */
    val cvcCheck: String?= null,

    /**
     * @return Set of key-value pairs that you can attach to an object. This can be useful fo
     * storing additional information about the object in a structured format.
     */
    val metadata: Map<String, String>?
) {
    /**
     * Builder class for a [Card] model.
     *
     * Constructor with most common [Card] fields.
     *
     * @param number the credit card number
     * @param expMonth the expiry month, as an integer value between 1 and 12
     * @param expYear the expiry year
     * @param cvc the card CVC number
     */
    class Builder(
        internal val number: String,
        @param:IntRange(from = 1, to = 12) internal val expMonth: Int? = null,
        @param:IntRange(from = 0) internal val expYear: Int? = null,
        internal val cvc: String? = null
    ) {
        private var name: String? = null
        private var addressLine1: String? = null
        private var addressLine1Check: String? = null
        private var addressLine2: String? = null
        private var addressCity: String? = null
        private var addressState: String? = null
        private var addressZip: String? = null
        private var addressZipCheck: String? = null
        private var addressCountry: String? = null
        private var brand: CardBrand? = null

        @Size(4)
        private var last4: String? = null
        private var fingerprint: String? = null
        private var country: String? = null
        private var currency: String? = null
        private var customerId: String? = null
        private var cvcCheck: String? = null
        private var id: String? = null
        private var metadata: Map<String, String>? = null
        private var loggingTokens: Set<String>? = null

        fun name(name: String?): Builder = apply {
            this.name = name
        }

        fun addressLine1(address: String?): Builder = apply {
            this.addressLine1 = address
        }

        fun addressLine1Check(addressLine1Check: String?): Builder = apply {
            this.addressLine1Check = addressLine1Check
        }

        fun addressLine2(address: String?): Builder = apply {
            this.addressLine2 = address
        }

        fun addressCity(city: String?): Builder = apply {
            this.addressCity = city
        }

        fun addressState(state: String?): Builder = apply {
            this.addressState = state
        }

        fun addressZip(zip: String?): Builder = apply {
            this.addressZip = zip
        }

        fun addressZipCheck(zipCheck: String?): Builder = apply {
            this.addressZipCheck = zipCheck
        }

        fun addressCountry(country: String?): Builder = apply {
            this.addressCountry = country
        }

        fun brand(brand: CardBrand?): Builder = apply {
            this.brand = brand
        }

        fun fingerprint(fingerprint: String?): Builder = apply {
            this.fingerprint = fingerprint
            return this
        }

        fun country(country: String?): Builder = apply {
            this.country = country
        }

        fun currency(currency: String?): Builder = apply {
            this.currency = currency
        }

        fun customer(customerId: String?): Builder = apply {
            this.customerId = customerId
        }

        fun cvcCheck(cvcCheck: String?): Builder = apply {
            this.cvcCheck = cvcCheck
        }

        fun last4(last4: String?): Builder = apply {
            this.last4 = last4
        }

        fun id(id: String?): Builder = apply {
            this.id = id
        }

        fun metadata(metadata: Map<String, String>?): Builder = apply {
            this.metadata = metadata
        }

        fun loggingTokens(loggingTokens: Set<String>): Builder = apply {
            this.loggingTokens = loggingTokens
        }

        /**
         * Generate a new [Card] object based on the arguments held by this Builder.
         *
         * @return the newly created [Card] object
         */
        fun build(): Card {
            val number = normalizeCardNumber(number).takeUnless { it.isNullOrBlank() }
            val last4 = last4.takeUnless { it.isNullOrBlank() } ?: calculateLast4(number)
            return Card(
                number = number,
                expMonth = expMonth,
                expYear = expYear,
                cvc = cvc.takeUnless { it.isNullOrBlank() },
                name = name.takeUnless { it.isNullOrBlank() },
                addressLine1 = addressLine1.takeUnless { it.isNullOrBlank() },
                addressLine1Check = addressLine1Check.takeUnless { it.isNullOrBlank() },
                addressLine2 = addressLine2.takeUnless { it.isNullOrBlank() },
                addressCity = addressCity.takeUnless { it.isNullOrBlank() },
                addressState = addressState.takeUnless { it.isNullOrBlank() },
                addressZip = addressZip.takeUnless { it.isNullOrBlank() },
                addressZipCheck = addressZipCheck.takeUnless { it.isNullOrBlank() },
                addressCountry = addressCountry.takeUnless { it.isNullOrBlank() },
                last4 = last4,
                brand = brand ?: CardUtils.getPossibleCardBrand(number),
                fingerprint = fingerprint.takeUnless { it.isNullOrBlank() },
                country = country.takeUnless { it.isNullOrBlank() },
                currency = currency.takeUnless { it.isNullOrBlank() },
                customerId = customerId.takeUnless { it.isNullOrBlank() },
                cvcCheck = cvcCheck.takeUnless { it.isNullOrBlank() },
                metadata = metadata
            )
        }

        private fun normalizeCardNumber(number: String?): String? {
            return number?.trim()?.replace("\\s+|-".toRegex(), "")
        }

        private fun calculateLast4(number: String?): String? {
            return if (number != null && number.length > 4) {
                number.substring(number.length - 4)
            } else {
                null
            }
        }
    }
}

