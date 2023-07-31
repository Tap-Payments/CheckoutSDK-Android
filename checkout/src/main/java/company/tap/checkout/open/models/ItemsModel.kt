package company.tap.checkout.open.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import company.tap.checkout.internal.api.enums.AmountModificatorType
import company.tap.checkout.internal.api.models.AmountModificator
import company.tap.checkout.open.enums.Category
import kotlinx.serialization.SerialName
import java.io.Serializable
import java.math.BigDecimal

@kotlinx.serialization.Serializable
data class ItemsModel(
    @field:Expose
    @SerialName("product_id")
    val product_id: String?,

    @field:Expose
    @SerialName("name")
    val name: String? = null,

    @field:Expose
    @SerialName("amount")
    var amount: Double? = null,

    @field:Expose
    @SerialName("currency")
    val currency: String? = "KWD",

    @field:Expose
    @SerialName("quantity")
    val quantity: Double? = null,

    @SerialName("category")
    @Expose
    private var category: Category?,

    @SerialName("discount")
    @Expose val discount: AmountModificator?,

    @SerialName("vendor")
    @Expose
    private val vendor: Vendor?,

    @SerialName("fulfillment_service")
    @Expose
    val fulfillmentService: String?,

    @SerialName("requires_shipping")
    @Expose
    val isRequireShipping: Boolean? = false,

    @SerialName("item_code")
    @Expose
    val itemCode: String?,

    @SerialName("account_code")
    @Expose
    val accountCode: String?,

    @SerialName("description")
    @Expose
    val description: String?,

    @SerialName("image")
    @Expose
    val image: String?,

    @SerialName("reference")
    @Expose
    private val reference: ReferenceItem?,

    @SerialName("dimensions")
    @Expose
    val dimensions: ItemDimensions?,

    @SerialName("tags")
    @Expose
    val tags: String?,

    @SerialName("meta_data")
    @Expose
    val metaData: MetaData?,

    @SerialName("isExpandedItem")
    @Expose
    var isExpandedItem: Boolean = false,
    @SerialName("total_amount")
    @Expose
    var totalAmount: Double? = null
) {


    fun getCategory(): Category? {
        return category
    }

    @JvmName("getDiscount1")
    fun getDiscount(): AmountModificator? {
        return discount
    }

    fun getVendor(): Vendor? {
        return vendor
    }

    fun getReference(): ReferenceItem? {
        return reference
    }

    /**
     * Gets plain amount.
     *
     * @return the plain amount
     */
    fun getPlainAmount(): Double? {
        println("  #### getPlainAmount : " + amount)
        println("  #### quantity : " + quantity)
        assert(amount != null)
        totalAmount = (amount?.toBigDecimal()?.multiply(quantity?.toBigDecimal() ?: BigDecimal.ONE))?.toDouble()
        return totalAmount?.toDouble()
    }

    /**
     * Gets discount amount.
     *
     * @return the discount amount
     */
    fun getDiscountAmount(): BigDecimal? {
        return if (getDiscount() == null) {
            BigDecimal.ZERO
        } else when (getDiscount()!!.type) {
            AmountModificatorType.PERCENTAGE -> getPlainAmount()?.toBigDecimal()?.multiply(
                getDiscount()!!.getNormalizedValue()
            )
            AmountModificatorType.FIXED -> getDiscount()?.value
            else -> BigDecimal.ZERO
        } as BigDecimal?
    }


}
