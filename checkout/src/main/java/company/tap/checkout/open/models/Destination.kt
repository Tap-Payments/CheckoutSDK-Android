package company.tap.checkout.open.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

/**
 * Created by AhlaamK on 6/13/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/

/**
 * The Destination array contains list of Merchant desired destinations accounts to receive money from payment transactions
 */
data class Destination(
    // Destination unique identifier (Required)
    @SerializedName("id") @Expose
    private var id: String? = null,

    // Amount to be transferred to the destination account (Required)
    @SerializedName("amount")
    @Expose
    private val amount: BigDecimal? = null,

    // Currency code (three digit ISO format) (Required)
    @SerializedName("currency")
    @Expose
    private val currency: String? = null,

    //Description about the transfer (Optional)
    @SerializedName("description")
    @Expose
    private val description: String? = null,

    //Merchant reference number to the destination (Optional)
    @SerializedName("reference")
    @Expose
    private val reference: String? = null
)
