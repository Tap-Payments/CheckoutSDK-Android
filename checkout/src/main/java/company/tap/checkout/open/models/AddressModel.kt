package company.tap.checkout.open.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class AddressModel(
    @SerializedName("type") @Expose
    private var type: String? = null,
    @SerializedName("line1")
    @Expose
    private val line1: String? = null,

    @SerializedName("line2")
    @Expose
    private val line2: String? = null,

    @SerializedName("line3")
    @Expose
    private val line3: String? = null,

    @SerializedName("line4")
    @Expose
    private val line4: String? = null,

    @SerializedName("avenue")
    @Expose
    private val avenue: String? = null,

    @SerializedName("street")
    @Expose
    private val street: String? = null,

    @SerializedName("building")
    @Expose
    private val building: String? = null,

    @SerializedName("apartment")
    @Expose
    private val apartment: String? = null,

    @SerializedName("country")
    @Expose
    private val country: String? = null,


    @SerializedName("state")
    @Expose
    private val state: String? = null,

    @SerializedName("city")
    @Expose
    private val city: String? = null,


    @SerializedName("area")
    @Expose
    private val area: String? = null,

    @SerializedName("zip_code")
    @Expose
    private val zipCode: String? = null,

    @SerializedName("postal_code")
    @Expose
    private val postalCode: String? = null,

    @SerializedName("locale")
    @Expose
    private val locale: String? = null
):Serializable
