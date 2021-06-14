package company.tap.checkout.internal.api.models

import com.google.gson.Gson
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import company.tap.checkout.internal.api.crypto.CryptoUtil
import company.tap.checkout.internal.interfaces.SecureSerializable
import java.io.Serializable

/**
 * Created by AhlaamK on 6/14/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
 class CreateTokenCard : Serializable {

    @SerializedName("crypted_data") @Expose
    var sensitiveCardData: String? = null

    @SerializedName("address") @Expose
    var address: Address? = null


    /**
     * Instantiates a new Create token card.
     *
     * @param cardNumber      the card number
     * @param expirationMonth the expiration month
     * @param expirationYear  the expiration year
     * @param cvc             the cvc
     * @param cardholderName  the cardholder name
     * @param address         the address
     */
    fun CreateTokenCard(
        cardNumber: String,
        expirationMonth: String,
        expirationYear: String,
        cvc: String,
        cardholderName: String,
        address: Address?
    ) {
        val _sensitiveCardData =
            SensitiveCardData(cardNumber, expirationYear, expirationMonth, cvc, cardholderName)
        val cryptedDataJson = Gson().toJson(_sensitiveCardData)
        this.sensitiveCardData = CryptoUtil.encryptJsonString(
            cryptedDataJson,
          TODO()//  PaymentDataManager.getInstance().getSDKSettings().getData().getEncryptionKey()
        )
        this.address = address
    }

    class SensitiveCardData internal constructor(
        @field:Expose @field:SerializedName("number") private val number: String,
        @field:Expose @field:SerializedName(
            "exp_year"
        ) private val expirationYear: String,
        @field:Expose @field:SerializedName("exp_month") private val expirationMonth: String,
        @field:Expose @field:SerializedName(
            "cvc"
        ) private val cvc: String,
        @field:Expose @field:SerializedName("name") private val cardholderName: String
    ) : SecureSerializable
}
