package company.tap.checkout.internal.api.models

import com.google.gson.Gson
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import company.tap.checkout.internal.api.crypto.CryptoUtil
import company.tap.checkout.internal.api.responses.PaymentOptionsResponse
import company.tap.checkout.internal.api.responses.SDKSettings
import company.tap.checkout.internal.interfaces.SecureSerializable
import company.tap.checkout.open.data_managers.PaymentDataSource
import java.io.Serializable

/**
 * Created by AhlaamK on 6/14/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
 class CreateTokenCard constructor(cardNumber: String,
                                     expirationMonth: String,
                                     expirationYear: String,
                                     cvc: String,
                                     cardholderName: String?,
                                     address: Address?) : Serializable {

    @SerializedName("crypted_data") @Expose
    var sensitiveCardData: String? = null

    @SerializedName("address") @Expose
    var address: Address? = null


    class SensitiveCardData internal constructor(
        @field:Expose @field:SerializedName("number") private val number: String,
        @field:Expose @field:SerializedName(
            "exp_year"
        ) private val expirationYear: String,
        @field:Expose @field:SerializedName("exp_month") private val expirationMonth: String,
        @field:Expose @field:SerializedName(
            "cvc"
        ) private val cvc: String,
        @field:Expose @field:SerializedName("name") private val cardholderName: String?
    ) : SecureSerializable

    init {
        val _sensitiveCardData =
                SensitiveCardData(cardNumber, expirationYear, expirationMonth, cvc, cardholderName)
        val cryptedDataJson = Gson().toJson(_sensitiveCardData)
        this.sensitiveCardData = PaymentDataSource.getMerchantData()?.encryptionKey?.let {
            CryptoUtil.encryptJsonString(
                    cryptedDataJson,
                    it
            )
        }
        this.address = address
    }
}
