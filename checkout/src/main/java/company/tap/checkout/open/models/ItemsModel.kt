package company.tap.checkout.open.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import company.tap.checkout.internal.api.enums.AmountModificatorType
import company.tap.checkout.internal.api.models.AmountModificator
import company.tap.checkout.open.data_managers.PaymentDataSource
import company.tap.checkout.open.enums.Category
import java.io.Serializable
import java.math.BigDecimal

data class ItemsModel(
    @field:Expose
    @field:SerializedName("product_id")
    val productId: String?,

    @field:Expose
    @field:SerializedName(
        "name")
    val name: String? = null,

    @field:Expose
    @field:SerializedName("amount")
    var amount: BigDecimal? = null,

    @field:Expose
    @field:SerializedName("currency")
    val currency: String?,

    @field:Expose
    @field:SerializedName("quantity")
    val quantity: BigDecimal? = null,

    @SerializedName("category")
    @Expose
    private var category: Category?,

    @SerializedName("discount")
    @Expose val discount: AmountModificator?,

    @SerializedName("vendor")
    @Expose
    private val vendor: Vendor?,

    @SerializedName("fulfillment_service")
    @Expose
    val fulfillmentService: String?,

    @SerializedName("requires_shipping")
    @Expose
    val isRequireShipping: Boolean? = false,

    @SerializedName("item_code")
    @Expose
    val itemCode: String?,

    @SerializedName("account_code")
    @Expose
    val accountCode: String?,

    @SerializedName("description")
    @Expose
    val description: String?,

    @SerializedName("image")
    @Expose
    val image: String?,

    @SerializedName("reference")
    @Expose
    private val reference: ReferenceItem?,

    @SerializedName("dimensions")
    @Expose
    val dimensions: ItemDimensions?,

    @SerializedName("tags")
    @Expose
    val tags: String?,

    @SerializedName("meta_data")
    @Expose
    val metaData: MetaData?,

    var totalAmount :BigDecimal? = null
) : Serializable {


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
    fun getPlainAmount(): BigDecimal? {
        println("  #### getPlainAmount : " + amount)
        assert(amount != null)
        totalAmount = amount!!?.multiply(quantity)
        return totalAmount
    }

    /**
     * Gets discount amount.
     *
     * @return the discount amount
     */
    fun getDiscountAmount(): BigDecimal? {
        return if (getDiscount() == null) {
            BigDecimal.ZERO
        } else when (getDiscount()!!.getType()) {
            AmountModificatorType.PERCENTAGE -> getPlainAmount()?.multiply(
                getDiscount()!!.getNormalizedValue()
            )
            AmountModificatorType.FIXED -> getDiscount()!!.getValue()
            else -> BigDecimal.ZERO
        }
    }


}
