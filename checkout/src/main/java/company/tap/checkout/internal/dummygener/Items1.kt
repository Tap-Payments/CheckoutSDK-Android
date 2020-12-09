package company.tap.checkout.internal.dummygener
import com.google.gson.annotations.SerializedName
import company.tap.checkout.internal.enums.Currencies


data class Items1 (

    @SerializedName("amount") var amount : Long,
    @SerializedName("currency") var currency : String,
    @SerializedName("description") val description : String,
    @SerializedName("discount") val discount1 : Discount1,
    @SerializedName("image") val image : String,
    @SerializedName("sku") val sku : String,
    @SerializedName("name") val name : String,
    @SerializedName("quantity") val quantity : Int
)