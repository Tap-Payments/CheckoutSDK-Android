package company.tap.checkout.internal.api.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by AhlaamK on 6/14/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
data class SaveCard(



    @SerializedName("risk")
    @Expose
     val risk: Boolean = false,

    @SerializedName("issuer")
    @Expose
     val issuer: Boolean = false,

    @SerializedName("promo")
    @Expose
     val promo: Boolean = false,

    @SerializedName("loyalty")
    @Expose
     val loyalty: Boolean = false,

    @SerializedName("card_issuer")
    @Expose
     var card_issuer: CardIssuer? = null
): Charge()
