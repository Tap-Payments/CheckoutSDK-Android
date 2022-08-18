package company.tap.checkout.open.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.math.BigDecimal

data  class ShippingObject(
    @SerializedName("amount")
    @Expose
    private val amount: BigDecimal,

    @SerializedName("currency")
    @Expose
    private var currency: String,

    @SerializedName("description")
    @Expose
    private var description: Description?,

    @SerializedName("recipient_name")
@Expose
private val recipientName: String?,

    @SerializedName("address")
@Expose
private val address: AddressCustomer?,

    @SerializedName("provider")
@Expose
private val provider: Provider?
) : Serializable {


    fun getAmount(): BigDecimal {
        return amount
    }

    fun getCurrency(): String {
        return currency
    }

    fun getDescription(): Description? {
        return description
    }

    fun getRecipientName(): String? {
        return recipientName
    }

    fun getAddress(): AddressCustomer? {
        return address
    }

    fun getProvider(): Provider? {
        return provider
    }


}
