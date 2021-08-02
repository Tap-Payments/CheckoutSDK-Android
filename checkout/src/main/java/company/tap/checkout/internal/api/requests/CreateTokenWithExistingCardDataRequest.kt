package company.tap.checkout.internal.api.requests

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import company.tap.checkout.internal.api.models.CreateTokenSavedCard

/**
 * Created by AhlaamK on 8/2/21.

Copyright (c) 2021    Tap Payments.
All rights reserved.
 **/
/**
 * The type Create token with existing card data request.
 */
class CreateTokenWithExistingCardDataRequest private constructor(savedCard: CreateTokenSavedCard) {
    @SerializedName("saved_card")
    @Expose
    private val savedCard: CreateTokenSavedCard = savedCard

    /**
     * The type Builder.
     */
    class Builder(card: CreateTokenSavedCard) {
        private val createTokenWithExistingCardDataRequest: CreateTokenWithExistingCardDataRequest = CreateTokenWithExistingCardDataRequest(card)

        /**
         * Build create token with existing card data request.
         *
         * @return the create token with existing card data request
         */
        fun build(): CreateTokenWithExistingCardDataRequest {
            return createTokenWithExistingCardDataRequest
        }

    }

}
