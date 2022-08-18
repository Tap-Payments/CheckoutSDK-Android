package company.tap.checkout.open.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import company.tap.checkout.internal.api.models.Merchant
import company.tap.checkout.internal.utils.AmountCalculator
import java.math.BigDecimal

data class OrderObject(
    @field:Expose @field:SerializedName("amount") var amount: BigDecimal,
    @field:Expose @field:SerializedName(
        "currency"
    ) private val currency: String,
    @SerializedName("customer")
    @Expose
    private var customer: TapCustomer?,

    @SerializedName("items")
    @Expose
    private var items: ArrayList<Items>? = null,

    @SerializedName("tax")
    @Expose
    private var tax: ArrayList<TaxObject>?,

    @SerializedName("shipping")
    @Expose
    private var shipping: ArrayList<ShippingObject>?,

    @SerializedName("merchant")
    @Expose
    private var merchant: Merchant?,

    @SerializedName("metadata")
    @Expose
    private var metaData: MetaData?,

    @SerializedName("reference")
    @Expose
    private val reference: ReferId? = null
) {



    /**
     * Gets taxes amount.
     *
     * @return the taxes amount
     */
    val taxesAmount: BigDecimal
        get() {
            val taxationAmount = amount
            return AmountCalculator.calculateTaxesOnItems(taxationAmount, tax)!!
        }

    //  Constructor is private to prevent access from client app, it must be through inner Builder class only
    init {
        this.customer = customer
        if (items != null && items!!.size > 0) {
            this.items = items
            amount = AmountCalculator.calculateTotalAmountOfOrder(items, tax, shipping, this)!!
        } else {
            this.items = null
            val plainAmount = amount ?: BigDecimal.ZERO
            amount =
                AmountCalculator.calculateTotalAmountOfOrder(null, tax, shipping, this)
                    ?.add(plainAmount)!!
        }
        this.tax = tax
        this.shipping = shipping
        this.merchant = merchant
        this.metaData = metaData
    }
}
