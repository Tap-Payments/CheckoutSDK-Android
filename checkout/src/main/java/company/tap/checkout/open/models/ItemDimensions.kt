package company.tap.checkout.open.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ItemDimensions(
    @SerializedName("weight_type") @Expose
    private var weightType: String? = null,

    @SerializedName("weight")
@Expose
private val weight: Double? = null,


@SerializedName("measurements")
@Expose
private val measurements: String? = null,

@SerializedName("length")
@Expose
private val length: Double? = null,

@SerializedName("width")
@Expose
private val width: Double? = null,

@SerializedName("height")
@Expose
private val height: Double? = null
)
