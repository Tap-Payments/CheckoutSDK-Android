package company.tap.checkout.internal.api.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by AhlaamK on 6/14/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
data class CreateTokenSavedCard(
    @SerializedName("card_id") @Expose
     var cardId: String? = null,

    @SerializedName("customer_id")
    @Expose
     val customerId: String? = null
): Serializable
