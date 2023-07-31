package company.tap.checkout.open.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import java.io.Serializable

@kotlinx.serialization.Serializable
data class ReferenceItem
    (
    @SerialName("SKU") @Expose var SKU: String? = null,

    @SerialName("GTIN") @Expose var GTIN: String? = null
) : Serializable {

    init {

        this.SKU = SKU
        this.GTIN = GTIN

    }

}


