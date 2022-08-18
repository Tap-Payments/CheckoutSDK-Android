package company.tap.checkout.open.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ReferenceItem
    (@SerializedName("SKU") @Expose var SKU: String? = null,

     @SerializedName("GTIN") @Expose var GTIN: String? = null): Serializable{

        init {

                this.SKU = SKU
                this.GTIN = GTIN

        }

    }


