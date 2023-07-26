package company.tap.checkout.open.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import java.io.Serializable

@kotlinx.serialization.Serializable
data class AddressModel(
    @SerialName("type") @Expose
    private var type: String? = null,
    @SerialName("line1")
    @Expose
    private val line1: String? = null,

    @SerialName("line2")
    @Expose
    private val line2: String? = null,

    @SerialName("line3")
    @Expose
    private val line3: String? = null,

    @SerialName("line4")
    @Expose
    private val line4: String? = null,

    @SerialName("avenue")
    @Expose
    private val avenue: String? = null,

    @SerialName("street")
    @Expose
    private val street: String? = null,

    @SerialName("building")
    @Expose
    private val building: String? = null,

    @SerialName("apartment")
    @Expose
    private val apartment: String? = null,

    @SerialName("country")
    @Expose
    private val country: String? = null,


    @SerialName("state")
    @Expose
    private val state: String? = null,

    @SerialName("city")
    @Expose
    private val city: String? = null,


    @SerialName("area")
    @Expose
    private val area: String? = null,

    @SerialName("zip_code")
    @Expose
    private val zipCode: String? = null,

    @SerialName("postal_code")
    @Expose
    private val postalCode: String? = null,

    @SerialName("locale")
    @Expose
    private val locale: String? = null
)
