package company.tap.checkout.open.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import company.tap.checkout.internal.api.models.PhoneNumber
import java.io.Serializable

/**
 * Created by AhlaamK on 6/13/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/

/**
 * The type TapCustomer.
 */
class TapCustomer(
    /**
     * Gets identifier.
     *
     * @return the identifier
     */
    @field:Expose @field:SerializedName("id") val identifier: String?,
    /**
     * Gets first name.
     *
     * @return the first name
     */
    @field:Expose @field:SerializedName("first_name") var firstName: String?,
    middleName: String?,
    lastName: String?,
    email: String?,
    phone: PhoneNumber?,
    metaData: String?
) :
    Serializable {

    /**
     * Gets middle name.
     *
     * @return the middle name
     */
    @SerializedName("middle_name")
    @Expose
    val middleName: String?

    /**
     * Gets last name.
     *
     * @return the last name
     */
    @SerializedName("last_name")
    @Expose
    val lastName: String?

    /**
     * Gets email.
     *
     * @return the email
     */
    @SerializedName("email")
    @Expose
    val email: String?

    @SerializedName("phone")
    @Expose
    private val phone: PhoneNumber?
    /**
     * Gets meta data.
     *
     * @return the meta data
     */
    /**
     * The Meta data.
     */
    @SerializedName("metadata")
    var metaData: String?

    /**
     * Gets phone.
     *
     * @return the phone
     */
    fun getPhone(): PhoneNumber? {
        return phone
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
         * Build customer.
         *
         * @return the customer
         */
        fun build(): TapCustomer {
            return TapCustomer(
                nestedIdentifier, nestedFirstName, nestedMiddleName, nestedLastName,
                nestedEmail, nestedPhone, nestedMetaData
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
    }
}
