package company.tap.checkout.open.models

import com.google.gson.annotations.Expose
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class Vendor     //  Constructor is private to prevent access from client app, it must be through inner Builder class only
    (
    @field:Expose @SerialName("id") private val id: String?,
    @field:Expose @SerialName(
        "name"
    ) private val name: String?
)