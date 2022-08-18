package company.tap.checkout.open.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data  class ReferId(
    @SerializedName("order")
    @Expose
    private val order: String? = null
) : Serializable