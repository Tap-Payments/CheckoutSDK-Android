package company.tap.checkout.internal.api.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class NormalCardData constructor(cardNumber: String,
                                 expirationMonth: String,
                                 expirationYear: String,
                                 cvc: String,
                                 cardholderName: String?,
                                 ) : Serializable {
    @SerializedName("cardNumber") @Expose
    var cardNumber: String? = cardNumber
    @SerializedName("expirationMonth") @Expose
    var expirationMonth: String? = expirationMonth
    @SerializedName("expirationYear") @Expose
    var expirationYear: String? = expirationYear
    @SerializedName("cvc") @Expose
    var cvc: String? = cvc
    @SerializedName("cardholderName") @Expose
    var cardholderName: String? = cardholderName
}