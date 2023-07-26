package company.tap.checkout.open.models

import company.tap.checkout.internal.api.models.PhoneNumber
import kotlinx.serialization.SerialName

/**
 * Created by AhlaamK on 6/13/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/

/**
 * The type TapCustomer.
 */
@kotlinx.serialization.Serializable
class TapCustomer(
    /**
     * Gets identifier.
     *
     * @return the identifier
     */
    @SerialName("id") var identifier: String?,
    /**
     * Gets first name.
     *
     * @return the first name
     */
    @SerialName("first_name") var firstName: String?,
    @SerialName("middle_name") var middleName: String?,
    @SerialName("last_name") var lastName: String?,
    @SerialName("email") var email: String?,
    @SerialName("phone") var phone: PhoneNumber?,
    @SerialName("metadata") var metaData: String?,
    @SerialName("nationality") var nationality: String? = null,
    @SerialName("address") var address: AddressModel? = null,
    @SerialName("locale") var locale: String? = null
) {


    /**
     * Gets Address.
     *
     * @return the Address
     */
    fun getLocale(): String? {
        return locale
    }


    override fun toString(): String {
        return """TapCustomer {
        id =  '$identifier'
        email =  '$email'
        first_name =  '$firstName'
        middle_name =  '$middleName'
        last_name =  '$lastName'
        phone  country code =  '${phone?.countryCode}'
        phone number =  '${phone?.number}'
        metadata =  '$metaData'
        address = '$address'
    }"""
    }
    ////////////////////////// ############################ Start of Builder Region ########################### ///////////////////////
    /**
     * The type TapCustomer builder.
     */
    class CustomerBuilder
    /**
     * Client app can create a customer object with only customer id
     *
     * @param innerId the inner id
     */(private val nestedIdentifier: String) {
        private var nestedFirstName: String? = null
        private var nestedMiddleName: String? = null
        private var nestedLastName: String? = null
        private var nestedEmail: String? = null
        private var nestedPhone: PhoneNumber? = null
        private var nestedMetaData: String? = null
        private var nestedNationality: String? = null
        private var nestedAddress: AddressModel? = null

        /**
         * First name customer builder.
         *
         * @param innerFirstName the inner first name
         * @return the customer builder
         */
        fun firstName(innerFirstName: String?): CustomerBuilder {
            nestedFirstName = innerFirstName
            return this
        }

        /**
         * Middle name customer builder.
         *
         * @param innerMiddle the inner middle
         * @return the customer builder
         */
        fun middleName(innerMiddle: String?): CustomerBuilder {
            nestedMiddleName = innerMiddle
            return this
        }

        /**
         * Last name customer builder.
         *
         * @param innerLastName the inner last name
         * @return the customer builder
         */
        fun lastName(innerLastName: String?): CustomerBuilder {
            nestedLastName = innerLastName
            return this
        }

        /**
         * Email customer builder.
         *
         * @param innerEmail the inner email
         * @return the customer builder
         */
        fun email(innerEmail: String?): CustomerBuilder {
            nestedEmail = innerEmail
            return this
        }

        /**
         * Phone customer builder.
         *
         * @param innerPhone the inner phone
         * @return the customer builder
         */
        fun phone(innerPhone: PhoneNumber?): CustomerBuilder {
            nestedPhone = innerPhone
            return this
        }

        /**
         * Metadata customer builder.
         *
         * @param innerMetadata the inner metadata
         * @return the customer builder
         */
        fun metadata(innerMetadata: String?): CustomerBuilder {
            nestedMetaData = innerMetadata
            return this
        }

        /**
         * Nationality of customer builder.
         *
         * @param innerNationality the inner innerNationality
         * @return the customer builder
         */
        fun nationality(innerNationality: String): CustomerBuilder? {
            this.nestedNationality = innerNationality
            return this
        }

        /**
         * Address of customer builder.
         *
         * @param innerAddress the inner innerAddress
         * @return the customer builder
         */
        fun address(innerAddress: AddressModel): CustomerBuilder? {
            this.nestedAddress = innerAddress
            return this
        }

        /**
         * Build customer.
         *
         * @return the customer
         */
        fun build(): TapCustomer {
            return TapCustomer(
                nestedIdentifier, nestedFirstName, nestedMiddleName, nestedLastName,
                nestedEmail, nestedPhone, nestedMetaData, nestedNationality, nestedAddress
            )
        }
    } ////////////////////////// ############################ End of Builder Region ########################### ///////////////////////

    //  Constructor is private to prevent access from client app, it must be through inner Builder class only
    init {
        firstName = firstName
        this.middleName = middleName
        this.lastName = lastName
        this.email = email
        this.phone = phone
        this.metaData = metaData
        this.nationality = nationality
        this.address = address
        this.locale = locale
    }
}
