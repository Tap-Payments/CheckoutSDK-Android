package company.tap.checkout.internal.api.requests

import com.google.gson.JsonObject
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class CreateTokenGPayRequest(
    @field:Expose @field:SerializedName("type") private val type: String?,
    @field:Expose @field:SerializedName(
        "token_data"
    ) private val tokenData: JsonObject?
) : Serializable {
    /**
     * The type Builder.
     */
    class Builder(type: String?, tokenData: JsonObject?) {
        private val createTokenGPayRequest: CreateTokenGPayRequest

        /**
         * Build create otp verification request.
         *
         * @return the createTokenGPayRequest request
         */
        fun build(): CreateTokenGPayRequest {
            return createTokenGPayRequest
        }

        /**
         * Instantiates a new Builder.
         *
         * @param type  the type
         * @param tokenData the tokenData
         */
        init {
            createTokenGPayRequest = CreateTokenGPayRequest(type, tokenData)
        }
    }
}
