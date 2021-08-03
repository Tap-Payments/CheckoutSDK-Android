package company.tap.checkout.internal.api.requests

import androidx.annotation.RestrictTo
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import company.tap.checkout.internal.api.models.CreateTokenCard
import company.tap.checkout.internal.interfaces.CreateTokenRequest

/**
 * Created by AhlaamK on 7/13/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
/**
 * The type Create token with card data request.
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
class CreateTokenWithCardDataRequest(card: CreateTokenCard) : CreateTokenRequest {
    @SerializedName("card")
    @Expose
    val card: CreateTokenCard = card

}