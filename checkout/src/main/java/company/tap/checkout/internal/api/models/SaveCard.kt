package company.tap.checkout.internal.api.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by AhlaamK on 6/14/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
data class SaveCard(
    @SerializedName("save_card") @Expose
    private var save_card: Boolean = false,


    @SerializedName("risk")
    @Expose
    private val risk: Boolean = false,

    @SerializedName("issuer")
    @Expose
    private val issuer: Boolean = false,

    @SerializedName("promo")
    @Expose
    private val promo: Boolean = false,

    @SerializedName("loyalty")
    @Expose
    private val loyalty: Boolean = false,

    @SerializedName("card_issuer")
    @Expose
    private var card_issuer: CardIssuer? = null
): Charge()
