package company.tap.checkout.internal.api.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import company.tap.checkout.internal.interfaces.BaseResponse

data class OrderId ( @SerializedName("id")
                     @Expose
                     private val id: String? = null): BaseResponse {

    fun getId(): String? {
        return id
    }


}