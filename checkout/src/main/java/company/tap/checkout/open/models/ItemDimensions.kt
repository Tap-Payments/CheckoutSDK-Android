package company.tap.checkout.open.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class ItemDimensions(
    @SerialName("weight_type") @Expose
    private var weight_type: String? = null,

    @SerialName("weight")
    @Expose
    private val weight: Double? = null,


    @SerialName("measurements")
    @Expose
    private val measurements: String? = null,

    @SerialName("length")
    @Expose
    private val length: Double? = null,

    @SerialName("width")
    @Expose
    private val width: Double? = null,

    @SerialName("height")
    @Expose
    private val height: Double? = null
)
